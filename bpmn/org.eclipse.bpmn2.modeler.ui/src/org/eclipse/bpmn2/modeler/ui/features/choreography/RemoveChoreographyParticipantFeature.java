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
 * @author Bob Brodt
 ******************************************************************************/

package org.eclipse.bpmn2.modeler.ui.features.choreography;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.ChoreographyActivity;
import org.eclipse.bpmn2.ChoreographyTask;
import org.eclipse.bpmn2.MessageFlow;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.modeler.core.features.choreography.ChoreographyUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.impl.DefaultRemoveFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

/**
 * @author Bob Brodt
 *
 */
public class RemoveChoreographyParticipantFeature extends DefaultRemoveFeature {

	/**
	 * @param fp
	 */
	public RemoveChoreographyParticipantFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canRemove(IRemoveContext context) {
		// participant bands in a ChoreographyActivity CAN be "removed" (from the modelObject's
		// participantRef list) but not "deleted" (from the model)
		if (ChoreographyUtil.isChoreographyParticipantBand(context.getPictogramElement())) {
			return true;
		}
		return false;
	}

	@Override
	public void execute(IContext context) {
		IRemoveContext dc = (IRemoveContext)context;
		PictogramElement pe = dc.getPictogramElement();
		ContainerShape choreographyActivityShape = null;
		if (ChoreographyUtil.isChoreographyParticipantBand(pe)) {
			PictogramElement labelShape = FeatureSupport.getLabelShape(pe);
			if (labelShape!=null)
				Graphiti.getPeService().deletePictogramElement(labelShape);
			Participant participant = (Participant)getBusinessObjectForPictogramElement(pe);
			choreographyActivityShape = (ContainerShape)pe.eContainer();
			Object bo = getBusinessObjectForPictogramElement(choreographyActivityShape);
			// update the Initiating Participant
			if (bo instanceof ChoreographyActivity) {
				ChoreographyActivity choreographyActivity = (ChoreographyActivity)bo;
				choreographyActivity.getParticipantRefs().remove(participant);
				if (choreographyActivity.getInitiatingParticipantRef() == participant) {
					// select a new Initiating Participant
					Participant initiatingParticipant = null;
					if (choreographyActivity.getParticipantRefs().size()>0)
						initiatingParticipant = choreographyActivity.getParticipantRefs().get(0);
					choreographyActivity.setInitiatingParticipantRef(initiatingParticipant);
				}
			}
			// update Choreography Task message flows
			if (bo instanceof ChoreographyTask) {
				ChoreographyTask choreographyTask = (ChoreographyTask)bo;
				List<MessageFlow> messageFlows = new ArrayList<MessageFlow>();
				messageFlows.addAll(choreographyTask.getMessageFlowRef());
				for (MessageFlow mf : messageFlows) {
					if (mf.getSourceRef() == participant) {
						choreographyTask.getMessageFlowRef().remove(mf);
					}
				}
			}
			super.execute(context);

			ChoreographyUtil.updateParticipantBands(getFeatureProvider(), choreographyActivityShape);
		}
	}

}
