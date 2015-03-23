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
package org.eclipse.bpmn2.modeler.core.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.features.context.impl.ResizeShapeContext;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.ILayoutService;
import org.eclipse.graphiti.ui.editor.DiagramEditor;

/**
 * 
 */
public class ShapeLayoutManager {

	private static final int HORZ_PADDING = 50;
	private static final int VERT_PADDING = 50;
	private DiagramEditor editor;
	private static final ILayoutService layoutService = Graphiti.getLayoutService();
	
	public ShapeLayoutManager(DiagramEditor editor) {
		this.editor = editor;
	}

	public void layout(BaseElement container) {
		layout( getContainerShape(container) );
		editor.selectPictogramElements(new PictogramElement[]{});
	}
	
	public void layout(ContainerShape container) {
		layout(container, 0);
	}
	
	private void layout(ContainerShape container, int level) {

		GraphicsUtil.dump(level, "layout", container); //$NON-NLS-1$
		if (container==null)
			return;
		
		// Collect all child shapes: this excludes any label shapes
		// (which also happen to be ContainerShape objects); we want ONLY the
		// graphical objects that have corresponding BPMNShape objects.
		List<ContainerShape> childShapes = new ArrayList<ContainerShape>();
		for (int i=0; i<container.getChildren().size(); ++i) {
			PictogramElement pe = container.getChildren().get(i);
			if (isChildShape(pe)) {
				ContainerShape childContainer = (ContainerShape)pe;
				boolean hasChildren = false;
				for (Shape shape : childContainer.getChildren()) {
					if (isChildShape(shape)) {
						hasChildren = true;
						break;
					}
				}
				if (hasChildren)
					layout(childContainer, level+1);
				// for some unknown reason, Diagram children are inserted
				// in reverse order by Graphiti
				if (container instanceof Diagram)
					childShapes.add(0,childContainer);
				else
					childShapes.add(childContainer);
			}
		}

		// layout child shapes from right to left;
		// shapes are sorted into bins according to the number of incoming
		// and ougtoing SequenceFlow connections:
		// 1. shapes that have only outgoing connections are added to the startShapes bin
		// 2. shapes with only incoming connections are tossed into the endShapes bin
		// 3. shapes with both incoming and outgoing connections are in the middleShapes bin
		// 4. shapes with no connections are in the unconnectShapes bin
		List<ContainerShape> startShapes = new ArrayList<ContainerShape>();
		List<ContainerShape> unconnectedShapes = new ArrayList<ContainerShape>();
		List<ContainerShape> middleShapes = new ArrayList<ContainerShape>();
		List<ContainerShape> endShapes = new ArrayList<ContainerShape>();
		for (ContainerShape child : childShapes) {
			if (!child.isActive())
				continue;
			
			BaseElement be = BusinessObjectUtil.getFirstBaseElement(child);
			if (be instanceof Participant && ModelUtil.isParticipantBand((Participant)be))
				continue;

			List<SequenceFlow> incomingFlows = getIncomingSequenceFlows(child);
			List<SequenceFlow> outgoingFlows = getOutgoingSequenceFlows(child);
			int incomingCount = 0;
			int outgoingCount = 0;

			// this may be a start or end shape depending on whether ALL of the incoming
			// our outgoing flows are from/to shapes that are in this container.
			for (SequenceFlow sf : incomingFlows) {
				ContainerShape shape = getContainerShape(sf.getSourceRef());
				if (childShapes.contains(shape) && shape!=child)
					++incomingCount;
			}
			for (SequenceFlow sf : outgoingFlows) {
				ContainerShape shape = getContainerShape(sf.getTargetRef());
				if (childShapes.contains(shape) && shape!=child)
					++outgoingCount;
			}

			if (incomingCount==0) {
				if (outgoingCount==0)
					unconnectedShapes.add(child);
				else
					startShapes.add(child);
			}
			else if (outgoingCount==0) {
				endShapes.add(child);
			}
			else {
				middleShapes.add(child);
			}
		}
		
		// now build threads of sequence flows starting with all of the startShapes
		List<List<ContainerShape[]>> threads = new ArrayList<List<ContainerShape[]>>();
		if (startShapes.size()>0) {
			for (ContainerShape child : startShapes) {
				List<ContainerShape[]> thread = new ArrayList<ContainerShape[]>();
				thread.add(new ContainerShape[] {child});
				buildThread(child, childShapes, thread);
				threads.add(thread);
			}
		}
		
		// arrange the threads
		int x = HORZ_PADDING;
		int y = VERT_PADDING;
		
		for (List<ContainerShape[]> thread : threads) {
			// stack the threads on top of each other
			x = HORZ_PADDING;
			int threadHeight = 0;
			for (ContainerShape[] group : thread) {
				int groupHeight = (group.length-1) * VERT_PADDING;
				for (ContainerShape shape : group) {
					IDimension size = GraphicsUtil.calculateSize(shape);
					groupHeight += size.getHeight();
					if (groupHeight > threadHeight) {
						threadHeight = groupHeight;
					}
				}
				threadHeight += (group.length-1) * VERT_PADDING;
			}
			
			for (ContainerShape[] group : thread) {
				int groupWidth = 0;
				int groupHeight = (group.length-1) * VERT_PADDING;
				for (ContainerShape shape : group) {
					IDimension size = GraphicsUtil.calculateSize(shape);
					groupHeight += size.getHeight();
				}
				int sy = y + (threadHeight/2 - groupHeight/2);
				for (ContainerShape shape : group) {
					IDimension size = GraphicsUtil.calculateSize(shape);
					if (size.getWidth()>groupWidth) {
						groupWidth = size.getWidth();
					}
					moveShape(container, shape, x, sy);
					sy += size.getHeight() + VERT_PADDING;
				}
				x += groupWidth + HORZ_PADDING;
			}
			y += threadHeight + VERT_PADDING;
		}

		stackShapes(container, unconnectedShapes);
		if (startShapes.size()==0 && endShapes.size()==0 && middleShapes.size()>0)
			stackShapes(container, middleShapes);
		
		// now resize the container so that all children are visible
		if (!(container instanceof Diagram)) {
			resizeContainerShape(container);
		}

		// TODO: remove this temporary hack to fix Manhattan Router issue
//		for (ContainerShape child : childShapes) {
//			FeatureSupport.updateConnections(editor.getDiagramTypeProvider().getFeatureProvider(), child);
//		}
	}

