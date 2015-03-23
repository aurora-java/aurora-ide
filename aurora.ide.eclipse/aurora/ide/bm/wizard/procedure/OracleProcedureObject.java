package aurora.ide.bm.wizard.procedure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;

import uncertain.composite.CompositeMap;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.api.composite.map.CommentXMLOutputter;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.helpers.DBConnectionUtil;
import aurora.ide.helpers.SystemException;

public class OracleProcedureObject {
	public final static String SPACES = " ";
	public String object_name;
	public String procedure_name;
	public int subprogram_id;
	public String object_type;
	public List parameters;
	private IProject project;
	public static Map<String,String> typeMap = new HashMap<String,String>();
	public static Map<String,String> parameterMap = new HashMap<String,String>();
	static {
		typeMap.put("PLS_INTEGER", "java.lang.Long");
		typeMap.put("BOOLEAN", "java.lang.Boolean");
		typeMap.put("NUMBER", "java.lang.Long");
		typeMap.put("INTEGER", "java.lang.Long");
		typeMap.put("CLOB", "java.sql.Clob");
		typeMap.put("DATE", "java.util.Date");
		typeMap.put("VARCHAR2", "java.lang.String");

		parameterMap.put("company_id", "/session/@company_id");
		parameterMap.put("user_id", "/session/@user_id");
		parameterMap.put("session_id", "/session/@session_id");
	}

	/**
	 * @param objectName
	 * @param procedureName
	 * @param subprogramId
	 * @param objectType
	 */
	public OracleProcedureObject(String objectName, String procedureName,
			int subprogramId, String objectType,IProject project ) {
		super();
		object_name = objectName;
		procedure_name = procedureName;
		subprogram_id = subprogramId;
		object_type = objectType;
		this.project = project;
	}

	public String getObject_name() {
		return object_name;
	}

	public void setObject_name(String objectName) {
		object_name = objectName;
	}

	public String getProcedure_name() {
		return procedure_name;
	}

	public void setProcedure_name(String procedureName) {
		procedure_name = procedureName;
	}

	public int getSubprogram_id() {
		return subprogram_id;
	}

	public void setSubprogram_id(int subprogramId) {
		subprogram_id = subprogramId;
	}

	public String getObject_type() {
		return object_type;
	}

	public void setObject_type(String objectType) {
		object_type = objectType;
	}

	public void addParameter(OracleParameter parameter) {
		parameters.add(parameter);
	}

	public List getParameters() {
		return parameters;
	}

	public void setParameters(List parameters) {
		this.parameters = parameters;
	}

	public String toText() throws ApplicationException {
		if (parameters == null)
			initParameters();
		String objectType = "procedure";

		if (parameters.size() <= 0) {
			return objectType + SPACES + getFullObjectName();
		}
		StringBuffer sb = new StringBuffer("");
		boolean firstRecord = true;
		String return_string = null;
		for (Iterator it = parameters.iterator(); it.hasNext();) {
			OracleParameter para = (OracleParameter) it.next();
			if (para.getPosition() == 0) {
				objectType = "function";
				return_string = " return " + para.getPls_type();
				continue;
			}
			if (firstRecord) {
				sb.append("(\r\n");
				firstRecord = false;
			} else {
				sb.append(",\r\n");
			}
			sb.append(para.getName()).append(SPACES).append(para.getIn_out())
					.append(SPACES).append(para.getPls_type());
		}
		if (!firstRecord)
			sb.append(")\r\n");
		if (return_string != null) {
			sb.append(return_string);
		}
		return objectType + SPACES + getFullObjectName() + sb.toString();
	}

