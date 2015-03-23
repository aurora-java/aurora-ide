package aurora.ide.search.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

public class AuroraFileFinder implements IResourceVisitor {
	

	public AuroraFileFinder() {
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
		return "bm".equalsIgnoreCase(fileExtension)
				|| "screen".equalsIgnoreCase(fileExtension)
				|| "svc".equalsIgnoreCase(fileExtension);
	}
}