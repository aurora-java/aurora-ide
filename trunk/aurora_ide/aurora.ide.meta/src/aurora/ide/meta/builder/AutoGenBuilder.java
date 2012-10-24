package aurora.ide.meta.builder;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import aurora.ide.helpers.StatusUtil;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.designer.IDesignerConst;
import aurora.ide.meta.gef.designer.gen.BaseBmGenerator;
import aurora.ide.meta.project.AuroraMetaProject;

public class AutoGenBuilder extends IncrementalProjectBuilder {
	public static final String MARKER_BUILD_ERROR = "aurora.ide.meta.builderror";
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
			deleteMarkers(resource);
			IFile file = (IFile) resource;
			try {
				new BaseBmGenerator(file).process();
			} catch (Exception e) {
				createMarker(resource, MARKER_BUILD_ERROR, e);
			}
		}
	}

	public static IMarker createMarker(IResource res, String marker_id,
			Exception e) {
		IMarker m = null;
		try {
			m = res.createMarker(marker_id);
			m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			m.setAttribute(IMarker.MESSAGE, e.getMessage());
			m.setAttribute(IMarker.LINE_NUMBER, 0);
		} catch (CoreException e1) {
			StatusUtil.showExceptionDialog(null, null,
					"Error occured while create markers on " + res.getName(),
					false, e1);
		}
		return m;
	}

	public static void deleteMarkers(IResource res) {
		try {
			res.deleteMarkers(MARKER_BUILD_ERROR, false, 0);
		} catch (CoreException e) {
			StatusUtil.showExceptionDialog(null, null,
					"Error occured while delete markers on " + res.getName(),
					false, e);
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
			StatusUtil.showExceptionDialog(null, null,
					"Error occurred during AutoGenBuild.", false, e);
		}
	}

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		delta.accept(new SampleDeltaVisitor());
	}
}
