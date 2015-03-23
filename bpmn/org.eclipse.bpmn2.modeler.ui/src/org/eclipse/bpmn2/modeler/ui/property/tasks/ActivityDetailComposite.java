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


package org.eclipse.bpmn2.modeler.ui.property.tasks;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.CallableElement;
import org.eclipse.bpmn2.Choreography;
import org.eclipse.bpmn2.Collaboration;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.LoopCharacteristics;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.MultiInstanceLoopCharacteristics;
import org.eclipse.bpmn2.Operation;
import org.eclipse.bpmn2.OutputSet;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.ReceiveTask;
import org.eclipse.bpmn2.SendTask;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.StandardLoopCharacteristics;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BpmnDiFactory;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.InsertionAdapter;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractBpmn2PropertySection;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.DefaultDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.PropertiesCompositeFactory;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.ComboObjectEditor;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.ObjectEditor;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.ui.adapters.properties.DataInputPropertiesAdapter;
import org.eclipse.bpmn2.modeler.ui.adapters.properties.DataOutputPropertiesAdapter;
import org.eclipse.bpmn2.modeler.ui.property.editors.ServiceImplementationObjectEditor;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.wsdl.WSDLElement;

public class ActivityDetailComposite extends DefaultDetailComposite {

	protected Button noneButton;
	protected Button addStandardLoopButton;
	protected Button addMultiLoopButton;
	protected AbstractDetailComposite loopCharacteristicsComposite;
	
	protected DataAssociationDetailComposite inputComposite;
	protected DataAssociationDetailComposite outputComposite;
	
	protected ServiceImplementationObjectEditor implementationEditor = null;
	
