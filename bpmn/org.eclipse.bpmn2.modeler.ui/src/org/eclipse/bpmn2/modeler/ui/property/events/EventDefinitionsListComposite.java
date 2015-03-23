/*******************************************************************************
 * Copyright (c) 2011, 2012 Red Hat, Inc. 
 * All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 *
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.property.events;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.CancelEventDefinition;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.ConditionalEventDefinition;
import org.eclipse.bpmn2.DataAssociation;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.Error;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.Escalation;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.LinkEventDefinition;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.OutputSet;
import org.eclipse.bpmn2.Signal;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.TerminateEventDefinition;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.DefaultDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.DefaultListComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.ListCompositeColumnProvider;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.PropertiesCompositeFactory;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.TableColumn;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.ModelSubclassSelectionDialog;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.core.utils.Tuple;
import org.eclipse.bpmn2.modeler.ui.property.tasks.DataAssociationDetailComposite;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class EventDefinitionsListComposite extends DefaultListComposite {
	
	protected Event event;
	
	public EventDefinitionsListComposite(Composite parent, Event event) {
		super(parent, DEFAULT_STYLE);
		this.event = event;
	}
	
	protected int createColumnProvider(EObject theobject, EStructuralFeature thefeature) {
		if (columnProvider==null) {
			getColumnProvider(theobject,thefeature);
		}
		return columnProvider.getColumns().size();
	}
	
	@Override
	protected EObject addListItem(final EObject object, EStructuralFeature feature) {
		EObject newItem = super.addListItem(object, feature);
		// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=417207
		// the Cancel Activity checkbox should always be TRUE
		// if the Boundary Event contains a Error Event Definition,
		// and should be hidden when it contains a Compensate Event Definition.
		if (object instanceof BoundaryEvent) {
			BoundaryEvent be = (BoundaryEvent) object;
			if (newItem instanceof ErrorEventDefinition) {
				be.setCancelActivity(true);
				((AbstractDetailComposite)getParent()).refresh();
			}
			else if (newItem instanceof CompensateEventDefinition) {
				be.setCancelActivity(false);
				((AbstractDetailComposite)getParent()).refresh();
			}
		}
		if (hasItemDefinition((EventDefinition)newItem)) {
			// create a new DataInput or DataOutput
			Tuple<ItemAwareElement,DataAssociation> param = getParameter((Event)object, (EventDefinition)newItem, true);
			param.getFirst().setId(null);
			ModelUtil.setID(param.getFirst(), object.eResource());
		}
		
//		Diagram diagram = getDiagramEditor().getDiagramTypeProvider().getDiagram();
//		IFeatureProvider fp = getDiagramEditor().getDiagramTypeProvider().getFeatureProvider();
//		PictogramElement pe = Graphiti.getLinkService().getPictogramElements(diagram, object).get(0);
//		AddContext context = new AddContext();
//		context.setTargetContainer((ContainerShape) pe);
//		context.setNewObject(newItem);
//		fp.addIfPossible(context);

		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (getPropertySection()!=null)
					getPropertySection().getSectionRoot().setBusinessObject(object);
			}
			
		});
		return newItem;
	}

	@Override
	protected Object removeListItem(final EObject object, EStructuralFeature feature, int index) {
		Object oldItem = getListItem(object,feature,index);
		if (hasItemDefinition((EventDefinition)oldItem)) {
			// remove this DataInput or DataOutput
			Tuple<ItemAwareElement,DataAssociation> param = getParameter((Event)object, (EventDefinition)oldItem, true);
			EcoreUtil.delete(param.getFirst());
			EcoreUtil.delete(param.getSecond());
		}
		Object newItem = super.removeListItem(object, feature, index);
		if (object instanceof BoundaryEvent) {
			BoundaryEvent be = (BoundaryEvent) object;
			if (oldItem instanceof ErrorEventDefinition) {
				be.setCancelActivity(true);
				((AbstractDetailComposite)getParent()).refresh();
			}
			else if (oldItem instanceof CompensateEventDefinition) {
				be.setCancelActivity(false);
				((AbstractDetailComposite)getParent()).refresh();
			}
		}
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (getPropertySection()!=null)
					getPropertySection().getSectionRoot().setBusinessObject(object);
			}
			
		});
		return newItem;
	}

	@Override
	protected Object moveListItemUp(EObject object, EStructuralFeature feature, int index) {
		Object result = super.moveListItemUp(object, feature, index);
		Object oldItem = getListItem(object,feature,index);
		Tuple<ItemAwareElement,DataAssociation> param = getParameter((Event)object, (EventDefinition)oldItem, true);
		if (object instanceof ThrowEvent) {
			// ThrowEvent input
			ThrowEvent te = (ThrowEvent) object;
			int i = te.getDataInputs().indexOf(param.getFirst());
			if (i>0) {
				te.getDataInputs().remove(i);
				te.getDataInputs().add(i-1, (DataInput) param.getFirst());
			}
		}
		else {
			// CatchEvent output
			CatchEvent te = (CatchEvent) object;
			int i = te.getDataOutputs().indexOf(param.getFirst());
			if (i>0) {
				te.getDataOutputs().remove(i);
				te.getDataOutputs().add(i-1, (DataOutput) param.getFirst());
			}
		}
		return result;
	}

	@Override
	protected Object moveListItemDown(EObject object, EStructuralFeature feature, int index) {
		Object result = super.moveListItemDown(object, feature, index);
		Object oldItem = getListItem(object,feature,index);
		Tuple<ItemAwareElement,DataAssociation> param = getParameter((Event)object, (EventDefinition)oldItem, true);
		if (object instanceof ThrowEvent) {
			// ThrowEvent input
			ThrowEvent te = (ThrowEvent) object;
			int i = te.getDataInputs().indexOf(param.getFirst());
			if (i>=0) {
				te.getDataInputs().remove(i);
				te.getDataInputs().add(i+1, (DataInput) param.getFirst());
			}
		}
		else {
			// CatchEvent output
			CatchEvent te = (CatchEvent) object;
			int i = te.getDataOutputs().indexOf(param.getFirst());
			if (i>=0) {
				te.getDataOutputs().remove(i);
				te.getDataOutputs().add(i+1, (DataOutput) param.getFirst());
			}
		}
		return result;
	}

	@Override
	public ListCompositeColumnProvider getColumnProvider(EObject object, EStructuralFeature feature) {
		columnProvider = super.getColumnProvider(object,feature);
		columnProvider.remove(0); // get rid of the ID column
		columnProvider.add(
				new TableColumn(object,feature) {
					public String getText(Object element) {
						EObject o = (EObject)element;
						return o.eClass().getName().replace("EventDefinition", ""); //$NON-NLS-1$ //$NON-NLS-2$
					}

					@Override
					public String getHeaderText() {
						return Messages.EventDefinitionsListComposite_Event_Type_Header;
					}
					
					@Override
					public CellEditor createCellEditor (Composite parent) {
						// need to override this to avoid any problems
						return super.createCellEditor(parent);
					}
				}
			).setEditable(false);
		columnProvider.addRaw(
				new TableColumn(object,object.eClass().getEStructuralFeature("id")) { //$NON-NLS-1$
					public String getText(Object element) {
						if (element instanceof CancelEventDefinition) {
						}
						if (element instanceof CompensateEventDefinition) {
							if (((CompensateEventDefinition)element).getActivityRef()!=null)
								return ((CompensateEventDefinition)element).getActivityRef().getId();
						}
						if (element instanceof ConditionalEventDefinition) {
							if (((ConditionalEventDefinition)element).getCondition()!=null)
								return ExtendedPropertiesProvider.getTextValue(((ConditionalEventDefinition)element).getCondition());
						}
						if (element instanceof ErrorEventDefinition) {
							if (((ErrorEventDefinition)element).getErrorRef()!=null)
								return ((ErrorEventDefinition)element).getErrorRef().getId();
						}
						if (element instanceof EscalationEventDefinition) {
							if (((EscalationEventDefinition)element).getEscalationRef()!=null)
								return ((EscalationEventDefinition)element).getEscalationRef().getId();
						}
						if (element instanceof LinkEventDefinition) {
						}
						if (element instanceof MessageEventDefinition) {
							if (((MessageEventDefinition)element).getMessageRef()!=null)
								return ((MessageEventDefinition)element).getMessageRef().getId();
						}
						if (element instanceof SignalEventDefinition) {
							if (((SignalEventDefinition)element).getSignalRef()!=null)
								return ((SignalEventDefinition)element).getSignalRef().getId();
						}
						if (element instanceof TerminateEventDefinition) {
						}
						if (element instanceof TimerEventDefinition) {
						}
						return ""; //$NON-NLS-1$
					}

					@Override
					public String getHeaderText() {
						return Messages.EventDefinitionsListComposite_Event_ID_Header;
					}
					
					@Override
					public CellEditor createCellEditor (Composite parent) {
						// need to override this to avoid any problems
						return super.createCellEditor(parent);
					}
				}
			).setEditable(false);
		return columnProvider;
	}
	
	@Override
	public EClass getListItemClassToAdd(EClass listItemClass) {
		EClass eclass = null;
		ModelSubclassSelectionDialog dialog = new ModelSubclassSelectionDialog(getDiagramEditor(), businessObject, feature) {
			@Override
			protected void filterList(List<EClass> items) {
				List<EClass> filteredItems = new ArrayList<EClass>();
				List<EClass> allowedItems = FeatureSupport.getAllowedEventDefinitions(event, null);
				for (EClass eclass : items) {
					if (allowedItems.contains(eclass))
						filteredItems.add(eclass);
				}
				items.clear();
				items.addAll(filteredItems);
			}
		};
		
		if (dialog.open()==Window.OK){
			eclass = (EClass)dialog.getResult()[0];
		}
		return eclass;
	}
	
	public static ItemDefinition getItemDefinition(EventDefinition eventDefinition) {
		ItemDefinition itemDefinition = null;
		if (eventDefinition instanceof ErrorEventDefinition) {
			Error payloadContainer = ((ErrorEventDefinition)eventDefinition).getErrorRef();
			itemDefinition = payloadContainer==null ? null : payloadContainer.getStructureRef();
		}
		if (eventDefinition instanceof EscalationEventDefinition) {
			Escalation payloadContainer = ((EscalationEventDefinition)eventDefinition).getEscalationRef();
			itemDefinition = payloadContainer==null ? null : payloadContainer.getStructureRef();
		}
		if (eventDefinition instanceof SignalEventDefinition) {
			Signal payloadContainer = ((SignalEventDefinition)eventDefinition).getSignalRef();
			itemDefinition = payloadContainer==null ? null : payloadContainer.getStructureRef();
		}
		if (eventDefinition instanceof MessageEventDefinition) {
			Message payloadContainer = ((MessageEventDefinition)eventDefinition).getMessageRef();
			itemDefinition = payloadContainer==null ? null : payloadContainer.getItemRef();
		}
		return itemDefinition;
	}
	
	public static boolean hasItemDefinition(EventDefinition eventDefinition) {
		return (eventDefinition instanceof ErrorEventDefinition ||
			eventDefinition instanceof EscalationEventDefinition ||
			eventDefinition instanceof SignalEventDefinition ||
			eventDefinition instanceof MessageEventDefinition);
	}
	
	public static Tuple<ItemAwareElement, DataAssociation> getParameter(Event event, EventDefinition eventDefinition, boolean inTransaction) {
		Resource resource = event.eResource();
		ItemAwareElement element = null;
		DataAssociation association = null;
		BaseElement ioSet = null;
		List<EventDefinition> eventDefinitions = null;
		List<ItemAwareElement> parameters = null;
		List<DataAssociation> associations = null;
		ThrowEvent throwEvent = null;
		CatchEvent catchEvent = null;
		boolean isInput = false;
		if (event instanceof ThrowEvent) {
			throwEvent = (ThrowEvent)event;
			eventDefinitions = throwEvent.getEventDefinitions();
			parameters = (List)throwEvent.getDataInputs();
			associations = (List)throwEvent.getDataInputAssociation();
			ioSet = throwEvent.getInputSet();
			isInput = true;
		}
		else {
			catchEvent = (CatchEvent)event;
			eventDefinitions = catchEvent.getEventDefinitions();
			parameters = (List)catchEvent.getDataOutputs();
			associations = (List)catchEvent.getDataOutputAssociation();
			ioSet = catchEvent.getOutputSet();
		}
		
		int index = -1;
		for (EventDefinition ed : eventDefinitions) {
			element = null;
			association = null;
			if (hasItemDefinition(ed)) {
				ItemDefinition itemDefinition = getItemDefinition(ed);
				++index;
				if (parameters.size()<=index) {
					if (!inTransaction)
						throw new IllegalStateException(Messages.EventDefinitionsListComposite_No_Transaction);
					String name = ed.getId().replace("EventDefinition", ""); //$NON-NLS-1$ //$NON-NLS-2$
					if (isInput) {
						element = Bpmn2ModelerFactory.create(resource, DataInput.class);
						((DataInput)element).setName(name+"_Input"); //$NON-NLS-1$
					}
					else {
						element = Bpmn2ModelerFactory.create(resource, DataOutput.class);
						((DataOutput)element).setName(name+"_Output"); //$NON-NLS-1$
					}
					if (itemDefinition!=null) {
						element.setItemSubjectRef(itemDefinition);
					}
					parameters.add(element);
				}
				else {
					element = parameters.get(index);
				}
				if (isInput) {
					for (DataAssociation a : associations) {
						if (a.getTargetRef() == element) {
							association = a;
							break;
						}
					}
					if (association==null) {
						if (!inTransaction)
							throw new IllegalStateException(Messages.EventDefinitionsListComposite_No_Transaction);
						association = Bpmn2ModelerFactory.create(resource, DataInputAssociation.class);
						association.setTargetRef(element);
						associations.add(association);
					}
				}
				else {
					for (DataAssociation a : associations) {
						if (a.getSourceRef().contains(element)) {
							association = a;
							break;
						}
					}
					if (association==null) {
						if (!inTransaction)
							throw new IllegalStateException(Messages.EventDefinitionsListComposite_No_Transaction);
						association = Bpmn2ModelerFactory.create(resource, DataOutputAssociation.class);
						if (element!=null)
							association.getSourceRef().add(element);
						associations.add(association);
					}
				}
				if (ioSet==null) {
					if (!inTransaction)
						throw new IllegalStateException(Messages.EventDefinitionsListComposite_No_Transaction);
					if (isInput) {
						ioSet = (BaseElement) Bpmn2ModelerFactory.create(resource, InputSet.class);
						throwEvent.setInputSet((InputSet)ioSet);
					}
					else {
						ioSet = (BaseElement) Bpmn2ModelerFactory.create(resource, OutputSet.class);
						catchEvent.setOutputSet((OutputSet)ioSet);
					}
				}
				if (isInput) {
					if (!((InputSet)ioSet).getDataInputRefs().contains(element)) {
						if (!inTransaction)
							throw new IllegalStateException(Messages.EventDefinitionsListComposite_No_Transaction);
						((InputSet)ioSet).getDataInputRefs().add((DataInput)element);
					}
				}
				else {
					if (!((OutputSet)ioSet).getDataOutputRefs().contains(element)) {
						if (!inTransaction)
							throw new IllegalStateException(Messages.EventDefinitionsListComposite_No_Transaction);
						((OutputSet)ioSet).getDataOutputRefs().add((DataOutput)element);
					}
				}
			}
			if (ed==eventDefinition)
				break;
		}
		return new Tuple(element,association);
	}

	public AbstractDetailComposite createDetailComposite(Class eClass, Composite parent, int style) {
		AbstractDetailComposite detailComposite = PropertiesCompositeFactory.INSTANCE.createDetailComposite(eClass, parent, style);
		if (detailComposite!=null)
			return detailComposite;
		
		if (eClass==TimerEventDefinition.class) {
			return new TimerEventDefinitionDetailComposite(parent, style);
		}
		if (eClass==ConditionalEventDefinition.class){
			return new ConditionalEventDefinitionDetailComposite(parent, style);
		}
		return new EventDefinitionsDetailComposite(parent, (Event)getBusinessObject());
	}
	
	public class EventDefinitionsDetailComposite extends DefaultDetailComposite {

		protected Event event;
		protected EventDefinition eventDefinition;
		protected DataAssociationDetailComposite dataAssociationComposite;

		public EventDefinitionsDetailComposite(Composite parent, Event event) {
			super(parent, SWT.NONE);
			this.event = event;
		}

		@Override
		protected void cleanBindings() {
			super.cleanBindings();
			dataAssociationComposite = null;
		}

		@Override
		public void createBindings(EObject be) {
			super.createBindings(be);
			
			eventDefinition = (EventDefinition) be;
			
			if (dataAssociationComposite==null) {
				dataAssociationComposite = new DataAssociationDetailComposite(getAttributesParent(), SWT.NONE);
			}
			
			if (hasItemDefinition(eventDefinition)) {
				dataAssociationComposite.setVisible(true);
				if (event instanceof ThrowEvent)
					dataAssociationComposite.setShowToGroup(false);
				else
					dataAssociationComposite.setShowFromGroup(false);
				
				// determine the correct I/O Parameter (DataInput or DataOutput) for this Event Definition
				Tuple<ItemAwareElement,DataAssociation> param = null;
				try {
					param = getParameter(event, eventDefinition, false);
				}
				catch (IllegalStateException e) {
					// The model is corrupt because it is missing one or more required BPMN2 model elements.
					// Create a transaction to add the missing elements.
					final Object result[] = new Object[1];
					TransactionalEditingDomain domain = getDiagramEditor().getEditingDomain();
					domain.getCommandStack().execute(new RecordingCommand(domain) {
						@Override
						protected void doExecute() {
							result[0] = getParameter(event, eventDefinition, true);
						}
					});
					param = (Tuple<ItemAwareElement, DataAssociation>) result[0];
				}
				if (param!=null) {
					dataAssociationComposite.setBusinessObject(param.getFirst());
					String type = eventDefinition.eClass().getName().replace("EventDefinition", ""); //$NON-NLS-1$ //$NON-NLS-2$
					if (event instanceof ThrowEvent) {
						dataAssociationComposite.getFromGroup().setText(
							NLS.bind(
								Messages.EventDefinitionsListComposite_Map_Outgoing,
								type
							)
						);
					}
					else {
						dataAssociationComposite.getToGroup().setText(
							NLS.bind(
								Messages.EventDefinitionsListComposite_Map_Incoming,
								type
							)
						);
					}
				}
			}
			else {
				dataAssociationComposite.setVisible(false);
			}
		}
		
		public DataAssociationDetailComposite getDataAssociationComposite() {
			return dataAssociationComposite;
		}
	}
}