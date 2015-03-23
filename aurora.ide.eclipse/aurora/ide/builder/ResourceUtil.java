package aurora.ide.builder;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import aurora.ide.AuroraMetaProjectNature;
import aurora.ide.AuroraProjectNature;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.project.propertypage.ProjectPropertyPage;

public final class ResourceUtil {
	/**
	 * find the bm file via its class path
	 * 
	 * @param proj
	 *            the project
	 * @param clsPath
	 *            model path of bm
	 * @return if any exception occurred , returns null
	 */
	public static final IFile getBMFile(IProject proj, String clsPath) {
		if (clsPath == null || proj == null)
			return null;
		String path = clsPath.replace('.', Path.SEPARATOR) + '.'
				+ AuroraConstant.BMFileExtension;
		String bmhome = getBMHome(proj);
		if (bmhome.length() == 0)
			return null;
		String fullPath = bmhome + Path.SEPARATOR + path;
		IResource res = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(fullPath);
		if (res instanceof IFile)
			return (IFile) res;
		return null;
	}

	/**
	 * get a file under web home<br/>
	 * use {@link #getWebHome(IProject)} to get the web home , if success then
	 * append path...
	 * 
	 * @param path
	 *            if webhome is <u>/AA/bb/web</u><br/>
	 *            and the file`s full path is <u>/AA/bb/web/cc/ff.txt</u><br>
	 *            then the path should be <u>cc/ff.txt</u>
	 * @return
	 */
	public static final IFile getFileUnderWebHome(IProject proj, String path) {
		String webHome = getWebHome(proj);
		if (webHome.length() == 0)
			return null;
		String fullPath = webHome + Path.SEPARATOR + path;
		IResource res = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(fullPath);
		if (res instanceof IFile)
			return (IFile) res;
		return null;
	}

	public static final String getBMHome(IProject proj) {
		IFolder folder = getBMHomeFolder(proj);
		if (folder == null)
			return "";
		return folder.getFullPath().toString();
	}

	/**
	 * if a project is a aurora project (has aurora nature),try to find its
	 * bmhome<br/>
	 * 1st. read its projectPropertyPage<br/>
	 * 2nd. try to find WEB-INF (then getFolder 'classes')
	 * 
	 * @param proj
	 * @return if any exception occurred , returns null
	 */
	public static final IFolder getBMHomeFolder(IProject proj) {
		if (!isAuroraProject(proj))
			return null;
		String bmHome = null;
		try {
			bmHome = proj.getPersistentProperty(ProjectPropertyPage.BMQN);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		if (bmHome == null || bmHome.length() == 0) {
			IFolder wiFolder = searchWebInf(proj);
			if (wiFolder == null)
				return null;
			return wiFolder.getFolder("classes");
		}
		return ResourcesPlugin.getWorkspace().getRoot()
				.getFolder(new Path(bmHome));
	}

	/**
	 * get pkg name(class path) of bm file<br/>
	 * if the file is not under <b>bmhome</b>, returns ""<br/>
	 * if the file is null or not in a aurora project ,still return ""
	 * 
	 * @param bmFile
	 * @return if not success ,return ""
	 */
	public static String getBmPkgPath(IFile bmFile) {
		if (bmFile == null)
			return "";
		IProject project = bmFile.getProject();
		IFolder bmhome = getBMHomeFolder(project);
		if (bmhome == null)
			return "";
		IPath bmhomepath = bmhome.getFullPath();
		IPath bmfilepath = bmFile.getFullPath();
		if (!bmhomepath.isPrefixOf(bmfilepath))
			return "";
		IPath path = bmfilepath.makeRelativeTo(bmhomepath);
		path = path.removeFileExtension();
		return path.toString().replace(Path.SEPARATOR, '.');
	}

	public static final String getWebHome(IProject proj) {
		if (!isAuroraProject(proj))
			return "";
		String webHome = null;
		try {
			webHome = proj.getPersistentProperty(ProjectPropertyPage.WebQN);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		if (webHome == null || webHome.length() == 0) {
			IFolder wiFolder = searchWebInf(proj);
			if (wiFolder == null)
				return "";
			return wiFolder.getParent().getFullPath().toString();
		}
		return webHome;
	}

	/**
	 * get a aurora project`s webhome<br/>
	 * 1st. try to read projectProperty<br/>
	 * 2nd. use {@link #searchWebInf(IResource)} and getParent
	 * 
	 * 
	 * @param proj
	 * @return id any exception occurred , returns null
	 */
	public static final IFolder getWebHomeFolder(IProject proj) {
		if (!isAuroraProject(proj))
			return null;
		String webHome = null;
		try {
			webHome = proj.getPersistentProperty(ProjectPropertyPage.WebQN);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		if (webHome == null || webHome.length() == 0) {
			IFolder wiFolder = searchWebInf(proj);
			if (wiFolder == null)
				return null;
			return (IFolder) wiFolder.getParent();
		}
		return ResourcesPlugin.getWorkspace().getRoot()
				.getFolder(new Path(webHome));
	}

	/**
	 * test weather a project is a aurora project(not null and has aurora
	 * nature)
	 * 
	 * @param proj
	 * @return
	 */
	public static final boolean isAuroraProject(IProject proj) {
		if (proj == null || !proj.exists() || !proj.isOpen())
			return false;
		try {
			return AuroraProjectNature.hasAuroraNature(proj);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static final boolean isAuroraMetaProject(IProject proj) {
		if (proj == null || !proj.exists() || !proj.isOpen())
			return false;
		try {
			return AuroraMetaProjectNature.hasAuroraNature(proj);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * just use <i>accept</i> to search the project (of resource) WEB-INF
	 * folder,<br/>
	 * not care weather the project is a aurora project
	 * 
	 * @param resource
	 * @return if any exception occurred , returns null
	 */
	public static final IFolder searchWebInf(IResource resource) {
		if (resource == null)
			return null;
		IProject proj = resource.getProject();
		final IFolder[] results = { null };
		try {
			proj.accept(new IResourceVisitor() {

				public boolean visit(IResource res) throws CoreException {
					if (res.getName().equals("WEB-INF")) {
						results[0] = (IFolder) res;
						return false;
					}
					return true;
				}
			}, IResource.DEPTH_INFINITE, IResource.FOLDER);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return results[0];
	}

	/**
	 * method to get the web-inf folder<br/>
	 * use {@link #getWebHome(IProject)} to get webhome ,if success then append
	 * WEB-INF...
	 * 
	 * @param resource
	 * @return if any exception occurred , returns null
	 */
	public static final IFolder getWebInf(IResource resource) {
		if (resource == null)
			return null;
		String webHome = getWebHome(resource.getProject());
		if (webHome.length() == 0)
			return null;
		IResource res = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(webHome + Path.SEPARATOR + "WEB-INF");
		if (res instanceof IFolder)
			return (IFolder) res;
		return null;
	}

	public static final ArrayList<IFolder> findAllWebInf(IProject proj) {
		final ArrayList<IFolder> als = new ArrayList<IFolder>();
		try {
			proj.accept(new IResourceVisitor() {

				public boolean visit(IResource resource) throws CoreException {
					if ("WEB-INF".equals(resource.getName()))
						als.add((IFolder) resource);
					return true;
				}
			}, IResource.DEPTH_INFINITE, IResource.FOLDER);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return als;
	}
}
