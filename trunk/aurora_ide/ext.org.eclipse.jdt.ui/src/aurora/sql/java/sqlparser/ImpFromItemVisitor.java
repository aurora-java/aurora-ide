package aurora.sql.java.sqlparser;

import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.IntoTableVisitor;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;

public class ImpFromItemVisitor implements FromItemVisitor, ItemsListVisitor,
		IntoTableVisitor {
	private IParserReporter reporter;

	public ImpFromItemVisitor(IParserReporter reporter) {
		this.reporter = reporter;
	}

	public void visit(Table t) {
		reporter.tableFounded(t);
	}

	public void visit(SubSelect ss) {
		Debug.err(this, ss.getAlias(), "visit(SubSelect u) .ss.getAlias(), ");
		ss.getSelectBody().accept(new ImpSelectVisitor(reporter));
	}

	public void visit(SubJoin sj) {
		Debug.err(this, sj.getAlias(), "visit(SubJoin u) .sj.getAlias(), ");
		Debug.err(this, sj.getJoin(), "visit(SubJoin u) .sj.getJoin(), ");
		// sj.getJoin().getRightItem().accept(new ImpFromItemVisitor(reporter));
		new ImpSelectVisitor(reporter).visit(sj.getJoin());
		sj.getLeft().accept(new ImpFromItemVisitor(reporter));

		// sj.accept(new ImpFromItemVisitor(reporter));
	}

	public void visit(ExpressionList el) {
		// Debug.err(this,el.getExpressions(),"visit(ExpressionList u) .el.getExpressions(), ");
		new ImpSelectVisitor(reporter).visit(el.getExpressions());
		// el.accept((ItemsListVisitor) new ImpFromItemVisitor(reporter));
	}

}
