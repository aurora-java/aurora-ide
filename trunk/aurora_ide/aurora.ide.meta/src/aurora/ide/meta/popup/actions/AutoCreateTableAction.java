package aurora.ide.meta.popup.actions;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import uncertain.composite.CompositeMap;
import aurora.ide.editor.textpage.ColorManager;
import aurora.ide.editor.textpage.IColorConstants;
import aurora.ide.editor.textpage.scanners.SQLCodeScanner;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.designer.IDesignerConst;
import aurora.ide.meta.gef.designer.gen.SqlGenerator;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.ModelUtil;
import aurora.ide.meta.project.AuroraMetaProject;
import aurora.ide.search.cache.CacheManager;
import aurora.ide.statistics.DBManager;

public class AutoCreateTableAction implements IObjectActionDelegate {

	private Shell shell;
	private ArrayList<IFile> als = new ArrayList<IFile>();
	private ArrayList<String> errorMsgs = new ArrayList<String>();
	private Connection conn;
	private Statement stmt;

	/**
	 * Constructor for Action1.
	 */
	public AutoCreateTableAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	void initdb() {
		try {
			IProject proj = als.get(0).getProject();
			AuroraMetaProject amp = new AuroraMetaProject(proj);
			IProject project = amp.getAuroraProject();
			DBManager manager = new DBManager(project);
			conn = manager.getConnection();
			stmt = conn.createStatement();
		} catch (ResourceNotFoundException e1) {
			e1.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
	}

	void closedb() {
		if (stmt != null)
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		if (conn != null)
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		if (als.size() == 0)
			return;
		errorMsgs.clear();
		initdb();
		if (stmt == null) {
		} else {
			for (IFile file : als) {
				try {
					CompositeMap map = CacheManager.getCompositeMap(file);
					BMModel model = ModelUtil.fromCompositeMap(map);
					SqlGenerator sqlg = new SqlGenerator(model, file
							.getFullPath().removeFileExtension().lastSegment());
					String sql = sqlg.gen();
					create(stmt, sql, false);
				} catch (Exception e) {
					errorMsgs.add("文件解析异常:" + file.getFullPath());
					continue;
				}
			}
		}
		closedb();
		MessageBox mb = new MessageBox(shell, SWT.ERROR);
		String title = "";
		StringBuilder sb = new StringBuilder(1000);
		if (errorMsgs.size() > 0) {
			title = "Error Happened";
			for (String s : errorMsgs)
				sb.append(s + "\n");
		} else {
			title = "Success";
			sb.append(als.size() + " table(s) created.");
		}
		mb.setText(title);
		mb.setMessage(sb.toString());
		mb.open();
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		als.clear();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			Iterator<?> it = ss.iterator();
			while (it.hasNext()) {
				Object obj = it.next();
				if (obj instanceof IFile) {
					if (((IFile) obj).getName().endsWith(
							"." + IDesignerConst.EXTENSION)) {
						als.add((IFile) obj);
					}
				}
			}
		}
	}

	private void create(Statement stmt, String sql, boolean force) {
		String[] sqls = sql.split(";\\s*");
		String tableName = getTableName(sql);
		try {
			for (String s : sqls)
				stmt.executeUpdate(s);
		} catch (SQLException e) {
			// if (force && e.getMessage().indexOf("ORA-00955") != -1) {
			// drop(stmt, sql);
			// for (String s : sqls)
			// stmt.executeUpdate(s);
			// } else
			if (e.getMessage().indexOf("ORA-00955") != -1) {
				errorMsgs.add("Table '" + tableName
						+ "' create failed. object already exists.");
			} else
				errorMsgs.add("Table '" + tableName + "' create failed. "
						+ e.getMessage());
		}
		try {
			stmt.executeQuery("create sequence " + tableName + "_s");
		} catch (SQLException e) {
			// e.printStackTrace();
			errorMsgs.add("Sequence '" + tableName
					+ "_s' create failed. object already exists.");
		}
	}

	private void drop(Statement stmt, String sql) throws SQLException {
		String tableName = getTableName(sql);
		stmt.executeQuery("drop table " + tableName);
		stmt.executeQuery("drop sequence " + tableName + "_s");
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

}
