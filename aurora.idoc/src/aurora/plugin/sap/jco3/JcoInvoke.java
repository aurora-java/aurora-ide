package aurora.plugin.sap.jco3;

import java.util.logging.Level;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.ConfigurationError;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

import aurora.plugin.sap.ISapConfig;
import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;

public class JcoInvoke extends AbstractEntry {
	public static final String LOGGING_TOPIC = "aurora.plugin.sap.jco3";
	
	public Parameter[] parameters;
	public String sid;
	public Table[] tables;
	public Structure[] structures;
	public String function;
	public String return_target;
	
	SapConfig sapConfig;
	ILogger logger;
	
	public JcoInvoke(ISapConfig config) {
		if(config instanceof SapConfig)
			sapConfig = (SapConfig)config;
		else
			throw new IllegalStateException("aurora.plugin.sap.jco3.SapConfig is undefined");
	}
	
	public void run(ProcedureRunner runner) throws Exception {
		CompositeMap context = runner.getContext();
		logger = LoggingContext.getLogger(context, LOGGING_TOPIC);
		logger.config("jco-invoke");
		logger.config("===================================");
		logger.log(Level.CONFIG, "config:{0}", new Object[] { this });
		
		ServiceInstance service = (ServiceInstance) ServiceInstance.getInstance(context.getRoot());
		
//		HttpServiceInstance service = (HttpServiceInstance) HttpServiceInstance
//				.getInstance(context.getRoot());
		CompositeMap target = null;
		CompositeMap model = null;
		if (service != null)
			model = service.getServiceContext().getModel();
		else
			model = context.getRoot().getChild("model");
		if (model == null)
			model = context.getRoot().createChild("model");
		if (return_target != null) {
			String t = TextParser.parse(return_target, context);
			target = (CompositeMap) model.getObject(t);
			if (target == null)
				target = model.createChildByTag(t);
		}
		JCoDestination destination = sapConfig.getJCoDestination(sid);
		String functionName = function;

		JCoFunctionTemplate ftemplate = destination.getRepository()
				.getFunctionTemplate(functionName);
		logger.info("function template:" + functionName);
		if (ftemplate == null) {
			logger.log(Level.SEVERE, "Function '" + function
					+ "' not found in SAP system.");
			throw new IllegalArgumentException("Function '" + function
					+ "' not found in SAP system.");
		}
		// Create a function from the template
		JCoFunction function = ftemplate.getFunction();
		JCoParameterList input = function.getImportParameterList();
		JCoParameterList output = function.getExportParameterList();
		if (parameters != null)
			for (int i = 0; i < parameters.length; i++) {
				Parameter param = parameters[i];
				if (param.Return_field == null) {
					Object o = param.Source_field == null ? param.Value
							: context.getObject(param.Source_field);
					String value = o == null ? "" : o.toString();					
					input.setValue(param.Name,value);
					logger.log(Level.CONFIG, "parameter {0} -> {1}",
							new Object[] { param.Name, value });
				}
			}
		if (structures != null) {
			for (int i = 0; i < structures.length; i++) {
				Structure structure = structures[i];				
				structure.setLogger(logger);
				if (structure.isImport()) {
					JCoStructure stc = structure.getJCOStructure(input);
					structure.fillJCOStructure(stc, context);
					input.setValue(structure.Name, stc);
				}
			}
		}
		// Set import table
		if (tables != null) {
			JCoParameterList list = function.getTableParameterList();
			for (int i = 0; i < tables.length; i++) {
				Table table = tables[i];				
				table.setLogger(logger);
				if (table.isImport()) {
					JCoTable tbl = table.getJCOTable(list);
					Object o = context.getObject(table.Source_field);
					logger.config("transfer import table " + table.Name
							+ " from '" + table.Source_field + "':" + o);
					if (o instanceof CompositeMap)
						table.fillJCOTable(tbl, (CompositeMap) context);
				}
			}
		}

		// Call the remote system and retrieve return value
		logger.config("call function " + function);
		function.execute(destination);
		if (parameters != null) {
			for (int i = 0; i < parameters.length; i++) {
				Parameter param = parameters[i];
				if (param.Return_field != null) {
					if (target == null)
						throw new ConfigurationError(
								"<jco-invoke>:must set 'return_target' attribute if there is return field");
					String vl = output.getString(param.Name);
					if (vl == null && !param.Nullable)
						throw new IllegalArgumentException(
								"jco-invoke: return field " + param.Name
										+ " is null");
					String f = TextParser.parse(param.Return_field, context);
					target.putObject(f, vl);
					logger.config("return: " + param.Name + "=" + vl + " -> "
							+ f);
				}
			}
		}
		if (structures != null) {
			for (int i = 0; i < structures.length; i++) {
				Structure structure = structures[i];			
				structure.setLogger(logger);
				if (structure.isImport())
					continue;
				if (structure.Target == null)
					throw new ConfigurationError(
							"Must set 'target' attribute for Structures "
									+ structure.Name);
				JCoStructure stc = structure.getJCOStructure(output);
				CompositeMap result = (CompositeMap) context
						.getObject(structure.Target);
				if (result == null)
					result = context.createChildByTag(structure.Target);
				structure.fillCompositeMap(stc, result);
			}
		}
		// Get export tables
		if (tables != null) {
			JCoParameterList list = function.getTableParameterList();
			if (list == null)
				throw new IllegalArgumentException("Function '" + function
						+ "' doesn't return tables");
			for (int i = 0; i < tables.length; i++) {
				Table table = tables[i];				
				if (table.isImport())
					continue;
				if (table.Target == null)
					throw new ConfigurationError(
							"Must set 'target' attribute for table "
									+ table.Name);
				table.setLogger(logger);
				JCoTable records = table.getJCOTable(list);
				// Fetch as CompositeMap

				CompositeMap result = (CompositeMap) context
						.getObject(table.Target);
				if (result == null)
					result = context.createChildByTag(table.Target);
				table.fillCompositeMap(records, result);
				int rc = 0;
				if (result.getChilds() != null)
					rc = result.getChilds().size();
				logger.config("loading export table " + table.Name
						+ " into path '" + table.Target + "', total " + rc
						+ " record(s)");
			}
		}

	}

	public Parameter[] getParameters() {
		return parameters;
	}

	public void setParameters(Parameter[] parameters) {
		this.parameters = parameters;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public Table[] getTables() {
		return tables;
	}

	public void setTables(Table[] tables) {
		this.tables = tables;
	}

	public Structure[] getStructures() {
		return structures;
	}

	public void setStructures(Structure[] structures) {
		this.structures = structures;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getReturn_target() {
		return return_target;
	}

	public void setReturn_target(String return_target) {
		this.return_target = return_target;
	}
}
