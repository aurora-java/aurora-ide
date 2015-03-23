/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013, 2014 Red Hat, Inc.
 * All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.features.label;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.modeler.core.preferences.ShapeStyle.LabelPosition;
import org.eclipse.bpmn2.modeler.core.preferences.ShapeStyle;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

public class MoveShapeLabelFeature extends DefaultMoveShapeFeature {

	public MoveShapeLabelFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canMoveShape(IMoveShapeContext context) {
		return super.canMoveShape(context);
	}

	@Override
	public void moveShape(IMoveShapeContext context) {
		// if this Label Shape is part of a multiselection, and if its owner is included
		// in that multiselection, then do not move the label. Moving the label is
		// already handled by the label's owner.
		PictogramElement pes[] = getFeatureProvider().getDiagramTypeProvider().
				getDiagramBehavior().getDiagramContainer().getSelectedPictogramElements();
		Shape labelShape = context.getShape();
		for (PictogramElement pe : pes) {
			ContainerShape s = BusinessObjectUtil.getFirstElementOfType(pe, ContainerShape.class);
			if (s==labelShape)
				return;
		}
		
		// If the label location relative to its shape is fixed (i.e. not "MOVABLE")
		// then move the owning shape so that it follows the label's move. 
		ContainerShape elementShape = BusinessObjectUtil.getFirstElementOfType(labelShape, ContainerShape.class);
		BaseElement element = (BaseElement) BusinessObjectUtil.getFirstElementOfType(elementShape, BaseElement.class);
		ShapeStyle ss = ShapeStyle.getShapeStyle(element);
		if (ss.getLabelPosition() != LabelPosition.MOVABLE) {
			GraphicsAlgorithm elementGA = elementShape.getGraphicsAlgorithm();
			MoveShapeContext newContext = new MoveShapeContext(elementShape);
			newContext.setDeltaX(context.getDeltaX());
			newContext.setDeltaY(context.getDeltaY());
			newContext.setX(elementGA.getX() + context.getDeltaX());
			newContext.setY(elementGA.getY() + context.getDeltaY());
			newContext.setSourceContainer(context.getSourceContainer());
			newContext.setTargetContainer(context.getTargetContainer());
			IMoveShapeFeature f = getFeatureProvider().getMoveShapeFeature(newContext);
			f.moveShape(newContext);
		}
		
		// Now move the label itself
		super.moveShape(context);
	}

}
