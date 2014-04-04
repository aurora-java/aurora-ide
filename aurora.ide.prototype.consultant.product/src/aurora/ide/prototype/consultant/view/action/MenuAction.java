package aurora.ide.prototype.consultant.view.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;

abstract public class MenuAction extends Action{

	abstract public void fillContextMenu(IMenuManager menu);
}
