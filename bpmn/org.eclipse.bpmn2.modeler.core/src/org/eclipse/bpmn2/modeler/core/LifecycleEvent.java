/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013, 2014 Red Hat, Inc.
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

package org.eclipse.bpmn2.modeler.core;

import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.ui.part.WorkbenchPart;

/**
 * BPMN2 Editor Lifecycle Event object.
 * <p>
 * These events are sent to the Target Runtime Extension implementation class
 * during the life of the editor. The method {@see
 * org.eclipse.bpmn2.modeler.core.IBpmn2RuntimeExtension#notify(LifecycleEvent)}
 * will be notified about these events.
 * <p>
 * Event notifications include Editor Events, Business Object Events, Graphiti
 * PictogramElement Events, Editing Domain Transaction Events and Command Stack
 * Events. Additional information may be available, depending on the event
 * type as described in {@see EventType}.
 */
public class LifecycleEvent {

	/**
	 * Lifecycle Event Types.
	 */
	public enum EventType {
		//  Editor Events:
		/**
		 * Sent immediately after the BPMN2 editor starts and just before the
		 * BPMN2 Resource is loaded.
		 * <p>
		 * The {@code LifecycleEvent.target} field will contain the BPMN2 model
		 * object instance.
		 */
		EDITOR_STARTUP,
		/**
		 * Sent by the BPMN2 editor after EDITOR_STARTUP and just before the
		 * graphical elements are created.
		 * <p>
		 * The {@code LifecycleEvent.target} field will contain the BPMN2 model
		 * object instance.
		 */
		EDITOR_INITIALIZED,
		/**
		 * Sent by the BPMN2 editor before shutting down during the
		 * {@link WorkbenchPart#dispose()} method.
		 * <p>
		 * The {@code LifecycleEvent.target} field will contain the BPMN2 model
		 * object instance.
		 */
		EDITOR_SHUTDOWN,
		// Business Object Events:
		/**
		 * Sent by the BPMN2 Object Factory immediately after a business object
		 * has been created. This event is always sent, even during file loading.
		 * <p>
		 * This is a good place to do any additional BPMN2 model object
		 * initialization, or hook adapters.
		 * <p>
		 * The {@code LifecycleEvent.target} field will contain the BPMN2 model
		 * object instance.
		 */
		BUSINESSOBJECT_CREATED,
		/**
		 * Sent by the BPMN2 Create Feature immediately after a business object
		 * has been created and initialized by the editor framework. Only create
		 * actions from the user will trigger this event. This event will not be
		 * sent during file loading, or as a result of a redo action.
		 * <p>
		 * This is a good place to do any additional BPMN2 model object
		 * initialization, or hook adapters.
		 * <p>
		 * The {@code LifecycleEvent.target} field will contain the BPMN2 model
		 * object instance.
		 * <p>
		 * The {@code LifecycleEvent.context} field will contain an
		 * {@code org.eclipse.graphiti.features.context.IAddContext} instance.
		 * <p>
		 * The {@code LifecycleEvent.featureProvider} field will be set to the
		 * BPMN2 Modeler Feature Provider instance.
		 */
		BUSINESSOBJECT_INITIALIZED,
		/**
		 * Sent by the BPMN2 Delete Feature immediately before the business
		 * object is destroyed. Only delete actions from the user will trigger
		 * this event. This event will not be sent as a result of an undo action.
		 * <p>
		 * The {@code LifecycleEvent.target} field will contain the BPMN2 model
		 * object instance.
		 */
		BUSINESSOBJECT_DELETED,
		// Pictogram Element Events:
		/**
		 * Sent by the BPMN2 Feature Provider to test if a Pictogram Element can
		 * be added. This event is sent only if the editor model constraints
		 * have already determined that the element can be added.
		 * <p>
		 * The {@code LifecycleEvent.target} field will contain the Pictogram
		 * Element.
		 * <p>
		 * The {@code LifecycleEvent.context} field will contain an
		 * {@code org.eclipse.graphiti.features.context.IAddContext} instance.
		 * <p>
		 * The {@code LifecycleEvent.featureProvider} field will be set to the
		 * BPMN2 Modeler Feature Provider instance.
		 * {@see org.eclipse.graphiti.ui.features.DefaultFeatureProvider}
		 * <p>
		 * The Target Runtime client may set {@code LifecycleEvent.doit} to
		 * false to prevent the action from being performed.
		 */
		PICTOGRAMELEMENT_CAN_ADD,
		/**
		 * Sent by the BPMN2 Feature Provider immediately after the Pictogram
		 * Element has been be added.
		 * <p>
		 * The {@code LifecycleEvent.target} field will contain the Pictogram
		 * Element.
		 * <p>
		 * The {@code LifecycleEvent.context} field will contain an
		 * {@code org.eclipse.graphiti.features.context.IAddContext} instance.
		 * <p>
		 * The {@code LifecycleEvent.featureProvider} field will be set to the
		 * BPMN2 Modeler Feature Provider instance.
		 */
		PICTOGRAMELEMENT_ADDED,
		/**
		 * Sent by the BPMN2 Feature Provider to test if a Pictogram Element needs
		 * to be updated. This event is sent even if the editor model constraints
		 * have already determined that an update is <strong>not</strong> required.
		 * <p>
		 * The {@code LifecycleEvent.target} field will contain the Pictogram
		 * Element.
		 * <p>
		 * The {@code LifecycleEvent.context} field will contain an
		 * {@code org.eclipse.graphiti.features.context.IUpdateContext} instance.
		 * <p>
		 * The {@code LifecycleEvent.featureProvider} field will be set to the
		 * BPMN2 Modeler Feature Provider instance.
		 * <p>
		 * The Target Runtime client may set {@code LifecycleEvent.doit} to
		 * true to force the update action, even if the model constraints
		 * have determined no update is required.
		 */
		PICTOGRAMELEMENT_UPDATE_NEEDED,
		/**
		 * Sent by the BPMN2 Feature Provider to test if a Pictogram Element can
		 * be updated. This event is sent even if the editor model constraints
		 * have already determined that an update can <strong>not</strong> be performed.
		 * <p>
		 * The {@code LifecycleEvent.target} field will contain the Pictogram
		 * Element.
		 * <p>
		 * The {@code LifecycleEvent.context} field will contain an
		 * {@code org.eclipse.graphiti.features.context.IUpdateContext} instance.
		 * <p>
		 * The {@code LifecycleEvent.featureProvider} field will be set to the
		 * BPMN2 Modeler Feature Provider instance.
		 * <p>
		 * The Target Runtime client may set {@code LifecycleEvent.doit} to
		 * true to force the update action, even if the model constraints
		 * have determined that an update can not be performed.
		 */
		PICTOGRAMELEMENT_CAN_UPDATE,
		/**
		 * Sent by the BPMN2 Feature Provider immediately before a Pictogram Element is
		 * updated.
		 * <p>
		 * The {@code LifecycleEvent.target} field will contain the Pictogram
		 * Element.
		 * <p>
		 * The {@code LifecycleEvent.context} field will contain an
		 * {@code org.eclipse.graphiti.features.context.IUpdateContext} instance.
		 * <p>
		 * The {@code LifecycleEvent.featureProvider} field will be set to the
		 * BPMN2 Modeler Feature Provider instance.
		 */
		PICTOGRAMELEMENT_UPDATE,
		/**
		 * Sent by the BPMN2 Feature Provider to test if a Pictogram Element can
		 * be laid out. This event is sent even if the editor model constraints
		 * have already determined that a layout can <strong>not</strong> be performed.
		 * <p>
		 * The {@code LifecycleEvent.target} field will contain the Pictogram
		 * Element.
		 * <p>
		 * The {@code LifecycleEvent.context} field will contain an
		 * {@code org.eclipse.graphiti.features.context.ILayoutContext} instance.
		 * <p>
		 * The {@code LifecycleEvent.featureProvider} field will be set to the
		 * BPMN2 Modeler Feature Provider instance.
		 * <p>
		 * The Target Runtime client may set {@code LifecycleEvent.doit} to
		 * true to force the layout action, even if the model constraints
		 * have determined that a layout can not be performed.
		 */
		PICTOGRAMELEMENT_CAN_LAYOUT,
		/**
		 * Sent by the BPMN2 Feature Provider immediately before a Pictogram Element has
		 * been laid out.
		 * <p>
		 * The {@code LifecycleEvent.target} field will contain the Pictogram
		 * Element.
		 * <p>
		 * The {@code LifecycleEvent.context} field will contain an
		 * {@code org.eclipse.graphiti.features.context.ILayoutContext} instance.
		 * <p>
		 * The {@code LifecycleEvent.featureProvider} field will be set to the
		 * BPMN2 Modeler Feature Provider instance.
		 */
		PICTOGRAMELEMENT_LAYOUT,
		/**
		 * Sent by the default BPMN2 Move Feature to test if a Pictogram Element can
		 * be moved. This event is sent even if the editor model constraints
		 * have already determined that the element can <strong>not</strong> be moved.
		 * <p>
		 * The {@code LifecycleEvent.target} field will contain the Pictogram
		 * Element.
		 * <p>
		 * The {@code LifecycleEvent.context} field will contain an
		 * {@code org.eclipse.graphiti.features.context.IMoveContext} instance.
		 * <p>
		 * The {@code LifecycleEvent.featureProvider} field will be set to the
		 * BPMN2 Modeler Feature Provider instance.
		 * <p>
		 * The Target Runtime client may set {@code LifecycleEvent.doit} to
		 * true to force the move action, even if the model constraints
		 * have determined that it can not be moved.
		 */
		PICTOGRAMELEMENT_CAN_MOVE,
		/**
		 * Sent by the default BPMN2 Move Feature immediately
		 * <strong>before</strong> a Pictogram Element is moved. This is a good
		 * place to save size and location information just prior to the move in
		 * case other graphical elements depend on this object's size or
		 * location.
		 * <p>
		 * The {@code LifecycleEvent.target} field will contain the Pictogram
		 * Element.
		 * <p>
		 * The {@code LifecycleEvent.context} field will contain an
		 * {@code org.eclipse.graphiti.features.context.IMoveContext} instance.
		 * <p>
		 * The {@code LifecycleEvent.featureProvider} field will be set to the
		 * BPMN2 Modeler Feature Provider instance.
		 */
		PICTOGRAMELEMENT_PRE_MOVE,
		/**
		 * Sent by the default BPMN2 Move Feature immediately <strong>after</strong>
		 * a Pictogram Element is moved. This is a good place to adjust the size
		 * or location of other graphical elements that may depend on this
		 * object's size or location.
		 * <p>
		 * The {@code LifecycleEvent.target} field will contain the Pictogram
		 * Element.
		 * <p>
		 * The {@code LifecycleEvent.context} field will contain an
		 * {@code org.eclipse.graphiti.features.context.IMoveContext} instance.
		 * <p>
		 * The {@code LifecycleEvent.featureProvider} field will be set to the
		 * BPMN2 Modeler Feature Provider instance.
		 */
		PICTOGRAMELEMENT_POST_MOVE,
		/**
		 * Sent by the default BPMN2 Resize Feature to test if a Pictogram Element can
		 * be resized. This event is sent even if the editor model constraints
		 * have already determined that the element can <strong>not</strong> be resized.
		 * <p>
		 * The {@code LifecycleEvent.target} field will contain the Pictogram
		 * Element.
		 * <p>
		 * The {@code LifecycleEvent.context} field will contain an
		 * {@code org.eclipse.graphiti.features.context.IResizeShapeContext} instance.
		 * <p>
		 * The {@code LifecycleEvent.featureProvider} field will be set to the
		 * BPMN2 Modeler Feature Provider instance.
		 * <p>
		 * The Target Runtime client may set {@code LifecycleEvent.doit} to
		 * true to force the resize action, even if the model constraints
		 * have determined that it can not be resized.
		 */
		PICTOGRAMELEMENT_CAN_RESIZE,
		/**
		 * Sent by the default BPMN2 Resize Feature immediately
		 * <strong>before</strong> a Pictogram Element is resized. This is a good
		 * place to save size and location information just prior to the resize in
		 * case other graphical elements depend on this object's size or
		 * location.
		 * <p>
		 * The {@code LifecycleEvent.target} field will contain the Pictogram
		 * Element.
		 * <p>
		 * The {@code LifecycleEvent.context} field will contain an
		 * {@code org.eclipse.graphiti.features.context.IResizeShapeContext} instance.
		 * <p>
		 * The {@code LifecycleEvent.featureProvider} field will be set to the
		 * BPMN2 Modeler Feature Provider instance.
		 */
		PICTOGRAMELEMENT_PRE_RESIZE,
		/**
		 * Sent by the default BPMN2 Resize Feature immediately <strong>after</strong>
		 * a Pictogram Element is resized. This is a good place to adjust the size
		 * or location of other graphical elements that may depend on this
		 * object's size or location.
		 * <p>
		 * The {@code LifecycleEvent.target} field will contain the Pictogram
		 * Element.
		 * <p>
		 * The {@code LifecycleEvent.context} field will contain an
		 * {@code org.eclipse.graphiti.features.context.IResizeShapeContext} instance.
		 * <p>
		 * The {@code LifecycleEvent.featureProvider} field will be set to the
		 * BPMN2 Modeler Feature Provider instance.
		 */
		PICTOGRAMELEMENT_POST_RESIZE,
		/**
		 * Sent by the default BPMN2 Delete Feature to test if a Pictogram Element can
		 * be deleted. This event is sent only if the editor model constraints
		 * have already determined that the element can be deleted.
		 * <p>
		 * The {@code LifecycleEvent.target} field will contain the Pictogram
		 * Element.
		 * <p>
		 * The {@code LifecycleEvent.context} field will contain an
		 * {@code org.eclipse.graphiti.features.context.IDeleteContext} instance.
		 * <p>
		 * The {@code LifecycleEvent.featureProvider} field will be set to the
		 * BPMN2 Modeler Feature Provider instance.
		 * {@see org.eclipse.graphiti.ui.features.DefaultFeatureProvider}
		 * <p>
		 * The Target Runtime client may set {@code LifecycleEvent.doit} to
		 * false to prevent the action from being performed.
		 */
		PICTOGRAMELEMENT_CAN_DELETE,
		/**
		 * Sent by the default BPMN2 Delete Feature immediately after the Pictogram
		 * Element has been be deleted.
		 * <p>
		 * The {@code LifecycleEvent.target} field will contain the Pictogram
		 * Element.
		 * <p>
		 * The {@code LifecycleEvent.context} field will contain an
		 * {@code org.eclipse.graphiti.features.context.IDeleteContext} instance.
		 * <p>
		 * The {@code LifecycleEvent.featureProvider} field will be set to the
		 * BPMN2 Modeler Feature Provider instance.
		 */
		PICTOGRAMELEMENT_DELETED,
		// Transaction Events:
		/**
		 * Sent by the editor framework immediately <strong>after</strong> a
		 * new transaction is started. This indicates that some change is about
		 * to be made to the model.
		 * <p>
		 * The {@code LifecycleEvent.target} field will contain the Transaction
		 * object.
		 */
		TRANSACTION_STARTING,
		/**
		 * Sent by the editor framework immediately <strong>after</strong> a
		 * transaction has been rolled back. This indicates that the user has canceled
		 * the operation and no changes were made to the model.
		 * <p>
		 * The {@code LifecycleEvent.target} field will contain the Transaction
		 * object.
		 */
		TRANSACTION_INTERRUPTED,
		/**
		 * Sent by the editor framework immediately <strong>after</strong> a
		 * transaction has been committed.
		 * <p>
		 * The {@code LifecycleEvent.target} field will contain the Transaction
		 * object.
		 */
		TRANSACTION_CLOSED,
		// Command Stack Events:
		/**
		 * Sent by the editor's operation command stack immediately <strong>after</strong>
		 * the user has performed an UNDO action.
		 * <p>
		 * The {@code LifecycleEvent.target} field will contain the command stack Operation
		 * object.
		 */
		COMMAND_UNDO,
		/**
		 * Sent by the editor's operation command stack immediately <strong>after</strong>
		 * the user has performed a REDO action.
		 * <p>
		 * The {@code LifecycleEvent.target} field will contain the command stack Operation
		 * object.
		 */
		COMMAND_REDO,
	};
	
