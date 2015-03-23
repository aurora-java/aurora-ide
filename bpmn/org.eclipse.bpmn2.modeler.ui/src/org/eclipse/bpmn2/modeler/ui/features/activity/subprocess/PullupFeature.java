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
 * @author Bob Brodt
 ******************************************************************************/

package org.eclipse.bpmn2.modeler.ui.features.activity.subprocess;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.features.choreography.ChoreographyUtil;
import org.eclipse.bpmn2.modeler.core.model.ModelHandler;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.ui.ImageProvider;
import org.eclipse.dd.di.DiagramElement;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.PictogramLink;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.osgi.util.NLS;

/**
 * @author Bob Brodt
 *
 */
public class PullupFeature extends AbstractCustomFeature {

	protected String description;

	/**
	 * @param fp
	 */
	public PullupFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
	public String getName() {
	    return Messages.PullupFeature_Name;
	}
	
	@Override
	public String getDescription() {
		if (description==null)
			description = Messages.PullupFeature_Description;
		return description;
	}

	@Override
	public String getImageId() {
		return ImageProvider.IMG_16_PULLUP;
	}

	@Override
	public boolean isAvailable(IContext context) {
		if (context instanceof ICustomContext) {
			PictogramElement[] pes = ((ICustomContext)context).getPictogramElements();
			if (pes != null && pes.length == 1) {
				PictogramElement pe = pes[0];
				if (!ChoreographyUtil.isChoreographyParticipantBand(pe)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			PictogramElement pe = pes[0];
			if (ChoreographyUtil.isChoreographyParticipantBand(pe))
				return false;
			Object bo = getBusinessObjectForPictogramElement(pe);
			description = NLS.bind(
				Messages.PullupFeature_Description_1,
				ModelUtil.getLabel(bo)
			);
			if (bo instanceof Participant) {
				bo = ((Participant)bo).getProcessRef();
			}
			if (bo instanceof FlowElementsContainer) {
				BPMNDiagram bpmnDiagram = DIUtils.findBPMNDiagram((BaseElement)bo);
				return bpmnDiagram != null;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public void execute(ICustomContext context) {
		// we already know there's one and only one PE element in canExecute() and that it's
		// a ContainerShape for an expandable activity
		ContainerShape shape = (ContainerShape)context.getPictogramElements()[0];
		Object bo = getBusinessObjectForPictogramElement(shape);

		BPMNShape bpmnShape = null;
		if (bo instanceof Participant) {
			bpmnShape = DIUtils.findBPMNShape((Participant)bo);
			bo = ((Participant)bo).getProcessRef();
		}
		else if (bo instanceof FlowElementsContainer) {
			bpmnShape = DIUtils.findBPMNShape((FlowElementsContainer)bo);
		}
		FlowElementsContainer container = (FlowElementsContainer)bo;
		
		// find out which BPMNPlane this sub process lives in - this will be the new home
		// for the DI elements in the existing BPMNDiagram.
		BPMNDiagram newBpmnDiagram = DIUtils.getBPMNDiagram(bpmnShape);
		BPMNPlane newPlane = newBpmnDiagram.getPlane();
		Diagram newDiagram = DIUtils.findDiagram(getDiagramBehavior(), newBpmnDiagram);
		
		BPMNDiagram oldBpmnDiagram = DIUtils.findBPMNDiagram(container);
		BPMNPlane oldPlane = oldBpmnDiagram.getPlane();
		Diagram oldDiagram = DIUtils.findDiagram(getDiagramBehavior(), oldBpmnDiagram);
		
		// copy the elements into the same plane as the sub process
		while (oldPlane.getPlaneElement().size()>0) {
			DiagramElement de = oldPlane.getPlaneElement().get(0);
			newPlane.getPlaneElement().add(de);
		}
		
		// copy the Graphiti diagram elements
		ILocation loc = Graphiti.getLayoutService().getLocationRelativeToDiagram(shape);
		shape.getChildren().addAll( oldDiagram.getChildren() );
		for (Connection c : oldDiagram.getConnections()) {
			if (c instanceof FreeFormConnection) {
				// adjust connection bendpoints
				FreeFormConnection ffc = (FreeFormConnection)c;
				for (Point pp : ffc.getBendpoints()) {
					pp.setX( pp.getX() + loc.getX() );
					pp.setY( pp.getY() + loc.getY() );
				}
			}
		}
		newDiagram.getConnections().addAll( oldDiagram.getConnections() );
		
		newDiagram.getPictogramLinks().addAll(oldDiagram.getPictogramLinks());
		newDiagram.getColors().addAll(oldDiagram.getColors());
		newDiagram.getFonts().addAll(oldDiagram.getFonts());
		newDiagram.getStyles().addAll(oldDiagram.getStyles());
		
		// get rid of the old BPMNDiagram
		DIUtils.deleteDiagram(getDiagramBehavior(), oldBpmnDiagram);
		
		// expand the sub process
		if (FeatureSupport.isExpandableElement(container)) {
			bpmnShape.setIsExpanded(false);
			ExpandFlowNodeFeature expandFeature = new ExpandFlowNodeFeature(getFeatureProvider());
			expandFeature.execute(context);
		}
	}
}