	private void stackShapes(ContainerShape container, List<ContainerShape> unconnectedShapes) {
		// stack any unconnected shapes on top of each other
		// first stack shapes that are NOT containers (like DataObject, DataStore, etc.)
		int maxWidth = 0;
		int maxHeight = 0;
		int x = HORZ_PADDING;
		int y = VERT_PADDING;
		if (unconnectedShapes.size()>0) {
			List<ContainerShape> children = getContainerShapeChildren(container);
			for (ContainerShape shape : unconnectedShapes) {
				BaseElement be = BusinessObjectUtil.getFirstBaseElement(shape);
				if (getContainerShapeChildren(shape).size()==0 && !(be instanceof Lane)) {
					IDimension size = GraphicsUtil.calculateSize(shape);
					Point p = moveShape(container, shape, x, y, children);
					x = p.getX();
					y = p.getY();
					y += size.getHeight() + VERT_PADDING;
					if (size.getWidth() > maxWidth)
						maxWidth = size.getWidth();
				}
			}
			if (y>maxHeight)
				maxHeight = y;
		
			// now handle all containers (Lane, SubProcess, Pool, etc.)
			x += maxWidth + HORZ_PADDING;
			y = VERT_PADDING;
			for (ContainerShape shape : unconnectedShapes) {
				BaseElement be = BusinessObjectUtil.getFirstBaseElement(shape);
				if (getContainerShapeChildren(shape).size()!=0 || be instanceof Lane) {
					IDimension size = GraphicsUtil.calculateSize(shape);
					Point p = moveShape(container, shape, x, y, children);
					x = p.getX();
					y = p.getY();
					if (be instanceof Lane) {
						resizeContainerShape(shape);
					}
					y += size.getHeight() + VERT_PADDING;
					if (size.getWidth() > maxWidth)
						maxWidth = size.getWidth();
				}
			}
		}
	}
	
