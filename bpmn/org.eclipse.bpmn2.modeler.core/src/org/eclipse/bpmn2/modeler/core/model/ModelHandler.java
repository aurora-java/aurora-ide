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
package org.eclipse.bpmn2.modeler.core.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.Artifact;
import org.eclipse.bpmn2.Association;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Choreography;
import org.eclipse.bpmn2.ChoreographyActivity;
import org.eclipse.bpmn2.ChoreographyTask;
import org.eclipse.bpmn2.Collaboration;
import org.eclipse.bpmn2.ConversationLink;
import org.eclipse.bpmn2.ConversationNode;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.InteractionNode;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.LaneSet;
import org.eclipse.bpmn2.MessageFlow;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.di.BpmnDiFactory;
import org.eclipse.bpmn2.di.ParticipantBandKind;
import org.eclipse.bpmn2.modeler.core.Messages;
import org.eclipse.bpmn2.modeler.core.di.ImportDiagnostics;
import org.eclipse.bpmn2.modeler.core.features.containers.participant.AddParticipantFeature;
import org.eclipse.bpmn2.modeler.core.preferences.Bpmn2Preferences;
import org.eclipse.bpmn2.modeler.core.preferences.ShapeStyle;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FixDuplicateIdsDialog;
import org.eclipse.bpmn2.modeler.core.utils.ShapeDecoratorUtil;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil.Bpmn2DiagramType;
import org.eclipse.bpmn2.modeler.core.utils.Tuple;
import org.eclipse.bpmn2.util.Bpmn2ResourceImpl;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.dc.DcFactory;
import org.eclipse.dd.dc.Point;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.IllegalValueException;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

public class ModelHandler {

	Bpmn2ResourceImpl resource;
	Bpmn2Preferences prefs;
	
	ModelHandler() {
	}

	void createDefinitionsIfMissing() {
		EList<EObject> contents = resource.getContents();

		if (contents.isEmpty() || !(contents.get(0) instanceof DocumentRoot)) {
			TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(resource);

			if (domain != null) {
				final DocumentRoot docRoot = create(DocumentRoot.class);
				final Definitions definitions = create(Definitions.class);

				domain.getCommandStack().execute(new RecordingCommand(domain) {
					@Override
					protected void doExecute() {
						docRoot.setDefinitions(definitions);
						resource.getContents().add(docRoot);
					}
				});
				return;
			}
		}
	}

	public Bpmn2Preferences getPreferences() {
		if (prefs==null)
			prefs = Bpmn2Preferences.getInstance(resource);
		return prefs;
	}
	
	public BPMNDiagram createDiagramType(final Bpmn2DiagramType diagramType, String targetNamespace) {
		BPMNDiagram diagram = null;
		switch (diagramType) {
		case PROCESS:
			diagram = createProcessDiagram(Messages.ModelHandler_Default);
			break;
		case COLLABORATION:
			diagram = createCollaborationDiagram(Messages.ModelHandler_Default);
			break;
		case CHOREOGRAPHY:
			diagram = createChoreographyDiagram(Messages.ModelHandler_Default);
			break;
		}
		if (diagram!=null)
			((Definitions)diagram.eContainer()).setTargetNamespace(targetNamespace);
		
		return diagram;
	}
	
