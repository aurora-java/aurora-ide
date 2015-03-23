/*******************************************************************************
 * Copyright (c) 2011, 2012 Red Hat, Inc.
 *  All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 *
 * @author Bob Brodt
 ******************************************************************************/

package org.eclipse.bpmn2.modeler.core.preferences;

import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.InsertionAdapter;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.model.ModelDecorator;
import org.eclipse.bpmn2.modeler.core.runtime.BaseRuntimeExtensionDescriptor;
import org.eclipse.bpmn2.modeler.core.runtime.ModelExtensionDescriptor;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.bpmn2.modeler.core.utils.StyleUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xml.type.AnyType;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.algorithms.styles.StylesFactory;
import org.eclipse.graphiti.mm.algorithms.styles.StylesPackage;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.ColorUtil;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

/**
 * Target Runtime Extension Descriptor class that defines color and font settings for graphical elements.
 * Instances of this class correspond to <style> extension elements in the extension's plugin.xml
 * See the description of the STYLE_OBJECT element in the org.eclipse.bpmn2.modeler.runtime extension point schema.
 */
public class ShapeStyle extends BaseRuntimeExtensionDescriptor {

	public final static String EXTENSION_NAME = "style"; //$NON-NLS-1$
	
	public static IColorConstant DEFAULT_COLOR = new ColorConstant(212, 231, 248);
	public static String DEFAULT_FONT_STRING = "arial,9,-,-"; //$NON-NLS-1$
	public final static int SS_SHAPE_BACKGROUND = 1 << 0;
	public final static int SS_SHAPE_FOREGROUND = 1 << 1;
	public final static int SS_SHAPE_PRIMARY_SELECTION = 1 << 2;
	public final static int SS_SHAPE_SECONDARY_SELECTION = 1 << 3;
	public final static int SS_LABEL_FONT = 1 << 4;
	public final static int SS_LABEL_FOREGROUND = 1 << 5;
	public final static int SS_LABEL_BACKGROUND = 1 << 6;
	public final static int SS_LABEL_POSITION = 1 << 11;
	public final static int SS_ROUTING_STYLE = 1 << 7;
	public final static int SS_USE_DEFAULT_SIZE = 1 << 8;
	public final static int SS_DEFAULT_WIDTH = 1 << 9;
	public final static int SS_DEFAULT_HEIGHT = 1 << 10;
	public final static int SS_ALL = -1;

	/** Attribute names of the ShapeStyle components in the style object in BaseElement extension values **/
	public final static String STYLE_OBJECT = "style";
	public final static String STYLE_ECLASS = "ShapeStyle";
	public final static String STYLE_SHAPE_FOREGROUND = "shapeForeground";
	public final static String STYLE_SHAPE_BACKGROUND = "shapeBackground";
	public final static String STYLE_LABEL_FONT = "labelFont";
	public final static String STYLE_LABEL_FOREGROUND = "labelForeground";
	public final static String STYLE_LABEL_BACKGROUND = "labelBackground";
	public final static String STYLE_LABEL_POSITION = "labelPosition";
	public final static String STYLE_ROUTING_STYLE = "routingStyle";
	public final static String STYLE_USE_DEFAULT_SIZE = "useDefaultSize";
	public final static String STYLE_DEFAULT_WIDTH = "defaultWidth";
	public final static String STYLE_DEFAULT_HEIGHT = "defaultHeight";
	
	private final static String DEFAULT_BACKGROUND = "FFFFFF";
	private final static String DEFAULT_FOREGROUND = "000000";
	
	String object;
	IColorConstant shapeBackground;
	IColorConstant shapePrimarySelectedColor;
	IColorConstant shapeSecondarySelectedColor;
	IColorConstant shapeForeground;
	Font labelFont;
	IColorConstant labelForeground;
	IColorConstant labelBackground;
	RoutingStyle routingStyle = RoutingStyle.Manhattan;
	boolean useDefaultSize;
	// the useDefault doubles as the flag for "snap to grid" in the Canvas ShapeStyle
//	boolean snapToGrid = true;
	int defaultWidth = 110;
	int defaultHeight = 50;
	LabelPosition labelPosition = LabelPosition.SOUTH;
	int changeMask;
	
	public static enum Category {
		CONNECTIONS(Messages.ShapeStyle_Category_Connections),
		SHAPES(Messages.ShapeStyle_Category_Shapes),
		EVENTS(Messages.ShapeStyle_Category_Events),
		GATEWAYS(Messages.ShapeStyle_Category_Gateways),
		TASKS(Messages.ShapeStyle_Category_Tasks),
		GLOBAL_TASKS(Messages.ShapeStyle_Category_GlobalTasks),
		SUBPROCESS(Messages.ShapeStyle_Category_SubProcess),
		CHOREOGRAPHY(Messages.ShapeStyle_Category_Choreography),
		CONVERSATION(Messages.ShapeStyle_Category_Conversation),
		SWIMLANES(Messages.ShapeStyle_Category_SwimLanes),
		DATA(Messages.ShapeStyle_Category_Data),
		ARTIFACTS(Messages.ShapeStyle_Category_Other),
		CANVAS(Messages.ShapeStyle_Category_Canvas),
		GRID(Messages.ShapeStyle_Category_Grid),
		NONE("");
		
