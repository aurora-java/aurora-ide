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
import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Interface;
import org.eclipse.bpmn2.Operation;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.FeatureDescriptor;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Bob Brodt
 *
 */
public class OperationRefFeatureDescriptor<T extends BaseElement> extends FeatureDescriptor<T> {

	public OperationRefFeatureDescriptor(ExtendedPropertiesAdapter<T> owner, T object, EStructuralFeature feature) {
		super(owner, object, feature);
	}

	@Override
	public String getChoiceString(Object value) {
		Operation op = (Operation)value;
		Interface intf = (Interface)op.eContainer();
		return intf.getName() + "/" + op.getName(); //$NON-NLS-1$
	}

	@Override
	public Hashtable<String, Object> getChoiceOfValues() {
		Hashtable<String,Object> choices = super.getChoiceOfValues();

		// collect all defined Interfaces and add their Operations to the list of available choices
		// Whether or not the Interface is actually supported by the underlying Process is a job
		// for validation
		Definitions definitions = ModelUtil.getDefinitions(object);
		List<Interface> interfaces = ModelUtil.getAllRootElements(definitions, Interface.class);
		
		for (Interface intf : interfaces) {
			for (Operation operation : intf.getOperations()) {
				choices.put(getChoiceString(operation), operation);
			}
		}

		return choices;
	}
}
