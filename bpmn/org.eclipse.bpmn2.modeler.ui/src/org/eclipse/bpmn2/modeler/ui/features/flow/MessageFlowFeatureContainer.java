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
package org.eclipse.bpmn2.modeler.ui.features.flow;

import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.InteractionNode;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.MessageFlow;
import org.eclipse.bpmn2.Operation;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.ReceiveTask;
import org.eclipse.bpmn2.SendTask;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.di.DIImport;
import org.eclipse.bpmn2.modeler.core.features.BaseElementConnectionFeatureContainer;
import org.eclipse.bpmn2.modeler.core.features.DefaultDeleteBPMNShapeFeature;
import org.eclipse.bpmn2.modeler.core.features.MultiUpdateFeature;
import org.eclipse.bpmn2.modeler.core.features.choreography.ChoreographyUtil;
import org.eclipse.bpmn2.modeler.core.features.flow.AbstractAddFlowFeature;
import org.eclipse.bpmn2.modeler.core.features.flow.AbstractCreateFlowFeature;
import org.eclipse.bpmn2.modeler.core.features.flow.AbstractReconnectFlowFeature;
import org.eclipse.bpmn2.modeler.core.features.label.UpdateLabelFeature;
import org.eclipse.bpmn2.modeler.core.model.ModelHandler;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.core.utils.StyleUtil;
import org.eclipse.bpmn2.modeler.ui.ImageProvider;
import org.eclipse.bpmn2.modeler.ui.features.data.MessageFeatureContainer;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.IReconnectionFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.AreaContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeService;
import org.eclipse.graphiti.util.IColorConstant;

public class MessageFlowFeatureContainer extends BaseElementConnectionFeatureContainer {

	public final static String MESSAGE_REF = "message.ref"; //$NON-NLS-1$
	final static IPeService peService = Graphiti.getPeService();
	final static IGaService gaService = Graphiti.getGaService();

	public Object getApplyObject(IContext context) {
		Object object = super.getApplyObject(context);
		if (context instanceof IPictogramElementContext) {
			PictogramElement pe = ((IPictogramElementContext) context).getPictogramElement();
			if (ChoreographyUtil.isChoreographyMessageLink(pe))
				return null;
			MessageFlow mf = getMessageFlow(pe);
			if (mf!=null)
				return mf;
		}
		return object;
	}
	
	@Override
	public boolean canApplyTo(Object o) {
		return super.canApplyTo(o) && o instanceof MessageFlow;
	}

	@Override
	public IAddFeature getAddFeature(IFeatureProvider fp) {
		return new AddMessageFlowFeature(fp);
	}

	@Override
	public ICreateConnectionFeature getCreateConnectionFeature(IFeatureProvider fp) {
		return new CreateMessageFlowFeature(fp);
	}

	@Override
	public IDeleteFeature getDeleteFeature(final IFeatureProvider fp) {
		return new DeleteMessageFromMessageFlowFeature(fp);
	}

	@Override
	public IUpdateFeature getUpdateFeature(IFeatureProvider fp) {
		MultiUpdateFeature multiUpdate = new MultiUpdateFeature(fp);
		multiUpdate.addFeature(new UpdateMessageFlowFeature(fp));
		multiUpdate.addFeature(new UpdateLabelFeature(fp));
		return multiUpdate;
	}
	
	@Override
	public IReconnectionFeature getReconnectionFeature(IFeatureProvider fp) {
		return new ReconnectMessageFlowFeature(fp);
	}
	
	public static ContainerShape findMessageShape(Connection connection) {
		ConnectionDecorator d = findMessageDecorator(connection);
		if (d!=null) {
			return BusinessObjectUtil.getFirstElementOfType(d, ContainerShape.class);
		}
		return null;
	}
	