		private String string;
		private Category(String string) {
			this.string = string;
		}
		
		@Override
		public String toString() {
			return string;
		}
	};
	
	public static enum RoutingStyle {
		ManualBendpoint(Messages.ShapeStyle_RoutingStyle_Direct),
		AutomaticBendpoint(Messages.ShapeStyle_RoutingStyle_Automatic),
		Manhattan(Messages.ShapeStyle_RoutingStyle_Manhattan);
		
		private String string;
		private RoutingStyle(String string) {
			this.string = string;
		}
		
		@Override
		public String toString() {
			return string;
		}
	};
	
	public static enum LabelPosition {
		SOUTH(Messages.ShapeStyle_LabelPosition_South), // this is the default value, ordinal=0
		NORTH(Messages.ShapeStyle_LabelPosition_North),
		WEST(Messages.ShapeStyle_LabelPosition_West),
		EAST(Messages.ShapeStyle_LabelPosition_East),
		TOP(Messages.ShapeStyle_LabelPosition_Top),
		CENTER(Messages.ShapeStyle_LabelPosition_Center),
		BOTTOM(Messages.ShapeStyle_LabelPosition_Bottom),
		LEFT(Messages.ShapeStyle_LabelPosition_Left),
		RIGHT(Messages.ShapeStyle_LabelPosition_Right),
		MOVABLE(Messages.ShapeStyle_LabelPosition_Movable);
		
		private String string;
		private LabelPosition(String string) {
			this.string = string;
		}
		
		@Override
		public String toString() {
			return string;
		}
	}
	
	public ShapeStyle() {
		setDefaultColors(DEFAULT_COLOR);
		labelFont = stringToFont(DEFAULT_FONT_STRING);
	}

	public ShapeStyle(IConfigurationElement e) {
		super(e);
		object = e.getAttribute("object");
		String shapeForeground = e.getAttribute(STYLE_SHAPE_FOREGROUND);
		String shapeBackground = e.getAttribute(STYLE_SHAPE_BACKGROUND);
		String labelFont = e.getAttribute(STYLE_LABEL_FONT);
		String labelForeground = e.getAttribute(STYLE_LABEL_FOREGROUND);
		String labelBackground = e.getAttribute(STYLE_LABEL_BACKGROUND);
		String labelPosition = e.getAttribute(STYLE_LABEL_POSITION);
		String routingStyle = e.getAttribute(STYLE_ROUTING_STYLE);
		String useDefaultSize = e.getAttribute(STYLE_USE_DEFAULT_SIZE);
		String defaultHeight = e.getAttribute(STYLE_DEFAULT_HEIGHT);
		String defaultWidth = e.getAttribute(STYLE_DEFAULT_WIDTH);

		// only background color is required to set up default color scheme
		if (shapeBackground==null || shapeBackground.isEmpty())
			shapeBackground = DEFAULT_BACKGROUND;
		this.shapeBackground = stringToColor(shapeBackground);
		setDefaultColors(this.shapeBackground);
		
		// optional:
		if (shapeForeground!=null && !shapeForeground.isEmpty())
			this.shapeForeground = stringToColor(shapeForeground);
		if (labelFont==null || labelFont.isEmpty())
			labelFont = DEFAULT_FONT_STRING;
		this.labelFont = stringToFont(labelFont);
		if (labelForeground!=null && !labelForeground.isEmpty())
			this.labelForeground = stringToColor(labelForeground);
		if (labelBackground!=null && !labelBackground.isEmpty())
			this.labelBackground = stringToColor(labelBackground);
		if (labelPosition!=null && !labelPosition.isEmpty())
			this.labelPosition = LabelPosition.valueOf(labelPosition);
		else
			this.labelPosition = LabelPosition.SOUTH;
		if (routingStyle!=null && !routingStyle.isEmpty())
			this.routingStyle = RoutingStyle.valueOf(labelPosition);
		else
			this.routingStyle = RoutingStyle.Manhattan;
		this.useDefaultSize = Boolean.parseBoolean(useDefaultSize);
		try { this.defaultHeight = Integer.parseInt(defaultHeight); } catch (Exception e1) {}
		try { this.defaultWidth = Integer.parseInt(defaultWidth); } catch (Exception e1) {}
	}

