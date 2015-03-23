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

import java.util.List;

import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.CallableElement;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.features.choreography.ChoreographyUtil;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.ui.ImageProvider;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * @author Bob Brodt
 *
 */
public class BlackboxFeature extends AbstractCustomFeature {

	/**
	 * @param fp
	 */
	public BlackboxFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
	public String getName() {
	    return Messages.BlackboxFeature_Name;
	}
	
	@Override
	public String getDescription() {
	    return Messages.BlackboxFeature_Description;
	}

	@Override
	public String getImageId() {
		return ImageProvider.IMG_16_BLACKBOX;
	}

	@Override
	public boolean isAvailable(IContext context) {
		if (context instanceof ICustomContext) {
			PictogramElement[] pes = ((ICustomContext)context).getPictogramElements();
			if (pes != null && pes.length == 1) {
				PictogramElement pe = pes[0];
				if (ChoreographyUtil.isChoreographyParticipantBand(pe)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			PictogramElement pe = pes[0];
			if (ChoreographyUtil.isChoreographyParticipantBand(pe)) {
				Participant participant = (Participant) getBusinessObjectForPictogramElement(pe);
				Process process = participant.getProcessRef();
				if (process!=null) {
					BPMNDiagram bpmnDiagram = DIUtils.findBPMNDiagram(process);
					return bpmnDiagram!=null;
				}
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public void execute(ICustomContext context) {
		PictogramElement pe = context.getPictogramElements()[0];
		Participant participant = (Participant) getBusinessObjectForPictogramElement(pe);
		participant.setProcessRef(null);
	}
}
