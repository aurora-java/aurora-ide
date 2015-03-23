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

import org.eclipse.bpmn2.modeler.core.LifecycleEvent;
import org.eclipse.bpmn2.modeler.core.LifecycleEvent.EventType;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.core.utils.GraphicsUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Shape;

// TODO: Auto-generated Javadoc
/**
 * The Class DefaultResizeBPMNShapeFeature.
 */
public class DefaultResizeBPMNShapeFeature extends DefaultResizeShapeFeature {

	/**
	 * Instantiates a new default resize bpmn shape feature.
	 *
	 * @param fp the fp
	 */
	public DefaultResizeBPMNShapeFeature(IFeatureProvider fp) {
		super(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature#canResizeShape(org.eclipse.graphiti.features.context.IResizeShapeContext)
	 */
	@Override
	public boolean canResizeShape(IResizeShapeContext context) {
		if (FeatureSupport.isLabelShape(context.getPictogramElement()))
			return false;
		
		boolean doit = super.canResizeShape(context);
		LifecycleEvent event = new LifecycleEvent(EventType.PICTOGRAMELEMENT_CAN_RESIZE,
				getFeatureProvider(), context, context.getPictogramElement());
		event.doit = doit;
		TargetRuntime.getCurrentRuntime().notify(event);
		return event.doit;
	}
	
	protected void preResizeShape(IResizeShapeContext context) {
		TargetRuntime rt = TargetRuntime.getCurrentRuntime();
		rt.notify(new LifecycleEvent(EventType.PICTOGRAMELEMENT_PRE_RESIZE,
				getFeatureProvider(), context, context.getPictogramElement()));

	}
	
	protected void postResizeShape(IResizeShapeContext context) {
		TargetRuntime rt = TargetRuntime.getCurrentRuntime();
		rt.notify(new LifecycleEvent(EventType.PICTOGRAMELEMENT_POST_RESIZE,
				getFeatureProvider(), context, context.getPictogramElement()));
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature#resizeShape(org.eclipse.graphiti.features.context.IResizeShapeContext)
	 */
	@Override
	public void resizeShape(IResizeShapeContext context) {
		preResizeShape(context);
		internalResizeShape(context);
		postResizeShape(context);
	}
	
	protected void internalResizeShape(IResizeShapeContext context) {
		Shape shape = context.getShape();

		super.resizeShape(context);
		
		if (shape!=null) {
			AnchorUtil.relocateFixPointAnchors(shape, context.getWidth(), context.getHeight());
		}
		
		DIUtils.updateDIShape(context.getPictogramElement());
		FeatureSupport.updateLabel(getFeatureProvider(), shape, null);

		FeatureSupport.updateConnections(getFeatureProvider(), shape);
		
		for (Connection connection : getDiagram().getConnections()) {
			if (GraphicsUtil.intersects(shape, connection)) {
				FeatureSupport.updateConnection(getFeatureProvider(), connection);
			}
		}

		FeatureSupport.updateCategoryValues(getFeatureProvider(), shape);
		
		for (Anchor a : shape.getAnchors()) {
			for (Connection c : a.getIncomingConnections()) {
				FeatureSupport.updateCategoryValues(getFeatureProvider(), c);
			}
			for (Connection c : a.getOutgoingConnections()) {
				FeatureSupport.updateCategoryValues(getFeatureProvider(), c);
			}
		}
	}
}