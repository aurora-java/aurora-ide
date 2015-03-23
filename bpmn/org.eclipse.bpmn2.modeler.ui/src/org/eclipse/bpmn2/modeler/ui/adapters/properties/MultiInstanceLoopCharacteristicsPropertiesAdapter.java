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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.DataObjectReference;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.MultiInstanceLoopCharacteristics;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.FeatureDescriptor;
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
public class MultiInstanceLoopCharacteristicsPropertiesAdapter extends ExtendedPropertiesAdapter<MultiInstanceLoopCharacteristics> {

	final static EStructuralFeature LOOP_DATA_INPUT_REF = Bpmn2Package.eINSTANCE.getMultiInstanceLoopCharacteristics_LoopDataInputRef();
	final static EStructuralFeature INPUT_DATA_ITEM = Bpmn2Package.eINSTANCE.getMultiInstanceLoopCharacteristics_InputDataItem();
	final static EStructuralFeature LOOP_DATA_OUTPUT_REF = Bpmn2Package.eINSTANCE.getMultiInstanceLoopCharacteristics_LoopDataOutputRef();
	final static EStructuralFeature OUTPUT_DATA_ITEM = Bpmn2Package.eINSTANCE.getMultiInstanceLoopCharacteristics_OutputDataItem();
	
	/**
	 * @param adapterFactory
	 * @param object
	 */
	public MultiInstanceLoopCharacteristicsPropertiesAdapter(AdapterFactory adapterFactory, MultiInstanceLoopCharacteristics object) {
		super(adapterFactory, object);

		setFeatureDescriptor(LOOP_DATA_INPUT_REF, new LoopCharacteristicsDataIoFeatureDescriptor(this, object, LOOP_DATA_INPUT_REF));
		setProperty(LOOP_DATA_INPUT_REF, UI_IS_MULTI_CHOICE, Boolean.TRUE);
		setProperty(LOOP_DATA_INPUT_REF, UI_CAN_EDIT, Boolean.TRUE);
		setProperty(LOOP_DATA_INPUT_REF, UI_CAN_CREATE_NEW, Boolean.TRUE);
		
//		setFeatureDescriptor(INPUT_DATA_ITEM, new LoopCharacteristicsDataIoFeatureDescriptor(adapterFactory,object, INPUT_DATA_ITEM));
//		setProperty(INPUT_DATA_ITEM, UI_IS_MULTI_CHOICE, Boolean.TRUE);

		setFeatureDescriptor(LOOP_DATA_OUTPUT_REF, new LoopCharacteristicsDataIoFeatureDescriptor(this, object, LOOP_DATA_OUTPUT_REF));
		setProperty(LOOP_DATA_OUTPUT_REF, UI_IS_MULTI_CHOICE, Boolean.TRUE);
		setProperty(LOOP_DATA_OUTPUT_REF, UI_CAN_EDIT, Boolean.TRUE);
		setProperty(LOOP_DATA_OUTPUT_REF, UI_CAN_CREATE_NEW, Boolean.TRUE);

//		setFeatureDescriptor(OUTPUT_DATA_ITEM, new LoopCharacteristicsDataIoFeatureDescriptor(adapterFactory,object, OUTPUT_DATA_ITEM));
//		setProperty(OUTPUT_DATA_ITEM, UI_IS_MULTI_CHOICE, Boolean.TRUE);
	}

	/**
	 * This class calculates the Loop Input/Output collections and Input/Output parameters that are in scope for the 
	 * Activity that owns this Multi-Instance Loop Characteristics object. From the BPMN2 spec:
	 * 
	 * Loop Data Input/Output (the collection):
	 *    For Tasks it is a reference to a Data Input/Output which is part of the Activity’s InputOutputSpecification.
	 *    For Sub-Processes it is a reference to a collection-valued Data Object in the context that is visible to the
	 *    Sub-Processes.
	 *    
	 * Input/Output Parameter (the instance parameter):
	 *   This Data Input/Output can be the source/target of DataInput/OutputAssociation to a data input/output of the
	 *   Activity’s InputOutputSpecification.
	 *   The type of this Data Input/Output MUST be the scalar of the type defined for the loopDataInput/Output.
	 */
	protected class LoopCharacteristicsDataIoFeatureDescriptor extends FeatureDescriptor<MultiInstanceLoopCharacteristics> {