	public BPMNDiagram createProcessDiagram(final String name) {
	
		EList<EObject> contents = resource.getContents();
		ResourceSet rs = resource.getResourceSet();
		TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(resource);
		final BPMNDiagram bpmnDiagram = BpmnDiFactory.eINSTANCE.createBPMNDiagram();

		if (domain != null) {
			domain.getCommandStack().execute(new RecordingCommand(domain) {
				@Override
				protected void doExecute() {
					BPMNPlane plane = BpmnDiFactory.eINSTANCE.createBPMNPlane();
					ModelUtil.setID(plane,resource);

					Process process = createProcess();
					process.setName(name+Messages.ModelHandler_Process);
					// the Process ID should be the same as the resource name
					String filename = resource.getURI().lastSegment();
					if (filename.contains(".")) //$NON-NLS-1$
						filename = filename.split("\\.")[0]; //$NON-NLS-1$
					process.setId( ModelUtil.generateID(process,resource,filename) );

					// create StartEvent
					StartEvent startEvent = create(StartEvent.class);
//					startEvent.setName("Start Event");
					process.getFlowElements().add(startEvent);
					
					// create SequenceFlow
					SequenceFlow flow = create(SequenceFlow.class);
					process.getFlowElements().add(flow);
					
					// create EndEvent
					EndEvent endEvent = create(EndEvent.class);
//					endEvent.setName("End Event");
					process.getFlowElements().add(endEvent);
					
					// hook 'em up
					startEvent.getOutgoing().add(flow);
					endEvent.getIncoming().add(flow);
					flow.setSourceRef(startEvent);
					flow.setTargetRef(endEvent);

					// create DI shapes
					BPMNShape shape = BpmnDiFactory.eINSTANCE.createBPMNShape();
					ModelUtil.setID(shape,resource);

					// StartEvent shape
					shape.setBpmnElement(startEvent);
					Bounds bounds = DcFactory.eINSTANCE.createBounds();
					bounds.setX(100);
					bounds.setY(100);
					bounds.setWidth(ShapeDecoratorUtil.EVENT_SIZE);
					bounds.setHeight(ShapeDecoratorUtil.EVENT_SIZE);
					shape.setBounds(bounds);
					plane.getPlaneElement().add(shape);
					getPreferences().applyBPMNDIDefaults(shape, null);
					
					// SequenceFlow edge
					BPMNEdge edge = BpmnDiFactory.eINSTANCE.createBPMNEdge();
					edge.setBpmnElement(flow);
					edge.setSourceElement(shape);
					
					Point wp = DcFactory.eINSTANCE.createPoint();
					wp.setX(100+ShapeDecoratorUtil.EVENT_SIZE);
					wp.setY(100+ShapeDecoratorUtil.EVENT_SIZE/2);
					edge.getWaypoint().add(wp);
					
					wp = DcFactory.eINSTANCE.createPoint();
					wp.setX(500);
					wp.setY(100+ShapeDecoratorUtil.EVENT_SIZE/2);
					edge.getWaypoint().add(wp);
					
					plane.getPlaneElement().add(edge);

					// EndEvent shape
					shape = BpmnDiFactory.eINSTANCE.createBPMNShape();
					ModelUtil.setID(shape,resource);

					shape.setBpmnElement(endEvent);
					bounds = DcFactory.eINSTANCE.createBounds();
					bounds.setX(500);
					bounds.setY(100);
					bounds.setWidth(ShapeDecoratorUtil.EVENT_SIZE);
					bounds.setHeight(ShapeDecoratorUtil.EVENT_SIZE);
					shape.setBounds(bounds);
					plane.getPlaneElement().add(shape);
					getPreferences().applyBPMNDIDefaults(shape, null);

					edge.setTargetElement(shape);
					
					// add to BPMNDiagram
					plane.setBpmnElement(process);
					bpmnDiagram.setPlane(plane);
					bpmnDiagram.setName(name+Messages.ModelHandler_Process_Diagram);
					getDefinitions().getDiagrams().add(bpmnDiagram);
				}
			});
		}
		return bpmnDiagram;
	}

