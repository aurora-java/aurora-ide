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
package org.eclipse.bpmn2.modeler.ui.features.conversation;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Collaboration;
import org.eclipse.bpmn2.Conversation;
import org.eclipse.bpmn2.ConversationLink;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.modeler.core.features.BaseElementConnectionFeatureContainer;
import org.eclipse.bpmn2.modeler.core.features.DefaultDeleteBPMNShapeFeature;
import org.eclipse.bpmn2.modeler.core.features.flow.AbstractAddFlowFeature;
import org.eclipse.bpmn2.modeler.core.features.flow.AbstractCreateFlowFeature;
import org.eclipse.bpmn2.modeler.core.features.flow.AbstractReconnectFlowFeature;
import org.eclipse.bpmn2.modeler.core.features.label.UpdateLabelFeature;
import org.eclipse.bpmn2.modeler.ui.ImageProvider;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReconnectionFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.pictograms.Connection;

public class ConversationLinkFeatureContainer extends BaseElementConnectionFeatureContainer {

	@Override
	public boolean canApplyTo(Object o) {
		return super.canApplyTo(o) && o instanceof ConversationLink;
	}

	@Override
	public IAddFeature getAddFeature(IFeatureProvider fp) {
		return new AddConversationLinkFeature(fp);
	}

	@Override
	public ICreateConnectionFeature getCreateConnectionFeature(IFeatureProvider fp) {
		return new CreateConversationLinkFeature(fp);
	}

	@Override
	public IReconnectionFeature getReconnectionFeature(IFeatureProvider fp) {
		return new ReconnectConversationLinkFeature(fp);
	}

	@Override
	public IUpdateFeature getUpdateFeature(IFeatureProvider fp) {
		return new UpdateLabelFeature(fp);
	}

	@Override
	public IDeleteFeature getDeleteFeature(IFeatureProvider fp) {
		return new DefaultDeleteBPMNShapeFeature(fp) {
			@Override
			protected void deleteBusinessObject(Object bo) {
				// the default implementation of this deletes the Conversation Link
				// using Ecore.delete() - this causes a class cast exception when it
				// tries to remove the object from the InteractionNode's eOpposite
				// outgoingConversationLinks and incomingConversationLinks lists.
				// So, we'll do this the hard way...
				if (bo instanceof ConversationLink) {
					ConversationLink cl = (ConversationLink) bo;
					if (cl.eContainer() instanceof Collaboration) {
						Collaboration co = (Collaboration)cl.eContainer();
						co.getConversationLinks().remove(cl);
					}
				}
				else
					super.deleteBusinessObject(bo);
			}
		};
	}

	public class AddConversationLinkFeature extends AbstractAddFlowFeature<ConversationLink> {
		public AddConversationLinkFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		protected Polyline createConnectionLine(Connection connection) {
			Polyline connectionLine = super.createConnectionLine(connection);
			connectionLine.setLineWidth(4);
			return connectionLine;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2AddFeature#getBusinessObjectType()
		 */
		@Override
		public Class getBusinessObjectType() {
			return ConversationLink.class;
		}
	}
	public static class CreateConversationLinkFeature extends AbstractCreateFlowFeature<ConversationLink, Participant, Conversation> {

		public CreateConversationLinkFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		protected String getStencilImageId() {
			return ImageProvider.IMG_16_CONVERSATION_LINK;
		}

		@Override
		protected Class<Participant> getSourceClass() {
			return Participant.class;
		}

		@Override
		protected Class<Conversation> getTargetClass() {
			return Conversation.class;
		}

		@Override
		public boolean isAvailable(IContext context) {
			if (!isModelObjectEnabled(Bpmn2Package.eINSTANCE.getConversationLink()))
				return false;
			return super.isAvailable(context);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2CreateConnectionFeature#getBusinessObjectClass()
		 */
		@Override
		public EClass getBusinessObjectClass() {
			return Bpmn2Package.eINSTANCE.getConversationLink();
		}
	}
	public static class ReconnectConversationLinkFeature extends AbstractReconnectFlowFeature {

		public ReconnectConversationLinkFeature(IFeatureProvider fp) {
			super(fp);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected Class<? extends EObject> getTargetClass() {
			return Conversation.class;
		}

		@Override
		protected Class<? extends EObject> getSourceClass() {
			return Participant.class;
		}

	}
	
}