/*******************************************************************************
 * Copyright (c) 2011, 2012 Red Hat, Inc. 
 * All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 *
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.preferences;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.swt.widgets.Composite;

public class SelectableComboFieldEditor extends ComboFieldEditor {

	public SelectableComboFieldEditor(String name, String labelText, String[][] entryNamesAndValues, Composite parent) {
		super(name, labelText, entryNamesAndValues, parent);
	}

	public String getSelectedValue() {
		doStore();
		return getPreferenceStore().getString(getPreferenceName());
	}

	public void setSelectedValue(String newValue) {
		getPreferenceStore().setValue(getPreferenceName(), newValue);
		doLoad();
	}
}