	public CompositeMap toCompositeMap() throws ApplicationException {
		if (parameters == null)
			initParameters();
		String pre = "bm";
		CompositeMap model = new CommentCompositeMap(pre, AuroraConstant.BMUri,
				"model");
		CompositeMap operations = new CommentCompositeMap(pre, AuroraConstant.BMUri,
				"operations");
		model.addChild(operations);
		CompositeMap operation = new CommentCompositeMap(pre, AuroraConstant.BMUri,
				"operation");
		operations.addChild(operation);
		operation.put("name", "execute");
		if (parameters.size() > 0) {
			CompositeMap bm_parameters = new CommentCompositeMap(pre,
					AuroraConstant.BMUri, "parameters");
			operation.addChild(bm_parameters);
			for (Iterator it = parameters.iterator(); it.hasNext();) {
				OracleParameter op = (OracleParameter) it.next();
				CompositeMap bm_parameter = new CommentCompositeMap(pre,
						AuroraConstant.BMUri, "parameter");
				bm_parameter.put("name", convertParameter(op.getName()));
				bm_parameter.put("dataType", typeMap.get(op.getPls_type()));
				bm_parameter.put("required", "true");
				bm_parameter.put("input",
						new Boolean(op.getIn_out().indexOf("IN") != -1));
				bm_parameter.put("output",
						new Boolean(op.getIn_out().indexOf("OUT") != -1));
				bm_parameters.addChild(bm_parameter);
			}
		}
		CompositeMap update_sql = new CommentCompositeMap(pre, AuroraConstant.BMUri,
				"update-sql");
		StringBuffer sb = new StringBuffer("");
		String return_string = "";
		if (parameters.size() > 0) {
			boolean firstRecord = true;
			for (Iterator it = parameters.iterator(); it.hasNext();) {
				OracleParameter para = (OracleParameter) it.next();
				if (para.getPosition() == 0) {
					return_string = "${@" + para.getName() + "} := ";
					continue;
				}
				if (firstRecord) {
					sb.append(newLine(5) + "(" + newLine(6));
					firstRecord = false;
				} else {
					sb.append("," + newLine(6));
				}
				sb.append(para.getName().toLowerCase()).append("=>")
						.append("${")
						.append(convertParameterForUse(para.getName()))
						.append("}");
			}
			if (!firstRecord)
				sb.append(newLine(5) + ")");
		}
		sb.append(";" + newLine(4));
		String text = newLine(4) + "begin" + newLine(5) + return_string
				+ getFullObjectName() + sb.toString() + "end;";
		update_sql.setText(text);
		operation.addChild(update_sql);
		return model;
	}

	private String newLine(int level) {
		StringBuffer sb = new StringBuffer(AuroraResourceUtil.LineSeparator);
		for (int i = 0; i < level; i++) {
			// sb.append(XMLOutputter.DEFAULT_INDENT);
			sb.append(CommentXMLOutputter.DEFAULT_INDENT);
		}
		return sb.toString();
	}

	public String convertParameter(String parameterName) {
		if (parameterName == null)
			return null;
		int index = parameterName.indexOf("_");
		String convertpn = parameterName;
		if (index != -1) {
			String pre = parameterName.substring(0, index);
			if (pre.toLowerCase().equals("p") || pre.toLowerCase().equals("v")) {
				convertpn = (parameterName.substring(index + 1)).toLowerCase();
			} else if (pre.toLowerCase().equals("pi")
					|| pre.toLowerCase().equals("po")) {
				convertpn = (parameterName.substring(index + 2)).toLowerCase();
			}
		}
		return convertpn.toLowerCase();
	}

	public String convertParameterForUse(String parameterName) {
		String pn = convertParameter(parameterName);
		Object mappn = parameterMap.get(pn);
		if (mappn != null)
			return (String) mappn;
		if (pn.indexOf("@") == -1) {
			return "@" + pn;
		}
		return pn;
	}

	public String getFullObjectName() {
		String fullObjectName = object_name;
		if (procedure_name != null && !"".equals(procedure_name)) {
			fullObjectName = fullObjectName + "." + procedure_name;
		}
		return fullObjectName;
	}

	public void initParameters() throws ApplicationException {
		parameters = new LinkedList();
		Connection connection = DBConnectionUtil.getDBConnectionSyncExec(project);
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			String select_sql = "select a.argument_name,a.position,a.pls_type,a.in_out from user_arguments a where a.object_name = ?"
					+ " and a.package_name = ? "
					+ " and a.subprogram_id = ? "
					+ " order by a.position";
			st = connection.prepareStatement(select_sql);
			st.setString(1, procedure_name);
			st.setString(2, object_name);
			st.setInt(3, subprogram_id);
			rs = st.executeQuery();
			while (rs.next()) {
				String argument_name = rs.getString(1);
				if ((argument_name == null || "".equals(argument_name))
						&& rs.getInt(2) != 0)
					continue;
				if (argument_name == null || "".equals(argument_name))
					argument_name = "return_value";
				parameters.add(new OracleParameter(argument_name, rs.getInt(2),
						rs.getString(3), rs.getString(4)));
			}
		} catch (SQLException e) {
			throw new SystemException(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
					st.close();
				} catch (SQLException e) {
					throw new SystemException(e);
				}
			}
		}
	}
}

class OracleParameter {
	public String name;
	public int position;
	public String pls_type;
	public String in_out;

	/**
	 * @param name
	 * @param position
	 * @param plsType
	 * @param inOut
	 */
	public OracleParameter(String name, int position, String plsType,
			String inOut) {
		super();
		this.name = name;
		this.position = position;
		pls_type = plsType;
		in_out = inOut;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getPls_type() {
		return pls_type;
	}

	public void setPls_type(String plsType) {
		pls_type = plsType;
	}

	public String getIn_out() {
		return in_out;
	}

	public void setIn_out(String inOut) {
		in_out = inOut;
	}
}
