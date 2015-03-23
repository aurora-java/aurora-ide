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
package org.eclipse.bpmn2.modeler.core.di;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.bpmn2.Artifact;
import org.eclipse.bpmn2.Association;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Choreography;
import org.eclipse.bpmn2.ChoreographyActivity;
import org.eclipse.bpmn2.Collaboration;
import org.eclipse.bpmn2.ConversationNode;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.DataStore;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.LaneSet;
import org.eclipse.bpmn2.MessageFlow;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.SubChoreography;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.di.BpmnDiFactory;
import org.eclipse.bpmn2.di.ParticipantBandKind;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.preferences.Bpmn2Preferences;
import org.eclipse.bpmn2.modeler.core.preferences.ShapeStyle;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.GraphicsUtil;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.core.utils.ShapeLayoutManager;
import org.eclipse.bpmn2.modeler.core.utils.Tuple;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.dc.DcFactory;
import org.eclipse.dd.dc.Point;
import org.eclipse.dd.di.DiagramElement;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

public class DIGenerator {

	private DIImport importer;
	private Diagram diagram;
	private BPMNDiagram bpmnDiagram;
	private DiagramEditor editor;
	private Definitions definitions;
	private HashMap<BaseElement, PictogramElement> elements;
	private ImportDiagnostics diagnostics;
	private DiagramElementTree missingElements;
	private Bpmn2Preferences preferences;
	
	public DIGenerator(DIImport importer) {
		this.importer = importer;
		elements = importer.getImportedElements();
		diagnostics = importer.getDiagnostics();
		editor = importer.getEditor();
		diagram = editor.getDiagramTypeProvider().getDiagram();
		bpmnDiagram = BusinessObjectUtil.getFirstElementOfType(diagram, BPMNDiagram.class);
		definitions = ModelUtil.getDefinitions(bpmnDiagram);
		preferences = Bpmn2Preferences.getInstance(definitions);
	}
	
	public boolean hasMissingDIElements() {
		if (missingElements==null)
			missingElements = findMissingDIElements();
		return missingElements.hasChildren();
	}
	
