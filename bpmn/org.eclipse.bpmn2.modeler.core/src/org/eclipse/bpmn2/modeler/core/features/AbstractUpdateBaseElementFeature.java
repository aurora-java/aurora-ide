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

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.modeler.core.features.label.UpdateLabelFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;

/**
 * This is the Graphiti UpdateFeature class for all BPMN2 model elements that
 * subclass {@link BaseElement}. This includes pretty much everything, except
 * Label shapes. This is to prevent a BaseElement shape from trying to update
 * its Label - this will be handled separately by the {@link UpdateLabelFeature}
 * as part of a {@link MultiUpdateFeature}.
 */
public abstract class AbstractUpdateBaseElementFeature<T extends BaseElement> extends AbstractBpmn2UpdateFeature {

	/**
	 * Instantiates a new UpdateFeature.
	 *
	 * @param fp the Feature Provider
	 */
	public AbstractUpdateBaseElementFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canUpdate(IUpdateContext context) {
		if (context.getPictogramElement() instanceof ContainerShape) {
			Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
			try {
				return ((T)bo) != null;
			}
			catch (ClassCastException e) {
				return false;
			}
		}
		return false;
	}
}
