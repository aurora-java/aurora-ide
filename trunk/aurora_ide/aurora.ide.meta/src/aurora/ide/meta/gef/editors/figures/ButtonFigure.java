package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

import aurora.ide.meta.gef.editors.ImagesUtils;
import aurora.ide.meta.gef.editors.models.Button;

/**
 */
public class ButtonFigure extends Figure {

	private static Image bgImg = ImagesUtils.getImage("btn.gif");
	private static String[] buttonTypes = { Button.ADD, Button.SAVE,
			Button.DELETE, Button.CLEAR, Button.EXCEL };
	private static Image stdimg = ImagesUtils
			.getImage("aurora/toolbar_btn.gif");
	private static Image defaultimg = ImagesUtils
			.getImage("aurora/default.gif");

	private Button model = null;

	public ButtonFigure() {
	}

	@Override
	public void handleFocusGained(FocusEvent event) {
		super.handleFocusGained(event);
	}

	protected void paintFigure(Graphics g) {
		super.paintFigure(g);
		g.pushState();
		Rectangle rect = getBounds();
		Dimension dim = model.getSize();
		IFigure parentFigure = getParent();
		if (!(parentFigure instanceof ToolbarFigure)) {
			g.drawImage(bgImg, 0, 0, 3, 2, rect.x, rect.y, 3, 2);// tl
			g.drawImage(bgImg, 0, 6, 1, 2, rect.x + 3, rect.y, dim.width - 6, 2);// tc
			g.drawImage(bgImg, 3, 0, 3, 2, rect.x + dim.width - 3, rect.y, 3, 2);// tr
			g.drawImage(bgImg, 0, 24, 3, 1, rect.x, rect.y + 2, 3,
					dim.height - 4);// ml
			g.drawImage(bgImg, 3, 24, 3, 1, rect.x + dim.width - 3, rect.y + 2,
					3, dim.height - 4);// mr
			g.drawImage(bgImg, 0, 1096, 1, dim.height - 4, rect.x + 3,
					rect.y + 2, dim.width - 6, dim.height - 4);// mc
			g.drawImage(bgImg, 0, 4, 3, 2, rect.x, rect.y + dim.height - 2, 3,
					2);// bl
			g.drawImage(bgImg, 0, 16, 1, 2, rect.x + 3,
					rect.y + dim.height - 2, dim.width - 3, 2);// bc
			g.drawImage(bgImg, 3, 4, 3, 2, rect.x + dim.width - 3, rect.y
					+ dim.height - 2, 3, 2);// br
		}
		String text = model.getText();
		Dimension textExtents = FigureUtilities.getTextExtents(text, getFont());
		Rectangle r1 = getStdImgRect();
		g.setForegroundColor(ColorConstants.BLACK);
		if (r1 == null) {
			g.drawString(text, rect.x + (dim.width - textExtents.width) / 2,
					rect.y + (dim.height - textExtents.height) / 2);
		} else {
			Rectangle r2 = new Rectangle(rect.x
					+ (dim.width - textExtents.width - 16) / 2, rect.y
					+ (dim.height - r1.height) / 2, 16, 17);
			g.drawImage(getBgImage(), r1, r2);
			g.drawString(text,
					rect.x + (dim.width - textExtents.width) / 2 + 8, rect.y
							+ (dim.height - textExtents.height) / 2);
		}
		g.popState();
	}

	private Rectangle getStdImgRect() {
		String btype = model.getButtonType();
		for (int i = 0; i < buttonTypes.length; i++) {
			if (buttonTypes[i].equals(btype)) {
				return new Rectangle(0, 17 * i, 16, 17);
			}
		}
		if (model.getIcon() == null || model.getIcon().length() == 0)
			return null;
		return new Rectangle(defaultimg.getBounds());
	}

	private Image getBgImage() {
		return model.isStdButton() ? stdimg : defaultimg;
	}

	public void setModel(Button model) {
		this.model = model;
	}

}
