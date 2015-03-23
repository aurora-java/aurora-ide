package aurora.ide.navigator;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;

import aurora.ide.AuroraPlugin;
import aurora.ide.AuroraProjectNature;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.ProjectUtil;

public class BMFileContentProvider implements ITreeContentProvider,
		IResourceChangeListener, IResourceDeltaVisitor {
	private StructuredViewer viewer;
	private static final Object[] NO_CHILD = new Object[0];

	public BMFileContentProvider() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this,
				IResourceChangeEvent.POST_CHANGE);
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IContainer) {
			try {
				Object[] bmFiles = getBMFilesFromResources(((IContainer) parentElement)
						.members());
				return bmFiles;
			} catch (CoreException e) {
				DialogUtil.logErrorException(e);
				return NO_CHILD;
			} catch (ApplicationException e) {
				// 错误信息不明确，
				DialogUtil.logErrorException(e);
				return NO_CHILD;
			}
		}
		BMFile bmLinkFile = null;
		try {
			bmLinkFile = getBMLinkFile(parentElement);
		} catch (ApplicationException e) {
			DialogUtil.showExceptionMessageBox(e);
			return NO_CHILD;
		}
		if (bmLinkFile == null)
			return NO_CHILD;
		return bmLinkFile.getSubBMFiles().toArray();
	}

	public Object getParent(Object element) {
		if (element instanceof BMFile) {
			BMFile file = (BMFile) element;
			try {
				IPath parentPath = file.getParentBMPath();
				if (parentPath != null)
					return getBMLinkFile(parentPath);
				else {
					return ResourcesPlugin.getWorkspace().getRoot()
							.findMember(parentPath);
				}
			} catch (ApplicationException e) {
				DialogUtil.showExceptionMessageBox(e);
				return null;
			}
		}
		if (element instanceof IResource) {
			return ((IResource) element).getParent();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof IContainer) {
			try {
				return ((IContainer) element).members().length > 0;
			} catch (CoreException e) {
				DialogUtil.logErrorException(e);
				return false;
			}
		}
		BMFile bmLinkFile = null;
		try {
			bmLinkFile = getBMLinkFile(element);
		} catch (ApplicationException e) {
			DialogUtil.showExceptionMessageBox(e);
			return false;
		}
		if (bmLinkFile == null)
			return false;
		return bmLinkFile.getSubBMFiles().size() > 0;
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof IContainer) {
			try {
				return ((IContainer) inputElement).members();
			} catch (CoreException e) {
				DialogUtil.logErrorException(e);
				return NO_CHILD;
			}
		}
		BMFile bmLinkFile = null;
		try {
			bmLinkFile = getBMLinkFile(inputElement);
		} catch (ApplicationException e) {
			DialogUtil.showExceptionMessageBox(e);
			return null;
		}
		if (bmLinkFile == null)
			return NO_CHILD;
		return bmLinkFile.getSubBMFiles().toArray();

	}

	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

	public void inputChanged(Viewer aViewer, Object oldInput, Object newInput) {
		viewer = (StructuredViewer) aViewer;
	}

	public void resourceChanged(IResourceChangeEvent event) {
		try {
			IResourceDelta delta = event.getDelta();
			delta.accept(this);
		} catch (CoreException e) {
			DialogUtil.logErrorException(e);
		}
	}

	public boolean visit(IResourceDelta delta) throws CoreException {
//		IProject project = ProjectUtil.getIProjectFromSelection();
		IProject project  = delta.getResource().getProject();
		if (project == null)
			return false;
		if (!AuroraProjectNature.hasAuroraNature(project)) {
			return false;
		}
		try {
			IResourceDelta bms = delta.findMember(new Path(ProjectUtil
					.getBMHome(project)));
			if (bms == null)
				return false;
			handleBMFileDelta(bms);
		} catch (ApplicationException e) {
			DialogUtil.showExceptionMessageBox(e);
		}
		viewer.getControl().getDisplay().asyncExec(new Runnable() {
			public void run() {
				viewer.refresh();
			}
		});
		return false;
	}

	private void handleBMFileDelta(IResourceDelta delta) {
		if (delta == null)
			return;
		IResourceDelta[] childs = delta.getAffectedChildren();
		if (childs == null || childs.length == 0) {
			IResource resource = delta.getResource();
			if ((resource instanceof IFile)
					&& AuroraConstant.BMFileExtension.equalsIgnoreCase(resource
							.getFileExtension())) {
				try {
					if (IResourceDelta.REMOVED == delta.getKind()) {
						BMHierarchyCache.getInstance().removeBMFile(
								delta.getResource());
					} else if (IResourceDelta.ADDED == delta.getKind()) {
						BMHierarchyCache.getInstance().addBMFile(
								delta.getResource());
					} else if (IResourceDelta.CHANGED == delta.getKind()) {
						BMHierarchyCache.getInstance().updateBMFile(
								delta.getResource());
					}
				} catch (ApplicationException e) {
					// DialogUtil.showExceptionMessageBox(e);
					DialogUtil.logErrorException(e);
				}
			}
			return;
		}
		for (int i = 0; i < childs.length; i++) {
			handleBMFileDelta(childs[i]);
		}
	}

	private Object[] getBMFilesFromResources(IResource[] resources)
			throws ApplicationException {
		List fileList = new LinkedList();
		BMHierarchyViewerTester test = new BMHierarchyViewerTester();
		for (int i = 0; i < resources.length; i++) {
			IResource child = resources[i];
			if (!test.test(child, null, null, null)) {
				fileList.add(resources[i]);
				continue;
			}
			try {

				BMFile bmFile = searchBMLinkFile(child);
				if (bmFile != null) {
					fileList.add(bmFile);
				} else {
					fileList.add(child);
				}
			} catch (ApplicationException e) {
				// DialogUtil.showErrorMessageBox(child.getFullPath().toString());
				throw e;
			}

		}

		return fileList.toArray();
	}

	private BMFile getBMLinkFile(Object file) throws ApplicationException {
		if (file instanceof IFile) {
			IFile resource = (IFile) file;
			return searchBMLinkFile(resource);
		} else if (file instanceof BMFile) {
			IResource resource = ResourcesPlugin.getWorkspace().getRoot()
					.findMember(((BMFile) file).getPath());
			return searchBMLinkFile(resource);
		} else if (file instanceof IPath) {
			IResource resource = ResourcesPlugin.getWorkspace().getRoot()
					.findMember((IPath) file);
			return searchBMLinkFile(resource);
		} else if (file instanceof IResource) {
			return searchBMLinkFile((IResource) file);
		} else {
			throw new ApplicationException("请检查对象是" + "IFile或者BMFile类型!");
		}
	}

	public static BMFile searchBMLinkFile(final IResource resource)
			throws ApplicationException {

		if (resource == null || !resource.exists())
			return null;
		if (!BMHierarchyCache.getInstance().isCached(resource.getProject())) {
			try {
				AuroraPlugin.getDefault().getWorkbench().getProgressService()
						.busyCursorWhile(new IRunnableWithProgress() {

							public void run(IProgressMonitor monitor)
									throws InvocationTargetException,
									InterruptedException {
								try {
									BMHierarchyCache.getInstance().initCache(
											resource.getProject());
								} catch (ApplicationException e) {
									throw new InvocationTargetException(e);
								}
							}
						});
			} catch (InvocationTargetException e) {
				throw new ApplicationException("", e);
			} catch (InterruptedException e) {
			}
		}
		Map ifileMap = BMHierarchyCache.getInstance().getBMFileMapNotNull(
				resource.getProject());
		Object obj = ifileMap.get(resource.getFullPath());
		if (obj == null) {
			return null;
		}
		return (BMFile) obj;
	}
}
