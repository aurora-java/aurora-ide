package aurora.bpmn.designer.rcp.workbench;

import org.eclipse.e4.ui.model.application.ui.SideValue;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimBar;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.keys.IBindingService;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(800, 600));
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(false);
		// this.getWindowConfigurer().setShowCoolBar(false);
		// this.getWindowConfigurer().setShowMenuBar(false);
	}

	@Override
	public void postWindowOpen() {
		// super.postWindowOpen();
		IActionBarConfigurer actionBarConfigurer = this.getWindowConfigurer()
				.getActionBarConfigurer();
		ICoolBarManager coolBarManager = actionBarConfigurer
				.getCoolBarManager();
		IContributionItem[] items = coolBarManager.getItems();
		// coolBarManager.setLockLayout(false);
		// org.eclipse.search.searchActionSet
		// org.eclipse.ui.edit.text.actionSet.annotationNavigation
		// org.eclipse.ui.edit.text.actionSet.navigation
		// org.eclipse.debug.ui.launchActionSet
		// group.editor
		for (IContributionItem iContributionItem : items) {
			if ("org.eclipse.search.searchActionSet".equals(iContributionItem
					.getId())) {
				iContributionItem.setVisible(false);
				coolBarManager.remove(iContributionItem);
			}
			if ("org.eclipse.ui.edit.text.actionSet.annotationNavigation"
					.equals(iContributionItem.getId())) {
				iContributionItem.setVisible(false);
				coolBarManager.remove(iContributionItem);
			}
			if ("org.eclipse.ui.edit.text.actionSet.navigation"
					.equals(iContributionItem.getId())) {
				iContributionItem.setVisible(false);
				coolBarManager.remove(iContributionItem);
			}
			if ("org.eclipse.debug.ui.launchActionSet".equals(iContributionItem
					.getId())) {
				iContributionItem.setVisible(false);
				coolBarManager.remove(iContributionItem);
			}
			if ("group.editor".equals(iContributionItem.getId())) {
				// print(iContributionItem);
				// if(iContributionItem instanceof GroupMarker){
				// IContributionItem[] items2 = ((GroupMarker)
				// iContributionItem).getParent().getItems();
				// for (IContributionItem iContributionItem2 : items2) {
				// System.out.println(iContributionItem2);
				// }
				// }
				// iContributionItem.setVisible(false);
				// coolBarManager.remove(iContributionItem);
			}

		}

		IMenuManager menuManager = actionBarConfigurer.getMenuManager();
		IContributionItem[] items2 = menuManager.getItems();
		for (IContributionItem iContributionItem : items2) {
			// System.err.println(iContributionItem.getId());
			if ("org.eclipse.search.menu".equals(iContributionItem.getId())) {
				iContributionItem.setVisible(false);
			}
			if ("org.eclipse.ui.run".equals(iContributionItem.getId())) {
				iContributionItem.setVisible(false);
			}
			if ("help".equals(iContributionItem.getId())) {
				iContributionItem.setVisible(false);
			}
		}
	}

	public void print(IContributionItem iContributionItem) {
		if (iContributionItem instanceof ToolBarContributionItem) {
			ToolBarContributionItem ci = (ToolBarContributionItem) iContributionItem;
			IContributionItem[] items2 = ci.getToolBarManager().getItems();
			for (IContributionItem iContributionItem2 : items2) {
				System.out.println(iContributionItem2.getId());
			}
		}
	}

	@Override
	public void openIntro() {
		super.openIntro();
		IWorkbenchWindow window = (IWorkbenchWindow) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (window instanceof WorkbenchWindow) {
			MWindow model = ((WorkbenchWindow) window).getModel();
			EModelService modelService = model.getContext().get(
					EModelService.class);
			MToolControl searchField = (MToolControl) modelService.find(
					"SearchField", model);
			if (searchField != null) {
				searchField.setToBeRendered(false);
				MTrimBar trimBar = modelService.getTrim((MTrimmedWindow) model,
						SideValue.TOP);
				trimBar.getChildren().remove(searchField);
			}
		}
		
//		IBindingService adapter = (IBindingService) PlatformUI.getWorkbench().getAdapter(IBindingService.class);
////		adapter.r.getBindings()[0].
//		adapter.setKeyFilterEnabled(false);
	}

	public void postWindowCreate() {
		super.postWindowCreate();
		// 设置打开时最大化窗口
		getWindowConfigurer().getWindow().getShell().setMaximized(true);
		// this.getWindowConfigurer().setShowCoolBar(false);
		// this.getWindowConfigurer().setShowMenuBar(false);
	}

}
