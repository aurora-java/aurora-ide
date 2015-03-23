package aurora.ide.statistics.viewer;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import aurora.ide.api.statistics.Statistician;
import aurora.ide.api.statistics.map.StatisticsResult;
import aurora.ide.api.statistics.model.StatisticsProject;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.DBManager;

public class LoadFromDBJob extends Job {

	private StatisticsProject statisticsProject;
	private IProject project;
	private StatisticsView statisticsView;

	public LoadFromDBJob(IProject project, StatisticsProject statisticsProject, StatisticsView statisticsView) {
		super("从数据库加载数据");
		this.project = project;
		this.statisticsProject = statisticsProject;
		this.statisticsView = statisticsView;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		DBManager dm = new DBManager(project);
		Connection connection = null;
		monitor.beginTask("数据读取中....", 80);
		try {
			monitor.worked(10);
			monitor.setTaskName("获取数据库连接");
			connection = dm.getConnection();
			Statistician s = new Statistician(statisticsProject, null, ObjectDependency.getInstance());
			monitor.worked(20);
			monitor.setTaskName("读取统计数据");
			StatisticsResult read = s.read(connection);
			monitor.worked(40);
			statisticsView.setInput(read, s);
			statisticsView.setSaveToXLSActionEnabled(true);
		} catch (ApplicationException e) {
			showMessage(e.getMessage());
		} catch (SQLException e) {
			// java.sql.SQLException: ORA-00942: 表或视图不存在
			if (e.getMessage().startsWith("ORA-00942"))
				showMessage("表或视图不存在");
			e.printStackTrace();
		} finally {
			try {
				if (null != connection && (!connection.isClosed())) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return Status.OK_STATUS;
	}

	private void showMessage(final String message) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessageDialog.openInformation(Display.getDefault().getActiveShell(), "统计分析", message);
			}
		});
	}
}