	private boolean moveShape(ContainerShape container, ContainerShape shape, int x, int y) {
		MoveShapeContext context = new MoveShapeContext(shape);
		context.setLocation(x, y);
		context.setSourceContainer(container);
		context.setTargetContainer(container);
		IMoveShapeFeature moveFeature = editor.getDiagramTypeProvider().getFeatureProvider().getMoveShapeFeature(context);
		if (moveFeature.canMoveShape(context)) {
			moveFeature.moveShape(context);
			return true;
		}
		return false;
	}
	
	private Point moveShape(ContainerShape container, ContainerShape child, int x, int y, List<ContainerShape> allChildren) {
		boolean intersects;
		do {
			intersects = false;
			BaseElement be = BusinessObjectUtil.getFirstBaseElement(child);
			if (be instanceof BoundaryEvent) {
				// special handling for Boundary Events
				Activity activity = ((BoundaryEvent)be).getAttachedToRef();
				ContainerShape activityShape = null;
				for (ContainerShape s : allChildren) {
					if (s!=child) {
						if (activity == BusinessObjectUtil.getFirstBaseElement(s)) {
							activityShape = s;
							break;
						}
					}
				}
				if (activityShape!=null) {
					ILocation activityLoc = Graphiti.getPeLayoutService().getLocationRelativeToDiagram(activityShape);
					IDimension activitySize = GraphicsUtil.calculateSize(activityShape);
					IDimension eventSize = GraphicsUtil.calculateSize(child);
					int index = activity.getBoundaryEventRefs().indexOf(be);
					int count = activity.getBoundaryEventRefs().size();
					int deltaX = activitySize.getWidth() / 2;
					if (count>1) {
						deltaX = index * activitySize.getWidth() / (count-1);
					}
					moveShape(activityShape, child, deltaX - eventSize.getWidth()/2, activitySize.getHeight() - eventSize.getHeight()/2);
					y = 0;
					break;
				}
			}
			else {
				if (!moveShape(container, child, x, y))
					break;
			}
			for (ContainerShape c : allChildren) {
				if (c!=child && GraphicsUtil.intersects(child, c)) {
					intersects = true;
					y += VERT_PADDING;
				}
			}
		}
		while (intersects);
		
		return Graphiti.getCreateService().createPoint(x, y);
	}
	
	private boolean resizeContainerShape(ContainerShape container) {
		List<ContainerShape> children = getContainerShapeChildren(container);
		ILocation containerLocation = layoutService.getLocationRelativeToDiagram(container);
		int width = 0;
		int height = 0;
		
		for (ContainerShape child : children) {
			if (BusinessObjectUtil.getFirstBaseElement(child)!=null) {
				IDimension size = GraphicsUtil.calculateSize(child);
				ILocation location = layoutService.getLocationRelativeToDiagram(child);
				int x = location.getX() - containerLocation.getX();
				int y = location.getY() - containerLocation.getY();
				int w = x + size.getWidth();
				int h = y + size.getHeight();
				if (w>width)
					width = w;
				if (h>height)
					height = h;
			}
		}
		if ( BusinessObjectUtil.getFirstBaseElement(container) instanceof Lane) {
			if (width < 800)
				width = 800;
			if (height < 100)
				height = 100;
		}
		if (width!=0 && height!=0)
			return resizeShape(container, width + HORZ_PADDING, height + VERT_PADDING);
		return false;
	}
	
