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
import org.eclipse.bpmn2.modeler.ui.Activator;
import org.eclipse.bpmn2.modeler.ui.Messages;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Label;

/**
 * EDataType ConversionDelegate for Font objects.
 * 
 * The class is registered with our EDataTypeConversionFactory with the name of a data type;
 * in this case the data type name is "EFont" in keeping with the EMF naming convention
 * for primitive data types (e.g. "EString", "EBoolean", etc.)
 */
public class EFontConversionDelegate extends DefaultConversionDelegate {

	private static FontData systemFontData;
	
	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.EDataType.Internal.ConversionDelegate#convertToString(java.lang.Object)
	 */
	@Override
	public String convertToString(Object value) {
		if (value instanceof FontData) {
			FontData fd = (FontData) value;
			int style = fd.getStyle();
			boolean isItalic = (style & SWT.ITALIC) != 0;
			boolean isBold = (style & SWT.BOLD) != 0;
			return new String(
					fd.getName() + "," + //$NON-NLS-1$
					fd.height + "," + //$NON-NLS-1$
					(isItalic ? "I" : "-") + "," + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					(isBold ? "B" : "-") //$NON-NLS-1$ //$NON-NLS-2$
			);
		}
		return Display.getDefault().getSystemFont().getFontData()[0].toString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.EDataType.Internal.ConversionDelegate#createFromString(java.lang.String)
	 * 
	 * This method MUST accept an empty String and construct a "default" object. This is required so that
	 * clients can determine the java type (class) of objects handled by this ConversionDelegate.
	 */
	@Override
	public Object createFromString(String literal) {
		try {
			String a[] = literal.split(",");
			if (a.length==4) {
				String name = a[0];
				int height = (int)Math.round(Double.parseDouble(a[1]));
				int style = 0;
				if ("I".equals(a[2]))
					style |= SWT.ITALIC;
				if ("B".equals(a[3]))
					style |= SWT.BOLD;
				return new FontData(name, height, style);
			}
		}
		catch (Exception e) {
			Activator.logError(e);
		}
		return getSystemFontData();
	}
	
	private static FontData getSystemFontData() {
		if (systemFontData==null) {
			try {
				systemFontData = Display.getDefault().getSystemFont().getFontData()[0];
			}
			catch (Exception e) {}
		}
		if (systemFontData!=null)
			return systemFontData;
		return new FontData("arial",12,SWT.NORMAL);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.EditControlProvider#createControl(org.eclipse.swt.widgets.Composite, int)
	 */
	@Override
	public EditControl createControl(Composite parent, int style) {
		return new FontControl(parent, style);
	}

	
	/**
	 * This is an EditControl that wraps a FontDialog widget for editing Fonts.
	 */
	public class FontControl extends EditControl {
	    private Button changeFontButton = null;

	    /**
	     * Font data for the chosen font button, or <code>null</code> if none.
	     */
	    private FontData[] selectedFont;
	    private Font previewLabelFont;

	    /**
	     * The label that displays the selected font, or <code>null</code> if none.
	     */
	    private Label previewLabel;

	    public FontControl(Composite parent, int style) {
	    	super(parent, style);
	    	this.setLayout(new GridLayout(2, false));

	    	previewLabel = new Label(this, SWT.LEFT);
	    	previewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
            previewLabel.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent event) {
                    previewLabel = null;
    	            if (previewLabelFont!=null) {
    	            	previewLabelFont.dispose();
    	            	previewLabelFont = null;
    	            }
                }
            });
	    	
            changeFontButton = new Button(this, SWT.PUSH);
            changeFontButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
			changeFontButton.setText(Messages.Bpmn2EditorPreferencePage_Change_Button);
            changeFontButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event) {
                    FontDialog fontDialog = new FontDialog(changeFontButton.getShell());
                    if (selectedFont != null) {
						fontDialog.setFontList(selectedFont);
					}
                    FontData font = fontDialog.open();
                    if (font != null) {
                        FontData[] oldFont = selectedFont;
                        if (oldFont == null) {
							oldFont = JFaceResources.getDefaultFont().getFontData();
						}
                        setSelectedFont(font);
                    }

                }
            });
            changeFontButton.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent event) {
                    changeFontButton = null;
                }
            });

            changeFontButton.addSelectionListener(this);
	    }

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.EditControlProvider.EditControl#getValue()
		 */
		@Override
		public Object getValue() {
	    	if (selectedFont!=null && selectedFont.length>0) {
				return selectedFont[0];
	    	}
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.EditControlProvider.EditControl#setValue(java.lang.Object)
		 */
		@Override
		public boolean setValue(Object value) {
			if (value instanceof FontData) {
				setSelectedFont((FontData)value);
			}
			else
				setSelectedFont(getDefaultFontData()[0]);
			return true;
		}
	    
	    private void setSelectedFont(FontData fd) {

	        FontData[] bestFont = JFaceResources.getFontRegistry().filterData(
	        		new FontData[]{fd}, previewLabel.getDisplay());

	        //if we have nothing valid do as best we can
	        if (bestFont == null) {
				bestFont = getDefaultFontData();
			}

	        //Now cache this value in the receiver
	        this.selectedFont = bestFont;

	        if (previewLabel != null) {
	            if (previewLabelFont!=null)
	            	previewLabelFont.dispose();
	            previewLabelFont = new Font(Display.getDefault(), selectedFont[0]);
	            previewLabel.setFont(previewLabelFont);
	            previewLabel.setText(StringConverter.asString(selectedFont[0]));
	            layout();
	        }
	    }

	    /**
	     * Get the system default font data.
	     * @return FontData[]
	     */
	    private FontData[] getDefaultFontData() {
	        return previewLabel.getDisplay().getSystemFont().getFontData();
	    }
	}
}