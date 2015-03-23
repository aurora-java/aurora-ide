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
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.MessageFlow;
import org.eclipse.bpmn2.Operation;
import org.eclipse.bpmn2.ReceiveTask;
import org.eclipse.bpmn2.SendTask;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;

/**
 * @author Bob Brodt
 *
 */
public class SendTaskPropertiesAdapter extends TaskPropertiesAdapter<SendTask> {

	/**
	 * @param adapterFactory
	 * @param object
	 */
	public SendTaskPropertiesAdapter(AdapterFactory adapterFactory, SendTask object) {
		super(adapterFactory, object);

    	EStructuralFeature feature = Bpmn2Package.eINSTANCE.getSendTask_MessageRef();
    	setProperty(feature, UI_CAN_CREATE_NEW, Boolean.TRUE);
    	setProperty(feature, UI_CAN_EDIT, Boolean.TRUE);
		setProperty(feature, UI_IS_MULTI_CHOICE, Boolean.TRUE);

    	setFeatureDescriptor(feature, new MessageRefFeatureDescriptor<SendTask>(this,object,feature) {

    		@Override
    		protected void internalSet(SendTask sendTask, EStructuralFeature feature, Object value, int index) {
    			if (value instanceof Message || value==null) {
					setMessageRef(sendTask, (Message)value);
    			}
    		}
    		
    	});

    	feature = Bpmn2Package.eINSTANCE.getSendTask_OperationRef();
    	setProperty(feature, UI_CAN_CREATE_NEW, Boolean.TRUE);
    	setProperty(feature, UI_CAN_EDIT, Boolean.TRUE);
		setProperty(feature, UI_IS_MULTI_CHOICE, Boolean.TRUE);

		setFeatureDescriptor(feature, new OperationRefFeatureDescriptor<SendTask>(this,object,feature) {
    		
    		@Override
    		protected void internalSet(SendTask sendTask, EStructuralFeature feature, Object value, int index) {
    			if (value instanceof Operation || value==null) {
					setOperationRef(sendTask, (Operation)value);
    			}
    		}
   		
    	});
	}
	
	private void setMessageRef(SendTask sendTask, Message message) {
		ResourceSet resourceSet = sendTask.eResource().getResourceSet();
		
		// first change the MessageRef on the SendTask
		if (sendTask.getMessageRef()!=message) {
			sendTask.setMessageRef(message);
			
			// If there are any OUTGOING Message Flows attached to this SendTask figure,
			// make sure the MessageFlow.messageRef is the same as ours
			List<ContainerShape> shapes = DIUtils.getContainerShapes(resourceSet, sendTask);
			for (ContainerShape shape : shapes) {
				for (Anchor a : shape.getAnchors()) {
					for (Connection c : a.getOutgoingConnections()) {
						Object o = BusinessObjectUtil.getFirstBaseElement(c);
						if (o instanceof MessageFlow && ((MessageFlow)o).getMessageRef()!=message) {
							((MessageFlow)o).setMessageRef(message);
						}
						// also set the "messageRef" on the target of this Message Flow
						// (the target should be a ReceiveTask)
						o = BusinessObjectUtil.getFirstBaseElement(c.getEnd().getParent());
						if (o instanceof ReceiveTask && ((ReceiveTask)o).getMessageRef()!=message) {
							ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(o);
							adapter.getFeatureDescriptor(Bpmn2Package.eINSTANCE.getReceiveTask_MessageRef()).setValue(message);
						}
					}
				}
			}
		}
	}

	private void setOperationRef(SendTask sendTask, Operation operation) {
		if (sendTask.getOperationRef()!=operation) {
			sendTask.setOperationRef(operation);
			Message message = operation==null ? null : operation.getOutMessageRef();
			setMessageRef(sendTask, message);
		}
	}
}