	public void generateMissingDIElements() {
		if (hasMissingDIElements()) {
			// Display a dialog of the missing elements and allow user
			// to choose which ones to create
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					MissingDIElementsDialog dlg = new MissingDIElementsDialog(missingElements);
					if (dlg.open()==Window.OK) {
						TransactionalEditingDomain domain = editor.getEditingDomain();
						domain.getCommandStack().execute(new RecordingCommand(domain) {
							@Override
							protected void doExecute() {
								createMissingDIElements(missingElements);
								
								ShapeLayoutManager layoutManager = new ShapeLayoutManager(editor);
								Iterator<DiagramElementTreeNode> iter = missingElements.iterator();
								while (iter.hasNext()) {
									DiagramElementTreeNode node = iter.next();
									if (node.getChecked()) {
										layoutManager.layout(node.getBaseElement());
									}
								}
							}
						});
					}
				}
			});
		}
	}
	
	private DiagramElementTree findMissingDIElements() {
		
		DiagramElementTree missing = new DiagramElementTree(null,null);
		
		// look for any BPMN2 elements that do not have corresponding DI elements
		for (BaseElement be : definitions.getRootElements()) {
			findMissingDIElements(missing, be);
		}
		
		removeDuplicates(missing.getChildren());
		return missing;
	}
	
	private void removeDuplicates(List<DiagramElementTreeNode> children) {
		List<DiagramElementTreeNode> duplicates = new ArrayList<DiagramElementTreeNode>();
		for (DiagramElementTreeNode node : children) {
			if (node.hasChildren())
				removeDuplicates(node.getChildren());
			
			BaseElement be = node.getBaseElement();
			if (be instanceof Collaboration) {
				Collaboration c = (Collaboration)be;
				for (Participant p : c.getParticipants()) {
					for (DiagramElementTreeNode n : children) {
						if (n.getBaseElement() == p.getProcessRef()) {
							duplicates.add(n);
						}
					}
				}
			}
			else if (be instanceof ChoreographyActivity) {
				ChoreographyActivity c = (ChoreographyActivity)be;
				for (Participant p : c.getParticipantRefs()) {
					for (DiagramElementTreeNode n : children) {
						if (n.getBaseElement() == p) {
							duplicates.add(n);
						}
					}
				}
			}
		}
		if (!duplicates.isEmpty())
			children.removeAll(duplicates);
	}
	
	private boolean isMissingDIElement(BaseElement be) {
		// ignore DataStores - there are bound to be references
		// to these, which *should* be rendered
		if (be instanceof DataStore)
			return false;
		BPMNDiagram bpmnDiagram = DIUtils.findBPMNDiagram(be);
		if (bpmnDiagram!=null)
			return false;
		// couldn't find a BPMNDiagram entry for this BaseElement
		// check its container to see if it has a BPMNDiagram
		FlowElementsContainer container = this.getRootElementContainer(be);
		bpmnDiagram = DIUtils.findBPMNDiagram(container);
		if (bpmnDiagram!=null) {
			// is the BaseElement defined as a BPMNShape or BPMNEdge in its
			// container's BPMNDiagram?
			if (bpmnDiagram.getPlane().getPlaneElement().contains(be))
				return false;
		}
		boolean missing = (elements.get(be) == null && diagnostics.get(be) == null);
		if (missing)
			GraphicsUtil.dump("Missing DI element for: "+be.eClass().getName()+" '"+ExtendedPropertiesProvider.getTextValue(be)+"'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return missing;
	}
	
	private boolean isDataElement(BaseElement be) {
		return be instanceof ItemAwareElement && be instanceof FlowElement;
	}
	
	private int findMissingDIElements(DiagramElementTreeNode missing, LaneSet laneSet, List<FlowElement> laneElements) {
		int added = 0;
		if (laneSet!=null) {
			for (Lane lane : laneSet.getLanes()) {
				// create the missing tree node for this Lane's container
				// this is either a FlowElementsContainer or another Lane
				BaseElement container = (BaseElement) lane.eContainer().eContainer();
				DiagramElementTreeNode containerNode = missing.getChild(container);
				if (containerNode==null)
					containerNode = missing.addChild(container);
				DiagramElementTreeNode parentNode = containerNode.addChild(lane);
				
				for (FlowNode fn : lane.getFlowNodeRefs()) {
					if (isMissingDIElement(fn)) {
						parentNode.addChild(fn);
						laneElements.add(fn);
						++added;
					}
				}
				added += findMissingDIElements(parentNode, lane.getChildLaneSet(), laneElements);
				
				if (added==0) {
					containerNode.removeChild(lane);
					missing.removeChild(container);
				}
			}
		}
		return added;
	}
	
	private void findMissingDIElements(DiagramElementTreeNode missing, BaseElement be) {
		if (be instanceof FlowElementsContainer) {
			// handles Process/SubProcess and Choreography/SubChoreography
			FlowElementsContainer container = (FlowElementsContainer)be;
			DiagramElementTreeNode parentNode = null;
			
			List<FlowElement> laneElements = new ArrayList<FlowElement>();
			for (LaneSet laneSet : container.getLaneSets()) {
				findMissingDIElements(missing, laneSet, laneElements);
			}
			
			for (FlowElement fe : container.getFlowElements()) {
				if (isMissingDIElement(fe) && !laneElements.contains(fe)) {
					if (fe instanceof SequenceFlow || fe instanceof DataObject || fe instanceof DataStore)
						continue;
					if (parentNode==null)
						parentNode = missing.addChild(container);
					parentNode.addChild(fe);
					if (fe instanceof FlowElementsContainer || fe instanceof ChoreographyActivity) {
						findMissingDIElements(parentNode, fe);
					}
				}
			}
			List<Artifact> artifacts = getArtifacts(container);
			if (artifacts!=null) {
				for (Artifact a : artifacts) {
					if (isMissingDIElement(a) && !(a instanceof Association)) {
						if (parentNode==null)
							parentNode = missing.addChild(container);
						parentNode.addChild(a);
					}
				}
			}
		}
		
		// Choreography inherits both Collaboration and FlowElementsContainer
		if (be instanceof Collaboration) {
			// also handle Choreography
			Collaboration container = (Collaboration)be;
			DiagramElementTreeNode parentNode = null;
			for (Artifact a : container.getArtifacts()) {
				if (isMissingDIElement(a) && !(a instanceof Association)) {
					if (parentNode==null)
						parentNode = missing.addChild(container);
					parentNode.addChild(a);
				}
			}
			for (Participant p : container.getParticipants()) {
				boolean isParticipantBand = false;
				if (p.eContainer() instanceof Choreography) {
					// this may be a Choreography Activity Participant band
					Choreography choreography = (Choreography) p.eContainer();
					for (FlowElement fe : choreography.getFlowElements()) {
						if (fe instanceof ChoreographyActivity) {
							if (((ChoreographyActivity)fe).getParticipantRefs().contains(p)) {
								isParticipantBand = true;
								break;
							}
						}
					}
				}
				if (isMissingDIElement(p) && p.getProcessRef()!=null && !isParticipantBand) {
					if (parentNode==null)
						parentNode = missing.addChild(container);
					parentNode.addChild(p);
				}
			}
			for (ConversationNode c : container.getConversations()) {
				if (isMissingDIElement(c)) {
					if (parentNode==null)
						parentNode = missing.addChild(container);
					parentNode.addChild(c);
				}
			}
		}
		else if (be instanceof Participant) {
			Participant container = (Participant) be;
			if (container.getProcessRef()!=null) {
				DiagramElementTreeNode parentNode = missing.addChild(container);
				parentNode.addChild(container.getProcessRef());
			}
		}
		else if (be instanceof ChoreographyActivity) {
			ChoreographyActivity container = (ChoreographyActivity)be;
			DiagramElementTreeNode parentNode = null;
			for (Participant p : container.getParticipantRefs()) {
				if (isMissingDIElement(p)) {
					if (parentNode==null)
						parentNode = missing.addChild(container);
					DiagramElementTreeNode child = parentNode.addChild(p);
					if (p.getProcessRef()!=null)
						findMissingDIElements(child, p.getProcessRef());
				}
			}
		}
		else if (isDataElement(be)) {
			if (isMissingDIElement(be)) {
				missing.addChild(be);
			}
		}
	}
	
	private List<Artifact> getArtifacts(BaseElement container) {
		if (container instanceof Process) {
			return ((Process)container).getArtifacts();
		}
		if (container instanceof SubProcess) {
			return ((SubProcess)container).getArtifacts();
		}
		if (container instanceof SubChoreography) {
			return ((SubChoreography)container).getArtifacts();
		}
		if (container instanceof Collaboration) {
			return ((Collaboration)container).getArtifacts();
		}
		return null;
	}

	private FlowElementsContainer getRootElementContainer(EObject o) {
		while (o!=null) {
			if (o instanceof FlowElementsContainer && o instanceof RootElement) {
				return (FlowElementsContainer)o;
			}
			o = o.eContainer();
		}
		return null;
	}
	
	private BPMNShape createMissingDIElement(DiagramElementTreeNode node, int x, int y, List<BaseElement> created) {
		BaseElement element = node.getBaseElement();
		BPMNShape bpmnShape = null;
		BPMNDiagram bpmnDiagram = createDIDiagram(element);
		
		if (element instanceof Lane) {
			Lane lane = (Lane)element;
			bpmnShape = createDIShape(bpmnDiagram, lane, x, y);
			node.setBpmnShape(bpmnShape);

			y = createMissingDIElementChildren(node, x, y, created);
			created.add(lane);
		}
		else if (element instanceof FlowElementsContainer) {
			FlowElementsContainer container = (FlowElementsContainer)element;

			if (container instanceof SubProcess || container instanceof SubChoreography) {
				bpmnShape = createDIShape(bpmnDiagram, container, x, y);
				node.setBpmnShape(bpmnShape);
				created.add(container);
			}

			y = createMissingDIElementChildren(node, x, y, created);
		}
		else if (element instanceof Collaboration) {
			y = createMissingDIElementChildren(node, x, y, created);
		}
		else if (element instanceof Artifact) {
			bpmnShape = createDIShape(bpmnDiagram, element, x, y);
			node.setBpmnShape(bpmnShape);
			created.add(element);
		}
		else if (element instanceof Participant) {
			boolean doImport = true;
			if (node.getParent().getBaseElement() instanceof ChoreographyActivity) {
				// this is a Participant Band in a Choreography Activity
				doImport = false;
			}
			
			bpmnShape = createDIShape(bpmnDiagram, element, x, y, doImport);
			node.setBpmnShape(bpmnShape);
			created.add(element);
			if (!doImport) {
				ChoreographyActivity ca = (ChoreographyActivity) node.getParent().getBaseElement();
				bpmnShape.setChoreographyActivityShape(node.getParent().getBpmnShape());
				if (ca.getParticipantRefs().get(0) == element)
					bpmnShape.setParticipantBandKind(ParticipantBandKind.TOP_INITIATING);
				else
					bpmnShape.setParticipantBandKind(ParticipantBandKind.BOTTOM_NON_INITIATING);
			}
			createMissingDIElementChildren(node, x, y, created);
		}
		else if (element instanceof ConversationNode) {
			bpmnShape = createDIShape(bpmnDiagram, element, x, y);
			node.setBpmnShape(bpmnShape);
			created.add(element);
		}
		else if (element instanceof FlowNode) {
			boolean doImport = !(element instanceof ChoreographyActivity);
			bpmnShape = createDIShape(bpmnDiagram, element, x, y, doImport);
			node.setBpmnShape(bpmnShape);
			created.add(element);
			y = createMissingDIElementChildren(node, x, y, created);
			if (!doImport)
				importer.importShape(bpmnShape);
		}
		else if (isDataElement(element)) {
			bpmnShape = createDIShape(bpmnDiagram, element, x, y);
			node.setBpmnShape(bpmnShape);
			created.add(element);
		}
		return bpmnShape;
	}
	
	private int createMissingDIElementChildren(DiagramElementTreeNode node, int x, int y, List<BaseElement> created) {
		for (DiagramElementTreeNode childNode : node.getChildren()) {
			if (childNode.getChecked()) {
				BPMNShape bpmnShape = createMissingDIElement(childNode, x, y, created);
				if (bpmnShape!=null) {
					y += bpmnShape.getBounds().getHeight() + 10;
				}
			}
		}
		return y;
	}
	
	private void createMissingDIElements(DiagramElementTree missing) {

		// look for any BPMN2 elements that do not have corresponding DI elements
		// and create DI elements for them. First, handle the BPMNShape objects:
		int x = 102400;
		int y = 0;
		List<BaseElement> shapes = new ArrayList<BaseElement>();
		for (DiagramElementTreeNode node : missing.getChildren()) {
			if (node.getChecked()) {
				BPMNShape bpmnShape = createMissingDIElement(node, x, y, shapes);
				if (bpmnShape!=null) {
					y += bpmnShape.getBounds().getHeight() + 10;
				}
			}
		}
		
		// Next create the BPMNEdge objects. At this point, all of the source
		// and target elements for the connections should already exist, so
		// we don't have to worry about that.
		List<BaseElement> connections = new ArrayList<BaseElement>();
		for (BaseElement be : shapes) {
			if (be instanceof FlowNode) {
				FlowNode flowNode = (FlowNode)be;
				// find the BPMNDiagram that contains this flow node
				BPMNDiagram bpmnDiagram = createDIDiagram(flowNode);

				for (SequenceFlow sf : flowNode.getIncoming()) {
					if (!connections.contains(sf)) {
						BPMNEdge bpmnEdge = createDIEdge(bpmnDiagram, sf);
						if (bpmnEdge!=null)
							connections.add(sf);
					}
				}

				for (SequenceFlow sf : flowNode.getOutgoing()) {
					if (!connections.contains(sf)) {
						BPMNEdge bpmnEdge = createDIEdge(bpmnDiagram, sf);
						if (bpmnEdge!=null)
							connections.add(sf);
					}
				}
			}
			else if (be instanceof ConversationNode) {
				ConversationNode convNode = (ConversationNode)be;
				BPMNDiagram bpmnDiagram = createDIDiagram(convNode);
				for (MessageFlow mf : convNode.getMessageFlowRefs()) {
					if (!connections.contains(mf)) {
						BPMNEdge bpmnEdge = createDIEdge(bpmnDiagram, mf);
						if (bpmnEdge!=null)
							connections.add(mf);
					}
				}
			}
		}
		// Finally, Associations are RootElements and since we only include shapes
		// in the missing elements tree, we'll have to revisit all of the RootElements
		TreeIterator<EObject> iter = definitions.eAllContents();
		while (iter.hasNext()) {
			EObject o = iter.next();
			if (o instanceof Association) {
				Association assoc = (Association)o;
				BPMNDiagram bpmnDiagram = createDIDiagram(assoc);
				BPMNEdge bpmnEdge = createDIEdge(bpmnDiagram, assoc);
				if (bpmnEdge!=null)
					connections.add(assoc);
			}
		}
	}
	
	// TODO: can these be merged into DIUtils?
	
	private BPMNDiagram createDIDiagram(BaseElement bpmnElement) {

		BPMNDiagram bpmnDiagram = DIUtils.findBPMNDiagram(bpmnElement, true);
	
		// if this container does not have a BPMNDiagram, create one
		if (bpmnElement instanceof Process) {
			if (bpmnDiagram==null) {
				// unless this Process is referenced by a Pool
				for (Collaboration c : ModelUtil.getAllObjectsOfType(bpmnElement.eResource(), Collaboration.class)) {
					for (Participant p : c.getParticipants()) {
						if (!ModelUtil.isParticipantBand(p)) {
							if (p.getProcessRef() == bpmnElement) {
								bpmnDiagram = DIUtils.findBPMNDiagram(p, true);
								break;
							}
						}
					}
					if (bpmnDiagram!=null)
						break;
				}
			}
			else {
				// Always create a new BPMNDiagram if this Process is being referenced by a Participant Band
//				for (Collaboration c : ModelUtil.getAllObjectsOfType(bpmnElement.eResource(), Collaboration.class)) {
//					for (Participant p : c.getParticipants()) {
//						if (ModelUtil.isParticipantBand(p)) {
//							if (p.getProcessRef() == bpmnElement) {
//								bpmnDiagram = null;
//								break;
//							}
//						}
//					}
//					if (bpmnDiagram==null)
//						break;
//				}
			}
		}
		
		if (bpmnDiagram==null) {
			FlowElementsContainer container = getRootElementContainer(bpmnElement);
			if (container==null) {
				diagnostics.add(IStatus.ERROR, bpmnElement, Messages.DIGenerator_No_Diagram); 
				return this.bpmnDiagram;
			}
			BPMNPlane plane = BpmnDiFactory.eINSTANCE.createBPMNPlane();
			plane.setBpmnElement(container);

			bpmnDiagram = BpmnDiFactory.eINSTANCE.createBPMNDiagram();
			bpmnDiagram.setName(ExtendedPropertiesProvider.getTextValue(container));
			bpmnDiagram.setPlane(plane);

			definitions.getDiagrams().add(bpmnDiagram);
		}

		return bpmnDiagram;
	}
	
	private BPMNShape createDIShape(BPMNDiagram bpmnDiagram, BaseElement bpmnElement, float x, float y) {
		return createDIShape(bpmnDiagram, bpmnElement, x, y, true);
	}
	
	private BPMNShape createDIShape(BPMNDiagram bpmnDiagram, BaseElement bpmnElement, float x, float y, boolean doImport) {
		
		BPMNPlane plane = bpmnDiagram.getPlane();
		BPMNShape bpmnShape = null;
		for (DiagramElement de : plane.getPlaneElement()) {
			if (de instanceof BPMNShape) {
				if (bpmnElement == ((BPMNShape)de).getBpmnElement()) {
					bpmnShape = (BPMNShape)de;
					break;
				}
			}
		}
		
		if (bpmnShape==null) {
			bpmnShape = BpmnDiFactory.eINSTANCE.createBPMNShape();
			bpmnShape.setBpmnElement(bpmnElement);
			Bounds bounds = DcFactory.eINSTANCE.createBounds();
			bounds.setX(x);
			bounds.setY(y);
			ShapeStyle ss = preferences.getShapeStyle(bpmnElement);
			bounds.setWidth(ss.getDefaultWidth());
			bounds.setHeight(ss.getDefaultHeight());
			bpmnShape.setBounds(bounds);
			plane.getPlaneElement().add(bpmnShape);
			preferences.applyBPMNDIDefaults(bpmnShape, null);

			ModelUtil.setID(bpmnShape);
			if (doImport)
				importer.importShape(bpmnShape);
		}
		
		return bpmnShape;
	}
	
	private BPMNEdge createDIEdge(BPMNDiagram bpmnDiagram, BaseElement bpmnElement) {
		BPMNPlane plane = bpmnDiagram.getPlane();
		BPMNEdge bpmnEdge = null;
		for (DiagramElement de : plane.getPlaneElement()) {
			if (de instanceof BPMNEdge) {
				if (bpmnElement == ((BPMNEdge)de).getBpmnElement()) {
					bpmnEdge = (BPMNEdge)de;
					break;
				}
			}
		}

		if (bpmnEdge==null) {
			bpmnEdge = BpmnDiFactory.eINSTANCE.createBPMNEdge();
			bpmnEdge.setBpmnElement(bpmnElement);
	
			BaseElement sourceElement = null;
			BaseElement targetElement = null;
			if (bpmnElement instanceof SequenceFlow) {
				sourceElement = ((SequenceFlow)bpmnElement).getSourceRef();
				targetElement = ((SequenceFlow)bpmnElement).getTargetRef();
			}
			else if (bpmnElement instanceof MessageFlow) {
				sourceElement = (BaseElement) ((MessageFlow)bpmnElement).getSourceRef();
				targetElement = (BaseElement) ((MessageFlow)bpmnElement).getTargetRef();
			}
			else if (bpmnElement instanceof Association) {
				sourceElement = ((Association)bpmnElement).getSourceRef();
				targetElement = ((Association)bpmnElement).getTargetRef();
			}
			
			if (sourceElement!=null && targetElement!=null) {
				DiagramElement de;
				de = DIUtils.findPlaneElement(plane.getPlaneElement(), sourceElement);
				bpmnEdge.setSourceElement(de);
				
				de = DIUtils.findPlaneElement(plane.getPlaneElement(), targetElement);
				bpmnEdge.setTargetElement(de);
				
				// the source and target elements should already have been created:
				// we know the PictogramElements for these can be found in our elements map
				Shape sourceShape = (Shape)elements.get(sourceElement);
				Shape targetShape = (Shape)elements.get(targetElement);
				if (sourceShape!=null && targetShape!=null) {
					Tuple<FixPointAnchor,FixPointAnchor> anchors =
							AnchorUtil.getSourceAndTargetBoundaryAnchors(sourceShape, targetShape, null);
					org.eclipse.graphiti.mm.algorithms.styles.Point sourceLoc = GraphicsUtil.createPoint(anchors.getFirst());
					org.eclipse.graphiti.mm.algorithms.styles.Point targetLoc = GraphicsUtil.createPoint(anchors.getSecond());
					Point point = DcFactory.eINSTANCE.createPoint();
					point.setX(sourceLoc.getX());
					point.setY(sourceLoc.getY());
					bpmnEdge.getWaypoint().add(point);
			
					point = DcFactory.eINSTANCE.createPoint();
					point.setX(targetLoc.getX());
					point.setY(targetLoc.getY());
					bpmnEdge.getWaypoint().add(point);
					
					plane.getPlaneElement().add(bpmnEdge);
					
					ModelUtil.setID(bpmnEdge);
					importer.importConnection(bpmnEdge);
				}
			}
		}
		
		return bpmnEdge;
	}
}
