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
package org.eclipse.bpmn2.modeler.ui.features.lane;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2CreateFeature;
import org.eclipse.bpmn2.modeler.core.model.ModelHandler;
import org.eclipse.bpmn2.modeler.core.model.ModelHandlerLocator;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.ui.ImageProvider;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class CreateLaneFeature extends AbstractBpmn2CreateFeature<Lane> {

	public CreateLaneFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		if (!super.canCreate(context))
			return false;

		// NOTE: This is slightly different from FeatureSupport.isValidFlowElementTarget()
		// because a Lane can be added to a Lane that is not a top-level Lane. This is not
		// the case for Activities, Events and Gateways.
		boolean intoDiagram = context.getTargetContainer() instanceof Diagram;
		boolean intoLane = FeatureSupport.isTargetLane(context);
		boolean intoParticipant = FeatureSupport.isTargetParticipant(context);
		boolean intoFlowElementContainer = FeatureSupport.isTargetFlowElementsContainer(context);
		boolean intoGroup = FeatureSupport.isTargetGroup(context);
		return (intoDiagram || intoLane || intoParticipant || intoFlowElementContainer) && !intoGroup;
	}

	@Override
	public Object[] create(ICreateContext context) {
		Lane lane = createBusinessObject(context);
		lane.setName(Messages.CreateLaneFeature_Default_Name+ModelUtil.getIDNumber(lane.getId()));
		PictogramElement pe = addGraphicalRepresentation(context, lane);
		return new Object[] { lane, pe };
	}

	@Override
	public String getCreateImageId() {
		return ImageProvider.IMG_16_LANE;
	}

	@Override
	public String getCreateLargeImageId() {
		return getCreateImageId(); // FIXME
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2CreateFeature#getBusinessObjectClass()
	 */
	@Override
	public EClass getBusinessObjectClass() {
		return Bpmn2Package.eINSTANCE.getLane();
	}

	@Override
	public Lane createBusinessObject(ICreateContext context) {
		ModelHandler mh = ModelHandlerLocator.getModelHandler(getDiagram().eResource());

		Object bo = getBusinessObjectForPictogramElement(context.getTargetContainer());
		Lane lane = null;
		if (FeatureSupport.isTargetLane(context)) {
			Lane targetLane = (Lane) bo;
			lane = ModelHandler.createLane(targetLane);
		} else {
			lane = mh.createLane(bo);
		}
		putBusinessObject(context, lane);
		
		return lane;
	}
}