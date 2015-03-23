/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013 Red Hat, Inc.
 * All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.editor;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.MessageFlow;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.ui.features.flow.MessageFlowFeatureContainer;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IClippingStrategy;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

public class ConnectionLayerClippingStrategy implements IClippingStrategy {

	protected Diagram diagram;
	protected GraphicalViewer graphicalViewer;
	
	public static void applyTo(GraphicalViewer graphicalViewer) {
		ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) graphicalViewer.getRootEditPart();
		Figure connectionLayer = (Figure) rootEditPart.getLayer(LayerConstants.CONNECTION_LAYER);
		if (connectionLayer.getClippingStrategy()==null) {
			EditPart editPart = graphicalViewer.getContents();
			Diagram diagram = (Diagram)editPart.getModel();
			IClippingStrategy clippingStrategy = new ConnectionLayerClippingStrategy(graphicalViewer, diagram);
			connectionLayer.setClippingStrategy(clippingStrategy);
		}
	}

	public ConnectionLayerClippingStrategy(GraphicalViewer graphicalViewer, Diagram diagram) {
		this.diagram = diagram;
		this.graphicalViewer = graphicalViewer;
	}
	
	@Override
	public Rectangle[] getClip(IFigure childFigure) {
		try {
			for (Object value : graphicalViewer.getEditPartRegistry().values()) {
				GraphicalEditPart part = (GraphicalEditPart)value;
				if (part.getFigure() == childFigure) {
					Object model = part.getModel();
					if (model instanceof Connection) {
						Connection connection = (Connection)model;
						AnchorContainer source = connection.getStart().getParent();
						AnchorContainer target = connection.getEnd().getParent();
						if (source.eContainer() != target.eContainer()) {
							// don't clip the connection if source and target are not in the same container
							return new Rectangle[] {childFigure.getBounds()};
						}
						BaseElement businessObject = BusinessObjectUtil.getFirstBaseElement(connection);
						if (businessObject instanceof MessageFlow) {
							ContainerShape messageShape = MessageFlowFeatureContainer.findMessageShape(connection);
							if (messageShape!=null) {
								Rectangle inner = getClip(messageShape)[0];
								Rectangle outer = childFigure.getBounds();
								return getClip(outer,inner);
							}
						}
						else if (businessObject!=null) {
							EObject container = businessObject.eContainer();
							if (container instanceof SubProcess) {
								// don't clip if contents of SubProcess have been moved to a different
								// BPMNDiagram ("pushed down")
								BPMNEdge bpmnEdge = BusinessObjectUtil.getFirstElementOfType(connection, BPMNEdge.class);
								if (bpmnEdge!=null) {
									for (PictogramElement pe : Graphiti.getLinkService().getPictogramElements(diagram, container)) {
										if (pe instanceof ContainerShape) {
											BPMNShape bpmnShape = BusinessObjectUtil.getFirstElementOfType(pe, BPMNShape.class);
											if (bpmnShape!=null) {
												if (bpmnShape.eContainer()!=bpmnEdge.eContainer())
													continue;
											}
											// don't clip connection if the source or target is this SubProcess
											EObject sourceBo = BusinessObjectUtil.getFirstBaseElement(source);
											EObject targetBo = BusinessObjectUtil.getFirstBaseElement(target);
											if (sourceBo!=container && targetBo!=container)
												return getClip((ContainerShape)pe);
										}
									}
								}								
							}
						}
					}
				}
			}
		}
		catch (Exception ex) {
			// Ignore exceptions: this could happen if a source or target shape of a connection has already been removed
			// before the connection has been removed. It all depends on the order in which they were created.
		}
		return new Rectangle[] {childFigure.getBounds()};
	}
	
	private Rectangle[] getClip(Rectangle outer, Rectangle inner) {
		if (outer.width > inner.width) {
			if (outer.height > inner.height) {
				Rectangle[] clip = new Rectangle[4];
				clip[0] = new Rectangle(
						outer.x, outer.y,
						outer.width, inner.y - outer.y);
				clip[1] = new Rectangle(
						outer.x, inner.y,
						inner.x - outer.x,
						inner.height
						);
				clip[2] = new Rectangle(
						inner.x + inner.width, inner.y,
						(outer.x + outer.width) - (inner.x + inner.width),
						inner.height
						);
				clip[3] = new Rectangle(
						outer.x, inner.y + inner.height,
						outer.width, (outer.y + outer.height) - (inner.y + inner.height));
				
				return clip;
			}
		}
		return new Rectangle[] {outer};
	}
	
	private Rectangle[] getClip(ContainerShape pe) {
		ILocation loc = Graphiti.getPeService().getLocationRelativeToDiagram(pe);
		GraphicsAlgorithm ga = pe.getGraphicsAlgorithm();
		return new Rectangle[] { new Rectangle(loc.getX(), loc.getY(), ga.getWidth(), ga.getHeight()) };
	}
}
