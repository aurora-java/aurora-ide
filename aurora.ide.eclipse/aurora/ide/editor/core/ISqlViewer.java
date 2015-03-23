/**
 * 
 */
package aurora.ide.editor.core;

import java.sql.Connection;
import java.sql.ResultSet;


public interface ISqlViewer{
	public Connection  getConnection();
	public String  getSql();
	public void refresh(ResultSet resultSet,int resultCount);
}
