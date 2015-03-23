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
import java.util.Map.Entry;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Operation;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.modeler.core.adapters.FeatureDescriptor;
import org.eclipse.bpmn2.modeler.core.preferences.Bpmn2Preferences;
import org.eclipse.bpmn2.modeler.core.runtime.ServiceImplementationDescriptor;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Extended Properties Adapter for Service Tasks.
 * <p>
 * This adapter initializes Service Task Operation features and provides Service
 * Implementation selection choices for the UI.
 */
public class ServiceTaskPropertiesAdapter extends TaskPropertiesAdapter<ServiceTask> {

	/**
	 * @param adapterFactory
	 * @param object
	 */
	public ServiceTaskPropertiesAdapter(AdapterFactory adapterFactory, ServiceTask object) {
		super(adapterFactory, object);
    	EStructuralFeature feature = Bpmn2Package.eINSTANCE.getServiceTask_OperationRef();

    	setProperty(feature, UI_CAN_CREATE_NEW, Boolean.TRUE);
    	setProperty(feature, UI_CAN_EDIT, Boolean.TRUE);
		setProperty(feature, UI_IS_MULTI_CHOICE, Boolean.TRUE);

		setFeatureDescriptor(feature, new OperationRefFeatureDescriptor<ServiceTask>(this,object,feature) {
    		
    		@Override
    		protected void internalSet(ServiceTask serviceTask, EStructuralFeature feature, Object value, int index) {
    			if (value instanceof Operation || value==null) {
					setOperationRef(serviceTask, (Operation)value);
    			}
    		}
   		
    	});

    	feature = Bpmn2Package.eINSTANCE.getServiceTask_Implementation();
    	setProperty(feature, UI_IS_MULTI_CHOICE, Boolean.TRUE);
    	
    	setFeatureDescriptor(feature,
			new FeatureDescriptor<ServiceTask>(this,object,feature) {

				@Override
				public Hashtable<String, Object> getChoiceOfValues() {
					return ServiceTaskPropertiesAdapter.getChoiceOfValues(object);
				}
			}
    	);
	}

	public static Hashtable<String, Object> getChoiceOfValues(EObject object) {
		Hashtable<String,Object> choices = new Hashtable<String,Object>();
		TargetRuntime rt = TargetRuntime.getCurrentRuntime();
		for (ServiceImplementationDescriptor eld : rt.getServiceImplementationDescriptors()) {
			choices.put(eld.getName(), ModelUtil.createStringWrapper(eld.getUri()));
		}
		
		Bpmn2Preferences prefs = Bpmn2Preferences.getInstance(object);
		for (Entry<String, String> entry : prefs.getServiceImplementations().entrySet()) {
			if (!choices.containsKey(entry.getKey())) {
				choices.put(entry.getKey(), ModelUtil.createStringWrapper(entry.getValue()));
			}
		}
		return choices;
	}

	private void setOperationRef(ServiceTask serviceTask, Operation operation) {
		if (serviceTask.getOperationRef()!=operation) {
			serviceTask.setOperationRef(operation);
		}
	}
}
