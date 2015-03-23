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
 * @author Innar Made
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.property.events;

import org.eclipse.bpmn2.ConditionalEventDefinition;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.modeler.core.adapters.InsertionAdapter;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractBpmn2PropertySection;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.ui.property.data.ExpressionDetailComposite;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.widgets.Composite;

public class ConditionalEventDefinitionDetailComposite extends ExpressionDetailComposite {

	public ConditionalEventDefinitionDetailComposite(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @param section
	 */
	public ConditionalEventDefinitionDetailComposite(AbstractBpmn2PropertySection section) {
		super(section);
	}

	@Override
	public void setBusinessObject(EObject object) {
		if (object instanceof ConditionalEventDefinition) {
			getDiagramEditor();
			addDomainListener();
			
			ConditionalEventDefinition ced = (ConditionalEventDefinition)object;
			if (ced.getCondition()==null) {
				object = createModelObject(FormalExpression.class);
				InsertionAdapter.add(ced,
						PACKAGE.getConditionalEventDefinition_Condition(),
						object);
			}
			else
				object = ced.getCondition();
		}
		super.setBusinessObject(object);
	}
	
}
