package aurora.ide.meta.gef.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

import aurora.ide.libs.AuroraImagesUtils;
import aurora.plugin.source.gen.screen.model.StyledStringText;

public class TextStyleUtil {
	// TextStyle
	// TextLayout
	private static final int BOLD = SWT.BOLD;
	private static final int ITALIC = SWT.ITALIC;

	public static TextStyle createTextStyle(StyledStringText styledStringText,
			Display display, Font font) {
		// TextLayout result = new TextLayout();
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
		// String fontName = styledStringText.getFontName();
		// if (fontName != null && "".equals(fontName) == false
		// && styledStringText.getFontSize() > 0) {
		// textFont = new Font(display, fontName,
		// styledStringText.getFontSize(), SWT.NORMAL);
		// setStyle(FONT);
		// }

		// ts.metrics.
		// ts.
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
}
