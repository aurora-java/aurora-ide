package aurora.ide.helpers;

import java.sql.Connection;

import org.eclipse.core.resources.IProject;

public class DBManager {

	private IProject project;

	public DBManager(IProject project) {
		this.project = project;
	}

	public Connection getConnection() throws ApplicationException {
		return DBConnectionUtil.getDBConnection(project);
	}

}
