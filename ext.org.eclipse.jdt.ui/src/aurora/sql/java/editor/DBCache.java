package aurora.sql.java.editor;

import java.util.ArrayList;
import java.util.List;

public class DBCache {

	static public List<String> getTableList() {
		List<String> tables = new ArrayList<String>();

		tables.add("From_DB_Table1");
		tables.add("From_DB_Table2");
		tables.add("From_DB_Table3");
		tables.add("From_DB_Table4");

		return tables;
	}

	static public List<String> getColumnList(String table) {
		List<String> columns = new ArrayList<String>();
		columns.add(table + "_DB_Column1");
		columns.add(table + "_DB_Column2");
		columns.add(table + "_DB_Column3");
		columns.add(table + "_DB_Column4");
		return columns;
	}

}
