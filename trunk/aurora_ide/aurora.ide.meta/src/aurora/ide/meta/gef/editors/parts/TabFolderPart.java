package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.figures.TabFolderFigure;
import aurora.ide.meta.gef.editors.layout.TabFolderLayout;
import aurora.ide.meta.gef.editors.models.TabFolder;

public class TabFolderPart extends ContainerPart {

	@Override
	protected IFigure createFigure() {
		TabFolderFigure figure = new TabFolderFigure();
		figure.setModel(getModel());
		return figure;
	}

	public TabFolder getModel() {
		return (TabFolder) super.getModel();
	}

	public TabFolderFigure getFigure() {
		return (TabFolderFigure) super.getFigure();
	}

	protected void refreshVisuals() {
		super.refreshVisuals();
	}

	@Override
	protected void refreshChildren() {
		super.refreshChildren();
	}

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
	}

	@Override
	public int getResizeDirection() {
		return NSEW;
	}
	public boolean isLayoutHorizontal() {
		return true;
	}
	public Rectangle layout() {
		TabFolderLayout layout = new TabFolderLayout();
		return layout.layout(this);
	}
}