	public BPMNDiagram createCollaborationDiagram(final String name) {
	
		EList<EObject> contents = resource.getContents();
		final Bpmn2Preferences preferences = Bpmn2Preferences.getInstance(resource);

		TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(resource);
		final BPMNDiagram bpmnDiagram = BpmnDiFactory.eINSTANCE.createBPMNDiagram();

		if (domain != null) {
			domain.getCommandStack().execute(new RecordingCommand(domain) {
				@Override
				protected void doExecute() {
					List<BPMNDiagram> diagrams = getAll(BPMNDiagram.class);
					BPMNPlane plane = BpmnDiFactory.eINSTANCE.createBPMNPlane();
					ModelUtil.setID(plane,resource);

					Collaboration collaboration = createCollaboration();
					collaboration.setName(name+Messages.ModelHandler_Collaboration);

					Process initiatingProcess = createProcess();
					initiatingProcess.setName(Messages.ModelHandler_Initiating_Process);
					initiatingProcess.setDefinitionalCollaborationRef(collaboration);
					
					Participant initiatingParticipant = create(Participant.class);
					initiatingParticipant.setName(Messages.ModelHandler_Initiating_Pool);
					initiatingParticipant.setProcessRef(initiatingProcess);
					
					Process nonInitiatingProcess = createProcess();
					nonInitiatingProcess.setName(Messages.ModelHandler_Non_Initiating_Process);
					nonInitiatingProcess.setDefinitionalCollaborationRef(collaboration);
					
					Participant nonInitiatingParticipant = create(Participant.class);
					nonInitiatingParticipant.setName(Messages.ModelHandler_Non_Initiating_Pool);
					nonInitiatingParticipant.setProcessRef(nonInitiatingProcess);
					
					collaboration.getParticipants().add(initiatingParticipant);
					collaboration.getParticipants().add(nonInitiatingParticipant);

					// create DI shapes

					boolean horz = getPreferences().isHorizontalDefault();
					// initiating pool
					BPMNShape shape = BpmnDiFactory.eINSTANCE.createBPMNShape();
					ModelUtil.setID(shape,resource);

					ShapeStyle ss = preferences.getShapeStyle(initiatingParticipant);

					shape.setBpmnElement(initiatingParticipant);
					Bounds bounds = DcFactory.eINSTANCE.createBounds();
					if (horz) {
						bounds.setX(100);
						bounds.setY(100);
						bounds.setWidth(ss.getDefaultWidth());
						bounds.setHeight(ss.getDefaultHeight());
					}
					else {
						bounds.setX(100);
						bounds.setY(100);
						bounds.setWidth(ss.getDefaultHeight());
						bounds.setHeight(ss.getDefaultWidth());
					}
					shape.setBounds(bounds);
					shape.setIsHorizontal(horz);
					plane.getPlaneElement().add(shape);
					getPreferences().applyBPMNDIDefaults(shape, null);

					// non-initiating pool
					shape = BpmnDiFactory.eINSTANCE.createBPMNShape();
					ModelUtil.setID(shape,resource);

					shape.setBpmnElement(nonInitiatingParticipant);
					bounds = DcFactory.eINSTANCE.createBounds();
					if (horz) {
						bounds.setX(100);
						bounds.setY(350);
						bounds.setWidth(ss.getDefaultWidth());
						bounds.setHeight(ss.getDefaultHeight());
					}
					else {
						bounds.setX(350);
						bounds.setY(100);
						bounds.setWidth(ss.getDefaultHeight());
						bounds.setHeight(ss.getDefaultWidth());
					}
					shape.setBounds(bounds);
					shape.setIsHorizontal(horz);
					plane.getPlaneElement().add(shape);
					getPreferences().applyBPMNDIDefaults(shape, null);

					plane.setBpmnElement(collaboration);
					bpmnDiagram.setPlane(plane);
					bpmnDiagram.setName(name+Messages.ModelHandler_Collaboration_Diagram);
					getDefinitions().getDiagrams().add(bpmnDiagram);
				}
			});
		}
		return bpmnDiagram;
	}
	

