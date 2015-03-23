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
package org.eclipse.bpmn2.modeler.core.features;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.Association;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.Collaboration;
import org.eclipse.bpmn2.ConversationLink;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.InteractionNode;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.LaneSet;
import org.eclipse.bpmn2.MessageFlow;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil.AnchorLocation;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil.BoundaryAnchor;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.core.utils.GraphicsUtil;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.AreaContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.features.AbstractPasteFeature;

/**
 * Default Graphiti {@code PasteFeature} class for Shapes.
 * <p>
 */
public class DefaultPasteBPMNElementFeature extends AbstractPasteFeature {

	/** The EMF Resource. */
	protected Resource resource;
	
	/** The BPMN2 Definitions object - the root element of the document. */
	protected Definitions definitions;
	
	/**
	 * Maps the ID strings of the original BPMN2 elements to their
	 * corresponding newly constructed copies.
	 */
	protected Hashtable<String, String> idMap;
	
	/** The shape map. */
	protected HashMap<ContainerShape, ContainerShape> shapeMap;
	
	/** The connection map. */
	protected HashMap<Connection, Connection> connectionMap;
	
	/** The x reference. */
	protected int xReference;
	
	/** The y reference. */
	protected int yReference;
	
	/** The diagram. */
	protected Diagram diagram;

