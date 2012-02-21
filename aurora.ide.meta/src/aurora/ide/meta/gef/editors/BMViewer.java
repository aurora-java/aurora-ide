package aurora.ide.meta.gef.editors;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.util.DelegatingDragAdapter;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.editors.dnd.BMTransfer;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.project.AuroraMetaProject;
import aurora.ide.project.propertypage.ProjectPropertyPage;

public class BMViewer {

	private VScreenEditor vse;
	private IProject project;
	private TreeViewer viewer;

	private class ContentProvider implements ITreeContentProvider {

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			if(inputElement instanceof ViewDiagram){
				
			}

			return null;
		}

		public Object[] getChildren(Object parentElement) {
			return null;
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			return false;
		}

	}

	private class LabelProvider extends WorkbenchLabelProvider {

	}

	public BMViewer(Composite c, VScreenEditor vScreenEditor) {
		vse = vScreenEditor;
		init();
		configrueTreeViewer(c);
	}

	private void init() {
		Object file = vse.getEditorInput().getAdapter(IFile.class);
		if (file instanceof IFile) {
			project = ((IFile) file).getProject();
		}
	}

	public String getBMHome() {
		AuroraMetaProject amp = new AuroraMetaProject(project);
		IProject auroraProject;
		try {
			auroraProject = amp.getAuroraProject();
			return auroraProject
					.getPersistentProperty(ProjectPropertyPage.BMQN);
		} catch (ResourceNotFoundException e) {
		} catch (CoreException e) {
		}
		return null;
	}

	public IProject getAuroraProject() {
		AuroraMetaProject amp = new AuroraMetaProject(project);
		try {
			return amp.getAuroraProject();

		} catch (ResourceNotFoundException e) {
		}
		return null;
	}

	private void configrueTreeViewer(Composite c) {
		viewer = new TreeViewer(c, SWT.NONE);
		DelegatingDragAdapter dragAdapter = new DelegatingDragAdapter();
		dragAdapter.addDragSourceListener(new TransferDragSourceListener() {
			public Transfer getTransfer() {
				return BMTransfer.getInstance();
			}

			public void dragStart(DragSourceEvent event) {
				// enable drag listener if there is a viewer selection
				IFile bm = getBM();
				if (bm == null)
					return;
				event.detail = DND.DROP_COPY;
				event.doit = true;
				BMTransfer.getInstance().setBM(bm);
			}

			private IFile getBM() {
				TreeSelection selection = (TreeSelection) viewer.getSelection();
				Object obj = selection.getFirstElement();
				if (obj instanceof IFile)
					return (IFile) obj;
				return null;
			}

			public void dragSetData(DragSourceEvent event) {
				event.data = getBM();
			}

			public void dragFinished(DragSourceEvent event) {
				BMTransfer.getInstance().setBM(null);
			}
		});
		viewer.addDragSupport(DND.DROP_COPY, dragAdapter.getTransfers(),
				dragAdapter);
		viewer.setContentProvider(new ContentProvider());
		viewer.setLabelProvider(new LabelProvider());
		refreshInput();
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
//		viewer.addFilter(new ViewerFilter() {
//
//			@Override
//			public boolean select(Viewer viewer, Object parentElement,
//					Object element) {
//				if (element instanceof IResource) {
//					IResource resource = (IResource) element;
//					boolean sameProject = isInAuroraProject(resource);
//					String bmHome = getBMHome();
//					if (bmHome == null || "".equals(bmHome.trim()))
//						return false;
//					if (sameProject) {
//						Path path = new Path(bmHome);
//						IPath fullPath = resource.getFullPath();
//						if (resource instanceof IFile) {
//							return "bm".equalsIgnoreCase(resource
//									.getFileExtension())
//									&& path.isPrefixOf(fullPath);
//						}
//						return fullPath.isPrefixOf(path)
//								|| path.isPrefixOf(fullPath);
//					}
//				}
//				return true;
//			}
//
//			protected boolean isInAuroraProject(IResource resource) {
//				IProject auroraProject = getAuroraProject();
//				return auroraProject != null && auroraProject.exists()
//						&& auroraProject.equals(resource.getProject());
//			}
//
//		});
	}

	public void refreshInput() {
//		String bmHome = getBMHome();
//		if (bmHome == null || "".equals(bmHome.trim()))
//			return;
//		IFolder folder = AuroraPlugin.getWorkspace().getRoot()
//				.getFolder(new Path(bmHome));
//		if (!folder.exists())
//			return;
		viewer.setInput(getViewDiagram());
	}

	private List<IFile> getModelFiles(ViewDiagram viewDiagram){
		if(viewDiagram.isBindTemplate()){
			
		}
		
		
		return null;
	}
	
	
	private ViewDiagram getViewDiagram() {
		Object model = vse.getGraphicalViewer().getContents().getModel();
		if(model instanceof ViewDiagram){
			return (ViewDiagram)model;
		}
		return null;
	}

}
