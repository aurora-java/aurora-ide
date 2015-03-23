/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013, 2014 Red Hat, Inc.
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

package org.eclipse.bpmn2.modeler.core.features.containers;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.features.label.UpdateLabelFeature;
import org.eclipse.bpmn2.modeler.core.preferences.ShapeStyle.LabelPosition;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.mm.algorithms.AbstractText;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

/**
 *
 */
public class UpdateContainerLabelFeature extends UpdateLabelFeature {

	/**
	 * @param fp the Feature Provider
	 */
	public UpdateContainerLabelFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	protected LabelPosition getLabelPosition(AbstractText text) {
		if (text.getAngle() == -90)
			return LabelPosition.LEFT;
		return LabelPosition.TOP;
	}

	@Override
	protected int getLabelWidth(AbstractText text) {
		if (text.getAngle() == -90)
			return getLabelSize(text).height;
		return getLabelSize(text).width;
	}

	@Override
	protected int getLabelHeight(AbstractText text) {
		if (text.getAngle() == -90)
			return getLabelSize(text).width;
		return getLabelSize(text).height;
	}

	@Override
	public boolean update(IUpdateContext context) {
		Hashtable<Shape, Point> offsetMap = (Hashtable<Shape, Point>) context.getProperty(GraphitiConstants.LABEL_OFFSET_MAP);
		Point offset = (Point) context.getProperty(GraphitiConstants.LABEL_OFFSET);
		boolean isAdding = isAddingLabel(context);
		PictogramElement ownerPE = FeatureSupport.getLabelOwner(context);
		boolean isHorizontal = FeatureSupport.isHorizontal((ContainerShape)ownerPE);
		Shape labelShape = FeatureSupport.getLabelShape(ownerPE);
		AbstractText textGA = (AbstractText) labelShape.getGraphicsAlgorithm();
		textGA.setAngle(isHorizontal ? -90 : 0);

		ContainerShape rootContainer = FeatureSupport.getRootContainer((ContainerShape)ownerPE);
		super.adjustLabelLocation(rootContainer, isAdding, offset);
		
		// now update all of the Labels for all contained shapes, including Lanes
		// we need to make a copy of the root container children because
		// {@link UpdateLabelFeature#adjustLabelLocation()} will change the order
		// of the children when it pushes the label to the top of the Z-order.
		List<PictogramElement> peList = new ArrayList<PictogramElement>();
		TreeIterator<EObject> iter = rootContainer.eAllContents();
		while (iter.hasNext()) {
			EObject o = iter.next();
			if (o instanceof PictogramElement) {
				PictogramElement pe = (PictogramElement) o;
				if (FeatureSupport.hasBPMNShape(pe) || FeatureSupport.hasBPMNEdge(pe))
					peList.add(pe);
			}
		}
		for (PictogramElement pe : peList) {
			if (FeatureSupport.hasBPMNShape(pe)) {
				if (FeatureSupport.isLane(pe)) {
					labelShape = FeatureSupport.getLabelShape(pe);
					textGA = (AbstractText) labelShape.getGraphicsAlgorithm();
					textGA.setAngle(isHorizontal ? -90 : 0);
					super.adjustLabelLocation(pe, isAdding, offset);
				}
				else {
					Point p = offsetMap==null ? null : offsetMap.get(pe);
					FeatureSupport.updateLabel(getFeatureProvider(), pe, p);
				}
			}
			else if (FeatureSupport.hasBPMNEdge(pe)) {
				Point p = offsetMap==null ? null : offsetMap.get(pe);
				FeatureSupport.updateLabel(getFeatureProvider(), pe, p);
			}
		}
		return true;
	}
	
	protected ContainerShape getTargetContainer(PictogramElement ownerPE) {
		// TODO: fix this so the label is a child of the Lane or Pool.
		// There's a problem with Resize Feature if the label is a direct child of Lane/Pool.
		return (ContainerShape) ownerPE.eContainer();
	}

}
