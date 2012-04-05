package aurora.ide.meta.gef.designer;

import aurora.ide.meta.gef.editors.models.Input;

public enum DataType {
	TEXT("varchar2(50)", "VARCHAR2", "java.lang.String", IDesignerConst.TEXT,
			IDesignerConst.OP_EQ, Input.TEXT) {
	},
	LONG_TEXT("clob", "VARCHAR2", "java.lang.String", IDesignerConst.LONG_TEXT,
			IDesignerConst.OP_ANY_MATCH, Input.TEXT) {
	},
	INTEGER("number", "NUMBER", "java.lang.Long", IDesignerConst.INTEGER,
			IDesignerConst.OP_EQ, Input.NUMBER) {
	},
	FLOAT("number(20,2)", "NUMBER", "java.lang.Double", IDesignerConst.FLOAT,
			IDesignerConst.OP_INTERVAL, Input.NUMBER) {
	},
	DATE("date", "DATE", "java.sql.Date", IDesignerConst.DATE,
			IDesignerConst.OP_INTERVAL, Input.CAL) {
	},
	DATE_TIME("date", "TIMESTAMP", "java.sql.Date", IDesignerConst.DATE_TIME,
			IDesignerConst.OP_INTERVAL, Input.DATETIMEPICKER) {
	};

	private String sqlType;// use in generate sql
	private String dbType;// use in bm 'databaseType'
	private String javaType;// use in bm 'dataType'
	private String displayType;// use for display in editor
	private String defaultOperator;
	private String defaultEditor;

	private DataType(String sqlType, String dbType, String javaType,
			String displayType, String defaultOp, String defaultEditor) {
		this.sqlType = sqlType;
		this.dbType = dbType;
		this.javaType = javaType;
		this.displayType = displayType;
		this.defaultOperator = defaultOp;
		this.defaultEditor = defaultEditor;
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

	public String getDefaultOperator() {
		return defaultOperator;
	}

	public String getDefaultEditor() {
		return defaultEditor;
	}

	/**
	 * find the datatype by display type ,Ignore Case
	 * 
	 * @param str
	 * @return
	 */
	public static DataType fromString(String str) {
		for (DataType dt : values()) {
			if (dt.getDisplayType().equalsIgnoreCase(str))
				return dt;
		}
		return null;
	}
}
