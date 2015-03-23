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
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.OutputSet;
import org.eclipse.bpmn2.StartEvent;
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
public class CatchEventPropertiesAdapter extends EventPropertiesAdapter<CatchEvent> {

	/**
	 * @param adapterFactory
	 * @param object
	 */
	public CatchEventPropertiesAdapter(AdapterFactory adapterFactory, CatchEvent object) {
		super(adapterFactory, object);
		
		EStructuralFeature feature;

    	feature = Bpmn2Package.eINSTANCE.getCatchEvent_DataOutputs();
    	setFeatureDescriptor(feature, new FeatureDescriptor<CatchEvent>(this, object, feature) {
   		
			@Override
			public EObject createFeature(Resource resource, EClass eclass) {
				OutputSet outputSet = object.getOutputSet();
				if (outputSet==null) {
					outputSet = Bpmn2ModelerFactory.create(OutputSet.class);
					object.setOutputSet(outputSet);
				}
				DataOutput dataOutput = DataOutputPropertiesAdapter.createDataOutput(resource, object.getDataOutputs());
				outputSet.getDataOutputRefs().add(dataOutput);

				return dataOutput;
			}
    	});
    	
    	if (object instanceof StartEvent) {
	    	feature = Bpmn2Package.eINSTANCE.getStartEvent_IsInterrupting();
	    	setFeatureDescriptor(feature, new FeatureDescriptor<CatchEvent>(this, object, feature) {

				@Override
				public String getLabel() {
					return "Cancel Activity";
				}
	    	});
    	}
	}
}
