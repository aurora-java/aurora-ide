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
 * @author Bob Brodt
 ******************************************************************************/

package org.eclipse.bpmn2.modeler.core.features;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.Group;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.modeler.core.LifecycleEvent;
import org.eclipse.bpmn2.modeler.core.LifecycleEvent.EventType;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.ObjectEditingDialog;
import org.eclipse.bpmn2.modeler.core.preferences.Bpmn2Preferences;
import org.eclipse.bpmn2.modeler.core.preferences.ModelEnablements;
import org.eclipse.bpmn2.modeler.core.runtime.CustomTaskDescriptor;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.IExecutionInfo;
import org.eclipse.graphiti.features.IFeatureAndContext;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.ReconnectionContext;
import org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.osgi.util.NLS;

/**
 * This is the Graphiti CreateFeature base class for all BPMN2 model elements which are
 * considered "connections" e.g. {@link org.eclipse.bpmn2.SequenceFlow}, {@link org.eclipse.bpmn2.Association},
 * {@link org.eclipse.bpmn2.MessageFlow} and {@link org.eclipse.bpmn2.ConversationLink}
 * 
 * The Type Parameter "CONNECTION" is the BPMN2 element class, "SOURCE" is the
 * BPMN2 class of the source object of the connection, "TARGET" is the BPMN2
 * class of the connection target object.
 *
 * @param <CONNECTION> the generic type for the BPMN2 connection
 * @param <SOURCE> the generic type for the BPMN2 source element
 * @param <TARGET> the generic type for the BPMN2 target element
 */
