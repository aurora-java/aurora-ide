package aurora.ide.meta.gef.editors.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import aurora.ide.meta.gef.editors.EditorMode;
import aurora.ide.meta.gef.editors.figures.IResourceDispose;
import aurora.ide.meta.gef.editors.layout.BackLayout;
import aurora.ide.meta.gef.editors.policies.NodeEditPolicy;
import aurora.ide.meta.gef.editors.property.IPropertySource2;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;
import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;

public abstract class ComponentPart extends AbstractGraphicalEditPart implements
		PropertyChangeListener, PositionConstants {
	protected static final String RESIZE_KEY = "resize-key";
	private EditorMode editorMode;

	public void propertyChange(PropertyChangeEvent evt) {
		this.getFigure().getBounds();
		String prop = evt.getPropertyName();
		if (!ComponentInnerProperties.CHILDREN.equals(prop))
			this.refreshVisuals();
		if (ComponentProperties.height.equals(prop)
				|| ComponentProperties.width.equals(prop)
				|| ComponentInnerProperties.BOUNDS.equals(prop)
				|| ComponentInnerProperties.SIZE.equals(prop)
				|| ComponentInnerProperties.LOCATION.equals(prop)
				|| ComponentProperties.colspan.equals(prop)
				|| ComponentProperties.rowspan.equals(prop)
				|| ComponentProperties.minColWidth.equals(prop)
				|| ComponentProperties.minRowHeight.equals(prop)) {
			this.getFigure().revalidate();
		}
	}

	protected ViewDiagramPart getDiagramPart(EditPart ep) {
		if (ep instanceof ViewDiagramPart)
			return (ViewDiagramPart) ep;
		return this.getDiagramPart(ep.getParent());
	}

	@Override
	public void activate() {
		super.activate();
		getComponent().addPropertyChangeListener(this);
	}

	public AuroraComponent getComponent() {
		return (AuroraComponent) getModel();
	}

	@Override
	public void deactivate() {
		getComponent().removePropertyChangeListener(this);
		IFigure figure = this.getFigure();
		if (figure instanceof IResourceDispose) {
			((IResourceDispose) figure).disposeResource();
		}
		super.deactivate();
	}

	protected void refreshVisuals() {
		this.getFigure().repaint();
	}

	protected void createEditPolicies() {
		// installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
		// new NodeDirectEditPolicy());
		// String mode = this.getEditorMode().getMode();
		// if (EditorMode.Template.equals(mode)) {
		// installEditPolicy(EditPolicy.COMPONENT_ROLE,
		// new TemplateNodeEditPolicy());
		// }
		// if (EditorMode.None.equals(mode))
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new NodeEditPolicy());

		// ResizeComponentEditPolicy rep = new ResizeComponentEditPolicy();
		// rep.setResizeDirections(getResizeDirection());
		// installEditPolicy(RESIZE_KEY, rep);
	}

	public EditorMode getEditorMode() {
		return editorMode;
	}

	public void setEditorMode(EditorMode editorMode) {
		this.editorMode = editorMode;
	}

	@Override
	protected abstract IFigure createFigure();

	public int getResizeDirection() {
		return NONE;
	}

	public IPropertySource2 getPropertySource2() {
		return (IPropertySource2) getModel();
	}

	public Rectangle layout() {
		return new BackLayout().layout(this);
	}

}
