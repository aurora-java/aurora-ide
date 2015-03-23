package aurora.ide.statistics.wizard.dialog;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import aurora.ide.api.statistics.DatabaseAction;
import aurora.ide.api.statistics.model.StatisticsProject;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.DBManager;
import aurora.ide.helpers.DialogUtil;

public class SecondLoadDataWizardPage extends WizardPage {

	private StatisticsProject[] projects;
	private Combo cboProjectName;
	private Text txtStorer;
	private Text txtStorerDate;
	private Text txtRepositoryType;
	private Text txtRepositoryRevesion;
	private Text txtRepositoryPath;

	private StatisticsProject statisticsProject;
	private IProject project;

	public SecondLoadDataWizardPage(String pageName) {
		super(pageName);
		setPageComplete(false);
	}

	public StatisticsProject getStatisticsProject() {
		return statisticsProject;
	}

	public IProject getProject() {
		return project;
	}

	public void init(IProject project) {
		this.project = project;
		Connection con = null;
		try {
			DBManager db = new DBManager(project);
			con = db.getConnection();
			projects = DatabaseAction.readAllProject(con);
		} catch (SQLException e) {
			DialogUtil.logErrorException(e);
			return;
		} catch (ApplicationException e) {
			DialogUtil.showExceptionMessageBox(e);
			return;
		} finally {
			try {
				if (null != con) {
					con.close();
				}
			} catch (SQLException e) {
				DialogUtil.logErrorException(e);
				return;
			}
		}
		for (int i = 0; i < projects.length; i++) {
			for (int j = i + 1; j < projects.length; j++) {
				int si = Integer.parseInt(projects[i].getProjectId());
				int sj = Integer.parseInt(projects[j].getProjectId());
				if (si < sj) {
					StatisticsProject temp = projects[i];
					projects[i] = projects[j];
					projects[j] = temp;
				}
			}
			String s = projects[i].getProjectId() + ":" + projects[i].getProjectName();
			cboProjectName.add(s);
			cboProjectName.setData(s, projects[i]);
		}
	}

	private void fillInfo(StatisticsProject sProject) {
		txtStorer.setText(sProject.getStorer());
		txtStorerDate.setText(sProject.getStoreDate());
		txtRepositoryType.setText(sProject.getRepositoryType());
		txtRepositoryRevesion.setText(sProject.getRepositoryRevision());
		txtRepositoryPath.setText(sProject.getRepositoryPath());
		this.statisticsProject = sProject;
		setPageComplete(true);
	}

	public void clear() {
		cboProjectName.removeAll();
		txtStorer.setText("");
		txtStorerDate.setText("");
		txtRepositoryType.setText("");
		txtRepositoryRevesion.setText("");
		txtRepositoryPath.setText("");
		setPageComplete(false);
	}

	public void createControl(Composite parent) {
		setTitle("载入数据");
		setDescription("选择项目");
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		// ---------------------project name----------------------------------
		Label lblProjectName = new Label(container, SWT.NULL);
		lblProjectName.setText("项目名：");

		GridData gbProjectName = new GridData(GridData.FILL_HORIZONTAL);
		cboProjectName = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
		cboProjectName.setLayoutData(gbProjectName);
		cboProjectName.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				Combo bo = (Combo) e.getSource();
				fillInfo((StatisticsProject) bo.getData(bo.getText()));
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});
		// ---------------------repository type------------------------------
		Label lblRepositoryType = new Label(container, SWT.NULL);
		lblRepositoryType.setText("资源库类型：");

		GridData gdRepositoryType = new GridData(GridData.FILL_HORIZONTAL);
		txtRepositoryType = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtRepositoryType.setLayoutData(gdRepositoryType);
		// ---------------------repository revesion--------------------------
		Label lblRepositoryRevesion = new Label(container, SWT.NULL);
		lblRepositoryRevesion.setText("资源库版本：");

		GridData gdRepositoryRevesion = new GridData(GridData.FILL_HORIZONTAL);
		txtRepositoryRevesion = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtRepositoryRevesion.setLayoutData(gdRepositoryRevesion);
		// ---------------------repository path------------------------------
		Label lblRepositoryPath = new Label(container, SWT.NULL);
		lblRepositoryPath.setText("资源库路径：");

		GridData gdRepositoryPath = new GridData(GridData.FILL_HORIZONTAL);
		txtRepositoryPath = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtRepositoryPath.setLayoutData(gdRepositoryPath);
		// ---------------------storer---------------------------------------
		Label lblStorer = new Label(container, SWT.NULL);
		lblStorer.setText("保存人：");

		GridData gdStorer = new GridData(GridData.FILL_HORIZONTAL);
		txtStorer = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtStorer.setLayoutData(gdStorer);
		// ---------------------store date-----------------------------------
		Label lblStorerDate = new Label(container, SWT.NULL);
		lblStorerDate.setText("保存时间：");

		GridData gdStorerDate = new GridData(GridData.FILL_HORIZONTAL);
		txtStorerDate = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtStorerDate.setLayoutData(gdStorerDate);

		setControl(container);
	}

}