	private boolean resizeShape(ContainerShape container, int width, int height) {
		ResizeShapeContext context = new ResizeShapeContext(container);
		int x = container.getGraphicsAlgorithm().getX();
		int y = container.getGraphicsAlgorithm().getY();
		context.setLocation(x, y);
		context.setSize(width, height);
		IResizeShapeFeature resizeFeature = editor.getDiagramTypeProvider().getFeatureProvider().getResizeShapeFeature(context);
		if (resizeFeature.canResizeShape(context)) {
			resizeFeature.resizeShape(context);
			return true;
		}
		return false;
	}
	
	private boolean threadContains(List<ContainerShape[]> thread, ContainerShape shape) {
		for (ContainerShape[] shapes : thread) {
			for (ContainerShape s : shapes) {
				if (s==shape)
					return true;
			}
		}
		return false;
	}
	
	private void buildThread(ContainerShape shape, List<ContainerShape> childShapes, List<ContainerShape[]> thread) {
		List<ContainerShape> bin = new ArrayList<ContainerShape>();
		List<SequenceFlow> flows = getOutgoingSequenceFlows(shape);
		for (SequenceFlow flow : flows) {
			FlowNode target = flow.getTargetRef();
			// make sure the target shape is also a child of this container
			// in case a SequenceFlow crosses the container boundary
			ContainerShape targetShape = getContainerShape(target);
			if (childShapes.contains(targetShape) && !threadContains(thread, targetShape)) {
				bin.add(targetShape);
			}
		}
		if (!bin.isEmpty()) {
			thread.add(bin.toArray(new ContainerShape[bin.size()]));
			for (ContainerShape nextShape : bin) {
				buildThread(nextShape, childShapes, thread);
			}
		}
	}
	
	private List<SequenceFlow> getIncomingSequenceFlows(ContainerShape shape) {
		List<SequenceFlow> flows = new ArrayList<SequenceFlow>();
		for (Anchor a : shape.getAnchors()) {
			for (Connection c : a.getIncomingConnections()) {
				BaseElement be = BusinessObjectUtil.getFirstBaseElement(c);
				if (be instanceof SequenceFlow) {
					flows.add((SequenceFlow)be);
				}
			}
		}
		return flows;
	}
	
	private List<SequenceFlow> getOutgoingSequenceFlows(ContainerShape shape) {
		List<SequenceFlow> flows = new ArrayList<SequenceFlow>();
		for (Anchor a : shape.getAnchors()) {
			for (Connection c : a.getOutgoingConnections()) {
				BaseElement be = BusinessObjectUtil.getFirstBaseElement(c);
				if (be instanceof SequenceFlow) {
					flows.add((SequenceFlow)be);
				}
			}
		}
		return flows;
	}
	
	private ContainerShape getContainerShape(BaseElement be) {
		Diagram diagram = null;
		BPMNDiagram bpmnDiagram = DIUtils.findBPMNDiagram(be, true);
		if (bpmnDiagram != null) {
			diagram = DIUtils.findDiagram(editor.getDiagramBehavior(), bpmnDiagram);
			if (diagram==null) {
				System.out.println("Diagram is null"); //$NON-NLS-1$
			}
		}
		if (diagram!=null) {
			List<PictogramElement> list = Graphiti.getLinkService().getPictogramElements(diagram, be);
			for (PictogramElement pe : list) {
				if (isChildShape(pe)) {
					if (BusinessObjectUtil.getFirstBaseElement(pe) == be)
						return (ContainerShape)pe;
				}
			}
			
			// maybe the BaseElement is a root element (like a Process or Choreography)?
			if (bpmnDiagram.getPlane().getBpmnElement() == be)
				return diagram;
		}
		System.out.println("Container is null!"); //$NON-NLS-1$
		return null;
	}

	private List<ContainerShape> getContainerShapeChildren(ContainerShape container) {
		
		List<ContainerShape> childShapes = new ArrayList<ContainerShape>();
		for (PictogramElement pe : container.getChildren()) {
			if (isChildShape(pe)) {
				childShapes.add((ContainerShape)pe);
			}
		}
		
		return childShapes;
	}
	
	private boolean isChildShape(PictogramElement pe) {
		return pe instanceof ContainerShape && !FeatureSupport.isLabelShape((Shape)pe);
	}
}
