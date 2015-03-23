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
package org.eclipse.bpmn2.modeler.ui.property.data;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractBpmn2PropertySection;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractListComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.DefaultListComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.ListCompositeColumnProvider;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.TableColumn;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.swt.widgets.Composite;

public class MessageListComposite extends DefaultListComposite {

	public MessageListComposite(AbstractBpmn2PropertySection section, int style) {
		super(section, style);
		// TODO Auto-generated constructor stub
	}

	public MessageListComposite(AbstractBpmn2PropertySection section) {
		super(section, DEFAULT_STYLE);
	}

	public MessageListComposite(Composite parent, int style) {
		super(parent, DEFAULT_STYLE);
	}
	
	@Override
	public ListCompositeColumnProvider getColumnProvider(EObject object, EStructuralFeature feature) {
		if (columnProvider==null) {
			columnProvider = new ListCompositeColumnProvider(this);
			columnProvider.add(new TableColumn(object,Bpmn2Package.eINSTANCE.getMessage_Name()));
			columnProvider.add(new TableColumn(object,Bpmn2Package.eINSTANCE.getMessage_ItemRef()));
		}
		return columnProvider;
	}
}
