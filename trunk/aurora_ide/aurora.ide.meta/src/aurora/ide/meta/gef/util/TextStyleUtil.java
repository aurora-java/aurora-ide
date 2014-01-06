package aurora.ide.meta.gef.util;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

import aurora.ide.libs.AuroraImagesUtils;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.StyledStringText;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;

public class TextStyleUtil {
	// TextStyle
	// TextLayout
	private static final int BOLD = SWT.BOLD;
	private static final int ITALIC = SWT.ITALIC;

	public static TextStyle createTextStyle(StyledStringText styledStringText,
			Display display, Font font) {

		TextStyle ts = new TextStyle();

		if (styledStringText == null)
			return ts;
		int fontStyle = SWT.NORMAL;
		if (styledStringText.isBold()) {
			fontStyle = fontStyle | BOLD;
		}
		if (styledStringText.isItalic()) {
			fontStyle = fontStyle | ITALIC;
		}
		ts.font = new Font(display, font.getFontData()[0].getName(),
				font.getFontData()[0].getHeight(), fontStyle);
		if (styledStringText.isStrikeout()) {
			if (styledStringText.getStrikeoutColor() != null
					&& false == StyledStringText.UNAVAILABLE_RGB
							.equals(styledStringText.getStrikeoutColor())) {
				ts.strikeoutColor = new Color(display,
						AuroraImagesUtils.toRGB(styledStringText
								.getStrikeoutColor()));
			}
			ts.strikeout = true;
		}
		if (styledStringText.isUnderline()) {
			if (styledStringText.getUnderlineStyle() != SWT.UNDERLINE_LINK) {
				if (styledStringText.getUnderlineColor() != null
						&& false == StyledStringText.UNAVAILABLE_RGB
								.equals(styledStringText.getUnderlineColor())) {
					ts.underlineColor = new Color(display,
							AuroraImagesUtils.toRGB(styledStringText
									.getUnderlineColor()));
				}
			}
			ts.underlineStyle = styledStringText.getUnderlineStyle();
			ts.underline = styledStringText.isUnderline();
		}
		if (styledStringText.getTextBackground() != null
				&& false == StyledStringText.UNAVAILABLE_RGB
						.equals(styledStringText.getTextBackground())) {
			ts.background = new Color(display,
					AuroraImagesUtils.toRGB(styledStringText
							.getTextBackground()));
		}
		if (styledStringText.getTextForeground() != null
				&& false == StyledStringText.UNAVAILABLE_RGB
						.equals(styledStringText.getTextForeground())) {
			ts.foreground = new Color(display,
					AuroraImagesUtils.toRGB(styledStringText
							.getTextForeground()));
		}
		return ts;
	}

	public static boolean isTextLayoutUseless(AuroraComponent ac, String pro_id) {
		Object obj = ac.getPropertyValue(pro_id
				+ ComponentInnerProperties.TEXT_STYLE);
		if (obj instanceof StyledStringText
				&& ((StyledStringText) obj).isUseless() == false) {
			return false;
		}
		return true;
	}

	public static void dispose(TextStyle ts) {
		if (ts.font != null) {
			if (ts.font.isDisposed() == false)
				ts.font.dispose();
			ts.font = null;
		}
		if (ts.strikeoutColor != null) {
			if (ts.strikeoutColor.isDisposed() == false)
				ts.strikeoutColor.dispose();
			ts.strikeoutColor = null;
		}
		if (ts.underlineColor != null) {
			if (ts.underlineColor.isDisposed() == false)
				ts.underlineColor.dispose();
			ts.underlineColor = null;
		}
		if (ts.background != null) {
			if (ts.background.isDisposed() == false)
				ts.background.dispose();
			ts.background = null;
		}
		if (ts.foreground != null) {
			if (ts.foreground.isDisposed() == false)
				ts.foreground.dispose();
			ts.foreground = null;
		}
	}

	public static Point getTextAlignment(Rectangle rect, String text,
			Font font, int alignmentStyle) {
		Rectangle cr = rect.getCopy();
		Dimension textExtents = FigureUtilities.getTextExtents(text, font);
		int right = cr.x + cr.width - textExtents.width;
		int left = cr.x + 2;
		int center = cr.getCenter().x - textExtents.width / 2;
		center = cr.width - textExtents.width <= 0 ? left : center;
		int y = cr.height - textExtents.height;
		y = y <= 0 ? cr.y : cr.y + y / 2;
		int x = left;
		if ((SWT.RIGHT & alignmentStyle) != 0)
			x = right;
		if ((SWT.CENTER & alignmentStyle) != 0)
			x = center;
		return new Point(x, y);
	}
}
