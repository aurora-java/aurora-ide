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
package org.eclipse.bpmn2.modeler.core.features.containers.participant;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2AddFeature;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.features.label.AddShapeLabelFeature;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.core.utils.StyleUtil;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.algorithms.AbstractText;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IPeCreateService;

public class AddParticipantFeature extends AbstractBpmn2AddFeature<Participant> {

	public AddParticipantFeature(IFeatureProvider fp) {
		super(fp);
	}

	public IAddFeature getAddLabelFeature(IFeatureProvider fp) {
		return new AddShapeLabelFeature(fp) {
			
			@Override
			protected AbstractText createText(Shape labelShape, String labelText) {
				// need to override the default MultiText created by super
				// because the Graphiti layout algorithm doesn't work as
				// expected when text angle is -90
				return gaService.createText(labelShape, labelText);
			}

			@Override
			public void applyStyle(AbstractText text, BaseElement be) {
				super.applyStyle(text, be);
				text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
				text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
			}
		};
	}

	@Override
	public boolean canAdd(IAddContext context) {
		boolean isParticipant = getBusinessObject(context) instanceof Participant;
		boolean addToDiagram = context.getTargetContainer() instanceof Diagram;
		return isParticipant && addToDiagram;
	}

	@Override
	public PictogramElement add(IAddContext context) {
		Participant businessObject = getBusinessObject(context);
 
		Diagram targetDiagram = (Diagram) context.getTargetContainer();
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		ContainerShape containerShape = peCreateService.createContainerShape(targetDiagram, true);

		int width = this.getWidth(context);
		int height = this.getHeight(context);

		Rectangle rect = gaService.createRectangle(containerShape);
		StyleUtil.applyStyle(rect, businessObject);
		gaService.setLocationAndSize(rect, context.getX(), context.getY(), width, height);

		boolean isImport = context.getProperty(GraphitiConstants.IMPORT_PROPERTY) != null;
		BPMNShape bpmnShape = createDIShape(containerShape, businessObject, !isImport);
		boolean horz = bpmnShape.isIsHorizontal();
		Object copiedBpmnShape = context.getProperty(GraphitiConstants.COPIED_BPMN_SHAPE);
		if (copiedBpmnShape instanceof BPMNShape) {
			horz = ((BPMNShape) copiedBpmnShape).isIsHorizontal();
		}
		FeatureSupport.setHorizontal(containerShape, horz);

		Shape lineShape = peCreateService.createShape(containerShape, false);
		Polyline line;
		if (horz)
			line = gaService.createPolyline(lineShape, new int[] { 30, 0, 30, height });
		else
			line = gaService.createPolyline(lineShape, new int[] { 0, 30, width, 30 });
		StyleUtil.applyStyle(line, businessObject);

		// the decorator for Participant Multiplicity will be added by the update feature
		// if necessary. Set this property to "false" here, to force an update.
		peService.setPropertyValue(containerShape, GraphitiConstants.MULTIPLICITY, Boolean.toString(false));
		
		decorateShape(context, containerShape, businessObject);
		
		peCreateService.createChopboxAnchor(containerShape);
		AnchorUtil.addFixedPointAnchors(containerShape, rect);

		return containerShape;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2AddFeature#decorateShape(org.eclipse.graphiti.features.context.IAddContext, org.eclipse.graphiti.mm.pictograms.ContainerShape, org.eclipse.bpmn2.BaseElement)
	 */
	protected void decorateShape(IAddContext context, ContainerShape containerShape, Participant businessObject) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2AddFeature#getBusinessObjectType()
	 */
	@Override
	public Class getBusinessObjectType() {
		return Participant.class;
	}
}
