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

package org.eclipse.bpmn2.modeler.ui.adapters.properties;

import java.util.Hashtable;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.Operation;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesProvider;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Bob Brodt
 *
 */
public class MessageRefFeatureDescriptor<T extends BaseElement> extends RootElementRefFeatureDescriptor<T> {
	
	public MessageRefFeatureDescriptor(ExtendedPropertiesAdapter<T> owner, T object, EStructuralFeature feature) {
		super(owner, object, feature);
	}

	@Override
	public Hashtable<String, Object> getChoiceOfValues() {
		Hashtable<String, Object> choices = super.getChoiceOfValues();
		Operation operation = null;
		EStructuralFeature f = object.eClass().getEStructuralFeature("operationRef"); //$NON-NLS-1$
		if (f!=null && object.eGet(f)!=null) {
			operation = (Operation) object.eGet(f);
		}
		
		if (operation==null) {
			choices = super.getChoiceOfValues();
		}
		else {
			choices = new Hashtable<String,Object>();
			Message message = operation.getOutMessageRef();
			if (message!=null)
				choices.put(ExtendedPropertiesProvider.getTextValue(message), message);
			message = operation.getInMessageRef();
			if (message!=null)
				choices.put(ExtendedPropertiesProvider.getTextValue(message), message);
		}
		return choices;
	}
}
