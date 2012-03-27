package aurora.ide.meta.gef.designer;

public enum DataType {
	TEXT("varchar2(50)", "VARCHAR2", "java.lang.String", "text") {
	},
	LONG_TEXT("clob", "VARCHAR2", "java.lang.String", "long text") {
	},
	INTEGER("number", "NUMBER", "java.lang.Long", "integer") {
	},
	FLOAT("number(20,2)", "NUMBER", "java.lang.Double", "float") {
	},
	DATE("date", "DATE", "java.sql.Date", "date") {
	},
	DATE_TIME("date", "TIMESTAMP", "java.sql.Date", "dateTime") {
	};

	private String sqlType;
	private String dbType;
	private String javaType;
	private String displayType;

	private DataType(String sqlType, String dbType, String javaType,
			String displayType) {
		this.sqlType = sqlType;
		this.dbType = dbType;
		this.javaType = javaType;
		this.displayType = displayType;
	}

	public String getSqlType() {
		return sqlType;
	}

	public String getDbType() {
		return dbType;
	}

	public String getJavaType() {
		return javaType;
	}

	public String getDisplayType() {
		return displayType;
	}

	public static DataType fromString(String str) {
		for (DataType dt : values()) {
			if (dt.getDisplayType().equals(str))
				return dt;
		}
		return null;
	}
}
