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
package org.eclipse.bpmn2.modeler.core.features.containers.lane;

import java.util.List;

import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.features.containers.AbstractResizeContainerFeature;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.core.utils.GraphicsUtil;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.impl.ResizeShapeContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

public class ResizeLaneFeature extends AbstractResizeContainerFeature {
	
	public ResizeLaneFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canResizeShape(IResizeShapeContext context) {
		boolean doit = false;
		boolean isLane = FeatureSupport.isLane(context.getPictogramElement());
		if (isLane) {
			boolean isParentLane = FeatureSupport.isLane(((ContainerShape) context
					.getPictogramElement()).getContainer());
			if (!isParentLane) {
				doit = true;
			}
			else {
				if (context.getHeight() == -1 && context.getWidth() == -1) {
					doit = true;
				}
				else {
					GraphicsAlgorithm ga = ((ContainerShape) context.getPictogramElement())
							.getGraphicsAlgorithm();
			
					int i = compare(ga.getHeight(), ga.getWidth(), context.getHeight(),
							context.getWidth());
			
					Lane lane = (Lane) BusinessObjectUtil.getFirstElementOfType(
							context.getPictogramElement(), Lane.class);
			
					if (i < 0) {// && lane.getFlowNodeRefs().size() == 0) {
						doit = true;
					}
					else if (i > 0) {
						doit = true;
					}
				}
			}
			
			if (doit && !super.canResizeShape(context))
				doit = false;
		}
		return doit;
	}
	
	@Override
	public void resizeShape(IResizeShapeContext context) {
		preResizeShape(context);

		resizeHeight(context);
		resizeWidth(context);
		
		postResizeShape(context);
	}

	protected void resizeHeight(IResizeShapeContext context) {
		ContainerShape laneShape = (ContainerShape) context.getShape();
		GraphicsAlgorithm ga = laneShape.getGraphicsAlgorithm();
		
		if ((isHorizontal && ga.getHeight() != context.getHeight()) 
				|| (!isHorizontal && ga.getWidth() != context.getWidth())) {
			
			boolean useFirstLane = false;
			Object fetchFirstProperty = context.getProperty(GraphitiConstants.RESIZE_FIRST_LANE);
			if (fetchFirstProperty != null && ((Boolean) fetchFirstProperty).booleanValue()) {
				useFirstLane = true;
			} else {
				if ((isHorizontal && context.getY() != ga.getY()) ||
						(!isHorizontal && context.getX() != ga.getX())) {
					useFirstLane = true;
					if (laneShape.equals(rootContainer)) {
						Graphiti.getGaService().setLocation(ga, context.getX(), context.getY());
					}
				}
			}
			
			ContainerShape lowestContainingLane = getLowestLane(laneShape, useFirstLane);
			GraphicsAlgorithm lowestLaneGA = lowestContainingLane.getGraphicsAlgorithm();
			
			int width = 0;
			int height = 0;
			
			if (isHorizontal) {
				int dHeight = context.getHeight() - ga.getHeight();
				height = lowestLaneGA.getHeight() + dHeight;
				if (height < 100) {
					height = 100;
				}
				width = lowestLaneGA.getWidth();
			} else {
				int dWidth = context.getWidth() - ga.getWidth();
				width = lowestLaneGA.getWidth() + dWidth;
				if (width < 100) {
					width = 100;
				}
				height = lowestLaneGA.getHeight();
			}
			
			ResizeShapeContext newContext = new ResizeShapeContext(lowestContainingLane);
			
			newContext.setX(lowestLaneGA.getX());
			newContext.setY(lowestLaneGA.getY());
			newContext.setHeight(height);
			newContext.setWidth(width);
			newContext.setDirection(context.getDirection());

			super.resizeShape(newContext);
		}
	}

