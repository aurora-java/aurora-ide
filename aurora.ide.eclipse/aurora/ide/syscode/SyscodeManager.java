package aurora.ide.syscode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SyscodeManager {

	public static final String SQL = "select code_id, code, code_value, code_value_name, language,enabled_flag from sys_service_lookup_v where CODE=?";

	public static List<Syscode> getSyscode(String code, Connection conn) {
		List<Syscode> result = new ArrayList<Syscode>();
		PreparedStatement sta = null;
		if (conn != null) {
			try {
				sta = conn.prepareStatement(SQL);
				sta.setString(1, code);
				ResultSet resultSet = sta.executeQuery();
				while (resultSet.next()) {
					Syscode c = new Syscode();
					c.setCode(resultSet.getString("code"));
					c.setCode_id(resultSet.getString("code_id"));
					c.setCode_value(resultSet.getString("code_value"));
					c.setCode_value_name(resultSet.getString("code_value_name"));
					c.setLanguage(resultSet.getString("language"));
					c.setEnabled_flag(resultSet.getString("enabled_flag"));
					result.add(c);
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

}