		public LoopCharacteristicsDataIoFeatureDescriptor(
				ExtendedPropertiesAdapter<MultiInstanceLoopCharacteristics> owner,
				MultiInstanceLoopCharacteristics object, EStructuralFeature feature) {
			super(owner, object, feature);
		}

		@Override
		public EObject createFeature(Resource resource, EClass eclass) {
			EObject value = super.createFeature(resource, eclass);
			// if the new object is the collection reference, we need to attach it to the
			// activity's InputOutputSpecification.
			if (feature==LOOP_DATA_INPUT_REF || feature==LOOP_DATA_OUTPUT_REF) {
				Activity container = (Activity)ModelUtil.getContainer(object);
				EStructuralFeature f = container.eClass().getEStructuralFeature("ioSpecification"); //$NON-NLS-1$
				if (f!=null) {
					InputOutputSpecification ioSpecification = (InputOutputSpecification)container.eGet(f);
					if (ioSpecification==null) {
						ioSpecification = Bpmn2ModelerFactory.createFeature(container, f, InputOutputSpecification.class);
					}
					if (value instanceof DataInput)
						ioSpecification.getDataInputs().add((DataInput)value);
					else
						ioSpecification.getDataOutputs().add((DataOutput)value);
				}
			}
			return value;
		}
		
		@Override
		public Hashtable<String, Object> getChoiceOfValues() {
			Hashtable<String, Object> choices = new Hashtable<String, Object>();
			
			Activity container = (Activity)ModelUtil.getContainer(object);
			List values = new ArrayList<EObject>();
			if (feature == LOOP_DATA_INPUT_REF || feature == LOOP_DATA_OUTPUT_REF) {
//				if (container instanceof Task)
				{
					EStructuralFeature f = container.eClass().getEStructuralFeature("ioSpecification"); //$NON-NLS-1$
					if (f!=null) {
						InputOutputSpecification ioSpecification = (InputOutputSpecification)container.eGet(f);
						if (ioSpecification!=null) {
							if (feature == LOOP_DATA_INPUT_REF)
								values.addAll(ioSpecification.getDataInputs());
							else
								values.addAll(ioSpecification.getDataOutputs());
						}
					}
				}
//				else 
				if (container instanceof SubProcess) {
					// Collect all DataObjects from Process and SubProcess ancestors
					// DataObjects are FlowElements, so we will have to weed those out from other FlowElements.
					List<EObject> flowElements = ModelUtil.collectAncestorObjects(object, "flowElements", new Class[] {Process.class, SubProcess.class}); //$NON-NLS-1$
					for (EObject fe : flowElements) {
						if (fe instanceof DataObjectReference) {
							fe = ((DataObjectReference)fe).getDataObjectRef();
						}
						if (!(fe instanceof DataObject)) {
							fe = null;
						}
						if (fe!=null && !values.contains(fe))
							values.add(fe);
					}
				}
			}
			else {
				// INPUT_DATA_ITEM or OUTPUT_DATA_ITEM feature
				// TODO: This part of the spec is unclear to me. It looks like the Data Input/Output Items need to
				// be contained in the MI Loop Characteristics. Something like this:
				//
				// <multiInstanceLoopCharacteristics id="MI_6">
				//     <loopDataInputRef>DataObject_3</bpmn2:loopDataInputRef>
				//     <inputDataItem xsi:type="bpmn2:tDataInput" id="DataInput_1" itemSubjectRef="ItemDefinition_1" name="input_param"/>
				// </multiInstanceLoopCharacteristics>
			}
			
			if (values!=null) {
				for (Object p : values) {
					choices.put( getChoiceString(p), p);
				}
			}
			super.setChoiceOfValues(choices);
			return super.getChoiceOfValues();
		}
	}
}
