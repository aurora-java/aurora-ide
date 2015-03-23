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

import java.util.List;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.OutputSet;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.FeatureDescriptor;
import org.eclipse.bpmn2.modeler.core.adapters.ObjectDescriptor;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * @author Bob Brodt
 *
 */
public class IoSpecificationPropertiesAdapter extends ExtendedPropertiesAdapter<InputOutputSpecification> {

	/**
	 * @param adapterFactory
	 * @param object
	 */
	public IoSpecificationPropertiesAdapter(AdapterFactory adapterFactory, InputOutputSpecification object) {
		super(adapterFactory, object);
		
		EStructuralFeature feature;
		
		feature = Bpmn2Package.eINSTANCE.getInputOutputSpecification_DataInputs();
    	setFeatureDescriptor(feature, new FeatureDescriptor<InputOutputSpecification>(this,object, feature) {
    		
			@Override
			public EObject createFeature(Resource resource, EClass eclass) {
				List<InputSet> inputSets = object.getInputSets();
				if (inputSets.size()==0) {
					inputSets.add(Bpmn2ModelerFactory.create(InputSet.class));
				}
				InputSet inputSet = inputSets.get(0);
				DataInput dataInput = DataInputPropertiesAdapter.createDataInput(resource, object.getDataInputs());
				inputSet.getDataInputRefs().add(dataInput);

				return dataInput;
			}
    	});

    	feature = Bpmn2Package.eINSTANCE.getInputOutputSpecification_DataOutputs();
    	setFeatureDescriptor(feature, new FeatureDescriptor<InputOutputSpecification>(this,object, feature) {
   		
			@Override
			public EObject createFeature(Resource resource, EClass eclass) {
				List<OutputSet> outputSets = object.getOutputSets();
				if (outputSets.size()==0) {
					outputSets.add(Bpmn2ModelerFactory.create(OutputSet.class));
				}
				OutputSet outputSet = outputSets.get(0);
				DataOutput dataOutput = DataOutputPropertiesAdapter.createDataOutput(resource, object.getDataOutputs());
				outputSet.getDataOutputRefs().add(dataOutput);

				return dataOutput;
			}
    	});
    	
		setObjectDescriptor(new ObjectDescriptor<InputOutputSpecification>(this,object) {
			
			@Override
			public InputOutputSpecification createObject(Resource resource, EClass eclass) {
				InputOutputSpecification ioSpec = Bpmn2ModelerFactory.eINSTANCE.createInputOutputSpecification();
				ModelUtil.setID(ioSpec, resource);
				InputSet is = Bpmn2ModelerFactory.eINSTANCE.createInputSet();
				ModelUtil.setID(is, resource);
				ioSpec.getInputSets().add(is);
				OutputSet os = Bpmn2ModelerFactory.eINSTANCE.createOutputSet();
				ModelUtil.setID(os, resource);
				ioSpec.getOutputSets().add(os);
				return ioSpec;
			}
		});    	
	}
}
