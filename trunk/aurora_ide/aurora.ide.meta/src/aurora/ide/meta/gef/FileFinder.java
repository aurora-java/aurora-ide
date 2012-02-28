package aurora.ide.meta.gef;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

public class FileFinder implements IResourceVisitor {

	public FileFinder() {
	}

	private List<IResource> result = new ArrayList<IResource>();

	public boolean visit(IResource resource) throws CoreException {
		if (resource.getType() == IResource.FILE) {
			boolean checkExtension = checkExtension(resource);
			if (checkExtension) {
				result.add(resource);
			}
			return false;
		}
		return true;
	}

	public List<IResource> getResult() {
		return result;
	}

	private boolean checkExtension(IResource resource) {
		IFile file = (IFile) resource;
		String fileExtension = file.getFileExtension();
		return "uip".equalsIgnoreCase(fileExtension);
	}
}