	public BPMNDiagram createChoreographyDiagram(final String name) {
	
		EList<EObject> contents = resource.getContents();
		TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(resource);
		final BPMNDiagram bpmnDiagram = BpmnDiFactory.eINSTANCE.createBPMNDiagram();
		final Bpmn2Preferences preferences = Bpmn2Preferences.getInstance(resource);

		if (domain != null) {
			domain.getCommandStack().execute(new RecordingCommand(domain) {
				@Override
				protected void doExecute() {
					List<BPMNDiagram> diagrams = getAll(BPMNDiagram.class);
					BPMNPlane plane = BpmnDiFactory.eINSTANCE.createBPMNPlane();
					ModelUtil.setID(plane,resource);

					Choreography choreography = createChoreography();
					choreography.setName(name+Messages.ModelHandler_Choreography);
					
					Participant initiatingParticipant = create(Participant.class);
					initiatingParticipant.setName(name+Messages.ModelHandler_Initiating_Participant);

//					Process initiatingProcess = createProcess();
//					initiatingProcess.setName(name+" Initiating Process");
//					initiatingParticipant.setProcessRef(initiatingProcess);
					
					Participant nonInitiatingParticipant = create(Participant.class);
					nonInitiatingParticipant.setName(name+Messages.ModelHandler_Non_Initiating_Participant);

//					Process nonInitiatingProcess = createProcess();
//					nonInitiatingProcess.setName(name+" Non-initiating Process");
//					nonInitiatingParticipant.setProcessRef(nonInitiatingProcess);
					
					choreography.getParticipants().add(initiatingParticipant);
					choreography.getParticipants().add(nonInitiatingParticipant);
					
					ChoreographyTask task = create(ChoreographyTask.class);
					task.setName(name+Messages.ModelHandler_Choreography_Task);
					task.getParticipantRefs().add(initiatingParticipant);
					task.getParticipantRefs().add(nonInitiatingParticipant);
					task.setInitiatingParticipantRef(initiatingParticipant);
					choreography.getFlowElements().add(task);

					BPMNShape taskShape = BpmnDiFactory.eINSTANCE.createBPMNShape();
					ModelUtil.setID(taskShape,resource);

					ShapeStyle ss = preferences.getShapeStyle(task);
					int bandHeight = ss.getDefaultHeight() / 7;
					
					taskShape.setBpmnElement(task);
					Bounds bounds = DcFactory.eINSTANCE.createBounds();
					bounds.setX(100);
					bounds.setY(100);
					bounds.setWidth(ss.getDefaultWidth());
					bounds.setHeight(ss.getDefaultHeight());
					taskShape.setBounds(bounds);
					plane.getPlaneElement().add(taskShape);
					getPreferences().applyBPMNDIDefaults(taskShape, null);

					BPMNShape participantShape = BpmnDiFactory.eINSTANCE.createBPMNShape();
					ModelUtil.setID(participantShape,resource);
					participantShape.setBpmnElement(initiatingParticipant);
					participantShape.setChoreographyActivityShape(taskShape);
					participantShape.setParticipantBandKind(ParticipantBandKind.TOP_INITIATING);
					bounds = DcFactory.eINSTANCE.createBounds();
					bounds.setX(100);
					bounds.setY(100);
					bounds.setWidth(ss.getDefaultWidth());
					bounds.setHeight(bandHeight);
					participantShape.setBounds(bounds);
					plane.getPlaneElement().add(participantShape);
					getPreferences().applyBPMNDIDefaults(participantShape, null);

					participantShape = BpmnDiFactory.eINSTANCE.createBPMNShape();
					ModelUtil.setID(participantShape,resource);
					participantShape.setBpmnElement(nonInitiatingParticipant);
					participantShape.setChoreographyActivityShape(taskShape);
					participantShape.setParticipantBandKind(ParticipantBandKind.BOTTOM_NON_INITIATING);
					bounds = DcFactory.eINSTANCE.createBounds();
					bounds.setX(100);
					bounds.setY(100 + ss.getDefaultHeight() - bandHeight);
					bounds.setWidth(ss.getDefaultWidth());
					bounds.setHeight(bandHeight);
					participantShape.setBounds(bounds);
					plane.getPlaneElement().add(participantShape);
					getPreferences().applyBPMNDIDefaults(participantShape, null);

					plane.setBpmnElement(choreography);
					bpmnDiagram.setPlane(plane);
					getDefinitions().getDiagrams().add(bpmnDiagram);
				}
			});
		}
		return bpmnDiagram;
	}
	
	
	public static ModelHandler getInstance(EObject object) {
		return ModelHandlerLocator.getModelHandler(object.eResource());
	}

	public void dispose() {
		ModelHandlerLocator.dispose(this);
	}
	
	/**
	 * @param <T>
	 * @param target object that this element is being added to
	 * @param elem flow element to be added
	 * @return
	 */
	public <T extends FlowElement> T addFlowElement(Object target, T elem) {
		FlowElementsContainer container = getFlowElementContainer(target);
		container.getFlowElements().add(elem);
		return elem;
	}

