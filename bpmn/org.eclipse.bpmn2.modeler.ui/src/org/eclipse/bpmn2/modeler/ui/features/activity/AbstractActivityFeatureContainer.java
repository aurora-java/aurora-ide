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
package org.eclipse.bpmn2.modeler.ui.features.activity;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.modeler.core.features.BaseElementFeatureContainer;
import org.eclipse.bpmn2.modeler.core.features.DefaultResizeBPMNShapeFeature;
import org.eclipse.bpmn2.modeler.core.features.MultiUpdateFeature;
import org.eclipse.bpmn2.modeler.core.features.activity.MoveActivityFeature;
import org.eclipse.bpmn2.modeler.core.features.activity.UpdateActivityCompensateMarkerFeature;
import org.eclipse.bpmn2.modeler.core.features.activity.UpdateActivityLoopAndMultiInstanceMarkerFeature;
import org.eclipse.bpmn2.modeler.core.features.label.UpdateLabelFeature;
import org.eclipse.bpmn2.modeler.core.preferences.ShapeStyle.LabelPosition;
import org.eclipse.bpmn2.modeler.ui.features.event.AppendEventFeature;
import org.eclipse.bpmn2.modeler.ui.features.gateway.AppendGatewayFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.algorithms.AbstractText;

public abstract class AbstractActivityFeatureContainer extends BaseElementFeatureContainer {

	@Override
	public IUpdateFeature getUpdateFeature(IFeatureProvider fp) {
		MultiUpdateFeature multiUpdate = new MultiUpdateFeature(fp);
		multiUpdate.addFeature(new UpdateActivityCompensateMarkerFeature(fp));
		multiUpdate.addFeature(new UpdateActivityLoopAndMultiInstanceMarkerFeature(fp));
		multiUpdate.addFeature(new UpdateLabelFeature(fp) {
			
			@Override
			public boolean canUpdate(IUpdateContext context) {
				Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
				return bo != null && bo instanceof BaseElement && canApplyTo((BaseElement) bo);
			}

			@Override
			protected LabelPosition getLabelPosition(AbstractText text) {
				return LabelPosition.CENTER;
			}
		});
		return multiUpdate;
	}

	@Override
	public IResizeShapeFeature getResizeFeature(IFeatureProvider fp) {
		return new DefaultResizeBPMNShapeFeature(fp);
	}

	@Override
	public IMoveShapeFeature getMoveFeature(IFeatureProvider fp) {
		return new MoveActivityFeature(fp);
	}

	@Override
	public IDeleteFeature getDeleteFeature(IFeatureProvider fp) {
		return new DeleteActivityFeature(fp);
	}

	@Override
	public ICustomFeature[] getCustomFeatures(IFeatureProvider fp) {
		ICustomFeature[] superFeatures = super.getCustomFeatures(fp);
		ICustomFeature[] thisFeatures = new ICustomFeature[4 + superFeatures.length];
		int i;
		for (i=0; i<superFeatures.length; ++i)
			thisFeatures[i] = superFeatures[i];
		thisFeatures[i++] = new AppendActivityFeature(fp);
		thisFeatures[i++] = new AppendGatewayFeature(fp);
		thisFeatures[i++] = new AppendEventFeature(fp);
		thisFeatures[i++] = new MorphActivityFeature(fp);
		return thisFeatures;
	}
}