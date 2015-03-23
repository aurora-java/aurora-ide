package aurora.ide.editor.widgets;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import aurora.ide.component.wizard.ActionsFactory;
import aurora.ide.editor.AbstractCMViewer;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.node.action.ActionInfo;


import uncertain.composite.CompositeMap;

public class ToolBarAddElementListener implements Listener {
	private ToolBar toolBar;
	private Menu menu;
	private ToolItem item;
	private AbstractCMViewer viewer;
	public ToolBarAddElementListener(ToolBar toolBar, Menu menu, ToolItem item,
			AbstractCMViewer container) {
		this.toolBar = toolBar;
		this.menu = menu;
		this.item = item;
		this.viewer = container;

	}

	public void handleEvent(Event event) {
		if (event.detail == SWT.ARROW) {
			CompositeMap currentNode = viewer.getFocus();
			if(currentNode == null)return;
			MenuItem[] mi = menu.getItems();
			for (int i = 0; i < mi.length; i++) {
				mi[i].dispose();
			}
			ActionInfo actionInfo = new ActionInfo(viewer,currentNode);
			try {
				ActionsFactory.getInstance().addActionsToMenu(menu, actionInfo);
			} catch (ApplicationException e) {
				DialogUtil.showExceptionMessageBox(e);
			}
			Rectangle rect = item.getBounds();
			Point pt = new Point(rect.x, rect.y + rect.height);
			pt = toolBar.toDisplay(pt);
			menu.setLocation(pt.x, pt.y);
			menu.setVisible(true);
		}
	}
}
