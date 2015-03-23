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

import org.eclipse.bpmn2.ChoreographyTask;
import org.eclipse.bpmn2.MessageFlow;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.modeler.core.features.choreography.ChoreographyUtil;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.ui.ImageProvider;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

/**
 * @author Bob Brodt
 *
 */
public class RemoveChoreographyMessageFeature extends AbstractCustomFeature {

	/**
	 * @param fp
	 */
	public RemoveChoreographyMessageFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
	public String getName() {
	    return Messages.RemoveChoreographyMessageFeature_Name;
	}
	
	@Override
	public String getDescription() {
	    return Messages.RemoveChoreographyMessageFeature_Description;
	}

	@Override
	public String getImageId() {
		return ImageProvider.IMG_16_REMOVE_MESSAGE;
	}

	@Override
	public boolean isAvailable(IContext context) {
		return true;
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			PictogramElement pe = pes[0];
			Object bo = getBusinessObjectForPictogramElement(pe);
			if (pe instanceof ContainerShape && bo instanceof Participant) {
				Participant participant = (Participant)bo;
				
				Object parent = getBusinessObjectForPictogramElement(((ContainerShape)pe).getContainer());
				if (parent instanceof ChoreographyTask) {
					
					// Check if choreography task is not associated with MessageFlow
					// with this participant as the source
					ChoreographyTask ct=(ChoreographyTask)parent;
					boolean canRemove=false;
					
					for (MessageFlow mf : ct.getMessageFlowRef()) {
						if (participant.equals(mf.getSourceRef())) {
							canRemove = true;
							break;
						}
					}
					
					return (canRemove);
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
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			PictogramElement pe = pes[0];
			Object bo = getBusinessObjectForPictogramElement(pe);
			if (pe instanceof ContainerShape && bo instanceof Participant) {
				ContainerShape containerShape = (ContainerShape)pe;
				Participant participant = (Participant)bo;
				
				Object parent = getBusinessObjectForPictogramElement(containerShape.getContainer());
				if (parent instanceof ChoreographyTask) {
					ChoreographyTask ct=(ChoreographyTask)parent;
					Diagram diagram = getFeatureProvider().getDiagramTypeProvider().getDiagram();
											
					for (MessageFlow mf : ct.getMessageFlowRef()) {
						if (participant.equals(mf.getSourceRef())) {
							// remove the visuals first
							Connection connection = (Connection)Graphiti.getLinkService().getPictogramElements(diagram, mf).get(0);
							if (ChoreographyUtil.removeChoreographyMessageLink(connection)) {
								// now remove the MessageFlow from the ChoreographyTask
								ct.getMessageFlowRef().remove(mf);
								EcoreUtil.delete(mf);

								BPMNShape bpmnShape = BusinessObjectUtil.getFirstElementOfType(containerShape, BPMNShape.class);
								bpmnShape.setIsMessageVisible(false);
							}
							break;
						}
					}
				}
			}
		}
	}
}
