package aurora.ide.refactoring;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import aurora.ide.search.core.Util;

public class FolderRefactorParticipant {

	private IFolder currentFolder;
	private ScopeVisitor visitor;

	private boolean isBMFolder = false;

	public FolderRefactorParticipant() {
	}

	public boolean initialize(Object element) {
		// if nature in
		if (element instanceof IFolder) {
			currentFolder = (IFolder) element;
			isBMFolder = false;
			visitor = new ScopeVisitor();
			return isTakeIn(currentFolder);
		}
		return false;
	}

	public boolean isTakeIn(IFolder currentFolder) {
		IContainer webInf = Util.findWebInf(currentFolder);
		if (webInf == null) {
			return false;
		}
		IPath webInfPath = webInf.getProjectRelativePath();
		// TODO get properties classes path
		IPath classesPath = webInfPath.append("classes");
		IPath webPath = webInf.getParent().getProjectRelativePath();
		IPath deletePath = currentFolder.getProjectRelativePath();
		boolean webPathPrefixOf = webPath.isPrefixOf(deletePath);
		boolean webInfPathPrefixOf = webInfPath.isPrefixOf(deletePath);
		boolean classesPathPrefixOf = classesPath.isPrefixOf(deletePath);
		if (classesPathPrefixOf) {
			// bm folder delete
			isBMFolder = true;
			try {
				currentFolder.accept(visitor);
			} catch (CoreException e) {
				return false;
			}
			return true;
		}

		if (!webInfPathPrefixOf && webPathPrefixOf) {
			// screen or svc folder delete
			isBMFolder = false;
			try {
				currentFolder.accept(visitor);
			} catch (CoreException e) {
				return false;
			}
			return true;
		}

		return false;
	}

	public String getName() {
		return "Folder Refactor Participant";
	}

	private class ScopeVisitor implements IResourceVisitor {
		private List<IFile> result = new ArrayList<IFile>();

		public boolean visit(IResource resource) throws CoreException {
			if (resource.getType() == IResource.FILE) {
				boolean checkExtension = checkExtension(resource);
				if (checkExtension) {
					result.add((IFile) resource);
				}
				return false;
			}
			return true;
		}

		public List<IFile> getResult() {
			return result;
		}

		private boolean checkExtension(IResource resource) {
			String fileExtension = resource.getFileExtension();
			return isBMFolder ? "bm".equalsIgnoreCase(fileExtension) : "screen"
					.equalsIgnoreCase(fileExtension)
					|| "svc".equalsIgnoreCase(fileExtension);
		}
	}

	public List<IFile> getFiles() {
		return visitor.getResult();
	}

	public boolean isBMFolder() {
		return isBMFolder;
	}

	public IFolder getCurrentFolder() {
		return currentFolder;
	}
}