	public static MessageFlow getMessageFlow(PictogramElement pe) {
		if (pe instanceof ContainerShape) {
			String id = peService.getPropertyValue(pe, MESSAGE_REF);
			if (id!=null && !id.isEmpty()) {
				EObject o = pe.eContainer();
				while (!(o instanceof Diagram)) {
					o = o.eContainer();
				}
				if (o instanceof Diagram) {
					Diagram diagram = (Diagram) o;
					for (Connection connection : diagram.getConnections()) {
						MessageFlow messageFlow = BusinessObjectUtil.getFirstElementOfType(connection, MessageFlow.class);
						if (messageFlow!=null) {
							if (id.equals(messageFlow.getId()))
								return messageFlow;
						}
					}
				}
			}
		}
		else if (pe instanceof Connection) {
			MessageFlow messageFlow = BusinessObjectUtil.getFirstElementOfType(pe, MessageFlow.class);
			return messageFlow;
		}
		else if (pe instanceof ConnectionDecorator) {
			pe = ((ConnectionDecorator)pe).getConnection();
			MessageFlow messageFlow = BusinessObjectUtil.getFirstElementOfType(pe, MessageFlow.class);
			return messageFlow;
		}
		return null;
	}
	
	protected static String messageToString(Message message) {
		if (message==null)
			return ""; //$NON-NLS-1$
		return message.getId();
	}
	
	protected static Connection getMessageFlowConnection(PictogramElement pe) {
		if (pe instanceof ContainerShape) {
			String id = peService.getPropertyValue(pe, MESSAGE_REF);
			if (id!=null && !id.isEmpty()) {
				EObject o = pe.eContainer();
				while (!(o instanceof Diagram)) {
					o = o.eContainer();
				}
				if (o instanceof Diagram) {
					Diagram diagram = (Diagram) o;
					for (Connection connection : diagram.getConnections()) {
						MessageFlow messageFlow = BusinessObjectUtil.getFirstElementOfType(connection, MessageFlow.class);
						if (messageFlow!=null) {
							if (id.equals(messageFlow.getId()))
								return connection;
						}
					}
				}
			}
		}
		else if (pe instanceof Connection) {
			MessageFlow messageFlow = BusinessObjectUtil.getFirstElementOfType(pe, MessageFlow.class);
			if (messageFlow!=null)
				return (Connection)pe;
		}
		else if (pe instanceof ConnectionDecorator) {
			pe = ((ConnectionDecorator)pe).getConnection();
			MessageFlow messageFlow = BusinessObjectUtil.getFirstElementOfType(pe, MessageFlow.class);
			if (messageFlow!=null)
				return (Connection)pe;
		}
		return null;
	}
	
	protected static ConnectionDecorator findMessageDecorator(Connection connection) {
		for (ConnectionDecorator d : connection.getConnectionDecorators()) {
			if (Graphiti.getPeService().getPropertyValue(d, MESSAGE_REF) != null) {
				return d;
			}
		}
		return null;
	}
	
	protected static boolean messageDecoratorMoved(Connection connection) {
		ContainerShape messageShape = findMessageShape(connection);
		if (messageShape!=null) {
			ILocation loc = peService.getConnectionMidpoint(connection, 0.25);
			int w = MessageFeatureContainer.ENVELOPE_WIDTH / 2;
			int h = MessageFeatureContainer.ENVELOPE_HEIGHT / 2;
			int x = loc.getX() - w;
			int y = loc.getY() - h;
			ILocation shapeLoc = peService.getLocationRelativeToDiagram(messageShape);
			return x != shapeLoc.getX() || y != shapeLoc.getY();
		}
		return false;
	}
	
	protected static void adjustMessageDecorator(IFeatureProvider fp, Connection connection) {
		ContainerShape messageShape = findMessageShape(connection);
		if (messageShape!=null) {
			// calculate new location: this will be 1/4 of the distance from start of the connection line
			ILocation loc = peService.getConnectionMidpoint(connection, 0.25);
			int w = MessageFeatureContainer.ENVELOPE_WIDTH / 2;
			int h = MessageFeatureContainer.ENVELOPE_HEIGHT / 2;
			int x = loc.getX() - w;
			int y = loc.getY() - h;
			MoveShapeContext moveContext = new MoveShapeContext(messageShape);
			moveContext.setX(x);
			moveContext.setY(y);
			IMoveShapeFeature moveFeature = fp.getMoveShapeFeature(moveContext);
			moveFeature.moveShape(moveContext);
		}
	}

