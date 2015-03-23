package aurora.ide.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;

import aurora.datasource.DatabaseConnection;
import aurora.ide.AuroraPlugin;
import aurora.ide.AuroraProjectNature;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.StringUtil;
import aurora.ide.project.propertypage.ProjectPropertyPage;

public class AuroraProject {
	private IProject project;

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

	public IFolder getWeb_inf() {
		IContainer web_home = getWeb_home();
		if (StringUtil.isBlank(web_home)) {
			return null;
		}
		return web_home.getFolder(new Path("WEB-INF"));
	}

	public IContainer getWeb_home() {
		String web = "";
		try {
			web = project.getPersistentProperty(ProjectPropertyPage.WebQN);
		} catch (CoreException e) {
		}
		if (StringUtil.isBlank(web)) {
			return null;
		}
		Path path = new Path(web);
		if (path.segmentCount() == 1) {
			return project;
		}
		IFolder webHome = project.getParent().getFolder(path);
		return webHome;
	}

	public IFolder getWeb_classes() {
		String bm = "";
		try {
			bm = project.getPersistentProperty(ProjectPropertyPage.BMQN);
		} catch (CoreException e) {
		}
		if (StringUtil.isBlank(bm)) {
			return null;
		}
		Path path = new Path(bm);
		if (path.segmentCount() == 1) {
			return null;
		}
		IFolder bmHome = project.getParent().getFolder(new Path(bm));
		return bmHome;
	}

	public void setBMHome(String bmHome) throws CoreException {
		project.setPersistentProperty(ProjectPropertyPage.BMQN, bmHome);
	}

	public void setWebHome(String webHome) throws CoreException {
		project.setPersistentProperty(ProjectPropertyPage.WebQN, webHome);
	}

	public void setMainPage(String page) throws CoreException {
		project.setPersistentProperty(ProjectPropertyPage.LoclaUrlHomeQN, page);
	}

	public AuroraProject(IProject project) {
		super();
		this.project = project;
	}

	public String openFolderSelectionDialog(String msg,
			org.eclipse.swt.widgets.Shell shell) {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				shell, new WorkbenchLabelProvider(),
				new BaseWorkbenchContentProvider());
		dialog.setMessage(msg);
		dialog.setInput(project);
		dialog.addFilter(new ViewerFilter() {
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {

				if (element instanceof IContainer) {
					if (((IContainer) element).getName().startsWith(".") == false) {
						return true;
					}
				}
				return false;
			}
		});
		int open = dialog.open();
		if (ElementTreeSelectionDialog.OK == open) {
			Object firstResult = dialog.getFirstResult();
			if (firstResult instanceof IResource) {
				return ((IResource) firstResult).getFullPath().toString();
			}
		}
		return null;
	}

	public static IProject openProjectSelectionDialog(
			org.eclipse.swt.widgets.Shell shell) {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				shell, new WorkbenchLabelProvider(),
				new BaseWorkbenchContentProvider());
		dialog.setMessage("Aurora Project ");
		dialog.setInput(AuroraPlugin.getWorkspace().getRoot());
		dialog.addFilter(new ViewerFilter() {
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {

				if (element instanceof IProject) {
					try {
						if (AuroraProjectNature
								.hasAuroraNature((IProject) element))
							return true;
					} catch (CoreException e) {
					}
				}
				return false;
			}
		});
		int open = dialog.open();
		if (ElementTreeSelectionDialog.OK == open) {
			Object firstResult = dialog.getFirstResult();
			if (firstResult instanceof IProject) {
				return ((IProject) firstResult);
			}
		}
		return null;
	}

	public static IPath openFolderSelectionDialog(
			org.eclipse.swt.widgets.Shell shell) {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				shell, new WorkbenchLabelProvider(),
				new BaseWorkbenchContentProvider());
		dialog.setMessage("Aurora Project ");
		dialog.setInput(AuroraPlugin.getWorkspace().getRoot());
		dialog.addFilter(new ViewerFilter() {
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {

				if (element instanceof IProject) {
					try {
						if (AuroraProjectNature
								.hasAuroraNature((IProject) element))
							return true;
					} catch (CoreException e) {
					}
				}
				if (element instanceof IFolder) {
					if (((IContainer) element).getName().startsWith(".") == false) {
						return true;
					}
				}
				return false;
			}
		});
		int open = dialog.open();
		if (ElementTreeSelectionDialog.OK == open) {
			Object firstResult = dialog.getFirstResult();
			if (firstResult instanceof IResource) {
				return (((IResource) firstResult).getFullPath());
			}
		}
		return null;
	}

	public DatabaseConnection getDefaultDatasourceConfig() {
		List<DatabaseConnection> datasouceConfig = getDatasouceConfig();
		for (DatabaseConnection dc : datasouceConfig) {
			if (dc.getName() == null)
				return dc;
		}
		return null;
	}

	public List<DatabaseConnection> getDatasouceConfig() {

		IFolder web_inf = this.getWeb_inf();
		if(web_inf == null)
			return new ArrayList<DatabaseConnection>();
		File configDirectory = web_inf.getLocation().toFile();
		File config = new File(configDirectory,
				"/aurora.database/datasource.config");
		if (config.exists() == false) {
			config = new File(configDirectory, "0.datasource.config");
		}
		final List<DatabaseConnection> dss = new ArrayList<DatabaseConnection>();
		if (config.exists() == false) {
			return dss;
		}
		CompositeMap loadFile = CompositeMapUtil.loadFile(config);
		loadFile.iterate(new IterationHandle() {
			public int process(CompositeMap map) {
				if ("database-connection".equalsIgnoreCase(map.getName())) {
					DatabaseConnection dc = new DatabaseConnection();
					dc.setName(CompositeMapUtil.getValueIgnoreCase(map, "name"));
					dc.setDriverClass(CompositeMapUtil.getValueIgnoreCase(map,
							"driverClass"));
					dc.setUrl(CompositeMapUtil.getValueIgnoreCase(map, "url"));
					dc.setUserName(CompositeMapUtil.getValueIgnoreCase(map,
							"userName"));
					dc.setPassword(CompositeMapUtil.getValueIgnoreCase(map,
							"password"));
					dss.add(dc);
				}
				return IterationHandle.IT_CONTINUE;
			}
		}, true);
		return dss;

	}
}
