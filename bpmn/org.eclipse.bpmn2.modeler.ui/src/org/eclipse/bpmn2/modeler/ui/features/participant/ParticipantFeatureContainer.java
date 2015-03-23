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
package org.eclipse.bpmn2.modeler.ui.features.participant;

import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.modeler.core.features.BaseElementFeatureContainer;
import org.eclipse.bpmn2.modeler.core.features.MultiUpdateFeature;
import org.eclipse.bpmn2.modeler.core.features.containers.LayoutContainerFeature;
import org.eclipse.bpmn2.modeler.core.features.containers.UpdateContainerLabelFeature;
import org.eclipse.bpmn2.modeler.core.features.containers.participant.AddParticipantFeature;
import org.eclipse.bpmn2.modeler.core.features.containers.participant.DirectEditParticipantFeature;
import org.eclipse.bpmn2.modeler.core.features.containers.participant.ResizeParticipantFeature;
import org.eclipse.bpmn2.modeler.core.features.containers.participant.UpdateParticipantFeature;
import org.eclipse.bpmn2.modeler.core.features.containers.participant.UpdateParticipantMultiplicityFeature;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.ui.features.activity.subprocess.PullupFeature;
import org.eclipse.bpmn2.modeler.ui.features.activity.subprocess.PushdownFeature;
import org.eclipse.bpmn2.modeler.ui.features.choreography.AddChoreographyMessageFeature;
import org.eclipse.bpmn2.modeler.ui.features.choreography.BlackboxFeature;
import org.eclipse.bpmn2.modeler.ui.features.choreography.RemoveChoreographyMessageFeature;
import org.eclipse.bpmn2.modeler.ui.features.choreography.RemoveChoreographyParticipantFeature;
import org.eclipse.bpmn2.modeler.ui.features.choreography.ShowDiagramPageFeature;
import org.eclipse.bpmn2.modeler.ui.features.choreography.UpdateChoreographyMessageLinkFeature;
import org.eclipse.bpmn2.modeler.ui.features.choreography.WhiteboxFeature;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IMoveContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.IResizeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class ParticipantFeatureContainer extends BaseElementFeatureContainer {

	@Override
	public Object getApplyObject(IContext context) {
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
				return null;
		}
		Object o = super.getApplyObject(context);
		
		return o;
	}

	@Override
	public boolean canApplyTo(Object o) {
		return super.canApplyTo(o) && o instanceof Participant;
	}

	@Override
	public ICreateFeature getCreateFeature(IFeatureProvider fp) {
		return new CreateParticipantFeature(fp);
	}

	@Override
	public IAddFeature getAddFeature(IFeatureProvider fp) {
		return new AddParticipantFeature(fp);
	}

	@Override
	public IUpdateFeature getUpdateFeature(IFeatureProvider fp) {
		MultiUpdateFeature multiUpdate = new MultiUpdateFeature(fp);
		multiUpdate.addFeature(new UpdateParticipantFeature(fp));
		multiUpdate.addFeature(new UpdateParticipantMultiplicityFeature(fp));
//		multiUpdate.addFeature(new UpdateChoreographyMessageLinkFeature(fp));
		multiUpdate.addFeature(new UpdateContainerLabelFeature(fp));
		return multiUpdate;
	}

	@Override
	public IDirectEditingFeature getDirectEditingFeature(IFeatureProvider fp) {
		return new DirectEditParticipantFeature(fp);
	}

	@Override
	public ILayoutFeature getLayoutFeature(IFeatureProvider fp) {
		return new LayoutContainerFeature(fp);
	}

	@Override
	public IMoveShapeFeature getMoveFeature(IFeatureProvider fp) {
		return new MoveParticipantFeature(fp);
	}

	@Override
	public IResizeShapeFeature getResizeFeature(IFeatureProvider fp) {
		return new ResizeParticipantFeature(fp);
	}

	@Override
	public IDeleteFeature getDeleteFeature(IFeatureProvider fp) {
		return new DeleteParticipantFeature(fp);
	}

	@Override
	public IRemoveFeature getRemoveFeature(IFeatureProvider fp) {
		return new RemoveChoreographyParticipantFeature(fp);
	}

	@Override
	public ICustomFeature[] getCustomFeatures(IFeatureProvider fp) {
		ICustomFeature[] superFeatures = super.getCustomFeatures(fp);
		ICustomFeature[] thisFeatures = new ICustomFeature[8 + superFeatures.length];
		thisFeatures[0] = new ShowDiagramPageFeature(fp);
		int i;
		for (i=0; i<superFeatures.length; ++i)
			thisFeatures[i+1] = superFeatures[i];
		thisFeatures[++i] = new AddChoreographyMessageFeature(fp);
		thisFeatures[++i] = new RemoveChoreographyMessageFeature(fp);
		thisFeatures[++i] = new RotatePoolFeature(fp);
		thisFeatures[++i] = new WhiteboxFeature(fp);
		thisFeatures[++i] = new BlackboxFeature(fp);
		thisFeatures[++i] = new PushdownFeature(fp);
		thisFeatures[++i] = new PullupFeature(fp);
		return thisFeatures;
	}
}