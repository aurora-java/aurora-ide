package aurora.ide.meta.gef.editors.parts;

import java.util.List;

import org.eclipse.draw2d.AbstractBackground;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;

import aurora.ide.helpers.ImagesUtils;
import aurora.ide.meta.gef.editors.figures.BoxFigure;
import aurora.ide.meta.gef.editors.layout.RowColBackLayout;
import aurora.ide.meta.gef.editors.models.BOX;
import aurora.ide.meta.gef.editors.policies.NoSelectionEditPolicy;

public class QueryFormToolBarPart extends BoxPart {

	@Override
	protected IFigure createFigure() {
		BoxFigure figure = new BoxFigure();
		figure.setBox((BOX) getModel());
		figure.setBorder(new AbstractBackground() {
			private Image bgImage = ImagesUtils
					.getImage("palette/queryform.gif");

			public void paintBackground(IFigure figure, Graphics graphics,
					Insets insets) {
				int d = 6;
				Path path = new Path(null);
				Rectangle rect = figure.getBounds();
				path.addArc(rect.x, rect.y, d, d, 90, 90);
				path.lineTo(rect.x, rect.y + rect.height - d / 2);
				path.addArc(rect.x, rect.y + rect.height - d, d, d, 180, 90);
				path.lineTo(rect.x + rect.width - d / 2, rect.y + rect.height);
				path.addArc(rect.x + rect.width - d, rect.y + rect.height - d,
						d, d, -90, 90);
				path.lineTo(rect.x + rect.width, rect.y + d / 2);
				path.addArc(rect.x + rect.width - d, rect.y, d, d, 0, 90);
				path.close();
				graphics.setClip(path);
				graphics.drawImage(bgImage, new Rectangle(bgImage.getBounds()),
						figure.getBounds());
			}
		});
		return figure;
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
		Rectangle rect = getFigure().getBounds();
		int buttonGap = 10;
		rect.height = 40;
		IFigure f1 = list.get(1).getFigure();
		Rectangle r1 = list.get(1).layout();
		r1.x = rect.width + rect.x - (r1.width + buttonGap) * 2;
		r1.y = rect.y + (rect.height - r1.height) / 2;
		f1.setBounds(r1);

		IFigure f2 = list.get(2).getFigure();
		Rectangle r2 = r1.getTranslated(r1.width + buttonGap, 0);
		f2.setBounds(r2);

		ComponentPart hbox = list.get(0);
		IFigure f0 = hbox.getFigure();
		if (f0.getBorder() != null)
			f0.setBorder(null);
		f0.setBounds(new Rectangle(rect.x, rect.y, rect.width - r1.width * 2
				- buttonGap * 3, rect.height));
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