public abstract class AbstractBpmn2CreateConnectionFeature<
			CONNECTION extends BaseElement,
			SOURCE extends EObject,
			TARGET extends EObject>
		extends AbstractCreateConnectionFeature
		implements IBpmn2CreateFeature<CONNECTION, ICreateConnectionContext> {

	/** The changes done. */
	protected boolean changesDone = true;

	/**
	 * Default constructor for this Create Feature.
	 *
	 * @param fp - the BPMN2 Modeler Feature Provider
	 * @param name - name of the type of object being created
	 * @param description - description of the object being created
	 * @link org.eclipse.bpmn2.modeler.ui.diagram.BPMNFeatureProvider
	 */
	public AbstractBpmn2CreateConnectionFeature(IFeatureProvider fp) {
		super(fp, "", "");
	}

	public String getCreateName() {
		// TODO: get name from Messages by generating a field name using the business object class
		return ModelUtil.toCanonicalString(getFeatureClass().getName());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractCreateFeature#getCreateDescription()
	 * This is displayed in the Edit -> Undo/Redo menu 
	 */
	@Override
	public String getCreateDescription() {
		return NLS.bind(Messages.AbstractBpmn2CreateConnectionFeature_Create,
				ModelUtil.toCanonicalString( getFeatureClass().getName()));
	}

	@Override
	public String getName() {
		return getCreateName();
	}

	@Override
	public String getDescription() {
		return getCreateDescription();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractFeature#isAvailable(org.eclipse.graphiti.features.context.IContext)
	 * Returns true if this type of connection is available in the tool palette and context menus. 
	 */
	@Override
	public boolean isAvailable(IContext context) {
		Object o = null;
		if (context instanceof ICreateConnectionContext) {
			ICreateConnectionContext ccc = (ICreateConnectionContext)context;
			if (ccc.getTargetPictogramElement()!=null) {
				o = BusinessObjectUtil.getFirstElementOfType(
						ccc.getTargetPictogramElement(), BaseElement.class);
			}
			else if (ccc.getSourcePictogramElement()!=null) {
				o = BusinessObjectUtil.getFirstElementOfType(
						ccc.getSourcePictogramElement(), BaseElement.class);
			}
		}
		else if (context instanceof IReconnectionContext) {
			IReconnectionContext rc = (IReconnectionContext)context;
			if (rc.getTargetPictogramElement()!=null) {
				o = BusinessObjectUtil.getFirstElementOfType(
						rc.getTargetPictogramElement(), BaseElement.class);
			}
		}
		
		if (o instanceof EndEvent || o instanceof Group)
			return false;
		
		if (o instanceof EObject) {
			return isModelObjectEnabled((EObject)o);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.ICreateConnection#canStartConnection(org.eclipse.graphiti.features.context.ICreateConnectionContext)
	 * Returns true if the source object is valid for this type of connection. 
	 */
	@Override
	public boolean canStartConnection(ICreateConnectionContext context) {
		return getSourceBo(context) != null;
	}

	/**
	 * Check if the connection is allowed.
	 * Only one connection of each type is allowed between the same source and target objects.
	 * Also enforce User Preference if only one incoming or outgoing connection is allowed. 
	 *
	 * @param sourceContainer the source container
	 * @param targetContainer the target container
	 * @param connectionClass the connection class
	 * @param reconnectType the reconnect type
	 * @return true, if the connection is allowed
	 */
	public static boolean canCreateConnection(AnchorContainer sourceContainer, AnchorContainer targetContainer, EClass connectionClass, String reconnectType) {
		if (sourceContainer!=null && targetContainer!=null) {
			// Make sure only one connection of each type is created for the same
			// source and target objects, i.e. you can't have two SequenceFlows
			// with the same source and target objects.
			for (Anchor sourceAnchor : sourceContainer.getAnchors()) {
				for (Connection sourceConnection : sourceAnchor.getOutgoingConnections()) {
					EObject sourceObject = BusinessObjectUtil.getBusinessObjectForPictogramElement(sourceConnection);
					if (connectionClass==Bpmn2Package.eINSTANCE.getDataAssociation()) {
						// Ugh! Special case for DataAssociations: we may have
						// an Activity with both a DataOutputAssociation and a
						// DataInputAssociation to the same ItemAwareElement
						EObject o = BusinessObjectUtil.getBusinessObjectForPictogramElement(sourceContainer);
						if (o instanceof ItemAwareElement)
							connectionClass = Bpmn2Package.eINSTANCE.getDataInputAssociation();
						else
							connectionClass = Bpmn2Package.eINSTANCE.getDataOutputAssociation();
					}
					if (sourceObject!=null && sourceObject.eClass() == connectionClass) {
						if (sourceConnection.getEnd().getParent() == targetContainer)
							return false;
					}
				}
			}
			
			Bpmn2Preferences prefs = Bpmn2Preferences.getInstance(sourceContainer);
			if (!prefs.getAllowMultipleConnections() && connectionClass==Bpmn2Package.eINSTANCE.getSequenceFlow()) {
				// if User Preferences don't allow multiple incoming/outgoing
				// connections on Activities, enforce it here.
				EObject businessObject;
				if (!ReconnectionContext.RECONNECT_TARGET.equals(reconnectType)) {
					businessObject = BusinessObjectUtil.getBusinessObjectForPictogramElement(sourceContainer);
					if (businessObject instanceof Activity || businessObject instanceof Event) {
						for (Anchor a : sourceContainer.getAnchors()) {
							for (Connection c : a.getOutgoingConnections()) {
								EObject o = BusinessObjectUtil.getBusinessObjectForPictogramElement(c);
								if (o instanceof SequenceFlow) {
									return false;
								}
							}
						}
					}
				}
				
				if (!ReconnectionContext.RECONNECT_SOURCE.equals(reconnectType)) {
					businessObject = BusinessObjectUtil.getBusinessObjectForPictogramElement(targetContainer);
					if (businessObject instanceof Activity || businessObject instanceof Event) {
						for (Anchor a : targetContainer.getAnchors()) {
							for (Connection c : a.getIncomingConnections()) {
								EObject o = BusinessObjectUtil.getBusinessObjectForPictogramElement(c);
								if (o instanceof SequenceFlow) {
									return false;
								}
							}
						}
					}
				}
			}
			return true;
		}
		return false;

	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IBpmn2CreateFeature#createBusinessObject(org.eclipse.graphiti.features.context.IContext)
	 * Creates the business object, i.e. the BPMN2 element
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CONNECTION createBusinessObject(ICreateConnectionContext context) {
		Resource resource = getResource(context);
		EClass eclass = getBusinessObjectClass();
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(eclass);
		CONNECTION businessObject = (CONNECTION)adapter.getObjectDescriptor().createObject(resource,eclass);
		EStructuralFeature nameFeature = businessObject.eClass().getEStructuralFeature("name"); //$NON-NLS-1$
		if (nameFeature!=null) {
			businessObject.eUnset(nameFeature);
		}
		SOURCE source = getSourceBo(context);
		TARGET target = getTargetBo(context);
		EStructuralFeature sourceRefFeature = businessObject.eClass().getEStructuralFeature("sourceRef"); //$NON-NLS-1$
		EStructuralFeature targetRefFeature = businessObject.eClass().getEStructuralFeature("targetRef"); //$NON-NLS-1$
		if (sourceRefFeature!=null && targetRefFeature!=null) {
			businessObject.eSet(sourceRefFeature, source);
			businessObject.eSet(targetRefFeature, target);
		}
		putBusinessObject(context, businessObject);
		changesDone = true;
		return businessObject;
	}
	
	protected Resource getResource(ICreateConnectionContext context) {
		PictogramElement pe = context.getSourcePictogramElement();
		if (pe==null)
			pe = context.getTargetPictogramElement();
		if (pe==null)
			pe = context.getSourceAnchor();
		if (pe==null)
			pe = context.getTargetAnchor();
		EObject bo = BusinessObjectUtil.getBusinessObjectForPictogramElement(pe);
		return bo.eResource();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IBpmn2CreateFeature#getBusinessObject(org.eclipse.graphiti.features.context.IContext)
	 * Fetches the business object from the Create Context
	 */
	@SuppressWarnings("unchecked")
	public CONNECTION getBusinessObject(ICreateConnectionContext context) {
		return (CONNECTION) context.getProperty(GraphitiConstants.BUSINESS_OBJECT);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IBpmn2CreateFeature#putBusinessObject(org.eclipse.graphiti.features.context.IContext, org.eclipse.emf.ecore.EObject)
	 * Saves the business object in the Create Context.
	 * If the object is a Custom Element, it is initialized as defined in the extension plugin's plugin.xml
	 */
	public void putBusinessObject(ICreateConnectionContext context, CONNECTION businessObject) {
		context.putProperty(GraphitiConstants.BUSINESS_OBJECT, businessObject);
		String id = (String)context.getProperty(GraphitiConstants.CUSTOM_ELEMENT_ID);
		if (id!=null) {
	    	TargetRuntime rt = TargetRuntime.getCurrentRuntime();
	    	CustomTaskDescriptor ctd = rt.getCustomTask(id);
	    	ctd.populateObject(businessObject, getResource(context), true);
		}
		
		TargetRuntime.getCurrentRuntime().notify(new LifecycleEvent(EventType.BUSINESSOBJECT_INITIALIZED,
				getFeatureProvider(), context, businessObject));
	}
	
	public EClass getFeatureClass() {
		return getBusinessObjectClass();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IBpmn2CreateFeature#postExecute(org.eclipse.graphiti.IExecutionInfo)
	 * Invoked after the graphic has been created to display an optional configuration dialog.
	 * The configuration dialog popup is enabled/disabled in the user Preferences for BPMN2 Editor.
	 */
	public void postExecute(IExecutionInfo executionInfo) {
		for (IFeatureAndContext fc : executionInfo.getExecutionList()) {
			IContext context = fc.getContext();
			if (context instanceof ICreateConnectionContext) {
				ICreateConnectionContext cc = (ICreateConnectionContext)context;
				CONNECTION businessObject = getBusinessObject(cc);
				Bpmn2Preferences prefs = (Bpmn2Preferences) ((DiagramEditor) getDiagramEditor()).getAdapter(Bpmn2Preferences.class);
				if (prefs!=null && prefs.getShowPopupConfigDialog(businessObject)) {
					ObjectEditingDialog dialog =
							new ObjectEditingDialog((DiagramEditor)getDiagramEditor(), businessObject);
					dialog.open();
				}
			}
		}
	}
	
	/**
	 * Creates and prepares a new AddConnectionContext from a CreateConnectionContext.
	 *
	 * @param context the CreateConnectionContext
	 * @param newObject the new object, must be a BPMN2 connection object (see class description)
	 * @return a new AddConnectionContext
	 */
	protected AddConnectionContext createAddConnectionContext(ICreateConnectionContext context, Object newObject) {
		AddConnectionContext newContext = new AddConnectionContext(context.getSourceAnchor(), context.getTargetAnchor());
		newContext.setNewObject(newObject);
		
		// copy properties into the new context
		Object value = context.getProperty(GraphitiConstants.CUSTOM_ELEMENT_ID);
		newContext.putProperty(GraphitiConstants.CUSTOM_ELEMENT_ID, value);
		value = context.getProperty(GraphitiConstants.IMPORT_PROPERTY);
		newContext.putProperty(GraphitiConstants.IMPORT_PROPERTY, value);
		value = context.getProperty(GraphitiConstants.BUSINESS_OBJECT);
		newContext.putProperty(GraphitiConstants.BUSINESS_OBJECT, value);
		return newContext;
	}

	/**
	 * Convenience method to check if a model object was disabled in the extension plugin.
	 * 
	 * @return true/false depending on if the model object is enabled or disabled.
	 * If disabled, the object will not be available and will not appear in the tool palette
	 * or context menus.
	 */
	protected boolean isModelObjectEnabled() {
		ModelEnablements me = getModelEnablements();
		if (me!=null)
			return me.isEnabled(getBusinessObjectClass());
		return false;
	}
	
	/**
	 * Checks if is model object enabled.
	 *
	 * @param o the o
	 * @return true, if is model object enabled
	 */
	protected boolean isModelObjectEnabled(EObject o) {
		ModelEnablements me = getModelEnablements();
		if (me!=null) {
			EClass eclass = (o instanceof EClass) ? (EClass)o : o.eClass();
			return me.isEnabled(eclass);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractFeature#hasDoneChanges()
	 */
	@Override
	public boolean hasDoneChanges() {
		return changesDone;
	}
	
	/**
	 * Gets the model enablements.
	 *
	 * @return the model enablements
	 */
	protected ModelEnablements getModelEnablements() {
		DiagramEditor editor = (DiagramEditor) getDiagramEditor();
		return (ModelEnablements) editor.getAdapter(ModelEnablements.class);
	}

	/**
	 * Returns the business object for the connection source shape. If the source object is not valid
	 * for this connection type, return null.
	 * 
	 * @param context - connection create context
	 * @return true if the source is valid, false if not.
	 */
	protected SOURCE getSourceBo(ICreateConnectionContext context) {
		Anchor a = getSourceAnchor(context);
		if (a != null) {
			return BusinessObjectUtil.getFirstElementOfType(a.getParent(), getSourceClass());
		}
		return null;
	}

	protected Anchor getSourceAnchor(ICreateConnectionContext context) {
		Anchor a = context.getSourceAnchor();
		PictogramElement pe = context.getSourcePictogramElement();
		if (a==null && FeatureSupport.isLabelShape(pe)) {
			pe = FeatureSupport.getLabelOwner(pe);
			((CreateConnectionContext)context).setSourcePictogramElement(pe);
			a = Graphiti.getPeService().getChopboxAnchor((AnchorContainer) pe);
			((CreateConnectionContext)context).setSourceAnchor(a);
		}
		return a;
	}

	/**
	 * Returns the business object for the connection target shape. If the target object is not valid
	 * for this connection type, return null.
	 * 
	 * @param context - connection create context
	 * @return true if the target is valid, false if not.
	 */
	protected TARGET getTargetBo(ICreateConnectionContext context) {
		Anchor a = getTargetAnchor(context);
		if (a != null) {
			return BusinessObjectUtil.getFirstElementOfType(a.getParent(), getTargetClass());
		}
		return null;
	}

	/**
	 * Gets the Target Anchor. This does the translation from a Label shape to its owner.
	 * 
	 * @param context
	 * @return
	 */
	protected Anchor getTargetAnchor(ICreateConnectionContext context) {
		Anchor a = context.getTargetAnchor();
		PictogramElement pe = context.getTargetPictogramElement();
		if (a==null && FeatureSupport.isLabelShape(pe)) {
			pe = FeatureSupport.getLabelOwner(pe);
			((CreateConnectionContext)context).setTargetPictogramElement(pe);
			a = Graphiti.getPeService().getChopboxAnchor((AnchorContainer) pe);
			((CreateConnectionContext)context).setTargetAnchor(a);
		}
		return a;
	}
	
	/**
	 * Gets the source object type.
	 * Implementation classes must override this method to provide the BPMN2
	 * object source and target classes that are valid for this connection.
	 *
	 * @return the source class
	 */
	protected abstract Class<SOURCE> getSourceClass();
	
	/**
	 * Gets the target object type.
	 * Implementation classes must override this method to provide the BPMN2
	 * object source and target classes that are valid for this connection.
	 *
	 * @return the target class
	 */
	protected abstract Class<TARGET> getTargetClass();

}
