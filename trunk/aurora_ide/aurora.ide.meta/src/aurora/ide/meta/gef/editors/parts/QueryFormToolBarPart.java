package aurora.ide.meta.gef.editors.parts;

import java.util.List;

import org.eclipse.draw2d.AbstractBackground;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Resource;

import aurora.ide.helpers.ImagesUtils;
import aurora.ide.meta.gef.editors.figures.BoxFigure;
import aurora.ide.meta.gef.editors.figures.ColorConstants;
import aurora.ide.meta.gef.editors.figures.FigureUtil;
import aurora.ide.meta.gef.editors.figures.ResourceDisposer;
import aurora.ide.meta.gef.editors.layout.RowColBackLayout;
import aurora.ide.meta.gef.editors.policies.NoSelectionEditPolicy;
import aurora.ide.meta.gef.util.BoundsConvert;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.BOX;
import aurora.plugin.source.gen.screen.model.QueryForm;

public class QueryFormToolBarPart extends BoxPart {

	@Override
	protected IFigure createFigure() {
		BoxFigure figure = new BoxFigure();
		figure.setBox((BOX) getModel());
		figure.setBorder(new AbstractBackground() {
			// private Image bgImage = ImagesUtils
			// .getImage("palette/queryform.gif");
			private Image bgImage = ImagesUtils.getImage("toolbar_bg");

			public void paint(IFigure figure, Graphics g, Insets insets) {
				// tempRect.setBounds(getPaintRectangle(figure, insets));
				//
				// FigureUtilities.paintEtchedBorder(g, tempRect);

				// Rectangle rec = tempRect;
				// rec.height = 25;
				// g.clipRect(rec);
				//
				// g.fillRectangle(rec);
				//
				// FigureUtilities.paintEtchedBorder(g, tempRect);

			}

			public void paintBackground(IFigure figure, Graphics graphics,
					Insets insets) {

				// int d = 12;
				// Path path = new Path(null);
				// Rectangle rect = figure.getBounds();
				// path.addArc(rect.x, rect.y, d, d, 90, 90);
				// path.lineTo(rect.x, rect.y + rect.height - d / 2);
				// path.addArc(rect.x, rect.y + rect.height - d, d, d, 180, 90);
				// path.lineTo(rect.x + rect.width - d / 2, rect.y +
				// rect.height);
				// path.addArc(rect.x + rect.width - d, rect.y + rect.height -
				// d,
				// d, d, -90, 90);
				// path.lineTo(rect.x + rect.width, rect.y + d / 2);
				// path.addArc(rect.x + rect.width - d, rect.y, d, d, 0, 90);
				// path.close();
				// graphics.setClip(path);
				graphics.drawImage(bgImage, new Rectangle(bgImage.getBounds()),
						figure.getBounds());
				List<IFigure> childs = figure.getChildren();
				if (childs.get(0).getChildren().size() == 0) {
					Rectangle hBoxRect = childs.get(0).getBounds();
					hBoxRect = hBoxRect.getShrinked(new Insets(10, 10, 10, 10));
					graphics.setForegroundColor(ColorConstants.WHITE);
					graphics.fillRectangle(hBoxRect);
					graphics.setForegroundColor(ColorConstants.EDITOR_BORDER);
					graphics.drawRectangle(hBoxRect);
					String hint = ((QueryForm) getParent().getModel())
							.getDefaultQueryHint();
					FigureUtil.paintText(graphics, hBoxRect.translate(3, 1),
							hint, -1, 0);
				}
				tempRect.setBounds(getPaintRectangle(figure, insets));
				paintEtchedBorder(graphics, tempRect);
			}
		});
		return figure;
	}

	private void paintEtchedBorder(Graphics g, Rectangle r) {
		disposeResource("shadow");
		disposeResource("highlight");
		Color rgb = g.getBackgroundColor(), shadow = FigureUtilities
				.darker(rgb), highlight = FigureUtilities.lighter(rgb);
		handleResource("shadow", shadow);
		handleResource("highlight", highlight);
		FigureUtilities.paintEtchedBorder(g, r, shadow, highlight);
	}

	@Override
	public void deactivate() {
		disposeResource();
		super.deactivate();
	}

	private ResourceDisposer disposer = new ResourceDisposer();

	private void disposeResource() {
		disposer.disposeResource();
		disposer = null;
	}

	private void handleResource(String id, Resource r) {
		disposer.handleResource(id, r);
	}

	private void disposeResource(String prop_id) {
		disposer.disposeResource(prop_id);
	}

	@Override
	protected void createEditPolicies() {
	}

	@Override
	protected void addChild(EditPart child, int index) {
		child.installEditPolicy(NoSelectionEditPolicy.TRANS_SELECTION_KEY,
				new NoSelectionEditPolicy());
		super.addChild(child, index);
	}

	protected void refreshVisuals() {
		super.refreshVisuals();
	}

	@Override
	public Rectangle layout() {
		@SuppressWarnings("unchecked")
		List<ComponentPart> list = getChildren();
		Rectangle rect = BoundsConvert.toDraw2d(((AuroraComponent) getModel())
				.getBoundsCopy());
		int buttonGap = 10;
		rect.height = 40;
		int buttonWidth = 80;
		for (int i = list.size() - 1; i > 0; i--) {
			IFigure f1 = list.get(i).getFigure();
			Rectangle r1 = list.get(i).layout();
			buttonWidth = r1.width;
			r1.x = rect.width + rect.x - (r1.width + buttonGap)
					* (list.size() - i);
			r1.y = rect.y + (rect.height - r1.height) / 2;
			f1.setBounds(r1);
		}
		ComponentPart hbox = list.get(0);
		IFigure f0 = hbox.getFigure();
		if (f0.getBorder() != null)
			f0.setBorder(null);
		f0.setBounds(new Rectangle(rect.x, rect.y, rect.width - buttonWidth
				* (list.size() - 1) - buttonGap * list.size(), rect.height));
		RowColBackLayout rowColBackLayout = new RowColBackLayout();
		rowColBackLayout.setPadding(new Insets(6, 0, 0, 0));
		rowColBackLayout.layout(hbox);
		return rect;
	}

	@Override
	public boolean isLayoutHorizontal() {
		return true;
	}

	@Override
	public int getResizeDirection() {
		return NONE;
	}
}
