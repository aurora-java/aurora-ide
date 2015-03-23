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
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.features.choreography.ChoreographyUtil;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.core.utils.StyleUtil;
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
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
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
public class PushdownFeature extends AbstractCustomFeature {

	protected String description;
	
	/**
	 * @param fp
	 */
	public PushdownFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
	public String getName() {
	    return Messages.PushdownFeature_Name;
	}
	
	@Override
	public String getDescription() {
		if (description==null)
			description = Messages.PushdownFeature_Description;
		return description;
	}

	@Override
	public String getImageId() {
		return ImageProvider.IMG_16_PUSHDOWN;
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
			description = NLS.bind(Messages.PushdownFeature_Description_1,ModelUtil.getLabel(bo));
			
			if (bo instanceof Participant) {
				bo = ((Participant)bo).getProcessRef();
			}
			if (bo instanceof FlowElementsContainer) {
				return DIUtils.findBPMNDiagram((BaseElement)bo) == null;
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
		Definitions definitions = ModelUtil.getDefinitions(container);
		
		BPMNDiagram oldBpmnDiagram = DIUtils.getBPMNDiagram(bpmnShape);
		Diagram oldDiagram = DIUtils.findDiagram(getDiagramBehavior(), oldBpmnDiagram);
		
		// the contents of this expandable element is in the flowElements list 
        BPMNDiagram newBpmnDiagram = DIUtils.createBPMNDiagram(definitions, container);
		BPMNPlane newPlane = newBpmnDiagram.getPlane();

		Diagram newDiagram = DIUtils.getOrCreateDiagram(getDiagramBehavior(), newBpmnDiagram);
		ILocation loc = Graphiti.getLayoutService().getLocationRelativeToDiagram(shape);
		List <EObject> moved = new ArrayList<EObject>();
		
		for (FlowElement fe : container.getFlowElements()) {
			DiagramElement de = DIUtils.findDiagramElement(fe);
			if (de==null)
				continue; // Diagram Element does not exist
			
			newPlane.getPlaneElement().add(de);
			
			List <PictogramElement> pes = Graphiti.getLinkService().getPictogramElements(oldDiagram, fe);
			for (PictogramElement pe : pes) {
				PictogramElement pictogramElement = null;
				if (pe instanceof ConnectionDecorator) {
					// this will be moved as part of the connection
					continue;
				}
				else if (pe instanceof Shape) {
					if (BusinessObjectUtil.getFirstElementOfType(pe, BPMNShape.class)!=null) {
						newDiagram.getChildren().add((Shape)pe);
						pictogramElement = pe;
					}
					else if (FeatureSupport.isLabelShape(pe)) {
						newDiagram.getChildren().add((Shape)pe);
						pictogramElement = pe;
					}
				}
				else if (pe instanceof Connection) {
					if (BusinessObjectUtil.getFirstElementOfType(pe, BPMNEdge.class)!=null) {
						newDiagram.getConnections().add((Connection)pe);
						pictogramElement = pe;
						if (pe instanceof FreeFormConnection) {
							// adjust connection bendpoints
							FreeFormConnection ffc = (FreeFormConnection)pe;
							for (Point p : ffc.getBendpoints()) {
								p.setX( p.getX() - loc.getX() );
								p.setY( p.getY() - loc.getY() );
							}
						}
					}
				}
				if (pictogramElement!=null) {
					TreeIterator<EObject> iter = pictogramElement.eAllContents();
					while (iter.hasNext()) {
						EObject o = iter.next();
						if (o instanceof PictogramLink) {
							newDiagram.getPictogramLinks().add((PictogramLink)o);
							moved.add(o);
						}
//						else if (o instanceof Color) {
//							newDiagram.getColors().add((Color)o);
//							moved.add(o);
//						}
//						else if (o instanceof Font) {
//							newDiagram.getFonts().add((Font)o);
//							moved.add(o);
//						}
//						else if (o instanceof Style) {
//							newDiagram.getStyles().add((Style)o);
//							moved.add(o);
//						}
					}
				}
			}
		}
		oldDiagram.getPictogramLinks().removeAll(moved);
//		oldDiagram.getColors().removeAll(moved);
//		oldDiagram.getFonts().removeAll(moved);
//		oldDiagram.getStyles().removeAll(moved);

		// collapse the sub process
		if (FeatureSupport.isExpandableElement(container)) {
			bpmnShape.setIsExpanded(true);
			CollapseFlowNodeFeature collapseFeature = new CollapseFlowNodeFeature(getFeatureProvider());
			collapseFeature.execute(context);
		}
	}
}
