package aurora.ide.prototype.consultant.view.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.handlers.CollapseAllHandler;

public class CollapseAllAction extends Action{


	private final TreeViewer commonViewer;

	/**
	 * Create the CollapseAll action.
	 * 
	 * @param aViewer
	 *            The viewer to be collapsed.
	 */
	public CollapseAllAction(TreeViewer aViewer) {
		super("Collapse All");
		setToolTipText("Collapse All");
		setActionDefinitionId(CollapseAllHandler.COMMAND_ID);
		commonViewer = aViewer;
	}

	public void run() {
		if (commonViewer != null) {
			commonViewer.collapseAll();
		}
	}

}
