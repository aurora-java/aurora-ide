package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import aurora.ide.meta.gef.editors.ImagesUtils;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.ViewDiagram;

/**
 */
public class InputField extends Figure {

	private Input model = null;

	public InputField() {
	}

	public void setModel(Input model) {
		this.model = model;
		setToolTip(new Label(model.getType()));
	}

	public int getLabelWidth() {
		IFigure parent = getParent();
		if (parent instanceof BoxFigure) {
			return ((BoxFigure) parent).getLabelWidth();
		} else {
			return ViewDiagram.DLabelWidth;
		}
	}

	@Override
	public void setBounds(Rectangle rect) {
		super.setBounds(rect);
	}

	@Override
	public void handleFocusGained(FocusEvent event) {
		super.handleFocusGained(event);

	}

	/**
	 * @see org.eclipse.draw2d.Label#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
		String prompt = model.getPrompt() + " : ";
		Dimension textExtents = FigureUtilities.getTextExtents(prompt,
				getFont());
		Rectangle textRectangle = new Rectangle();
		int pWidth = this.getLabelWidth() - textExtents.width;
		// if (pWidth < 0) {
		// prompt = prompt.substring(0, 3) + "...";
		// textExtents = FigureUtilities.getTextExtents(prompt, getFont());
		// pWidth = this.getLabelWidth() - textExtents.width;
		// }

		textRectangle.x = pWidth + getBounds().x;
		int i = getBounds().height - textExtents.height;
		textRectangle.y = i <= 0 ? getBounds().y : getBounds().y + i / 2;

		textRectangle.setSize(textExtents);

		graphics.drawText(prompt, textRectangle.getLocation());

		Rectangle inputRectangle = new Rectangle();

		inputRectangle.x = textRectangle.getTopRight().x + 1;
		inputRectangle.y = getBounds().y + 1;
		int j = getBounds().width - getLabelWidth() - 1;
		inputRectangle.width = j <= 0 ? 0 : j;
		inputRectangle.height = getBounds().height - 1;

		// FigureUtilities.paintEtchedBorder(graphics, inputRectangle);
		graphics.setForegroundColor(ColorConstants.EDITOR_BORDER);
		graphics.drawRectangle(inputRectangle.getResized(-1, -1));
		Rectangle r = inputRectangle.getTranslated(1, 1).getResized(-2, -2);
		Color bgColor = ColorConstants.WHITE;
		if (model.isRequired())
			bgColor = ColorConstants.REQUIRED_BG;
		if (model.isReadOnly())
			bgColor = ColorConstants.READONLY_BG;
		graphics.setBackgroundColor(bgColor);
		graphics.fillRectangle(r);
		paintEmptyText(graphics, model.getEmptyText(), r);
		Image image = getImage();

		if (image != null) {
			Rectangle imageR = inputRectangle.getCopy();
			graphics.drawImage(image, getImageLocation().x,
					getImageLocation().y, 16, 16, imageR.getTopRight().x - 18,
					imageR.getTopRight().y, 16, 16);
		}
	}

	private void paintEmptyText(Graphics g, String emptyText, Rectangle r) {
		g.pushState();
		g.setForegroundColor(ColorConstants.EDITOR_BORDER);
		Dimension dim = FigureUtilities.getTextExtents(emptyText, getFont());
		g.setClip(r.getResized(-16, 0));
		g.drawString(emptyText, r.x + 2, r.y + (r.height - dim.height) / 2);
		g.popState();
	}

	private Image getImage() {
		String type = model.getType();
		if (Input.Combo.equals(type))
			return ImagesUtils.getImage("palette/itembar_01.png");
		if (Input.CAL.equals(type) || Input.DATETIMEPICKER.equals(type))
			return ImagesUtils.getImage("palette/itembar_02.png");
		if (Input.LOV.equals(type))
			return ImagesUtils.getImage("palette/itembar_03.png");
		if (Input.TEXT.equals(type))
			return ImagesUtils.getImage("palette/itembar_04.png");
		if (Input.NUMBER.equals(type))
			return ImagesUtils.getImage("palette/itembar_05.png");
		return null;
	}

	private Point getImageLocation() {
		Point p = new Point(0, 0);
		// String type = model.getType();
		// if (Input.Combo.equals(type)) {
		// return p.setY(0);
		// }
		// if (Input.CAL.equals(type)) {
		// return p.setY(20);
		// }
		// if (Input.LOV.equals(type)) {
		// return p.setY(42);
		// }
		return p;
	}
}
