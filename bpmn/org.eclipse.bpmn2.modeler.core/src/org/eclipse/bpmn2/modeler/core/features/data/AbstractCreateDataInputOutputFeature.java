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
package org.eclipse.bpmn2.modeler.core.features.data;

import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2CreateFeature;
import org.eclipse.bpmn2.modeler.core.model.ModelHandler;
import org.eclipse.bpmn2.modeler.core.model.ModelHandlerLocator;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public abstract class AbstractCreateDataInputOutputFeature<T extends ItemAwareElement> extends AbstractBpmn2CreateFeature<T> {

	public AbstractCreateDataInputOutputFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		if (!super.canCreate(context))
			return false;
		
		Object containerBO = BusinessObjectUtil.getBusinessObjectForPictogramElement( context.getTargetContainer() );
		boolean intoDiagram = containerBO instanceof BPMNDiagram;
		if (intoDiagram) {
			// SubProcess are not allowed to define their own DataInputs or DataOutputs
			BPMNDiagram bpmnDiagram = (BPMNDiagram) containerBO;
			if (bpmnDiagram.getPlane().getBpmnElement() instanceof SubProcess)
				intoDiagram = false;
		}
		boolean intoLane = FeatureSupport.isTargetLane(context) && FeatureSupport.isTargetLaneOnTop(context);
		boolean intoParticipant = FeatureSupport.isTargetParticipant(context);
		return intoDiagram || intoLane || intoParticipant;
	}

	@Override
	public Object[] create(ICreateContext context) {
		T element = createBusinessObject(context);
		ModelHandler mh = ModelHandlerLocator.getModelHandler(getDiagram().eResource());
		mh.addDataInputOutput(context.getTargetContainer(), element);
		PictogramElement pe = addGraphicalRepresentation(context, element);
		return new Object[] { element, pe };
	}

	protected abstract String getStencilImageId();

	@Override
	public String getCreateImageId() {
		return getStencilImageId();
	}

	@Override
	public String getCreateLargeImageId() {
		return getStencilImageId();
	}
}