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

import org.eclipse.bpmn2.ChoreographyActivity;
import org.eclipse.bpmn2.modeler.core.features.BaseElementFeatureContainer;
import org.eclipse.bpmn2.modeler.core.features.MultiUpdateFeature;
import org.eclipse.bpmn2.modeler.core.features.choreography.LayoutChoreographyFeature;
import org.eclipse.bpmn2.modeler.core.features.choreography.UpdateChoreographyInitiatingParticipantFeature;
import org.eclipse.bpmn2.modeler.core.features.choreography.UpdateChoreographyLabelFeature;
import org.eclipse.bpmn2.modeler.core.features.choreography.UpdateChoreographyParticipantBandsFeature;
import org.eclipse.bpmn2.modeler.core.features.choreography.UpdateChoreographyParticipantRefsFeature;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.ui.features.AbstractDefaultDeleteFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IMoveContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.IResizeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public abstract class AbstractChoreographyFeatureContainer extends BaseElementFeatureContainer {

	@Override
	public Object getApplyObject(IContext context) {
		Object o = super.getApplyObject(context);
		if (
				context instanceof IUpdateContext ||
				context instanceof ILayoutContext ||
				context instanceof IMoveContext ||
				context instanceof IResizeContext
				) {
			PictogramElement pe = ((IPictogramElementContext)context).getPictogramElement();
			if (FeatureSupport.isLabelShape(pe))
				pe = (PictogramElement) pe.eContainer();
			if (FeatureSupport.isChoreographyParticipantBand(pe))
				return o;
		}
		if (o instanceof ChoreographyActivity)
			return o;
		return null;
	}

	@Override
	public MultiUpdateFeature getUpdateFeature(IFeatureProvider fp) {
		MultiUpdateFeature multiUpdate = new MultiUpdateFeature(fp);
		multiUpdate.addFeature(new UpdateChoreographyParticipantRefsFeature(fp));
		multiUpdate.addFeature(new UpdateChoreographyInitiatingParticipantFeature(fp));
		multiUpdate.addFeature(new UpdateChoreographyParticipantBandsFeature(fp));
		multiUpdate.addFeature(new UpdateChoreographyMarkerFeature(fp));
		// This UpdateLabelFeature is called for both the ChoreographyActivity label
		// as well as the Participant Bands labels.
		multiUpdate.addFeature(new UpdateChoreographyLabelFeature(fp));
		return multiUpdate;
	}

	@Override
	public ILayoutFeature getLayoutFeature(IFeatureProvider fp) {
		return new LayoutChoreographyFeature(fp);
	}

	@Override
	public IMoveShapeFeature getMoveFeature(IFeatureProvider fp) {
		return new MoveChoreographyFeature(fp);
	}

	@Override
	public IResizeShapeFeature getResizeFeature(IFeatureProvider fp) {
		return new ResizeChoreographyFeature(fp);
	}

	@Override
	public IDeleteFeature getDeleteFeature(IFeatureProvider fp) {
		return new AbstractDefaultDeleteFeature(fp);
	}
	
	@Override
	public ICustomFeature[] getCustomFeatures(IFeatureProvider fp) {
		ICustomFeature[] superFeatures = super.getCustomFeatures(fp);
		ICustomFeature[] thisFeatures = new ICustomFeature[1 + superFeatures.length];
		int i;
		for (i=0; i<superFeatures.length; ++i)
			thisFeatures[i] = superFeatures[i];
		thisFeatures[i++] = new AddChoreographyParticipantFeature(fp);
		return thisFeatures;
	}
}