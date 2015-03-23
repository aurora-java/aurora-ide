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
package org.eclipse.bpmn2.modeler.core.features.choreography;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.bpmn2.CallChoreography;
import org.eclipse.bpmn2.ChoreographyActivity;
import org.eclipse.bpmn2.ChoreographyTask;
import org.eclipse.bpmn2.InteractionNode;
import org.eclipse.bpmn2.MessageFlow;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.di.ParticipantBandKind;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2UpdateFeature;
import org.eclipse.bpmn2.modeler.core.features.label.AddShapeLabelFeature;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.core.utils.StyleUtil;
import org.eclipse.bpmn2.modeler.core.utils.Tuple;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.di.DiagramElement;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.AreaContext;
import org.eclipse.graphiti.features.context.impl.LayoutContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeService;
import org.eclipse.graphiti.util.IColorConstant;

public class UpdateChoreographyParticipantRefsFeature extends AbstractBpmn2UpdateFeature {

	private final static IPeService peService = Graphiti.getPeService();
	private final static IGaService gaService = Graphiti.getGaService();
	private final static int CORNER_RADIUS = 5;
	
	private ContainerShape choreographyActivityShape;
	private ChoreographyActivity choreographyActivity;

	public UpdateChoreographyParticipantRefsFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canUpdate(IUpdateContext context) {
		return ChoreographyUtil.getChoreographyActivityShape(context.getPictogramElement()) != null;
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {
		choreographyActivityShape = ChoreographyUtil.getChoreographyActivityShape(context.getPictogramElement());
		if (choreographyActivityShape!=null) {
			choreographyActivity= BusinessObjectUtil.getFirstElementOfType(choreographyActivityShape, ChoreographyActivity.class);
			String shapeIds = ChoreographyUtil.getParticipantRefIds(choreographyActivityShape);
			String activityIds = ChoreographyUtil.getParticipantRefIds(choreographyActivity);
			if (!shapeIds.equals(activityIds))
				return Reason.createTrueReason("Participants");
		}
		return Reason.createFalseReason();
	}

	@Override
	public boolean update(IUpdateContext context) {
		choreographyActivityShape = ChoreographyUtil.getChoreographyActivityShape(context.getPictogramElement());
		choreographyActivity = BusinessObjectUtil.getFirstElementOfType(choreographyActivityShape, ChoreographyActivity.class);
		List<Participant> participants = choreographyActivity.getParticipantRefs();
		List<ContainerShape> bandShapes = FeatureSupport.getParticipantBandContainerShapes(choreographyActivityShape);

		updateParticipantReferences(bandShapes, participants);

		peService.setPropertyValue(choreographyActivityShape, ChoreographyUtil.PARTICIPANT_REF_IDS, ChoreographyUtil.getParticipantRefIds(choreographyActivity));
		
		Participant initiatingParticipant = choreographyActivity.getInitiatingParticipantRef();
		String id = initiatingParticipant == null ? "null" : initiatingParticipant.getId(); //$NON-NLS-1$
		peService.setPropertyValue(choreographyActivityShape, ChoreographyUtil.INITIATING_PARTICIPANT_REF, id);

//		ChoreographyUtil.drawMessageLinks(getFeatureProvider(), choreographyActivityShape);
		
		ILayoutContext layoutContext = new LayoutContext(choreographyActivityShape);
		LayoutChoreographyFeature feature = new LayoutChoreographyFeature(getFeatureProvider());
		feature.layout(layoutContext);
		
		return true;
	}

	private void updateParticipantReferences(List<ContainerShape> currentParticipantContainers, List<Participant> newParticipants) {

		Diagram diagram = peService.getDiagramForShape(choreographyActivityShape);

		BPMNDiagram dia = BusinessObjectUtil.getFirstElementOfType(diagram, BPMNDiagram.class);
		List<DiagramElement> diElements = dia.getPlane().getPlaneElement();
		for (int i = 0; i < currentParticipantContainers.size(); i++) {
			ContainerShape container = currentParticipantContainers.get(i);
			for (Connection c : peService.getOutgoingConnections(container)) {
				AnchorContainer parent = c.getEnd().getParent();
				Shape labelShape = FeatureSupport.getLabelShape(parent);
				if (labelShape!=null)
					peService.deletePictogramElement(labelShape);
				peService.deletePictogramElement(parent);
			}
			BPMNShape bpmnShape = BusinessObjectUtil.getFirstElementOfType(container, BPMNShape.class);
			diElements.remove(bpmnShape);
			Shape labelShape = FeatureSupport.getLabelShape(container);
			if (labelShape!=null)
				peService.deletePictogramElement(labelShape);
			peService.deletePictogramElement(container);
		}

		GraphicsAlgorithm ga = choreographyActivityShape.getGraphicsAlgorithm();
		IDimension size = gaService.calculateSize(ga);

		List<ContainerShape> newContainers = new ArrayList<ContainerShape>();
		int y = 0;
		boolean first = true;

		List<InteractionNode> sources = new ArrayList<InteractionNode>();
		if (choreographyActivity instanceof ChoreographyTask) {
			for (MessageFlow message : ((ChoreographyTask) choreographyActivity).getMessageFlowRef()) {
				sources.add(message.getSourceRef());
			}
		}

		IFeatureProvider fp = getFeatureProvider();
		Iterator<Participant> iterator = newParticipants.iterator();
		while (iterator.hasNext()) {
			Participant participant = iterator.next();

			ContainerShape bandShape = peService.createContainerShape(choreographyActivityShape, true);

			ParticipantBandKind bandKind = getNewParticipantBandKind(choreographyActivity, participant, first,
					!iterator.hasNext());

			boolean multiple = participant.getParticipantMultiplicity() != null
					&& participant.getParticipantMultiplicity().getMaximum() > 1;

			int w = size.getWidth();
			int h = multiple ? 40 : 20;

			BPMNShape bpmnShape = DIUtils.createDIShape(bandShape, participant, 0, y + h, w, h, fp, diagram);
			bpmnShape.setChoreographyActivityShape(BusinessObjectUtil.getFirstElementOfType(choreographyActivityShape,
					BPMNShape.class));
			bpmnShape.setIsMarkerVisible(multiple);
			bpmnShape.setParticipantBandKind(bandKind);
			bpmnShape.setIsMessageVisible(sources.contains(participant));
			createParticipantBandContainerShape(bandKind, bandShape, bpmnShape);
			if (multiple) {
				drawMultiplicityMarkers(bandShape);
			}
			newContainers.add(bandShape);

			y += h;
			first = false;
		}

		Tuple<List<ContainerShape>, List<ContainerShape>> topAndBottom = FeatureSupport.getTopAndBottomBands(newContainers);
		resizeParticipantBandContainerShapes(size.getWidth(), size.getHeight(), topAndBottom.getFirst(),
				topAndBottom.getSecond());
	}

	private ParticipantBandKind getNewParticipantBandKind(ChoreographyActivity choreography,
			Participant participant, boolean first, boolean last) {
		boolean initiating = choreography.getInitiatingParticipantRef() != null
				&& choreography.getInitiatingParticipantRef().equals(participant);
		if (first) {
			return initiating ? ParticipantBandKind.TOP_INITIATING : ParticipantBandKind.TOP_NON_INITIATING;
		} else if (last) {
			return initiating ? ParticipantBandKind.BOTTOM_INITIATING : ParticipantBandKind.BOTTOM_NON_INITIATING;
		} else {
			return initiating ? ParticipantBandKind.MIDDLE_INITIATING : ParticipantBandKind.MIDDLE_NON_INITIATING;
		}
	}

	private ContainerShape createParticipantBandContainerShape(ParticipantBandKind bandKind, ContainerShape bandShape, BPMNShape bpmnShape) {

		switch (bandKind) {
		case TOP_INITIATING:
			return createTopShape(bandShape, bpmnShape, true);
		case TOP_NON_INITIATING:
			return createTopShape(bandShape, bpmnShape, false);
		case MIDDLE_INITIATING:
			return createMiddleShape(bandShape, bpmnShape, true);
		case MIDDLE_NON_INITIATING:
			return createMiddleShape(bandShape, bpmnShape, false);
		case BOTTOM_INITIATING:
			return createBottomShape(bandShape, bpmnShape, true);
		case BOTTOM_NON_INITIATING:
			return createBottomShape(bandShape, bpmnShape, false);
		}

		return bandShape;
	}

	private ContainerShape createTopShape(ContainerShape bandShape, BPMNShape bpmnShape, boolean initiating) {

		if (bandShape == null) {
			bandShape = peService.createContainerShape(choreographyActivityShape, true);
		}

		Bounds bounds = bpmnShape.getBounds();
		int w = (int) bounds.getWidth();
		int h = (int) bounds.getHeight();

		Diagram diagram = peService.getDiagramForPictogramElement(choreographyActivityShape);
		RoundedRectangle band = gaService.createRoundedRectangle(bandShape, CORNER_RADIUS, CORNER_RADIUS);
		StyleUtil.applyStyle(band, bpmnShape.getBpmnElement());
		band.setStyle(null);
		band.setBackground(initiating ? gaService.manageColor(diagram, IColorConstant.WHITE) : gaService.manageColor(
				diagram, IColorConstant.LIGHT_GRAY));
		gaService.setLocationAndSize(band, 0, 0, w, h);

		IFeatureProvider fp = getFeatureProvider();
		Participant participant = (Participant) bpmnShape.getBpmnElement();
		fp.link(bandShape, participant);
		fp.link(bandShape, bpmnShape);
		
		addBandLabel(bandShape, w, h);

		Graphiti.getPeCreateService().createChopboxAnchor(bandShape);
		AnchorUtil.addFixedPointAnchors(bandShape, band);
		peService.setPropertyValue(bandShape, ChoreographyUtil.PARTICIPANT_BAND, Boolean.toString(true));
		peService.setPropertyValue(bandShape, ChoreographyUtil.MESSAGE_VISIBLE,
				Boolean.toString(bpmnShape.isIsMessageVisible()));
		return bandShape;
	}

	private ContainerShape createBottomShape(ContainerShape bandShape, BPMNShape bpmnShape, boolean initiating) {

		if (bandShape == null) {
			bandShape = peService.createContainerShape(choreographyActivityShape, true);
		}

		Bounds bounds = bpmnShape.getBounds();
		int w = (int) bounds.getWidth();
		int h = (int) bounds.getHeight();

		ILocation parentLoc = peService.getLocationRelativeToDiagram(choreographyActivityShape);
		int y = (int) bounds.getY() - parentLoc.getY();

		Diagram diagram = peService.getDiagramForPictogramElement(choreographyActivityShape);
		RoundedRectangle band = gaService.createRoundedRectangle(bandShape, CORNER_RADIUS, CORNER_RADIUS);
		StyleUtil.applyStyle(band, bpmnShape.getBpmnElement());
		band.setStyle(null);
		band.setBackground(initiating ? gaService.manageColor(diagram, IColorConstant.WHITE) : gaService.manageColor(
				diagram, IColorConstant.LIGHT_GRAY));
		gaService.setLocationAndSize(band, 0, y, w, h);

		IFeatureProvider fp = getFeatureProvider();
		Participant participant = (Participant) bpmnShape.getBpmnElement();
		fp.link(bandShape, participant);
		fp.link(bandShape, bpmnShape);

		addBandLabel(bandShape, w, h);

		Graphiti.getPeCreateService().createChopboxAnchor(bandShape);
		AnchorUtil.addFixedPointAnchors(bandShape, band);
		peService.setPropertyValue(bandShape, ChoreographyUtil.PARTICIPANT_BAND, Boolean.toString(true));
		peService.setPropertyValue(bandShape, ChoreographyUtil.MESSAGE_VISIBLE,
				Boolean.toString(bpmnShape.isIsMessageVisible()));
		return bandShape;
	}

	private ContainerShape createMiddleShape(ContainerShape bandShape, BPMNShape bpmnShape, boolean initiating) {

		if (bandShape == null) {
			bandShape = peService.createContainerShape(choreographyActivityShape, true);
		}

		Bounds bounds = bpmnShape.getBounds();
		int w = (int) bounds.getWidth();
		int h = (int) bounds.getHeight();

		ILocation parentLoc = peService.getLocationRelativeToDiagram(choreographyActivityShape);
		int y = (int) bounds.getY() - parentLoc.getY();

		Diagram diagram = peService.getDiagramForPictogramElement(choreographyActivityShape);
		Rectangle band = gaService.createRectangle(bandShape);
		StyleUtil.applyStyle(band, bpmnShape.getBpmnElement());
		band.setStyle(null);
		band.setBackground(initiating ? gaService.manageColor(diagram, IColorConstant.WHITE) : gaService.manageColor(
				diagram, IColorConstant.LIGHT_GRAY));
		gaService.setLocationAndSize(band, 0, y, w, h);

		IFeatureProvider fp = getFeatureProvider();
		Participant participant = (Participant) bpmnShape.getBpmnElement();
		fp.link(bandShape, participant);
		fp.link(bandShape, bpmnShape);

		addBandLabel(bandShape, w, h);

		Graphiti.getPeCreateService().createChopboxAnchor(bandShape);
		AnchorUtil.addFixedPointAnchors(bandShape, band);
		peService.setPropertyValue(bandShape, ChoreographyUtil.PARTICIPANT_BAND, Boolean.toString(true));
		peService.setPropertyValue(bandShape, ChoreographyUtil.MESSAGE_VISIBLE,
				Boolean.toString(bpmnShape.isIsMessageVisible()));
		return bandShape;
	}

	private void addBandLabel(final ContainerShape bandShape, int w, int h) {
		Participant participant = (Participant) BusinessObjectUtil.getFirstBaseElement(bandShape);
		AreaContext ac = new AreaContext();
		ac.setHeight(h);
		ac.setWidth(w);
		AddContext context = new AddContext(ac, bandShape);
		context.setNewObject(participant);
		IAddFeature feature = new AddShapeLabelFeature(getFeatureProvider()) {
			@Override
			protected ContainerShape getTargetContainer(IAddContext context) {
				return bandShape;
			}
			
			@Override
			protected PictogramElement getLabelOwner(IAddContext context) {
				return bandShape;
			}
		};
		feature.add(context);
	}
	
	private void resizeParticipantBandContainerShapes(int w, int h, List<ContainerShape> top,
			List<ContainerShape> bottom) {

		boolean shrink = choreographyActivity instanceof CallChoreography;
		int y = shrink ? 2 : 0;
		int x = shrink ? 2 : 0;
		if (shrink) {
			w -= 4;
			h -= 2;
		}
		
		for (ContainerShape container : top) {
			BPMNShape bpmnShape = BusinessObjectUtil.getFirstElementOfType(container, BPMNShape.class);
			Bounds bounds = bpmnShape.getBounds();
			int hAcc = (int) bounds.getHeight();
			gaService.setLocationAndSize(container.getGraphicsAlgorithm(), x, y, w, hAcc);
			y += hAcc;
			resizeParticipantBandChildren(container, w);
			DIUtils.updateDIShape(container);
			AnchorUtil.relocateFixPointAnchors(container, w, (int) bounds.getHeight());
		}

		Collections.reverse(bottom); // start from bottom towards center
		y = h;
		for (ContainerShape container : bottom) {
			BPMNShape bpmnShape = BusinessObjectUtil.getFirstElementOfType(container, BPMNShape.class);
			Bounds bounds = bpmnShape.getBounds();
			y -= bounds.getHeight();
			gaService.setLocationAndSize(container.getGraphicsAlgorithm(), x, y, w, (int) bounds.getHeight());
			resizeParticipantBandChildren(container, w);
			DIUtils.updateDIShape(container);
			AnchorUtil.relocateFixPointAnchors(container, w, (int) bounds.getHeight());
		}
	}

	private void resizeParticipantBandChildren(ContainerShape container, int w) {
		for (Shape s : container.getChildren()) {
			GraphicsAlgorithm ga = s.getGraphicsAlgorithm();
			if (ga instanceof Text) {
				gaService.setSize(ga, w, ga.getHeight());
			} else if (ga instanceof Rectangle) {
				gaService.setLocation(ga, (w / 2) - (ga.getWidth() / 2), ga.getY());
			}
		}
	}

	private void drawMultiplicityMarkers(ContainerShape container) {
		Diagram diagram = peService.getDiagramForPictogramElement(container);
		Shape multiplicityShape = peService.createShape(container, false);
		Rectangle rect = gaService.createInvisibleRectangle(multiplicityShape);

		IDimension size = gaService.calculateSize(container.getGraphicsAlgorithm());
		int w = 10;
		int h = 10;
		int x = (size.getWidth() / 2) - (w / 2);
		int y = size.getHeight() - h - 1;
		gaService.setLocationAndSize(rect, x, y, w, h);

		int[][] coorinates = { new int[] { 0, 0, 0, h }, new int[] { 4, 0, 4, h }, new int[] { 8, 0, 8, h } };
		for (int[] xy : coorinates) {
			Polyline line = gaService.createPolyline(rect, xy);
			line.setLineWidth(2);
			line.setForeground(gaService.manageColor(diagram, StyleUtil.CLASS_FOREGROUND));
		}
	}
}