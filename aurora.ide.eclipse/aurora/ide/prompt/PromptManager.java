package aurora.ide.prompt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.eclipse.core.resources.IFile;

import uncertain.composite.CompositeMap;

import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.DBConnectionUtil;

public class PromptManager {
	private static final String SQL = "select  language, description  from sys_prompts t where t.prompt_code=?";

	private static final String IN_CODES_PARA = "%PROMPT_CODES%";
	private static final String IN_SQL = "select t.language, t.description,t.prompt_code from sys_prompts t "
			+ "where t.prompt_code in (" + IN_CODES_PARA + ")";
	final private static String COMMENT_SQL = "select  c.COLUMN_NAME,c.comments  from  user_col_comments c where  c.table_name=?";

	public static String[] getPrompts(String code, Connection conn) {
		String[] result = new String[2];
		PreparedStatement sta = null;
		if (conn != null) {
			try {
				sta = conn.prepareStatement(SQL);
				sta.setString(1, code);
				ResultSet resultSet = sta.executeQuery();
				while (resultSet.next()) {
					String language = resultSet.getString("language");
					String description = resultSet.getString("description");
					// ZHS / US
					if ("ZHS".equalsIgnoreCase(language)) {
						result[0] = description;
					}
					if ("US".equalsIgnoreCase(language)) {
						result[1] = description;
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (sta != null) {
					try {
						sta.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return result;
	}

	public static CompositeMap getPrompts(List<String> codes, IFile file) {
		Connection conn = null;
		try {
			conn = DBConnectionUtil.getDBConnection(file.getProject());
		} catch (ApplicationException e1) {
			e1.printStackTrace();
		}
		CompositeMap result = new CommentCompositeMap();
		String sql = createInSql(codes);
		if (sql == null)
			return result;
		PreparedStatement sta = null;
		if (conn != null) {
			try {
				sta = conn.prepareStatement(sql);
				ResultSet resultSet = sta.executeQuery();
				while (resultSet.next()) {
					String language = resultSet.getString("language");
					String description = resultSet.getString("description");
					String prompt_code = resultSet.getString("prompt_code");
					CompositeMap child = result.getChildByAttrib("prompt_code",
							prompt_code);
					if (child == null) {
						child = result.createChild(prompt_code);
					}
					child.put("prompt_code", prompt_code);
					child.put(language, description);
					// // ZHS / US
					// if ("ZHS".equalsIgnoreCase(language)) {
					// result[0] = description;
					// }
					// if ("US".equalsIgnoreCase(language)) {
					// result[1] = description;
					// }
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (sta != null) {
					try {
						sta.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return result;
	}

	private static String createInSql(List<String> codes) {
		StringBuilder sb = new StringBuilder();
		for (String c : codes) {
			sb.append(" '");
			sb.append(c);
			sb.append("' ");
			sb.append(",");
		}
		if (sb.length() == 0)
			return null;
		return IN_SQL.replaceAll(IN_CODES_PARA,
				sb.substring(0, sb.length() - 2));
	}

	public static CompositeMap getColumnComments(String baseTable, IFile file) {
		Connection conn = null;
		try {
			conn = DBConnectionUtil.getDBConnection(file.getProject());
		} catch (ApplicationException e1) {
			e1.printStackTrace();
		}
		CompositeMap result = new CommentCompositeMap();
		PreparedStatement sta = null;
		if (conn != null) {
			try {
				sta = conn.prepareStatement(COMMENT_SQL);
				sta.setString(1, baseTable.toUpperCase());
				ResultSet resultSet = sta.executeQuery();
				while (resultSet.next()) {
					CompositeMap c = result.createChild(baseTable.toUpperCase());
					c.put("column_name", resultSet.getString("column_name"));
					c.put("comments", resultSet.getString("comments"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (sta != null) {
					try {
						sta.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return result;
	}

	public static boolean isPromptsCode(String key) {
		// "AB.A_9V".matches("[A-Z\\d_]+(\\.[A-Z\\d_]+)*")
		return key.matches("[A-Z\\d_]+(\\.[A-Z\\d_]+)*");
	}
}
