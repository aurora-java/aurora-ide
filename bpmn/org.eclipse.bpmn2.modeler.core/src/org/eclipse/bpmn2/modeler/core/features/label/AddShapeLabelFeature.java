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

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

public class AddShapeLabelFeature extends AbstractAddLabelFeature {

	public AddShapeLabelFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canAdd(IAddContext context) {
		return true;
	}

	@Override
	public PictogramElement add(IAddContext context) {
		Shape labelShape = null;

		BaseElement businessObject = (BaseElement) context.getNewObject();
		ContainerShape targetContainer = getTargetContainer(context);
		PictogramElement labelOwner = getLabelOwner(context);
		if (labelOwner instanceof ContainerShape) {
			labelShape = peService.createShape(targetContainer, true);
			createText(labelOwner, labelShape, businessObject);
		}
		
		// force an update of the label
		updatePictogramElement(context, labelShape);
		
		return labelShape;
	}
	
	/**
	 * Get the correct target container for the label shape.
	 * 
	 * @param context
	 * @return the target container for the current context
	 */
	protected ContainerShape getTargetContainer(IAddContext context) {
		return getFeatureProvider().getDiagramTypeProvider().getDiagram();
	}
}