	protected static void addMessageDecorator(IFeatureProvider fp, Connection connection, Message message, Shape messageShape) {
		ILocation loc = peService.getConnectionMidpoint(connection, 0.25);
		Diagram diagram = peService.getDiagramForPictogramElement(connection);
		ConnectionDecorator decorator = peService.createConnectionDecorator(connection, true, 0.25, true);
		MessageFlow messageFlow = BusinessObjectUtil.getFirstElementOfType(connection, MessageFlow.class);
		
		int w = MessageFeatureContainer.ENVELOPE_WIDTH / 2;
		int h = MessageFeatureContainer.ENVELOPE_HEIGHT / 2;
		int x = loc.getX() - w;
		int y = loc.getY() - h;
		if (messageShape==null) {
			AddContext addContext = new AddContext(new AreaContext(), message);
			addContext.putProperty(MessageFeatureContainer.IS_REFERENCE, Boolean.TRUE);
			addContext.setX(x);
			addContext.setY(y);
			addContext.setTargetContainer(diagram);
			IAddFeature addFeature = fp.getAddFeature(addContext);
			messageShape = (Shape) addFeature.add(addContext);
		}
		else {
			MoveShapeContext moveContext = new MoveShapeContext(messageShape);
			moveContext.setLocation(x, y);
			moveContext.setSourceContainer(messageShape.getContainer());
			moveContext.setTargetContainer(messageShape.getContainer());
			IMoveShapeFeature moveFeature = fp.getMoveShapeFeature(moveContext);
			moveFeature.moveShape(moveContext);
		}
		fp.link(decorator, new Object[] {message, messageShape});
		peService.setPropertyValue(decorator, MESSAGE_REF, "true"); //$NON-NLS-1$
		// Set our MessageFlow ID in the Message shape. Sadly Graphiti shape properties
		// can only hold Strings, so if the MessageFlow ID is null, we need to assign
		// a new one to it.
		String id = messageFlow.getId();
		if (id==null || id.isEmpty())
			id = ModelUtil.setID(messageFlow);
		peService.setPropertyValue(messageShape, MESSAGE_REF, id);
		messageFlow.setMessageRef(message);
	}
	
	protected static void removeMessageDecorator(IFeatureProvider fp, Connection connection) {
		ConnectionDecorator decorator = findMessageDecorator(connection);
		if (decorator!=null) {
			ContainerShape messageShape = findMessageShape(connection);
			if (messageShape!=null) {
				peService.removeProperty(messageShape, MESSAGE_REF);
				DeleteContext deleteContext = new DeleteContext(messageShape);
				IDeleteFeature deleteFeature = fp.getDeleteFeature(deleteContext);
				deleteFeature.delete(deleteContext);
			}
			peService.deletePictogramElement(decorator);
			MessageFlow mf = BusinessObjectUtil.getFirstElementOfType(connection, MessageFlow.class);
			mf.setMessageRef(null);
		}
	}

	public static class AddMessageFlowFeature extends AbstractAddFlowFeature<MessageFlow> {
		public AddMessageFlowFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		public boolean canAdd(IAddContext context) {
			if (context instanceof IAddConnectionContext) {
				IAddConnectionContext acc = (IAddConnectionContext) context;
				if (acc.getSourceAnchor() != null) {
					PictogramElement pe = acc.getSourceAnchor().getParent();
					Object source = BusinessObjectUtil.getFirstElementOfType(pe, BaseElement.class);
					if (source instanceof EndEvent) {
						// End Events can only be the source of Message Flow connections
						// if the End Event has a Message Event Definition
						List<EventDefinition> eventDefinitions = ((EndEvent) source).getEventDefinitions();
						for (EventDefinition eventDefinition : eventDefinitions) {
							if (eventDefinition instanceof MessageEventDefinition) {
								return true;
							}
						}
						return false;
					}
					if (source instanceof StartEvent) {
						return false;
					}
				}
			}
			return super.canAdd(context);
		}

