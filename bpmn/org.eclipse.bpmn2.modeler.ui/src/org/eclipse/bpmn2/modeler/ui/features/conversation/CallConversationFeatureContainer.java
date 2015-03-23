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
package org.eclipse.bpmn2.modeler.ui.features.conversation;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.CallConversation;
import org.eclipse.bpmn2.modeler.core.features.DefaultMoveBPMNShapeFeature;
import org.eclipse.bpmn2.modeler.core.features.MultiUpdateFeature;
import org.eclipse.bpmn2.modeler.core.features.label.UpdateLabelFeature;
import org.eclipse.bpmn2.modeler.core.preferences.ShapeStyle.LabelPosition;
import org.eclipse.bpmn2.modeler.ui.ImageProvider;
import org.eclipse.bpmn2.modeler.ui.features.AbstractDefaultDeleteFeature;
import org.eclipse.bpmn2.modeler.ui.features.activity.subprocess.AbstractExpandableActivityFeatureContainer;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;
import org.eclipse.graphiti.mm.algorithms.AbstractText;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;

public class CallConversationFeatureContainer extends AbstractExpandableActivityFeatureContainer {

	@Override
	public boolean canApplyTo(Object o) {
		return super.canApplyTo(o) && o instanceof CallConversation;
	}

	@Override
	public ICreateFeature getCreateFeature(IFeatureProvider fp) {
		return new AbstractCreateConversationNodeFeature<CallConversation>(fp) {

			@Override
			public String getCreateImageId() {
				return ImageProvider.IMG_16_CALL_CONVERSATION;
			}

			@Override
			public EClass getBusinessObjectClass() {
				return Bpmn2Package.eINSTANCE.getCallConversation();
			}
		};			
	}

	@Override
	public IAddFeature getAddFeature(IFeatureProvider fp) {
		return new AddConversationNodeFeature<CallConversation>(fp) {
			@Override
			protected void decorateShape(IAddContext context, ContainerShape containerShape, CallConversation businessObject) {
				super.decorateShape(context, containerShape, businessObject);
				Shape hexShape = containerShape.getChildren().get(0);
				hexShape.getGraphicsAlgorithm().setLineWidth(4);
			}

			@Override
			public Class getBusinessObjectType() {
				return CallConversation.class;
			}
		};
	}

	@Override
	public IUpdateFeature getUpdateFeature(IFeatureProvider fp) {
		MultiUpdateFeature multiUpdate = new MultiUpdateFeature(fp);
		multiUpdate.addFeature(new UpdateLabelFeature(fp) {

			@Override
			protected LabelPosition getLabelPosition(AbstractText text) {
				return LabelPosition.SOUTH;
			}
		});
		return multiUpdate;
	}

	@Override
	public ILayoutFeature getLayoutFeature(IFeatureProvider fp) {
		return null;
	}

	@Override
	public IMoveShapeFeature getMoveFeature(IFeatureProvider fp) {
		return new DefaultMoveBPMNShapeFeature(fp);
	}

	@Override
	public IResizeShapeFeature getResizeFeature(IFeatureProvider fp) {
		return new DefaultResizeShapeFeature(fp) {
			@Override
			public boolean canResizeShape(IResizeShapeContext context) {
				return false;
			}
		};
	}

	@Override
	public IDeleteFeature getDeleteFeature(IFeatureProvider fp) {
		return new AbstractDefaultDeleteFeature(fp);
	}
}