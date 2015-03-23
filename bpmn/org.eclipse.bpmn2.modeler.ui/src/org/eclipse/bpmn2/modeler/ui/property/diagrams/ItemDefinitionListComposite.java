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
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.swt.widgets.Composite;

public class ItemDefinitionListComposite extends DefaultListComposite {

	public ItemDefinitionListComposite(AbstractBpmn2PropertySection section, int style) {
		super(section, style);
	}

	public ItemDefinitionListComposite(AbstractBpmn2PropertySection section) {
		super(section, DEFAULT_STYLE|EDIT_BUTTON);
	}

	public ItemDefinitionListComposite(Composite parent, int style) {
		super(parent, style);
	}

	public ItemDefinitionListComposite(Composite parent) {
		super(parent, DEFAULT_STYLE|EDIT_BUTTON);
	}

	public ListCompositeColumnProvider getColumnProvider(EObject object, EStructuralFeature feature) {
		if (columnProvider==null) {
			columnProvider = new ListCompositeColumnProvider(this);
			EClass eclass = PACKAGE.getItemDefinition();
			
			TableColumn tc = columnProvider.add(object,PACKAGE.getItemDefinition_StructureRef());
			if (tc!=null)
				tc.setEditable(false);
			columnProvider.add(object,PACKAGE.getItemDefinition_ItemKind());
			columnProvider.add(object,PACKAGE.getItemDefinition_IsCollection());
		}
		return columnProvider;
	}
}
