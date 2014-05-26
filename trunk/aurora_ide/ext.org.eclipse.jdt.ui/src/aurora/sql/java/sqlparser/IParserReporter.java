package aurora.sql.java.sqlparser;

import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;

public interface IParserReporter {
	void tableFounded(Table table);

	void columnFounded(Column a);

	void columnFounded(SelectExpressionItem a);

}
