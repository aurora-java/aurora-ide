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

package org.eclipse.bpmn2.modeler.ui.features.lane;

import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.LaneSet;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.ui.features.AbstractDefaultDeleteFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.impl.ResizeShapeContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

/**
 * @author Bob Brodt
 *
 */
public class DeleteLaneFeature extends AbstractDefaultDeleteFeature {

	/**
	 * @param fp
	 */
	public DeleteLaneFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public void delete(IDeleteContext context) {
		ContainerShape laneContainerShape = (ContainerShape) context.getPictogramElement();
		ContainerShape parentContainerShape = laneContainerShape.getContainer();
		Lane lane = (Lane)getBusinessObjectForPictogramElement(laneContainerShape);
		if (lane==null) {
			// this can happen if the lane was already deleted by its parent Lane or Pool
			return;
		}
		LaneSet laneSet = (LaneSet)lane.eContainer();
		
		if (parentContainerShape != null) {
			boolean before = false;
			ContainerShape neighborContainerShape = getLaneAfter(laneContainerShape);
			if (neighborContainerShape == null) {
				neighborContainerShape = getLaneBefore(laneContainerShape);
				if (neighborContainerShape == null) {
					super.delete(context);
					if (laneSet.getLanes().size()==0) {
						EcoreUtil.delete(laneSet);
					}
					return;
				} else {
					before = true;
				}
			}
			boolean isHorizontal = FeatureSupport.isHorizontal(laneContainerShape);
			GraphicsAlgorithm ga = laneContainerShape.getGraphicsAlgorithm();
			GraphicsAlgorithm neighborGA = neighborContainerShape.getGraphicsAlgorithm();
			ResizeShapeContext newContext = new ResizeShapeContext(neighborContainerShape);
			if (!before) {
				Graphiti.getGaService().setLocation(neighborGA, ga.getX(), ga.getY());
			}
			newContext.setLocation(neighborGA.getX(), neighborGA.getY());
			if (isHorizontal) {
				newContext.setHeight(neighborGA.getHeight() + ga.getHeight());
				newContext.setWidth(neighborGA.getWidth());
			} else {
				newContext.setHeight(neighborGA.getHeight());
				newContext.setWidth(neighborGA.getWidth() + ga.getWidth());
			}
			
			IResizeShapeFeature resizeFeature = getFeatureProvider().getResizeShapeFeature(newContext);
			if (resizeFeature.canResizeShape(newContext)) {
				super.delete(context);
				resizeFeature.resizeShape(newContext);
				return;
			}
		}
		super.delete(context);
	}
	
	private ContainerShape getLaneBefore(ContainerShape container) {
		if (!BusinessObjectUtil.containsElementOfType(container, Lane.class)) {
			return null;
		}
		
		ContainerShape parentContainerShape = container.getContainer();
		if (parentContainerShape == null) {
			return null;
		}
		
		GraphicsAlgorithm ga = container.getGraphicsAlgorithm();
		int x = ga.getX();
		int y = ga.getY();
		boolean isHorizontal = FeatureSupport.isHorizontal(container);
		
		ContainerShape result = null;
		for (PictogramElement picElem : BusinessObjectUtil.getChildElementsOfType(parentContainerShape, Lane.class)) {
			if (picElem instanceof ContainerShape && !picElem.equals(container)) {
				ContainerShape currentContainerShape = (ContainerShape) picElem;
				GraphicsAlgorithm currentGA = currentContainerShape.getGraphicsAlgorithm();
				if (isHorizontal) {
					if (currentGA.getY() < y) {
						if (result != null) {
							GraphicsAlgorithm resultGA = result.getGraphicsAlgorithm();
							if (resultGA.getY() < currentGA.getY()) {
								result = currentContainerShape;
							}
						} else {
							result = currentContainerShape;
						}
					}
				} else {
					if (currentGA.getX() < x) {
						if (result != null) {
							GraphicsAlgorithm resultGA = result.getGraphicsAlgorithm();
							if (resultGA.getX() < currentGA.getX()) {
								result = currentContainerShape;
							}
						} else {
							result = currentContainerShape;
						}
					}
				}
			}
		}
		return result;
	}
	
	private ContainerShape getLaneAfter(ContainerShape container) {
		if (!BusinessObjectUtil.containsElementOfType(container, Lane.class)) {
			return null;
		}
		
		ContainerShape parentContainerShape = container.getContainer();
		if (parentContainerShape == null) {
			return null;
		}
		
		GraphicsAlgorithm ga = container.getGraphicsAlgorithm();
		int x = ga.getX();
		int y = ga.getY();
		boolean isHorizontal = FeatureSupport.isHorizontal(container);
		
		ContainerShape result = null;
		for (PictogramElement picElem : BusinessObjectUtil.getChildElementsOfType(parentContainerShape, Lane.class)) {
			if (picElem instanceof ContainerShape && !picElem.equals(container)) {
				ContainerShape currentContainerShape = (ContainerShape) picElem;
				GraphicsAlgorithm currentGA = currentContainerShape.getGraphicsAlgorithm();
				if (isHorizontal) {
					if (currentGA.getY() > y) {
						if (result != null) {
							GraphicsAlgorithm resultGA = result.getGraphicsAlgorithm();
							if (resultGA.getY() > currentGA.getY()) {
								result = currentContainerShape;
							}
						} else {
							result = currentContainerShape;
						}
					}
				} else {
					if (currentGA.getX() > x) {
						if (result != null) {
							GraphicsAlgorithm resultGA = result.getGraphicsAlgorithm();
							if (resultGA.getX() > currentGA.getX()) {
								result = currentContainerShape;
							}
						} else {
							result = currentContainerShape;
						}
					}
				}
			}
		}
		return result;
	}
}
