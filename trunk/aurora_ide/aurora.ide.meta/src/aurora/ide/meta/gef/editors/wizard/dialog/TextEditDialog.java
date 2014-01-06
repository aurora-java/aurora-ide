package aurora.ide.meta.gef.editors.wizard.dialog;

import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import aurora.ide.libs.AuroraImagesUtils;
import aurora.plugin.source.gen.screen.model.StyledStringText;

public class TextEditDialog extends Dialog {

	private Display display;
	private Shell shell;
	private StyledStringText styledStringText;
	private int alignmentStyle;

	public TextEditDialog(Shell shell) {
		super(shell);
		display = shell.getDisplay();
		this.shell = shell;
	}

	// getDialogBoundsSettings()

	static final ResourceBundle resources = ResourceBundle
			.getBundle("aurora.ide.meta.gef.editors.wizard.dialog.examples_texteditor"); //$NON-NLS-1$

	static String getResourceString(String key) {
		try {
			return resources.getString(key);
		} catch (MissingResourceException e) {
			return key;
		} catch (NullPointerException e) {
			return "!" + key + "!";
		}
	}

	@Override
	protected Button createButton(Composite parent, int id, String label,
			boolean defaultButton) {
		return super.createButton(parent, id, label, defaultButton);
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		return super.createButtonBar(parent);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
	}

	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		this.getShell().setSize(600, 250);
	}

	@Override
	protected Control createContents(Composite parent) {
		return super.createContents(parent);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite dialogArea = (Composite) super.createDialogArea(parent);
		initResources();
		createToolbar(dialogArea);
		coolBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		styledText = new StyledText(dialogArea, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL);
		styledText.setLayoutData(new GridData(GridData.FILL_BOTH));
		styledText.setFocus();
		installListeners();
		initStyledText();
		updateToolBar();
		return dialogArea;
	}

	private void initStyledText() {
		if (styledStringText == null)
			return;
		String text = styledStringText.getText();
		if (text == null || "".equals(text))
			return;
		styledText.setText(text);
		String fontName = styledStringText.getFontName();
		if (fontName != null && "".equals(fontName) == false
				&& styledStringText.getFontSize() > 0) {
			textFont = new Font(Display.getDefault(), fontName,
					styledStringText.getFontSize(), SWT.NORMAL);
			setStyle(FONT);
		}

		if (styledStringText.isBold()) {
			setStyle(BOLD);
		}
		if (styledStringText.isItalic()) {
			setStyle(ITALIC);
		}
		if (styledStringText.isStrikeout()) {
			if (styledStringText.getStrikeoutColor() != null
					&& false == StyledStringText.UNAVAILABLE_RGB
							.equals(styledStringText.getStrikeoutColor())) {
				strikeoutColor = new Color(Display.getDefault(),
						AuroraImagesUtils.toRGB(styledStringText
								.getStrikeoutColor()));
			}
			setStyle(STRIKEOUT);
		}
		if (styledStringText.isUnderline()) {
			if (styledStringText.getUnderlineStyle() != SWT.UNDERLINE_LINK) {
				underlineSingleItem
						.setSelection((styleState & UNDERLINE_SINGLE) != 0);
				underlineDoubleItem
						.setSelection((styleState & UNDERLINE_DOUBLE) != 0);
				underlineErrorItem
						.setSelection((styleState & UNDERLINE_ERROR) != 0);
				underlineSquiggleItem
						.setSelection((styleState & UNDERLINE_SQUIGGLE) != 0);
				if (styledStringText.getUnderlineColor() != null
						&& false == StyledStringText.UNAVAILABLE_RGB
								.equals(styledStringText.getUnderlineColor())) {
					underlineColor = new Color(Display.getDefault(),
							AuroraImagesUtils.toRGB(styledStringText
									.getUnderlineColor()));
				}
			}
			switch (styledStringText.getUnderlineStyle()) {
			case SWT.UNDERLINE_SINGLE:
				setStyle(UNDERLINE_SINGLE);
				break;
			case SWT.UNDERLINE_DOUBLE:
				setStyle(UNDERLINE_DOUBLE);
				break;
			case SWT.UNDERLINE_SQUIGGLE:
				setStyle(UNDERLINE_SQUIGGLE);
				break;
			case SWT.UNDERLINE_ERROR:
				setStyle(UNDERLINE_ERROR);
				break;
			case SWT.UNDERLINE_LINK:
				setLink();
				break;
			}

		}
		if (styledStringText.getTextBackground() != null
				&& false == StyledStringText.UNAVAILABLE_RGB
						.equals(styledStringText.getTextBackground())) {
			textBackground = new Color(Display.getDefault(),
					AuroraImagesUtils.toRGB(styledStringText
							.getTextBackground()));
			setStyle(BACKGROUND);
		}
		if (styledStringText.getTextForeground() != null
				&& false == StyledStringText.UNAVAILABLE_RGB
						.equals(styledStringText.getTextForeground())) {
			textForeground = new Color(Display.getDefault(),
					AuroraImagesUtils.toRGB(styledStringText
							.getTextForeground()));
			setStyle(FOREGROUND);
		}
		// this.updateAlignmentStyle(styledStringText.getAlignment());
		// 16384
		int alignment = styledStringText.getAlignment();
		this.updateAlignmentStyle(alignment == -1 ? SWT.LEFT : alignment);
	}

	private void createToolbar(Composite parent) {
		final Composite shell = parent;
		coolBar = new CoolBar(parent, SWT.FLAT);
		ToolBar styleToolBar = new ToolBar(coolBar, SWT.FLAT);
		boldControl = new ToolItem(styleToolBar, SWT.CHECK);
		boldControl.setImage(iBold);
		boldControl.setToolTipText(getResourceString("Bold")); //$NON-NLS-1$
		boldControl.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				setStyle(BOLD);
			}
		});
		italicControl = new ToolItem(styleToolBar, SWT.CHECK);
		italicControl.setImage(iItalic);
		italicControl.setToolTipText(getResourceString("Italic")); //$NON-NLS-1$
		italicControl.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				setStyle(ITALIC);
			}
		});

		final Menu underlineMenu = new Menu(shell.getShell(), SWT.POP_UP);
		underlineSingleItem = new MenuItem(underlineMenu, SWT.RADIO);
		underlineSingleItem.setText(getResourceString("Single_menuitem")); //$NON-NLS-1$
		underlineSingleItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (underlineSingleItem.getSelection()) {
					setStyle(UNDERLINE_SINGLE);
				}
			}
		});
		underlineSingleItem.setSelection(true);

		underlineDoubleItem = new MenuItem(underlineMenu, SWT.RADIO);
		underlineDoubleItem.setText(getResourceString("Double_menuitem")); //$NON-NLS-1$
		underlineDoubleItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (underlineDoubleItem.getSelection()) {
					setStyle(UNDERLINE_DOUBLE);
				}
			}
		});

		underlineSquiggleItem = new MenuItem(underlineMenu, SWT.RADIO);
		underlineSquiggleItem.setText(getResourceString("Squiggle_menuitem")); //$NON-NLS-1$
		underlineSquiggleItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (underlineSquiggleItem.getSelection()) {
					setStyle(UNDERLINE_SQUIGGLE);
				}
			}
		});

		underlineErrorItem = new MenuItem(underlineMenu, SWT.RADIO);
		underlineErrorItem.setText(getResourceString("Error_menuitem")); //$NON-NLS-1$
		underlineErrorItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (underlineErrorItem.getSelection()) {
					setStyle(UNDERLINE_ERROR);
				}
			}
		});

		MenuItem underlineColorItem = new MenuItem(underlineMenu, SWT.PUSH);
		underlineColorItem.setText(getResourceString("Color_menuitem")); //$NON-NLS-1$
		underlineColorItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				ColorDialog dialog = new ColorDialog(shell.getShell());
				RGB rgb = underlineColor != null ? underlineColor.getRGB()
						: null;
				dialog.setRGB(rgb);
				RGB newRgb = dialog.open();
				if (newRgb != null) {
					if (!newRgb.equals(rgb)) {
						disposeResource(underlineColor);
						underlineColor = new Color(Display.getDefault(), newRgb);
					}
					if (underlineSingleItem.getSelection())
						setStyle(UNDERLINE_SINGLE);
					else if (underlineDoubleItem.getSelection())
						setStyle(UNDERLINE_DOUBLE);
					else if (underlineErrorItem.getSelection())
						setStyle(UNDERLINE_ERROR);
					else if (underlineSquiggleItem.getSelection())
						setStyle(UNDERLINE_SQUIGGLE);
				}
			}
		});

		final ToolItem underlineControl = new ToolItem(styleToolBar,
				SWT.DROP_DOWN);
		underlineControl.setImage(iUnderline);
		underlineControl.setToolTipText(getResourceString("Underline")); //$NON-NLS-1$
		underlineControl.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (event.detail == SWT.ARROW) {
					Rectangle rect = underlineControl.getBounds();
					Point pt = new Point(rect.x, rect.y + rect.height);
					underlineMenu.setLocation(display.map(
							underlineControl.getParent(), null, pt));
					underlineMenu.setVisible(true);
				} else {
					if (underlineSingleItem.getSelection())
						setStyle(UNDERLINE_SINGLE);
					else if (underlineDoubleItem.getSelection())
						setStyle(UNDERLINE_DOUBLE);
					else if (underlineErrorItem.getSelection())
						setStyle(UNDERLINE_ERROR);
					else if (underlineSquiggleItem.getSelection())
						setStyle(UNDERLINE_SQUIGGLE);
				}
			}
		});

		ToolItem strikeoutControl = new ToolItem(styleToolBar, SWT.DROP_DOWN);
		strikeoutControl.setImage(iStrikeout);
		strikeoutControl.setToolTipText(getResourceString("Strikeout")); //$NON-NLS-1$
		strikeoutControl.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (event.detail == SWT.ARROW) {
					ColorDialog dialog = new ColorDialog(shell.getShell());
					RGB rgb = strikeoutColor != null ? strikeoutColor.getRGB()
							: null;
					dialog.setRGB(rgb);
					RGB newRgb = dialog.open();
					if (newRgb == null)
						return;
					if (!newRgb.equals(rgb)) {
						disposeResource(strikeoutColor);
						strikeoutColor = new Color(Display.getDefault(), newRgb);
					}
				}
				setStyle(STRIKEOUT);
			}
		});

		ToolItem foregroundItem = new ToolItem(styleToolBar, SWT.DROP_DOWN);
		foregroundItem.setImage(iTextForeground);
		foregroundItem.setToolTipText(getResourceString("TextForeground")); //$NON-NLS-1$
		foregroundItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (event.detail == SWT.ARROW || textForeground == null) {
					ColorDialog dialog = new ColorDialog(shell.getShell());
					RGB rgb = textForeground != null ? textForeground.getRGB()
							: null;
					dialog.setRGB(rgb);
					RGB newRgb = dialog.open();
					if (newRgb == null)
						return;
					if (!newRgb.equals(rgb)) {
						disposeResource(textForeground);
						textForeground = new Color(Display.getDefault(), newRgb);
					}
				}
				setStyle(FOREGROUND);
			}
		});

		ToolItem backgroundItem = new ToolItem(styleToolBar, SWT.DROP_DOWN);
		backgroundItem.setImage(iTextBackground);
		backgroundItem.setToolTipText(getResourceString("TextBackground")); //$NON-NLS-1$
		backgroundItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (event.detail == SWT.ARROW || textBackground == null) {
					ColorDialog dialog = new ColorDialog(shell.getShell());
					RGB rgb = textBackground != null ? textBackground.getRGB()
							: null;
					dialog.setRGB(rgb);
					RGB newRgb = dialog.open();
					if (newRgb == null)
						return;
					if (!newRgb.equals(rgb)) {
						disposeResource(textBackground);
						textBackground = new Color(Display.getDefault(), newRgb);
					}
				}
				setStyle(BACKGROUND);
			}
		});

		ToolItem linkItem = new ToolItem(styleToolBar, SWT.PUSH);
		linkItem.setImage(iLink);
		linkItem.setToolTipText(getResourceString("Link")); //$NON-NLS-1$
		linkItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				setLink();
			}
		});
		CoolItem coolItem = new CoolItem(coolBar, SWT.NONE);
		coolItem.setControl(styleToolBar);

		// Composite composite = new Composite(coolBar, SWT.NONE);
		// GridLayout layout = new GridLayout(2, false);
		// layout.marginHeight = 1;
		// composite.setLayout(layout);
		// fontNameControl = new Combo(composite, SWT.DROP_DOWN |
		// SWT.READ_ONLY);
		// fontNameControl.setItems(getFontNames());
		// fontNameControl.setVisibleItemCount(12);
		// fontSizeControl = new Combo(composite, SWT.DROP_DOWN |
		// SWT.READ_ONLY);
		// fontSizeControl.setItems(FONT_SIZES);
		// fontSizeControl.setVisibleItemCount(8);
		// SelectionAdapter adapter = new SelectionAdapter() {
		// public void widgetSelected(SelectionEvent event) {
		// String name = fontNameControl.getText();
		// int size = Integer.parseInt(fontSizeControl.getText());
		// disposeResource(textFont);
		// textFont = new Font(display, name, size, SWT.NORMAL);
		// setStyle(FONT);
		// }
		// };
		// fontSizeControl.addSelectionListener(adapter);
		// fontNameControl.addSelectionListener(adapter);
		// coolItem = new CoolItem(coolBar, SWT.NONE);
		// coolItem.setControl(composite);

		ToolBar alignmentToolBar = new ToolBar(coolBar, SWT.FLAT);
		leftAlignmentItem = new ToolItem(alignmentToolBar, SWT.RADIO);
		leftAlignmentItem.setImage(iLeftAlignment);
		leftAlignmentItem.setToolTipText(getResourceString("AlignLeft")); //$NON-NLS-1$
		leftAlignmentItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				updateAlignmentStyle(SWT.LEFT);
			}
		});

		centerAlignmentItem = new ToolItem(alignmentToolBar, SWT.RADIO);
		centerAlignmentItem.setImage(iCenterAlignment);
		centerAlignmentItem
				.setToolTipText(getResourceString("Center_menuitem")); //$NON-NLS-1$
		centerAlignmentItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				updateAlignmentStyle(SWT.CENTER);
			}
		});

		rightAlignmentItem = new ToolItem(alignmentToolBar, SWT.RADIO);
		rightAlignmentItem.setImage(iRightAlignment);
		rightAlignmentItem.setToolTipText(getResourceString("AlignRight")); //$NON-NLS-1$
		rightAlignmentItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				updateAlignmentStyle(SWT.RIGHT);
			}
		});

		coolItem = new CoolItem(coolBar, SWT.NONE);
		coolItem.setControl(alignmentToolBar);

		CoolItem[] coolItems = coolBar.getItems();
		for (int i = 0; i < coolItems.length; i++) {
			CoolItem item = coolItems[i];
			Control control = item.getControl();
			Point size = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			item.setMinimumSize(size);
			size = item.computeSize(size.x, size.y);
			item.setPreferredSize(size);
			item.setSize(size);
		}
		// coolBar.addControlListener(new ControlAdapter() {
		// public void controlResized(ControlEvent event) {
		// handleResize(event);
		// }
		// });

	}

	protected void updateAlignmentStyle(int style) {
		// Point selection = styledText.getSelection();
		// int lineStart = styledText.getLineAtOffset(selection.x);
		// int lineEnd = styledText.getLineAtOffset(selection.y);
		// styledText.setLineAlignment(lineStart, lineEnd - lineStart + 1,
		// style);
		// styledText.setAlignment(style);
		alignmentStyle = style;
		this.leftAlignmentItem.setSelection((SWT.LEFT & style) != 0);
		this.centerAlignmentItem.setSelection((SWT.CENTER & style) != 0);
		this.rightAlignmentItem.setSelection((SWT.RIGHT & style) != 0);
	}

	@Override
	protected void okPressed() {
		styledStringText = createStyledStringText();
		super.okPressed();
	}

	@Override
	public boolean close() {
		releaseResources();
		return super.close();
	}

	private CoolBar coolBar;
	private StyledText styledText;
	private ToolItem boldControl, italicControl, leftAlignmentItem,
			centerAlignmentItem, rightAlignmentItem;
	// private Combo fontNameControl, fontSizeControl;
	private MenuItem underlineSingleItem, underlineDoubleItem,
			underlineErrorItem, underlineSquiggleItem, borderSolidItem,
			borderDashItem, borderDotItem;

	private boolean insert = true;
	private StyleRange[] selectedRanges;
	private int newCharCount, start;
	private int styleState;
	private String link;

	// Resources
	static private Image iBold, iItalic, iUnderline, iStrikeout,
			iLeftAlignment, iRightAlignment, iCenterAlignment,
			iJustifyAlignment, iCopy, iCut, iLink;
	static private Image iPaste, iTextForeground, iTextBackground, iBaselineUp,
			iBaselineDown, iBulletList, iNumberedList, iBlockSelection,
			iBorderStyle;
	private Font font, textFont;
	private Color textForeground, textBackground, strikeoutColor,
			underlineColor, borderColor;

	// private static final int MARGIN = 5;
	private static final int BOLD = SWT.BOLD;
	private static final int ITALIC = SWT.ITALIC;
	private static final int FONT_STYLE = BOLD | ITALIC;
	private static final int STRIKEOUT = 1 << 3;
	private static final int FOREGROUND = 1 << 4;
	private static final int BACKGROUND = 1 << 5;
	private static final int FONT = 1 << 6;
	private static final int BASELINE_UP = 1 << 7;
	private static final int BASELINE_DOWN = 1 << 8;
	private static final int UNDERLINE_SINGLE = 1 << 9;
	private static final int UNDERLINE_DOUBLE = 1 << 10;
	private static final int UNDERLINE_ERROR = 1 << 11;
	private static final int UNDERLINE_SQUIGGLE = 1 << 12;
	private static final int UNDERLINE_LINK = 1 << 13;
	private static final int UNDERLINE = UNDERLINE_SINGLE | UNDERLINE_DOUBLE
			| UNDERLINE_SQUIGGLE | UNDERLINE_ERROR | UNDERLINE_LINK;
	private static final int BORDER_SOLID = 1 << 23;
	private static final int BORDER_DASH = 1 << 24;
	private static final int BORDER_DOT = 1 << 25;
	private static final int BORDER = BORDER_SOLID | BORDER_DASH | BORDER_DOT;

	//	private static final String[] FONT_SIZES = new String[] { "6", //$NON-NLS-1$
	//			"8", //$NON-NLS-1$
	//			"9", //$NON-NLS-1$
	//			"10", //$NON-NLS-1$
	//			"11", //$NON-NLS-1$
	//			"12", //$NON-NLS-1$
	//			"14", //$NON-NLS-1$
	//			"24", //$NON-NLS-1$
	//			"36", //$NON-NLS-1$
	//			"48" //$NON-NLS-1$
	// };

	private void disposeRanges(StyleRange[] ranges) {
		StyleRange[] allRanges = styledText.getStyleRanges(0,
				styledText.getCharCount(), false);
		for (int i = 0; i < ranges.length; i++) {
			StyleRange style = ranges[i];
			boolean disposeFg = true, disposeBg = true, disposeStrike = true, disposeUnder = true, disposeBorder = true, disposeFont = true;

			for (int j = 0; j < allRanges.length; j++) {
				StyleRange s = allRanges[j];
				if (disposeFont && style.font == s.font)
					disposeFont = false;
				if (disposeFg && style.foreground == s.foreground)
					disposeFg = false;
				if (disposeBg && style.background == s.background)
					disposeBg = false;
				if (disposeStrike && style.strikeoutColor == s.strikeoutColor)
					disposeStrike = false;
				if (disposeUnder && style.underlineColor == s.underlineColor)
					disposeUnder = false;
				if (disposeBorder && style.borderColor == s.borderColor)
					disposeBorder = false;
			}
			if (disposeFont && style.font != textFont && style.font != null)
				style.font.dispose();
			if (disposeFg && style.foreground != textForeground
					&& style.foreground != null)
				style.foreground.dispose();
			if (disposeBg && style.background != textBackground
					&& style.background != null)
				style.background.dispose();
			if (disposeStrike && style.strikeoutColor != strikeoutColor
					&& style.strikeoutColor != null)
				style.strikeoutColor.dispose();
			if (disposeUnder && style.underlineColor != underlineColor
					&& style.underlineColor != null)
				style.underlineColor.dispose();
			if (disposeBorder && style.borderColor != borderColor
					&& style.borderColor != null)
				style.borderColor.dispose();

			Object data = style.data;
			if (data != null) {
				if (data instanceof Image)
					((Image) data).dispose();
				if (data instanceof Control)
					((Control) data).dispose();
			}
		}
	}

	private void disposeResource(Resource resource) {
		if (resource == null)
			return;
		StyleRange[] styles = styledText.getStyleRanges(0,
				styledText.getCharCount(), false);
		int index = 0;
		while (index < styles.length) {
			if (styles[index].font == resource)
				break;
			if (styles[index].foreground == resource)
				break;
			if (styles[index].background == resource)
				break;
			if (styles[index].strikeoutColor == resource)
				break;
			if (styles[index].underlineColor == resource)
				break;
			if (styles[index].borderColor == resource)
				break;
			index++;
		}
		if (index == styles.length)
			resource.dispose();
	}

	// private String[] getFontNames() {
	// FontData[] fontNames = display.getFontList(null, true);
	// String[] names = new String[fontNames.length];
	// int count = 0;
	// mainfor: for (int i = 0; i < fontNames.length; i++) {
	// String fontName = fontNames[i].getName();
	//			if (fontName.startsWith("@")) //$NON-NLS-1$
	// continue;
	// for (int j = 0; j < count; j++) {
	// if (names[j].equals(fontName))
	// continue mainfor;
	// }
	// names[count++] = fontName;
	// }
	// if (count < names.length) {
	// String[] newNames = new String[count];
	// System.arraycopy(names, 0, newNames, 0, count);
	// names = newNames;
	// }
	// return names;
	// }

	private void handleKeyDown(Event event) {
		if (event.keyCode == SWT.INSERT) {
			insert = !insert;
		}
	}

	private void handleModify(ModifyEvent event) {
		if (newCharCount > 0 && start >= 0) {
			StyleRange style = new StyleRange();
			if (textFont != null && !textFont.equals(styledText.getFont())) {
				style.font = textFont;
			}
			style.fontStyle = SWT.NONE;
			if (boldControl.getSelection())
				style.fontStyle |= SWT.BOLD;
			if (italicControl.getSelection())
				style.fontStyle |= SWT.ITALIC;

			if ((styleState & FOREGROUND) != 0) {
				style.foreground = textForeground;
			}
			if ((styleState & BACKGROUND) != 0) {
				style.background = textBackground;
			}
			int underlineStyle = styleState & UNDERLINE;
			if (underlineStyle != 0) {
				style.underline = true;
				style.underlineColor = underlineColor;
				switch (underlineStyle) {
				case UNDERLINE_SINGLE:
					style.underlineStyle = SWT.UNDERLINE_SINGLE;
					break;
				case UNDERLINE_DOUBLE:
					style.underlineStyle = SWT.UNDERLINE_DOUBLE;
					break;
				case UNDERLINE_SQUIGGLE:
					style.underlineStyle = SWT.UNDERLINE_SQUIGGLE;
					break;
				case UNDERLINE_ERROR:
					style.underlineStyle = SWT.UNDERLINE_ERROR;
					break;
				case UNDERLINE_LINK: {
					style.underlineColor = null;
					if (link != null && link.length() > 0) {
						style.underlineStyle = SWT.UNDERLINE_LINK;
						style.data = link;
					} else {
						style.underline = false;
					}
					break;
				}
				}
			}
			if ((styleState & STRIKEOUT) != 0) {
				style.strikeout = true;
				style.strikeoutColor = strikeoutColor;
			}
			int borderStyle = styleState & BORDER;
			if (borderStyle != 0) {
				style.borderColor = borderColor;
				switch (borderStyle) {
				case BORDER_DASH:
					style.borderStyle = SWT.BORDER_DASH;
					break;
				case BORDER_DOT:
					style.borderStyle = SWT.BORDER_DOT;
					break;
				case BORDER_SOLID:
					style.borderStyle = SWT.BORDER_SOLID;
					break;
				}
			}
			StyleRange[] styles = { style };
			// int[] ranges = { start, newCharCount };
			// styledText.setStyleRanges(start, newCharCount, ranges, styles);
			int[] ranges = { 0, styledText.getText().length() };
			styledText.setStyleRanges(0, styledText.getText().length(), ranges,
					styles);
		}
		disposeRanges(selectedRanges);
	}

	// private void handleMouseUp(Event event) {
	// if (link != null) {
	// int offset = styledText.getCaretOffset();
	// StyleRange range = offset > 0 ? styledText
	// .getStyleRangeAtOffset(offset - 1) : null;
	// if (range != null) {
	// if (link == range.data) {
	// Shell dialog = new Shell(shell);
	// dialog.setLayout(new FillLayout());
	//					dialog.setText(getResourceString("Browser")); //$NON-NLS-1$
	// Browser browser = new Browser(dialog, SWT.MOZILLA);
	// browser.setUrl(link);
	// dialog.open();
	// }
	// }
	// }
	// }

	// private void handlePaintObject(PaintObjectEvent event) {
	// GC gc = event.gc;
	// StyleRange style = event.style;
	// Object data = style.data;
	// if (data instanceof Image) {
	// Image image = (Image) data;
	// int x = event.x;
	// int y = event.y + event.ascent - style.metrics.ascent;
	// gc.drawImage(image, x, y);
	// }
	// if (data instanceof Control) {
	// Control control = (Control) data;
	// Point pt = control.getSize();
	// int x = event.x + MARGIN;
	// int y = event.y + event.ascent - 2 * pt.y / 3;
	// control.setLocation(x, y);
	// }
	// }

	// private void handleResize(ControlEvent event) {
	// Rectangle rect = shell.getClientArea();
	// Point cSize = coolBar.computeSize(rect.width, SWT.DEFAULT);
	// int statusMargin = 2;
	// coolBar.setBounds(rect.x, rect.y, cSize.x, cSize.y);
	// styledText.setBounds(rect.x, rect.y + cSize.y, rect.width,
	// rect.height
	// - cSize.y - (sSize.y + 2 * statusMargin));
	// }

	private void handleVerifyText(VerifyEvent event) {
		start = event.start;
		newCharCount = event.text.length();
		int replaceCharCount = event.end - start;

		// mark styles to be disposed
		selectedRanges = styledText.getStyleRanges(start, replaceCharCount,
				false);
	}

	private void initResources() {
		iBold = iBold == null ? loadImage(Display.getDefault(), "bold") : iBold; //$NON-NLS-1$
		iItalic = iItalic == null ? loadImage(Display.getDefault(), "italic") : iItalic; //$NON-NLS-1$
		iUnderline = iUnderline == null ? loadImage(Display.getDefault(),
				"underline") : iUnderline; //$NON-NLS-1$
		iStrikeout = iStrikeout == null ? loadImage(Display.getDefault(),
				"strikeout") : iStrikeout; //$NON-NLS-1$
		iBlockSelection = iBlockSelection == null ? loadImage(
				Display.getDefault(), "fullscrn") : iBlockSelection; //$NON-NLS-1$
		iBorderStyle = iBorderStyle == null ? loadImage(Display.getDefault(),
				"resize") : iBorderStyle; //$NON-NLS-1$
		iLeftAlignment = iLeftAlignment == null ? loadImage(
				Display.getDefault(), "left") : iLeftAlignment; //$NON-NLS-1$
		iRightAlignment = iRightAlignment == null ? loadImage(
				Display.getDefault(), "right") : iRightAlignment; //$NON-NLS-1$
		iCenterAlignment = iCenterAlignment == null ? loadImage(
				Display.getDefault(), "center") : iCenterAlignment; //$NON-NLS-1$
		iJustifyAlignment = iJustifyAlignment == null ? loadImage(
				Display.getDefault(), "justify") : iJustifyAlignment; //$NON-NLS-1$
		iCut = iCut == null ? loadImage(Display.getDefault(), "cut") : iCut; //$NON-NLS-1$
		iCopy = iCopy == null ? loadImage(Display.getDefault(), "copy") : iCopy; //$NON-NLS-1$
		iPaste = iPaste == null ? loadImage(Display.getDefault(), "paste") : iPaste; //$NON-NLS-1$
		iTextForeground = iTextForeground == null ? loadImage(
				Display.getDefault(), "textForeground") : iTextForeground; //$NON-NLS-1$
		iTextBackground = iTextBackground == null ? loadImage(
				Display.getDefault(), "textBackground") : iTextBackground; //$NON-NLS-1$
		iBaselineUp = iBaselineUp == null ? loadImage(Display.getDefault(),
				"font_big") : iBaselineUp; //$NON-NLS-1$
		iBaselineDown = iBaselineDown == null ? loadImage(Display.getDefault(),
				"font_sml") : iBaselineDown; //$NON-NLS-1$
		iBulletList = iBulletList == null ? loadImage(Display.getDefault(),
				"para_bul") : iBulletList; //$NON-NLS-1$
		iNumberedList = iNumberedList == null ? loadImage(Display.getDefault(),
				"para_num") : iNumberedList; //$NON-NLS-1$
		iLink = iLink == null ? new Image(Display.getDefault(), getClass()
				.getResourceAsStream("link_obj.gif")) : iLink; //$NON-NLS-1$
	}

	private void installListeners() {
		styledText.addCaretListener(new CaretListener() {
			public void caretMoved(CaretEvent event) {
				// updateToolBar();
			}
		});
		// styledText.addListener(SWT.MouseUp, new Listener() {
		// public void handleEvent(Event event) {
		// handleMouseUp(event);
		// }
		// });
		styledText.addListener(SWT.KeyDown, new Listener() {
			public void handleEvent(Event event) {
				handleKeyDown(event);
			}
		});
		styledText.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent event) {
				handleVerifyText(event);
			}
		});
		styledText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				handleModify(event);
			}
		});
		// styledText.addPaintObjectListener(new PaintObjectListener() {
		// public void paintObject(PaintObjectEvent event) {
		// handlePaintObject(event);
		// }
		// });
		// styledText.addListener(SWT.Dispose, new Listener() {
		// public void handleEvent(Event event) {
		// StyleRange[] styles = styledText.getStyleRanges(0,
		// styledText.getCharCount(), false);
		// for (int i = 0; i < styles.length; i++) {
		// Object data = styles[i].data;
		// if (data != null) {
		// if (data instanceof Image)
		// ((Image) data).dispose();
		// if (data instanceof Control)
		// ((Control) data).dispose();
		// }
		// }
		// }
		// });
		// shell.addControlListener(new ControlAdapter() {
		// public void controlResized(ControlEvent event) {
		// handleResize(event);
		// }
		// });
	}

	private Image loadImage(Display display, String fileName) {
		Image image = null;
		try {
			InputStream sourceStream = getClass().getResourceAsStream(
					fileName + ".ico"); //$NON-NLS-1$ //$NON-NLS-2$
			ImageData source = new ImageData(sourceStream);
			ImageData mask = source.getTransparencyMask();
			image = new Image(display, source, mask);
			sourceStream.close();
		} catch (IOException e) {
			showError(getResourceString("Error"), e.getMessage()); //$NON-NLS-1$
		}
		return image;
	}

	private void releaseResources() {
		// iBold.dispose();
		// iBold = null;
		// iItalic.dispose();
		// iItalic = null;
		// iUnderline.dispose();
		// iUnderline = null;
		// iStrikeout.dispose();
		// iStrikeout = null;
		// iBorderStyle.dispose();
		// iBorderStyle = null;
		// iBlockSelection.dispose();
		// iBlockSelection = null;
		// iLeftAlignment.dispose();
		// iLeftAlignment = null;
		// iRightAlignment.dispose();
		// iRightAlignment = null;
		// iCenterAlignment.dispose();
		// iCenterAlignment = null;
		// iJustifyAlignment.dispose();
		// iJustifyAlignment = null;
		// iCut.dispose();
		// iCut = null;
		// iCopy.dispose();
		// iCopy = null;
		// iPaste.dispose();
		// iPaste = null;
		// iTextForeground.dispose();
		// iTextForeground = null;
		// iTextBackground.dispose();
		// iTextBackground = null;
		// iBaselineUp.dispose();
		// iBaselineUp = null;
		// iBaselineDown.dispose();
		// iBaselineDown = null;
		// iBulletList.dispose();
		// iBulletList = null;
		// iNumberedList.dispose();
		// iNumberedList = null;
		// iLink.dispose();
		// iLink = null;

		if (textFont != null)
			textFont.dispose();
		textFont = null;
		if (textForeground != null)
			textForeground.dispose();
		textForeground = null;
		if (textBackground != null)
			textBackground.dispose();
		textBackground = null;
		if (strikeoutColor != null)
			strikeoutColor.dispose();
		strikeoutColor = null;
		if (underlineColor != null)
			underlineColor.dispose();
		underlineColor = null;
		if (borderColor != null)
			borderColor.dispose();
		borderColor = null;

		if (font != null)
			font.dispose();
		font = null;
	}

	private void setStyle(int style) {
		// int[] ranges = styledText.getSelectionRanges();
		// int i = 0;
		// while (i < ranges.length) {
		// setStyle(style, ranges[i++], ranges[i++]);
		// }
		setStyle(style, 0, styledText.getText().length());
		updateStyleState(style, FOREGROUND);
		updateStyleState(style, BACKGROUND);
		updateStyleState(style, UNDERLINE);
		updateStyleState(style, STRIKEOUT);
		updateStyleState(style, BORDER);
	}

	private void setStyle(int style, int start, int length) {
		if (length == 0)
			return;

		/* Create new style range */
		StyleRange newRange = new StyleRange();
		if ((style & FONT) != 0) {
			newRange.font = textFont;
		}
		if ((style & FONT_STYLE) != 0) {
			newRange.fontStyle = style & FONT_STYLE;
		}
		if ((style & FOREGROUND) != 0) {
			newRange.foreground = textForeground;
		}
		if ((style & BACKGROUND) != 0) {
			newRange.background = textBackground;
		}
		if ((style & BASELINE_UP) != 0)
			newRange.rise++;
		if ((style & BASELINE_DOWN) != 0)
			newRange.rise--;
		if ((style & STRIKEOUT) != 0) {
			newRange.strikeout = true;
			newRange.strikeoutColor = strikeoutColor;
		}
		if ((style & UNDERLINE) != 0) {
			newRange.underline = true;
			newRange.underlineColor = underlineColor;
			switch (style & UNDERLINE) {
			case UNDERLINE_SINGLE:
				newRange.underlineStyle = SWT.UNDERLINE_SINGLE;
				break;
			case UNDERLINE_DOUBLE:
				newRange.underlineStyle = SWT.UNDERLINE_DOUBLE;
				break;
			case UNDERLINE_ERROR:
				newRange.underlineStyle = SWT.UNDERLINE_ERROR;
				break;
			case UNDERLINE_SQUIGGLE:
				newRange.underlineStyle = SWT.UNDERLINE_SQUIGGLE;
				break;
			case UNDERLINE_LINK:
				newRange.underlineColor = null;
				if (link != null && link.length() > 0) {
					newRange.underlineStyle = SWT.UNDERLINE_LINK;
					newRange.data = link;
				} else {
					newRange.underline = false;
				}
				break;
			}
		}
		if ((style & BORDER) != 0) {
			switch (style & BORDER) {
			case BORDER_DASH:
				newRange.borderStyle = SWT.BORDER_DASH;
				break;
			case BORDER_DOT:
				newRange.borderStyle = SWT.BORDER_DOT;
				break;
			case BORDER_SOLID:
				newRange.borderStyle = SWT.BORDER_SOLID;
				break;
			}
			newRange.borderColor = borderColor;
		}

		int newRangeStart = start;
		int newRangeLength = length;
		int[] ranges = styledText.getRanges(start, length);
		StyleRange[] styles = styledText.getStyleRanges(start, length, false);
		int maxCount = ranges.length * 2 + 2;
		int[] newRanges = new int[maxCount];
		StyleRange[] newStyles = new StyleRange[maxCount / 2];
		int count = 0;
		for (int i = 0; i < ranges.length; i += 2) {
			int rangeStart = ranges[i];
			int rangeLength = ranges[i + 1];
			StyleRange range = styles[i / 2];
			if (rangeStart > newRangeStart) {
				newRangeLength = rangeStart - newRangeStart;
				newRanges[count] = newRangeStart;
				newRanges[count + 1] = newRangeLength;
				newStyles[count / 2] = newRange;
				count += 2;
			}
			newRangeStart = rangeStart + rangeLength;
			newRangeLength = (start + length) - newRangeStart;

			/* Create merged style range */
			StyleRange mergedRange = new StyleRange(range);
			// Note: fontStyle is not copied by the constructor
			mergedRange.fontStyle = range.fontStyle;
			if ((style & FONT) != 0) {
				mergedRange.font = newRange.font;
			}
			if ((style & FONT_STYLE) != 0) {
				mergedRange.fontStyle = range.fontStyle ^ newRange.fontStyle;
			}
			if (mergedRange.font != null
					&& ((style & FONT) != 0 || (style & FONT_STYLE) != 0)) {
				boolean change = false;
				FontData[] fds = mergedRange.font.getFontData();
				for (int j = 0; j < fds.length; j++) {
					FontData fd = fds[j];
					if (fd.getStyle() != mergedRange.fontStyle) {
						fds[j].setStyle(mergedRange.fontStyle);
						change = true;
					}
				}
				if (change) {
					mergedRange.font = new Font(Display.getDefault(), fds);
				}
			}
			if ((style & FOREGROUND) != 0) {
				mergedRange.foreground = newRange.foreground != range.foreground ? newRange.foreground
						: null;
			}
			if ((style & BACKGROUND) != 0) {
				mergedRange.background = newRange.background != range.background ? newRange.background
						: null;
			}
			if ((style & BASELINE_UP) != 0)
				mergedRange.rise++;
			if ((style & BASELINE_DOWN) != 0)
				mergedRange.rise--;
			if ((style & STRIKEOUT) != 0) {
				mergedRange.strikeout = !range.strikeout
						|| range.strikeoutColor != newRange.strikeoutColor;
				mergedRange.strikeoutColor = mergedRange.strikeout ? newRange.strikeoutColor
						: null;
			}
			if ((style & UNDERLINE) != 0) {
				if ((style & UNDERLINE_LINK) != 0) {
					if (link != null && link.length() > 0) {
						mergedRange.underline = !range.underline
								|| range.underlineStyle != newRange.underlineStyle
								|| range.data != newRange.data;
					} else {
						mergedRange.underline = false;
					}
					mergedRange.underlineColor = null;
				} else {
					mergedRange.underline = !range.underline
							|| range.underlineStyle != newRange.underlineStyle
							|| range.underlineColor != newRange.underlineColor;
					mergedRange.underlineColor = mergedRange.underline ? newRange.underlineColor
							: null;
				}
				mergedRange.underlineStyle = mergedRange.underline ? newRange.underlineStyle
						: SWT.NONE;
				mergedRange.data = mergedRange.underline ? newRange.data : null;
			}
			if ((style & BORDER) != 0) {
				if (range.borderStyle != newRange.borderStyle
						|| range.borderColor != newRange.borderColor) {
					mergedRange.borderStyle = newRange.borderStyle;
					mergedRange.borderColor = newRange.borderColor;
				} else {
					mergedRange.borderStyle = SWT.NONE;
					mergedRange.borderColor = null;
				}
			}

			newRanges[count] = rangeStart;
			newRanges[count + 1] = rangeLength;
			newStyles[count / 2] = mergedRange;
			count += 2;
		}
		if (newRangeLength > 0) {
			newRanges[count] = newRangeStart;
			newRanges[count + 1] = newRangeLength;
			newStyles[count / 2] = newRange;
			count += 2;
		}
		if (0 < count && count < maxCount) {
			int[] tmpRanges = new int[count];
			StyleRange[] tmpStyles = new StyleRange[count / 2];
			System.arraycopy(newRanges, 0, tmpRanges, 0, count);
			System.arraycopy(newStyles, 0, tmpStyles, 0, count / 2);
			newRanges = tmpRanges;
			newStyles = tmpStyles;
		}
		styledText.setStyleRanges(start, length, newRanges, newStyles);
		disposeRanges(styles);
	}

	private void showError(String title, String message) {
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR
				| SWT.CLOSE);
		messageBox.setText(title);
		messageBox.setMessage(message);
		messageBox.open();
	}

	private void updateStyleState(int style, int changingStyle) {
		if ((style & changingStyle) != 0) {
			if ((style & changingStyle) == (styleState & changingStyle)) {
				styleState &= ~changingStyle;
			} else {
				styleState &= ~changingStyle;
				styleState |= style;
			}
		}
	}

	private void setLink() {
		link = "http://aurora.hand-china.com/";
		setStyle(UNDERLINE_LINK);
		// final Shell dialog = new Shell(shell, SWT.APPLICATION_MODAL |
		// SWT.SHELL_TRIM);
		// dialog.setLayout(new GridLayout(2, false));
		//		dialog.setText(getResourceString("SetLink")); //$NON-NLS-1$
		// Label label = new Label(dialog, SWT.NONE);
		//		label.setText(getResourceString("URL")); //$NON-NLS-1$
		// final Text text = new Text(dialog, SWT.SINGLE);
		// text.setLayoutData(new GridData(200, SWT.DEFAULT));
		// if (link != null) {
		// text.setText(link);
		// text.selectAll();
		// }
		// final Button okButton = new Button(dialog, SWT.PUSH);
		//		okButton.setText(getResourceString("Ok")); //$NON-NLS-1$
		// final Button cancelButton = new Button(dialog, SWT.PUSH);
		//		cancelButton.setText(getResourceString("Cancel")); //$NON-NLS-1$
		// Listener listener = new Listener() {
		// public void handleEvent(Event event) {
		// if (event.widget == okButton) {
		// link = text.getText();
		// setStyle(UNDERLINE_LINK);
		// }
		// dialog.dispose();
		// }
		// };
		// okButton.addListener(SWT.Selection, listener);
		// cancelButton.addListener(SWT.Selection, listener);
		// dialog.setDefaultButton(okButton);
		// dialog.pack();
		// dialog.open();
		// while (!dialog.isDisposed()) {
		// if (!display.readAndDispatch()) {
		// display.sleep();
		// }
		// }
	}

	private void updateToolBar() {
		styleState = 0;
		link = null;
		boolean bold = false, italic = false;
		Font font = null;

		// int offset = styledText.getCaretOffset();
		// StyleRange range = offset > 0 ? styledText
		// .getStyleRangeAtOffset(offset - 1) : null;
		StyleRange range = styledText.getText().length() > 0 ? styledText
				.getStyleRangeAtOffset(0) : null;
		if (range != null) {
			if (range.font != null) {
				font = range.font;
				FontData[] fds = font.getFontData();
				for (int i = 0; i < fds.length; i++) {
					int fontStyle = fds[i].getStyle();
					if (!bold && (fontStyle & SWT.BOLD) != 0)
						bold = true;
					if (!italic && (fontStyle & SWT.ITALIC) != 0)
						italic = true;
				}
			} else {
				bold = (range.fontStyle & SWT.BOLD) != 0;
				italic = (range.fontStyle & SWT.ITALIC) != 0;
			}
			if (range.foreground != null) {
				styleState |= FOREGROUND;
				if (textForeground != range.foreground) {
					disposeResource(textForeground);
					textForeground = range.foreground;
				}
			}
			if (range.background != null) {
				styleState |= BACKGROUND;
				if (textBackground != range.background) {
					disposeResource(textBackground);
					textBackground = range.background;
				}
			}
			if (range.underline) {
				switch (range.underlineStyle) {
				case SWT.UNDERLINE_SINGLE:
					styleState |= UNDERLINE_SINGLE;
					break;
				case SWT.UNDERLINE_DOUBLE:
					styleState |= UNDERLINE_DOUBLE;
					break;
				case SWT.UNDERLINE_SQUIGGLE:
					styleState |= UNDERLINE_SQUIGGLE;
					break;
				case SWT.UNDERLINE_ERROR:
					styleState |= UNDERLINE_ERROR;
					break;
				case SWT.UNDERLINE_LINK:
					styleState |= UNDERLINE_LINK;
					link = (String) range.data;
					break;
				}
				if (range.underlineStyle != SWT.UNDERLINE_LINK) {
					underlineSingleItem
							.setSelection((styleState & UNDERLINE_SINGLE) != 0);
					underlineDoubleItem
							.setSelection((styleState & UNDERLINE_DOUBLE) != 0);
					underlineErrorItem
							.setSelection((styleState & UNDERLINE_ERROR) != 0);
					underlineSquiggleItem
							.setSelection((styleState & UNDERLINE_SQUIGGLE) != 0);
					disposeResource(underlineColor);
					underlineColor = range.underlineColor;
				}
			}
			if (range.strikeout) {
				styleState |= STRIKEOUT;
				disposeResource(strikeoutColor);
				strikeoutColor = range.strikeoutColor;
			}
			if (range.borderStyle != SWT.NONE) {
				switch (range.borderStyle) {
				case SWT.BORDER_SOLID:
					styleState |= BORDER_SOLID;
					break;
				case SWT.BORDER_DASH:
					styleState |= BORDER_DASH;
					break;
				case SWT.BORDER_DOT:
					styleState |= BORDER_DOT;
					break;
				}
				borderSolidItem.setSelection((styleState & BORDER_SOLID) != 0);
				borderDashItem.setSelection((styleState & BORDER_DASH) != 0);
				borderDotItem.setSelection((styleState & BORDER_DOT) != 0);
				disposeResource(borderColor);
				borderColor = range.borderColor;
			}
		}

		boldControl.setSelection(bold);
		italicControl.setSelection(italic);
		// FontData fontData = font != null ? font.getFontData()[0] : styledText
		// .getFont().getFontData()[0];
		// int index = 0;
		// int count = fontNameControl.getItemCount();
		// String fontName = fontData.getName();
		// while (index < count) {
		// if (fontNameControl.getItem(index).equals(fontName)) {
		// fontNameControl.select(index);
		// break;
		// }
		// index++;
		// }
		// index = 0;
		// count = fontSizeControl.getItemCount();
		// int fontSize = fontData.getHeight();
		// while (index < count) {
		// int size = Integer.parseInt(fontSizeControl.getItem(index));
		// if (fontSize == size) {
		// fontSizeControl.select(index);
		// break;
		// }
		// if (size > fontSize) {
		// fontSizeControl.add(String.valueOf(fontSize), index);
		// fontSizeControl.select(index);
		// break;
		// }
		// index++;
		// }

		disposeResource(textFont);
		textFont = font;
	}

	public StyledStringText createStyledStringText() {
		StyleRange[] styleRanges = this.styledText.getStyleRanges();
		// StyleRange styleRange3 = styledText.getStyleRangeAtOffset(0);
		StyledStringText sst = new StyledStringText();
		sst.setText(styledText.getText());
		if (styleRanges.length == 0)
			return sst;
		StyleRange styleRange = styleRanges[0];
		Font font = styleRange.font == null ? styledText.getFont()
				: styleRange.font;
		sst.setFontName(font.getFontData()[0].getName());
		sst.setFontSize(font.getFontData()[0].getHeight());
		sst.setBold((styleRange.fontStyle & SWT.BOLD) != 0);
		sst.setItalic((styleRange.fontStyle & SWT.ITALIC) != 0);
		if (styleRange.background != null) {
			sst.setTextBackground(AuroraImagesUtils
					.toString(styleRange.background.getRGB()));
		}
		if (styleRange.foreground != null) {
			sst.setTextForeground(AuroraImagesUtils
					.toString(styleRange.foreground.getRGB()));
		}
		if (styleRange.strikeout) {
			sst.setStrikeout(styleRange.strikeout);
			if (styleRange.strikeoutColor != null)
				sst.setStrikeoutColor(AuroraImagesUtils
						.toString(styleRange.strikeoutColor.getRGB()));
		}
		if (styleRange.underline) {
			sst.setUnderline(styleRange.underline);
			if (styleRange.underlineColor != null)
				sst.setUnderlineColor(AuroraImagesUtils
						.toString(styleRange.underlineColor.getRGB()));
			sst.setUnderlineStyle(styleRange.underlineStyle);
		}
		sst.setAlignment(alignmentStyle);
		return sst;
	}

	public StyledStringText getStyledStringText() {
		return styledStringText;
	}

	public void setStyledStringText(StyledStringText styledStringText) {
		this.styledStringText = styledStringText;
	}

}
