package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.figures.NavbarFigure;
import aurora.ide.meta.gef.editors.policies.NoSelectionEditPolicy;
import aurora.plugin.source.gen.screen.model.Navbar;

public class NavbarPart extends ComponentPart {

	@Override
	protected IFigure createFigure() {
		NavbarFigure figure = new NavbarFigure();
		figure.setModel(getModel());
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(NoSelectionEditPolicy.TRANS_SELECTION_KEY,
				new NoSelectionEditPolicy());
	}

	protected void refreshVisuals() {
		super.refreshVisuals();

	}

	public NavbarFigure getFigure() {
		return (NavbarFigure) super.getFigure();
	}

	public Navbar getModel() {
		return (Navbar) super.getModel();
	}

	public Rectangle layout() {
		return new Rectangle(0, 0, 1, 25);
	}

}
