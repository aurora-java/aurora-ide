package aurora.ide.statistics.viewer;

import java.sql.Connection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import aurora.ide.AuroraPlugin;
import aurora.ide.api.statistics.DatabaseAction;
import aurora.ide.api.statistics.Statistician;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.DBManager;

public class SaveToDBJob extends Job {

	private Statistician statistician;
	private StatisticsView statisticsView;

	public SaveToDBJob(Statistician statistician,StatisticsView statisticsView) {
		super("StatisticsViewSaveToDB");
		this.statistician = statistician;
		this.statisticsView = statisticsView;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		IProject project = AuroraPlugin.getWorkspace().getRoot().getProject(statistician.getProject().getEclipseProjectName());
		if (!project.exists()) {
			showMessage("工程不存在，无法保存");
			return Status.CANCEL_STATUS;
		}

		monitor.beginTask("数据保存中....", 80);
		try {
			monitor.worked(10);
			monitor.setTaskName("获取数据库连接");
			DBManager dm = new DBManager(project);
			Connection connection = dm.getConnection();
			monitor.worked(20);

			monitor.setTaskName("保存统计数据");

			aurora.ide.api.statistics.Status save = statistician.save(connection);
			if (save.getStatus() == aurora.ide.api.statistics.Status.ERROR) {
				monitor.worked(10);
				monitor.setTaskName("数据保存失败 ：" + save.getMessage());

				if (save.getMessage().startsWith("ORA-02289:")) {

					monitor.setTaskName("创建保存需要的表");
					connection = dm.getConnection();
					DatabaseAction.createTables(connection);
					monitor.worked(10);
					monitor.setTaskName("保存统计数据");

					save = statistician.save(connection);
				}
			}
			monitor.worked(20);
			monitor.setTaskName("数据保存成功");
			statisticsView.setSaveToDBActionEnabled(true);
			statisticsView.setSaveToXLSActionEnabled(true);
			return Status.OK_STATUS;
		} catch (ApplicationException e) {
			showMessage(e.getMessage());
		} finally {
			monitor.done();
		}
		return Status.CANCEL_STATUS;
	}

	private void showMessage(final String message) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessageDialog.openInformation(Display.getDefault().getActiveShell(), "统计分析", message);
			}
		});
	}
}
