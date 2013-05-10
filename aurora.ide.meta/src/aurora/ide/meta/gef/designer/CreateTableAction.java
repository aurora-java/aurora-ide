package aurora.ide.meta.gef.designer;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import aurora.ide.editor.textpage.ColorManager;
import aurora.ide.editor.textpage.IColorConstants;
import aurora.ide.editor.textpage.scanners.SQLCodeScanner;
import aurora.ide.helpers.DBManager;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.meta.gef.editors.PrototypeImagesUtils;

public abstract class CreateTableAction extends Action {
	private IProject aProj;
	private String tableName;

	public CreateTableAction(IProject aProj) {
		super("Create Table", PrototypeImagesUtils.getImageDescriptor("run.gif"));
		this.aProj = aProj;
	}

	public void run() {
		DBManager dbm = new DBManager(aProj);
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = dbm.getConnection();
			stmt = conn.createStatement();
			String[] sql = getSQLs();
			tableName = getTableName(sql[0]);
			create(stmt, sql);
			MessageBox mb = new MessageBox(getShell(), SWT.APPLICATION_MODAL);
			mb.setText("Success");
			mb.setMessage("Table : " + tableName + " created.");
			mb.open();
		} catch (Exception e) {
			DialogUtil.showErrorMessageBox(e.getMessage());
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void create(Statement stmt, String[] sqls) throws SQLException {
		try {
			for (String s : sqls)
				stmt.executeUpdate(s);
		} catch (SQLException e) {
			if (isForce() && e.getMessage().indexOf("ORA-00955") != -1) {
				drop(stmt, tableName);
				for (String s : sqls)
					stmt.executeUpdate(s);
			} else
				throw e;
		}
		stmt.executeUpdate("create sequence " + tableName + "_s");
	}

	private void drop(Statement stmt, String tableName) throws SQLException {
		stmt.executeUpdate("drop table " + tableName);
		stmt.executeUpdate("drop sequence " + tableName + "_s");
	}

	private String getTableName(String sql) {
		IDocument doc = new Document();
		doc.set(sql);
		SQLCodeScanner scanner = new SQLCodeScanner(new ColorManager());
		scanner.setRange(doc, 0, sql.length());
		IToken token = null;
		while ((token = scanner.nextToken()) != Token.EOF) {
			if (token.getData() instanceof TextAttribute) {
				TextAttribute text = (TextAttribute) token.getData();
				int tokenOffset = scanner.getTokenOffset();
				int tokenLength = scanner.getTokenLength();
				try {
					if (text.getForeground().getRGB()
							.equals(IColorConstants.SQL_KEYWORD_COLOR)
							&& "table".equalsIgnoreCase(doc.get(tokenOffset,
									tokenLength))) {
						int idx = tokenOffset + tokenLength;
						int idx2 = sql.indexOf('(', idx);
						return sql.substring(idx, idx2).trim();
					}
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		}
		return "";
	}

	private Shell getShell() {
		Display current = Display.getCurrent();
		Shell shell = (current == null ? Display.getDefault() : current)
				.getActiveShell();
		if (shell == null) {
			shell = new Shell(current);
		}
		return shell;
	}

	public abstract String[] getSQLs();

	public abstract boolean isForce();
}
