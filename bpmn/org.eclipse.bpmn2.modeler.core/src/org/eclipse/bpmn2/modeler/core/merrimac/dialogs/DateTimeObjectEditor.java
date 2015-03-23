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

package org.eclipse.bpmn2.modeler.core.merrimac.dialogs;

import java.util.Date;

import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractDetailComposite;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Shell;

/**
 *
 */
public class DateTimeObjectEditor extends ReadonlyTextObjectEditor {

	/**
	 * @param parent
	 * @param object
	 * @param feature
	 */
	public DateTimeObjectEditor(AbstractDetailComposite parent, EObject object, EStructuralFeature feature) {
		super(parent, object, feature);
	}


	@Override
	protected Control createControl(Composite composite, String label, int style) {
		return super.createControl(composite, label, style);
	}


	@Override
	protected void buttonClicked(int buttonId) {
		Object value = object.eGet(feature);
		if (value==null)
			value = new Date();
		if (!(value instanceof Date)) {
			value = new Date();
		}
		String title = "Select "+getLabel().getText();
		DateTimeDialog dialog = new DateTimeDialog(getControl().getShell(), title, style);
		dialog.setDate((Date)value);
		if (dialog.open() == Window.OK) {
			super.setValue(dialog.getDate());
		}
	}
	
	public static class DateTimeDialog extends Dialog {

		private Button okButton;
		String title;
		private Date result;
		private DateTime calendar;
		private DateTime date;
		private DateTime time;
		private int style = SWT.CALENDAR | SWT.TIME;
		
		/**
		 * @param parentShell
		 */
		protected DateTimeDialog(Shell parentShell, String title, int syle) {
			super(parentShell);
			this.title = title;
		}
		
		public void setDate(Date date) {
			this.result = date == null ? new Date() : date;
		}

		protected Control createDialogArea(Composite parent) {
	        Composite composite = createMyComposite(parent);
	        if ((style & (SWT.CALENDAR | SWT.DATE | SWT.TIME)) == 0) 
	        	style = SWT.CALENDAR | SWT.TIME;
	        
	        if ((style & SWT.CALENDAR)!=0) {
				calendar = new DateTime(composite, SWT.CALENDAR);
				calendar.setDate(result.getYear(), result.getMonth(), result.getDay());
	        }
	        else if ((style & SWT.DATE)!=0) {
				date = new DateTime(composite, SWT.DATE);
				date.setDate(result.getYear(), result.getMonth(), result.getDay());
	        }
	        if ((style & SWT.TIME)!=0) {
				time = new DateTime(composite, SWT.TIME);
				time.setTime(result.getHours(), result.getMinutes(), result.getSeconds());
	        }
	        
	        return composite;
		}
		
		private Composite createMyComposite(Composite parent) {
			// create a composite with standard margins and spacing
			Composite composite = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout(1, false);
			layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
			layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
			layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
			composite.setLayout(layout);
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			applyDialogFont(composite);
			return composite;
		}
		
	    @Override
		protected void configureShell(Shell shell) {
	        super.configureShell(shell);
	        if (title!=null)
	        	shell.setText(title);
	        else
	        	shell.setText("Select Date/Time");
	    }
	    
	    @Override
		protected void createButtonsForButtonBar(Composite parent) {
	        // create OK and Cancel buttons by default
	        okButton = createButton(parent, IDialogConstants.OK_ID,
	                IDialogConstants.OK_LABEL, true);
	        createButton(parent, IDialogConstants.CANCEL_ID,
	                IDialogConstants.CANCEL_LABEL, false);
	    }

	    @Override
		protected void buttonPressed(int buttonId) {
	        if (buttonId == IDialogConstants.OK_ID) {
	        	result = new Date();
	        	if (calendar!=null) {
	        		result.setYear(calendar.getYear() - 1900);
	            	result.setMonth(calendar.getMonth());
	            	result.setDate(calendar.getDay());
	        	}
	        	if (date!=null) {
	        		result.setYear(date.getYear() - 1900);
	            	result.setMonth(date.getMonth());
	            	result.setDate(date.getDay());
	        	}
	        	if (time!=null) {
	            	result.setHours(time.getHours());
	            	result.setMinutes(time.getMinutes());
	            	result.setSeconds(time.getSeconds());
	        	}
	        } else {
	            result = null;
	        }
	        super.buttonPressed(buttonId);
	    }
		
		public Date getDate()  {
			return result;
		}
	}
}
