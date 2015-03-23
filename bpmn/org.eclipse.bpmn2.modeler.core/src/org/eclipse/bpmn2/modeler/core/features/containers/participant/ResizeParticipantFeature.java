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
package org.eclipse.bpmn2.modeler.core.features.containers.participant;

import java.util.List;

import org.eclipse.bpmn2.ChoreographyActivity;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.features.containers.AbstractResizeContainerFeature;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.impl.ResizeShapeContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class ResizeParticipantFeature extends AbstractResizeContainerFeature {

	public ResizeParticipantFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canResizeShape(IResizeShapeContext context) {
		EObject container = context.getShape().eContainer();
		if (container instanceof PictogramElement) {
			PictogramElement containerElem = (PictogramElement) container;
			if (BusinessObjectUtil.containsElementOfType(containerElem, ChoreographyActivity.class)) {
				return false;
			}
		}
		return super.canResizeShape(context);
	}
	
	protected void resizeHeight(IResizeShapeContext context) {
		ContainerShape poolShape = (ContainerShape) context.getShape();
		GraphicsAlgorithm ga = poolShape.getGraphicsAlgorithm();
		
		ContainerShape laneToResize = null;
		GraphicsAlgorithm laneToResizeGA  = null;
		int width = 0;
		int height = 0;
		boolean resizeFirstLane = false;
		boolean resize = false;
		if (isHorizontal) {
			int dHeight = context.getHeight() - ga.getHeight();
			if (dHeight != 0) {
				resize = true;
				if (context.getY() != ga.getY()) {
					laneToResize = (ContainerShape) FeatureSupport.getFirstLaneInContainer(poolShape);
					resizeFirstLane = true;
				} else {
					laneToResize = (ContainerShape) FeatureSupport.getLastLaneInContainer(poolShape);
				}
				laneToResizeGA = laneToResize.getGraphicsAlgorithm();
				width = laneToResizeGA.getWidth();
				height = laneToResizeGA.getHeight() + dHeight;
			}
		} else {
			int dWidth = context.getWidth() - ga.getWidth();
			if (dWidth != 0) {
				resize = true;
				if (context.getX() != ga.getX()) {
					laneToResize = (ContainerShape) FeatureSupport.getFirstLaneInContainer(poolShape);
					resizeFirstLane = true;
				} else {
					laneToResize = (ContainerShape) FeatureSupport.getLastLaneInContainer(poolShape);
				}
				laneToResizeGA = laneToResize.getGraphicsAlgorithm();
				width = laneToResizeGA.getWidth() + dWidth;
				height = laneToResizeGA.getHeight();
			}
		}
		if (resize) {
			ResizeShapeContext newContext = new ResizeShapeContext(laneToResize);
			
			newContext.setX(laneToResizeGA.getX());
			newContext.setY(laneToResizeGA.getY());
			newContext.setHeight(height);
			newContext.setWidth(width);
			newContext.setDirection(context.getDirection());
			newContext.putProperty(GraphitiConstants.POOL_RESIZE_PROPERTY, true);
			newContext.putProperty(GraphitiConstants.RESIZE_FIRST_LANE, resizeFirstLane);
			
			IResizeShapeFeature resizeFeature = getFeatureProvider().getResizeShapeFeature(newContext);
			if (resizeFeature.canResizeShape(newContext)) {
				resizeFeature.resizeShape(newContext);
			}
			if (isHorizontal) {
				((ResizeShapeContext) context).setHeight(ga.getHeight());
			} else {
				((ResizeShapeContext) context).setWidth(ga.getWidth());
			}
		}
	}
	
	protected void resizeWidth(IResizeShapeContext context) {
		ContainerShape poolShape = (ContainerShape) context.getShape();
		GraphicsAlgorithm ga = poolShape.getGraphicsAlgorithm();
		
		int dHeight = context.getHeight() - ga.getHeight();
		int dWidth = context.getWidth() - ga.getWidth();
		
		if ((dWidth != 0 && isHorizontal) || (dHeight != 0 && !isHorizontal)) {
			List<PictogramElement> childrenShapes = BusinessObjectUtil.getChildElementsOfType(poolShape, Lane.class);
			for (PictogramElement currentPicElem : childrenShapes) {
				if (currentPicElem instanceof ContainerShape) {
					ContainerShape currentContainerShape = (ContainerShape) currentPicElem; 
					GraphicsAlgorithm laneGA = currentContainerShape.getGraphicsAlgorithm();
					
					ResizeShapeContext newContext = new ResizeShapeContext(currentContainerShape);
					
					newContext.setLocation(laneGA.getX(), laneGA.getY());
					if (isHorizontal) {
						newContext.setWidth(laneGA.getWidth() + dWidth);
						newContext.setHeight(laneGA.getHeight());
					} else {
						newContext.setHeight(laneGA.getHeight() + dHeight);
						newContext.setWidth(laneGA.getWidth());
					}
					newContext.setDirection(context.getDirection());

					newContext.putProperty(GraphitiConstants.POOL_RESIZE_PROPERTY, true);
					
					IResizeShapeFeature resizeFeature = getFeatureProvider().getResizeShapeFeature(newContext);
					if (resizeFeature.canResizeShape(newContext)) {
						resizeFeature.resizeShape(newContext);
					}
				}
			}
		}
	}
	
	@Override
	public void resizeShape(IResizeShapeContext context) {
		
		preResizeShape(context);
		
		if (BusinessObjectUtil.containsChildElementOfType(context.getPictogramElement(), Lane.class)) {
			resizeHeight(context);
			resizeWidth(context);
		}
		
		internalResizeShape(context);
		postResizeShape(context);
	}
}
