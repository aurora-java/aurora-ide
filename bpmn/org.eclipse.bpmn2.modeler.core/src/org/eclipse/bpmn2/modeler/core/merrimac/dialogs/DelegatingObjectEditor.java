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

import org.eclipse.bpmn2.modeler.core.EditControlProvider;
import org.eclipse.bpmn2.modeler.core.EditControlProvider.EditControl;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractDetailComposite;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * This is an ObjectEditor that delegates to an EditControl provider for constructing
 * the edit widget(s). This ObjectEditor is used in AbstractDetailComposite for
 * EAttributes whose DataType is not one of the primitive types (EString, EBoolean, etc.)
 * 
 * The EditControlProvider is typically an EMF ConversionDelegate that implements this interface.
 * Since the ConversionDelegate already knows how to convert the DataType to and from Strings,
 * it makes sense that it should provide an editing widget for the UI. 
 */
public class DelegatingObjectEditor extends ObjectEditor {

	EditControlProvider provider;
	EditControl control;
	
	public DelegatingObjectEditor(AbstractDetailComposite parent, EObject object, EStructuralFeature feature, EditControlProvider provider) {
		super(parent, object, feature);
		this.provider = provider;
	}

	@Override
	protected Control createControl(Composite composite, String label, int style) {
		createLabel(composite, label);
		
		control = provider.createControl(composite, style);
		control.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		control.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setValue( getValue() );
			}
		});
		Object value = getBusinessObjectDelegate().getValue(object, feature);
		control.setValue(value);
		return control;
	}

	@Override
	public Object getValue() {
		return control.getValue();
	}
	
	@Override
	public void notifyChanged(Notification notification) {
		super.notifyChanged(notification);
		Object value = getBusinessObjectDelegate().getValue(object, feature);
		control.setValue(value);
	}

}
