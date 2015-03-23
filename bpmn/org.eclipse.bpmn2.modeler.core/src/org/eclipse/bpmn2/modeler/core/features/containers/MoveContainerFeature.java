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

import java.util.List;

import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.features.DefaultMoveBPMNShapeFeature;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

/**
 *
 */
public class MoveContainerFeature extends DefaultMoveBPMNShapeFeature {
	protected List<PictogramElement> children;

	/**
	 * @param fp
	 */
	public MoveContainerFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	protected void preMoveShape(IMoveShapeContext context) {
		Shape shape = context.getShape();
		if (!FeatureSupport.isLabelShape(shape) && shape instanceof ContainerShape) {
			ContainerShape rootContainer = FeatureSupport.getRootContainer((ContainerShape)shape);
			children =  FeatureSupport.getPoolAndLaneDescendants(rootContainer);
		}
		super.preMoveShape(context);
	}

	@Override
	protected void postMoveShape(IMoveShapeContext context) {
		Shape shape = context.getShape();

		if (!FeatureSupport.isLabelShape(shape) && shape instanceof ContainerShape) {
			for (PictogramElement pe : children) {
				if (pe instanceof Connection) {
					FeatureSupport.updateConnection(getFeatureProvider(), (Connection)pe, true);
				}
				else {
					DIUtils.updateDIShape(pe);
					FeatureSupport.updateLabel(getFeatureProvider(), pe, null);
				}
			}
		}
		
		super.postMoveShape(context);
	}
}
