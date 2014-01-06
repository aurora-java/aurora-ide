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
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.util.TextStyleUtil;
import aurora.plugin.source.gen.screen.model.Input;
import aurora.plugin.source.gen.screen.model.ScreenBody;
import aurora.plugin.source.gen.screen.model.StyledStringText;
import aurora.plugin.source.gen.screen.model.TextArea;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;
import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;

/**
 */
public class InputField extends Figure implements IResourceDispose {

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
		Rectangle textRectangle = getTextRectangle(ComponentProperties.prompt,
				getLabelRectangle());
		paintStyledText(graphics, prompt, ComponentProperties.prompt,
				textRectangle);
		Rectangle inputRectangle = getInputRectangle();
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

			if (TextStyleUtil.isTextLayoutUseless(this.model,
					ComponentInnerProperties.INPUT_SIMPLE_DATA) == false) {
				paintStyledText(graphics, sd,
						ComponentInnerProperties.INPUT_SIMPLE_DATA, r);
			} else {
				paintSimpleData(graphics,
						ComponentInnerProperties.INPUT_SIMPLE_DATA, r);
			}

		} else {
			paintEmptyText(graphics, model.getEmptyText(), r);
		}

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
		g.pushState();
		this.disposer.disposeResource(property_id);
		Rectangle copy = r.getCopy();
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
			Rectangle textRectangle = this.getTextRectangle(property_id, r);
			g.drawTextLayout(tl, textRectangle.x, textRectangle.y);
		}
		this.disposer.handleResource(property_id, tl);
		g.popState();
	}

	protected Rectangle getInputRectangle() {
		Rectangle inputRectangle = new Rectangle();
		inputRectangle.x = this.getBounds().x + this.getLabelWidth() + 1;
		inputRectangle.y = getBounds().y + 1;
		int j = getBounds().width - getLabelWidth() - 1;
		inputRectangle.width = j <= 0 ? 0 : j;
		inputRectangle.height = getBounds().height - 1;
		return inputRectangle;
	}

	protected Rectangle getTextRectangle(String property_id, Rectangle rect) {

		// String prompt = model.getPrompt() + " : ";
		// Dimension textExtents = FigureUtilities.getTextExtents(prompt,
		// getFont());
		// Rectangle textRectangle = new Rectangle();
		// int pWidth = this.getLabelWidth() - textExtents.width;
		// textRectangle.x = pWidth + getBounds().x;
		// int i = getBounds().height - textExtents.height;
		// textRectangle.y = i <= 0 ? getBounds().y : getBounds().y + i / 2;
		// textRectangle.setSize(textExtents);
		// return textRectangle;

		String text = model.getStringPropertyValue(property_id);
		if (ComponentProperties.prompt.equals(property_id)) {
			text += " : ";
		}
		if (ComponentInnerProperties.INPUT_SIMPLE_DATA.equals(property_id)) {
			rect = rect.getCopy().setWidth(rect.width-18);
		}
		Dimension textExtents = FigureUtilities.getTextExtents(text, getFont());
		Point point = TextStyleUtil.getTextAlignment(rect, text, getFont(),
				getAlignmentStyle(property_id));
		return new Rectangle(point, textExtents);
	}

	protected Rectangle getLabelRectangle() {
		Rectangle rect = getBounds().getCopy();
		rect.setWidth(getLabelWidth());
		return rect;
	}

	private int getAlignmentStyle(String property_id) {
		Object obj = model.getPropertyValue(property_id
				+ ComponentInnerProperties.TEXT_STYLE);
		if (obj instanceof StyledStringText) {
			return ((StyledStringText) obj).getAlignment();
		}
		return ComponentProperties.prompt.equals(property_id) ? SWT.RIGHT
				: SWT.LEFT;
	}

	private void paintEmptyText(Graphics g, String emptyText, Rectangle r) {
		g.pushState();
		g.setForegroundColor(ColorConstants.EDITOR_BORDER);
		g.setClip(r.getResized(-16, 0));
		g.drawString(emptyText, calTextLocation(emptyText, r));
		g.popState();
	}

	protected Point calTextLocation(String emptyText, Rectangle r) {
		Dimension dim = FigureUtilities.getTextExtents(emptyText, getFont());
		if (TextArea.TEXT_AREA.equals(model.getComponentType())) {
			return new Point(r.x + 2, r.y + 2);
		}
		return new Point(r.x + 2, r.y + (r.height - dim.height) / 2);
	}

	protected void paintSimpleData(Graphics g, String propertie_id, Rectangle r) {
		g.pushState();
		g.setForegroundColor(ColorConstants.BLACK);
		g.setClip(r.getResized(-16, 0));
		String text = model.getStringPropertyValue(propertie_id);
		g.drawText(text, getTextRectangle(propertie_id, r).getTopLeft());
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

	// public Rectangle getTextBounds() {
	// Rectangle textRectangle = getTextRectangle(ComponentProperties.prompt);
	// return textRectangle;
	// }

	private ResourceDisposer disposer = new ResourceDisposer();

	public void disposeResource() {
		disposer.disposeResource();
		disposer = null;
	}

	protected void handleResource(String id, Resource r) {
		disposer.handleResource(id, r);
	}

	protected void disposeResource(String prop_id) {
		disposer.disposeResource(prop_id);
	}

}
