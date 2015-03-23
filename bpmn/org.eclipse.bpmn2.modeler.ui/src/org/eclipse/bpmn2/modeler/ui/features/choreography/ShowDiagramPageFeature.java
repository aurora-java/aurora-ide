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
package org.eclipse.bpmn2.modeler.ui.features.choreography;

import java.io.IOException;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.modeler.ui.IConstants;
import org.eclipse.bpmn2.modeler.ui.ImageProvider;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.features.choreography.ChoreographyUtil;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.ObjectEditingDialog;
import org.eclipse.bpmn2.modeler.core.model.ModelHandler;
import org.eclipse.bpmn2.modeler.core.model.ModelHandlerLocator;
import org.eclipse.bpmn2.modeler.core.preferences.Bpmn2Preferences;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.ui.editor.BPMN2Editor;
import org.eclipse.bpmn2.modeler.ui.editor.BPMN2MultiPageEditor;
import org.eclipse.dd.di.DiagramElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DiagramEditor;

public class ShowDiagramPageFeature extends AbstractCustomFeature {

	public ShowDiagramPageFeature(IFeatureProvider fp) {
		super(fp);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		return Messages.ShowDiagramPageFeature_Name;
	}

	@Override
	public String getDescription() {
		return Messages.ShowDiagramPageFeature_Description;
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			PictogramElement pe = pes[0];
			if (ChoreographyUtil.isChoreographyParticipantBand(pe))
				return false;
			Object bo = getBusinessObjectForPictogramElement(pe);
			BaseElement baseElement = null;
			if (bo instanceof Participant) {
				Participant participant = (Participant)bo;
				baseElement = participant.getProcessRef();
			}
			else if (bo instanceof CallActivity) {
				CallActivity callActivity = (CallActivity)bo;
				baseElement = callActivity.getCalledElementRef();
			}
			else if (bo instanceof BaseElement) {
				baseElement = (BaseElement)bo;
			}
			
			if (DIUtils.findBPMNDiagram(baseElement) != null)
				return true;
		}
		return false;
	}

	@Override
	public boolean isAvailable(IContext context) {
		return true;
	}

	@Override
	public void execute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			PictogramElement pe = pes[0];
			Object bo = getBusinessObjectForPictogramElement(pe);
			BaseElement baseElement = null;
			if (bo instanceof Participant) {
				Participant participant = (Participant)bo;
				baseElement = participant.getProcessRef();
			}
			else if (bo instanceof CallActivity) {
				CallActivity callActivity = (CallActivity)bo;
				baseElement = callActivity.getCalledElementRef();
			}
			else if (bo instanceof BaseElement) {
				baseElement = (BaseElement)bo;
			}
			BPMNDiagram bpmnDiagram = DIUtils.findBPMNDiagram(baseElement);
			if (bpmnDiagram!=null) {
				if (bpmnDiagram.eResource() == ((EObject)bo).eResource()) {
					BPMN2MultiPageEditor mpe = ((BPMN2Editor)getDiagramEditor()).getMultipageEditor();
					mpe.showDesignPage(bpmnDiagram);
				}
				else {
					// the called process lives in another BPMN file:
					// open a new editor to display it.
					BPMN2Editor.openEditor(bpmnDiagram.eResource().getURI());
				}
			}
		}
	}

	@Override
	public String getImageId() {
		return ImageProvider.IMG_16_EXPAND;
	}

	@Override
	public boolean hasDoneChanges() {
		return false;
	}

}
