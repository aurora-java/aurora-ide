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

package org.eclipse.bpmn2.modeler.ui.adapters.properties;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.CallableElement;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.DataAssociation;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.DataObjectReference;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.DataStore;
import org.eclipse.bpmn2.DataStoreReference;
import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.FeatureDescriptor;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.bpmn2.modeler.core.preferences.ModelEnablements;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.core.utils.GraphicsUtil;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.ui.features.flow.DataAssociationFeatureContainer;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReconnectionFeature;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.context.impl.ReconnectionContext;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditor;

/**
 * @author Bob Brodt
 *
 */
public class DataAssociationPropertiesAdapter extends ExtendedPropertiesAdapter<DataAssociation> {

	public final static String UI_SHOW_ITEMS_IN_SCOPE = "show.items.in.scope";
	
	/**
	 * @param adapterFactory
	 * @param object
	 */
	public DataAssociationPropertiesAdapter(AdapterFactory adapterFactory, DataAssociation object) {
		super(adapterFactory, object);

    	EStructuralFeature ref;
    	
    	ref = Bpmn2Package.eINSTANCE.getDataAssociation_SourceRef();
    	setFeatureDescriptor(ref, new SourceTargetFeatureDescriptor(this,object,ref));
		setProperty(ref, UI_CAN_EDIT_INLINE, Boolean.FALSE);
		setProperty(ref, UI_CAN_EDIT, Boolean.FALSE);
		setProperty(ref, UI_CAN_CREATE_NEW, Boolean.FALSE);
		setProperty(ref, UI_IS_MULTI_CHOICE, Boolean.TRUE);

		ref = Bpmn2Package.eINSTANCE.getDataAssociation_TargetRef();
    	setFeatureDescriptor(ref, new SourceTargetFeatureDescriptor(this,object,ref));
		setProperty(ref, UI_CAN_EDIT_INLINE, Boolean.FALSE);
		setProperty(ref, UI_CAN_EDIT, Boolean.FALSE);
		setProperty(ref, UI_CAN_CREATE_NEW, Boolean.FALSE);
		setProperty(ref, UI_IS_MULTI_CHOICE, Boolean.TRUE);
	}

	public class SourceTargetFeatureDescriptor extends FeatureDescriptor<DataAssociation> {

		public SourceTargetFeatureDescriptor(ExtendedPropertiesAdapter<DataAssociation> owner, DataAssociation object,
				EStructuralFeature feature) {
			super(owner, object, feature);
		}

		@Override
		public String getLabel() {
			if (object instanceof DataInputAssociation)
				return Messages.DataAssociationPropertiesAdapter_Source;
			return Messages.DataAssociationPropertiesAdapter_Target;
		}
		
		@Override
		public Hashtable<String, Object> getChoiceOfValues() {
			List<EObject> values = new ArrayList<EObject>();
			// search for all Properties and DataStores
			// Properties are contained in the nearest enclosing Process or Event;
			// DataStores are contained in the DocumentRoot
			EObject container = ModelUtil.getContainer(object);
			
			Object p = owner.getProperty(UI_SHOW_ITEMS_IN_SCOPE);
			if (p instanceof Boolean && (Boolean)p) {
				values.addAll( ModelUtil.collectAncestorObjects(container, "properties", new Class[] {Activity.class}) ); //$NON-NLS-1$
				values.addAll( ModelUtil.collectAncestorObjects(container, "properties", new Class[] {Process.class}) ); //$NON-NLS-1$
				values.addAll( ModelUtil.collectAncestorObjects(container, "properties", new Class[] {Event.class}) ); //$NON-NLS-1$
				values.addAll( ModelUtil.collectAncestorObjects(container, "dataStore", new Class[] {DocumentRoot.class}) ); //$NON-NLS-1$
				values.addAll( ModelUtil.collectAncestorObjects(container, "flowElements", new Class[] {FlowElementsContainer.class}, new Class[] {ItemAwareElement.class})); //$NON-NLS-1$
			}
			else {
				if (container instanceof Activity) {
					Activity activity = (Activity)container;
					if (object instanceof DataInputAssociation) {
						values.addAll(ModelUtil.getItemAwareElements(activity.getDataInputAssociations()));
					}
					else {
						values.addAll(ModelUtil.getItemAwareElements(activity.getDataOutputAssociations()));
					}
				}
				else if (container instanceof ThrowEvent) {
					ThrowEvent event = (ThrowEvent)container;
					values.addAll(ModelUtil.getItemAwareElements(event.getDataInputAssociation()));
				}
				else if (container instanceof CatchEvent) {
					CatchEvent event = (CatchEvent)container;
					values.addAll(ModelUtil.getItemAwareElements(event.getDataOutputAssociation()));
				}
			}
			super.setChoiceOfValues(values);
			return super.getChoiceOfValues();
		}
		
