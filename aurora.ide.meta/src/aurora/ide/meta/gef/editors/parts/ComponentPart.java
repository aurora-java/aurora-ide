package aurora.ide.meta.gef.editors.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.IProperties;
import aurora.ide.meta.gef.editors.policies.NodeDirectEditPolicy;
import aurora.ide.meta.gef.editors.policies.NodeEditPolicy;
import aurora.ide.meta.gef.editors.policies.ResizeComponentEditPolicy;
import aurora.ide.meta.gef.editors.property.IPropertySource2;

public abstract class ComponentPart extends AbstractGraphicalEditPart implements
		PropertyChangeListener, IProperties, PositionConstants {
	protected static final String RESIZE_KEY = "resize-key";

	public void propertyChange(PropertyChangeEvent evt) {
		this.getFigure().getBounds();
		String prop = evt.getPropertyName();
		if (!IProperties.CHILDREN.equals(prop))
			this.refreshVisuals();
		if (IProperties.SIZE.equals(prop) || IProperties.BOUNDS.equals(prop)) {
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
		super.deactivate();
	}

	protected void refreshVisuals() {
		// this.getFigure().setBounds(this.getComponent().getBounds());
		// super.refreshVisuals();
		this.getFigure().repaint();
	}

	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
				new NodeDirectEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new NodeEditPolicy());
		ResizeComponentEditPolicy rep = new ResizeComponentEditPolicy();
		rep.setResizeDirections(getResizeDirection());
		installEditPolicy(RESIZE_KEY, rep);
	}

	@Override
	protected abstract IFigure createFigure();

	public int getResizeDirection() {
		return NONE;
	}

	public IPropertySource2 getPropertySource2() {
		return (IPropertySource2) getModel();
	}

}