	public ActivityDetailComposite(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @param section
	 */
	public ActivityDetailComposite(AbstractBpmn2PropertySection section) {
		super(section);
	}

	@Override
	public void cleanBindings() {
		super.cleanBindings();
		noneButton = null;
		addStandardLoopButton = null;
		addMultiLoopButton = null;
		loopCharacteristicsComposite = null;
		inputComposite = null;
		outputComposite = null;
	}
	
	@Override
	public AbstractPropertiesProvider getPropertiesProvider(EObject object) {
		if (propertiesProvider==null) {
			propertiesProvider = new AbstractPropertiesProvider(object) {
				String[] properties = new String[] {
						"anyAttribute", //$NON-NLS-1$
						"calledElementRef", // only used in CallActivity //$NON-NLS-1$
						"calledChoreographyRef", // only used in CallChoreography //$NON-NLS-1$
						"calledCollaborationRef", // only used in CallConversation //$NON-NLS-1$
						"implementation", // used by BusinessRuleTask, SendTask, ReceiveTask, UserTask and ServiceTask //$NON-NLS-1$
						"operationRef", // SendTask, ReceiveTask, ServiceTask //$NON-NLS-1$
						"messageRef", // SendTask, ReceiveTask //$NON-NLS-1$
						"scriptFormat", "script", // ScriptTask //$NON-NLS-1$ //$NON-NLS-2$
						"instantiate", // ReceiveTask //$NON-NLS-1$
						//"startQuantity", // these are "MultipleAssignments" features and should be used
						//"completionQuantity", // with caution, according to the BPMN 2.0 spec
						"triggeredByEvent", //$NON-NLS-1$
						"cancelRemainingInstances", //$NON-NLS-1$
						"ordering", //$NON-NLS-1$
						"completionCondition", //$NON-NLS-1$
						"method", //$NON-NLS-1$
						"protocol", //$NON-NLS-1$

						"isForCompensation", //$NON-NLS-1$
						"loopCharacteristics", //$NON-NLS-1$
						"properties", //$NON-NLS-1$
						"resources", //$NON-NLS-1$
				};
				
				@Override
				public String[] getProperties() {
					return properties; 
				}
			};
		}
		return propertiesProvider;
	}
	
	protected void bindAttribute(Composite parent, EObject object, EAttribute attribute, String label) {
		if ("implementation".equals(attribute.getName())) { //$NON-NLS-1$
			implementationEditor = new ServiceImplementationObjectEditor(this,object,attribute);
			implementationEditor.createControl(parent,label);
		}
		else
			super.bindAttribute(parent, object, attribute, label);
	}
	
	protected void bindReference(final Composite parent, final EObject object, final EReference reference) {
		if (!isModelObjectEnabled(object.eClass(), reference))
			return;
		
		if ("loopCharacteristics".equals(reference.getName())) { //$NON-NLS-1$
			final Activity activity = (Activity) businessObject;
			LoopCharacteristics loopCharacteristics = (LoopCharacteristics) activity.getLoopCharacteristics();
				
			Composite composite = getAttributesParent();

			createLabel(composite, Messages.ActivityDetailComposite_Loop_Characteristics_Label);
			
			Composite buttonComposite = toolkit.createComposite(composite);
			buttonComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
			FillLayout layout = new FillLayout();
			layout.marginWidth = 20;
			buttonComposite.setLayout(layout);
			
			noneButton = toolkit.createButton(buttonComposite, Messages.ActivityDetailComposite_None, SWT.RADIO);
			noneButton.setSelection(loopCharacteristics == null);
			noneButton.addSelectionListener(new SelectionAdapter() {
				
				public void widgetSelected(SelectionEvent e) {
					if (noneButton.getSelection()) {
						@SuppressWarnings("restriction")
						TransactionalEditingDomain domain = getDiagramEditor().getEditingDomain();
						domain.getCommandStack().execute(new RecordingCommand(domain) {
							@Override
							protected void doExecute() {
								if (activity.getLoopCharacteristics() !=null)
									activity.setLoopCharacteristics(null);
								setBusinessObject(activity);
							}
						});
					}
				}
			});
			
			addStandardLoopButton = toolkit.createButton(buttonComposite, Messages.ActivityDetailComposite_Standard, SWT.RADIO);
			addStandardLoopButton.setSelection(loopCharacteristics instanceof StandardLoopCharacteristics);
			addStandardLoopButton.addSelectionListener(new SelectionAdapter() {
				
				public void widgetSelected(SelectionEvent e) {
					if (addStandardLoopButton.getSelection()) {
						@SuppressWarnings("restriction")
						TransactionalEditingDomain domain = getDiagramEditor().getEditingDomain();
						domain.getCommandStack().execute(new RecordingCommand(domain) {
							@Override
							protected void doExecute() {
								StandardLoopCharacteristics loopChar = createModelObject(StandardLoopCharacteristics.class);
								activity.setLoopCharacteristics(loopChar);
								setBusinessObject(activity);
							}
						});
					}
				}
			});

			addMultiLoopButton = toolkit.createButton(buttonComposite, Messages.ActivityDetailComposite_MultiInstance, SWT.RADIO);
			addMultiLoopButton.setSelection(loopCharacteristics instanceof MultiInstanceLoopCharacteristics);
			addMultiLoopButton.addSelectionListener(new SelectionAdapter() {
				
				public void widgetSelected(SelectionEvent e) {
					if (addMultiLoopButton.getSelection()) {
						@SuppressWarnings("restriction")
						TransactionalEditingDomain domain = getDiagramEditor().getEditingDomain();
						domain.getCommandStack().execute(new RecordingCommand(domain) {
							@Override
							protected void doExecute() {
								MultiInstanceLoopCharacteristics loopChar = createModelObject(MultiInstanceLoopCharacteristics.class);
								activity.setLoopCharacteristics(loopChar);
								setBusinessObject(activity);
							}
						});
					}
				}
			});
			
			if (loopCharacteristics != null) {
				loopCharacteristicsComposite = PropertiesCompositeFactory.INSTANCE.createDetailComposite(
						loopCharacteristics.eClass().getInstanceClass(), composite, SWT.NONE);
				loopCharacteristicsComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
				loopCharacteristicsComposite.setBusinessObject(loopCharacteristics);
				loopCharacteristicsComposite.setTitle(loopCharacteristics instanceof StandardLoopCharacteristics ?
						Messages.ActivityDetailComposite_Standard_Loop_Characteristics_Label : Messages.ActivityDetailComposite_MultiInstance_Loop_Characteristics_Label);
			}
			else if (loopCharacteristicsComposite!=null) {
				loopCharacteristicsComposite.dispose();
				loopCharacteristicsComposite = null;
			}

		}
		else if ("calledElementRef".equals(reference.getName())) { //$NON-NLS-1$
			// Handle CallActivity.calledElementRef
			//
			String displayName = getBusinessObjectDelegate().getLabel(object, reference);
			ObjectEditor editor = new ComboObjectEditor(this,object,reference) {
				// handle creation of new target elements here:
				protected EObject createObject() throws Exception {
					CallableElement calledElement = (CallableElement)super.createObject();
					// create a new diagram for the CallableElement
					if (calledElement instanceof Process) {
						createNewDiagram(calledElement);
					}
					return calledElement;
				}
			};
			editor.createControl(parent,displayName);
		}
		else if ("calledChoreographyRef".equals(reference.getName())) { //$NON-NLS-1$
			// Handle CallChoreography.calledChoreographyRef
			//
			// FIXME: This section should really be in a different detail composite class.
			// This detail composite is intended for Activity elements and their subclasses
			// but a CallChoreography is a ChoreographyActivity, not a subclass of Activity.
			// See the "static" initializers section of BPMN2Editor.
			// For now, this will have to do...
			String displayName = getBusinessObjectDelegate().getLabel(object, reference);
			ObjectEditor editor = new ComboObjectEditor(this,object,reference) {
				// handle creation of new target elements here:
				protected EObject createObject() throws Exception {
					Choreography choreography = (Choreography)super.createObject();
					// create a new diagram for the Choreography
					createNewDiagram(choreography);
					return choreography;
				}
			};
			editor.createControl(parent,displayName);
		}
		else if ("calledCollaborationRef".equals(reference.getName())) { //$NON-NLS-1$
			// Handle CallConversation.calledCollaborationRef
			//
			// FIXME: This section should really be in a different detail composite class.
			// This detail composite is intended for Activity elements and their subclasses
			// but a CallConversation is a ChoreographyNode, not a subclass of Activity.
			// See the "static" initializers section of BPMN2Editor.
			// For now, this will have to do...
			String displayName = getBusinessObjectDelegate().getLabel(object, reference);
			ObjectEditor editor = new ComboObjectEditor(this,object,reference) {
				// handle creation of new target elements here:
				protected EObject createObject() throws Exception {
					Collaboration collaboration = (Collaboration)super.createObject();
					// create a new diagram for the Collaboration
					createNewDiagram(collaboration);
					return collaboration;
				}
			};
			editor.createControl(parent,displayName);
		}
		else if ("operationRef".equals(reference.getName())) { //$NON-NLS-1$
			EReference messageRef = (EReference) object.eClass().getEStructuralFeature("messageRef"); //$NON-NLS-1$
			bindOperationMessageRef(getAttributesParent(), (Activity)object, reference, messageRef);
		}
		else if ("messageRef".equals(reference.getName())) { //$NON-NLS-1$
			return; // already done
		}
		else if ("completionCondition".equals(reference.getName())) { //$NON-NLS-1$
			super.bindReference(parent, object, reference);
		}
		else
			super.bindReference(parent, object, reference);
		
		redrawPage();
	}
	
	private void bindOperationMessageRef(final Composite container, final Activity activity, final EReference operationRef, final EReference messageRef) {
		final String operationLabel = getBusinessObjectDelegate().getLabel(activity, operationRef);
		final ObjectEditor operationEditor = new ComboObjectEditor(this,activity,operationRef) {
			@Override
			protected boolean setValue(final Object result) {
				TransactionalEditingDomain domain = getDiagramEditor().getEditingDomain();
				domain.getCommandStack().execute(new RecordingCommand(domain) {
					@Override
					protected void doExecute() {
						Operation operation = null;
						Message message = null;
						if (result instanceof Operation)
							operation = (Operation)result;
						if (messageRef!=null)
							message = (Message) activity.eGet(messageRef);
							
						createMessageAssociations(container, activity,
								operationRef, operation,
								messageRef, message);

						// This was me, trying to be too smart:
						// if the selected Operation is a WSDL Operation,
						// then set the service implementation to ##WebService.
						// This is not always desired behavior.
//						if (implementationEditor!=null) {
//							String imp = null;
//							if ( operation!=null) {
//								// If the Interface is defined by a WSDL set the default
//								// service implementation as ##WebService, otherwise
//								// use ##unspecified
//								if (operation.getImplementationRef() instanceof WSDLElement)
//									imp = ServiceImplementationObjectEditor.WEBSERVICE_VALUE;
//								else
//									imp = ServiceImplementationObjectEditor.UNSPECIFIED_VALUE;
//							}
//							implementationEditor.setValue(imp);
//						}
					}
				});
				return true;
			}

			@Override
			protected boolean canSetNull() {
				return true;
			}
		};
		operationEditor.createControl(container,operationLabel);
		
		if (messageRef!=null) {
			final String messageLabel = getBusinessObjectDelegate().getLabel(activity, messageRef);
			final ObjectEditor messageEditor = new ComboObjectEditor(this,activity,messageRef) {
				@Override
				protected boolean setValue(final Object result) {
					TransactionalEditingDomain domain = getDiagramEditor().getEditingDomain();
					domain.getCommandStack().execute(new RecordingCommand(domain) {
						@Override
						protected void doExecute() {
							Operation operation = (Operation) activity.eGet(operationRef);
							Message message = (Message) activity.eGet(messageRef);
							if (result instanceof String && ((String)result).isEmpty())
								message = null;
							if (result instanceof Message)
								message = (Message)result;
							createMessageAssociations(container, activity,
									operationRef, operation,
									messageRef, message);
						}
					});
					return true;
				}
				
				@Override
				protected boolean canSetNull() {
					return true;
				}
			};
			messageEditor.createControl(container,messageLabel);
		}
		
		Operation operation = null;
		Message message = null;
		if (operationRef!=null)
			operation = (Operation)activity.eGet(operationRef);
		if (messageRef!=null)
			message = (Message) activity.eGet(messageRef);

		createMessageAssociations(container, activity,
				operationRef, operation,
				messageRef, message);
	}
	
	protected void createMessageAssociations(Composite container, final Activity activity,
			EReference operationRef, Operation operation,
			EReference messageRef, Message message
	) {
		
		Operation oldOperation = (Operation) activity.eGet(operationRef);
		boolean operationChanged = (oldOperation != operation);
		boolean messageChanged = false;
		ExtendedPropertiesAdapter activityAdapter = ExtendedPropertiesAdapter.adapt(activity);
		if (operationChanged) {
			activityAdapter.getFeatureDescriptor(operationRef).setValue(operation);
			if (operation!=null) {
				if (activity instanceof ReceiveTask)
					activityAdapter.getFeatureDescriptor(messageRef).setValue(operation.getInMessageRef());
				else if (activity instanceof SendTask)
					activityAdapter.getFeatureDescriptor(messageRef).setValue(operation.getOutMessageRef());
			}
		}
		
		if (messageRef!=null) {
			Message oldMessage = (Message) activity.eGet(messageRef);
			messageChanged = (oldMessage != message);
			if (messageChanged)
				activityAdapter.getFeatureDescriptor(messageRef).setValue(message);
		}

		if (inputComposite==null) {
			inputComposite = new DataAssociationDetailComposite(container, SWT.NONE);
			inputComposite.setShowToGroup(false);
		}
//		inputComposite.setShowItemsInScope(false);

		if (outputComposite==null) {
			outputComposite = new DataAssociationDetailComposite(container, SWT.NONE);
			outputComposite.setShowFromGroup(false);
		}
//		outputComposite.setShowItemsInScope(false);
		
		Resource resource = activity.eResource();
		InputOutputSpecification ioSpec = activity.getIoSpecification();
		if (ioSpec==null) {
			ioSpec = Bpmn2ModelerFactory.create(resource, InputOutputSpecification.class);
			ModelUtil.setID(ioSpec, resource);
			if (operationChanged) {
				activity.setIoSpecification(ioSpec);
			}
		}
		if (ioSpec.getInputSets().size()==0) {
			final InputSet inputSet = Bpmn2ModelerFactory.create(resource, InputSet.class);
			ModelUtil.setID(inputSet);
			if (operationChanged || ioSpec.eContainer()==null)
			{
				ioSpec.getInputSets().add(inputSet);
			}
			else {
				InsertionAdapter.add(ioSpec, Bpmn2Package.eINSTANCE.getInputOutputSpecification_InputSets(), inputSet);
			}
		}
		if (ioSpec.getOutputSets().size()==0) {
			final OutputSet outputSet = Bpmn2ModelerFactory.create(resource, OutputSet.class);
			ModelUtil.setID(outputSet);
			if (operationChanged || ioSpec.eContainer()==null)
			{
				ioSpec.getOutputSets().add(outputSet);
			}
			else {
				InsertionAdapter.add(ioSpec, Bpmn2Package.eINSTANCE.getInputOutputSpecification_OutputSets(), outputSet);
			}
		}
		DataInput input = null;
		DataOutput output = null;
		Message inMessage = null;
		Message outMessage = null;
		if (operation!=null) {
			inMessage = (activity instanceof ServiceTask) ? operation.getInMessageRef() : operation.getOutMessageRef();
			outMessage = (activity instanceof ServiceTask) ? operation.getOutMessageRef() : operation.getInMessageRef();
		}
		else if (message!=null) {
			if (activity instanceof SendTask)
				inMessage = message;
			else if (activity instanceof ReceiveTask)
				outMessage = message;
		}
		else {
			if (activity instanceof SendTask)
				message = inMessage = ((SendTask) activity).getMessageRef();
			else if (activity instanceof ReceiveTask)
				message = outMessage = ((ReceiveTask) activity).getMessageRef();
		}
		
		if (activity instanceof SendTask)
			outMessage = null;
		else if (activity instanceof ReceiveTask)
			inMessage = null;

		if (inMessage!=null) {
			// display the "From" association widgets
			boolean newInput = false;
			if (ioSpec.getDataInputs().size()>0) {
				input = ioSpec.getDataInputs().get(0);
			}
			else {
				input = Bpmn2ModelerFactory.create(resource, DataInput.class);
				input.setName( DataInputPropertiesAdapter.generateName(ioSpec.getDataInputs()) );
				newInput = true;
			}
			if (operationChanged || messageChanged) {
				ItemDefinition id = inMessage.getItemRef();
				if (id!=null && id!=input.getItemSubjectRef()) {
					input.setItemSubjectRef(id);
					input.setIsCollection(id.isIsCollection());
				}
			}
			if (newInput) {
				if (operationChanged || messageChanged || ioSpec.eContainer()==null)
				{
					ioSpec.getDataInputs().add(input);
					ioSpec.getInputSets().get(0).getDataInputRefs().add(input);
				}
				else {
					InsertionAdapter.add(ioSpec,
							Bpmn2Package.eINSTANCE.getInputOutputSpecification_DataInputs(), input);
					InsertionAdapter.add(ioSpec.getInputSets().get(0),
							Bpmn2Package.eINSTANCE.getInputSet_DataInputRefs(), input);
				}
			}
		}
		
		if (outMessage!=null) {
			boolean newOutput = false;
			if (ioSpec.getDataOutputs().size()>0) {
				output = ioSpec.getDataOutputs().get(0);
			}
			else {
				output = Bpmn2ModelerFactory.create(resource, DataOutput.class);
				output.setName( DataOutputPropertiesAdapter.generateName(ioSpec.getDataOutputs()) );
				newOutput = true;
			}
			if (operationChanged || messageChanged) {
				ItemDefinition id = outMessage.getItemRef();
				if (id!=null && id!=input.getItemSubjectRef()) {
					output.setItemSubjectRef(id);
					output.setIsCollection(id.isIsCollection());
				}
			}
			if (newOutput) {
				if (operationChanged || messageChanged ||ioSpec.eContainer()==null)
				{
					ioSpec.getDataOutputs().add(output);
					ioSpec.getOutputSets().get(0).getDataOutputRefs().add(output);
				}
				else {
					InsertionAdapter.add(ioSpec,
							Bpmn2Package.eINSTANCE.getInputOutputSpecification_DataOutputs(), output);
					InsertionAdapter.add(ioSpec.getOutputSets().get(0),
							Bpmn2Package.eINSTANCE.getOutputSet_DataOutputRefs(), output);
				}
			}
		}
		
		if (ioSpec.getDataInputs().size()>0) {
			input = ioSpec.getDataInputs().get(0);
			// fix missing InputSet
			final InputSet inputSet = ioSpec.getInputSets().get(0);
			if (!inputSet.getDataInputRefs().contains(input)) {
				final DataInput i = input;
				TransactionalEditingDomain domain = getDiagramEditor().getEditingDomain();
				domain.getCommandStack().execute(new RecordingCommand(domain) {
					@Override
					protected void doExecute() {
						inputSet.getDataInputRefs().add(i);
					}
				});
			}
		}
		if (ioSpec.getDataOutputs().size()>0) {
			output = ioSpec.getDataOutputs().get(0);
			// fix missing OutputSet
			final OutputSet outputSet = ioSpec.getOutputSets().get(0);
			if (!outputSet.getDataOutputRefs().contains(output)) {
				final DataOutput o = output;
				TransactionalEditingDomain domain = getDiagramEditor().getEditingDomain();
				domain.getCommandStack().execute(new RecordingCommand(domain) {
					@Override
					protected void doExecute() {
						outputSet.getDataOutputRefs().add(o);
					}
				});
			}
		}
		
		// Attach the I/O Spec to the Activity if it is not already contained
		if (ioSpec.eContainer()==null) {
			InsertionAdapter.add(activity, Bpmn2Package.eINSTANCE.getActivity_IoSpecification(), ioSpec);
		}
		
		if (activity instanceof ServiceTask) {
			if (inMessage!=null) {
				// display the "From" association widgets
				inputComposite.setVisible(true);
				inputComposite.setBusinessObject(input);
				inputComposite.getFromGroup().setText(Messages.ActivityDetailComposite_Map_Request_Message);
			}
			else
				inputComposite.setVisible(false);
			
			if (outMessage!=null) {
				outputComposite.setVisible(true);
				outputComposite.setBusinessObject(output);
				outputComposite.getToGroup().setText(Messages.ActivityDetailComposite_Map_Response_Message);
			}
			else
				outputComposite.setVisible(false);
		}
		else if (activity instanceof SendTask) {
			if (inMessage!=null) {
				inputComposite.setVisible(true);
				inputComposite.setBusinessObject(input);
				inputComposite.getFromGroup().setText(Messages.ActivityDetailComposite_Map_Outgoing_Message);
			}
			else
				inputComposite.setVisible(false);
		}
		else if (activity instanceof ReceiveTask) {
			if (outMessage!=null) {
				outputComposite.setVisible(true);
				outputComposite.setBusinessObject(output);
				outputComposite.getToGroup().setText(Messages.ActivityDetailComposite_Map_Incoming_Message);
			}
			else
				outputComposite.setVisible(false);
		}

		if (operationChanged || messageChanged) {
			redrawPage();
		}
	}
	
	private void createNewDiagram(final BaseElement bpmnElement) {
		final Definitions definitions = ModelUtil.getDefinitions(bpmnElement);
		final String name = ModelUtil.getName(bpmnElement);
		
		editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {
				BPMNPlane plane = BpmnDiFactory.eINSTANCE.createBPMNPlane();
				plane.setBpmnElement(bpmnElement);
				
				BPMNDiagram diagram = BpmnDiFactory.eINSTANCE.createBPMNDiagram();
				diagram.setPlane(plane);
				diagram.setName(name);
				definitions.getDiagrams().add(diagram);
				
				ModelUtil.setID(plane);
				ModelUtil.setID(diagram);
			}
		});
	}
}