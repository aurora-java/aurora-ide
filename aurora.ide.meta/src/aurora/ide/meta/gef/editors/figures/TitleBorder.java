package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.TitleBarBorder;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Resource;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;

public class TitleBorder extends TitleBarBorder implements IResourceDispose{
//	private Insets padding = new Insets(2, 3, 2, 2);
	private String imageKey = "toolbar_bg";

	public TitleBorder(String string) {
		super(string);
	}

	public TitleBorder(String title, String imageKey) {
		super(title);
		this.imageKey = imageKey;
	}

	@Override
	public void paint(IFigure figure, Graphics g, Insets insets) {
		g.pushState();
		tempRect.setBounds(getPaintRectangle(figure, insets));

		paintEtchedBorder(g, tempRect);

		Rectangle rec = tempRect;
		rec.height = 25;
		g.clipRect(rec);

		g.fillRectangle(rec);
		Image i = getBGImage();
		Rectangle src = new Rectangle(i.getBounds().x, i.getBounds().y,
				i.getBounds().width, 25);
		g.drawImage(i, src, rec);

		int x = rec.x + 5;
		int y = rec.getCenter().y;

		y = y - getTextExtents(figure).height / 2;
		g.setFont(getFont(figure));
		g.setForegroundColor(getTextColor());
		g.drawString(getLabel(), x, y);

		paintEtchedBorder(g, tempRect);
		g.popState();
	}

	private Image getBGImage() {
		return PrototypeImagesUtils.getImage(imageKey);
	}

	@Override
	public Color getTextColor() {
		return ColorConstants.TITLETEXT;
	}
	
	private void paintEtchedBorder(Graphics g, Rectangle r){
		disposeResource("shadow");
		disposeResource("highlight");
		Color rgb = g.getBackgroundColor(), shadow = FigureUtilities.darker(rgb), highlight = FigureUtilities.lighter(rgb);
		handleResource("shadow",shadow);
		handleResource("highlight",highlight);
		FigureUtilities.paintEtchedBorder(g, r, shadow, highlight);
	}
	
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
