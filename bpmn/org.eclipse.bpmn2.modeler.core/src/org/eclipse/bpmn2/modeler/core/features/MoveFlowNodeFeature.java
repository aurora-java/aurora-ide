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
package org.eclipse.bpmn2.modeler.core.features;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.MessageFlow;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.modeler.core.Activator;
import org.eclipse.bpmn2.modeler.core.model.ModelHandler;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

// TODO: Auto-generated Javadoc
/**
 * The Class MoveFlowNodeFeature.
 */
public class MoveFlowNodeFeature extends DefaultMoveBPMNShapeFeature {

	private final List<Algorithm> algorithms;
	private AlgorithmContainer algorithmContainer;
	protected ModelHandler modelHandler;
	
	/** The context. */
	protected IMoveShapeContext context;
	
	/**
	 * Instantiates a new move flow node feature.
	 *
	 * @param fp the fp
	 */
	public MoveFlowNodeFeature(IFeatureProvider fp) {
		super(fp);
		algorithms = new ArrayList<MoveFlowNodeFeature.Algorithm>();
		algorithms.add(new FromLaneAlgorithm());
		algorithms.add(new ToLaneAlgorithm());
		algorithms.add(new FromParticipantAlgorithm());
		algorithms.add(new ToParticipantAlgorithm());
		algorithms.add(new FromFlowElementsContainerAlgorithm());
		algorithms.add(new ToFlowElementsContainerAlgorithm());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.DefaultMoveBPMNShapeFeature#canMoveShape(org.eclipse.graphiti.features.context.IMoveShapeContext)
	 */
	@Override
	public boolean canMoveShape(IMoveShapeContext context) {
		if (!(getBusinessObjectForPictogramElement(context.getShape()) instanceof FlowNode)) {
			return false;
		}

		algorithmContainer = getAlgorithmContainer(context);
		if (algorithmContainer.isEmpty()) {
			return onMoveAlgorithmNotFound(context);
		}

		modelHandler = ModelHandler.getInstance(getDiagram());
		return algorithmContainer.isMoveAllowed(getSourceBo(context), getTargetBo(context));
	}

	/**
	 * On move algorithm not found.
	 *
	 * @param context the context
	 * @return true, if successful
	 */
	protected boolean onMoveAlgorithmNotFound(IMoveShapeContext context) {
		return super.canMoveShape(context);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.DefaultMoveBPMNShapeFeature#moveShape(org.eclipse.graphiti.features.context.IMoveShapeContext)
	 */
	@Override
	public void moveShape(IMoveShapeContext context) {
		this.context = context;
		super.moveShape(context);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.DefaultMoveBPMNShapeFeature#postMoveShape(org.eclipse.graphiti.features.context.IMoveShapeContext)
	 */
	@Override
	protected void postMoveShape(IMoveShapeContext context) {
		Shape shape = context.getShape();
		if (!FeatureSupport.isLabelShape(shape)) {
			try {
				Object[] nodes = getAllBusinessObjectsForPictogramElement(shape);
				for (Object object : nodes) {
					if (object instanceof FlowNode && algorithmContainer!=null && !algorithmContainer.isEmpty()) {
						algorithmContainer.move(((FlowNode) object), getSourceBo(context), getTargetBo(context));
					}
				}
			} catch (Exception e) {
				Activator.logError(e);
			}
		}
		super.postMoveShape(context);
	}

	private Object getSourceBo(IMoveShapeContext context) {
		if (context.getSourceContainer().equals(getDiagram()))
			return modelHandler.getFlowElementContainer(context.getSourceContainer());
		return getBusinessObjectForPictogramElement(context.getSourceContainer());
	}

	private Object getTargetBo(IMoveShapeContext context) {
		if (context.getTargetContainer().equals(getDiagram())) {
			Object target = modelHandler.getFlowElementContainer(context.getTargetContainer());
			if (target==null) {
				// This handles the case where {@link #canMoveShape(IMoveShapeContext)} is called:
				// at this point there is no write transaction open yet on the EditingDomain
				// however,  if the target is the Diagram but no default Process exists yet
				// for that Diagram, then the move should be conditionally allowed.
				return Bpmn2Factory.eINSTANCE.createProcess();
			}
			return target;
		}
		return getBusinessObjectForPictogramElement(context.getTargetContainer());
	}

	private boolean isSourceParticipant(IMoveShapeContext context) {
		Object bo = getBusinessObjectForPictogramElement(context.getSourceContainer());
		return context.getSourceContainer().equals(getDiagram()) || (bo != null && bo instanceof Participant);
	}

	private boolean isSourceLane(IMoveShapeContext context) {
		Object bo = getBusinessObjectForPictogramElement(context.getSourceContainer());
		return bo != null && bo instanceof Lane;
	}

	protected boolean checkConnectionAfterMove(Connection c) {
		return true;
	}
	
	/**
	 * The Class AlgorithmContainer.
	 */
	class AlgorithmContainer {
		
		/** The from algorithm. */
		public Algorithm fromAlgorithm;
		
		/** The to algorithm. */
		public Algorithm toAlgorithm;

		/**
		 * Instantiates a new algorithm container.
		 *
		 * @param fromAlgorithm the from algorithm
		 * @param toAlgorithm the to algorithm
		 */
		public AlgorithmContainer(Algorithm fromAlgorithm, Algorithm toAlgorithm) {
			this.fromAlgorithm = fromAlgorithm;
			this.toAlgorithm = toAlgorithm;
		}

		/**
		 * Checks if is move allowed.
		 *
		 * @param source the source
		 * @param target the target
		 * @return true, if is move allowed
		 */
		boolean isMoveAllowed(Object source, Object target) {
			return fromAlgorithm.isMoveAllowed(source, target) && toAlgorithm.isMoveAllowed(source, target);
		}

		/**
		 * Move.
		 *
		 * @param node the node
		 * @param source the source
		 * @param target the target
		 */
		void move(FlowNode node, Object source, Object target) {
			fromAlgorithm.move(node, source, target);
			toAlgorithm.move(node, source, target);

			// If this was a multiselection, keep all of the connections that
			// were included in the move
			PictogramElement pes[] = getDiagramEditor().getSelectedPictogramElements();
			List<Connection> internalConnections = new ArrayList<Connection>();
			for (PictogramElement pe : pes) {
				if (pe instanceof AnchorContainer) {
					for (Anchor a : ((AnchorContainer)pe).getAnchors()) {
						for (Connection c : a.getIncomingConnections()) {
							internalConnections.add(c);
						}
						for (Connection c : a.getOutgoingConnections()) {
							internalConnections.add(c);
						}
					}
				}
			}
			
			List<Connection> externalConnections = new ArrayList<Connection>();
			for (Connection c : internalConnections) {
				boolean foundSource = false;
				boolean foundTarget = false;
				for (PictogramElement p : pes) {
					if (p==c.getStart().getParent())
						foundSource = true;
					if (p==c.getEnd().getParent())
						foundTarget = true;
				}
				if (!foundSource || !foundTarget)
					externalConnections.add(c);
			}
			internalConnections.removeAll(externalConnections);
			
			// If flow node was moved from one Pool to another, delete all
			// incoming and outgoing Sequence Flows; if flow node was connect
			// to another flow node by a Message Flow, and it is moved into the
			// same Pool as the other flow node, delete the Message Flows.
			List<Connection> connections = new ArrayList<Connection>();
			IFeatureProvider fp = MoveFlowNodeFeature.this.getFeatureProvider();
			Shape shape = context.getShape();
			for (Anchor a : shape.getAnchors()) {
				for (Connection c : a.getIncomingConnections()) {
					if (!internalConnections.contains(c) && !isConnectionValid(c))
						connections.add(c);
				}
				for (Connection c : a.getOutgoingConnections()) {
					if (!internalConnections.contains(c) && !isConnectionValid(c))
						connections.add(c);
				}
			}
			for (Connection c : connections) {
				DeleteContext dc = new DeleteContext(c);
				IDeleteFeature df = fp.getDeleteFeature(dc);
				df.delete(dc);
			}
		}

		/**
		 * Checks if is empty.
		 *
		 * @return true, if is empty
		 */
		boolean isEmpty() {
			return fromAlgorithm == null || toAlgorithm == null;
		}
		
		/**
		 * Checks if is connection valid.
		 *
		 * @param flow the flow
		 * @return true, if is connection valid
		 */
		boolean isConnectionValid(Connection c) {
			if (!MoveFlowNodeFeature.this.checkConnectionAfterMove(c))
				return true;

			BaseElement flow = BusinessObjectUtil.getFirstBaseElement(c);
			if (!(flow instanceof SequenceFlow || flow instanceof MessageFlow))
				return true;
			
			EStructuralFeature sourceRef = flow.eClass().getEStructuralFeature("sourceRef"); //$NON-NLS-1$
			EStructuralFeature targetRef = flow.eClass().getEStructuralFeature("targetRef"); //$NON-NLS-1$
			BaseElement source = (BaseElement) flow.eGet(sourceRef);
			BaseElement target = (BaseElement) flow.eGet(targetRef);
			EObject sourceContainer = source.eContainer();
			while (sourceContainer!=null) {
				if (sourceContainer instanceof FlowElementsContainer)
					break;
				sourceContainer = sourceContainer.eContainer();
			}
			if (sourceContainer==null)
				return true;
			EObject targetContainer = target.eContainer();
			while (targetContainer!=null) {
				if (targetContainer instanceof FlowElementsContainer)
					break;
				targetContainer = targetContainer.eContainer();
			}
			if (targetContainer==null)
				return true;
			if (flow instanceof SequenceFlow)
				return sourceContainer==targetContainer;
			if (flow instanceof MessageFlow)
				return sourceContainer!=targetContainer;
			return false;
		}
	}

	private AlgorithmContainer getAlgorithmContainer(IMoveShapeContext context) {
		Algorithm fromAlgorithm = null;
		Algorithm toAlgorithm = null;

		for (Algorithm a : algorithms) {
			if (a.canApplyTo(context)) {
				switch (a.getType()) {
				case Algorithm.TYPE_FROM:
					fromAlgorithm = a;
					break;
				case Algorithm.TYPE_TO:
					toAlgorithm = a;
					break;
				}
			}
		}

		return new AlgorithmContainer(fromAlgorithm, toAlgorithm);
	}

	/**
	 * The Interface Algorithm.
	 */
	interface Algorithm {

		/** The type from. */
		int TYPE_FROM = 0;

		/** The type to. */
		int TYPE_TO = 1;

		/**
		 * Gets the type.
		 *
		 * @return the type
		 */
		int getType();

		/**
		 * Can apply to.
		 *
		 * @param context the context
		 * @return true, if successful
		 */
		boolean canApplyTo(IMoveShapeContext context);

		/**
		 * Checks if is move allowed.
		 *
		 * @param source the source
		 * @param target the target
		 * @return true, if is move allowed
		 */
		boolean isMoveAllowed(Object source, Object target);

		/**
		 * Move.
		 *
		 * @param node the node
		 * @param source the source
		 * @param target the target
		 */
		void move(FlowNode node, Object source, Object target);
	}

	/**
	 * The Class DefaultAlgorithm.
	 */
	abstract class DefaultAlgorithm implements Algorithm {

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.MoveFlowNodeFeature.Algorithm#isMoveAllowed(java.lang.Object, java.lang.Object)
		 */
		@Override
		public boolean isMoveAllowed(Object source, Object target) {
			return true;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.MoveFlowNodeFeature.Algorithm#move(org.eclipse.bpmn2.FlowNode, java.lang.Object, java.lang.Object)
		 */
		@Override
		public void move(FlowNode node, Object source, Object target) {
			modelHandler.moveFlowNode(node, source, target);
		}
	}

	/**
	 * The Class FromLaneAlgorithm.
	 */
	class FromLaneAlgorithm extends DefaultAlgorithm {

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.MoveFlowNodeFeature.Algorithm#getType()
		 */
		@Override
		public int getType() {
			return TYPE_FROM;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.MoveFlowNodeFeature.Algorithm#canApplyTo(org.eclipse.graphiti.features.context.IMoveShapeContext)
		 */
		@Override
		public boolean canApplyTo(IMoveShapeContext context) {
			return isSourceLane(context);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.MoveFlowNodeFeature.DefaultAlgorithm#move(org.eclipse.bpmn2.FlowNode, java.lang.Object, java.lang.Object)
		 */
		@Override
		public void move(FlowNode node, Object source, Object target) {
			Lane lane = (Lane) source;
			lane.getFlowNodeRefs().remove(node);
			node.getLanes().remove(lane);
		}
	}

	/**
	 * The Class ToLaneAlgorithm.
	 */
	class ToLaneAlgorithm extends DefaultAlgorithm {

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.MoveFlowNodeFeature.Algorithm#getType()
		 */
		@Override
		public int getType() {
			return TYPE_TO;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.MoveFlowNodeFeature.Algorithm#canApplyTo(org.eclipse.graphiti.features.context.IMoveShapeContext)
		 */
		@Override
		public boolean canApplyTo(IMoveShapeContext context) {
			return FeatureSupport.isTargetLane(context);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.MoveFlowNodeFeature.DefaultAlgorithm#isMoveAllowed(java.lang.Object, java.lang.Object)
		 */
		@Override
		public boolean isMoveAllowed(Object source, Object target) {
			Lane lane = (Lane) target;
			return lane.getChildLaneSet() == null || lane.getChildLaneSet().getLanes().isEmpty();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.MoveFlowNodeFeature.DefaultAlgorithm#move(org.eclipse.bpmn2.FlowNode, java.lang.Object, java.lang.Object)
		 */
		@Override
		public void move(FlowNode node, Object source, Object target) {
			Lane lane = (Lane) target;
			lane.getFlowNodeRefs().add(node);
			node.getLanes().add(lane);
			super.move(node, source, target);
		}
	}

	/**
	 * The Class FromParticipantAlgorithm.
	 */
	class FromParticipantAlgorithm extends DefaultAlgorithm {

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.MoveFlowNodeFeature.Algorithm#getType()
		 */
		@Override
		public int getType() {
			return TYPE_FROM;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.MoveFlowNodeFeature.Algorithm#canApplyTo(org.eclipse.graphiti.features.context.IMoveShapeContext)
		 */
		@Override
		public boolean canApplyTo(IMoveShapeContext context) {
			return isSourceParticipant(context);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.MoveFlowNodeFeature.DefaultAlgorithm#move(org.eclipse.bpmn2.FlowNode, java.lang.Object, java.lang.Object)
		 */
		@Override
		public void move(FlowNode node, Object source, Object target) {
			// DO NOTHING HERE
		}
	}

	/**
	 * The Class ToParticipantAlgorithm.
	 */
	class ToParticipantAlgorithm extends DefaultAlgorithm {

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.MoveFlowNodeFeature.Algorithm#getType()
		 */
		@Override
		public int getType() {
			return TYPE_TO;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.MoveFlowNodeFeature.Algorithm#canApplyTo(org.eclipse.graphiti.features.context.IMoveShapeContext)
		 */
		@Override
		public boolean canApplyTo(IMoveShapeContext context) {
			return context.getTargetContainer().equals(getDiagram()) || FeatureSupport.isTargetParticipant(context);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.MoveFlowNodeFeature.DefaultAlgorithm#isMoveAllowed(java.lang.Object, java.lang.Object)
		 */
		@Override
		public boolean isMoveAllowed(Object source, Object target) {
			try {
				if (source==target)
					return true;
				if (target instanceof Participant) {
					Participant p = (Participant) target;
					if (p.equals(modelHandler.getInternalParticipant())) {
						return true;
					}
					if (p.getProcessRef() == null) {
						return true;
					}
					if (p.getProcessRef().getLaneSets().isEmpty()) {
						return true;
					}
				}
				else if (target instanceof FlowElementsContainer) {
					FlowElementsContainer p = (FlowElementsContainer) target;
					if (p.getLaneSets().isEmpty()) {
						return true;
					}
				}
			} catch (Exception e) {
				Activator.logError(e);
			}
			return false;
		}
		
		// TODO: I have no idea what this was supposed to do.
		// This is preventing the move of a shape out of a FlowElementsContainer 
//		@Override
//		public void move(FlowNode node, Object source, Object target) {
//			try {
//				if (ModelHandler.getInstance(node).getInternalParticipant() == null){ // this is not a collaboration, don't move 
//					return;
//				}else{
//					super.move(node, source, target);
//				}
//			} catch (IOException e) {
//				Activator.logError(e);
//			}
//		}
	}

	/**
	 * The Class FromFlowElementsContainerAlgorithm.
	 */
	class FromFlowElementsContainerAlgorithm extends DefaultAlgorithm {

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.MoveFlowNodeFeature.Algorithm#getType()
		 */
		@Override
		public int getType() {
			return TYPE_FROM;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.MoveFlowNodeFeature.Algorithm#canApplyTo(org.eclipse.graphiti.features.context.IMoveShapeContext)
		 */
		@Override
		public boolean canApplyTo(IMoveShapeContext context) {
			Object bo = getBusinessObjectForPictogramElement(context.getSourceContainer());
			return bo != null && bo instanceof FlowElementsContainer;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.MoveFlowNodeFeature.DefaultAlgorithm#move(org.eclipse.bpmn2.FlowNode, java.lang.Object, java.lang.Object)
		 */
		@Override
		public void move(FlowNode node, Object source, Object target) {
		}
	}

	/**
	 * The Class ToFlowElementsContainerAlgorithm.
	 */
	class ToFlowElementsContainerAlgorithm extends DefaultAlgorithm {

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.MoveFlowNodeFeature.Algorithm#getType()
		 */
		@Override
		public int getType() {
			return TYPE_TO;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.MoveFlowNodeFeature.Algorithm#canApplyTo(org.eclipse.graphiti.features.context.IMoveShapeContext)
		 */
		@Override
		public boolean canApplyTo(IMoveShapeContext context) {
			Object bo = getBusinessObjectForPictogramElement(context.getTargetContainer());
			return bo != null && bo instanceof FlowElementsContainer;
		}
	}
}