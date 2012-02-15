package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;

import aurora.ide.meta.gef.editors.figures.ViewDiagramLayout;
import aurora.ide.meta.gef.editors.models.TabBody;
import aurora.ide.meta.gef.editors.policies.AutoCreateFormGridEditPolicy;

public class TabBodyPart extends ContainerPart {

	@Override
	protected IFigure createFigure() {
		Figure figure = new Figure();
		figure.setOpaque(true);
		ViewDiagramLayout manager = new ViewDiagramLayout(false, this);
		figure.setLayoutManager(manager);
		return figure;
	}

	public TabBody getModel() {
		return (TabBody) super.getModel();
	}

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy("Drop BM", new AutoCreateFormGridEditPolicy());
	}

	@Override
	protected void addChild(EditPart child, int index) {
		super.addChild(child, index);
	}

	@Override
	protected void refreshVisuals() {
		getFigure().setVisible(getModel().getVisible());
		super.refreshVisuals();
	}
}
