package aurora.ide.helpers;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import aurora.ide.bm.BMUtil;
import aurora.ide.project.propertypage.ProjectPropertyPage;

public class PathUtil {

	public static final IPath REQUEST_PATH = new Path(
			"${/request/@context_path}");
	public static final IPath CRUD_PATH = REQUEST_PATH.append("/autocrud/");

	public static final List<String> AURORA_FILE_EXTENSION = Arrays.asList(
			"bm", "screen", "svc");

	public static final List<String> AURORA_CONFIG_FILE_EXTENSION = Arrays
			.asList("xml", "proc", "config");

	/**
	 * 
	 * @param file
	 *            : 参照文件
	 * @param value
	 *            : 文件中使用包含地址信息的合理值
	 * 
	 * @return screen,svc,bm 文件
	 */

	public static IFile findFile(IFile file, String value) {
		IFile screenFile = findScreenFile(file, value);
		if (screenFile != null) {
			return screenFile;
		} else {
			IPath path = new Path(value);
			boolean prefixOfRequest = CRUD_PATH.isPrefixOf(path);
			if (prefixOfRequest) {
				path = path.makeRelativeTo(CRUD_PATH);
			} else if (path.segmentCount() == 4
					&& "autocrud".equals(path.segment(1))) {
				path = path.removeFirstSegments(2);
			}
			String pkg = path.segment(0);
			if (pkg != null) {
				String[] split = pkg.split("\\?");
				if (split == null || split.length == 0)
					return null;
				pkg = split[0];
				try {
					IResource bm = BMUtil.getBMResourceFromClassPath(
							file.getProject(), pkg);
					if (bm instanceof IFile && bm.exists()) {
						return (IFile) bm;
					}
				} catch (ApplicationException e) {
					DialogUtil.logErrorException(e);
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	private static class WebInfFinder implements IResourceVisitor {
		private IFolder folder = null;

		public boolean visit(IResource resource) throws CoreException {
			if (folder != null)
				return false;
			if (resource.getType() == IResource.FOLDER) {
				if ("WEB-INF".equals(resource.getName())) {
					folder = (IFolder) resource;
					return false;
				}
				return true;
			}
			if (resource.getType() == IResource.FILE) {
				return false;
			}

			return true;
		}

		public IFolder getFolder() {
			return folder;
		}

	}

	public static IContainer findWebInf(IResource resource) {
		if (null == resource) {
			return null;
		}
		IFolder webINF = null;
		IProject project = resource.getProject();
		try {
			String web = project
					.getPersistentProperty(ProjectPropertyPage.WebQN);
			if (web != null) {
				IPath webINFPath = new Path(web).append("WEB-INF");
				webINF = project.getParent().getFolder(webINFPath);
			}
		} catch (CoreException e1) {

		}
		if (webINF != null && webINF.exists()) {
			return webINF;
		}
		try {
			WebInfFinder finder = new WebInfFinder();
			project.accept(finder);
			return finder.getFolder();
		} catch (CoreException e) {
		}
		return null;
	}

	public static IContainer findBMHome(IResource resource) {
		if (null == resource) {
			return null;
		}
		IFolder bmHome = null;
		IProject project = resource.getProject();
		try {
			String bm = project.getPersistentProperty(ProjectPropertyPage.BMQN);
			IPath bmPath = new Path(bm);
			bmHome = project.getParent().getFolder(bmPath);
		} catch (CoreException e1) {

		}
		if (bmHome != null && bmHome.exists()) {
			return bmHome;
		}
		IContainer webINF = findWebInf(resource);
		if (webINF != null && webINF.exists()) {
			IFolder classes = webINF.getFolder(new Path("classes"));
			return classes.exists() ? classes : null;
		}

		return null;
	}

	public static IFile findBMFileByPKG(Object pkg) {
		try {
			IResource file = BMUtil.getBMResourceFromClassPath((String) pkg);
			if (file instanceof IFile
					&& "bm".equalsIgnoreCase(file.getFileExtension()))
				return (IFile) file;
		} catch (ApplicationException e) {

		}
		return null;
	}

	public static IFile findScreenFile(IFile file, Object pkg) {
		if (pkg instanceof String) {
			IContainer webInf = findWebInf(file);
			if (webInf == null)
				return null;
			IResource webRoot = webInf.getParent();
			IContainer parent = file.getParent();
			IPath parentPath = parent.getFullPath();
			IPath rootPath = webRoot.getFullPath();
			IPath path = new Path((String) pkg);
			boolean prefixOfRequest = REQUEST_PATH.isPrefixOf(path);
			if (prefixOfRequest) {
				path = path.makeRelativeTo(REQUEST_PATH);
			}
			String[] split = path.toString().split("\\?");
			if (split == null || split.length == 0)
				return null;
			path = new Path(split[0]);
			// path.segmentCount() < ICoreConstants.MINIMUM_FILE_SEGMENT_LENGTH
			if (path.segmentCount() == 0) {
				return null;
			}
			IPath relativePath = parentPath.makeRelativeTo(rootPath);
			boolean prefixOf = relativePath.isPrefixOf(path);
			if (prefixOf || prefixOfRequest) {
				// fullpath
				IPath sourceFilePath = rootPath.append(path);
				if (sourceFilePath.segmentCount() < 2)
					return null;
				IFile sourceFile = file.getProject().getParent()
						.getFile(sourceFilePath);
				if (sourceFile.exists())
					return sourceFile;

			} else {
				// relativepath
				IFile sourceFile = parent.getFile(path);
				if (sourceFile.exists())
					return sourceFile;
			}

			// fullpath
			IPath sourceFilePath = rootPath.append(path);
			if (sourceFilePath.segmentCount() < 2)
				return null;
			IFile sourceFile = file.getProject().getParent()
					.getFile(sourceFilePath);
			if (sourceFile.exists())
				return sourceFile;

		}
		return null;
	}

	public static String findScreenUrl(IFile file, Object pkg) {
		if (pkg instanceof String) {

			IPath path = new Path((String) pkg);
			IPath requestPath = new Path("${/request/@context_path}");
			boolean prefixOfRequest = requestPath.isPrefixOf(path);
			if (prefixOfRequest) {
				path = path.makeRelativeTo(requestPath);
			}
			String[] split = path.toString().split("\\?");
			if (split == null || split.length == 0)
				return "";
			path = new Path(split[0]);
			// path.segmentCount() < ICoreConstants.MINIMUM_FILE_SEGMENT_LENGTH
			if (path.segmentCount() == 0) {
				return "";
			}
			return path.toString();
		}
		return "";
	}

	public static String toBMPKG(IFile file) {
		IPath path = file.getProjectRelativePath().removeFileExtension();
		return toPKG(path);
	}

	// 如果不属于classes，将会返回 "web.WEB-INF.a"
	public static String toPKG(IPath path) {
		String[] segments = path.segments();
		StringBuilder result = new StringBuilder();
		StringBuilder _result = new StringBuilder();
		int classes_idx = -1;
		for (int i = 0; i < segments.length; i++) {
			_result.append(segments[i]);
			if (i != segments.length - 1)
				_result.append(".");
			if (classes_idx != -1) {
				result.append(segments[i]);
				if (i != segments.length - 1)
					result.append(".");
			}
			if ("classes".equals(segments[i])) {
				classes_idx = i;
			}
		}
		if (result.length() == 0) {
			result = _result;
		}
		return result.toString();
	}

	// 如果不属于classes，将会返回""
	public static String toRelativeClassesPKG(IPath path) {
		String[] segments = path.segments();
		StringBuilder result = new StringBuilder();
		int classes_idx = -1;
		for (int i = 0; i < segments.length; i++) {
			if (classes_idx != -1) {
				result.append(segments[i]);
				if (i != segments.length - 1)
					result.append(".");
			}
			if ("classes".equals(segments[i])) {
				classes_idx = i;
			}
		}
		return result.toString();
	}

	public static String getPKG(IPath path) {
		String fileExtension = path.getFileExtension();
		if ("bm".equalsIgnoreCase(fileExtension)) {
			return toPKG(path.removeFileExtension());
		}
		if ("screen".equalsIgnoreCase(fileExtension)) {
			return path.toString();
		}
		if ("svc".equalsIgnoreCase(fileExtension)) {
			return path.toString();
		}
		return "";
	}

	public static String getPathInScreen(IFile file) {
		IProject project = file.getProject();
		String webHome = "";
		try {
			webHome = ProjectUtil.getWebHome(project);
		} catch (ApplicationException e) {
			DialogUtil.logErrorException(e);
			e.printStackTrace();
		}
		Path webPath = new Path(webHome);
		IPath fullPath = file.getFullPath();
		IPath makeRelativeTo = fullPath.makeRelativeTo(webPath);
		return getPKG(makeRelativeTo);
	}

	public static boolean isAuroraFile(IFile file) {
		String fileExtension = getFileExtension(file);
		return isExists(file)
				&& AURORA_FILE_EXTENSION.contains(fileExtension.toLowerCase());
	}

	public static String getFileExtension(IFile file) {
		String fileExtension = file.getFileExtension() == null ? "" : file
				.getFileExtension();
		return fileExtension;
	}

	private static boolean isExists(IFile file) {
		return file != null && file.exists();
	}

	public static boolean isBMFile(IFile file) {
		String fileExtension = getFileExtension(file);
		return isExists(file) && "bm".equals(fileExtension.toLowerCase());
	}

	public static boolean isConfigFile(IFile file) {
		String fileExtension = getFileExtension(file);
		return isExists(file)
				&& AURORA_CONFIG_FILE_EXTENSION.contains(fileExtension
						.toLowerCase());
	}
}
