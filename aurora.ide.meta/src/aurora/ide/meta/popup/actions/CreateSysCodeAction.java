package aurora.ide.meta.popup.actions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import uncertain.composite.CompositeMap;
import aurora.ide.bm.AuroraDataBase;
import aurora.ide.meta.gef.designer.editor.LookupCodeUtil;
import aurora.ide.meta.gef.designer.wizard.CreateSyscodeWizard;
import aurora.ide.meta.project.AuroraMetaProject;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class CreateSysCodeAction implements IObjectActionDelegate,
		IRunnableWithProgress {
	private IFile sysCodeFile;
	private Shell shell;
	private List<CompositeMap> list;
	private Connection conn;
	private Statement stmt;

	public CreateSysCodeAction() {
	}

	boolean initdb() {
		try {
			IProject proj = sysCodeFile.getProject();
			AuroraMetaProject amp = new AuroraMetaProject(proj);
			IProject project = amp.getAuroraProject();
			conn = new AuroraDataBase(project).getDBConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
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

	public void run(IAction action) {
		if (!initdb())
			return;
		CreateSyscodeWizard wizard = new CreateSyscodeWizard();
		wizard.setSysCodeFile(sysCodeFile);
		wizard.setConnection(conn);
		WizardDialog wd = new WizardDialog(shell, wizard);
		if (wd.open() == WizardDialog.OK) {
			list = wizard.getResult();
			try {
				new ProgressMonitorDialog(null).run(false, true, this);
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		closedb();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			if (obj instanceof IFile)
				sysCodeFile = (IFile) obj;
		}
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		monitor.beginTask("create sys_code", list.size());
		for (CompositeMap m : list) {
			String code = LookupCodeUtil.getCode(m);
			monitor.setTaskName("create sys_code " + code + "...");
			// /
			String sql = getSql(m);
			try {
				if (sql == null || sql.length() == 0)
					continue;
				stmt.execute(sql);
				conn.commit();
			} catch (SQLException e) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
			// /
			monitor.worked(1);
		}
	}

	private String getSql(CompositeMap m) {
		HashMap<String, Object> root = new HashMap<String, Object>();
		String code = LookupCodeUtil.getCode(m);
		root.put("code", code);
		root.put("codename", m.getString("code_name"));
		root.put("codeprompt", m.getString("code_prompt"));
		root.put("codenameprompt", m.getString("code_name_prompt"));
		List<HashMap<String, String>> values = new ArrayList<HashMap<String, String>>();
		root.put("values", values);
		List<CompositeMap> list = m.getChildsNotNull();
		for (CompositeMap v : list) {
			HashMap<String, String> value = new HashMap<String, String>();
			value.put("value", LookupCodeUtil.getValue(v));
			value.put("zhs", LookupCodeUtil.getValueNameZHS(v));
			value.put("us", LookupCodeUtil.getValueNameUS(v));
			values.add(value);
		}
		String sql = "";
		OutputStreamWriter writer = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			writer = new OutputStreamWriter(bos);
			Template tpl = LookupCodeUtil.getSourceTemplate();
			if (tpl != null)
				tpl.process(root, writer);
			sql = bos.toString().replace("\r\n", "\n").replace('\r', '\n');
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sql;
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

}
