package aurora.ide.bm.editor.toolbar.action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import aurora.ide.AuroraPlugin;
import aurora.ide.dialog.ParamQueryDialog;
import aurora.ide.editor.core.ISqlViewer;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.LocaleMessage;

public class ExecuteSqlAction extends Action {
	ISqlViewer viewer;

	public ExecuteSqlAction(ISqlViewer viewer) {
		this.viewer = viewer;
	}

	public ExecuteSqlAction(ISqlViewer viewer, ImageDescriptor imageDescriptor, String text) {
		if (imageDescriptor != null)
			setHoverImageDescriptor(imageDescriptor);
		if (text != null)
			setText(text);
		this.viewer = viewer;
	}

	public void run() {
		Connection conn = viewer.getConnection();
		String sql = viewer.getSql().trim();
		if (sql == null || "".equals(sql)) {
			DialogUtil.showErrorMessageBox("请先输入SQL语句。");
			return;
		}
		String[] parameters = new String[0];
		if (sql.indexOf('$') != -1) {
			sql = sql.replaceAll("\r\n|\r|\n", " ");
			sql = sql.replace("@", "");
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			ParamQueryDialog dialog = new ParamQueryDialog(shell, sql);
			if (Dialog.OK == dialog.open()) {
				sql = sql.replaceAll("\\$\\{[^:}]+\\}", "?");
				parameters = dialog.getValues();
				for (String s : parameters) {
					if (null != s && s.indexOf("~") != -1) {
						sql = sql.replaceFirst("\\$\\{[^}]+\\}", s.substring(1));
					}
				}
			} else {
				return;
			}
		}
		String action = sql.trim().split(" ")[0];
		ResultSet resultSet = null;
		PreparedStatement stmt = null;
		int resultCount = 0;
		try {
			stmt = conn.prepareStatement(sql);
			for (int i = 0, n = 0; i < parameters.length; i++) {
				if (null == parameters[i]) {
					stmt.setString(n + 1, parameters[i]);
					n++;
				} else if (parameters[i].indexOf("~") == -1) {
					stmt.setString(n + 1, parameters[i]);
					n++;
				}
			}
			if ("select".equalsIgnoreCase(action)) {
				resultSet = stmt.executeQuery();
			} else if (action != null) {
				resultCount = stmt.executeUpdate();
			}
			if (resultSet != null) {
				resultCount = resultSet.getFetchSize();
			}
			viewer.refresh(resultSet, resultCount);
		} catch (SQLException e) {
			DialogUtil.showExceptionMessageBox(e);
			return;
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				try {
					if (stmt != null) {
						stmt.close();
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
	}

	public static ImageDescriptor getDefaultImageDescriptor() {
		return AuroraPlugin.getImageDescriptor(LocaleMessage.getString("run.icon"));
	}
}
