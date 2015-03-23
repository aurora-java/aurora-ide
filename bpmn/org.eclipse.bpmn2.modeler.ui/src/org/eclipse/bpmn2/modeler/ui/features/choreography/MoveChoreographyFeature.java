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
 * @author Innar Made
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.features.choreography;

import org.eclipse.bpmn2.modeler.core.features.MoveFlowNodeFeature;
import org.eclipse.bpmn2.modeler.core.features.choreography.ChoreographyUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;

public class MoveChoreographyFeature extends MoveFlowNodeFeature {

	public MoveChoreographyFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	protected void postMoveShape(final IMoveShapeContext context) {
		super.postMoveShape(context);
		DiagramBehavior db = (DiagramBehavior) getDiagramBehavior();
		db.getUpdateBehavior().setAdapterActive(false);

		ChoreographyUtil.updateParticipantBands(getFeatureProvider(), context.getPictogramElement());
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		// adjust Participant Band size and location
		ChoreographyUtil.updateParticipantBands(getFeatureProvider(), context.getPictogramElement());
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		// adjust Messages and MessageLinks
		ChoreographyUtil.updateChoreographyMessageLinks(getFeatureProvider(), context.getPictogramElement());
		
		db.getUpdateBehavior().setAdapterActive(true);
	}

	@Override
	protected boolean checkConnectionAfterMove(Connection c) {
		if (ChoreographyUtil.isChoreographyMessageLink(c))
			return false;
		return super.checkConnectionAfterMove(c);
	}
}