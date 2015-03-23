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
package org.eclipse.bpmn2.modeler.core.features.label;

import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.impl.ResizeShapeContext;
import org.eclipse.graphiti.features.impl.AbstractLayoutFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class LayoutLabelFeature extends AbstractLayoutFeature {

	public LayoutLabelFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canLayout(ILayoutContext context) {
		return true;
	}

	@Override
	public boolean layout(ILayoutContext context) {
//		PictogramElement pictogramElement = context.getPictogramElement();
//		SubProcess subProcess = BusinessObjectUtil.getFirstElementOfType(pictogramElement, SubProcess.class);
//		if (subProcess!=null && pictogramElement instanceof ContainerShape) {
//			try {
//				ContainerShape containerShape = (ContainerShape)pictogramElement;
//				BPMNShape shape = DIUtils.findBPMNShape(subProcess);
//				
//				if (shape.isIsExpanded()) {
//					
//					// SubProcess is expanded
//					
//					boolean needResize = false;
//					GraphicsAlgorithm parentGa = containerShape.getGraphicsAlgorithm();
//					
//					for (PictogramElement pe : FeatureSupport.getContainerChildren(containerShape)) {
//						GraphicsAlgorithm ga = pe.getGraphicsAlgorithm();
//						if (ga!=null) {
//							if (ga.getX() < 0 || ga.getY() < 0) {
//								needResize = true;
//								break;
//							}
//							if (ga.getX() + ga.getWidth() > parentGa.getWidth()) {
//								needResize = true;
//								break;
//							}
//							if (ga.getY() + ga.getHeight() > parentGa.getHeight()) {
//								needResize = true;
//								break;
//							}
//						}
//					}
//					if (needResize) {
//						ResizeShapeContext resizeContext = new ResizeShapeContext(containerShape);
//						resizeContext.setX(parentGa.getX());
//						resizeContext.setY(parentGa.getY());
//						resizeContext.setWidth(parentGa.getWidth());
//						resizeContext.setHeight(parentGa.getHeight());
//						IResizeShapeFeature resizeFeature = getFeatureProvider().getResizeShapeFeature(resizeContext);
//						resizeFeature.resizeShape(resizeContext);
//					}
//					
//					FeatureSupport.setContainerChildrenVisible(containerShape, true);
//				}
//				else {
//					
//					// SubProcess is collapsed
//					
//					FeatureSupport.setContainerChildrenVisible(containerShape, false);
//				}
//				
//			} catch (Exception e) {
//				// It's OK, I've played a programmer before...
//				// e.printStackTrace();
//			}
//		}
		return true;
	}
}
