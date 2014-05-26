package aurora.sql.java.sqlparser;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;

public class DefaultParserReporter implements IParserReporter {

	private List<SelectExpressionItem> columns = new ArrayList<SelectExpressionItem>();

	private List<Table> tables = new ArrayList<Table>();

	public void tableFounded(Table table) {
		getTables().add(table);
		// table.getAlias();
		// table.getName();
		// table.getSchemaName();
		// table.getWholeTableName();
	}

	public void columnFounded(Column a) {
		SelectExpressionItem sei = new SelectExpressionItem();
		sei.setExpression(a);
		getColumns().add(sei);
	}

	public List<Table> getTables() {
		return tables;
	}

	public List<SelectExpressionItem> getColumns() {
		return columns;
	}

	public void columnFounded(SelectExpressionItem a) {
		// class net.sf.jsqlparser.statement.select.SubSelect
		// class net.sf.jsqlparser.schema.Column
		SelectExpressionItem sei = new SelectExpressionItem();
		sei.setAlias(a.getAlias());
		sei.setExpression(a.getExpression());
		getColumns().add(sei);
	}

}
