package aurora.ide.meta.builder;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.designer.IDesignerConst;
import aurora.ide.meta.gef.designer.gen.BaseBmGenerator;
import aurora.ide.meta.project.AuroraMetaProject;

public class AutoGenBuilder extends IncrementalProjectBuilder {
	private IFolder mpFolder;

	class SampleDeltaVisitor implements IResourceDeltaVisitor {
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				// buildMeta(resource);
				break;
			case IResourceDelta.REMOVED:
				break;
			case IResourceDelta.CHANGED:
				buildMeta(resource);
				break;
			}
			return true;
		}
	}

	class SampleResourceVisitor implements IResourceVisitor {
		public boolean visit(IResource resource) {
			buildMeta(resource);
			return true;
		}
	}

	public static final String BUILDER_ID = "aurora.ide.meta.autoGenBuilder";

	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		AuroraMetaProject amp = new AuroraMetaProject(getProject());
		try {
			mpFolder = amp.getModelFolder();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		}
		if (mpFolder == null)
			return null;
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	void buildMeta(IResource resource) {
		if (canBuild(resource)) {
			IFile file = (IFile) resource;
			System.out.println("building : " + file);
			try {
				new BaseBmGenerator(file).process();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private boolean canBuild(IResource res) {
		if (!(res instanceof IFile))
			return false;
		if (!mpFolder.getFullPath().isPrefixOf(res.getFullPath()))
			return false;
		if (!res.getName().toLowerCase()
				.endsWith("." + IDesignerConst.EXTENSION))
			return false;
		return true;
	}

	protected void fullBuild(final IProgressMonitor monitor)
			throws CoreException {
		try {
			getProject().accept(new SampleResourceVisitor());
		} catch (CoreException e) {
		}
	}

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		delta.accept(new SampleDeltaVisitor());
	}
}
