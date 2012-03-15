package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

import aurora.ide.meta.gef.editors.EditorMode;
import aurora.ide.meta.gef.editors.figures.GridColumnFigure;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.policies.GridLayoutEditPolicy;
import aurora.ide.meta.gef.editors.policies.tplt.TemplateGridLayoutEditPolicy;

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
		String mode = this.getEditorMode().getMode();
		if (EditorMode.Template.equals(mode)) {
			installEditPolicy(EditPolicy.LAYOUT_ROLE,
					new TemplateGridLayoutEditPolicy());
		}
		if (EditorMode.None.equals(mode)) {
			installEditPolicy(EditPolicy.LAYOUT_ROLE,
					new GridLayoutEditPolicy());
		}
	}

	protected void refreshVisuals() {
		super.refreshVisuals();
	}

	@Override
	public int getResizeDirection() {
		return EAST_WEST;
	}

}
