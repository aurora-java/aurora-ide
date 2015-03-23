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
package org.eclipse.bpmn2.modeler.ui.features.gateway;

import org.eclipse.bpmn2.EventBasedGateway;
import org.eclipse.bpmn2.EventBasedGatewayType;
import org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2UpdateFeature;
import org.eclipse.bpmn2.modeler.core.features.AbstractUpdateBaseElementFeature;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.core.utils.ShapeDecoratorUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IPeService;

public class UpdateEventBasedGatewayFeature extends AbstractUpdateBaseElementFeature<EventBasedGateway> {

	public UpdateEventBasedGatewayFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {
		IReason reason = super.updateNeeded(context);
		if (reason.toBoolean())
			return reason;

		IPeService service = Graphiti.getPeService();

		boolean instantiate = Boolean.parseBoolean(service.getPropertyValue(context.getPictogramElement(),
		        EventBasedGatewayFeatureContainer.INSTANTIATE_PROPERTY));
		EventBasedGatewayType gatewayType = EventBasedGatewayType.getByName(service.getPropertyValue(
		        context.getPictogramElement(), EventBasedGatewayFeatureContainer.EVENT_GATEWAY_TYPE_PROPERTY));

		EventBasedGateway gateway = (EventBasedGateway) getBusinessObjectForPictogramElement(context
		        .getPictogramElement());

		boolean changed = instantiate != gateway.isInstantiate() || gatewayType != gateway.getEventGatewayType();
		return changed ? Reason.createTrueReason("Is Instantiate") : Reason.createFalseReason();
	}

	@Override
	public boolean update(IUpdateContext context) {
		IPeService service = Graphiti.getPeService();

		EventBasedGateway gateway = (EventBasedGateway) getBusinessObjectForPictogramElement(context
		        .getPictogramElement());

		clearGateway(context.getPictogramElement());

		if (gateway.isInstantiate()) {
			if (gateway.getEventGatewayType() == EventBasedGatewayType.PARALLEL) {
				drawParallelMultipleEventBased((ContainerShape) context.getPictogramElement());
			} else {
				drawExclusiveEventBased((ContainerShape) context.getPictogramElement());
			}
		} else {
			drawEventBased((ContainerShape) context.getPictogramElement());
		}

		service.setPropertyValue(context.getPictogramElement(), EventBasedGatewayFeatureContainer.INSTANTIATE_PROPERTY,
		        Boolean.toString(gateway.isInstantiate()));
		service.setPropertyValue(context.getPictogramElement(),
		        EventBasedGatewayFeatureContainer.EVENT_GATEWAY_TYPE_PROPERTY, gateway.getEventGatewayType().getName());
		return true;
	}

	private void clearGateway(PictogramElement element) {
		ShapeDecoratorUtil.clearGateway(element);
	}

	private void drawEventBased(ContainerShape container) {
		if (FeatureSupport.isLabelShape(container)) {
			// don't draw decorators on Labels
			return;
		}
		Ellipse outer = ShapeDecoratorUtil.createGatewayOuterCircle(container);
		Ellipse inner = ShapeDecoratorUtil.createGatewayInnerCircle(outer);
		Polygon pentagon = ShapeDecoratorUtil.createGatewayPentagon(container);
		pentagon.setFilled(false);
	}

	private void drawExclusiveEventBased(ContainerShape container) {
		if (FeatureSupport.isLabelShape(container)) {
			// don't draw decorators on Labels
			return;
		}
		Ellipse ellipse = ShapeDecoratorUtil.createGatewayOuterCircle(container);
		Polygon pentagon = ShapeDecoratorUtil.createGatewayPentagon(container);
		pentagon.setFilled(false);
	}

	private void drawParallelMultipleEventBased(ContainerShape container) {
		if (FeatureSupport.isLabelShape(container)) {
			// don't draw decorators on Labels
			return;
		}
		Ellipse ellipse = ShapeDecoratorUtil.createGatewayOuterCircle(container);
		Polygon cross = ShapeDecoratorUtil.createEventGatewayParallelCross(container);
	}
}