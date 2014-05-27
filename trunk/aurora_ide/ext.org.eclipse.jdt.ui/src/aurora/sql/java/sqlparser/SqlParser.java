package aurora.sql.java.sqlparser;

import java.io.StringReader;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import sqlj.exception.ParserException;
import sqlj.parser.ParameterParser;
import sqlj.parser.ParsedSql;

public class SqlParser {
	private String sql;

	private String sqlj;
	private int pos;

	private CharSequence prefix;

	public SqlParser(String sqlj, int pos, CharSequence prefix) {
		this.sqlj = sqlj;
		this.pos = pos;
		this.prefix = prefix;
	}

	private String prepareSql() {
		if (sql == null) {
			try {

				ParameterParser pparser = new ParameterParser(sqlj);
				ParsedSql psql;
				psql = pparser.parse();
				String stringLiteral = psql.toStringLiteral();
				int l = prefix.length();

				sql = stringLiteral.substring(0, pos - l)
						+ stringLiteral.substring(pos);
				return sql;
			} catch (ParserException e) {
				e.printStackTrace();
			}
		}
		return sql;
	}

	private String getSQL() {
		return prepareSql();
	}

	public void parse(DefaultParserReporter reporter)
			throws JSQLParserException {
		String sql = getSQL();
		if (sql == null)
			return;
		Statement parsedStm = parse(sql);
		parsedStm.accept(new ImpStatementVisitor(reporter));
	}

	static public Statement parse(String sql) throws JSQLParserException {
		CCJSqlParserManager parserManager = new CCJSqlParserManager();

		Statement parsedStm = parserManager.parse(new StringReader(sql));

		return parsedStm;
	}

}
