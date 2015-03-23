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

import java.util.List;

import org.eclipse.bpmn2.ChoreographyActivity;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.modeler.core.features.DefaultResizeBPMNShapeFeature;
import org.eclipse.bpmn2.modeler.core.features.choreography.ChoreographyUtil;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class ResizeChoreographyFeature extends DefaultResizeBPMNShapeFeature {

	final static int TEXT_H = 15;

	public ResizeChoreographyFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canResizeShape(IResizeShapeContext context) {
		PictogramElement pe = context.getPictogramElement();
		if (pe instanceof ContainerShape) {
			if (BusinessObjectUtil.getFirstBaseElement(pe) instanceof ChoreographyActivity) {
				List<BPMNShape> bands = ChoreographyUtil.getParticipantBandBpmnShapes((ContainerShape)pe);
				int h = TEXT_H;
	
				for (BPMNShape shape : bands) {
					h += shape.getBounds().getHeight();
				}
	
				boolean doit = context.getHeight() > 0 ? context.getHeight() > h : true;
				if (doit && !super.canResizeShape(context))
					doit = false;
				return doit;
			}
		}
		return false;
	}

	@Override
	public void resizeShape(IResizeShapeContext context) {
		super.resizeShape(context);

		// adjust Participant Band size and location
		ChoreographyUtil.updateParticipantBands(getFeatureProvider(), context.getPictogramElement());
		// adjust Messages and MessageLinks
		ChoreographyUtil.updateChoreographyMessageLinks(getFeatureProvider(), context.getPictogramElement());
	}

}