package aurora.ide.meta.popup.actions;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import uncertain.composite.CompositeMap;
import aurora.ide.bm.AuroraDataBase;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.StatusUtil;
import aurora.ide.meta.MetaPlugin;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.designer.IDesignerConst;
import aurora.ide.meta.gef.designer.gen.SqlGenerator;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.ModelUtil;
import aurora.ide.meta.gef.designer.wizard.CreateTableWizard;
import aurora.ide.meta.project.AuroraMetaProject;
import aurora.ide.search.cache.CacheManager;

public class AutoCreateTableAction implements IObjectActionDelegate,
		IRunnableWithProgress {

	private Shell shell;
	private ArrayList<IFile> als = new ArrayList<IFile>();
	private ArrayList<IStatus> errorMsgs = new ArrayList<IStatus>();
	private Connection conn;
	private Statement stmt;
	private HashMap<String, Boolean> config;

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

	boolean initdb() {
		try {
			IProject proj = als.get(0).getProject();
			AuroraMetaProject amp = new AuroraMetaProject(proj);
			IProject project = amp.getAuroraProject();
			conn = new AuroraDataBase(project).getDBConnection();
			stmt = conn.createStatement();
		} catch (ApplicationException e) {
			StatusUtil.showExceptionDialog(shell,
					"Error", Messages.AutoCreateTableAction_1, true, e); //$NON-NLS-1$
			return false;
		} catch (SQLException e) {
			StatusUtil.showExceptionDialog(shell,
					"Error", Messages.AutoCreateTableAction_3, //$NON-NLS-1$
					true, e);
			return false;
		} catch (ResourceNotFoundException e) {
			StatusUtil.showExceptionDialog(shell,
					"Error", Messages.AutoCreateTableAction_5, true, e); //$NON-NLS-1$
			return false;
		}
		return true;
	}

	void closedb() {
		if (stmt != null)
			try {
				stmt.close();
			} catch (SQLException e) {
			}
		if (conn != null)
			try {
				conn.close();
			} catch (SQLException e) {
			}
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		if (als.size() == 0)
			return;
		errorMsgs.clear();
		if (!initdb())
			return;
		CreateTableWizard ctw = new CreateTableWizard();
		ctw.setStatement(stmt);
		ctw.setSelection(als);
		WizardDialog wd = new WizardDialog(shell, ctw);
		if (wd.open() == WizardDialog.OK) {
			config = ctw.getConfig();
			execute();
		}
		closedb();
	}

	private void execute() {
		try {
			new ProgressMonitorDialog(null).run(false, true, this);
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		if (errorMsgs.size() > 0) {
			MultiStatus status = new MultiStatus(MetaPlugin.PLUGIN_ID,
					IStatus.ERROR, getStatusChildren(), null, null);
			ErrorDialog.openError(shell, Messages.AutoCreateTableAction_6,
					Messages.AutoCreateTableAction_7, status);
			return;
		}
		MessageBox mb = new MessageBox(shell, SWT.ICON_INFORMATION);
		mb.setText(Messages.AutoCreateTableAction_8);
		mb.setMessage(Messages.AutoCreateTableAction_9);
		mb.open();
	}

	private void createTable(String[] sqls, String tableName)
			throws SQLException {
		try {
			for (String s : sqls)
				stmt.executeUpdate(s);
		} catch (SQLException e) {
			if (e.getMessage().indexOf("ORA-00955") != -1) { //$NON-NLS-1$
				stmt.executeUpdate("drop table " + tableName);
				for (String s : sqls)
					stmt.executeUpdate(s);
			} else {
				IStatus s = new Status(IStatus.WARNING, MetaPlugin.PLUGIN_ID,
						NLS.bind(Messages.AutoCreateTableAction_13, tableName),
						e);
				errorMsgs.add(s);
			}
		}
	}

	private void createSequence(String seqName) throws SQLException {
		try {
			stmt.executeUpdate(NLS.bind(Messages.AutoCreateTableAction_15,
					seqName));
		} catch (SQLException e) {
			if (e.getMessage().indexOf("ORA-00955") != -1) { //$NON-NLS-1$
				stmt.executeUpdate(NLS.bind(Messages.AutoCreateTableAction_17,
						seqName));
				stmt.executeUpdate(NLS.bind(Messages.AutoCreateTableAction_15,
						seqName));
			} else {
				IStatus s = new Status(IStatus.WARNING, MetaPlugin.PLUGIN_ID,
						NLS.bind(Messages.AutoCreateTableAction_19, seqName), e);
				errorMsgs.add(s);
			}
		}
	}

	private IStatus[] getStatusChildren() {
		IStatus[] ss = new IStatus[als.size()];
		return errorMsgs.toArray(ss);
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

	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		monitor.beginTask(Messages.AutoCreateTableAction_22, config.size());
		for (IFile file : als) {
			try {
				CompositeMap map = CacheManager.getCompositeMap(file);
				BMModel model = ModelUtil.fromCompositeMap(map);
				SqlGenerator sqlg = new SqlGenerator(model, file.getFullPath()
						.removeFileExtension().lastSegment());
				String[] sqls = sqlg.gen();
				try {
					String tableName = file.getFullPath().removeFileExtension()
							.lastSegment().toLowerCase();
					String seqName = tableName + "_s";
					if (config.get(tableName)) {
						monitor.subTask(NLS.bind(
								Messages.AutoCreateTableAction_24, tableName));
						createTable(sqls, tableName);
					}
					monitor.worked(1);
					if (config.get(seqName)) {
						monitor.subTask(NLS.bind(
								Messages.AutoCreateTableAction_26, seqName));
						createSequence(seqName);
					}
					monitor.worked(1);
				} catch (SQLException e) {
					IStatus s = new Status(IStatus.ERROR, MetaPlugin.PLUGIN_ID,
							NLS.bind(Messages.AutoCreateTableAction_28,
									e.getMessage()), e);
					errorMsgs.add(s);
				}
			} catch (Exception e) {
				IStatus s = new Status(IStatus.ERROR, MetaPlugin.PLUGIN_ID,
						NLS.bind(Messages.AutoCreateTableAction_29,
								file.getFullPath()), e);
				errorMsgs.add(s);
			}
		}
		monitor.done();
	}

}
