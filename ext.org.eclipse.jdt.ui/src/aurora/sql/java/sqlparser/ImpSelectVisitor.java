package aurora.sql.java.sqlparser;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.IntoTableVisitor;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.Union;
import net.sf.jsqlparser.statement.select.WithItem;

public class ImpSelectVisitor implements SelectVisitor {
	private IParserReporter reporter;

	public ImpSelectVisitor(IParserReporter reporter) {
		this.reporter = reporter;
	}

	public void visit(PlainSelect ps) {
		// ps.getDistinct();
		// ps.getGroupByColumnReferences();
		// ps.getJoins();
		// ps.getOrderByElements();
		// ps.getSelectItems();
		visit(ps.getGroupByColumnReferences());
		visit(ps.getJoins());
		visit(ps.getOrderByElements());
		visit(ps.getSelectItems());

		ps.getFromItem().accept(new ImpFromItemVisitor(reporter));
		Expression having = ps.getHaving();
		if (having != null)
			having.accept(new ImpExpressionVisitor(reporter));
		visit(ps.getInto());
		// ps.getLimit();
		// ps.getTop();
		Expression where = ps.getWhere();
		if (where != null)
			where.accept(new ImpExpressionVisitor(reporter));
	}

	public void visit(Table table) {
		if (table == null)
			return;
		table.accept((FromItemVisitor) new ImpFromItemVisitor(reporter));
		table.accept((IntoTableVisitor) new ImpFromItemVisitor(reporter));
	}

	public void visit(Union un) {
		visit(un.getOrderByElements());
		visit(un.getPlainSelects());
	}

	private static final List<Class> knownClass = new ArrayList<Class>() {
		{
			this.add(PlainSelect.class);
			this.add(SelectExpressionItem.class);
			this.add(AllColumns.class);
			this.add(AllTableColumns.class);
			this.add(Join.class);
			this.add(Column.class);
			this.add(OrderByElement.class);
			this.add(Addition.class);
			this.add(WithItem.class);
			this.add(JdbcParameter.class);
			this.add(net.sf.jsqlparser.expression.StringValue.class);
		}
	};

	public void visit(List unKnownTypeList) {
		if (unKnownTypeList != null) {
			for (Object object : unKnownTypeList) {

				if (object instanceof PlainSelect) {
					visit((PlainSelect) object);
				}

				if (object instanceof AllColumns) {
					((AllColumns) object).accept(new ImpSelectItemVisitor(
							reporter));
				}
				if (object instanceof SelectExpressionItem) {
					Expression expression = ((SelectExpressionItem) object)
							.getExpression();
					reporter.columnFounded((SelectExpressionItem) object);
					if (expression instanceof Column) {
					} else {
						expression.accept(new ImpExpressionVisitor(reporter));
					}
					// System.err.println(expression.getClass());
				}
				if (object instanceof AllTableColumns) {
					((AllTableColumns) object).accept(new ImpSelectItemVisitor(
							reporter));
				}

				if (object instanceof Join) {
					visit((Join) object);
				}

				if (object instanceof Column) {
					reporter.columnFounded((Column) object);
				}
				if (object instanceof OrderByElement) {
					((OrderByElement) object).getExpression().accept(
							new ImpExpressionVisitor(reporter));
				}
				if (object instanceof Addition) {
					((Addition) object).accept(new ImpExpressionVisitor(
							reporter));
				}
				if (object instanceof WithItem) {
					SelectBody selectBody = ((WithItem) object).getSelectBody();
					if (selectBody != null)
						selectBody.accept(new ImpSelectVisitor(reporter));
					visit(((WithItem) object).getWithItemList());
				}
				if (knownClass.contains(object.getClass()) == false)
					System.err.println(object.getClass());
			}
		}
	}

	public void visit(Join j) {
		Expression onExpression = (j).getOnExpression();
		if (onExpression != null)
			onExpression.accept(new ImpExpressionVisitor(reporter));
		FromItem rightItem = (j).getRightItem();
		if (rightItem != null)
			rightItem.accept(new ImpFromItemVisitor(reporter));
		visit((j).getUsingColumns());
	}
}
