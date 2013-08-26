package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;

import aurora.ide.meta.gef.editors.figures.GridFigure;
import aurora.ide.meta.gef.editors.layout.GridBackLayout;
import aurora.ide.meta.gef.editors.policies.GridLayoutEditPolicy;
import aurora.plugin.source.gen.screen.model.Container;
import aurora.plugin.source.gen.screen.model.Grid;
import aurora.plugin.source.gen.screen.model.properties.ComponentFSDProperties;

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
//		String mode = this.getEditorMode().getMode();
//		if (EditorMode.Template.equals(mode)) {
//			installEditPolicy(EditPolicy.LAYOUT_ROLE,
//					new TemplateGridLayoutEditPolicy());
//		}
//		if (EditorMode.None.equals(mode)) {
			installEditPolicy(EditPolicy.LAYOUT_ROLE,
					new GridLayoutEditPolicy());
//		}
	}

	protected void refreshVisuals() {
		super.refreshVisuals();
		getFigure().setToolTip(new Label(this.getComponent().getStringPropertyValue(ComponentFSDProperties.FSD_DESC)));
	}

	@Override
	public int getResizeDirection() {
		return NSEW;
	}
	public Rectangle layout() {
		GridBackLayout layout = new GridBackLayout();
		return layout.layout(this);
	}
	
	public Grid getGrid(){
		return (Grid) this.getComponent();
	}
}
