package aurora.ide.statistics.viewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;

import uncertain.composite.CompositeMap;
import aurora.ide.AuroraPlugin;
import aurora.ide.api.statistics.IRunningListener;
import aurora.ide.api.statistics.RepositoryInfo;
import aurora.ide.api.statistics.Statistician;
import aurora.ide.api.statistics.cvs.CVSEntryLineTag;
import aurora.ide.api.statistics.cvs.CVSRepositoryLocation;
import aurora.ide.api.statistics.cvs.CVSTag;
import aurora.ide.api.statistics.cvs.FolderSyncInfo;
import aurora.ide.api.statistics.map.PreferencesTag;
import aurora.ide.api.statistics.map.StatisticsResult;
import aurora.ide.api.statistics.model.ProjectObject;
import aurora.ide.api.statistics.model.StatisticsProject;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.LoadSchemaManager;
import aurora.ide.search.cache.CacheManager;
import aurora.ide.search.core.AuroraFileFinder;
import aurora.ide.search.core.Message;
import aurora.ide.search.ui.MessageFormater;
import aurora.ide.statistics.repo.CVSFileReader;

public class StatisticianRunner implements IRunningListener {
	private StatisticsView statisticsView;
	private int fNumberOfFilesToScan;
	private String fCurrentFileName;
	private int fNumberOfScannedFiles;
	private IProgressMonitor monitor;
	private boolean isStatistician = false;

	public StatisticianRunner(StatisticsView statisticsView) {
		this.statisticsView = statisticsView;
	}

	public void noProjectRun(final Object[] objects) {
		Job job = new Job("Aurora Statistician Progress") {

			@Override
			public IStatus run(IProgressMonitor monitor) {
				StatisticianRunner.this.monitor = monitor;
				Statistician st = noProjectStatistician();
				privateRunning(st, objects, monitor);
				statisticsView.setSaveToXLSActionEnabled(true);
				return Status.OK_STATUS;
			}

		};
		job.setUser(true);
		job.schedule();
	}

