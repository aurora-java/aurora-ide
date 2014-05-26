package aurora.sql.java.sqlparser;

import java.util.List;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.IntoTableVisitor;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;

public class ImpStatementVisitor implements StatementVisitor {
	private IParserReporter reporter;
	private ImpSelectVisitor selectVisitor;

	public ImpStatementVisitor(IParserReporter reporter) {
		this.reporter = reporter;
		selectVisitor = new ImpSelectVisitor(reporter);
	}

	public void visit(Select s) {
		visitList(s.getWithItemsList());
		s.getSelectBody().accept(selectVisitor);
	}

	private void visitList(List list) {
		selectVisitor.visit(list);
	}

	public void visit(Delete d) {
		visit(d.getTable());
		visit(d.getWhere());
	}

	public void visit(Table table) {
		if (table != null) {
			table.accept((FromItemVisitor) new ImpFromItemVisitor(reporter));
			table.accept((IntoTableVisitor) new ImpFromItemVisitor(reporter));
		}
	}

	public void visit(Expression expression) {
		if (expression != null)
			expression.accept(new ImpExpressionVisitor(reporter));
	}

	public void visit(Update u) {
		visitList(u.getColumns());
		visitList(u.getExpressions());
		visit(u.getTable());
		visit(u.getWhere());
	}

	public void visit(Insert i) {
		visitList(i.getColumns());
		ItemsList itemsList = i.getItemsList();
		if (itemsList != null) {
			itemsList.accept(new ImpFromItemVisitor(reporter));
		}
		visit(i.getTable());
		// i.accept(new ImpStatementVisitor(reporter));
	}

	public void visit(Replace r) {
		visitList(r.getColumns());
		ItemsList itemsList = r.getItemsList();
		if (itemsList != null) {
			itemsList.accept(new ImpFromItemVisitor(reporter));
		}
		visitList(r.getExpressions());
		visit(r.getTable());
	}

	public void visit(Drop d) {
		Debug.err(this, d.getName(), "visit(Drop u) .d.getName(), ");
		Debug.err(this, d.getType(), "visit(Drop u) .d.getType(), ");
		visitList(d.getParameters());
	}

	public void visit(Truncate t) {
		visit(t.getTable());
	}

	public void visit(CreateTable ct) {
		visitList(ct.getColumnDefinitions());
		visitList(ct.getIndexes());
		visitList(ct.getTableOptionsStrings());
		visit(ct.getTable());
	}

}