	/**
	 * Instantiates a new default {@code PasteFeature).
	 *
	 * @param fp the Feature Provider
	 */
	public DefaultPasteBPMNElementFeature(IFeatureProvider fp) {
		super(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.IPasteFeature#canPaste(org.eclipse.graphiti.features.context.IPasteContext)
	 */
	@Override
	public boolean canPaste(IPasteContext context) {
		// target must be a FlowElementsContainer (Process, etc.)
		ContainerShape targetContainerShape = getTargetContainerShape(context);
		if (targetContainerShape==null)
			return false;
		BaseElement targetContainerObject = getContainerObject(targetContainerShape);
		if (targetContainerObject==null)
			return false;

		Object[] pasteObjects;
		if (context.getProperty(GraphitiConstants.COPY_FROM_CONTEXT) != null) {
			// Get objects to paste from the context. This can be used to test if
			// objects can be pasted before they have been copied to the clipboard.
			pasteObjects = context.getPictogramElements();
		}
		else {
			// can paste, if all objects on the clipboard are PictogramElements
			pasteObjects = getFromClipboard();
		}

		if (pasteObjects == null || pasteObjects.length == 0) {
			return false;
		}
		int count = 0;
		for (Object object : pasteObjects) {
			if (!(object instanceof PictogramElement)) {
				continue;
			}
			PictogramElement pe = (PictogramElement) object;
			BaseElement be = BusinessObjectUtil.getFirstBaseElement(pe);
			if (!(be instanceof FlowElement) && !(be instanceof Lane) && !(be instanceof Participant)) {
				continue;
			}
			// can't paste Boundary Events directly - these are "carried along"
			// by the Activity to which they are attached.
			if (be instanceof BoundaryEvent) {
				continue;
			}
			// can't paste Label shapes
			if (pe instanceof Shape && FeatureSupport.isLabelShape((Shape)pe)) {
				continue;
			}
			// Participants can only be pasted into into a Collaboration
			if (be instanceof Participant && !(targetContainerObject instanceof Collaboration)) {
				continue;
			}
			++count;
		}
		if (count==0)
			return false;

		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.IPasteFeature#paste(org.eclipse.graphiti.features.context.IPasteContext)
	 */
	@Override
	public void paste(IPasteContext context) {
		ContainerShape targetContainerShape = getTargetContainerShape(context);
		BaseElement targetContainerObject = getContainerObject(targetContainerShape);

		// save the Diagram and Resource needed for constructing the new objects
		diagram = getFeatureProvider().getDiagramTypeProvider().getDiagram();
		resource = targetContainerObject.eResource();
		definitions = ModelUtil.getDefinitions(resource);
		idMap = new Hashtable<String, String>();
		shapeMap = new HashMap<ContainerShape, ContainerShape>();
		connectionMap = new HashMap<Connection, Connection>();
		xReference = 0;
		yReference = 0;
		
		int xMin = Integer.MAX_VALUE;
		int yMin = Integer.MAX_VALUE;
		Object[] fromClipboard = getFromClipboard();
		for (Object object : fromClipboard) {
			if (object instanceof ContainerShape) {
				ILocation loc = Graphiti.getLayoutService().getLocationRelativeToDiagram((ContainerShape) object);
				if (loc.getX() < xMin) {
					xMin = loc.getX();
				}
				if (loc.getY() < yMin) {
					yMin = loc.getY();
				}
			}
		}
		if (xMin!=Integer.MAX_VALUE) {
			xReference = xMin;
			yReference = yMin;
		}

		int x = context.getX();
		int y = context.getY();
		if (!(targetContainerShape instanceof Diagram)) {
			ILocation loc = Graphiti.getLayoutService().getLocationRelativeToDiagram(targetContainerShape);
			x -= loc.getX();
			y -= loc.getY();
		}

		// First create all shapes. This creates a lookup map of old to new
		// ContainerShape objects.
		for (Object object : fromClipboard) {
			if (object instanceof ContainerShape) {
				copyShape((ContainerShape) object, targetContainerShape, x, y);
			}
		}

		// Handle connections now that we know all shapes have been created
		x = context.getX(); // Connection bendpoint coordinates are always
							// relative to diagram
		y = context.getY();
		for (Object object : fromClipboard) {
			if (object instanceof Connection) {
				copyConnection((Connection) object, targetContainerShape, x, y);
			}
		}
		
		// handle any connections that were not created because of missing source/target
		for (Entry<Connection, Connection> entry : connectionMap.entrySet()) {
			if (entry.getValue()==null) {
				copyConnection(entry.getKey(), targetContainerShape, x, y);
			}
		}
		PictogramElement newPes[] = new PictogramElement[shapeMap.size()];
		int i = 0;
		for (Entry<ContainerShape, ContainerShape> entry : shapeMap.entrySet()) {
			newPes[i++] = entry.getValue();
		}
		
		this.getDiagramEditor().setPictogramElementsForSelection(newPes);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.features.AbstractPasteFeature#getFromClipboard()
	 */
	protected Object[] getFromClipboard() {
		List<Object> allObjects = new ArrayList<Object>();
		Object[] objects = super.getFromClipboard();
		for (Object object : objects) {
			if (object instanceof EObject && ((EObject)object).eContainer()!=null)
				allObjects.add(object);
		}
		
		List<Object> filteredObjects = new ArrayList<Object>();
		for (Object object : allObjects) {
			if (object instanceof EObject && ((EObject)object).eContainer()!=null) {
				if (object instanceof ContainerShape) {
					filteredObjects.add(object);
				}
				else if (object instanceof Connection) {
					Connection c = (Connection)object;
					if (c.getStart()!=null && c.getEnd()!=null) {
						if (allObjects.contains(c.getStart().getParent()) &&
								allObjects.contains(c.getEnd().getParent())) {
							filteredObjects.add(object);
						}
					}
				}
			}
		}
		
		return filteredObjects.toArray();
	}

	public <T extends EObject> T copyEObject(T eObject) {
		Copier copier = new Copier() {
			@Override
			protected EObject createCopy(EObject eObject) {
				EClass eClass = getTarget(eObject.eClass());
				if (eClass.getEPackage().getEFactoryInstance() == Bpmn2Factory.eINSTANCE) {
					return Bpmn2ModelerFactory.create(resource, eClass);
				}
				return super.createCopy(eObject); 
			}

		};
		EObject result = copier.copy(eObject);
		copier.copyReferences();

		@SuppressWarnings("unchecked")
		T t = (T) result;
		return t;
	}

	private BaseElement createNewObject(BaseElement oldObject, BaseElement targetContainerObject) {
		Bpmn2ModelerFactory.setEnableModelExtensions(false);
		BaseElement newObject = copyEObject(oldObject);
		Bpmn2ModelerFactory.setEnableModelExtensions(true);

		if (targetContainerObject instanceof Participant) {
			// need to create a Process for target container if it doesn't have one yet
			Participant participant = (Participant) targetContainerObject;
			if (participant.getProcessRef()==null) {
				Process process = Bpmn2ModelerFactory.create(resource, Process.class);
				participant.setProcessRef(process);
			}
			targetContainerObject = participant.getProcessRef();
		}
		
		// get rid of some of the objects created by EcoreUtil.copy() as these will be
		// constructed here because we need to create the Graphiti shapes and DI elements
		// along with these
		if (newObject instanceof FlowElementsContainer) {
			// we will create our own FlowElements, thank you!
			((FlowElementsContainer)newObject).getFlowElements().clear();
		}
		
		if (newObject instanceof Lane) {
			// we will construct these ourselves
			((Lane) newObject).getFlowNodeRefs().clear();
			((Lane) newObject).setChildLaneSet(null);
			if (targetContainerObject instanceof FlowElementsContainer) {
				FlowElementsContainer fc = (FlowElementsContainer)targetContainerObject;
				if (fc.getLaneSets().size()!=0) {
					fc.getLaneSets().get(0).getLanes().add((Lane)newObject);
				}
				else {
					LaneSet ls = Bpmn2ModelerFactory.create(resource, LaneSet.class);
					fc.getLaneSets().add(ls);
					ls.getLanes().add((Lane)newObject);
				}
			}
			else if (targetContainerObject instanceof Lane) {
				Lane ln = (Lane)targetContainerObject;
				if (ln.getChildLaneSet()==null) {
					LaneSet ls = Bpmn2ModelerFactory.create(resource, LaneSet.class);
					ln.setChildLaneSet(ls);
				}
				ln.getChildLaneSet().getLanes().add((Lane)newObject);
			}
		}
		else if (newObject instanceof FlowElement) {
			if (targetContainerObject instanceof Lane) {
				Lane ln = (Lane)targetContainerObject;
				targetContainerObject = getFlowElementsContainer(ln);
				// newObject could be either a Shape (FlowNode) or a Connection;
				// only add FlowNodes to the Lane's FlowNodeRefs list.
				if (newObject instanceof FlowNode)
					ln.getFlowNodeRefs().add((FlowNode)newObject);
			}
			if (targetContainerObject instanceof FlowElementsContainer) {
				((FlowElementsContainer)targetContainerObject).getFlowElements().add((FlowElement) newObject);
			}
		}
		else if (newObject instanceof Participant) {
			Participant participant = (Participant)newObject;
			if (((Participant) newObject).getProcessRef()!=null) {
				// need to create a new Process for this thing
				Process process = Bpmn2ModelerFactory.create(resource, Process.class);
				participant.setProcessRef(process);
			}
			if (targetContainerObject instanceof Collaboration) {
				Collaboration collab = (Collaboration)targetContainerObject;
				collab.getParticipants().add((Participant)newObject);
			}
		}

		// Ensure IDs are unique
		setId(newObject);

		TreeIterator<EObject> iter = newObject.eAllContents();
		while (iter.hasNext()) {
			EObject newChild = iter.next();
			setId(newChild);
		}

		for (EReference ref : newObject.eClass().getEAllReferences()) {
			if (!ref.isContainment()) {
				Object oldValue = oldObject.eGet(ref);
				// TODO: do we need this?
				// this mess also duplicates "incoming" and "outgoing" (for SequenceFlows)
				// which are already being handled in copyConnection()...
//				if (oldValue instanceof EObjectEList) {
//					EObjectEList oldList = (EObjectEList)oldObject.eGet(ref);
//					EObjectEList newList = (EObjectEList)newObject.eGet(ref);
//					for (Object oldRefObject : oldList) {
//						if (oldRefObject instanceof EObject) {
//							String oldId = getId((EObject)oldRefObject);
//							if (oldId!=null) {
//								String newId = idMap.get(oldId);
//								EObject newRefObject = findObjectById(newId);
//								newList.add(newRefObject);
//							}
//						}
//					}
//				}
//				else
				if (oldValue instanceof EObject){
					EObject oldRefObject = (EObject)oldValue;
					String oldId = getId(oldRefObject);
					if (oldId!=null) {
						String newId = idMap.get(oldId);
						if (newId!=null) {
							EObject newRefObject = findObjectById(newId);
							newObject.eSet(ref, newRefObject);
						}
						else if (newObject.eGet(ref) != null){
							EObject newRefObject = (EObject) newObject.eGet(ref);
							newId = getId(newRefObject);
							if (newId!=null)
								idMap.put(oldId, newId);
						}
					}
				}
			}
		}
		return newObject;
	}

	private String getId(EObject newObject) {
		EStructuralFeature feature = newObject.eClass().getEStructuralFeature("id"); //$NON-NLS-1$
		if (feature != null) {
			return (String) newObject.eGet(feature);
		}
		return null;
	}
	
	private String setId(EObject newObject) {
		String newId = null;
		String oldId = null;
		EStructuralFeature feature = newObject.eClass().getEStructuralFeature("id"); //$NON-NLS-1$
		if (feature != null) {
			oldId = (String) newObject.eGet(feature);
			if (idMap.contains(oldId)) {
				newId = idMap.get(oldId);
				newObject.eSet(feature, newId);
			}
			else {
				newObject.eUnset(feature);
				newId = ModelUtil.setID(newObject);
				idMap.put(oldId, newId);
			}
		}
		return oldId;
	}

	private boolean wasCopied(EObject object) {
		String id = getId(object);
		if (id!=null) {
			return idMap.containsValue(id);
		}
		return false;
	}
	
	private EObject findObjectById(String id) {
		TreeIterator<EObject> iter = definitions.eAllContents();
		while (iter.hasNext()) {
			EObject o = iter.next();
			EStructuralFeature feature = o.eClass().getEStructuralFeature("id"); //$NON-NLS-1$
			if (feature != null) {
				String thisId = (String) o.eGet(feature);
				if (thisId != null && !thisId.isEmpty() && thisId.equals(id))
					return o;
			}
		}
		return null;
	}

	private ContainerShape findShape(EObject object) {
		List<PictogramElement> pes = Graphiti.getLinkService().getPictogramElements(diagram, object);
		for (PictogramElement pe : pes) {
			if (pe instanceof ContainerShape)
				return (ContainerShape) pe;
		}
		return null;
	}

	private Connection findConnection(EObject object) {
		List<PictogramElement> pes = Graphiti.getLinkService().getPictogramElements(diagram, object);
		for (PictogramElement pe : pes) {
			if (pe instanceof Connection)
				return (Connection) pe;
		}
		return null;
	}

	private BaseElement copyShape(ContainerShape oldShape, ContainerShape targetContainerShape, int x, int y) {
		if (shapeMap.get(oldShape)!=null)
			return null;
		
		BaseElement targetContainerObject = getContainerObject(targetContainerShape);
		BaseElement oldObject = BusinessObjectUtil.getFirstBaseElement(oldShape);
		BaseElement newObject = createNewObject(oldObject, targetContainerObject);

		AddContext ac = new AddContext(new AreaContext(), newObject);
		ILocation loc = Graphiti.getLayoutService().getLocationRelativeToDiagram(oldShape);
		IDimension size = GraphicsUtil.calculateSize(oldShape);
		// The default Add BPMN Shape feature will position the new shape so its
		// center is at the target location; for copy/paste we want to use the
		// top-left corner instead so that copied connection bendpoints (if any)
		// line up properly.
		int deltaX = 0;
		int deltaY = 0;
		if (oldObject instanceof FlowNode) {
			deltaX = loc.getX() - xReference + size.getWidth() / 2;
			deltaY = loc.getY() - yReference + size.getHeight() / 2;
		}
		ac.setLocation(x + deltaX, y + deltaY);
		ac.setSize(size.getWidth(), size.getHeight());
		ac.setTargetContainer(targetContainerShape);

		BPMNShape oldBpmnShape = null;
		if (oldObject instanceof BaseElement) {
			oldBpmnShape = DIUtils.findBPMNShape((BaseElement)oldObject);
			ac.putProperty(GraphitiConstants.COPIED_BPMN_SHAPE, oldBpmnShape);
		}
		ac.putProperty(GraphitiConstants.COPIED_BPMN_OBJECT, oldObject);
		
		
		ContainerShape newShape = (ContainerShape) getFeatureProvider().addIfPossible(ac);

		shapeMap.put(oldShape, newShape);

		if (oldObject instanceof Participant) {
			// copy the contained Process elements
			oldObject = ((Participant)oldObject).getProcessRef();
		}

		// create shapes and connections for children if this is a FlowElementsContainer
		if (oldObject instanceof FlowElementsContainer) {
			List<ContainerShape> childShapes = new ArrayList<ContainerShape>();
			List<Connection> childConnections = new ArrayList<Connection>();
			TreeIterator<EObject> iter = oldObject.eAllContents();
			while (iter.hasNext()) {
				// look up the old child object that corresponds to the new child object 
				EObject oldChildObject = iter.next();
				if (oldChildObject instanceof BoundaryEvent) {
					// Defer Boundary Event creation until we're sure that the
					// new attachedToRef task is actually created.
					continue;
				}
				if (wasCopied(oldChildObject)) {
					// stop infinite recursion: this would happen if a FlowElementsContainer
					//was copied into itself.
					continue;
				}
				
				// if the old child has a Graphiti ContainerShape, duplicate it.
				ContainerShape oldChildShape = findShape(oldChildObject);
				if (oldChildShape != null) {
					childShapes.add(oldChildShape);
				}
				Connection oldChildConnection = findConnection(oldChildObject);
				if (oldChildConnection != null) {
					childConnections.add(oldChildConnection);
				}
			}
			
			for (ContainerShape oldChildShape : childShapes) {
				copyShape(oldChildShape, newShape, 0, 0);
			}
			
			for (Connection oldChildConnection : childConnections) {
				copyConnection(oldChildConnection, newShape, x, y);
			}
		}
		else if (oldObject instanceof Lane) {
	        List<PictogramElement> shapes = new ArrayList<PictogramElement>();
	        Lane oldLane = (Lane)oldObject;
	        if (oldLane.getChildLaneSet()!=null) {
	        	for (Lane oldChildLaneObject : oldLane.getChildLaneSet().getLanes()) {
	        		ContainerShape oldChildLaneShape = findShape(oldChildLaneObject);
	        		if (oldChildLaneShape != null) {
	        			copyShape(oldChildLaneShape, newShape, 0, 0);
	        		}
	        	}
	        }
			for (FlowNode oldChildObject : oldLane.getFlowNodeRefs()) {
				ContainerShape oldChildShape = findShape(oldChildObject);
				if (oldChildShape != null) {
					copyShape(oldChildShape, newShape, 0, 0);
					shapes.add(oldChildShape);
				}
			}
			List<Connection> connections = DefaultCopyBPMNElementFeature.findAllConnections(shapes);
			for (Connection oldChildConnection : connections) {
				copyConnection(oldChildConnection, newShape, x, y);
			}
		}
		
		// also copy the BPMNShape properties
		if (oldBpmnShape!=null) {
			BPMNShape newBpmnShape = DIUtils.findBPMNShape((BaseElement)newObject);
			newBpmnShape.setIsExpanded(oldBpmnShape.isIsExpanded());
			newBpmnShape.setIsHorizontal(oldBpmnShape.isIsHorizontal());
			newBpmnShape.setIsMarkerVisible(oldBpmnShape.isIsMarkerVisible());
			newBpmnShape.setIsMessageVisible(oldBpmnShape.isIsMessageVisible());
			newBpmnShape.setParticipantBandKind(oldBpmnShape.getParticipantBandKind());
		}

		UpdateContext uc = new UpdateContext(newShape);
		IUpdateFeature uf = getFeatureProvider().getUpdateFeature(uc);
		// force an update to cause the newly created ContainerShape to be rendered properly
		uc.putProperty(GraphitiConstants.FORCE_UPDATE_ALL, Boolean.TRUE);
		uf.update(uc);
		
		if (newObject instanceof Activity) {
			// copy the Activity's Boundary Events if it has any
			TreeIterator<EObject> i = definitions.eAllContents();
			while (i.hasNext()) {
				EObject o = i.next();
				if (o instanceof BoundaryEvent) {
					BoundaryEvent oldBeObject = (BoundaryEvent)o;
					if (oldBeObject.getAttachedToRef() == oldObject) {
						// here's one...
						ContainerShape oldBeShape = findShape(oldBeObject);
						copyShape(oldBeShape, targetContainerShape, x, y);
					}
				}
			}
		}

		return newObject;
	}

	private BaseElement copyConnection(Connection oldConnection, ContainerShape targetContainerShape, int x, int y) {
		if (connectionMap.get(oldConnection)!=null)
			return null;
		
		BaseElement targetContainerObject = getContainerObject(targetContainerShape);
		BaseElement oldObject = BusinessObjectUtil.getFirstBaseElement(oldConnection);
		BaseElement newObject = createNewObject(oldObject, targetContainerObject);

		Anchor oldStart = oldConnection.getStart();
		Anchor oldEnd = oldConnection.getEnd();
		ContainerShape newSource = shapeMap.get(oldStart.getParent());
		ContainerShape newTarget = shapeMap.get(oldEnd.getParent());
		if (newSource==null || newTarget==null) {
			// source or target does not exist yet - handle this connection later
			connectionMap.put(oldConnection, null);
			return null;
		}
		
		Anchor newStart;
		Anchor newEnd;
		if (AnchorUtil.isBoundaryAnchor(oldStart)) {
			AnchorLocation al = AnchorUtil.getBoundaryAnchorLocation(oldStart);
			Map<AnchorLocation, BoundaryAnchor> bas = AnchorUtil.getBoundaryAnchors(newSource);
			newStart = bas.get(al).anchor;
		}
		else {
			ILocation loc = Graphiti.getLayoutService().getLocationRelativeToDiagram(oldStart);
			newStart = AnchorUtil.createAdHocAnchor(newSource, loc.getX(), loc.getY());
		}
		if (AnchorUtil.isBoundaryAnchor(oldEnd)) {
			AnchorLocation al = AnchorUtil.getBoundaryAnchorLocation(oldEnd);
			Map<AnchorLocation, BoundaryAnchor> bas = AnchorUtil.getBoundaryAnchors(newTarget);
			newEnd = bas.get(al).anchor;
		}
		else {
			ILocation loc = Graphiti.getLayoutService().getLocationRelativeToDiagram(oldEnd);
			newEnd = AnchorUtil.createAdHocAnchor(newTarget, loc.getX(), loc.getY());
		}

		BaseElement newSourceObject = BusinessObjectUtil.getFirstBaseElement(newSource);
		BaseElement newTargetObject = BusinessObjectUtil.getFirstBaseElement(newTarget);
		if (newObject instanceof SequenceFlow) {
			((SequenceFlow) newObject).setSourceRef((FlowNode) newSourceObject);
			((SequenceFlow) newObject).setTargetRef((FlowNode) newTargetObject);
		}
		else if (newObject instanceof Association) {
			((Association) newObject).setSourceRef((FlowNode) newSourceObject);
			((Association) newObject).setTargetRef((FlowNode) newTargetObject);
		}
		else if (newObject instanceof MessageFlow) {
			((MessageFlow) newObject).setSourceRef((InteractionNode) newSourceObject);
			((MessageFlow) newObject).setTargetRef((InteractionNode) newTargetObject);
		}
		else if (newObject instanceof ConversationLink) {
			((ConversationLink) newObject).setSourceRef((InteractionNode) newSourceObject);
			((ConversationLink) newObject).setTargetRef((InteractionNode) newTargetObject);
		}
		AddConnectionContext acc = new AddConnectionContext(newStart, newEnd);
		acc.setNewObject(newObject);

		Connection newConnection = (Connection) getFeatureProvider().addIfPossible(acc);
		connectionMap.put(oldConnection, newConnection);

		if (oldConnection instanceof FreeFormConnection && newConnection instanceof FreeFormConnection) {
			for (Point p : ((FreeFormConnection) oldConnection).getBendpoints()) {
				int deltaX = p.getX() - xReference;
				int deltaY = p.getY() - yReference;
				Point newPoint = GraphicsUtil.createPoint(x + deltaX, y + deltaY);
				((FreeFormConnection) newConnection).getBendpoints().add(newPoint);
			}
		}
		
		// also copy the BPMNEdge properties
		if (oldObject instanceof BaseElement) {
			BPMNEdge oldBpmnEdge = DIUtils.findBPMNEdge((BaseElement)oldObject);
			if (oldBpmnEdge!=null) {
				BPMNEdge newBpmnEdge = DIUtils.findBPMNEdge((BaseElement)newObject);
				newBpmnEdge.setMessageVisibleKind(oldBpmnEdge.getMessageVisibleKind());
			}
		}

		FeatureSupport.updateConnection(getFeatureProvider(), newConnection);

		return newObject;
	}
	
	private ContainerShape getTargetContainerShape(IPasteContext context) {
		Diagram diagram = getFeatureProvider().getDiagramTypeProvider().getDiagram();
		
		Point p = GraphicsUtil.createPoint(context.getX(), context.getY());
		Shape s = GraphicsUtil.findShapeAt(diagram, p, new GraphicsUtil.IShapeFilter() {
			@Override
			public boolean matches(Shape shape) {
				if (shape instanceof ContainerShape) {
					BaseElement be = getContainerObject((ContainerShape) shape);
					return be instanceof FlowElementsContainer || be instanceof Participant;
				}
				return false;
			}
		});
		if (s!=null)
			return (ContainerShape) s;
		return diagram;
	}
	
	private BaseElement getContainerObject(ContainerShape targetContainerShape) {
		EObject bo = BusinessObjectUtil.getBusinessObjectForPictogramElement(targetContainerShape);
		if (bo instanceof BPMNDiagram) {
			bo = ((BPMNDiagram) bo).getPlane().getBpmnElement();
		}
		if (bo instanceof Participant) {
			if (!FeatureSupport.isChoreographyParticipantBand(targetContainerShape))
				return (Participant) bo;
			bo = ((Participant) bo).getProcessRef();
		}
		if (bo instanceof FlowElementsContainer || bo instanceof Lane || bo instanceof Collaboration)
			return (BaseElement) bo;
		return null;
	}
	
	private FlowElementsContainer getFlowElementsContainer(Lane lane) {
		EObject container = lane.eContainer();
		while (!(container instanceof FlowElementsContainer) && container!=null)
			container = container.eContainer();
		return (FlowElementsContainer)container;
	}
}
