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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.ChoreographyActivity;
import org.eclipse.bpmn2.ChoreographyTask;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.MessageFlow;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil.AnchorLocation;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil.BoundaryAnchor;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.context.impl.LayoutContext;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.PropertyContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeService;

/**
 * FIXME: Clean this mess up. These should be in their appropriate Features, not a utility class.
 */
public class ChoreographyUtil implements ChoreographyProperties {

	public final static int ENV_W = 30;
	public final static int ENV_H = 18;
	public final static int ENVELOPE_HEIGHT_MODIFIER = 30;
	public final static String PARTICIPANT_REF_IDS = "choreography.activity.participant.ref.ids"; //$NON-NLS-1$
	public final static String INITIATING_PARTICIPANT_REF = "choreography.activity.initiating.participant.ref"; //$NON-NLS-1$
	public final static String MESSAGE_VISIBLE = "choreography.activity.band.message.visible"; //$NON-NLS-1$
	public final static String PARTICIPANT_BAND = "choreography.activity.band"; //$NON-NLS-1$
	public final static String MESSAGE_LINK = "choreography.messageLink"; //$NON-NLS-1$
	public final static String MESSAGE_NAME = "choreography.messageName"; //$NON-NLS-1$
	public final static String MESSAGE_REF_IDS = "choreography.message.ref.ids"; //$NON-NLS-1$

	private static IGaService gaService = Graphiti.getGaService();
	private static IPeService peService = Graphiti.getPeService();

	public static List<BPMNShape> getParticipantBandBpmnShapes(ContainerShape choreographyActivityShape) {
		List<BPMNShape> bpmnShapes = new ArrayList<BPMNShape>();
		List<ContainerShape> containers = FeatureSupport.getParticipantBandContainerShapes(choreographyActivityShape);
		for (ContainerShape container : containers) {
			BPMNShape bpmnShape = BusinessObjectUtil.getFirstElementOfType(container, BPMNShape.class);
			bpmnShapes.add(bpmnShape);
		}
		return bpmnShapes;
	}

	/**
	 * Get the Choreography Activity shape that either owns, or is, the given
	 * PictogramElement.
	 * 
	 * @param pe a PictogramElement that must be either the Choreography
	 *            Activity shape, or a Participant Band of the Choreography
	 *            Activity shape.
	 * @return a ContainerShape for the Choreography Activity or null if the
	 *         given PictogramElement is not contained in a Choreography
	 *         Activity shape.
	 */
	public static ContainerShape getChoreographyActivityShape(PictogramElement pe) {
		if (isChoreographyParticipantBand(pe))
			return (ContainerShape) pe.eContainer();
		if (isChoreographyActivity(pe))
			return (ContainerShape) pe;
		return null;
	}

	/**
	 * Check if the given PictogramElement is a Choreography Participant Band
	 * shape.
	 * 
	 * @param pe a PictogramElement
	 * @return true if the PictogramElement is a Participant Band, false
	 *         otherwise.
	 */
	public static boolean isChoreographyParticipantBand(PictogramElement pe) {
		if (pe instanceof ContainerShape) {
			return isChoreographyActivity((PictogramElement)pe.eContainer());
		}
		return false;
	}
	
