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
package org.eclipse.bpmn2.modeler.ui.adapters.properties;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.CallableElement;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.LoopCharacteristics;
import org.eclipse.bpmn2.MultiInstanceLoopCharacteristics;
import org.eclipse.bpmn2.OutputSet;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.adapters.FeatureDescriptor;
import org.eclipse.bpmn2.modeler.ui.adapters.properties.OutputSetPropertiesAdapter.InputSetFeatureDescriptor;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class InputSetPropertiesAdapter extends ExtendedPropertiesAdapter<InputSet> {

	public InputSetPropertiesAdapter(AdapterFactory adapterFactory, InputSet object) {
		super(adapterFactory, object);

    	EStructuralFeature f = Bpmn2Package.eINSTANCE.getInputSet_DataInputRefs();
		setProperty(f, UI_IS_MULTI_CHOICE, Boolean.TRUE);
		setFeatureDescriptor(f, new DataInputFeatureDescriptor(this,object, f));

    	f = Bpmn2Package.eINSTANCE.getInputSet_OptionalInputRefs();
		setProperty(f, UI_IS_MULTI_CHOICE, Boolean.TRUE);
		setFeatureDescriptor(f, new DataInputFeatureDescriptor(this,object, f));

    	f = Bpmn2Package.eINSTANCE.getInputSet_WhileExecutingInputRefs();
		setProperty(f, UI_IS_MULTI_CHOICE, Boolean.TRUE);
		setFeatureDescriptor(f, new DataInputFeatureDescriptor(this,object, f));

    	f = Bpmn2Package.eINSTANCE.getInputSet_OutputSetRefs();
		setProperty(f, UI_IS_MULTI_CHOICE, Boolean.TRUE);
		setFeatureDescriptor(f, new OutputSetFeatureDescriptor(this,object, f));
	}

	protected class DataInputFeatureDescriptor extends FeatureDescriptor<InputSet> {
		
		public DataInputFeatureDescriptor(ExtendedPropertiesAdapter<InputSet> owner, InputSet object,
				EStructuralFeature feature) {
			super(owner, object, feature);
		}

		@Override
		public Hashtable<String, Object> getChoiceOfValues() {
			Hashtable<String, Object> values = new Hashtable<String, Object>();
			List<DataInput> valid = new ArrayList<DataInput>();
			if (feature == Bpmn2Package.eINSTANCE.getInputSet_DataInputRefs()) {
				// choices are all DataInputs in scope
				EObject container = object.eContainer();
				while (container!=null) {
					if (container instanceof ThrowEvent) {
						valid.addAll( ((ThrowEvent)container).getDataInputs() );
					}
					else if (container instanceof SubProcess) {
						// a SubProcess must not define DataInputs and DataOutputs directly
						// only indirectly via MultiInstanceLoopCharacteristics.
						LoopCharacteristics lc = ((SubProcess)container).getLoopCharacteristics();
						if (lc instanceof MultiInstanceLoopCharacteristics) {
							DataInput i = ((MultiInstanceLoopCharacteristics)lc).getInputDataItem();
							if (i!=null)
								valid.add(i);
						}
					}
					else if (container instanceof Activity) {
						InputOutputSpecification ioSpec = ((Activity)container).getIoSpecification();
						if (ioSpec!=null)
							valid.addAll(ioSpec.getDataInputs());
					}
					else if (container instanceof CallableElement) {
						InputOutputSpecification ioSpec = ((CallableElement)container).getIoSpecification();
						if (ioSpec!=null)
							valid.addAll(ioSpec.getDataInputs());
					}
					container = container.eContainer();
				}
			}
			else {
				// choices are only the DataInputs listed in "InputSet.dataInputRefs"
				valid.addAll(object.getDataInputRefs());
			}
			for (DataInput data : valid) {
				values.put( ExtendedPropertiesProvider.getTextValue(data), data);
			}
			return values;
		}		
	}

	protected class OutputSetFeatureDescriptor extends FeatureDescriptor<InputSet> {
		
		public OutputSetFeatureDescriptor(ExtendedPropertiesAdapter<InputSet> owner, InputSet object,
				EStructuralFeature feature) {
			super(owner, object, feature);
		}

		@Override
		public Hashtable<String, Object> getChoiceOfValues() {
			Hashtable<String, Object> values = new Hashtable<String, Object>();
			EObject container = object.eContainer();
			if (container instanceof InputOutputSpecification) {
				// an InputSet.outputSetRefs can only reference OutputSets in the same InputOutputSpecification
				InputOutputSpecification ioSpec = (InputOutputSpecification)container;
				for (OutputSet os : ioSpec.getOutputSets()) {
					values.put( ExtendedPropertiesProvider.getTextValue(os), os);
				}
			}
			return values;
		}		
	}
}
