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
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.modeler.core.adapters.AdapterUtil;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.FeatureDescriptor;
import org.eclipse.bpmn2.modeler.core.adapters.ObjectDescriptor;
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
public class ProcessPropertiesAdapter extends RootElementPropertiesAdapter<Process> {

	/**
	 * @param adapterFactory
	 * @param object
	 */
	public ProcessPropertiesAdapter(AdapterFactory adapterFactory, Process object) {
		super(adapterFactory, object);
    	EStructuralFeature feature;
    	
    	feature = Bpmn2Package.eINSTANCE.getCallableElement_Name();
    	setFeatureDescriptor(feature,
			new FeatureDescriptor<Process>(this,object,feature) {
    		
	    		@Override
				protected void internalSet(Process process, EStructuralFeature feature, Object value, int index) {
	    			// changing the process name also changes its BPMNDiagram name
	    			// which is used as the tab label in the multipage editor
	    			// NO! This is a bad idea.
//	    			BPMNDiagram bpmnDiagram = null;
//	    			Definitions defs = ModelUtil.getDefinitions(process);
//	    			if (defs!=null) {
//	    				for (BPMNDiagram d : defs.getDiagrams()) {
//	    					if (d.getPlane().getBpmnElement() == process) {
//	    						bpmnDiagram = d;
//	    						break;
//	    					}
//	    				}
//	    			}

	    			process.setName((String)value);
//    				if (bpmnDiagram!=null)
//    					bpmnDiagram.setName((String)value);
	    		}
    		}
    	);
    	
		feature = Bpmn2Package.eINSTANCE.getProcess_Properties();
		setFeatureDescriptor(feature,
			new FeatureDescriptor<Process>(this,object,feature) {
				@Override
				public EObject createFeature(Resource resource, EClass eclass) {
					return PropertyPropertiesAdapter.createProperty(object.getProperties());
				}
			}
		);
	}
	
}