	public ShapeStyle(ShapeStyle other) {
		this(encode(other));
		this.object = other.object;
		this.targetRuntime = other.targetRuntime;
	}
	
	private ShapeStyle(String s) {
		String[] a = s.trim().split(";"); //$NON-NLS-1$
		if (a.length>0)
			shapeBackground = stringToColor(a[0]);
		if (a.length>1)
			shapePrimarySelectedColor = stringToColor(a[1]);
		if (a.length>2)
			shapeSecondarySelectedColor = stringToColor(a[2]);
		if (a.length>3)
			shapeForeground = stringToColor(a[3]);
		if (a.length>4)
			labelFont = stringToFont(a[4]);
		if (a.length>5)
			labelForeground = stringToColor(a[5]);
		if (a.length>6)
			labelBackground = stringToColor(a[6]);
		if (a.length>7) {
			try {
				routingStyle = RoutingStyle.values()[Integer.parseInt(a[7])];
			}
			catch (Exception e) {
				routingStyle = RoutingStyle.ManualBendpoint;
			}
		}
		else
			routingStyle = RoutingStyle.ManualBendpoint;
		
		if (a.length>8) {
			useDefaultSize = stringToBoolean(a[8]);
		}
		else
			useDefaultSize = false;
		
		if (a.length>9) {
			defaultWidth = Integer.parseInt(a[9]);
		}
		else
			defaultWidth = 110;
		
		if (a.length>10) {
			defaultHeight= Integer.parseInt(a[10]);
		}
		else
			defaultHeight = 50;
		
		if (a.length>11) {
			labelPosition = LabelPosition.values()[Integer.parseInt(a[11])];
		}
		else
			labelPosition = LabelPosition.SOUTH;
	}

	@Override
	public void setConfigFile(IFile configFile) {
		super.setConfigFile(configFile);
		if (configFile!=null) {
			Bpmn2Preferences prefs = Bpmn2Preferences.getInstance(configFile.getProject());
			prefs.loadDefaults(targetRuntime, Bpmn2Preferences.PREF_SHAPE_STYLE);
		}
	}

	public void dispose() {
		// remove the ModelEnablement classes and features that may
		// have been defined in this Model Extension
		if (configFile!=null) {
			Bpmn2Preferences prefs = Bpmn2Preferences.getInstance(configFile.getProject());
			prefs.unloadDefaults(targetRuntime, Bpmn2Preferences.PREF_SHAPE_STYLE);
		}
		super.dispose();
		if (configFile!=null) {
			Bpmn2Preferences prefs = Bpmn2Preferences.getInstance(configFile.getProject());
			prefs.loadDefaults(targetRuntime, Bpmn2Preferences.PREF_SHAPE_STYLE);
		}
	}

	@Override
	public String getExtensionName() {
		return EXTENSION_NAME;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}
	
	public void setDefaultColors(IColorConstant defaultColor) {
		setShapeBackground(defaultColor);
		setShapePrimarySelectedColor(StyleUtil.shiftColor(defaultColor, 32));
		setShapeSecondarySelectedColor(StyleUtil.shiftColor(defaultColor, -32));
		setShapeForeground(StyleUtil.shiftColor(defaultColor, -128));
		setLabelForeground(StyleUtil.shiftColor(defaultColor, -128));
		setLabelBackground(stringToColor(DEFAULT_BACKGROUND));
	}
	
	public boolean isDirty() {
		return changeMask!=0;
	}
	
	public void setDirty(boolean dirty) {
		this.changeMask = SS_ALL;
	}
	
	public IColorConstant getShapeBackground() {
		return shapeBackground;
	}

	public void setShapeBackground(IColorConstant shapeDefaultColor) {
		if (!equals(this.shapeBackground, shapeDefaultColor)) {
			this.shapeBackground = shapeDefaultColor;
			changeMask |= SS_SHAPE_BACKGROUND;
		}
	}

	public IColorConstant getShapePrimarySelectedColor() {
		return shapePrimarySelectedColor;
	}

	public void setShapePrimarySelectedColor(IColorConstant shapePrimarySelectedColor) {
		if (!equals(this.shapePrimarySelectedColor, shapePrimarySelectedColor)) {
			this.shapePrimarySelectedColor = shapePrimarySelectedColor;
			changeMask |= SS_SHAPE_PRIMARY_SELECTION;
		}
	}

	public IColorConstant getShapeSecondarySelectedColor() {
		return shapeSecondarySelectedColor;
	}

	public void setShapeSecondarySelectedColor(IColorConstant shapeSecondarySelectedColor) {
		if (!equals(this.shapeSecondarySelectedColor, shapeSecondarySelectedColor)) {
			this.shapeSecondarySelectedColor = shapeSecondarySelectedColor;
			changeMask |= SS_SHAPE_SECONDARY_SELECTION;
		}
	}

