package aurora.sql.java.sqlparser;

import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.InverseExpression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

public class ImpExpressionVisitor implements ExpressionVisitor {
	private IParserReporter reporter;

	public ImpExpressionVisitor(IParserReporter reporter) {
		this.reporter = reporter;
	}

	public void visit(NullValue a) {
		// a.accept(new ImpExpressionVisitor(reporter));
	}

	public void visit(Function a) {
		a.getName();
		a.getParameters();
		// a.accept(this);
	}

	public void visit(InverseExpression a) {
		a.getExpression();
		// a.accept(this);
	}

	public void visit(JdbcParameter a) {
		// a.accept(this);
		// just ?
	}

	public void visit(DoubleValue a) {
		a.getValue();
		// a.accept(this);
	}

	public void visit(LongValue a) {
		a.getStringValue();
		a.getValue();
		// a.accept(this);
	}

	public void visit(DateValue a) {
		a.getValue();
	}

	public void visit(TimeValue a) {
		a.getValue();
	}

	public void visit(TimestampValue a) {
		a.getValue();
	}

	public void visit(Parenthesis a) {
		// a.accept(this);

		a.getExpression().accept(this);
	}

	public void visit(StringValue a) {
		a.getNotExcapedValue();
		a.getValue();
	}

	public void visit(Addition a) {
		// a.accept(this);
		visitBinaryExpression(a);
	}

	public void visitBinaryExpression(BinaryExpression binaryExpression) {
		binaryExpression.getLeftExpression().accept(this);
		binaryExpression.getRightExpression().accept(this);
	}

	public void visit(Division a) {
		visitBinaryExpression(a);
	}

	public void visit(Multiplication a) {
		visitBinaryExpression(a);
	}

	public void visit(Subtraction a) {
		visitBinaryExpression(a);
	}

	public void visit(AndExpression a) {
		visitBinaryExpression(a);
	}

	public void visit(OrExpression a) {
		visitBinaryExpression(a);
	}

	public void visit(Between a) {
		// visitBinaryExpression(a);
		// a.accept(expressionVisitor)
		a.getLeftExpression().accept(this);
		a.getBetweenExpressionStart().accept(this);
		a.getBetweenExpressionEnd().accept(this);

	}

	public void visit(EqualsTo a) {
		visitBinaryExpression(a);
	}

	public void visit(GreaterThan a) {
		visitBinaryExpression(a);

	}

	public void visit(GreaterThanEquals a) {
		visitBinaryExpression(a);
	}

	public void visit(InExpression a) {
		a.getItemsList();
		a.getLeftExpression().accept(this);
	}

	public void visit(IsNullExpression a) {
		a.getLeftExpression().accept(this);
	}

	public void visit(LikeExpression a) {
		visitBinaryExpression(a);
	}

	public void visit(MinorThan a) {
		visitBinaryExpression(a);
	}

	public void visit(MinorThanEquals a) {
		visitBinaryExpression(a);
	}

	public void visit(NotEqualsTo a) {
		visitBinaryExpression(a);
	}

	public void visit(Column a) {
		this.reporter.columnFounded(a);
	}

	public void visit(SubSelect a) {
		Debug.err(this, a.getAlias(), "visit(SubSelect u) .ss.getAlias(), ");
		a.getSelectBody().accept(new ImpSelectVisitor(reporter));
		// a.accept(new ImpExpressionVisitor(reporter));
		// a.accept((FromItemVisitor) new ImpFromItemVisitor(reporter));
		// a.accept((ItemsListVisitor) new ImpFromItemVisitor(reporter));
	}

	public void visit(CaseExpression a) {
		Expression elseExpression = a.getElseExpression();
		if (elseExpression != null)
			elseExpression.accept(this);
		Expression switchExpression = a.getSwitchExpression();
		if (switchExpression != null)
			switchExpression.accept(this);
	}

	public void visit(WhenClause a) {
		a.getThenExpression().accept(this);
		a.getWhenExpression().accept(this);
	}

	public void visit(ExistsExpression a) {
		a.getRightExpression().accept(this);
	}

	public void visit(AllComparisonExpression a) {
		visit(a.GetSubSelect());
	}

	public void visit(AnyComparisonExpression a) {
		visit(a.GetSubSelect());
	}

	public void visit(Concat a) {
		visitBinaryExpression(a);
	}

	public void visit(Matches a) {
		visitBinaryExpression(a);
	}

	public void visit(BitwiseAnd a) {
		visitBinaryExpression(a);
	}

	public void visit(BitwiseOr a) {
		visitBinaryExpression(a);
	}

	public void visit(BitwiseXor a) {
		visitBinaryExpression(a);
	}

}
