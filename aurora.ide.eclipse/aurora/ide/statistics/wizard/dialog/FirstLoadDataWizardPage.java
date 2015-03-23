package aurora.ide.statistics.wizard.dialog;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import aurora.ide.AuroraPlugin;
import aurora.ide.AuroraProjectNature;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.DBManager;

public class FirstLoadDataWizardPage extends WizardPage {

	private Map<String, ConnectionInfo> info = new HashMap<String, ConnectionInfo>();

	private Combo comProjectName;
	private Text txtConnectionInfo;

	public FirstLoadDataWizardPage(String pageName) {
		super(pageName);
		setPageComplete(false);
	}

	public void init() {
		for (IProject p : getAuroraProjects()) {
			DBManager db = new DBManager(p);
			Connection con = null;
			try {
				con = db.getConnection();
				DatabaseMetaData metaData = con.getMetaData();
				comProjectName.add(p.getName());
				info.put(p.getName(), new ConnectionInfo(p, metaData.getUserName(), metaData.getURL(), metaData.getDriverName(), metaData.getDriverVersion()));
			} catch (SQLException e) {
				continue;
			} catch (ApplicationException e) {
				continue;
			} finally {
				try {
					if (null != con) {
						con.close();
					}
				} catch (SQLException e) {
//					setPageComplete(false);
//					DialogUtil.showExceptionMessageBox(e);
//					return;
				}
			}
		}
	}

	public void createControl(Composite parent) {
		setTitle("载入数据");
		setDescription("选择连接");
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout gridLayout = new GridLayout(2, false);
		container.setLayout(gridLayout);
		// ------------------------------------------------------------------
		Label lalProjectName = new Label(container, SWT.NONE);
		lalProjectName.setText("工程名：");

		comProjectName = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData gdProjectName = new GridData(GridData.FILL_HORIZONTAL);
		comProjectName.setLayoutData(gdProjectName);
		comProjectName.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				fillInfo(comProjectName.getText());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});
		// ------------------------------------------------------------------
		Label lalConnectionInfo = new Label(container, SWT.NONE);
		GridData gdLabelConnectionInfo = new GridData();
		gdLabelConnectionInfo.verticalAlignment = SWT.TOP;
		lalConnectionInfo.setText("连接信息：");
		lalConnectionInfo.setLayoutData(gdLabelConnectionInfo);

		GridData gdConnectionInfo = new GridData(GridData.FILL_BOTH);
		txtConnectionInfo = new Text(container, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.READ_ONLY);
		txtConnectionInfo.setLayoutData(gdConnectionInfo);

		setControl(container);

	}

	private void fillInfo(String projectName) {
		final ConnectionInfo cInfo = info.get(projectName);
		String s = "User：" + cInfo.getUserName() + "\nURL：" + cInfo.getURL() + "\nDriver：" + cInfo.getDriverName() + " " + cInfo.getDriverVersion();
		txtConnectionInfo.setText(s);
		setPageComplete(true);
		final SecondLoadDataWizardPage page = ((SecondLoadDataWizardPage) getNextPage());
		page.clear();
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				page.init(cInfo.getProject());
			}
		});
	}

	private IProject[] getAuroraProjects() {
		List<IProject> projects = new ArrayList<IProject>();
		for (IProject p : AuroraPlugin.getWorkspace().getRoot().getProjects()) {
			if (isAuroraProject(p)) {
				projects.add(p);
			}
		}
		return projects.toArray(new IProject[projects.size()]);
	}

	private boolean isAuroraProject(IProject project) {
		try {
			for (String nature : project.getDescription().getNatureIds()) {
				if (AuroraProjectNature.ID.equals(nature)) {
					return true;
				}
			}
		} catch (CoreException e) {
			return false;
		}
		return false;
	}
}

class ConnectionInfo {
	IProject project;
	String userName;
	String URL;
	String driverName;
	String driverVersion;

	public ConnectionInfo(IProject project, String userName, String URL, String driverName, String driverVersion) {
		this.project = project;
		this.userName = userName;
		this.URL = URL;
		this.driverName = driverName;
		this.driverVersion = driverVersion;
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getDriverVersion() {
		return driverVersion;
	}

	public void setDriverVersion(String driverVersion) {
		this.driverVersion = driverVersion;
	}
}