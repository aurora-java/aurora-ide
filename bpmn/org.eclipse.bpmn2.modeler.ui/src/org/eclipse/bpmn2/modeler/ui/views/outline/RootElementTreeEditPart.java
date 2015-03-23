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
package org.eclipse.bpmn2.modeler.ui.views.outline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.Collaboration;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.ui.util.PropertyUtil;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Image;

public class RootElementTreeEditPart extends AbstractGraphicsTreeEditPart {
	
	public RootElementTreeEditPart(DiagramTreeEditPart dep, RootElement graphicsAlgorithm) {
		super(dep, graphicsAlgorithm);
	}

	public RootElement getRootElement() {
		return (RootElement) getModel();
	}

	// ======================= overwriteable behaviour ========================

	/**
	 * Creates the EditPolicies of this EditPart. Subclasses often overwrite
	 * this method to change the behaviour of the editpart.
	 */
	@Override
	protected void createEditPolicies() {
	}
	
	@Override
	protected Image getImage() {
		EObject o = (EObject)getModel();
		return PropertyUtil.getImage(o);
	}

	@Override
	protected List<Object> getModelChildren() {
		List<Object> retList = new ArrayList<Object>();
		RootElement elem = getRootElement();
		if (elem != null && elem.eResource() != null) {
//			if (getParent() instanceof DiagramTreeEditPart)
			{
				if (elem instanceof FlowElementsContainer) {
					FlowElementsContainer container = (FlowElementsContainer)elem;
					return FlowElementTreeEditPart.getFlowElementsContainerChildren(container);
				}
				if (elem instanceof Collaboration) {
					Collaboration collaboration = (Collaboration)elem;
					retList.addAll(collaboration.getParticipants());
					retList.addAll(collaboration.getConversations());
					retList.addAll(collaboration.getConversationLinks());
					retList.addAll(collaboration.getMessageFlows());
					retList.addAll(collaboration.getArtifacts());
				}
			}
		}
		return retList;
	}
}