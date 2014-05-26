package aurora.sql.java.sqlparser.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.List;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import aurora.sql.java.sqlparser.DefaultParserReporter;
import aurora.sql.java.sqlparser.ImpStatementVisitor;

public class CCJSqlParserManagerTest {

	public CCJSqlParserManagerTest(String arg0) {
	}

	public static void main(String[] args) {
		// junit.swingui.TestRunner.run(CCJSqlParserManagerTest.class);
		try {
			new CCJSqlParserManagerTest("").testParse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testParse() throws Exception {
		CCJSqlParserManager parserManager = new CCJSqlParserManager();
		BufferedReader in = new BufferedReader(new FileReader("testfiles"
				+ File.separator + "simple_parsing3.txt"));
		String statement = "";
		DefaultParserReporter reporter = new DefaultParserReporter();
		while (true) {
			try {
				statement = CCJSqlParserManagerTest.getStatement(in);
				if (statement == null)
					break;

				Statement parsedStm = parserManager.parse(new StringReader(
						statement));
				parsedStm.accept(new ImpStatementVisitor(reporter));
				// System.out.println(parsedStm);
				printReslut(reporter);
			} catch (JSQLParserException e) {
				// throw new TestException("impossible to parse statement: "
				// + statement, e);
			}
		}
	}

	private void printReslut(DefaultParserReporter reporter) {
		List<Table> tables = reporter.getTables();
		List<SelectExpressionItem> columns = reporter.getColumns();
		System.out.println(tables);
		System.out.println(columns);
	}

	public static String getStatement(BufferedReader in) throws Exception {
		StringBuffer buf = new StringBuffer();
		String line = null;
		while ((line = CCJSqlParserManagerTest.getLine(in)) != null) {

			if (line.length() == 0)
				break;

			buf.append(line);
			buf.append("\n");

		}

		if (buf.length() > 0) {
			return buf.toString();
		} else {
			return null;
		}

	}

	public static String getLine(BufferedReader in) throws Exception {
		String line = null;
		while (true) {
			line = in.readLine();
			if (line != null) {
				line.trim();
				// if ((line.length() != 0) && ((line.length() < 2) ||
				// (line.length() >= 2) && !(line.charAt(0) == '/' &&
				// line.charAt(1) == '/')))
				if (((line.length() < 2) || (line.length() >= 2)
						&& !(line.charAt(0) == '/' && line.charAt(1) == '/')))
					break;
			} else {
				break;
			}

		}

		return line;
	}

}
