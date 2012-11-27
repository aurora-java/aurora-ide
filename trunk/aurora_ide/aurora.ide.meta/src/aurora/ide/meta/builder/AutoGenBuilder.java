package aurora.ide.meta.builder;

import java.util.ArrayList;
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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

import aurora.ide.helpers.StatusUtil;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.designer.IDesignerConst;
import aurora.ide.meta.gef.designer.gen.BaseBmGenerator;
import aurora.ide.meta.project.AuroraMetaProject;

public class AutoGenBuilder extends IncrementalProjectBuilder {
	public static final String MARKER_BUILD_ERROR = "aurora.ide.meta.builderror";
	private ArrayList<IResource> filesToBuild = new ArrayList<IResource>();
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
				filesToBuild.add(resource);
				break;
			}
			return true;
		}
	}

	class SampleResourceVisitor implements IResourceVisitor {
		public boolean visit(IResource resource) {
			filesToBuild.add(resource);
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
			try {
				deleteMarkers(resource);
				IFile file = (IFile) resource;
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
			filesToBuild.clear();
			getProject().accept(new SampleResourceVisitor());
			buildFiles(monitor);
		} catch (CoreException e) {
			StatusUtil.showExceptionDialog(null, null,
					"Error occurred during AutoGenBuild.", false, e);
		}
	}

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		filesToBuild.clear();
		delta.accept(new SampleDeltaVisitor());
		buildFiles(monitor);
	}

	private void buildFiles(final IProgressMonitor monitor) {
		monitor.beginTask("build " + getProject().getName(),
				filesToBuild.size());
		final Object[] status = { 0, 0, null };
		Job job = new Job("update status") {

			@Override
			protected IStatus run(IProgressMonitor innerMonitor) {
				while (!monitor.isCanceled()) {
					IResource res = (IResource) status[2];
					if (res != null) {
						updateStatus(res);
					}
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						return Status.OK_STATUS;
					}
				}
				return Status.OK_STATUS;
			}

			private void updateStatus(final IResource res) {
				Display.getDefault().asyncExec(new Runnable() {

					public void run() {
						status[1] = (Integer) status[1] + (Integer) status[0];
						monitor.subTask("build " + status[1] + " of "
								+ filesToBuild.size() + " "
								+ res.getFullPath().toString());
						monitor.worked((Integer) status[0]);
						status[0] = 0;
					}
				});
			}

		};
		job.setSystem(true);
		job.schedule(1);
		for (IResource res : filesToBuild) {
			if (monitor.isCanceled()) {
				break;
			}
			status[0] = ((Integer) status[0]) + 1;
			status[2] = res;
			buildMeta(res);
		}
		job.cancel();
		monitor.done();
	}
}
