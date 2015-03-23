package aurora.ide.fake.uncertain.engine;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import aurora.datasource.DatabaseConnection;
import aurora.ide.helpers.LogUtil;

public class FakeDataSource implements DataSource {

	private String name;
	private String driverClass;
	private String url;
	private String userName;
	private String password;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public PrintWriter getLogWriter() throws SQLException {
		throw new RuntimeException("unsupport methord");
	}

	public void setLogWriter(PrintWriter out) throws SQLException {
		throw new RuntimeException("unsupport methord");
	}

	public void setLoginTimeout(int seconds) throws SQLException {
		throw new RuntimeException("unsupport methord");
	}

	public int getLoginTimeout() throws SQLException {
		throw new RuntimeException("unsupport methord");
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new RuntimeException("unsupport methord");
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new RuntimeException("unsupport methord");
	}

	public Connection getConnection() throws SQLException {
		try {
			Class.forName(this.getDriverClass());
			DriverManager.setLoginTimeout(5000);
			Connection conn = DriverManager.getConnection(url, userName, password);
			return conn;
		} catch (ClassNotFoundException e) {
			throw new SQLException(e);
		}catch (SQLException e){
			LogUtil.getInstance().logError(url, e);
			throw e;
		}
	}

	public Connection getConnection(String username, String password)
			throws SQLException {
		try {
			Class.forName(this.getDriverClass());
			Connection conn = DriverManager.getConnection(url, username, password);
			return conn;
		} catch (ClassNotFoundException e) {
			throw new SQLException(e);
		}catch (SQLException e){
			LogUtil.getInstance().logError(url, e);
			throw e;
		}
		
	}

	public static DataSource createDataSource(DatabaseConnection dbConfig) {
		FakeDataSource fakeDataSource = new FakeDataSource();
		fakeDataSource.setDriverClass(dbConfig.getDriverClass());
		fakeDataSource.setName(dbConfig.getName());
		fakeDataSource.setUrl(dbConfig.getUrl());
		fakeDataSource.setUserName(dbConfig.getUserName());
		fakeDataSource.setPassword(dbConfig.getPassword());
		return fakeDataSource;
	}

}
