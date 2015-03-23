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

package org.eclipse.bpmn2.modeler.ui.features.choreography;

import org.eclipse.bpmn2.ChoreographyActivity;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.modeler.core.features.AbstractCreateFlowElementFeature;
import org.eclipse.bpmn2.modeler.core.model.ModelHandler;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 *
 */
public abstract class AbstractCreateChoreographyActivityFeature<T extends ChoreographyActivity> extends AbstractCreateFlowElementFeature<T> {

	/**
	 * @param fp
	 */
	public AbstractCreateChoreographyActivityFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}
	
	@Override
	public Object[] create(ICreateContext context) {
		ChoreographyActivity element = createBusinessObject(context);
		if (element!=null) {
			changesDone = true;
			ModelHandler mh = ModelHandler.getInstance(getDiagram());
			BPMNDiagram bpmnDiagram = BusinessObjectUtil.getFirstElementOfType(context.getTargetContainer(), BPMNDiagram.class);
			mh.addChoreographyActivity(bpmnDiagram, element);
			PictogramElement pe = null;
			pe = addGraphicalRepresentation(context, element);
			return new Object[] { element, pe };
		}
		else
			changesDone = false;
		return new Object[] { null };
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.AbstractCreateFlowElementFeature#getStencilImageId()
	 */
	@Override
	public abstract String getStencilImageId();

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.AbstractCreateFlowElementFeature#getFlowElementClass()
	 */
	@Override
	public abstract EClass getBusinessObjectClass();
}
