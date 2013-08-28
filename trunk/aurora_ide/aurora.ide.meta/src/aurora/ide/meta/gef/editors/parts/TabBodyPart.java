package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;

import aurora.ide.meta.gef.editors.figures.ViewDiagramLayout;
import aurora.plugin.source.gen.screen.model.TabBody;
import aurora.plugin.source.gen.screen.model.TabItem;

public class TabBodyPart extends ContainerPart {

	@Override
	protected IFigure createFigure() {
		Figure figure = new Figure() {

			@Override
			protected void paintFigure(Graphics graphics) {
				TabBody body = getModel();
				if (body != null) {
					TabItem ti = body.getTabItem();
					if (ti != null) {
//						TabRef tr = ti.getTabRef();
//						if (tr != null) {
//							graphics.setForegroundColor(ColorConstants.LINK_COLOR);
//							FigureUtil.paintTextAtCenter(graphics, getBounds(),
//									tr.getOpenPath());
//						}
					}
				}
			}
		};
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
		// installEditPolicy("Drop BM", new AutoCreateFormGridEditPolicy());
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
