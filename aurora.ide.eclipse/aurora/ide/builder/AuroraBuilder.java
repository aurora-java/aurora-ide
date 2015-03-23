package aurora.ide.builder;

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
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.widgets.Display;

import aurora.ide.builder.validator.BmValidator;
import aurora.ide.builder.validator.ScreenValidator;
import aurora.ide.builder.validator.SvcValidator;
import aurora.ide.builder.validator.UncertainLocalValidator;
import aurora.ide.project.propertypage.ProjectPropertyPage;
import aurora.ide.search.core.Message;
import aurora.ide.search.ui.MessageFormater;

public class AuroraBuilder extends IncrementalProjectBuilder {
	private int filecount = 0;
	private IPath webPath;
	private ArrayList<IResource> filesToBuild = new ArrayList<IResource>();

	class SampleDeltaVisitor implements IResourceDeltaVisitor {
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				filesToBuild.add(resource);
				break;
			case IResourceDelta.REMOVED:
				break;
			case IResourceDelta.CHANGED:
				filesToBuild.add(resource);
				break;
			}
			// return true to continue visiting children.
			return true;
		}
	}

	class SampleResourceVisitor implements IResourceVisitor {
		public boolean visit(IResource resource) {
			filesToBuild.add(resource);
			// return true to continue visiting children.
			return true;
		}
	}

	public static final String BUILDER_ID = "aurora.ide.auroraBuilder";

	public static final String UNDEFINED_ATTRIBUTE = "aurora.ide.undefinedAttribute";
	public static final String UNDEFINED_BM = "aurora.ide.undefinedBM";
	public static final String UNDEFINED_DATASET = "aurora.ide.undefinedDataSet";
	public static final String UNDEFINED_FOREIGNFIELD = "aurora.ide.undefinedForeignField";
	public static final String UNDEFINED_LOCALFIELD = "aurora.ide.undefinedLocalField";
	public static final String UNDEFINED_SCREEN = "aurora.ide.undefinedScreen";
	public static final String NONENAMESPACE = "aurora.ide.nonenamespace";
	public static final String CONFIG_PROBLEM = "aurora.ide.configProblem";
	public static final String UNDEFINED_TAG = "aurora.ide.undefinedTag";
	public static final String FATAL_ERROR = "aurora.ide.fatalError";

	private static String[] definedMarkerTypes = { UNDEFINED_ATTRIBUTE,
			UNDEFINED_BM, UNDEFINED_DATASET, UNDEFINED_FOREIGNFIELD,
			UNDEFINED_LOCALFIELD, UNDEFINED_SCREEN, NONENAMESPACE,
			CONFIG_PROBLEM, UNDEFINED_TAG, FATAL_ERROR };

	public static IMarker addMarker(IFile file, String message, int lineNumber,
			int severity, String markerType) {
		try {
			IMarker marker = file.createMarker(markerType);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
			return marker;
		} catch (CoreException e) {
		}

		return null;
	}

	public static IMarker addMarker(IFile file, String message, int line,
			int start, int length, int severity, String markerType) {
		try {
			IMarker marker = file.createMarker(markerType);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			marker.setAttribute(IMarker.LINE_NUMBER, line);
			marker.setAttribute(IMarker.CHAR_START, start);
			marker.setAttribute(IMarker.CHAR_END, start + length);
			return marker;
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected IResource fCurrentResource;
	protected int fNumberOfScannedFiles;
	private Job updateMonitorJob;

	public static IMarker addMarker(IFile file, String msg, int lineno,
			IRegion region, int sevrity, String markerType) {
		if (region == null) {
			return addMarker(file, msg, lineno, sevrity, markerType);
		} else {
			return addMarker(file, msg, lineno, region.getOffset(),
					region.getLength(), sevrity, markerType);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 * java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IProject[] build(int kind,
			@SuppressWarnings("rawtypes") Map args, IProgressMonitor monitor)
			throws CoreException {
		BuildContext.initBuildLevel();
		updateMonitorJob = createJob(monitor);
		fNumberOfScannedFiles = 0;
		filesToBuild.clear();
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				return null;
				// fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	private Job createJob(final IProgressMonitor monitor) {
		return new Job("Aurora build progress") {

			private int fLastNumberOfScannedFiles = 0;

			public IStatus run(final IProgressMonitor inner) {
				while (!inner.isCanceled()) {
					final IResource res = fCurrentResource;
					if (res != null) {
						if (!isRunInUI()) {
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									updateMonitor(monitor, res);
								}
							});
						} else {
							updateMonitor(monitor, res);
						}
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						return Status.OK_STATUS;
					}
				}
				return Status.OK_STATUS;
			}

			private boolean isRunInUI() {
				return true;
			}

			private void updateMonitor(final IProgressMonitor monitor,
					final IResource res) {
				String fileName = res.getName();
				final Object[] args = { fileName, fNumberOfScannedFiles,
						filecount };
				monitor.subTask(MessageFormater.format(Message._scanning, args));
				int steps = fNumberOfScannedFiles - fLastNumberOfScannedFiles;
				monitor.worked(steps);
				fLastNumberOfScannedFiles += steps;
			}
		};
	}

	public static void deleteMarkers(IFile file) {
		try {
			for (String s : definedMarkerTypes)
				file.deleteMarkers(s, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

	protected void fullBuild(final IProgressMonitor monitor)
			throws CoreException {
		try {
			if (!checkWebDir())
				return;
			getProject().accept(new ResourceCountVisitor());
			monitor.beginTask("builder " + getProject().getName(), filecount);
			updateMonitorJob.setSystem(true);
			updateMonitorJob.schedule();
			getProject().accept(new SampleResourceVisitor());
			for (IResource res : filesToBuild) {
				if (monitor.isCanceled()) {
					break;
				}
				validate(res);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		} finally {
			updateMonitorJob.cancel();
			monitor.done();
		}
	}

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		if (!checkWebDir())
			return;
		delta.accept(new ResourceCountVisitor());
		monitor.beginTask("builder " + getProject().getName(), filecount);
		updateMonitorJob.setSystem(true);
		updateMonitorJob.schedule();
		try {
			delta.accept(new SampleDeltaVisitor());
			for (IResource res : filesToBuild) {
				if (monitor.isCanceled()) {
					break;
				}
				validate(res);
			}
		} finally {
			updateMonitorJob.cancel();
			monitor.done();
		}
	}

	private boolean checkWebDir() throws CoreException {
		IProject project = getProject();
		project.deleteMarkers(CONFIG_PROBLEM, false, IResource.DEPTH_ZERO);
		String webdir = project
				.getPersistentProperty(ProjectPropertyPage.WebQN);
		if (webdir == null) {
			ArrayList<IFolder> als = ResourceUtil.findAllWebInf(project);
			for (IFolder f : als)
				f.deleteMarkers(CONFIG_PROBLEM, false, 0);
			if (als.size() > 1) {
				IMarker marker = als.get(1).createMarker(CONFIG_PROBLEM);
				marker.setAttribute(IMarker.MESSAGE,
						BuildMessages.get("build.webinfo.muliti"));
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				return false;
			} else if (als.size() == 0) {
				IMarker marker = project.createMarker(CONFIG_PROBLEM);
				marker.setAttribute(IMarker.MESSAGE,
						BuildMessages.get("build.web.notexists"));
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				// Display.getDefault().asyncExec(new Runnable() {
				// public void run() {
				// MessageBox mb = new MessageBox(new Shell(), SWT.ERROR);
				// mb.setText(BuildMessages.get("build.error.prompt"));
				// mb.setMessage(BuildMessages.get("build.web.notexists"));
				// mb.open();
				// }
				// });
				return false;
			} else {
				IFolder webinf = als.get(0);
				webPath = webinf.getParent().getFullPath();
				project.setPersistentProperty(ProjectPropertyPage.WebQN,
						webPath.toString());
				project.setPersistentProperty(ProjectPropertyPage.BMQN, webinf
						.getFolder("classes").getFullPath().toString());
			}
		} else
			webPath = new Path(webdir);
		return true;
	}

	private void validate(IResource resource) {
		fCurrentResource = resource;
		fNumberOfScannedFiles++;
		if (!webPath.isPrefixOf(resource.getFullPath()))
			return;
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			validateFile(file);
		}
	}

	private void validateFile(IFile file) {
		deleteMarkers(file);
		String ext = file.getFileExtension();
		if (ext != null)
			ext = ext.toLowerCase();
		if (file.getName().equalsIgnoreCase("uncertain.local.xml")) {
			new UncertainLocalValidator(file).validate();
		} else if ("bm".equals(ext)) {
			new BmValidator(file).validate();
		} else if ("svc".equals(ext)) {
			new SvcValidator(file).validate();
		} else if ("screen".equals(ext)) {
			new ScreenValidator(file).validate();
		} else if ("config".equals(ext)) {
		}
	}

	public static String[] getDefinedMarkerTypes() {
		return definedMarkerTypes;
	}

	private class ResourceCountVisitor implements IResourceDeltaVisitor,
			IResourceVisitor {

		public ResourceCountVisitor() {
			filecount = 0;
		}

		public boolean visit(IResource resource) throws CoreException {
			filecount++;
			return true;
		}

		public boolean visit(IResourceDelta delta) throws CoreException {
			filecount++;
			return true;
		}
	}
}
