/** Perform a JCO function call in CreateModel step
 *  Created on 2011-8-26
 *  Created by zoulei1266
 */
package aurora.plugin.sap;

import java.util.logging.Level;

import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;
import com.sap.mw.jco.JCO.ParameterList;

import aurora.service.http.HttpServiceInstance;
import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.ConfigurationError;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class JcoInvoke extends AbstractEntry {
	SapConfig sapConfig;
	InstanceConfig sapInstance;
	ILogger logger;

	public Parameter[] parameters;
	public String sid;
	public Table[] tables;
	public Structure[] structures;
	public String function;
	public String return_target;

	public JcoInvoke(ISapConfig config) {
		if(config instanceof SapConfig)
			sapConfig = (SapConfig)config;
		else
			throw new IllegalStateException("aurora.plugin.sap.SapConfig is undefined");
	}

	public JcoInvoke(SapInstance si) {
		sapInstance = si;
	}

	public void run(ProcedureRunner runner) throws Exception {
		CompositeMap context = runner.getContext();
		if (sapConfig != null) {
			sid = TextParser.parse(sid, context);
			if (sid == null) {
				sapInstance = sapConfig.getSapInstance();
				if (sapInstance == null)
					throw new IllegalArgumentException(
							"jco-invoke: sid attribute is null");
			} else {
				sapInstance = sapConfig.getSapInstance(sid);
			}
		}
		logger = LoggingContext.getLogger(context, "aurora.plugin.sap");
		logger.config("jco-invoke");
		logger.config("===================================");
		logger.log(Level.CONFIG, "config:{0}", new Object[] { this });
		HttpServiceInstance service = (HttpServiceInstance) HttpServiceInstance
				.getInstance(context.getRoot());
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

		IRepository repository = sapInstance.getRepository();
		JCO.Client client = null;
		try {
			// Get a function template from the repository
			IFunctionTemplate ftemplate = repository
					.getFunctionTemplate(function);
			logger.info("function template:" + function);
			// if the function definition was found in backend system
			if (ftemplate != null) {

				// Create a function from the template
				JCO.Function function = ftemplate.getFunction();

				// Get a client from the pool
				// client = JCO.getClient(sapInstance.SID);
				client = sapInstance.getClient();

				logger.config("connected to " + sapInstance.getServer_ip()
						+ ":" + sapInstance.getSid());

				JCO.ParameterList input = function.getImportParameterList();
				JCO.ParameterList output = function.getExportParameterList();

				// String s_client = input.getStructure("CLIENT");
				// JCO.Structure s_client=null;
				// input.setValue(sapInstance.SAP_CLIENT,"CLIENT");
				if (parameters != null)
					for (int i = 0; i < parameters.length; i++) {
						Parameter param = parameters[i];
						if (param.Return_field == null) {
							Object o = param.Source_field == null ? param.Value
									: context.getObject(param.Source_field);
							String value = o == null ? "" : o.toString();
							input.setValue(value, param.Name);
							logger.log(Level.CONFIG, "parameter {0} -> {1}",
									new Object[] { param.Name, value });
						}
					}
				if (structures != null) {
					for (int i = 0; i < structures.length; i++) {
						Structure structure = structures[i];
						structure.setLogger(logger);
						if (structure.isImport()) {
							JCO.Structure stc = structure
									.getJCOStructure(input);
							structure.fillJCOStructure(stc, context);
							input.setValue(stc, structure.Name);
						}
					}
				}
				// Set import table
				if (tables != null) {
					ParameterList list = function.getTableParameterList();
					for (int i = 0; i < tables.length; i++) {
						Table table = tables[i];
						table.setLogger(logger);
						if (table.isImport()) {
							JCO.Table tbl = table.getJCOTable(list);
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
				client.execute(function);

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
										"jco-invoke: return field "
												+ param.Name + " is null");
							String f = TextParser.parse(param.Return_field,
									context);
							target.putObject(f, vl);
							logger.config("return: " + param.Name + "=" + vl
									+ " -> " + f);
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
						JCO.Structure stc = structure.getJCOStructure(output);
						CompositeMap result = (CompositeMap) context
								.getObject(structure.Target);
						if (result == null)
							result = context.createChildByTag(structure.Target);
						structure.fillCompositeMap(stc, result);
					}
				}
				// Get export tables
				if (tables != null) {
					ParameterList list = function.getTableParameterList();
					if (list == null)
						throw new IllegalArgumentException("Function '"
								+ function + "' doesn't return tables");
					for (int i = 0; i < tables.length; i++) {
						Table table = tables[i];
						if (table.isImport())
							continue;
						if (table.Target == null)
							throw new ConfigurationError(
									"Must set 'target' attribute for table "
											+ table.Name);
						table.setLogger(logger);
						JCO.Table records = table.getJCOTable(list);
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
								+ " into path '" + table.Target + "', total "
								+ rc + " record(s)");

					}
				}
				// finish
				logger.config("jco invoke finished");
			} else {
				throw new IllegalArgumentException("Function '" + function
						+ "' not found in SAP system.");
			}
		} finally {
			JCO.releaseClient(client);
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
