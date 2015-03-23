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
package org.eclipse.bpmn2.modeler.core.features.activity;


import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.features.AbstractLayoutBpmn2ShapeFeature;
import org.eclipse.bpmn2.modeler.core.features.event.AbstractBoundaryEventOperation;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.ShapeDecoratorUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;

// TODO: Auto-generated Javadoc
/**
 * The Class LayoutActivityFeature.
 */
public class LayoutActivityFeature extends AbstractLayoutBpmn2ShapeFeature {

	/**
	 * Instantiates a new layout activity feature.
	 *
	 * @param fp the fp
	 */
	public LayoutActivityFeature(IFeatureProvider fp) {
		super(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.AbstractLayoutBpmn2ShapeFeature#canLayout(org.eclipse.graphiti.features.context.ILayoutContext)
	 */
	@Override
	public boolean canLayout(ILayoutContext context) {
		PictogramElement pe = context.getPictogramElement();
		if (!(pe instanceof ContainerShape)) {
			return false;
		}
		Object bo = BusinessObjectUtil.getFirstElementOfType(pe, BaseElement.class);
		return bo != null && bo instanceof Activity;
	}

	@Override
	public boolean layout(ILayoutContext context) {
		IGaService gaService = Graphiti.getGaService();
	
		ContainerShape containerShape = (ContainerShape) context.getPictogramElement();
		GraphicsAlgorithm parentGa = containerShape.getGraphicsAlgorithm();
		int newWidth = parentGa.getWidth();
		int newHeight = parentGa.getHeight();
	
		Shape rectShape = containerShape.getChildren().get(0);
		gaService.setSize(rectShape.getGraphicsAlgorithm(), newWidth, newHeight);
		layoutInRectangle((RoundedRectangle) rectShape.getGraphicsAlgorithm());
	
		ShapeDecoratorUtil.setActivityMarkerOffest(containerShape, getMarkerContainerOffset());
		ShapeDecoratorUtil.layoutActivityMarker(containerShape);
	
		Activity activity = BusinessObjectUtil.getFirstElementOfType(containerShape, Activity.class);
		new AbstractBoundaryEventOperation() {
			@Override
			protected void doWorkInternal(ContainerShape container) {
				layoutPictogramElement(container);
			}
		}.doWork(activity, getDiagram());
	
		DIUtils.updateDIShape(containerShape);
		
		if (containerShape.eContainer() instanceof ContainerShape) {
			PictogramElement pe = (PictogramElement) containerShape.eContainer();
			if (BusinessObjectUtil.containsElementOfType(pe, SubProcess.class)) {
				layoutPictogramElement(pe);
			}
		}
		return true;
	}

	/**
	 * Gets the marker container offset.
	 *
	 * @return the marker container offset
	 */
	protected int getMarkerContainerOffset() {
		return 0;
	}

	/**
	 * Layout in rectangle.
	 *
	 * @param rect the rect
	 */
	protected void layoutInRectangle(RoundedRectangle rect) {
	}
}