		@Override
		public PictogramElement add(IAddContext context) {
			Connection connection = (Connection)super.add(context);
			if (!DIImport.isImporting(context)) {
				// take a SWAG at the Message type by looking at the source and target figures:
				// if the source is a SendTask, use the Message from the SendTask
				// if the target is a ReceiveTask, use the Message from that
				// if the source or target is a ServiceTask use the input or output Message from its Operation
				BaseElement source = BusinessObjectUtil.getFirstBaseElement(connection.getStart().getParent());
				BaseElement target = BusinessObjectUtil.getFirstBaseElement(connection.getEnd().getParent());
				Message message = null;
				if (source instanceof SendTask) {
					message = ((SendTask)source).getMessageRef(); 
				}
				else if (target instanceof ReceiveTask) {
					message = ((ReceiveTask)target).getMessageRef(); 
				}
				else if (source instanceof ServiceTask) {
					Operation operation = ((ServiceTask)source).getOperationRef();
					if (operation!=null)
						message = operation.getInMessageRef();
				}
				else if (target instanceof ServiceTask) {
					Operation operation = ((ServiceTask)target).getOperationRef();
					if (operation!=null)
						message = operation.getOutMessageRef();
				}
				
				if (message!=null) {
					// Set the Message type. The ExtendedPropertiesAdapter will
					// handle the setting of Message types in the source and target
					// nodes as required.
					MessageFlow mf = BusinessObjectUtil.getFirstElementOfType(connection, MessageFlow.class);
					ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(mf);
					adapter.getFeatureDescriptor(Bpmn2Package.eINSTANCE.getMessageFlow_MessageRef()).setValue(message);
				}
			}
			return connection;
		}

		@Override
		protected Polyline createConnectionLine(final Connection connection) {
			MessageFlow messageFlow = (MessageFlow) BusinessObjectUtil.getFirstBaseElement(connection);

			Polyline connectionLine = super.createConnectionLine(connection);
			connectionLine.setLineStyle(LineStyle.DASH);
			connectionLine.setLineWidth(2);

			ConnectionDecorator endDecorator = peService.createConnectionDecorator(connection, false, 1.0, true);
			ConnectionDecorator startDecorator = peService.createConnectionDecorator(connection, false, 0, true);
			
			int w = 5;
			int l = 10;
			
			Polyline arrowhead = gaService.createPolygon(endDecorator, new int[] { -l, w, 0, 0, -l, -w, -l, w });
			StyleUtil.applyStyle(arrowhead, messageFlow);
			arrowhead.setBackground(manageColor(IColorConstant.WHITE));

			Ellipse circle = gaService.createEllipse(startDecorator);
			gaService.setSize(circle, 10, 10);
			StyleUtil.applyStyle(circle, messageFlow);
			circle.setBackground(manageColor(IColorConstant.WHITE));
			
			return connectionLine;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2AddFeature#getBusinessObjectType()
		 */
		@Override
		public Class getBusinessObjectType() {
			return MessageFlow.class;
		}
	}

	public static class CreateMessageFlowFeature extends AbstractCreateFlowFeature<MessageFlow, InteractionNode, InteractionNode> {

		public CreateMessageFlowFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		public boolean isAvailable(IContext context) {
			if (!isModelObjectEnabled(Bpmn2Package.eINSTANCE.getMessageFlow()))
				return false;
			
			if (context instanceof ICreateConnectionContext) {
				ICreateConnectionContext ccc = (ICreateConnectionContext) context;
				if (ccc.getSourcePictogramElement() != null) {
					Object obj = BusinessObjectUtil.getFirstElementOfType(
							ccc.getSourcePictogramElement(), BaseElement.class);
					if (obj instanceof EndEvent) {
						List<EventDefinition> eventDefinitions = ((EndEvent) obj)
								.getEventDefinitions();
						for (EventDefinition eventDefinition : eventDefinitions) {
							if (eventDefinition instanceof MessageEventDefinition) {
								return true;
							}
						}
					}
					else if (obj instanceof StartEvent){
						return false;
					}
				}
			}
			return super.isAvailable(context);
		}