	/**
	 * Check if the given PictogramElement is a Choreography Activity shape.
	 * 
	 * @param pe a PictogramElement
	 * @return true if the PictogramElement is a Choreography Activity, false
	 *         otherwise.
	 */
	public static boolean isChoreographyActivity(PictogramElement pe) {
		if (pe instanceof ContainerShape) {
			Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
			if (bo instanceof ChoreographyActivity) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check if the given PictogramElement is a Choreography Task Message Link.
	 * 
	 * @param pe a PictogramElement
	 * @return true if the PictogramElement is a Message Link, false otherwise.
	 */
	public static boolean isChoreographyMessageLink(PictogramElement pe) {
		EObject o = BusinessObjectUtil.getFirstElementOfType(pe, BaseElement.class);
		if (o instanceof MessageFlow && pe instanceof Connection) {
			Connection c = (Connection)pe;
			if (c.getStart()!=null && peService.getPropertyValue(c.getStart().getParent(),ChoreographyUtil.MESSAGE_LINK) != null)
				return true;
			if (c.getEnd()!=null && peService.getPropertyValue(c.getEnd().getParent(),ChoreographyUtil.MESSAGE_LINK) != null)
				return true;
		}
		return false;
	}

	/**
	 * Check if the given PictogramElement is a Choreography Task Message.
	 * 
	 * @param pe a PictogramElement
	 * @return true if the PictogramElement is a Message, false otherwise.
	 */
	public static boolean isChoreographyMessage(PictogramElement pe) {
		EObject o = BusinessObjectUtil.getFirstElementOfType(pe, BaseElement.class);
		if (o instanceof Message && pe instanceof ContainerShape) {
			if (peService.getPropertyValue(pe,ChoreographyUtil.MESSAGE_LINK) != null)
				return true;
		}
		return false;
	}
	
	public static boolean removeChoreographyMessageLink(PictogramElement pe) {
		if (isChoreographyMessageLink(pe)) {
			Connection connection = (Connection)pe;
			// remove the Message figure
			peService.deletePictogramElement( connection.getEnd().getParent() );
			// remove the connection
			peService.deletePictogramElement(connection);
			return true;
		}
		return false;
	}
	
	public static String getMessageRefIds(ContainerShape choreographyTaskShape) {
		String property = peService.getPropertyValue(choreographyTaskShape, MESSAGE_REF_IDS);
		if (property == null) {
			return new String(); // return empty string
		}
		return property;
	}
	
	public static String getMessageRefIds(ChoreographyTask choreographyTask) {
		if (choreographyTask.getMessageFlowRef() == null) {
			return new String();
		}
		Iterator<MessageFlow> iterator = choreographyTask.getMessageFlowRef().iterator();
		StringBuilder sb = new StringBuilder();
		while (iterator.hasNext()) {
			MessageFlow message = iterator.next();
			sb.append(message.getId());
			if (iterator.hasNext()) {
				sb.append(':');
			}
		}
		return sb.toString();
	}
	
	public static String getMessageFlowName(MessageFlow flow) {
		if (flow.getMessageRef() == null) {
			return flow.getName();
		} else if (flow.getMessageRef().getItemRef()==null ||
				flow.getMessageRef().getItemRef().getStructureRef()==null) {
			return flow.getMessageRef().getName();
		} else {
			String messageName = flow.getMessageRef().getName();
			String itemDefinitionName = ExtendedPropertiesProvider.getTextValue(flow.getMessageRef().getItemRef());
			String text = itemDefinitionName;
			if (messageName!=null && !messageName.isEmpty())
				text += "/" + messageName; //$NON-NLS-1$
			text = messageName;
			return text;
		}
	}
	
	public static String getMessageName(Message mesg) {
		if (mesg.getItemRef()==null ||
				mesg.getItemRef().getStructureRef()==null) {
			if (mesg.getName()==null)
				return mesg.getId();
			return mesg.getName();
		} else {
			String type = "(" + ExtendedPropertiesProvider.getTextValue(mesg.getItemRef()) +")"; //$NON-NLS-1$ //$NON-NLS-2$
			if (mesg.getName()==null)
				return type; 
			return mesg.getName() + type;
		}
	}
	
	public static void updateChoreographyMessageLinks(IFeatureProvider fp, PictogramElement pe) {
		if (pe instanceof ContainerShape) {
			ContainerShape choreographyTaskShape = (ContainerShape) pe;
			BPMNShape bpmnShape = BusinessObjectUtil.getFirstElementOfType(choreographyTaskShape, BPMNShape.class);
			Bounds bounds = bpmnShape.getBounds();
			int x = (int) ((bounds.getX() + bounds.getWidth() / 2) - (ChoreographyUtil.ENV_W / 2));
	
			Map<AnchorLocation, BoundaryAnchor> boundaryAnchors = AnchorUtil.getBoundaryAnchors(choreographyTaskShape);
			BoundaryAnchor topBoundaryAnchor = boundaryAnchors.get(AnchorLocation.TOP);
			BoundaryAnchor bottomBoundaryAnchor = boundaryAnchors.get(AnchorLocation.BOTTOM);
	
			for (Connection connection : topBoundaryAnchor.anchor.getOutgoingConnections()) {
				EObject container = connection.getEnd().eContainer();
				if (container instanceof PropertyContainer) {
					String property = peService.getPropertyValue((PropertyContainer) container, ChoreographyUtil.MESSAGE_LINK);
					if (property != null && new Boolean(property)) {
						int y = (int) (bounds.getY() - ChoreographyUtil.ENVELOPE_HEIGHT_MODIFIER - ChoreographyUtil.ENV_H);
						gaService.setLocation(((ContainerShape) container).getGraphicsAlgorithm(), x, y);
						break;
					}
				}
			}
	
			for (Connection connection : bottomBoundaryAnchor.anchor.getOutgoingConnections()) {
				EObject container = connection.getEnd().eContainer();
				if (container instanceof PropertyContainer) {
					String property = peService.getPropertyValue((PropertyContainer) container, ChoreographyUtil.MESSAGE_LINK);
					if (property != null && new Boolean(property)) {
						int y = (int) (bounds.getY() + bounds.getHeight() + ChoreographyUtil.ENVELOPE_HEIGHT_MODIFIER);
						gaService.setLocation(((ContainerShape) container).getGraphicsAlgorithm(), x, y);
						break;
					}
				}
			}
		}
	}

	/**
	 * Update the Choreography Activity Participant Bands and force a layout
	 * of the Choreography Activity such that its Label and any Loop markers
	 * are correctly placed inside the Choreography Activity shape
	 * 
	 * @param fp the Feature Provider
	 * @param pe the Choreography Activity Container Shape
	 */
	public static void updateParticipantBands(IFeatureProvider fp, PictogramElement pe) {
		IUpdateContext updateContext = new UpdateContext(pe);
		updateContext.putProperty(GraphitiConstants.FORCE_UPDATE_ALL, Boolean.TRUE);
		IUpdateFeature updateFeature = fp.getUpdateFeature(updateContext); //new UpdateChoreographyParticipantRefsFeature(fp);
		updateFeature.update(updateContext);

		ILayoutContext layoutContext = new LayoutContext(pe);
		ILayoutFeature layoutFeature = fp.getLayoutFeature(layoutContext);
		layoutFeature.layout(layoutContext);
	}

	public static String getParticipantRefIds(ChoreographyActivity choreographyActivity) {
		if (choreographyActivity.getParticipantRefs() == null) {
			return new String();
		}
		Iterator<Participant> iterator = choreographyActivity.getParticipantRefs().iterator();
		StringBuilder sb = new StringBuilder();
		while (iterator.hasNext()) {
			Participant participant = iterator.next();
			sb.append(participant.getId());
			boolean multiple = participant.getParticipantMultiplicity() != null
					&& participant.getParticipantMultiplicity().getMaximum() > 1;
			if (multiple)
				sb.append('*');
			if (iterator.hasNext()) {
				sb.append(':');
			}
		}
		return sb.toString();
	}

	public static String getParticipantRefIds(ContainerShape choreographyActivityShape) {
		String property = peService.getPropertyValue(choreographyActivityShape, PARTICIPANT_REF_IDS);
		if (property == null) {
			return new String(); // return empty string
		}
		return property;
	}
}