	/**
	 * @param <A>
	 * @param target object that this artifact is being added to
	 * @param artifact artifact to be added
	 * @return
	 */
	public <T extends Artifact> T addArtifact(Object target, T artifact) {
		Process process = getOrCreateProcess(getParticipant(target));
		process.getArtifacts().add(artifact);
		return artifact;
	}

	public <T extends RootElement> T addRootElement(T element) {
		getDefinitions().getRootElements().add(element);
		return element;
	}

	public ItemAwareElement addDataInputOutput(Object target, ItemAwareElement element) {
		if (element instanceof DataOutput)
			getOrCreateIOSpecification(target).getDataOutputs().add((DataOutput)element);
		else if (element instanceof DataInput)
			getOrCreateIOSpecification(target).getDataInputs().add((DataInput)element);
		else
			return null;
		return element;
	}

	public ConversationNode addConversationNode(BPMNDiagram bpmnDiagram, ConversationNode conversationNode) {
		Collaboration collaboration = getParticipantContainer(bpmnDiagram);
		if (collaboration==null) {
			collaboration = getCollaboration();
			if (collaboration==null)
				collaboration = createCollaboration();
		}
		if (collaboration!=null)
			collaboration.getConversations().add(conversationNode);
		return conversationNode;
	}

	public Choreography addChoreographyActivity(BPMNDiagram bpmnDiagram, ChoreographyActivity choreographyActivity) {
		Collaboration collaboration = getParticipantContainer(bpmnDiagram);
		Choreography choreography = null;
		if (collaboration instanceof Choreography) {
			choreography = (Choreography) collaboration;
		}
		else {
			choreography = getChoreography();
			if (choreography==null)
				choreography = createChoreography();
		}
		choreography.getFlowElements().add(choreographyActivity);
		return choreography;
	}

	private InputOutputSpecification getOrCreateIOSpecification(Object target) {
		Process process = getOrCreateProcess(getParticipant(target));
		if (process.getIoSpecification() == null) {
			InputOutputSpecification ioSpec = create(InputOutputSpecification.class);
			process.setIoSpecification(ioSpec);
		}
		return process.getIoSpecification();
	}

	public void moveFlowNode(FlowNode node, Object source, Object target) {
		FlowElementsContainer sourceContainer = getFlowElementContainer(source);
		FlowElementsContainer targetContainer = getFlowElementContainer(target);
		if (sourceContainer!=targetContainer) {
			sourceContainer.getFlowElements().remove(node);
			targetContainer.getFlowElements().add(node);
			for (SequenceFlow flow : node.getOutgoing()) {
				sourceContainer.getFlowElements().remove(flow);
				targetContainer.getFlowElements().add(flow);
			}
		}
	}

	public Participant addParticipant(BPMNDiagram bpmnDiagram) {
		Participant participant = null;
		Collaboration collaboration = getParticipantContainer(bpmnDiagram);
		if (collaboration!=null) {
			participant = create(Participant.class);
			collaboration.getParticipants().add(participant);
		}
		return participant;
	}

	public Process createProcess() {
		Process process = create(Process.class);
		getDefinitions().getRootElements().add(process);
		return process;
	}
	
	public Process getOrCreateProcess(Participant participant) {
		if (participant==null) {
			participant = getInternalParticipant();
		}
		if (participant!=null && participant.getProcessRef()!=null) {
			return participant.getProcessRef();
		}
		
		Process process = null;
		
		if (participant == null) {
			List<Process> processes = getAll(Process.class);
			// not a collaboration, and only one process -> append it there
			process = processes.size() == 1 ? processes.get(0) : null; 
		}
		
		if (process == null) {
			process = create(Process.class);
			getDefinitions().getRootElements().add(process);
			if (participant!=null) {
				process.setName(participant.getName() + " Process");
				if (participant.eContainer() instanceof Collaboration) {
					process.setDefinitionalCollaborationRef((Collaboration)participant.eContainer());
				}
				participant.setProcessRef(process);
			}
		}

		return process;
	}

