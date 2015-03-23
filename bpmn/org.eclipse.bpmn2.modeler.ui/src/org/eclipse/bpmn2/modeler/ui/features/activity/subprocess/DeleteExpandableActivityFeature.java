/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013 Red Hat, Inc.
 * All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.features.activity.subprocess;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.ui.features.activity.DeleteActivityFeature;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class DeleteExpandableActivityFeature extends DeleteActivityFeature {

	public DeleteExpandableActivityFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public void delete(final IDeleteContext context) {
		// Delete the BPMNDiagram if there is one
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);
		if (bo instanceof FlowElementsContainer) {
			BPMNDiagram bpmnDiagram = DIUtils.findBPMNDiagram((BaseElement)bo);
			if (bpmnDiagram != null) {
				DIUtils.deleteDiagram(getDiagramBehavior(), bpmnDiagram);
			}
		}

		super.delete(context);
	}
}