	public IColorConstant getShapeForeground() {
		return shapeForeground;
	}

	public void setShapeForeground(IColorConstant shapeBorderColor) {
		if (!equals(this.shapeForeground, shapeBorderColor)) {
			this.shapeForeground = shapeBorderColor;
			changeMask |= SS_SHAPE_FOREGROUND;
		}
	}

	public Font getLabelFont() {
		return labelFont;
	}

	public void setLabelFont(Font labelFont) {
		if (!equals(this.labelFont, labelFont)) {
			this.labelFont = labelFont;
			changeMask |= SS_LABEL_FONT;
		}
	}

	public IColorConstant getLabelForeground() {
		return labelForeground;
	}

	public void setLabelForeground(IColorConstant labelForeground) {
		if (!equals(this.labelForeground, labelForeground)) {
			this.labelForeground = labelForeground;
			changeMask |= SS_LABEL_FOREGROUND;
		}
	}

	public IColorConstant getLabelBackground() {
		if (labelBackground==null)
			return stringToColor(DEFAULT_BACKGROUND);
		return labelBackground;
	}

	public void setLabelBackground(IColorConstant labelBackground) {
		if (!equals(this.labelBackground, labelBackground)) {
			this.labelBackground = labelBackground;
			changeMask |= SS_LABEL_BACKGROUND;
		}
	}

	public RoutingStyle getRoutingStyle() {
		return routingStyle;
	}

	public void setRoutingStyle(RoutingStyle routingStyle) {
		if (this.routingStyle != routingStyle) {
			this.routingStyle = routingStyle;
			changeMask |= SS_ROUTING_STYLE;
		}
	}
	
	public int getDefaultWidth() {
		if (defaultWidth<=0) {
			if (object.toLowerCase().contains("gateway"))
				return 50;
			if (object.toLowerCase().contains("event"))
				return 36;
			if (object.toLowerCase().contains("choreography"))
				return 150;
			if (object.toLowerCase().contains("data"))
				return 36;
			return 110;
		}
		return defaultWidth;
	}

	public void setDefaultWidth(int defaultWidth) {
		if (this.defaultWidth!=defaultWidth) {
			this.defaultWidth = defaultWidth;
			changeMask |= SS_DEFAULT_WIDTH;
		}
	}
	
	public int getDefaultHeight() {
		if (defaultHeight<=0) {
			if (object.toLowerCase().contains("gateway"))
				return 50;
			if (object.toLowerCase().contains("event"))
				return 36;
			if (object.toLowerCase().contains("choreography"))
				return 150;
			if (object.toLowerCase().contains("data"))
				return 50;
			return 50;
		}
		return defaultHeight;
	}

	public void setDefaultHeight(int defaultHeight) {
		if (this.defaultHeight!=defaultHeight) {
			this.defaultHeight = defaultHeight;
			changeMask |= SS_DEFAULT_HEIGHT;
		}
	}

	public LabelPosition getLabelPosition() {
		return labelPosition;
	}

	public void setLabelPosition(LabelPosition labelPosition) {
		if (this.labelPosition!=labelPosition) {
			this.labelPosition = labelPosition;
			changeMask |= SS_LABEL_POSITION;
		}
	}
	
	public boolean getUseDefaultSize() {
		return useDefaultSize;
	}
	
	public void setUseDefaultSize(boolean b) {
		if (useDefaultSize != b) {
			useDefaultSize = b;
			changeMask |= SS_USE_DEFAULT_SIZE;
		}
	}

	public boolean getSnapToGrid() {
		return getUseDefaultSize();
	}
	
	public void setSnapToGrid(boolean value) {
		setUseDefaultSize(value);
	}
	
	public static String colorToString(IColorConstant c) {
		return new String(
				String.format("%02X",c.getRed()) + //$NON-NLS-1$
				String.format("%02X",c.getGreen()) + //$NON-NLS-1$
				String.format("%02X",c.getBlue()) //$NON-NLS-1$
				);
	}
	
	public static IColorConstant stringToColor(String s) {
		if (s.contains(",")) { //$NON-NLS-1$
			String[] a = s.split(","); //$NON-NLS-1$
			int r = Integer.parseInt(a[0]);
			int g = Integer.parseInt(a[1]);
			int b = Integer.parseInt(a[2]);
			return new ColorConstant(r, g, b);
		}
		if (s.length()<6)
			return new ColorConstant(0,0,0);
		return new ColorConstant(
				ColorUtil.getRedFromHex(s),
				ColorUtil.getGreenFromHex(s),
				ColorUtil.getBlueFromHex(s)
				);
	}
	
