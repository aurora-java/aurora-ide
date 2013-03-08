package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;

import aurora.ide.meta.gef.editors.figures.BoxFigure;
import aurora.ide.meta.gef.editors.layout.QueryFormLayout;
import aurora.ide.meta.gef.editors.models.BOX;
import aurora.ide.meta.gef.editors.policies.NoSelectionEditPolicy;
import aurora.ide.meta.gef.editors.policies.QueryFormLayoutEditPolicy;

public class QueryFormPart extends BoxPart {
	private QueryFormLayout layouter = new QueryFormLayout();

	@Override
	protected IFigure createFigure() {
		BoxFigure figure = new BoxFigure();
		figure.setBox((BOX) getModel());
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		// String mode = this.getEditorMode().getMode();
		// if (EditorMode.Template.equals(mode)) {
		// installEditPolicy(EditPolicy.LAYOUT_ROLE,
		// new TemplateGridLayoutEditPolicy());
		// }
		// if (EditorMode.None.equals(mode)) {
		installEditPolicy(EditPolicy.LAYOUT_ROLE,
				new QueryFormLayoutEditPolicy());
	}

	//
	// }

	@Override
	protected void addChild(EditPart child, int index) {
		if (child instanceof QueryFormToolBarPart)
			child.installEditPolicy(NoSelectionEditPolicy.TRANS_SELECTION_KEY,
					new NoSelectionEditPolicy());
		super.addChild(child, index);
	}

	@Override
	public Rectangle layout() {
		return layouter.layout(this);
	}

	protected void refreshVisuals() {
		super.refreshVisuals();
	}

	@Override
	public int getResizeDirection() {
		return NSEW;
	}
}
