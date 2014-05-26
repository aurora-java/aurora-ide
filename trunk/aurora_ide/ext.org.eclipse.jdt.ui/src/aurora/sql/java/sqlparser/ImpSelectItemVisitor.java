package aurora.sql.java.sqlparser;

import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;

public class ImpSelectItemVisitor implements SelectItemVisitor {

	private IParserReporter reporter;

	public ImpSelectItemVisitor(IParserReporter reporter) {
		this.reporter = reporter;
	}

	public void visit(AllColumns a) {
		// just *
	}

	public void visit(AllTableColumns a) {
		// just table.*
		reporter.tableFounded(a.getTable());
	}

	public void visit(SelectExpressionItem a) {
		a.getExpression().accept(new ImpExpressionVisitor(reporter));
		Debug.err(this, a.getAlias(),
				"visit(SelectExpressionItem u) a.getAlias(), ");
	}

}
