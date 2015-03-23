package aurora.ide.helpers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

import aurora.ide.AuroraProjectNature;
import aurora.ide.project.propertypage.ProjectPropertyPage;

public class ProjectUtil {

	public static List<IProject> getALLAuroraProjects() {
		List<IProject> r = new ArrayList<IProject>();

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject[] projects = workspace.getRoot().getProjects();
		for (IProject p : projects) {
			try {
				boolean hasAuroraNature = AuroraProjectNature
						.hasAuroraNature(p);
				if (hasAuroraNature)
					r.add(p);
			} catch (CoreException e) {
			}
		}
		return r;
	}

	public static boolean isDebugMode(IProject project) {
		if (project == null)
			return false;
		try {
			if ("true".equals(project
					.getPersistentProperty(ProjectPropertyPage.DebugModeQN)))
				return true;
		} catch (CoreException e) {
			DialogUtil.logErrorException(e);
		}
		return false;
	}

	public static String getWebHome(IProject project)
			throws ApplicationException {
		if (project == null)
			throw new ApplicationException("project参数是空！");
		String webHome = null;
		try {
			webHome = project.getPersistentProperty(ProjectPropertyPage.WebQN);

			if (ProjectPropertyPage.filtEmpty(webHome) == null) {
				webHome = ProjectUtil.autoGetWebHome(project);
				if (ProjectPropertyPage.filtEmpty(webHome) != null) {
					return webHome;
				}
				if (ProjectUtil.openProjectPropertyPage(project) == Window.OK) {
					webHome = project
							.getPersistentProperty(ProjectPropertyPage.WebQN);
				}
			}
		} catch (CoreException e) {
			throw new SystemException(e);
		}
		return webHome;
	}

	public static String getBMHome(IProject project)
			throws ApplicationException {
		if (project == null)
			throw new ApplicationException("project参数是空！");
		String bmHome = null;
		try {
			bmHome = project.getPersistentProperty(ProjectPropertyPage.BMQN);
			if (ProjectPropertyPage.filtEmpty(bmHome) == null) {
				bmHome = ProjectUtil.autoGetBMHome(project);
				if (ProjectPropertyPage.filtEmpty(bmHome) != null) {
					return bmHome;
				}
				if (ProjectUtil.openProjectPropertyPage(project) == Window.OK) {
					bmHome = project
							.getPersistentProperty(ProjectPropertyPage.BMQN);
				}
			}
		} catch (CoreException e) {
			throw new SystemException(e);
		}
		if (ProjectPropertyPage.filtEmpty(bmHome) == null) {
			throw new ApplicationException("设置BM目录失败！");
		}
		return bmHome;
	}

	public static String getWebHomeLocalPath(IProject project)
			throws ApplicationException {
		String webBaseDir = getWebHome(project);
		if (ProjectPropertyPage.filtEmpty(webBaseDir) == null)
			throw new ApplicationException("获取Web目录失败！");
		IPath path = new Path(webBaseDir);
		return AuroraResourceUtil.getLocalPathFromIPath(path);
	}

	public static int openProjectPropertyPage(IProject project) {
		PreferenceDialog propertyDialog = PreferencesUtil
				.createPropertyDialogOn(Display.getCurrent().getActiveShell(),
						project, ProjectPropertyPage.PropertyId,
						new String[] { ProjectPropertyPage.PropertyId }, null);
		return propertyDialog.open();
	}

	public static String getBMHomeLocalPath(IProject project)
			throws ApplicationException {
		String bmBaseDir = getBMHome(project);
		if (filteEmptyString(bmBaseDir) == null)
			throw new ApplicationException("获取BM目录失败！");
		IPath path = new Path(bmBaseDir);
		return AuroraResourceUtil.getLocalPathFromIPath(path);
	}

