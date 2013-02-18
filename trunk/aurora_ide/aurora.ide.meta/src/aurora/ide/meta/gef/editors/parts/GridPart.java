package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;

import aurora.ide.meta.gef.editors.EditorMode;
import aurora.ide.meta.gef.editors.figures.GridFigure;
import aurora.ide.meta.gef.editors.layout.GridBackLayout;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.policies.GridLayoutEditPolicy;
import aurora.ide.meta.gef.editors.policies.tplt.TemplateGridLayoutEditPolicy;

public class GridPart extends ContainerPart {

	@Override
	protected IFigure createFigure() {
		GridFigure figure = new GridFigure();
		figure.setModel((Container) this.getComponent());
		return figure;
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
		return NSEW;
	}
	public Rectangle layout() {
		GridBackLayout layout = new GridBackLayout();
		return layout.layout(this);
	}
}
