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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.ChoreographyActivity;
import org.eclipse.bpmn2.ChoreographyTask;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2AddFeature;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.features.choreography.ChoreographyUtil;
import org.eclipse.bpmn2.modeler.core.features.label.AddShapeLabelFeature;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil;
import org.eclipse.bpmn2.modeler.core.utils.ShapeDecoratorUtil;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.core.utils.StyleUtil;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeService;

public abstract class AddChoreographyActivityFeature<T extends ChoreographyActivity>
	extends AbstractBpmn2AddFeature<T> {

	protected final IGaService gaService = Graphiti.getGaService();
	protected final IPeService peService = Graphiti.getPeService();

	public AddChoreographyActivityFeature(IFeatureProvider fp) {
		super(fp);
	}

	public IAddFeature getAddLabelFeature(IFeatureProvider fp) {
		return new AddShapeLabelFeature(fp);
	}

	@Override
	public boolean canAdd(IAddContext context) {
		return context.getTargetContainer().equals(getDiagram());
	}

	@Override
	public PictogramElement add(IAddContext context) {
		T businessObject = getBusinessObject(context);

		int x = context.getX();
		int y = context.getY();
		int width = this.getWidth(context);
		int height = this.getHeight(context);

		ContainerShape containerShape = peService.createContainerShape(context.getTargetContainer(), true);
		Rectangle invisibleRect = gaService.createInvisibleRectangle(containerShape);
		gaService.setLocationAndSize(invisibleRect, x, y, width, height);
		
		Shape rectShape = peService.createShape(containerShape, false);
		RoundedRectangle rect = gaService.createRoundedRectangle(rectShape, 5, 5);
		StyleUtil.applyStyle(rect, businessObject);
		gaService.setLocationAndSize(rect, 0, 0, width, height);
		link(rectShape, businessObject);

		boolean isImport = context.getProperty(GraphitiConstants.IMPORT_PROPERTY) != null;
		if (isImport) {
			List<Participant> participants = businessObject.getParticipantRefs();
			List<BPMNShape> allShapes = ModelUtil.getAllObjectsOfType(businessObject.eResource(), BPMNShape.class);
			List<BPMNShape> participantBandShapes = new ArrayList<BPMNShape>();
			BPMNShape choreoBpmnShape = null;

			for (BPMNShape bpmnShape : allShapes) {
				if (businessObject.equals(bpmnShape.getBpmnElement())) {
					choreoBpmnShape = bpmnShape;
					break;
				}
			}

			for (BPMNShape bpmnShape : allShapes) {
				if (participants.contains(bpmnShape.getBpmnElement())
						&& choreoBpmnShape.equals(bpmnShape.getChoreographyActivityShape())) {
					participantBandShapes.add(bpmnShape);
				}
			}
		}

		ShapeDecoratorUtil.hideActivityMarker(containerShape, GraphitiConstants.ACTIVITY_MARKER_EXPAND);

		if (businessObject instanceof ChoreographyTask) {
			peService.setPropertyValue(containerShape, ChoreographyUtil.MESSAGE_REF_IDS,
					ChoreographyUtil.getMessageRefIds((ChoreographyTask) businessObject));
		}

		peService.createChopboxAnchor(containerShape);
		createDIShape(containerShape, businessObject, !isImport);
		
		decorateShape(context, containerShape, businessObject);
		
		AnchorUtil.addFixedPointAnchors(containerShape, rect);

		return containerShape;
	}
}