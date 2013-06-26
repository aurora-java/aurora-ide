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
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.util.TextStyleUtil;
import aurora.plugin.source.gen.screen.model.Input;
import aurora.plugin.source.gen.screen.model.ScreenBody;
import aurora.plugin.source.gen.screen.model.StyledStringText;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;
import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;

/**
 */
public class InputField extends Figure {

	private Input model = null;

	public InputField() {
	}

	public void setModel(Input model) {
		this.model = model;
		setToolTip(new Label(model.getComponentType()));
	}

	public int getLabelWidth() {
		IFigure parent = getParent();
		if (parent instanceof BoxFigure) {
			return ((BoxFigure) parent).getLabelWidth();
		} else {
			return ScreenBody.DLabelWidth;
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
		Rectangle textRectangle = getTextRectangle();
		paintStyledText(graphics, prompt, ComponentProperties.prompt,
				textRectangle);
		// graphics.drawText(prompt, textRectangle.getLocation());
		Rectangle inputRectangle = getInputRectangle(textRectangle);
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

		String sd = model
				.getStringPropertyValue(ComponentInnerProperties.INPUT_SIMPLE_DATA);
		if (sd != null && "".equals(sd) == false) {
			// paintSimpleData(graphics, sd, r);
			paintStyledText(graphics, sd,
					ComponentInnerProperties.INPUT_SIMPLE_DATA, r);
		} else {
			paintEmptyText(graphics, model.getEmptyText(), r);
		}
		// graphics.setForegroundColor(ColorConstants.EDITOR_BORDER);
		Image image = getImage();

		if (image != null) {
			Rectangle imageR = inputRectangle.getCopy();
			graphics.drawImage(image, getImageLocation().x,
					getImageLocation().y, 16, 16, imageR.getTopRight().x - 18,
					imageR.getTopRight().y, 16, 16);
		}
	}

	protected void paintStyledText(Graphics g, String text, String property_id,
			Rectangle r) {
		Rectangle copy = r.getCopy();
		g.pushState();
		g.setForegroundColor(ColorConstants.BLACK);
		Dimension dim = FigureUtilities.getTextExtents(text, getFont());
		// FigureUtilities.
		if (ComponentProperties.prompt.equals(property_id) == false)
			g.setClip(r.getResized(-16, 0));
		TextLayout tl = new TextLayout(null);
		tl.setText(text);
		tl.setFont(getFont());
		Object obj = model.getPropertyValue(property_id
				+ ComponentInnerProperties.TEXT_STYLE);
		TextStyle ts = null;
		if (obj instanceof StyledStringText) {
			ts = TextStyleUtil.createTextStyle((StyledStringText) obj,
					Display.getDefault(), getFont());
		} else {
			ts = new TextStyle();
		}
		tl.setStyle(ts, 0, text.length() - 1);
		Point p = new Point(r.x + 2, r.y + (r.height - dim.height) / 2);
		if (ComponentProperties.prompt.equals(property_id)) {
			g.drawTextLayout(tl, copy.x, copy.y);
		} else {
			g.drawTextLayout(tl, p.x, p.y);
		}
		g.popState();
	}

	protected Rectangle getInputRectangle(Rectangle textRectangle) {
		Rectangle inputRectangle = new Rectangle();
		inputRectangle.x = textRectangle.getTopRight().x + 1;
		inputRectangle.y = getBounds().y + 1;
		int j = getBounds().width - getLabelWidth() - 1;
		inputRectangle.width = j <= 0 ? 0 : j;
		inputRectangle.height = getBounds().height - 1;
		return inputRectangle;
	}

	private Rectangle getTextRectangle() {
		String prompt = model.getPrompt() + " : ";
		Dimension textExtents = FigureUtilities.getTextExtents(prompt,
				getFont());
		Rectangle textRectangle = new Rectangle();
		int pWidth = this.getLabelWidth() - textExtents.width;
		textRectangle.x = pWidth + getBounds().x;
		int i = getBounds().height - textExtents.height;
		textRectangle.y = i <= 0 ? getBounds().y : getBounds().y + i / 2;
		textRectangle.setSize(textExtents);
		return textRectangle;
	}

	private void paintEmptyText(Graphics g, String emptyText, Rectangle r) {
		g.pushState();
		g.setForegroundColor(ColorConstants.EDITOR_BORDER);
		Dimension dim = FigureUtilities.getTextExtents(emptyText, getFont());
		g.setClip(r.getResized(-16, 0));
		g.drawString(emptyText, r.x + 2, r.y + (r.height - dim.height) / 2);
		g.popState();
	}

	protected void paintSimpleData(Graphics g, String text, Rectangle r) {
		g.pushState();
		g.setForegroundColor(ColorConstants.BLACK);
		Dimension dim = FigureUtilities.getTextExtents(text, getFont());
		g.setClip(r.getResized(-16, 0));
		g.drawString(text, r.x + 2, r.y + (r.height - dim.height) / 2);
		g.popState();
	}

	private Image getImage() {
		String type = model.getComponentType();
		if (Input.Combo.equals(type))
			return PrototypeImagesUtils.getImage("palette/itembar_01.png");
		if (Input.DATE_PICKER.equals(type) || Input.DATETIMEPICKER.equals(type))
			return PrototypeImagesUtils.getImage("palette/itembar_02.png");
		if (Input.LOV.equals(type))
			return PrototypeImagesUtils.getImage("palette/itembar_03.png");
		if (Input.TEXT.equals(type))
			return PrototypeImagesUtils.getImage("palette/itembar_04.png");
		if (Input.NUMBER.equals(type))
			return PrototypeImagesUtils.getImage("palette/itembar_05.png");
		return null;
	}

	private Point getImageLocation() {
		Point p = new Point(0, 0);
		return p;
	}

	public Rectangle getTextBounds() {
		Rectangle textRectangle = getTextRectangle();
		return textRectangle;
	}

	public void setName(String value) {
	}
}
