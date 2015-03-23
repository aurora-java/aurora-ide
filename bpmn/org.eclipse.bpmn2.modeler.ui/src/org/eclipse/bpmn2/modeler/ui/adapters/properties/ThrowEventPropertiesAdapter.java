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

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.FeatureDescriptor;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * @author Bob Brodt
 *
 */
public class ThrowEventPropertiesAdapter extends EventPropertiesAdapter<ThrowEvent> {

	/**
	 * @param adapterFactory
	 * @param object
	 */
	public ThrowEventPropertiesAdapter(AdapterFactory adapterFactory, ThrowEvent object) {
		super(adapterFactory, object);
		
		EStructuralFeature feature;
		
		feature = Bpmn2Package.eINSTANCE.getThrowEvent_DataInputs();
    	setFeatureDescriptor(feature, new FeatureDescriptor<ThrowEvent>(this,object, feature) {
    		
			@Override
			public EObject createFeature(Resource resource, EClass eclass) {
				InputSet inputSet = object.getInputSet();
				if (inputSet==null) {
					inputSet = Bpmn2ModelerFactory.create(InputSet.class);
					object.setInputSet(inputSet);
				}
				DataInput dataInput = DataInputPropertiesAdapter.createDataInput(resource, object.getDataInputs());
				inputSet.getDataInputRefs().add(dataInput);

				return dataInput;
			}
    	});
	}
}
