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
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.LoopCharacteristics;
import org.eclipse.bpmn2.MultiInstanceLoopCharacteristics;
import org.eclipse.bpmn2.OutputSet;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.adapters.FeatureDescriptor;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class OutputSetPropertiesAdapter extends ExtendedPropertiesAdapter<OutputSet> {

	public OutputSetPropertiesAdapter(AdapterFactory adapterFactory, OutputSet object) {
		super(adapterFactory, object);

    	EStructuralFeature f = Bpmn2Package.eINSTANCE.getOutputSet_DataOutputRefs();
		setProperty(f, UI_IS_MULTI_CHOICE, Boolean.TRUE);
		setFeatureDescriptor(f, new DataOutputFeatureDescriptor(this,object, f));

    	f = Bpmn2Package.eINSTANCE.getOutputSet_OptionalOutputRefs();
		setProperty(f, UI_IS_MULTI_CHOICE, Boolean.TRUE);
		setFeatureDescriptor(f, new DataOutputFeatureDescriptor(this,object, f));

    	f = Bpmn2Package.eINSTANCE.getOutputSet_WhileExecutingOutputRefs();
		setProperty(f, UI_IS_MULTI_CHOICE, Boolean.TRUE);
		setFeatureDescriptor(f, new DataOutputFeatureDescriptor(this,object, f));

    	f = Bpmn2Package.eINSTANCE.getOutputSet_InputSetRefs();
		setProperty(f, UI_IS_MULTI_CHOICE, Boolean.TRUE);
		setFeatureDescriptor(f, new InputSetFeatureDescriptor(this,object, f));
	}

	protected class DataOutputFeatureDescriptor extends FeatureDescriptor<OutputSet> {

		public DataOutputFeatureDescriptor(ExtendedPropertiesAdapter<OutputSet> owner, OutputSet object,
				EStructuralFeature feature) {
			super(owner, object, feature);
		}

		@Override
		public Hashtable<String, Object> getChoiceOfValues() {
			Hashtable<String, Object> values = new Hashtable<String, Object>();
			List<DataOutput> valid = new ArrayList<DataOutput>();
			if (feature == Bpmn2Package.eINSTANCE.getOutputSet_DataOutputRefs()) {
				// choices are all DataOutputs in scope
				EObject container = object.eContainer();
				while (container!=null) {
					if (container instanceof CatchEvent) {
						valid.addAll( ((CatchEvent)container).getDataOutputs() );
					}
					else if (container instanceof SubProcess) {
						// a SubProcess must not define DataInputs and DataOutputs directly
						// only indirectly via MultiInstanceLoopCharacteristics.
						LoopCharacteristics lc = ((SubProcess)container).getLoopCharacteristics();
						if (lc instanceof MultiInstanceLoopCharacteristics) {
							DataOutput o = ((MultiInstanceLoopCharacteristics)lc).getOutputDataItem();
							if (o!=null)
								valid.add(o);
						}
					}
					else if (container instanceof Activity) {
						InputOutputSpecification ioSpec = ((Activity)container).getIoSpecification();
						if (ioSpec!=null)
							valid.addAll(ioSpec.getDataOutputs());
					}
					else if (container instanceof CallableElement) {
						InputOutputSpecification ioSpec = ((CallableElement)container).getIoSpecification();
						if (ioSpec!=null)
							valid.addAll(ioSpec.getDataOutputs());
					}
					container = container.eContainer();
				}
			}
			else {
				// choices are only the DataOutputs listed in "InputSet.dataOutputRefs"
				valid.addAll(object.getDataOutputRefs());
			}
			for (DataOutput data : valid) {
				values.put( ExtendedPropertiesProvider.getTextValue(data), data);
			}
			return values;
		}		
	}

	protected class InputSetFeatureDescriptor extends FeatureDescriptor<OutputSet> {

		
		public InputSetFeatureDescriptor(ExtendedPropertiesAdapter<OutputSet> owner, OutputSet object,
				EStructuralFeature feature) {
			super(owner, object, feature);
		}


		@Override
		public Hashtable<String, Object> getChoiceOfValues() {
			Hashtable<String, Object> values = new Hashtable<String, Object>();
			EObject container = object.eContainer();
			if (container instanceof InputOutputSpecification) {
				// an OutputSet.outputSetRefs can only reference OutputSets in the same InputOutputSpecification
				InputOutputSpecification ioSpec = (InputOutputSpecification)container;
				for (InputSet is : ioSpec.getInputSets()) {
					values.put( ExtendedPropertiesProvider.getTextValue(is), is);
				}
			}
			return values;
		}		
	}
}
