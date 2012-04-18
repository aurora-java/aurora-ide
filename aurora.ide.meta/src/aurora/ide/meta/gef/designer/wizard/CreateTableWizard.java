package aurora.ide.meta.gef.designer.wizard;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;

public class CreateTableWizard extends Wizard implements IRunnableWithProgress {
	ArrayList<ObjectDescriptor> model = new ArrayList<ObjectDescriptor>();
	PreCreateTablePage pctp = new PreCreateTablePage();
	private Statement stmt;

	public CreateTableWizard() {
		setWindowTitle("Create Table Wizard");
	}

	@Override
	public void addPages() {
		addPage(pctp);

	}

	public void setSelection(ArrayList<IFile> als) {
		for (IFile f : als) {
			ObjectDescriptor odt = new ObjectDescriptor();
			odt.name = f.getFullPath().removeFileExtension().lastSegment();
			odt.type = "TABLE";
			model.add(odt);
			ObjectDescriptor ods = new ObjectDescriptor();
			ods.name = odt.name + "_s";
			ods.type = "SEQUENCE";
			model.add(ods);
		}
		checkExists();
		pctp.setModel(model);
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	public HashMap<String, Boolean> getConfig() {
		HashMap<String, Boolean> map = new HashMap<String, Boolean>();
		for (ObjectDescriptor od : model) {
			map.put(od.name.toLowerCase(), od.create);
		}
		return map;
	}

	class ObjectDescriptor {
		String name = "";
		String type = "";
		boolean exists = false;
		boolean create = true;
	}

	public void setStatement(Statement stmt) {
		this.stmt = stmt;
	}

	private void checkExists() {
		try {
			new ProgressMonitorDialog(getShell()).run(true, true, this);
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		monitor.beginTask("checking which object already exists...",
				model.size());
		try {
			for (ObjectDescriptor od : model) {
				ResultSet rs = stmt
						.executeQuery("select 1 from user_objects o where o.OBJECT_NAME= '"
								+ od.name.toUpperCase() + "'");
				od.exists = rs.next();
				od.create = !od.exists;
				monitor.worked(1);
				monitor.subTask(String.format("checking %s %s ...", od.type,
						od.name));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		monitor.done();
	}
}
