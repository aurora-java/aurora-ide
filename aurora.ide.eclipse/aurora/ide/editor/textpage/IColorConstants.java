package aurora.ide.editor.textpage;

import org.eclipse.swt.graphics.RGB;

public interface IColorConstants {

	public static final RGB XML_COMMENT = new RGB(56, 94, 15);
	public static final RGB PROC_INSTR = new RGB(65, 105, 225);
	public static final RGB DOCTYPE = new RGB(0, 150, 150);
	public static final RGB STRING = new RGB(0, 0, 255);
	public static final RGB DEFAULT = new RGB(0, 0, 0);
	public static final RGB TAG = new RGB(0, 0, 128);

	// enhancements
	public static final RGB ESCAPED_CHAR = new RGB(128, 128, 0);
	public static final RGB CDATA = new RGB(0,255,0 );
	public static final RGB CDATA_TEXT = new RGB(0, 0, 0);
	public static final RGB TAG_NAME = new RGB(0, 128, 128);
	public static final RGB ATTRIBUTE = new RGB(135, 38, 87);// (128,0,0 );

	// js
	public static final RGB KEYWORD = new RGB(86, 0, 191);
	public static final RGB TYPE = new RGB(0, 0, 128);
	public static final RGB SINGLE_LINE_COMMENT = new RGB(56, 94, 15);

	// sql
	public static final RGB SQL_COMMENT_COLOR = new RGB(56, 94, 15); // dark blue
	public static final RGB SQL_MULTILINE_COMMENT_COLOR = new RGB(56, 94, 15); // dark
																				// red
	public static final RGB SQL_QUOTED_LITERAL_COLOR = new RGB(0, 0, 255); // bright
																			// blue
	public static final RGB SQL_KEYWORD_COLOR = new RGB(86, 0, 191); // dark red
	public static final RGB SQL_IDENTIFIER_COLOR = new RGB(0, 0, 128); // dark
																		// blue
	public static final RGB SQL_DELIMITED_IDENTIFIER_COLOR = new RGB(0, 128, 0); // dark
																					// green
	public static final RGB SQL_DEFAULT_COLOR = new RGB(0, 0, 0); // black

	// Define colors that can be used when the display is in "high contrast"
	// mode.
	// (High contrast is a Windows feature that helps vision impaired people.)
	public static final RGB SQL_HC_COMMENT_COLOR = new RGB(255, 0, 0); // bright
																		// red
	public static final RGB SQL_HC_MULTILINE_COMMENT_COLOR = new RGB(0, 0, 255); // bright
																					// blue
	public static final RGB SQL_HC_QUOTED_LITERAL_COLOR = new RGB(0, 255, 0); // bright
																				// green
	public static final RGB SQL_HC_KEYWORD_COLOR = new RGB(255, 255, 0); // yellow
	public static final RGB SQL_HC_IDENTIFIER_COLOR = new RGB(0, 0, 255); // bright
																			// blue
	public static final RGB SQL_HC_DELIMITED_IDENTIFIER_COLOR = new RGB(255, 0,
			0); // bright red
	public static final RGB SQL_HC_DEFAULT_COLOR = new RGB(255, 255, 255); // bright
																			// white

}