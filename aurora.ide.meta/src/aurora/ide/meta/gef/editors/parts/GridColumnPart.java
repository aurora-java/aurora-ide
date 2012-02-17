package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

import aurora.ide.meta.gef.editors.figures.GridColumnFigure;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.policies.GridLayoutEditPolicy;

public class GridColumnPart extends ContainerPart {

	@Override
	protected IFigure createFigure() {
		GridColumnFigure figure = new GridColumnFigure();
		figure.setModel(getModel());
		return figure;
	}

	public GridColumn getModel() {
		return (GridColumn) super.getModel();
	}

	public GridColumnFigure getFigure() {
		return (GridColumnFigure) super.getFigure();
	}

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new GridLayoutEditPolicy());
	}

	protected void refreshVisuals() {
		super.refreshVisuals();
	}

	@Override
	public int getResizeDirection() {
		return EAST_WEST;
	}

}
