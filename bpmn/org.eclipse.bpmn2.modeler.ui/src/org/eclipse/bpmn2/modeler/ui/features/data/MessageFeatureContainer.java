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
package org.eclipse.bpmn2.modeler.ui.features.data;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.MessageFlow;
import org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2AddFeature;
import org.eclipse.bpmn2.modeler.core.features.BaseElementFeatureContainer;
import org.eclipse.bpmn2.modeler.core.features.DefaultDeleteBPMNShapeFeature;
import org.eclipse.bpmn2.modeler.core.features.DefaultMoveBPMNShapeFeature;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.features.choreography.ChoreographyUtil;
import org.eclipse.bpmn2.modeler.core.features.data.AbstractCreateRootElementFeature;
import org.eclipse.bpmn2.modeler.core.features.label.AddShapeLabelFeature;
import org.eclipse.bpmn2.modeler.core.features.label.UpdateLabelFeature;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.core.utils.ShapeDecoratorUtil;
import org.eclipse.bpmn2.modeler.core.utils.ShapeDecoratorUtil.Envelope;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.core.utils.StyleUtil;
import org.eclipse.bpmn2.modeler.ui.ImageProvider;
import org.eclipse.bpmn2.modeler.ui.features.flow.MessageFlowFeatureContainer;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.ITargetConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeService;
import org.eclipse.graphiti.ui.internal.util.ui.PopupMenu;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class MessageFeatureContainer extends BaseElementFeatureContainer {

	public static final int ENVELOPE_WIDTH = 30;
	public static final int ENVELOPE_HEIGHT = 20;
	public static final String IS_REFERENCE = "is.reference"; //$NON-NLS-1$

	@Override
	public Object getApplyObject(IContext context) {
		Object object = super.getApplyObject(context);
		if (object instanceof Message &&
				!isChoreographyMessage(context) &&
				!isMessageFlowMessage(context)) {
			return object;
		}
		return null;
	}
	
	@Override
	public boolean canApplyTo(Object o) {
		return super.canApplyTo(o) && o instanceof Message;
	}

	public static boolean isMessageFlowMessage(IContext context) {
		// This Feature Container DOES NOT handle Messages attached
		// to Message Flows.
		// See MessageFlowFeatureContainer instead.
		if (context instanceof IPictogramElementContext) {
			PictogramElement pe = ((IPictogramElementContext)context).getPictogramElement();
			MessageFlow mf = MessageFlowFeatureContainer.getMessageFlow(pe);
			return mf != null;
		}
		return false;
	}
	
	public static boolean isChoreographyMessage(IContext context) {
		// This Feature Container DOES NOT handle Messages attached
		// to Choreography Participant Bands.
		// See ChoreographyMessageLinkFeatureContainer instead.
		if (context instanceof IPictogramElementContext) {
			PictogramElement pe = ((IPictogramElementContext)context).getPictogramElement();
			if (ChoreographyUtil.isChoreographyMessage(pe))
				return true;
		}
		return false;
	}
	
	@Override
	public ILayoutFeature getLayoutFeature(IFeatureProvider fp) {
		return null;
	}

	@Override
	public ICreateFeature getCreateFeature(IFeatureProvider fp) {
		return new CreateMessageFeature(fp);
	}

	@Override
	public IAddFeature getAddFeature(IFeatureProvider fp) {
		return new AddMessageFeature(fp);
	}
	
	@Override
	public IDeleteFeature getDeleteFeature(IFeatureProvider fp) {
		return new DeleteMessageFeature(fp);
	}

	@Override
	public IUpdateFeature getUpdateFeature(IFeatureProvider fp) {
		return new UpdateLabelFeature(fp);
	}

	@Override
	public IMoveShapeFeature getMoveFeature(IFeatureProvider fp) {
		return new MoveMessageFeature(fp);
	}

	@Override
	public IResizeShapeFeature getResizeFeature(IFeatureProvider fp) {
		return new DefaultResizeShapeFeature(fp) {
			@Override
			public boolean canResizeShape(IResizeShapeContext context) {
				return false;
			}
		};
	}

	public class AddMessageFeature extends AbstractBpmn2AddFeature<Message> {
		public AddMessageFeature(IFeatureProvider fp) {
			super(fp);
		}

		public IAddFeature getAddLabelFeature(IFeatureProvider fp) {
			return new AddShapeLabelFeature(fp);
		}

		@Override
		public boolean canAdd(IAddContext context) {
			return true;
		}

		@Override
		public PictogramElement add(IAddContext context) {
			Message businessObject = getBusinessObject(context);
			
			// if the Message is being dropped onto a MessageFlow, associate it with that flow
			// instead of adding a new Message shape to the Diagram
			ContainerShape containerShape = addMessageFlowMessage(context);
			if (containerShape!=null)
				return containerShape;

			int width = getWidth(context);
			int height = getHeight(context);

			containerShape = peService.createContainerShape(context.getTargetContainer(), true);
			Rectangle invisibleRect = gaService.createInvisibleRectangle(containerShape);
			gaService.setLocationAndSize(invisibleRect, context.getX(), context.getY(), width, height);

			Envelope envelope = ShapeDecoratorUtil.createEnvelope(invisibleRect, 0, 0, width, height);
			envelope.rect.setFilled(true);
			StyleUtil.applyStyle(envelope.rect, businessObject);
			envelope.line.setForeground(manageColor(StyleUtil.CLASS_FOREGROUND));

			if (context.getProperty(IS_REFERENCE)==null) {
				boolean isImport = context.getProperty(GraphitiConstants.IMPORT_PROPERTY) != null;
				createDIShape(containerShape, businessObject, !isImport);
			}
			else {
				link(containerShape, businessObject);
			}
			
			// hook for subclasses to inject extra code
			((AddContext)context).setWidth(width);
			((AddContext)context).setHeight(height);
			decorateShape(context, containerShape, businessObject);

			peService.createChopboxAnchor(containerShape);
			AnchorUtil.addFixedPointAnchors(containerShape, invisibleRect);
			
			return containerShape;
		}
		
		private ContainerShape addMessageFlowMessage(ITargetConnectionContext context) {
			
			Shape messageShape = null;
			Message message = null;
			if (context instanceof IPictogramElementContext) {
				messageShape = (Shape) ((IPictogramElementContext)context).getPictogramElement();
				message = BusinessObjectUtil.getFirstElementOfType(messageShape, Message.class);
			}
			else if (context instanceof IAddContext) {
				message = (Message) ((IAddContext)context).getNewObject();
			}
			Connection connection = context.getTargetConnection();
			MessageFlow messageFlow = BusinessObjectUtil.getFirstElementOfType(connection, MessageFlow.class);
			if (messageFlow!=null && messageFlow.getMessageRef()!=message) {
				messageFlow.setMessageRef(message);
				UpdateContext uc = new UpdateContext(connection);
				// set the Message shape into the update context;
				// this will cause the MessageFlow update feature to reuse the shape.
				uc.putProperty(MessageFlowFeatureContainer.MESSAGE_REF, messageShape);
				IUpdateFeature uf = getFeatureProvider().getUpdateFeature(uc);
				uf.update(uc);
				return MessageFlowFeatureContainer.findMessageShape(connection);
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2AddFeature#getBusinessObjectType()
		 */
		@Override
		public Class getBusinessObjectType() {
			return Message.class;
		}
	}

	public static class CreateMessageFeature extends AbstractCreateRootElementFeature<Message> {
		
		private static ILabelProvider labelProvider = new ILabelProvider() {

			public void removeListener(ILabelProviderListener listener) {
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void dispose() {

			}

			public void addListener(ILabelProviderListener listener) {

			}

			public String getText(Object element) {
				if (((Message) element).getId() == null)
					return ((Message) element).getName();
				return NLS.bind(
					Messages.MessageFeatureContainer_Default_Name, ((Message) element).getName());
			}

			public Image getImage(Object element) {
				return null;
			}

		};

		public CreateMessageFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		public String getStencilImageId() {
			return ImageProvider.IMG_16_MESSAGE;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2CreateFeature#getBusinessObjectClass()
		 */
		@Override
		public EClass getBusinessObjectClass() {
			return Bpmn2Package.eINSTANCE.getMessage();
		}

		@Override
		public Message createBusinessObject(ICreateContext context) {
			changesDone = true;

			Message message = null;
			message = Bpmn2ModelerFactory.create(Message.class);
			String oldName = message.getName();
			message.setName(Messages.MessageFeatureContainer_New);
			message.setId(null);
			EObject targetBusinessObject = (EObject)getBusinessObjectForPictogramElement(context.getTargetContainer());
			Definitions definitions = ModelUtil.getDefinitions(targetBusinessObject);

			List<Message> messageList = new ArrayList<Message>();
			messageList.add(message);
			messageList.addAll( ModelUtil.getAllRootElements(definitions, Message.class) );

			Message result = message;
			if (messageList.size() > 1) {
				PopupMenu popupMenu = new PopupMenu(messageList, labelProvider);
				changesDone = popupMenu.show(Display.getCurrent().getActiveShell());
				if (changesDone) {
					result = (Message) popupMenu.getResult();
				}
				else {
					EcoreUtil.delete(message);
					message = null;
				}
			}
			else
				changesDone = true;

			if (changesDone) {
				if (result == message) {
					// the new one
					definitions.getRootElements().add(message);
					message.setId(null);
					ModelUtil.setID(message);
					message.setName(oldName);
				} else {
					// and existing one
					message = result;
				}
				putBusinessObject(context, message);
			}
			
			return message;
		}
	}

	public static class MoveMessageFeature extends DefaultMoveBPMNShapeFeature {

		public MoveMessageFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		protected void postMoveShape(IMoveShapeContext context) {
			super.postMoveShape(context);
			
			// if the Message shape was moved onto a MessageFlow, associate the flow with
			// this Message and force an update.
			Shape messageShape = context.getShape();
			Message message = BusinessObjectUtil.getFirstElementOfType(messageShape, Message.class);
			Connection messageFlowConnection = context.getTargetConnection();
			MessageFlow messageFlow = BusinessObjectUtil.getFirstElementOfType(messageFlowConnection, MessageFlow.class);
			if (messageFlow!=null && messageFlow.getMessageRef()!=message) {
				messageFlow.setMessageRef(message);
				UpdateContext uc = new UpdateContext(messageFlowConnection);
				// set the Message shape into the update context;
				// this will cause the MessageFlow update feature to reuse the shape.
				uc.putProperty(MessageFlowFeatureContainer.MESSAGE_REF, messageShape);
				IUpdateFeature uf = getFeatureProvider().getUpdateFeature(uc);
				uf.update(uc);
			}
		}
	}
	
	public static class DeleteMessageFeature extends DefaultDeleteBPMNShapeFeature {

		public DeleteMessageFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		protected void deleteBusinessObject(Object bo) {
			
			if (bo instanceof Message) {
				// This Message can be deleted from model if there are no more references to it
				Message message = (Message)bo;
				List<EObject> list = FeatureSupport.findMessageReferences(getDiagram(), message);
				if (list.size()>0)
					return;
			}
			
			super.deleteBusinessObject(bo);
		}
	}
}