	public static Lane createLane(Lane targetLane) {
		Resource resource = targetLane.eResource();
		Lane lane = create(resource, Lane.class);

		if (targetLane.getChildLaneSet() == null) {
			targetLane.setChildLaneSet(create(resource, LaneSet.class));
		}

		LaneSet targetLaneSet = targetLane.getChildLaneSet();
		targetLaneSet.getLanes().add(lane);

		lane.getFlowNodeRefs().addAll(targetLane.getFlowNodeRefs());
		targetLane.getFlowNodeRefs().clear();

		return lane;
	}

	public Lane createLane(Object target) {
		Lane lane = create(Lane.class);
		FlowElementsContainer container = getFlowElementContainer(target);
		if (container.getLaneSets().isEmpty()) {
			LaneSet laneSet = create(LaneSet.class);
			laneSet.setName(Messages.ModelHandler_Lane_Set+ModelUtil.getIDNumber( laneSet.getId() ));
			container.getLaneSets().add(laneSet);
		}
		container.getLaneSets().get(0).getLanes().add(lane);
		return lane;
	}

	public SequenceFlow createSequenceFlow(FlowNode source, FlowNode target) {
		SequenceFlow sequenceFlow = create(SequenceFlow.class);

		addFlowElement(source.eContainer(), sequenceFlow);
		sequenceFlow.setSourceRef(source);
		sequenceFlow.setTargetRef(target);
		return sequenceFlow;
	}

	public MessageFlow createMessageFlow(InteractionNode source, InteractionNode target) {
		MessageFlow messageFlow = null;
		Participant participant = getParticipant(source);
		if (participant!=null) {
			messageFlow = create(MessageFlow.class);
			messageFlow.setSourceRef(source);
			messageFlow.setTargetRef(target);
			if (participant.eContainer() instanceof Collaboration)
				((Collaboration)participant.eContainer()).getMessageFlows().add(messageFlow);
		}
		return messageFlow;
	}

	public ConversationLink createConversationLink(InteractionNode source, InteractionNode target) {
		ConversationLink link = null;
		Participant participant = getParticipant(source);
		if (participant!=null) {
			link = create(ConversationLink.class);
			link.setSourceRef(source);
			link.setTargetRef(target);
			if (participant.eContainer() instanceof Collaboration)
				((Collaboration)participant.eContainer()).getConversationLinks().add(link);
		}
		return link;
	}

	public Association createAssociation(BaseElement source, BaseElement target) {
		BaseElement e = null;
		if (getParticipant(source) != null) {
			e = source;
		} else if (getParticipant(target) != null) {
			e = target;
		} else {
			e = getInternalParticipant();
		}
		Association association = create(Association.class);
		addArtifact(e, association);
		association.setSourceRef(source);
		association.setTargetRef(target);
		return association;
	}

	private Collaboration getCollaboration() {
		final List<RootElement> rootElements = getDefinitions().getRootElements();

		for (RootElement element : rootElements) {
			if (element instanceof Collaboration) {
				return (Collaboration) element;
			}
		}
		return null;
	}
	
	public Collaboration createCollaboration() {
		Collaboration collaboration = create(Collaboration.class);
		getDefinitions().getRootElements().add(collaboration);
		return collaboration;
	}
	
	private Collaboration getOrCreateCollaboration() {
		Collaboration c = getCollaboration();
		if (c!=null)
			return c;
		
		final List<RootElement> rootElements = getDefinitions().getRootElements();
		TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(resource);
		final Collaboration collaboration = create(Collaboration.class);
		if (domain != null) {
			domain.getCommandStack().execute(new RecordingCommand(domain) {

				@Override
				protected void doExecute() {
					addCollaborationToRootElements(rootElements, collaboration);
				}
			});
		}
		return collaboration;
	}
	