	/** An Event Type. {@see LifecycleEvent.EventType} */
	public EventType eventType;
	/** The object affected by the event */
	public Object target;
	/** A Graphiti Context for Pictogram Element events*/
	public IContext context;
	/** The BPMN2 Feature Provider for Pictogram Element events*/
	public IFeatureProvider featureProvider;
	/** An override flag to force (or prevent) certain Pictogram Element actions */
	public boolean doit = true;
	
	/**
	 * Constructor for a simple event type and event object.
	 * 
	 * @param eventType  one of the EventType enumerations.
	 * @param target  object affected by the event.
	 */
	public LifecycleEvent(EventType eventType, Object target) {
		this.eventType = eventType;
		this.target = target;
	}
	
	/**
	 * Constructor for a Pictogram Element event.
	 * 
	 * @param eventType  one of the EventType enumerations.
	 * @param featureProvider  the BPMN2 Feature Provider instance.
	 * @param context  a Graphiti Context for the event.
	 * @param target  object affected by the event.
	 */
	public LifecycleEvent(EventType eventType, IFeatureProvider featureProvider, IContext context, Object target) {
		this.eventType = eventType;
		this.featureProvider = featureProvider;
		this.context = context;
		this.target = target;
	}
	
	@Override
	public String toString() {
		String s = Messages.LifecycleEvent_Event_Prefix+eventType;
		if (target instanceof PictogramElement) {
			EObject o = BusinessObjectUtil.getBusinessObjectForPictogramElement((PictogramElement)target);
			if (o!=null) {
				s += " " +o.eClass().getName(); //$NON-NLS-1$
			}
		}
		else if (target instanceof EObject) {
			EObject o = (EObject) target;
			s += " " +o.eClass().getName(); //$NON-NLS-1$
		}
		return s;
	}
}