		@Override
		public EObject createFeature(Resource resource, EClass eClass) {
			// what kind of object should we create? Property or DataStore?
			if (eClass==null) {
				if (ModelUtil.findNearestAncestor(object, new Class[] {Process.class, Event.class}) != null)
					// nearest ancestor is a Process or Event, so new object will be a Property
					eClass = Bpmn2Package.eINSTANCE.getProperty();
				else if(ModelUtil.findNearestAncestor(object, new Class[] {DocumentRoot.class}) != null)
					eClass = Bpmn2Package.eINSTANCE.getDataStore();
			}			
			if (eClass!=null) {
				return Bpmn2ModelerFactory.create(resource, eClass);
			}
			return null;
		}
		
		@SuppressWarnings("unchecked")
		@Override
   		protected void internalSet(DataAssociation association, EStructuralFeature feature, Object value, int index) {
			EObject container = null;
			EStructuralFeature containerFeature = null;
			if (value instanceof Property) {
				if (((Property)value).eContainer()==null) {
					// this Property isn't owned by anything yet - figure out who the owner is
					container = ModelUtil.findNearestAncestor(association, new Class[] {Activity.class});
					if (container==null)
						container = ModelUtil.findNearestAncestor(association, new Class[] {Event.class});
					if (container==null)
						container = ModelUtil.findNearestAncestor(association, new Class[] {Process.class});
					containerFeature = container.eClass().getEStructuralFeature("properties"); //$NON-NLS-1$
				}
			}
			else if (value instanceof DataStore) {
				if (((DataStore)value).eContainer()==null) {
					// this DataStore isn't owned by anything yet - figure out who the owner is
					container = ModelUtil.findNearestAncestor(association, new Class[] {DocumentRoot.class});
					containerFeature = container.eClass().getEStructuralFeature("dataStore"); //$NON-NLS-1$
				}
			}
			else if (value instanceof String) {
				// first check if a property with this name already exists
				Hashtable<String, Object> choices = getChoiceOfValues();
				Property property = (Property)choices.get(value);
				if (property==null) {
					// need to create a new one!
					DiagramEditor editor = ModelUtil.getEditor(object);
					ModelEnablements modelEnablement =
							(ModelEnablements)editor.getAdapter(ModelEnablements.class);
					// find nearest element that can contain a Property and create one
					container = association;
					for (;;) {
						container = ModelUtil.findNearestAncestor(container, new Class[] {Activity.class, Event.class, Process.class});
						if (container==null)
							return;
						containerFeature = container.eClass().getEStructuralFeature("properties"); //$NON-NLS-1$
						if (modelEnablement.isEnabled(container.eClass(), containerFeature))
							break;
					}
						
					containerFeature = container.eClass().getEStructuralFeature("properties"); //$NON-NLS-1$
					property = Bpmn2ModelerFactory.create(Property.class);
					ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(property);
					adapter.getObjectDescriptor().setTextValue((String)value);
				}
				value = property;
			}

			final EObject c = container;
			final EStructuralFeature cf = containerFeature;
			final ItemAwareElement v = (ItemAwareElement)value;
			
			if (feature == Bpmn2Package.eINSTANCE.getDataAssociation_SourceRef()) {
				setSourceRef(association, v, c, cf);
			}
			else {
				setTargetRef(association, v, c, cf);
			}
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		private void setSourceRef(DataAssociation association, ItemAwareElement value, EObject container, EStructuralFeature containerFeature) {
			if (association.getSourceRef().size()==0) {
				if (container!=null) {
					if (containerFeature.isMany())
						((List)container.eGet(containerFeature)).add(value);
					else
						container.eSet(containerFeature, value);
				}
				if (value==null)
					association.getSourceRef().clear();
				else
					association.getSourceRef().add(value);
				updateConnectionIfNeeded(association, value);
			}
			else {
				if (container!=null) {
					if (containerFeature.isMany())
						((List)container.eGet(containerFeature)).add(value);
					else
						container.eSet(containerFeature, value);
				}
				updateConnectionIfNeeded(association, value);
				if (value==null)
					association.getSourceRef().clear();
				else
					association.getSourceRef().set(0,value);
				updateConnectionIfNeeded(association, value);
			}
			if (association.getTargetRef()!=null) {
				ItemAwareElement targetRef = association.getTargetRef();
				if (value!=null)
					targetRef.setItemSubjectRef(value.getItemSubjectRef());
				else
					targetRef.setItemSubjectRef(null);
				updateConnectionIfNeeded(association, value);
			}
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		private void setTargetRef(DataAssociation association, ItemAwareElement value, EObject container, EStructuralFeature containerFeature) {
			if (container!=null) {
				if (containerFeature.isMany())
					((List)container.eGet(containerFeature)).add(value);
				else
					container.eSet(containerFeature, value);
			}
			updateConnectionIfNeeded(association, value);
			association.setTargetRef(value);
			if (!association.getSourceRef().isEmpty()) {
				ItemAwareElement sourceRef = association.getSourceRef().get(0);
				if (value!=null)
					sourceRef.setItemSubjectRef(value.getItemSubjectRef());
				else
					sourceRef.setItemSubjectRef(null);
			}
			updateConnectionIfNeeded(association, value);
		}

		private void updateConnectionIfNeeded(DataAssociation association, ItemAwareElement value) {
			DiagramEditor diagramEditor = ModelUtil.getDiagramEditor(association);
			if (diagramEditor==null)
				return;
			boolean hasDoneChanges = false;
			Diagram diagram = diagramEditor.getDiagramTypeProvider().getDiagram();
			IFeatureProvider fp = diagramEditor.getDiagramTypeProvider().getFeatureProvider();
			Shape taskShape = null;
			EObject container = association.eContainer();
			if (container instanceof Activity || container instanceof Event) {
				for (PictogramElement pe : Graphiti.getLinkService().getPictogramElements(diagram, container)) {
					if (pe instanceof Shape && BusinessObjectUtil.getFirstElementOfType(pe, BPMNShape.class)!=null) {
						taskShape = (Shape) pe;
						break;
					}
				}
			}
			
			Shape dataShape = null;
			if (value instanceof DataObject ||
					value instanceof DataObjectReference ||
					value instanceof DataStore ||
					value instanceof DataStoreReference ||
					value instanceof DataInput ||
					value instanceof DataOutput) {
				List<PictogramElement> pes = Graphiti.getLinkService().getPictogramElements(diagram, (EObject)value);
				for (PictogramElement p : pes) {
					if (BusinessObjectUtil.getFirstElementOfType(p, BPMNShape.class)!=null) {
						dataShape = (Shape) p;
						break;
					}
				}
			}

			Connection connection = DataAssociationFeatureContainer.findDataAssociation(diagram, association);
			if (connection!=null) {
				// There's an existing DataAssociation connection which needs to
				// either be reconnected or deleted, depending on what the combobox
				// selection is.
				if (dataShape!=null) {
					// need to reconnect the DataAssociation
					ReconnectionContext rc = null;
					if (association instanceof DataInputAssociation) {
						Point p = GraphicsUtil.createPoint(connection.getStart());
						Anchor a = AnchorUtil.findNearestAnchor((AnchorContainer) dataShape, p);
						rc = new ReconnectionContext(connection, connection.getStart(), a, null);
						rc.setTargetPictogramElement(dataShape);
						rc.setTargetLocation(Graphiti.getPeService().getLocationRelativeToDiagram(a));
						rc.setReconnectType(ReconnectionContext.RECONNECT_SOURCE);
					}
					else {
						Point p = GraphicsUtil.createPoint(connection.getEnd());
						Anchor a = AnchorUtil.findNearestAnchor(dataShape, p);
						rc = new ReconnectionContext(connection, a, connection.getEnd(), null);
						rc.setTargetPictogramElement(dataShape);
						rc.setTargetLocation(Graphiti.getPeService().getLocationRelativeToDiagram(a));
						rc.setReconnectType(ReconnectionContext.RECONNECT_TARGET);
					}
					IReconnectionFeature rf = fp.getReconnectionFeature(rc);
					if (rf.canReconnect(rc)) {
						rf.reconnect(rc);
						hasDoneChanges = true;
					}
				}
				else {
					// need to delete the DataAssociation connection
					DeleteContext dc = new DeleteContext(connection);
					connection.getLink().getBusinessObjects().remove(0);
					IDeleteFeature df = fp.getDeleteFeature(dc);
					df.delete(dc);
				}
			}
			else if (dataShape!=null){
				// There is no existing DataAssociation connection, but the newly selected source or target
				// is some kind of data object shape, so we need to create a connection between the Activity
				// (or Throw/Catch Event) that owns the DataAssociation, and the new data object shape.
				Point p = GraphicsUtil.createPoint((AnchorContainer) dataShape);
				Anchor ownerAnchor = AnchorUtil.findNearestAnchor(taskShape, p);
				p = GraphicsUtil.createPoint(taskShape);
				Anchor peAnchor = AnchorUtil.findNearestAnchor((AnchorContainer) dataShape, p);
				AddConnectionContext ac = null;
				if (association instanceof DataOutputAssociation) {
					ac = new AddConnectionContext(ownerAnchor, peAnchor);
				}
				else {
					ac = new AddConnectionContext(peAnchor, ownerAnchor);
				}
				ac.putProperty(GraphitiConstants.BUSINESS_OBJECT, association);
				ac.setNewObject(association);
				IAddFeature af = fp.getAddFeature(ac);
				if (af.canAdd(ac)) {
					PictogramElement pe = af.add(ac);
					if (pe instanceof Connection) {
						connection = (Connection) pe;
						hasDoneChanges = true;
					}
				}
			}
			if (hasDoneChanges) {
				FeatureSupport.updateConnection(diagramEditor.getDiagramTypeProvider().getFeatureProvider(), connection);
			}
		}
	}
}