	private void privateRunning(Statistician st, Object[] objects, final IProgressMonitor monitor) {

		fNumberOfFilesToScan = objects.length;
		Job monitorUpdateJob = new Job("Aurora Statistician Progress") {
			private int fLastNumberOfScannedFiles = 0;

			public IStatus run(final IProgressMonitor inner) {
				while (!inner.isCanceled()) {
					if (fCurrentFileName != null) {
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								updateMonitor(monitor);
							}
						});
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						return Status.OK_STATUS;
					}
				}
				return Status.OK_STATUS;
			}

			private void updateMonitor(IProgressMonitor monitor) {
				final Object[] args = { fCurrentFileName, new Integer(fNumberOfScannedFiles), new Integer(fNumberOfFilesToScan) };
				if (isStatistician) {
					monitor.subTask("分析 :" + MessageFormater.format(Message._scanning, args));
				} else {
					monitor.subTask("统计 :" + MessageFormater.format(Message._scanning, args));
				}
				int steps = fNumberOfScannedFiles - fLastNumberOfScannedFiles;
				if (steps < 0) {
					fLastNumberOfScannedFiles = 0;
					steps = fNumberOfScannedFiles - fLastNumberOfScannedFiles;
				}
				monitor.worked(steps);
				fLastNumberOfScannedFiles += steps;
			}
		};

		monitor.beginTask("文件统计分析", fNumberOfFilesToScan * 2);
		monitorUpdateJob.setSystem(true);
		monitorUpdateJob.schedule();
		try {
			setPreferencesTag();
			st.addRuningListener(this);
			Statistician statistician = fillStatistician(st, objects, monitor);
			isStatistician = true;
			StatisticsResult doStatistic = statistician.doStatistic();
			statisticsView.setInput(doStatistic, statistician);
		} finally {
			isStatistician = false;
			monitorUpdateJob.cancel();
			monitor.done();
		}
	}

	public Statistician fillStatistician(Statistician st, Object[] objects, IProgressMonitor monitor) {
		int i = 1;
		for (Object o : objects) {
			if (monitor.isCanceled())
				return st;
			if (o instanceof IFile) {
				this.fCurrentFileName = ((IFile) o).getName();
				this.fNumberOfScannedFiles = i;
				ProjectObject po = createProjectObject((IFile) o);
				if (po != null) {
					st.addProjectObject(po);
				}
			}
			i++;
		}
		return st;
	}

	private void setPreferencesTag() {
		IPreferenceStore store = AuroraPlugin.getDefault().getPreferenceStore();
		if (store.getDefaultString("statistician.checked").trim().equals("")) {
			StringBuffer defaultStore = new StringBuffer();
			Map<String, List<String>> defaultMap = PreferencesTag.INSTANCE().getDefaultMap();
			for (String n : defaultMap.keySet()) {
				defaultStore.append("*");
				defaultStore.append(n);
				defaultStore.append("!");
				for (String t : defaultMap.get(n)) {
					defaultStore.append(t);
					defaultStore.append("!");
				}
			}
			store.setDefault("statistician.checked", defaultStore.toString());
			store.setValue("statistician.checked", defaultStore.toString());
		}
		String[] ss = store.getString("statistician.checked").split("!");
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		String nameSpace = "";
		ArrayList<String> list = null;
		for (String s : ss) {
			if ("".equals(s.trim())) {
				continue;
			} else if (s.indexOf("*") == 0) {
				if (null != list) {
					map.put(nameSpace, list);
				}
				nameSpace = s.substring(1);
				list = new ArrayList<String>();
			} else {
				list.add(s.substring((s.indexOf(":") > 0 ? (s.indexOf(":") + 1) : 0)));
			}
		}
		map.put(nameSpace, list);
		PreferencesTag.INSTANCE().setNamespaceMap(map);
	}

	public ProjectObject createProjectObject(IFile file) {
		String type = getProjectObjectType(file);
		if (type.equals(ProjectObject.UNSUPPORT))
			return null;
		ProjectObject po = new ProjectObject();
		po.setType(type);
		po.setName(file.getName());
		po.setPath(new Path(file.getProject().getName()).append(file.getProjectRelativePath()).toString());
		try {
			CompositeMap compositeMap = CacheManager.getCompositeMap(file);
			po.setRootMap(compositeMap);
			return po;
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getProjectObjectType(IFile file) {
		String fileExtension = file.getFileExtension();
		return ProjectObject.getType(fileExtension);
	}

	private Statistician noProjectStatistician() {
		return new Statistician(StatisticsProject.NONE_PROJECT, LoadSchemaManager.getSchemaManager(), ObjectDependency.getInstance());
	}

	public boolean notice(ProjectObject po, int poIndex) {
		this.fCurrentFileName = po.getName();
		this.fNumberOfScannedFiles = poIndex + 1;
		if (monitor != null && monitor.isCanceled()) {
			return false;
		}
		return true;
	}

	public void projectRun(final Object[] objects) {
		Job job = new Job("Aurora Statistician Progress") {

			@Override
			public IStatus run(IProgressMonitor monitor) {
				StatisticianRunner.this.monitor = monitor;
				Statistician st = projectStatistician(objects[0]);

				if (st != null) {

					AuroraFileFinder finder = new AuroraFileFinder();
					try {
						((IProject) objects[0]).accept(finder);
						List<IResource> result = finder.getResult();
						privateRunning(st, result.toArray(new IResource[result.size()]), monitor);
						statisticsView.setSaveToDBActionEnabled(true);
						statisticsView.setSaveToXLSActionEnabled(true);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();

	}

	protected Statistician projectStatistician(Object object) {
		if (object instanceof IProject) {
			IProject project = (IProject) object;
			StatisticsProject sp = new StatisticsProject(project.getName(), project.getName());
			RepositoryInfo repInfo = createCVSInfo(project);

			if (repInfo == null) {
				repInfo = this.createSVNInfo(project);
			}

			if (repInfo != null) {
				sp.setRepositoryPath(repInfo.getRepoPath());
				sp.setRepositoryRevision(repInfo.getRevision());
				sp.setRepositoryType(repInfo.getType());
				sp.setStorer(repInfo.getUserName());
			}
			return new Statistician(sp, LoadSchemaManager.getSchemaManager(), ObjectDependency.getInstance());
		}
		return this.noProjectStatistician();
	}

	private RepositoryInfo createCVSInfo(IProject project) {
		FolderSyncInfo readFolderSync = CVSFileReader.readFolderSync(project);
		if (readFolderSync == null)
			return null;
		String repository = readFolderSync.getRepository();
		String root = readFolderSync.getRoot();
		String projectCVSPath = root + FolderSyncInfo.SERVER_SEPARATOR + repository;

		CVSEntryLineTag tag = readFolderSync.getTag();
		String tagLabel = CVSTag.getTagLabel(tag);

		CVSRepositoryLocation location = new CVSRepositoryLocation(projectCVSPath);
		RepositoryInfo info = new RepositoryInfo(projectCVSPath, tagLabel);
		info.setUserName(location.getUserName());
		info.setType(RepositoryInfo.CVS);
		return info;
	}

	private RepositoryInfo createSVNInfo(IProject project) {
		File file = project.getFolder(".svn").getFile("entries").getLocation().toFile();
		try {
			FileInputStream fis = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis), 512);

			String revision = "";
			String url = "";
			for (int i = 0; i < 5; i++) {
				String line = reader.readLine();
				if (line == null)
					return null;
				else if (i == 3) {
					// must be a integer
					Integer.valueOf(line);
					revision = line;
				} else if (i == 4) {
					url = line;
				}
			}
			RepositoryInfo repositoryInfo = new RepositoryInfo(url, revision);
			repositoryInfo.setType(RepositoryInfo.SVN);
			repositoryInfo.setUserName("");
			return repositoryInfo;
		} catch (FileNotFoundException e) {
		} catch (NumberFormatException nfe) {
		} catch (IOException e) {
		}
		return null;
	}

}