	protected void resizeWidth(IResizeShapeContext context) {
		ContainerShape laneShape = (ContainerShape) context.getShape();
		GraphicsAlgorithm ga = laneShape.getGraphicsAlgorithm();
		
		if ((isHorizontal && ga.getWidth() != context.getWidth()) 
				|| (!isHorizontal && ga.getHeight() != context.getHeight())) {
			
			
			int dWidth = 0;
			int dHeight = 0;
			if (isHorizontal) {
				dWidth = context.getWidth() - ga.getWidth();
			} else {
				dHeight = context.getHeight() - ga.getHeight();
			}
			
			Object poolResizeProperty = context.getProperty(GraphitiConstants.POOL_RESIZE_PROPERTY);
			if (poolResizeProperty != null && ((Boolean) poolResizeProperty).booleanValue()) {
				if (isHorizontal) {
					Graphiti.getGaService().setWidth(ga, context.getWidth());
				} else {
					Graphiti.getGaService().setHeight(ga, context.getHeight());
				}
				for (PictogramElement currentChild : BusinessObjectUtil.getChildElementsOfType(laneShape, Lane.class)) {
					if (currentChild instanceof ContainerShape) {
						ContainerShape currentContainer = (ContainerShape) currentChild;
						GraphicsAlgorithm currentGA = currentChild.getGraphicsAlgorithm();
						
						ResizeShapeContext newContext = new ResizeShapeContext(currentContainer);
						
						newContext.setX(currentGA.getX());
						newContext.setY(currentGA.getY());
						newContext.setHeight(currentGA.getHeight() + dHeight);
						newContext.setWidth(currentGA.getWidth() + dWidth);
						newContext.setDirection(context.getDirection());
						
						newContext.putProperty(GraphitiConstants.POOL_RESIZE_PROPERTY, true);
						
						resizeShape(newContext);
					}
				}
			} else {
				GraphicsAlgorithm rootGA = rootContainer.getGraphicsAlgorithm();
				
				if (FeatureSupport.isParticipant(rootContainer)) {
					ResizeShapeContext newContext = new ResizeShapeContext(rootContainer);

					newContext.setX(rootGA.getX());
					newContext.setY(rootGA.getY());
					newContext.setWidth(rootGA.getWidth() + dWidth);
					newContext.setHeight(rootGA.getHeight() + dHeight);
					newContext.setDirection(context.getDirection());

					IResizeShapeFeature resizeFeature = getFeatureProvider().getResizeShapeFeature(newContext);
					if (resizeFeature.canResizeShape(newContext)) {
						resizeFeature.resizeShape(newContext);
					}
				} else {
					ContainerShape container = null;
					Object rootIsLaneProperty = context.getProperty(GraphitiConstants.LANE_RESIZE_PROPERTY);
					if (rootIsLaneProperty != null && ((Boolean) rootIsLaneProperty).booleanValue()) {
						Graphiti.getGaService().setWidth(ga, context.getWidth());
						Graphiti.getGaService().setHeight(ga, context.getHeight());
						container = laneShape;
					} else {
						container = rootContainer;
						if (isHorizontal) {
							Graphiti.getGaService().setWidth(rootGA, rootGA.getWidth() + dWidth);
						} else {
							Graphiti.getGaService().setHeight(rootGA, rootGA.getHeight() + dHeight);
						}
						if (laneShape.equals(rootContainer)) {
							Graphiti.getGaService().setLocation(ga, context.getX(), context.getY());
						}
					}
					for (PictogramElement currentChild : BusinessObjectUtil.getChildElementsOfType(container, Lane.class)) {
						if (currentChild instanceof ContainerShape) {
							ContainerShape currentContainer = (ContainerShape) currentChild;
							GraphicsAlgorithm currentGA = currentChild.getGraphicsAlgorithm();

							ResizeShapeContext newContext = new ResizeShapeContext(currentContainer);

							newContext.setX(currentGA.getX());
							newContext.setY(currentGA.getY());
							newContext.setWidth(currentGA.getWidth() + dWidth);
							newContext.setHeight(currentGA.getHeight() + dHeight);
							newContext.setDirection(context.getDirection());
							newContext.putProperty(GraphitiConstants.LANE_RESIZE_PROPERTY, true);

							resizeShape(newContext);
						}
					}
				}
			}
		}
	}
	
	@Override
	protected void preResizeShape(IResizeShapeContext context) {
		super.preResizeShape(context);
		// TODO: figure out an algorithm to resize lanes so that children
		// are always visible
//		List<PictogramElement> children = FeatureSupport.getPoolOrLaneChildren((ContainerShape)context.getShape());
//		Rectangle bounds = GraphicsUtil.getBoundingRectangle(children);
//		int direction = 0;
//		if (bounds.x < context.getX()) {
//			((ResizeShapeContext)context).setX(bounds.x);
//		}
//		if (bounds.y < context.getY()) {
//			((ResizeShapeContext)context).setY(bounds.y);
//		}
//		if (bounds.x + bounds.width > context.getWidth()) {
//			((ResizeShapeContext)context).setWidth(bounds.x + bounds.width);
//		}
//		if (bounds.y + bounds.height > context.getHeight()) {
//			((ResizeShapeContext)context).setHeight(bounds.y + bounds.height);
//		}
	}
	
	private ContainerShape getLowestLane(ContainerShape root, boolean useFirstLane) {
		ContainerShape result;
		if (useFirstLane) {
			result = (ContainerShape) FeatureSupport.getFirstLaneInContainer(root);
		} else {
			result = (ContainerShape) FeatureSupport.getLastLaneInContainer(root);
		}
		if (!result.equals(root)) {
			return getLowestLane(result, useFirstLane);
		}
		return result;
	}

	private int compare(int heightBefore, int widthBefore, int heightAfter,
			int widthAfter) {
		if (heightAfter > heightBefore || widthAfter > widthBefore) {
			return 1;
		}
		if (heightAfter < heightBefore || widthAfter < widthBefore) {
			return -1;
		}
		return 0;
	}
}
