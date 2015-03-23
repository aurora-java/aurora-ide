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
 * @author Ivar Meikas
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.features;


import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.modeler.core.model.ModelHandler;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * This is the Graphiti CreateFeature class for all BPMN2 model elements that
 * subclass {@link FlowElement}.
 *
 * @param <T> the generic type, a subclass of {@code FlowElement}
 */
public abstract class AbstractCreateFlowElementFeature<T extends FlowElement> extends AbstractBpmn2CreateFeature<T> {
	
	/**
	 * Instantiates a new CreateFeature for BPMN2 {@link FlowElement} objects.
	 *
	 * @param fp the Feature Provider
	 * @param name the type name of the Flow Element
	 * @param description the description of the Flow Element
	 */
	public AbstractCreateFlowElementFeature(IFeatureProvider fp) {
		super(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.ICreate#canCreate(org.eclipse.graphiti.features.context.ICreateContext)
	 */
	@Override
	public boolean canCreate(ICreateContext context) {
		if (!super.canCreate(context))
			return false;
		if (FeatureSupport.isTargetParticipant(context)) {
			Participant participant = FeatureSupport.getTargetParticipant(context);
			if (FeatureSupport.hasBpmnDiagram(participant)) {
				return false;
			}
		}
		if (FeatureSupport.isTargetFlowElementsContainer(context)) {
			FlowElementsContainer flowElementsContainer = FeatureSupport.getTargetFlowElementsContainer(context);
			if (FeatureSupport.hasBpmnDiagram(flowElementsContainer))
				return false;
		}
		return FeatureSupport.isValidFlowElementTarget(context);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.ICreate#create(org.eclipse.graphiti.features.context.ICreateContext)
	 */
	@Override
	public Object[] create(ICreateContext context) {
		T element = createBusinessObject(context);
		if (element!=null) {
			changesDone = true;
			ModelHandler mh = ModelHandler.getInstance(getDiagram());
			if (FeatureSupport.isTargetLane(context) && element instanceof FlowNode) {
				((FlowNode) element).getLanes().add(
						(Lane) getBusinessObjectForPictogramElement(context.getTargetContainer()));
			}
			mh.addFlowElement(getBusinessObjectForPictogramElement(context.getTargetContainer()), element);
			PictogramElement pe = null;
			pe = addGraphicalRepresentation(context, element);
			return new Object[] { element, pe };
		}
		else
			changesDone = false;
		return new Object[] { null };
	}
	
	/**
	 * Gets the stencil image id. This image is used in the Tool Palette.
	 *
	 * @return the stencil image id
	 */
	protected abstract String getStencilImageId();
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractCreateFeature#getCreateImageId()
	 */
	@Override
	public String getCreateImageId() {
	    return getStencilImageId();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractCreateFeature#getCreateLargeImageId()
	 */
	@Override
	public String getCreateLargeImageId() {
	    return getCreateImageId(); // FIXME
	}
}