	private Collaboration getParticipantContainer(BPMNDiagram bpmnDiagram) {
		if (bpmnDiagram==null) {
			// return the first Collaboration or Choreography in the model hierarchy
			List<RootElement> rootElements = getDefinitions(resource).getRootElements();
			for (RootElement element : rootElements) {
				// yeah, Collaboration and Choreography are both instanceof Collaboration...
				if (element instanceof Collaboration || element instanceof Choreography) {
					return (Collaboration)element;
				}
			}
		}
		else {
			BaseElement be = bpmnDiagram.getPlane().getBpmnElement();
			if (be instanceof Collaboration || be instanceof Choreography) {
				return (Collaboration)be;
			}
		}
		return null;
	}
	
	public Choreography getChoreography() {
		for (RootElement re : getDefinitions().getRootElements()) {
			if (re instanceof Choreography)
				return (Choreography)re;
		}
		return null;
	}
	
	public Choreography createChoreography() {
		Choreography choreography = create(Choreography.class);
		getDefinitions().getRootElements().add(choreography);
		return choreography;
	}

	private void addCollaborationToRootElements(final List<RootElement> rootElements, final Collaboration collaboration) {
		Participant participant = create(Participant.class);
		for (RootElement element : rootElements) {
			if (element instanceof Process) {
				participant.setProcessRef((Process) element);
				break;
			}
		}
		collaboration.getParticipants().add(participant);
		rootElements.add(collaboration);
	}

	private void addChoreographyToRootElements(final List<RootElement> rootElements, final Choreography choreography) {
		Participant participant = create(Participant.class);
		for (RootElement element : rootElements) {
			if (element instanceof Process) {
				participant.setProcessRef((Process) element);
				break;
			}
		}
		choreography.getParticipants().add(participant);
		rootElements.add(choreography);
	}

	public Bpmn2ResourceImpl getResource() {
		return resource;
	}

	public Definitions getDefinitions() {
		return getDefinitions(resource);
	}
	
	public static Definitions getDefinitions(Resource resource) {
		return (Definitions) resource.getContents().get(0).eContents().get(0);
	}

	// TODO: Move all of this model handler crap into BPMN2PersistencyBehavior where it belongs
	void loadResource() {
		try {
			resource.load(null);
			List<Tuple<EObject,EObject>> dups = ModelUtil.findDuplicateIds(resource);
			if (dups.size()>0) {
				FixDuplicateIdsDialog dlg = new FixDuplicateIdsDialog(dups);
				dlg.open();
			}
		} catch (Exception e) {
			if (!resource.getErrors().isEmpty()) {
				ImportDiagnostics diagnostics = new ImportDiagnostics(resource);
				for (Resource.Diagnostic error : resource.getErrors()) {
					if (error instanceof IllegalValueException) {
						IllegalValueException wrappedException = (IllegalValueException) error;
						IllegalValueException iv = (IllegalValueException) wrappedException;
						
						String stringValue;
						Object value = iv.getValue();
						if (value instanceof EObject)
							stringValue = diagnostics.getText((EObject)value);
						else
							stringValue = "\"" + value.toString() + "\""; //$NON-NLS-1$ //$NON-NLS-2$
						
						String message = Messages.ModelHandler_20 +
								stringValue +
								Messages.ModelHandler_21 +
								"\"" + iv.getFeature().getName() + "\""; //$NON-NLS-1$ //$NON-NLS-2$
								
						diagnostics.add(IStatus.ERROR, iv.getObject(), message);
					} else {
						EObject contents = resource.getContents().get(0);
						diagnostics.add(IStatus.ERROR, contents, error.getMessage());
					}
				}
				diagnostics.report();
			}
		}
	}

	public Participant getInternalParticipant() {
		Collaboration collaboration = getParticipantContainer(null);
		if (collaboration!=null && collaboration.getParticipants().size()>0) {
			return collaboration.getParticipants().get(0);
		}
		return null;
	}

