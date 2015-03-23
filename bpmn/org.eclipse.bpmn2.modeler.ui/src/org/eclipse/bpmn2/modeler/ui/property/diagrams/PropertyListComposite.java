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
package org.eclipse.bpmn2.modeler.ui.property.diagrams;

import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractBpmn2PropertySection;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.DefaultListComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.ListCompositeColumnProvider;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.TableColumn;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.swt.widgets.Composite;

public class PropertyListComposite extends DefaultListComposite {

	public PropertyListComposite(AbstractBpmn2PropertySection section) {
		super(section, DEFAULT_STYLE|EDIT_BUTTON);
	}
	
	public PropertyListComposite(Composite parent) {
		this(parent, DEFAULT_STYLE|EDIT_BUTTON);
	}
	
	public PropertyListComposite(Composite parent, int style) {
		super(parent,style);
	}
	
	@Override
	public void bindList(EObject theobject, EStructuralFeature thefeature) {
		super.bindList(theobject, thefeature);
	}
	
	public ListCompositeColumnProvider getColumnProvider(EObject object, EStructuralFeature feature) {
		if (columnProvider==null) {
			columnProvider = new ListCompositeColumnProvider(this);
			columnProvider.add(new TableColumn(object, PACKAGE.getProperty_Name()));
			columnProvider.add(new TableColumn(object, PACKAGE.getItemAwareElement_ItemSubjectRef()));
		}
		return columnProvider;
	}
}