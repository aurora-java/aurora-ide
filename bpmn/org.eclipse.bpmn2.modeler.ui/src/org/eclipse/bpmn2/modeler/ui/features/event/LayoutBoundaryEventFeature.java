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
package org.eclipse.bpmn2.modeler.ui.features.event;

import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.utils.BoundaryEventPositionHelper;
import org.eclipse.bpmn2.modeler.core.utils.BoundaryEventPositionHelper.PositionOnLine;
import org.eclipse.bpmn2.modeler.core.utils.BoundaryEventPositionHelper.PositionOnLine.LocationType;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.impl.AbstractLayoutFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;

public class LayoutBoundaryEventFeature extends AbstractLayoutFeature {

	public LayoutBoundaryEventFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canLayout(ILayoutContext context) {
		return true;
	}

	@Override
	public boolean layout(ILayoutContext context) {
		boolean layout = false;

		final PictogramElement element = context.getPictogramElement();
		GraphicsAlgorithm eventGa = element.getGraphicsAlgorithm();
		BoundaryEvent event = BusinessObjectUtil.getFirstElementOfType(element, BoundaryEvent.class);

		PictogramElement activityContainer = BusinessObjectUtil.getFirstBaseElementFromDiagram(getDiagram(),
		        event.getAttachedToRef());
		GraphicsAlgorithm activityGa = activityContainer.getGraphicsAlgorithm();

		PositionOnLine pos = BoundaryEventPositionHelper.getPositionOnLineProperty(element);

		switch (pos.getLineType()) {
		case X:
			moveX(eventGa, activityGa, pos.getLocationType());
			if ((eventGa.getY() < activityGa.getY()) ||
					(eventGa.getY() + eventGa.getHeight() > activityGa.getY() + activityGa.getHeight())) {
				moveY(eventGa, activityGa, pos.getLocationType());
			}
			layout = true;
			break;
		case Y:
			moveY(eventGa, activityGa, pos.getLocationType());
			if ((eventGa.getX() < activityGa.getX()) || 
					(eventGa.getX() + eventGa.getWidth() > activityGa.getX() + activityGa.getWidth())) {
				moveX(eventGa, activityGa, pos.getLocationType());
			}
			layout = true;
			break;
		case XY:
			moveX(eventGa, activityGa, pos.getLocationType());
			moveY(eventGa, activityGa, pos.getLocationType());
			layout = true;
			break;
		default:
			layout = false;
			break;
		}

		DIUtils.updateDIShape(element);
		if (layout) {
			PositionOnLine newPos = BoundaryEventPositionHelper.getPositionOnLineUsingAbsoluteCoordinates(
			        (Shape) element, (Shape) activityContainer);
			BoundaryEventPositionHelper.assignPositionOnLineProperty(element, newPos);
		}

		// There's some weird stuff going on here that I can't explain...See bug 433417
		// When a Boundary Event is DND'ed onto an Activity that is contained in the Diagram,
		// the Boundary Event is always drawn BELOW the Activity.
		// I've tried forcing a refresh by changing the Z-order of the Boundary Event
		// (which should be the top-most Figure anyway) but that didn't work. I've tried
		// changing the Z-order of the Activity so it's at the bottom and that worked but,
		// an undo-redo of the add action draws the Boundary Event BELOW the Activity again!
		// The only thing that appears to work (without causing additional problems) is
		// to use an EList.move() on the Diagram's children which, apparently, does not get
		// recorded by the ChangeRecorder adapter and undo-redo works as expected.
		if (activityContainer.eContainer()==getDiagram()) {
			getDiagram().getChildren().move(0, (Shape)activityContainer);
		}

		return layout;
	}

	private void moveX(GraphicsAlgorithm ga, GraphicsAlgorithm parentGa, LocationType locType) {
		IGaService gaService = Graphiti.getGaService();
		if (isLeft(locType)) {
			gaService.setLocation(ga, parentGa.getX() - (ga.getWidth() / 2), ga.getY());
		} else if (isRight(locType)) {
			gaService.setLocation(ga, parentGa.getX() + parentGa.getWidth() - (ga.getWidth() / 2), ga.getY());
		}
		else {
			gaService.setLocation(ga, parentGa.getX() + parentGa.getWidth() / 2 - (ga.getWidth() / 2), ga.getY());
		}
	}

	private boolean isLeft(LocationType locType) {
		return locType == LocationType.TOP_LEFT || locType == LocationType.LEFT || locType == LocationType.BOTTOM_LEFT;
	}

	private boolean isRight(LocationType locType) {
		return locType == LocationType.TOP_RIGHT || locType == LocationType.RIGHT || locType == LocationType.BOTTOM_RIGHT;
	}

	private void moveY(GraphicsAlgorithm ga, GraphicsAlgorithm parentGa, LocationType locType) {
		IGaService gaService = Graphiti.getGaService();
		if (isTop(locType)) {
			gaService.setLocation(ga, ga.getX(), parentGa.getY() - (ga.getHeight() / 2));
		} else if (isBottom(locType)) {
			gaService.setLocation(ga, ga.getX(), parentGa.getY() + parentGa.getHeight() - (ga.getHeight() / 2));
		}
		else {
			gaService.setLocation(ga, ga.getX(), parentGa.getY() + parentGa.getHeight() / 2 - (ga.getHeight() / 2));
		}
	}

	private boolean isTop(LocationType locType) {
		return locType == LocationType.TOP_LEFT || locType == LocationType.TOP || locType == LocationType.TOP_RIGHT;
	}

	private boolean isBottom(LocationType locType) {
		return locType == LocationType.BOTTOM_LEFT || locType == LocationType.BOTTOM || locType == LocationType.BOTTOM_RIGHT;
	}
}