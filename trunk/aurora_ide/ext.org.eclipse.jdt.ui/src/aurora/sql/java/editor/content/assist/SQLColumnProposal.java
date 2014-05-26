package aurora.sql.java.editor.content.assist;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.contentassist.CompletionProposal;

import aurora.sql.java.editor.DBCache;
import aurora.sql.java.sqlparser.DefaultParserReporter;
import aurora.sql.java.sqlparser.SqlParser;

public class SQLColumnProposal extends AbctractSQLProposal {
	private SqlContentAssistInvocationContext context;

	public SQLColumnProposal(SqlContentAssistInvocationContext context) {
		super(context);
		this.context = context;
	}

	@Override
	public List<CompletionProposal> getCompletionProposal()
			throws BadLocationException {
		List<CompletionProposal> cps = new ArrayList<CompletionProposal>();
		String sql = context.getSql();
		int pos = context.getInvocationOffset()
				- context.getPartition().getOffset() - 2;
		SqlParser sqlParser = new SqlParser(sql, pos,context.computeIdentifierPrefix());
		DefaultParserReporter reporter = new DefaultParserReporter();
		try {
			sqlParser.parse(reporter);
		} catch (JSQLParserException e) {
			e.printStackTrace();
		}
		List<String> cols = new ArrayList<String>();
		List<SelectExpressionItem> columns = reporter.getColumns();
		for (SelectExpressionItem sei : columns) {
			String alias = sei.getAlias();
			if (alias != null && "".equals(alias) == false) {
				cols.add(alias);
			} else {
				Expression expression = sei.getExpression();
				if (expression instanceof Column) {
					String columnName = ((Column) expression).getColumnName();
					cols.add(columnName);
				}
			}
			// cols.add()
		}
		List<Table> tables = reporter.getTables();
		for (Table table : tables) {
			List<String> columnList = DBCache.getColumnList(table.getName());
			cols.addAll(columnList);
		}
		// from db
		CharSequence computeIdentifierPrefix = context
				.computeIdentifierPrefix();
		for (String string : cols) {
			if (string.toLowerCase().startsWith(
					computeIdentifierPrefix.toString().toLowerCase()) == false) {
				continue;
			}
			CompletionProposal cp = new CompletionProposal(string,
					context.getInvocationOffset()
							- computeIdentifierPrefix.length(),
					computeIdentifierPrefix.length(),
					context.getInvocationOffset());
			cps.add(cp);
		}

		return cps;
	}

}
