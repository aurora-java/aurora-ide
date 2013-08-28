package aurora.ide.meta.gef.editors.parts;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.AbstractLabeledBorder;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.figures.BoxFigure;
import aurora.ide.meta.gef.editors.layout.RowColBackLayout;
import aurora.ide.meta.gef.editors.layout.RowColSpanBackLayout;
import aurora.plugin.source.gen.screen.model.BOX;
import aurora.plugin.source.gen.screen.model.properties.ComponentFSDProperties;
import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;

public class BoxPart extends ContainerPart {

	@Override
	protected IFigure createFigure() {
		BoxFigure figure = new BoxFigure();
		BOX model = (BOX) getModel();
		figure.setBox(model);
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
	}

	protected void refreshVisuals() {
		BOX model = (BOX) getModel();
		BoxFigure figure = (BoxFigure) getFigure();
		Border border = figure.getBorder();
		if (border instanceof AbstractLabeledBorder) {
			((AbstractLabeledBorder) border).setLabel(model.getTitle());
		}
		super.refreshVisuals();
		getFigure().setToolTip(new Label(this.getComponent().getStringPropertyValue(ComponentFSDProperties.FSD_DESC)));
	}

	public void applyToModel() {
		super.applyToModel();
		Rectangle bounds = this.getFigure().getBounds();
		this.getComponent().applyToModel(
				new aurora.plugin.source.gen.screen.model.Rectangle(bounds.x,
						bounds.y, bounds.width, bounds.height));
	}

	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		String prop = evt.getPropertyName();
		if (ComponentProperties.row.equals(prop)
				|| ComponentProperties.column.equals(prop)) {
			this.getFigure().revalidate();
		}
	}

	@Override
	public int getResizeDirection() {
		return NSEW;
	}

	public boolean isLayoutHorizontal() {
		BOX model = (BOX) getModel();
		int col = model.getCol();
		return col > 1;
	}

	private static final Insets BOX_PADDING = new Insets(8, 16, 8, 6);

	public Rectangle layout() {
//		RowColBackLayout rowColBackLayout = new RowColBackLayout();
		RowColSpanBackLayout rowColBackLayout = new RowColSpanBackLayout();
		rowColBackLayout.setPadding(BOX_PADDING);
		return rowColBackLayout.layout(this);
	}

}
