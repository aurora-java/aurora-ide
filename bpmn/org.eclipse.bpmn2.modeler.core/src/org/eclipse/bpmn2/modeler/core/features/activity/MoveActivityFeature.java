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
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.features.MoveFlowNodeFeature;
import org.eclipse.bpmn2.modeler.core.features.event.AbstractBoundaryEventOperation;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

// TODO: Auto-generated Javadoc
/**
 * The Class MoveActivityFeature.
 */
public class MoveActivityFeature extends MoveFlowNodeFeature {

	/**
	 * Instantiates a new move activity feature.
	 *
	 * @param fp the Feature Provider
	 */
	public MoveActivityFeature(IFeatureProvider fp) {
		super(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.DefaultMoveBPMNShapeFeature#preMoveShape(org.eclipse.graphiti.features.context.IMoveShapeContext)
	 */
	@Override
	protected void preMoveShape(IMoveShapeContext context) {
		super.preMoveShape(context);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.MoveFlowNodeFeature#postMoveShape(org.eclipse.graphiti.features.context.IMoveShapeContext)
	 */
	@Override
	protected void postMoveShape(final IMoveShapeContext context) {
		super.postMoveShape(context);

		Shape containerShape = context.getShape();
		Activity activity = BusinessObjectUtil.getFirstElementOfType(containerShape, Activity.class);
		
		new AbstractBoundaryEventOperation() {
			@Override
			protected void doWorkInternal(ContainerShape container) {
				GraphicsAlgorithm ga = container.getGraphicsAlgorithm();

				MoveShapeContext newContext = new MoveShapeContext(container);
				newContext.setDeltaX(context.getDeltaX());
				newContext.setDeltaY(context.getDeltaY());
				newContext.setSourceContainer(context.getSourceContainer());
				newContext.setTargetContainer(context.getTargetContainer());
				newContext.setTargetConnection(context.getTargetConnection());
				newContext.setLocation(ga.getX(), ga.getY());
				newContext.putProperty(GraphitiConstants.ACTIVITY_MOVE_PROPERTY, true);

				IMoveShapeFeature moveFeature = getFeatureProvider().getMoveShapeFeature(newContext);
				if (moveFeature.canMoveShape(newContext)) {
					moveFeature.moveShape(newContext);
				}
			}
		}.doWork(activity, getDiagram());
		
		layoutPictogramElement(containerShape);
		if (containerShape.eContainer() instanceof ContainerShape) {
			PictogramElement pe = (PictogramElement) containerShape.eContainer();
			if (BusinessObjectUtil.containsElementOfType(pe, SubProcess.class)) {
				layoutPictogramElement(pe);
			}
		}
	}
}