		@Override
		public boolean canStartConnection(ICreateConnectionContext context) {
			if (ChoreographyUtil.isChoreographyParticipantBand(context.getSourcePictogramElement()))
				return false;
			return true;
		}

		@Override
		public boolean canCreate(ICreateConnectionContext context) {
			if (ChoreographyUtil.isChoreographyParticipantBand(context.getSourcePictogramElement()))
				return false;
			if (context.getTargetPictogramElement()!=null) {
				if (ChoreographyUtil.isChoreographyParticipantBand(context.getTargetPictogramElement()))
					return false;
			}
			InteractionNode source = getSourceBo(context);
			// Special case for End Event: only allow Message Flows if the End Event
			// has a Message Event Definition.
			if (source instanceof EndEvent) {
				boolean allow = false;
				List<EventDefinition> eventDefinitions = ((EndEvent) source).getEventDefinitions();
				for (EventDefinition eventDefinition : eventDefinitions) {
					if (eventDefinition instanceof MessageEventDefinition) {
						allow = true;
						break;
					}
				}
				if (!allow)
					return false;
			}
			InteractionNode target = getTargetBo(context);
			if (source instanceof ReceiveTask)
				return false;
			if (target instanceof SendTask)
				return false;
			return super.canCreate(context) && isDifferentParticipants(source, target);
		}

		@Override
		protected String getStencilImageId() {
			return ImageProvider.IMG_16_MESSAGE_FLOW;
		}

		@Override
		protected Class<InteractionNode> getSourceClass() {
			return InteractionNode.class;
		}

		@Override
		protected Class<InteractionNode> getTargetClass() {
			return InteractionNode.class;
		}

		private boolean isDifferentParticipants(InteractionNode source, InteractionNode target) {
			if (source == null || target == null) {
				return true;
			}
			boolean different = false;
			ModelHandler mh = ModelHandler.getInstance(getDiagram());
			Participant sourceParticipant = mh.getParticipant(source);
			Participant targetParticipant = mh.getParticipant(target);
			if (sourceParticipant==null) {
				if (targetParticipant==null)
					return true;
				return false;
			}
			different = !sourceParticipant.equals(targetParticipant);

			return different;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2CreateConnectionFeature#getBusinessObjectClass()
		 */
		@Override
		public EClass getBusinessObjectClass() {
			return Bpmn2Package.eINSTANCE.getMessageFlow();
		}
	}
	
	public static class DeleteMessageFromMessageFlowFeature extends DefaultDeleteBPMNShapeFeature {

		public DeleteMessageFromMessageFlowFeature(IFeatureProvider fp) {
			super(fp);
		}

		boolean canDeleteMessage = true;
		Connection messageFlowConnection;
		
		@Override
		public boolean canDelete(IDeleteContext context) {
			PictogramElement pe = context.getPictogramElement();
			if (getMessageFlow(pe)!=null)
				return true;
			return false;
		}

		@Override
		public void delete(IDeleteContext context) {
			Message message = null;
			PictogramElement pe = context.getPictogramElement();
			if (pe instanceof ContainerShape) {
				ContainerShape messageShape = (ContainerShape) pe;
				messageFlowConnection = getMessageFlowConnection(messageShape);
				message = BusinessObjectUtil.getFirstElementOfType(messageShape, Message.class);
			}
			else if (pe instanceof Connection) {
				messageFlowConnection = (Connection)pe;
				MessageFlow messageFlow = (MessageFlow) BusinessObjectUtil.getFirstBaseElement(messageFlowConnection);
				message = messageFlow.getMessageRef();
			}
			
			if (message!=null) {
				List<EObject> list = FeatureSupport.findMessageReferences(getDiagram(), message);
				if (list.size()>2)
					canDeleteMessage = false;
	
				if (canDeleteMessage) {
					EcoreUtil.delete(message, true);
				}
	
				ConnectionDecorator decorator = findMessageDecorator(messageFlowConnection);
				if (decorator!=null) {
					ContainerShape messageShape = BusinessObjectUtil.getFirstElementOfType(decorator, ContainerShape.class);
					if (messageShape!=null) {
						ContainerShape labelShape = BusinessObjectUtil.getFirstElementOfType(messageShape, ContainerShape.class);
						if (labelShape!=null)
							peService.deletePictogramElement(labelShape);
						peService.deletePictogramElement(messageShape);
					}
					peService.deletePictogramElement(decorator);
				}
			}
			
			super.delete(context);
		}
		