	public static String booleanToString(boolean b) {
		return b ? "1" : "0"; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static boolean stringToBoolean(String s) {
		return "1".equals(s); //$NON-NLS-1$
	}
	
	public static RGB colorToRGB(IColorConstant c) {
		return new RGB(c.getRed(),c.getGreen(),c.getBlue());
	}
	
	public static IColorConstant RGBToColor(RGB rgb) {
		return new ColorConstant(rgb.red, rgb.green, rgb.blue);
	}

	public static String fontToString(Font f) {
		if (f!=null)
			return new String(
					f.getName() + "," + //$NON-NLS-1$
					f.getSize() + "," + //$NON-NLS-1$
					(f.isItalic() ? "I" : "-") + "," + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					(f.isBold() ? "B" : "-") //$NON-NLS-1$ //$NON-NLS-2$
					);
		return "";
	}
	
	public static Font stringToFont(String s) {
		String[] a = s.split(","); //$NON-NLS-1$
		Font f = StylesFactory.eINSTANCE.createFont();
		f.eSet(StylesPackage.eINSTANCE.getFont_Name(), a[0]);
		f.eSet(StylesPackage.eINSTANCE.getFont_Size(), Integer.valueOf(a[1]));
		f.eSet(StylesPackage.eINSTANCE.getFont_Italic(), a[2].equals("I")); //$NON-NLS-1$
		f.eSet(StylesPackage.eINSTANCE.getFont_Bold(), a[3].equals("B")); //$NON-NLS-1$
		return f;
	}

	public static FontData fontToFontData(Font f) {
		int style = 0;
		if (f.isItalic())
			style |= SWT.ITALIC;
		if (f.isBold())
			style |= SWT.BOLD;
		return new FontData(f.getName(), f.getSize(), style);
	}
	
	public static Font fontDataToFont(FontData fd) {
		Font f = StylesFactory.eINSTANCE.createFont();
		f.eSet(StylesPackage.eINSTANCE.getFont_Name(),fd.getName());
		f.eSet(StylesPackage.eINSTANCE.getFont_Size(), fd.getHeight());
		f.eSet(StylesPackage.eINSTANCE.getFont_Italic(), (fd.getStyle() & SWT.ITALIC)!=0);
		f.eSet(StylesPackage.eINSTANCE.getFont_Bold(), (fd.getStyle() & SWT.BOLD)!=0);
		return f;
	}

	/**
	 * @param fontData
	 * @return
	 */
	public static Font toGraphitiFont(Diagram diagram, FontData fontData) {
		if (fontData == null) {
			return null;
		}
		Font ret = null;
		try {
			String name = fontData.getName();
			int height = fontData.getHeight();
			boolean italic = (fontData.getStyle() & SWT.ITALIC) != 0;
			boolean bold = (fontData.getStyle() & SWT.BOLD) != 0;
			ret = Graphiti.getGaService().manageFont(diagram, name, height, italic, bold);
		}
		catch (Exception e) {
		}
		return ret;
	}

	/**
	 * @param pictogramFont
	 * @return
	 */
	public static FontData toFontData(Font pictogramFont) {
		FontData fontData;
		if (pictogramFont != null) {
			int style = SWT.NORMAL;
			if (pictogramFont.isItalic()) {
				style |= SWT.ITALIC;
			}
			if (pictogramFont.isBold()) {
				style |= SWT.BOLD;
			}
			int size = pictogramFont.getSize();
			String name = pictogramFont.getName();
			fontData = new FontData(name, size, style);
		} else {
			fontData = new FontData();
		}
		return fontData;
	}

	public static String encode(ShapeStyle sp) {
		if (sp==null)
			return encode(new ShapeStyle());
		return new String(
				colorToString(sp.shapeBackground) + ";" + //$NON-NLS-1$
				colorToString(sp.shapePrimarySelectedColor) + ";" + //$NON-NLS-1$
				colorToString(sp.shapeSecondarySelectedColor) + ";" + //$NON-NLS-1$
				colorToString(sp.shapeForeground) + ";" + //$NON-NLS-1$
				fontToString(sp.labelFont) + ";" + //$NON-NLS-1$
				colorToString(sp.labelForeground) + ";" + //$NON-NLS-1$
				// placeholder for backward compatibility
				booleanToString(sp.useDefaultSize) + ";" + //$NON-NLS-1$
				sp.routingStyle.ordinal() + ";" + //$NON-NLS-1$
				booleanToString(sp.useDefaultSize) + ";" + //$NON-NLS-1$
				sp.defaultWidth + ";" + //$NON-NLS-1$
				sp.defaultHeight + ";" + //$NON-NLS-1$
				sp.labelPosition.ordinal()
				);
	}
	
	public static ShapeStyle decode(String s) {
		if (s==null || s.trim().split(";").length<11) //$NON-NLS-1$
			return new ShapeStyle();
		return new ShapeStyle(s);
	}

	public void applyChanges(ShapeStyle other) {
		int m = other.changeMask;
		if ((m & SS_SHAPE_BACKGROUND) != 0)
			this.setShapeBackground(other.getShapeBackground());
		if ((m & SS_SHAPE_FOREGROUND) != 0)
			this.setShapeForeground(other.getShapeForeground());
		if ((m & SS_SHAPE_PRIMARY_SELECTION) != 0)
			this.setShapePrimarySelectedColor(other.getShapePrimarySelectedColor());
		if ((m & SS_SHAPE_SECONDARY_SELECTION) != 0)
			this.setShapeSecondarySelectedColor(other.getShapeSecondarySelectedColor());
		if ((m & SS_LABEL_FONT) != 0)
			this.setLabelFont(other.getLabelFont());
		if ((m & SS_LABEL_FOREGROUND) != 0)
			this.setLabelForeground(other.getLabelForeground());
		if ((m & SS_ROUTING_STYLE) != 0)
			this.setRoutingStyle(other.getRoutingStyle());
		if ((m & SS_USE_DEFAULT_SIZE) != 0)
			this.setSnapToGrid(other.getSnapToGrid());
		if ((m & SS_DEFAULT_WIDTH) != 0)
			this.setDefaultWidth(other.getDefaultWidth());
		if ((m & SS_DEFAULT_HEIGHT) != 0)
			this.setDefaultHeight(other.getDefaultHeight());
		if ((m & SS_LABEL_POSITION) != 0)
			this.setLabelPosition(other.getLabelPosition());
	}

	public void setValue(int m, Object value) {
		if (m == SS_SHAPE_BACKGROUND)
			this.setShapeBackground((IColorConstant)value);
		if (m == SS_SHAPE_FOREGROUND)
			this.setShapeForeground((IColorConstant)value);
		if (m == SS_SHAPE_PRIMARY_SELECTION)
			this.setShapePrimarySelectedColor((IColorConstant)value);
		if (m == SS_SHAPE_SECONDARY_SELECTION)
			this.setShapeSecondarySelectedColor((IColorConstant)value);
		if (m == SS_LABEL_FONT)
			this.setLabelFont((Font)value);
		if (m == SS_LABEL_FOREGROUND)
			this.setLabelForeground((IColorConstant)value);
		if (m == SS_ROUTING_STYLE)
			this.setRoutingStyle((RoutingStyle)value);
		if (m == SS_USE_DEFAULT_SIZE)
			this.setSnapToGrid((Boolean)value);
		if (m == SS_DEFAULT_WIDTH)
			this.setDefaultWidth((Integer)value);
		if (m == SS_DEFAULT_HEIGHT)
			this.setDefaultHeight((Integer)value);
		if (m == SS_LABEL_POSITION)
			this.setLabelPosition((LabelPosition)value);
	}

	private static boolean equals(IColorConstant c1, IColorConstant c2) {
		if (c1==c2)
			return true;
		if (c1==null || c2==null)
			return false;
		return c1.getRed() == c2.getRed() &&
				c1.getGreen() == c2.getGreen() &&
				c1.getBlue() == c2.getBlue();
	}
	
	private static boolean equals(Font f1, Font f2) {
		String s1 = fontToString(f1);
		String s2 = fontToString(f2);
		return s1.equals(s2);
	}
	
	public static IColorConstant lighter(IColorConstant c) {
		int r = c.getRed() + 8;
		int g = c.getGreen() + 8;
		int b = c.getBlue() + 8;
		if (r>255) r = 255;
		if (g>255) g = 255;
		if (b>255) b = 255;
		return new ColorConstant(r, g, b);
	}
	
	public static IColorConstant darker(IColorConstant c) {
		int r = c.getRed() - 8;
		int g = c.getGreen() - 8;
		int b = c.getBlue() - 8;
		if (r<0) r = 0;
		if (g<0) g = 0;
		if (b<0) b = 0;
		return new ColorConstant(r, g, b);
	}

	@Override
	public String toString() {
		return encode(this);
	}
	
	public static boolean hasStyle(BaseElement businessObject) {
		ModelExtensionDescriptor med = TargetRuntime.getDefaultRuntime().getModelExtensionDescriptor(businessObject);
		if (med!=null) {
			ModelDecorator md = med.getModelDecorator();
			EStructuralFeature styleFeature = md.getEStructuralFeature(businessObject, STYLE_OBJECT);
			if (styleFeature!=null)
				return true;
		}
		return false;
	}

	public static EObject createStyleObject(BaseElement element) {
		EObject style = null;
		try {
			ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(element);
			ModelExtensionDescriptor med = TargetRuntime.getDefaultRuntime().getModelExtensionDescriptor(element);
			ModelDecorator md = med.getModelDecorator();
			EStructuralFeature styleFeature = md.getEStructuralFeature(element, STYLE_OBJECT);
			if (styleFeature!=null) {
				ShapeStyle ss = getShapeStyle(element);
				style = (EObject)adapter.getFeatureDescriptor(styleFeature).getValue();
				if (style==null) {
					// this object does not have a <style> extension element yet so create one
					// and initialize it from the User Preference store
					style = med.createObject((EClass)styleFeature.getEType());
					setShapeStyle(element, style, ss);
					// add it to the BaseElement extension values
					InsertionAdapter.add(element, styleFeature, style);
				}
				else {
					setShapeStyle(element, style, ss);
				}
			}
		}
		catch (Exception e) {
			// ignore exceptions - the BaseElement doesn't have a <style> extension element
			e.printStackTrace();
		}
		return style;
	}
	
	public static EObject getStyleObject(BaseElement element) {
		EObject style = null;
		try {
			ModelExtensionDescriptor med = TargetRuntime.getDefaultRuntime().getModelExtensionDescriptor(element);
			ModelDecorator md = med.getModelDecorator();
			EStructuralFeature styleFeature = md.getEStructuralFeature(element, STYLE_OBJECT);
			if (styleFeature!=null) {
				ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(element);
				if (adapter!=null)
					style = (EObject)adapter.getFeatureDescriptor(styleFeature).getValue();
			}
		}
		catch (Exception e) {
			// ignore exceptions - the BaseElement doesn't have a <style> extension element
		}
		return style;
	}

	public static Object getStyleValue(EObject style, String feature) {
		EStructuralFeature f = style.eClass().getEStructuralFeature(feature);
		if (f!=null && style.eIsSet(f))
			return style.eGet(f);
		return null;
	}
	
	public Object getStyleValue(BaseElement element, String feature) {
		if (STYLE_SHAPE_FOREGROUND.equals(feature))
			return colorToRGB(getShapeForeground());
		if (STYLE_SHAPE_BACKGROUND.equals(feature))
			return colorToRGB(getShapeBackground());
		if (STYLE_LABEL_FOREGROUND.equals(feature))
			return colorToRGB(getLabelForeground());
		if (STYLE_LABEL_BACKGROUND.equals(feature))
			return null;
		if (STYLE_LABEL_FONT.equals(feature))
			return ShapeStyle.toFontData(getLabelFont());
		if (STYLE_LABEL_POSITION.equals(feature))
			return ShapeStyle.toEENumLiteral(element, getLabelPosition());
		if (STYLE_ROUTING_STYLE.equals(feature))
			return ShapeStyle.toEENumLiteral(element, getRoutingStyle());
		return null;
	}
	
	private static void setStyleValue(EObject style, String feature, Object value) {
		try {
			EStructuralFeature f = style.eClass().getEStructuralFeature(feature);
			Object oldValue = style.eGet(f);
			if (value!=null && !value.equals(oldValue))
				style.eSet(f, value);
		}
		catch (Exception e) {
		}
	}

	public static boolean isStyleObject(Object object) {
		if (object instanceof AnyType) {
			AnyType at = (AnyType)object;
			EClass ec = at.eClass();
			String name = ec.getName();
			return STYLE_ECLASS.equals(name);
		}
		return false;
	}

	public static boolean isStyleFeature(EStructuralFeature feature) {
		return feature.getName().equals(STYLE_OBJECT) && feature.getEType().getName().equals(STYLE_ECLASS);
	}
	
	public static ShapeStyle getShapeStyle(BaseElement element) {
		Bpmn2Preferences preferences = Bpmn2Preferences.getInstance(element);
		ShapeStyle ss = preferences.getShapeStyle(element);
		ss = new ShapeStyle(ss); // makes a copy of the value in Preference Store

		EObject style = getStyleObject(element);
		if (style!=null) {
			style.eSetDeliver(false);
			
			RGB shapeForeground = (RGB) getStyleValue(style,STYLE_SHAPE_FOREGROUND);
			RGB shapeBackground = (RGB) getStyleValue(style,STYLE_SHAPE_BACKGROUND);
			RGB labelForeground = (RGB) getStyleValue(style,STYLE_LABEL_FOREGROUND);
			FontData labelFont = (FontData) getStyleValue(style,STYLE_LABEL_FONT);
			EEnumLiteral labelPosition = (EEnumLiteral) getStyleValue(style,STYLE_LABEL_POSITION);
			EEnumLiteral routingStyle = (EEnumLiteral) getStyleValue(style,STYLE_ROUTING_STYLE);
			
			if (shapeBackground!=null) {
				IColorConstant cc = ShapeStyle.RGBToColor(shapeBackground);
				ss.setShapeBackground(cc);
				ss.setShapePrimarySelectedColor(StyleUtil.shiftColor(cc, 32));
				ss.setShapeSecondarySelectedColor(StyleUtil.shiftColor(cc, -32));
			}
			else
				setStyleValue(style, STYLE_SHAPE_BACKGROUND, ShapeStyle.colorToRGB(ss.getShapeBackground()));

			if (shapeForeground!=null)
				ss.setShapeForeground(ShapeStyle.RGBToColor(shapeForeground));
			else
				setStyleValue(style, STYLE_SHAPE_FOREGROUND, ShapeStyle.colorToRGB(ss.getShapeForeground()));

			if (labelForeground!=null)
				ss.setLabelForeground(ShapeStyle.RGBToColor(labelForeground));
			else
				setStyleValue(style, STYLE_LABEL_FOREGROUND, ShapeStyle.colorToRGB(ss.getLabelForeground()));

			if (labelFont!=null) {
				// roundabout way to get the Diagram for a Business Object:
				// see {@link DIUtils} for details.
				Resource res = ExtendedPropertiesAdapter.getResource(element);
				List<PictogramElement> pes = DIUtils.getPictogramElements(res.getResourceSet(), element);
				if (pes.size()>0) {
					Diagram diagram = Graphiti.getPeService().getDiagramForPictogramElement(pes.get(0));
					ss.setLabelFont(ShapeStyle.toGraphitiFont(diagram, labelFont));
				}
			}
			else
				setStyleValue(style, STYLE_LABEL_FONT, ShapeStyle.toFontData(ss.getLabelFont()));

			if (labelPosition!=null)
				ss.setLabelPosition((LabelPosition)fromEENumLiteral(element, labelPosition));
			else
				setStyleValue(style, STYLE_LABEL_POSITION, toEENumLiteral(element, ss.getLabelPosition()));

			if (routingStyle!=null)
				ss.setRoutingStyle( (RoutingStyle)fromEENumLiteral(element, routingStyle) );
			else
				setStyleValue(style, STYLE_ROUTING_STYLE, toEENumLiteral(element, ss.getRoutingStyle()));

			style.eSetDeliver(true);
		}
		return ss;
	}

	private static Enum fromEENumLiteral(EObject element, EEnumLiteral el) {
		try {
			LabelPosition.values();
			Class c = Class.forName(ShapeStyle.class.getName() + "$" + el.getEEnum().getName());
			for (Enum en : (Enum[])c.getEnumConstants()) {
				if (en.ordinal() == el.getValue())
					return en;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static EEnumLiteral toEENumLiteral(EObject element, Enum en) {
		ModelExtensionDescriptor med = TargetRuntime.getDefaultRuntime().getModelExtensionDescriptor(element);
		ModelDecorator md = med.getModelDecorator();
		EEnum lp = (EEnum)md.getEDataType(en.getClass().getSimpleName());
		EEnumLiteral el = lp.getEEnumLiteral(en.ordinal());
		fromEENumLiteral(element, el);
		return el;
	}

	public static void setShapeStyle(BaseElement element, EObject style, ShapeStyle ss) {
		if (hasStyle(element)) {
			if (style==null)
				style = getStyleObject(element);

			setStyleValue(style, STYLE_SHAPE_FOREGROUND, ShapeStyle.colorToRGB(ss.getShapeForeground()));
			setStyleValue(style, STYLE_SHAPE_BACKGROUND, ShapeStyle.colorToRGB(ss.getShapeBackground()));
			setStyleValue(style, STYLE_LABEL_FOREGROUND, ShapeStyle.colorToRGB(ss.getLabelForeground()));
			setStyleValue(style, STYLE_LABEL_FONT, ShapeStyle.toFontData(ss.getLabelFont()));
			setStyleValue(style, STYLE_LABEL_POSITION, toEENumLiteral(element, ss.getLabelPosition()));
			setStyleValue(style, STYLE_ROUTING_STYLE, toEENumLiteral(element, ss.getRoutingStyle()));
		}
		else {
			Bpmn2Preferences preferences = Bpmn2Preferences.getInstance(element);
			preferences.setShapeStyle(element,ss);
		}
	}
	
	public static boolean isDirty(BaseElement element) {
		if(true)
			return true;
		if (element==null)
			return false;
		Bpmn2Preferences preferences = Bpmn2Preferences.getInstance(element);
		ShapeStyle ssDefault = preferences.getShapeStyle(element);
		ShapeStyle ssElement = getShapeStyle(element);
		String defaultString = ssDefault.toString();
		String elementString = ssElement.toString();
		return !defaultString.equals(elementString);
	}
}
