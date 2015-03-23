package aurora.bpmn.designer.rcp.viewer.action;

import org.eclipse.jface.action.Action;

abstract public class ViewAction extends Action {
	private boolean isVisible;

	public ViewAction() {
//		init();
	}

	public abstract void init();

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
}
