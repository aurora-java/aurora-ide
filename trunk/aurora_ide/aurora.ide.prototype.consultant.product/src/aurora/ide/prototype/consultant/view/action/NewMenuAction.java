package aurora.ide.prototype.consultant.view.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import aurora.ide.libs.AuroraImagesUtils;
import aurora.ide.prototype.consultant.view.NavigationView;

public class NewMenuAction extends Action implements IMenuCreator {

	private NavigationView viewer;
	private CreateProjectAction createProjectAction;
	private CreateFunctionAction createFunctionAction;
	private CreateUIPAction createUIPAction;
	private CreateModuleAction createModuleAction;

	private Action activeAction;

	public NewMenuAction(NavigationView viewer) {
		super(Messages.NewMenuAction_0, Action.AS_DROP_DOWN_MENU);
		this.viewer = viewer;
		this.setMenuCreator(this);
		makeActions();
	}

	private void makeActions() {
		createProjectAction = new CreateProjectAction(this.viewer);
		createFunctionAction = new CreateFunctionAction(this.viewer);
		createUIPAction = new CreateUIPAction(this.viewer);
		createModuleAction = new CreateModuleAction(this.viewer);
	}

	public void run() {
		if (activeAction != null && activeAction.isEnabled()) {
			activeAction.run();
		} else {
			createProjectAction.run();
		}
	}

	@Override
	public void dispose() {

	}

	public Menu getMenu(Control parent) {
		Menu menu = new Menu(parent);
		fillMenu(menu);
		return menu;
	}

	private class ActionRunner extends SelectionAdapter {

		private Action action;

		private ActionRunner(Action action) {
			this.action = action;
		}

		public void widgetSelected(SelectionEvent e) {
			if (action != null && action.isEnabled()) {
				action.run();
				activeAction = action;
			}
		}
	}

	private MenuItem createMenuItem(Menu menu, Action action, String text) {
		MenuItem mi = new MenuItem(menu, SWT.NONE);
		mi.setText(text);
		mi.setEnabled(action.isEnabled());
		mi.addSelectionListener(new ActionRunner(action));
		return mi;
	}

	public void fillMenu(Menu menu) {
		createMenuItem(menu, createProjectAction, Messages.NewMenuAction_1)
				.setImage(
						PlatformUI.getWorkbench().getSharedImages()
								.getImage(ISharedImages.IMG_OBJ_FOLDER));
		createMenuItem(menu, createModuleAction, Messages.NewMenuAction_2)
				.setImage(
						PlatformUI.getWorkbench().getSharedImages()
								.getImage(ISharedImages.IMG_OBJ_FOLDER));
		createMenuItem(menu, createFunctionAction, Messages.NewMenuAction_3)
				.setImage(
						PlatformUI.getWorkbench().getSharedImages()
								.getImage(ISharedImages.IMG_OBJ_FOLDER));
		createMenuItem(menu, createUIPAction, Messages.NewMenuAction_4)
				.setImage(AuroraImagesUtils.getImage("/meta.png"));
	}

	public Menu getMenu(Menu parent) {
		Menu menu = new Menu(parent);
		fillMenu(menu);
		return menu;
	}

}
