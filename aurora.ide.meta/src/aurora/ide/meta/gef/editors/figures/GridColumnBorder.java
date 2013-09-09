package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.AbstractLabeledBorder;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.util.TextStyleUtil;
import aurora.plugin.source.gen.screen.model.StyledStringText;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;
import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;

public class GridColumnBorder extends AbstractLabeledBorder implements
		IResourceDispose {

	// private Insets padding = new Insets(1, 3, 2, 2);
	private String imageKey;
	private GridColumnFigure figure;

	public GridColumnBorder(String title, String imageKey,
			GridColumnFigure figure) {
		super(title);
		this.imageKey = imageKey;
		this.figure = figure;
	}

	private Image getImage(String key) {
		return PrototypeImagesUtils.getImage(key);
	}

	@Override
	public Color getTextColor() {
		return ColorConstants.TITLETEXT;
	}

	public GridColumnBorder() {
	}

	public void paint(IFigure figure, Graphics g, Insets insets) {
		g.pushState();
		Rectangle rect = figure.getBounds();
		g.clipRect(rect);
		Rectangle r = rect.getTranslated(-1, -1);
		g.setForegroundColor(ColorConstants.GRID_COLUMN_GRAY);
		g.drawRectangle(r);

		Image bgimg = getImage(imageKey);
		Rectangle headRect = rect.getCopy().setHeight(getColumnHight());
		IFigure gf = figure;
		while (gf.getParent() instanceof GridColumnFigure)
			gf = gf.getParent();
		Rectangle src = new Rectangle(0, rect.y - gf.getBounds().y, 1,
				getColumnHight());
		Rectangle imgBounds = new Rectangle(bgimg.getBounds());
		// keep the location of src ,but compute the intersect with imgbounds
		src = imgBounds.intersect(src).setLocation(src.getLocation());
		g.drawImage(bgimg, src, headRect.getCopy().setHeight(src.height));

		if(this.figure.getModel().getBooleanPropertyValue(ComponentInnerProperties.GRID_COLUMN_SORTABLE)){
			Image sortimg = getImage("palette/sort_desc.gif");
			
			Rectangle sortImgBounds = new Rectangle(sortimg.getBounds());
			
			g.drawImage(sortimg, sortImgBounds, new Rectangle(
					headRect.getTopRight().x - 15, headRect.getCenter().y,
					sortImgBounds.width, sortImgBounds.height));
		}

		Dimension textExtents = FigureUtilities.getTextExtents(getPrompt(),
				getFont(figure));
		g.setFont(getFont(figure));
		g.setForegroundColor(getTextColor());

		if (TextStyleUtil.isTextLayoutUseless(this.figure.getModel(),
				ComponentProperties.prompt) == false) {
			paintStyledText(g, getPrompt(), ComponentProperties.prompt,
					headRect.getCenter().x - textExtents.width / 2,
					headRect.getCenter().y - textExtents.height / 2);
		}

		else {
			g.drawString(getPrompt(), headRect.getCenter().x
					- textExtents.width / 2, headRect.getCenter().y
					- textExtents.height / 2);
		}

		g.setForegroundColor(ColorConstants.WHITE);
		g.drawRectangle(headRect);
		g.setForegroundColor(ColorConstants.GRID_COLUMN_GRAY);
		g.drawRectangle(headRect.getTranslated(-1, -1));
		g.popState();
	}

	protected void paintStyledText(Graphics g, String text, String property_id,
			int x, int y) {
		g.pushState();
		this.disposer.disposeResource(property_id);
		TextLayout tl = new TextLayout(null);
		tl.setText(text);
		tl.setFont(figure.getFont());
		Object obj = figure.getModel().getPropertyValue(
				property_id + ComponentInnerProperties.TEXT_STYLE);
		TextStyle ts = null;
		if (obj instanceof StyledStringText) {
			ts = TextStyleUtil.createTextStyle((StyledStringText) obj,
					Display.getDefault(), figure.getFont());
		} else {
			ts = new TextStyle();
		}
		tl.setStyle(ts, 0, text.length() - 1);
		g.drawTextLayout(tl, x, y);
		this.disposer.handleResource(property_id, tl);
		g.popState();
	}

	protected String getPrompt() {
		String prompt = this.figure.getPrompt();
		return prompt == null ? "prompt" : prompt;
	}

	private int getColumnHight() {
		return figure.getColumnHight();
	}

	@Override
	protected Insets calculateInsets(IFigure figure) {
		return new Insets(0, 0, 0, 0);
	}

	private ResourceDisposer disposer = new ResourceDisposer();

	public void disposeResource() {
		disposer.disposeResource();
		disposer = null;
	}
}
