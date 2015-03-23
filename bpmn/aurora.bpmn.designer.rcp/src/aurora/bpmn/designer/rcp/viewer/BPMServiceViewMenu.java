package aurora.bpmn.designer.rcp.viewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;

import aurora.bpmn.designer.rcp.viewer.action.BPMDefinePropertyAction;
import aurora.bpmn.designer.rcp.viewer.action.CreateBPMDefineAction;
import aurora.bpmn.designer.rcp.viewer.action.CreateBPMDefineVerAction;
import aurora.bpmn.designer.rcp.viewer.action.CreateServiceAction;
import aurora.bpmn.designer.rcp.viewer.action.DelServiceAction;
import aurora.bpmn.designer.rcp.viewer.action.DeleteBPMDefineAction;
import aurora.bpmn.designer.rcp.viewer.action.EditBPMDefineAction;
import aurora.bpmn.designer.rcp.viewer.action.EnableBPMDefineAction;
import aurora.bpmn.designer.rcp.viewer.action.LoadBPMServiceAction;
import aurora.bpmn.designer.rcp.viewer.action.ServicePropertyAction;
import aurora.bpmn.designer.rcp.viewer.action.SubmitBPMDefineAction;
import aurora.bpmn.designer.rcp.viewer.action.ViewAction;

public class BPMServiceViewMenu {

	private TreeViewer viewer;
	private BPMServiceViewer bpmServiceViewer;

	public BPMServiceViewMenu(TreeViewer viewer,
			BPMServiceViewer bpmServiceViewer) {
		super();
		this.viewer = viewer;
		this.bpmServiceViewer = bpmServiceViewer;
	}

	public void initContextMenu() {
		MenuManager menuMgr = new MenuManager("NavigationViewMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}

		});
		Menu menu = menuMgr.createContextMenu(viewer.getTree());
		viewer.getTree().setMenu(menu);
	}

	private void fillContextMenu(IMenuManager menu) {
		List<ViewAction> makeActions = makeActions();
		for (ViewAction viewAction : makeActions) {
			viewAction.init();
			if (viewAction.isVisible())
				menu.add(viewAction);
		}
		// menu.add(new Action("新建服务") {
		// public void run() {
		// Shell shell = bpmServiceViewer.getSite().getShell();
		//
		// CreateBPMServiceWizard wizard = new CreateBPMServiceWizard(
		// shell);
		// int open = wizard.open();
		// if (open == WizardDialog.OK) {
		// ServiceModel serviceModel = wizard.getServiceModel();
		// ViewerInput viewerInput = bpmServiceViewer.getViewerInput();
		// viewerInput.addService(serviceModel);
		// BPMServiceViewerStore.saveViewerInput(viewerInput);
		// bpmServiceViewer.getTreeViewer().refresh(viewerInput);
		// }
		// }
		// });

		// TreeItem[] selection = viewer.getTree().getSelection();
		// if (selection.length == 0)
		// return;
		// Object data = selection[0].getData();
		// if (data instanceof ServiceModel) {
		// // LoadBPMServiceAction lsa = new LoadBPMServiceAction("连接服务",
		// // (ServiceModel) data, viewer);
		// //
		// // menu.add(lsa);
		// // CreateBPMDefineAction ca = new CreateBPMDefineAction("新建工作流",
		// // (ServiceModel) data, bpmServiceViewer);
		// // menu.add(ca);
		// // LoadBPMServiceAction lsar = new LoadBPMServiceAction("刷新",
		// // (ServiceModel) data, viewer);
		// //
		// // menu.add(lsar);
		// // menu.add(new Action("删除服务") {
		// //
		// // });
		// menu.add(new Action("属性") {
		//
		// });
		//
		// }
		//
		// if (data instanceof BPMNDefineModel) {
		// // EditBPMDefineAction ea = new EditBPMDefineAction("编辑工作流",
		// // (BPMNDefineModel) data, bpmServiceViewer);
		// // menu.add(ea);
		// // DeleteBPMDefineAction del = new DeleteBPMDefineAction("删除工作流",
		// // (BPMNDefineModel) data, bpmServiceViewer);
		// // menu.add(del);
		// menu.add(new Action("属性") {
		//
		// });
		// }

	}

	private List<ViewAction> makeActions() {
		List<ViewAction> actions = new ArrayList<ViewAction>();
		actions.add(new CreateServiceAction(bpmServiceViewer, "新建服务"));
		actions.add(new LoadBPMServiceAction("连接服务", viewer));
		actions.add(new LoadBPMServiceAction("刷新", viewer));
		actions.add(new CreateBPMDefineAction("新建工作流", bpmServiceViewer));
		//TODO
		actions.add(new CreateBPMDefineVerAction("创建新版本", bpmServiceViewer));
		actions.add(new DelServiceAction("删除服务", viewer));
		
		actions.add(new EditBPMDefineAction("编辑工作流", bpmServiceViewer));
		actions.add(new DeleteBPMDefineAction("删除工作流", bpmServiceViewer));
		actions.add(new SubmitBPMDefineAction("提交工作流", bpmServiceViewer));
		actions.add(new EnableBPMDefineAction("生效/失效工作流", bpmServiceViewer));
		actions.add(new ServicePropertyAction("属性", viewer));
		actions.add(new BPMDefinePropertyAction("属性", bpmServiceViewer));

		return actions;
	}
}
