/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013, 2014 Red Hat, Inc.
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
package org.eclipse.bpmn2.modeler.ui.property;

import org.eclipse.bpmn2.modeler.core.DefaultConversionDelegate;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * EDataType ConversionDelegate for RGB (color value) objects.
 * 
 * The class is registered with our EDataTypeConversionFactory with the name of a data type;
 * in this case the data type name is "EColor" in keeping with the EMF naming convention
 * for primitive data types (e.g. "EString", "EBoolean", etc.)
 */
public class EColorConversionDelegate extends DefaultConversionDelegate {

	private enum RGBComponent { RED, GREEN, BLUE };
	
	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.EDataType.Internal.ConversionDelegate#convertToString(java.lang.Object)
	 */
	@Override
	public String convertToString(Object value) {
		if (value instanceof RGB) {
			RGB c = (RGB) value;
			return "#" +
					toHex(c.red) +
					toHex(c.green) +
					toHex(c.blue);
		}
		if (value==null)
			return "";
		return value.toString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.EDataType.Internal.ConversionDelegate#createFromString(java.lang.String)
	 * 
	 * This method MUST accept an empty String and construct a "default" object. This is required so that
	 * clients can determine the java type (class) of objects handled by this ConversionDelegate.
	 */
	@Override
	public Object createFromString(String literal) {
		if (literal.startsWith("#"))
			literal = literal.substring(1);
		if (literal.length()==6) {
			RGB c = new RGB(
					fromHex(literal, RGBComponent.RED),
					fromHex(literal, RGBComponent.GREEN),
					fromHex(literal, RGBComponent.BLUE)
			);
			return c;
		}
		return new RGB(0,0,0);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.EditControlProvider#createControl(org.eclipse.swt.widgets.Composite, int)
	 */
	@Override
	public EditControl createControl(Composite parent, int style) {
		return new ColorControl(parent, style);
	}

	private String toHex(int i) {
		String s = Integer.toHexString(i);
		int l = s.length();
		if (l==1)
			s = "0" + s;
		else if (l>2)
			s = s.substring(l-2, l-1);
		return s;
	}
	
	private int fromHex(String s, EColorConversionDelegate.RGBComponent cc) {
		switch (cc) {
		case RED:
			s = s.substring(0,2);
			break;
		case GREEN:
			s = s.substring(2,4);
			break;
		case BLUE:
			s = s.substring(4,6);
			break;
		}
		return Integer.parseInt(s, 16);
	}

	
	/**
	 * This is an EditControl that wraps a ColorSelector widget for editing RGB values.
	 */
	public class ColorControl extends EditControl {
		private ColorSelector colorSelector;
	    
	    public ColorControl(Composite parent, int style) {
	    	super(parent, style);
	    	this.setLayout(new RowLayout());
	    	
	    	colorSelector = new ColorSelector(this);
	    	colorSelector.getButton().addSelectionListener(this);
	    }
	    
		@Override
		public Object getValue() {
			return colorSelector.getColorValue();
		}
		
		@Override
		public boolean setValue(Object value) {
			if (value instanceof RGB) {
				colorSelector.setColorValue((RGB)value);
				return true;
			}
			return false;
		}
	}
}