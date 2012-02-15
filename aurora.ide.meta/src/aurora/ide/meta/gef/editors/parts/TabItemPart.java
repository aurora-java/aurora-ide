package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.Request;

import aurora.ide.meta.gef.editors.figures.TabItemFigure;
import aurora.ide.meta.gef.editors.models.TabItem;

public class TabItemPart extends ComponentPart {

	@Override
	protected IFigure createFigure() {
		TabItemFigure f = new TabItemFigure();
		TabItem model = getModel();
		f.setModel(model);
		return f;
	}

	public TabItem getModel() {
		return (TabItem) super.getModel();
	}

	public TabItemFigure getFigure() {
		return (TabItemFigure) super.getFigure();
	}

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
	}

	@Override
	public void performRequest(Request req) {
		super.performRequest(req);
		if (req.getType().equals(REQ_OPEN)) {
			TabFolderPart parent = (TabFolderPart) getParent();
			parent.getModel().disSelectAll();
			getModel().setCurrent(true);
		}
	}

	@Override
	public int getResizeDirection() {
		return EAST_WEST;
	}
}