		@Override
		protected void deleteBusinessObject(Object bo) {
			if (bo instanceof Message && !canDeleteMessage)
				return;
			super.deleteBusinessObject(bo);
		}
		
		@Override
		public void postDelete(IDeleteContext context) {
			MessageFlow messageFlow = (MessageFlow) BusinessObjectUtil.getFirstBaseElement(messageFlowConnection);
			if (messageFlow!=null) {
				messageFlow.setMessageRef(null);
				peService.setPropertyValue(messageFlowConnection, MESSAGE_REF, ""); //$NON-NLS-1$
			}
		}

	}

	public static class UpdateMessageFlowFeature extends UpdateLabelFeature {

		boolean isUpdating = false;
		
		public UpdateMessageFlowFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		public IReason updateNeeded(IUpdateContext context) {
			if (isUpdating)
				return Reason.createFalseReason();
			
			if (context.getPictogramElement() instanceof Connection) {
				Connection connection = (Connection) context.getPictogramElement();
				MessageFlow messageFlow = (MessageFlow) BusinessObjectUtil.getFirstBaseElement(connection);
				
				String oldMessageRef = peService.getPropertyValue(connection, MESSAGE_REF);
				if (oldMessageRef==null)
					oldMessageRef = ""; //$NON-NLS-1$
				
				String newMessageRef = messageToString(messageFlow.getMessageRef());
				
				if (!oldMessageRef.equals(newMessageRef)) {
					return Reason.createTrueReason(Messages.MessageFlowFeatureContainer_Ref_Changed);
				}
				
				// check if connection has been moved or reconnected
				if (messageDecoratorMoved(connection))
					return Reason.createTrueReason(Messages.MessageFlowFeatureContainer_Decorator_Moved);
			}
			return super.updateNeeded(context);
		}
		
		@Override
		public boolean update(IUpdateContext context) {
			try {
				isUpdating = true;
				Connection connection = (Connection) context.getPictogramElement();
				MessageFlow messageFlow = (MessageFlow) BusinessObjectUtil.getFirstBaseElement(connection);
				Message message = messageFlow.getMessageRef();
				String oldMessageRef = peService.getPropertyValue(connection, MESSAGE_REF);
				if (oldMessageRef==null)
					oldMessageRef = ""; //$NON-NLS-1$
				
				String newMessageRef = messageToString(messageFlow.getMessageRef());
				
				if (!oldMessageRef.equals(newMessageRef)) {
					removeMessageDecorator(getFeatureProvider(), connection);
					if (message!=null) {
						Shape messageShape = (Shape) context.getProperty(MESSAGE_REF);
						addMessageDecorator(getFeatureProvider(), connection, message, messageShape);
					}
					peService.setPropertyValue(connection, MESSAGE_REF, newMessageRef);
				}
				else {
					// move the message decorator
					adjustMessageDecorator(getFeatureProvider(), connection);
				}

				return super.update(context);
			}
			finally {
				isUpdating = false;
			}
		}
	}
	
	public static class ReconnectMessageFlowFeature extends AbstractReconnectFlowFeature {

		public ReconnectMessageFlowFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		protected Class<? extends EObject> getTargetClass() {
			return InteractionNode.class;
		}

		@Override
		protected Class<? extends EObject> getSourceClass() {
			return InteractionNode.class;
		}
	} 
}