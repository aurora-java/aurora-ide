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
package org.eclipse.bpmn2.modeler.core.features;

import org.eclipse.bpmn2.modeler.core.LifecycleEvent;
import org.eclipse.bpmn2.modeler.core.LifecycleEvent.EventType;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.impl.AbstractLayoutFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * Default Graphiti {@code LayoutFeature} class for Shapes.
 */
public abstract class AbstractLayoutBpmn2ShapeFeature extends AbstractLayoutFeature {

	/**
	 * Instantiates a new default LayoutFeature
	 *
	 * @param fp the Feature Provider
	 */
	public AbstractLayoutBpmn2ShapeFeature(IFeatureProvider fp) {
		super(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.ILayout#canLayout(org.eclipse.graphiti.features.context.ILayoutContext)
	 */
	@Override
	public boolean canLayout(ILayoutContext context) {
		return true;
	}

	@Override
	public void execute(IContext context) {
		TargetRuntime rt = TargetRuntime.getCurrentRuntime();
		PictogramElement pe = ((ILayoutContext)context).getPictogramElement();
		rt.notify(new LifecycleEvent(EventType.PICTOGRAMELEMENT_LAYOUT, getFeatureProvider(), context, pe));
		super.execute(context);
	}
}