	public FlowElementsContainer getFlowElementContainer(Object o) {
		if (o == null) {
			return getOrCreateProcess(getInternalParticipant());
		}
		if (o instanceof Diagram) {
	        o = BusinessObjectUtil.getFirstElementOfType((Diagram)o, BPMNDiagram.class);
		}
		if (o instanceof BPMNDiagram) {
			BPMNDiagram bpmnDiagram = (BPMNDiagram) o;
			BaseElement be = bpmnDiagram.getPlane().getBpmnElement();
			if (be != null && be instanceof FlowElementsContainer) {
				return (FlowElementsContainer)be;
			}
			else {
				// find an elligible Process for this FlowElement,
				// one that is not referenced by a Pool
				List<Participant> pools = getAll(Participant.class);
				for (Process process : getAll(Process.class)) {
					boolean isProcessForPool = false;
					for (Participant pool : pools) {
						if (pool.getProcessRef() == process) {
							isProcessForPool = true;
							break;
						}
					}
					if (!isProcessForPool)
						return process;
				}
				// create a default Process.
				// The BPMNDiagram now becomes a Process Diagram
				try {
					Process process = create(Process.class);
					bpmnDiagram.getPlane().setBpmnElement(process);
					return process;
				}
				catch (IllegalStateException e) {
				}
				return null;
			}
		}
		if (o instanceof Participant) {
			return getOrCreateProcess((Participant) o);
		}
		if (o instanceof SubProcess) {
			return (FlowElementsContainer) o;
		}
		return findElementOfType(FlowElementsContainer.class, o);
	}

	public Participant getParticipant(final Object o) {
		if (o == null) {
			return getInternalParticipant();
		}
		
		if (o instanceof Diagram) {
	        BPMNDiagram bpmnDiagram = BusinessObjectUtil.getFirstElementOfType((Diagram)o, BPMNDiagram.class);
	        Collaboration collaboration = getParticipantContainer(bpmnDiagram);
			if (collaboration!=null && collaboration.getParticipants().size()>0) {
				return collaboration.getParticipants().get(0);
			}
			return null;
		}

		Object object = o;
		if (o instanceof Shape) {
			object = BusinessObjectUtil.getFirstElementOfType((PictogramElement) o, BaseElement.class);
		}

		if (object instanceof Participant) {
			return (Participant) object;
		}

		Process process = findElementOfType(Process.class, object);
		
		Collaboration collaboration = getParticipantContainer(null);
		if (collaboration!=null) {
			if (process==null) {
				if (collaboration.getParticipants().size()>0)
					return collaboration.getParticipants().get(0);
			}
			else {
				for (Participant p : collaboration.getParticipants()) {
					if (p.getProcessRef() != null && p.getProcessRef().equals(process)) {
						return p;
					}
				}
			}
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends BaseElement> T findElementOfType(Class<T> clazz, Object from) {
		if (!(from instanceof BaseElement)) {
			return null;
		}

		if (clazz.isAssignableFrom(from.getClass())) {
			return (T) from;
		}

		return findElementOfType(clazz, ((BaseElement) from).eContainer());
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getAll(final Class<T> class1) {
		return getAll(this.resource, class1);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> getAll(Resource resource, final Class<T> class1) {
		ArrayList<T> l = new ArrayList<T>();
		TreeIterator<EObject> contents = resource.getAllContents();
		while (contents.hasNext()) {
			Object t = contents.next();
			if (class1.isInstance(t)) {
				l.add((T) t);
			}
		}
		return l;
	}

	public BaseElement findElement(String id) {
		if (id==null || id.isEmpty())
			return null;
		
		List<BaseElement> baseElements = getAll(BaseElement.class);

		for (BaseElement be : baseElements) {
			if (id.equals(be.getId())) {
				return be;
			}
		}

		return null;
	}
	
	/**
	 * General-purpose factory method that sets appropriate default values for new objects.
	 */
	public EObject create(EClass eClass) {
		return create(this.resource, eClass);
	}

	public <T extends EObject> T create(Class<T> clazz) {
		return (T) create(this.resource, clazz);
	}
	
	////////////////////////////////////////////////////////////////////////////
	// static versions of the above, for convenience
	
	public static EObject create(Resource resource, EClass eClass) {
		return Bpmn2ModelerFactory.create(resource, eClass);
	}

	public static <T extends EObject> T create(Resource resource, Class<T> clazz) {
		return (T) Bpmn2ModelerFactory.create(resource, clazz);
	}
}