	public static String getLocalWebUrl(IProject project)
			throws ApplicationException {
		if (project == null)
			throw new ApplicationException("project参数是空！");
		String localWebUrl = null;
		try {
			localWebUrl = project
					.getPersistentProperty(ProjectPropertyPage.LoclaUrlHomeQN);
			if (ProjectPropertyPage.filtEmpty(localWebUrl) == null) {
				if (ProjectUtil.openProjectPropertyPage(project) == Window.OK) {
					localWebUrl = project
							.getPersistentProperty(ProjectPropertyPage.LoclaUrlHomeQN);
				}
			}
		} catch (CoreException e) {
			throw new SystemException(e);
		}
		if (ProjectPropertyPage.filtEmpty(localWebUrl) == null) {
			throw new ApplicationException("没设置本机服务器主页面");
		}
		return localWebUrl;
	}

	public static String filteEmptyString(String str) {
		if ("".equals(str))
			return null;
		return str;
	}

	public static String autoGetWebHome(IProject project)
			throws SystemException {
		IResource webInf = AuroraResourceUtil.getResource(project, "web-inf");
		if (webInf == null)
			return null;
		else {
			IResource webDir = webInf.getParent();
			if (webDir.exists()) {
				String errorMessage = ProjectPropertyPage.validWebHome(project,
						webDir.getFullPath());
				if (errorMessage == null) {
					String webDirPath = webDir.getFullPath().toString();
					try {
						project.setPersistentProperty(
								ProjectPropertyPage.WebQN, webDirPath);
					} catch (CoreException e) {
						throw new SystemException(e);
					}
					return webDirPath;
				}
			}
		}
		return null;
	}

	public static String autoGetBMHome(IProject project) throws SystemException {
		IResource classesDir = AuroraResourceUtil.getResource(project,
				"classes");
		if (classesDir == null)
			return null;
		else {
			if (classesDir.exists()) {
				String errorMessage = ProjectPropertyPage.validBMHome(project,
						classesDir.getFullPath());
				if (errorMessage == null) {
					String classesDirPath = classesDir.getFullPath().toString();
					try {
						project.setPersistentProperty(ProjectPropertyPage.BMQN,
								classesDirPath);
					} catch (CoreException e) {
						throw new SystemException(e);
					}
					return classesDirPath;
				}
			}
		}
		return null;
	}

	public static String autoGetLocalWebUrl(IProject project)
			throws ApplicationException {
		String defaultUrl = "http://127.0.0.1:8080/" + project.getName();
		try {
			project.setPersistentProperty(ProjectPropertyPage.LoclaUrlHomeQN,
					defaultUrl);
		} catch (CoreException e) {
			throw new SystemException(e);
		}
		return defaultUrl;
	}

	public static IProject getIProjectFromSelection() {
		IResource selection = AuroraResourceUtil.getIResourceSelection();
		if (selection != null)
			return selection.getProject();
		return null;
	}

	public static IProject getIProjectFromActiveEditor() {
		IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (activeWorkbenchWindow == null) {
			activeWorkbenchWindow = PlatformUI.getWorkbench()
					.getWorkbenchWindows()[0];
		}
		IEditorInput input = activeWorkbenchWindow.getActivePage()
				.getActiveEditor().getEditorInput();
		if ((input instanceof IFileEditorInput) == false)
			return null;
		IFile ifile = ((IFileEditorInput) input).getFile();
		IProject project = ifile.getProject();
		return project;
	}

	public static List getBMSFromProject(IProject project)
			throws ApplicationException {
		List bmList = new LinkedList();
		if (project == null)
			return bmList;
		String bmHome = getBMHome(project);
		if (bmHome == null)
			return bmList;
		IResource bmRoot = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(bmHome);
		if (bmRoot == null || !bmRoot.exists()
				|| (!(bmRoot instanceof IContainer)))
			return bmList;
		IContainer parent = (IContainer) bmRoot;
		AuroraResourceUtil.iteratorResource(parent, bmList);
		return bmList;
	}
}
