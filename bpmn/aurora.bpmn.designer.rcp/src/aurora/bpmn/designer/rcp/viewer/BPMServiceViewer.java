package aurora.bpmn.designer.rcp.viewer;

import java.util.List;

import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import aurora.bpmn.designer.rcp.Activator;
import aurora.bpmn.designer.rcp.viewer.action.EditBPMDefineAction;
import aurora.bpmn.designer.ws.BPMNDefineCategory;
import aurora.bpmn.designer.ws.BPMNDefineModel;
import aurora.bpmn.designer.ws.BPMNDefineModelVER;
import aurora.bpmn.designer.ws.ServiceModel;
import aurora.ide.swt.util.ImagesUtils;

public class BPMServiceViewer extends ViewPart {
	public static final String ID = "aurora.bpmn.designer.rcp.viewer.BPMServiceViewer";
	private TreeViewer viewer;
	private ViewerInput viewerInput;

	class ViewContentProvider implements IStructuredContentProvider,
			ITreeContentProvider {

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			if (parent instanceof ViewerInput) {
				List<ServiceModel> services = ((ViewerInput) parent)
						.getServices();
				return services.toArray(new ServiceModel[services.size()]);
			}
			return null;
		}

		public Object getParent(Object child) {
			if (child instanceof ServiceModel)
				return viewerInput;
			if (child instanceof INode) {
				return ((INode) child).getParent();
			}
			return null;
		}

		public Object[] getChildren(Object parent) {
			if (parent instanceof IParent) {
				return ((IParent) parent).getChildren();
			}
			return new Object[0];
		}

		public boolean hasChildren(Object parent) {
			if (parent instanceof IParent) {
				return ((IParent) parent).getChildren().length > 0;
			}
			return false;
		}
	}

	class ViewLabelProvider extends LabelProvider {

		public String getText(Object obj) {
			if (obj instanceof ServiceModel)
				return ((ServiceModel) obj).getServiceName();
			else if (obj instanceof BPMNDefineModel) {
//				return ((BPMNDefineModel) obj).getName();
				return ((BPMNDefineModel) obj).getProcess_version();
			} else if (obj instanceof BPMNDefineCategory) {
				return ((BPMNDefineCategory) obj).getName();
			} else if (obj instanceof BPMNDefineModelVER) {
				return ((BPMNDefineModelVER) obj).getName();
			}
			return obj.toString();
		}

		public Image getImage(Object obj) {
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			if (obj instanceof ServiceModel)
				imageKey = ISharedImages.IMG_OBJ_FOLDER;
			if (obj instanceof BPMNDefineCategory)
				imageKey = ISharedImages.IMG_OBJ_FOLDER;
			if (obj instanceof BPMNDefineModelVER){
//				imageKey = ISharedImages.IMG_OBJ_FOLDER;
				return ImagesUtils.getImage("obj16/bpmn2process.png");
			}
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(imageKey);
		}
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new ViewerSorter());
		viewerInput = createViewerInput();
		viewer.setInput(getViewerInput());
		BPMServiceViewMenu menu = new BPMServiceViewMenu(viewer, this);
		menu.initContextMenu();
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(final DoubleClickEvent event) {
				SafeRunner.run(new SafeRunnable() {
					public void run() throws Exception {
						handleDoubleClick(event);
					}

				});
			}
		});
	}

	private ViewerInput createViewerInput() {
		return BPMServiceViewerStore.loadViewerInput();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	private void handleDoubleClick(DoubleClickEvent event) {
		// open editor
		EditBPMDefineAction editBPMDefineAction = new EditBPMDefineAction(
				"编辑工作流", this);
		editBPMDefineAction.init();
		if (editBPMDefineAction.isVisible()) {
			editBPMDefineAction.run();
		}
		// try {
		// // URI modelUri = URI
		// //
		// .createURI("platform:/plugin/aurora.bpmn.designer.rcp/test.bpmn#/0");
		// // URI diagramUri = URI
		// //
		// .createURI("platform:/plugin/aurora.bpmn.designer.rcp/test.bpmn#/1");
		// // Bpmn2DiagramEditorInput input = new Bpmn2DiagramEditorInput(
		// // modelUri, diagramUri,
		// // "org.eclipse.bpmn2.modeler.ui.diagram.MainBPMNDiagramType");
		// // diagramComposite.setInput(new DiagramEditorInput(uri,
		// //
		// "org.eclipse.graphiti.examples.tutorial.diagram.TutorialDiagramTypeProvider"));
		// this.getSite()
		// .getPage()
		// .openEditor(
		// new BPMServiceInputStreamEditorInput(
		// TestBPMN.getStream()), AuroraBpmnEditor.ID,
		// true);
		// } catch (PartInitException e) {
		// MessageDialog.openError(this.getSite().getShell(), "Error",
		// "Error opening view:" + e.getMessage());
		// }
	}

	public TreeViewer getTreeViewer() {
		return this.viewer;
	}

	public ViewerInput getViewerInput() {
		return viewerInput;
	}

}