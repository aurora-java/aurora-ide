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
 * @author Innar Made
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.features.conversation;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Collaboration;
import org.eclipse.bpmn2.ConversationNode;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2CreateFeature;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.bpmn2.modeler.core.model.ModelHandler;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.ui.ImageProvider;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public abstract class AbstractCreateConversationNodeFeature<T extends ConversationNode> extends AbstractBpmn2CreateFeature<ConversationNode> {

	public AbstractCreateConversationNodeFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		if (context.getTargetContainer().equals(getDiagram())) {
			// Only Participants are allowed in a Conversation
			BPMNDiagram bpmnDiagram = BusinessObjectUtil.getFirstElementOfType(getDiagram(), BPMNDiagram.class);
			BaseElement bpmnElement = bpmnDiagram.getPlane().getBpmnElement();
			if (bpmnElement instanceof Collaboration) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object[] create(ICreateContext context) {
		T businessObject = createBusinessObject(context);
		PictogramElement pe = addGraphicalRepresentation(context, businessObject);
		return new Object[] { businessObject, pe };
	}

	@Override
	public String getCreateImageId() {
		return ImageProvider.IMG_16_CONVERSATION;
	}

	@Override
	public String getCreateLargeImageId() {
		return ImageProvider.IMG_16_CONVERSATION;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2CreateFeature#getBusinessObjectClass()
	 */
	@Override
	public abstract EClass getBusinessObjectClass();

	@Override
	public T createBusinessObject(ICreateContext context) {
		ModelHandler mh = ModelHandler.getInstance(getDiagram());
		
		T businessObject = (T) Bpmn2ModelerFactory.eINSTANCE.create(getBusinessObjectClass());
        BPMNDiagram bpmnDiagram = BusinessObjectUtil.getFirstElementOfType(context.getTargetContainer(), BPMNDiagram.class);
        mh.addConversationNode(bpmnDiagram,businessObject);
		ModelUtil.setID(businessObject);
		businessObject.setName(ModelUtil.toCanonicalString(businessObject.getId()));
		putBusinessObject(context, businessObject);
		
		return businessObject;
	}
}