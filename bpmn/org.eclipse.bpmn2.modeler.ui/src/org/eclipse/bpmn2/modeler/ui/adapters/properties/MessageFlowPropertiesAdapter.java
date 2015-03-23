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

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.MessageFlow;
import org.eclipse.bpmn2.ReceiveTask;
import org.eclipse.bpmn2.SendTask;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.adapters.ObjectDescriptor;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.features.choreography.ChoreographyUtil;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.graphiti.mm.pictograms.Connection;

/**
 * @author Gary Brown
 *
 */
public class MessageFlowPropertiesAdapter extends ExtendedPropertiesAdapter<MessageFlow> {

	/**
	 * @param adapterFactory
	 * @param object
	 */
	public MessageFlowPropertiesAdapter(AdapterFactory adapterFactory, MessageFlow object) {
		super(adapterFactory, object);

		EStructuralFeature ref = Bpmn2Package.eINSTANCE.getMessageFlow_MessageRef();
    	setFeatureDescriptor(ref, new RootElementRefFeatureDescriptor<MessageFlow>(this,object,ref) {
    		
    		protected void internalSet(MessageFlow messageFlow, EStructuralFeature feature, Object value, int index) {
    			if (value instanceof Message || value==null) {
	    			final Message message = (Message)value; 
    				ResourceSet resourceSet = messageFlow.eResource().getResourceSet();
    				
    				// first change the MessageRef on the SendTask
    				messageFlow.setMessageRef(message);
    				
    				// If the source and/or target of this Message Flow are a SendTask
    				// or ReceiveTask make sure the messageRef is the same as ours
    				List<Connection> connections = DIUtils.getConnections(resourceSet, messageFlow);
    				for (Connection connection : connections) {
    					BaseElement source = BusinessObjectUtil.getFirstBaseElement(connection.getStart().getParent());
    					BaseElement target = BusinessObjectUtil.getFirstBaseElement(connection.getEnd().getParent());
    					if (source instanceof SendTask) {
    						ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(source);
							adapter.getFeatureDescriptor(Bpmn2Package.eINSTANCE.getSendTask_MessageRef()).setValue(message);
    					}
    					if (target instanceof ReceiveTask) {
    						ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(target);
							adapter.getFeatureDescriptor(Bpmn2Package.eINSTANCE.getReceiveTask_MessageRef()).setValue(message);
    					}
    				}
    			}
    		}
    		
    	});

    	setObjectDescriptor(new ObjectDescriptor<MessageFlow>(this,object) {
			@Override
			public String getTextValue() {
				String text = ""; //$NON-NLS-1$
				if (object.getName()!=null)
					text = object.getName();
				else {
					if (object.getMessageRef()!=null) {
						text += ChoreographyUtil.getMessageFlowName(object);
					}
					
					if (object.getSourceRef() != null) {
						text += "(" + ExtendedPropertiesProvider.getTextValue(object.getSourceRef())+"->"; //$NON-NLS-1$ //$NON-NLS-2$
						
						if (object.getTargetRef() != null) {
							text += ExtendedPropertiesProvider.getTextValue(object.getTargetRef());
						}
						text += ")"; //$NON-NLS-1$
					}
				}
				return text;
			}
    	});
	}
}
