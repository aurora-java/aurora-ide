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
package org.eclipse.bpmn2.modeler.ui.property.tasks;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.Expression;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.MultiInstanceBehavior;
import org.eclipse.bpmn2.MultiInstanceLoopCharacteristics;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.adapters.InsertionAdapter;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractBpmn2PropertySection;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractListComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.DefaultDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.PropertiesCompositeFactory;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.BooleanObjectEditor;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.ComboObjectEditor;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.FeatureEditingDialog;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.ObjectEditor;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.TextAndButtonObjectEditor;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

public class MultiInstanceLoopCharacteristicsDetailComposite extends DefaultDetailComposite {
	
	public enum InstanceType {
		None,
		DataInput,
		LoopCardinality
	};

	ComboObjectEditor throwBehaviorEditor;
	ComboObjectEditor noneBehaviorEventEditor;
	ComboObjectEditor oneBehaviorEventEditor;
	AbstractListComposite complexBehaviorList;

	ObjectEditor isSequentialEditor;
	boolean completionConditionWidgetsShowing;
	Composite completionConditionWidgets;
	AbstractDetailComposite completionConditionEditor;

	boolean loopCardinalityWidgetsShowing;
	Composite instanceWidgets;
	Button loopCardinalityButton;
	Composite loopCardinalityWidgets;
	AbstractDetailComposite loopCardinalityEditor;

	boolean loopDataInputWidgetsShowing;
	Button loopDataInputButton;
	Composite loopDataInputWidgets;
	AbstractDetailComposite loopDataInputEditor;
	
	boolean loopDataOutputWidgetsShowing;
	Button producesOutputButton;
	Composite loopDataOutputWidgets;
	AbstractDetailComposite loopDataOutputEditor;

	boolean updatingWidgets;

	public MultiInstanceLoopCharacteristicsDetailComposite(Composite parent, int style) {
		super(parent, style);
	}

	public MultiInstanceLoopCharacteristicsDetailComposite(AbstractBpmn2PropertySection section) {
		super(section);
	}
	
	@Override
	public AbstractPropertiesProvider getPropertiesProvider(EObject object) {
		if (propertiesProvider==null) {
			propertiesProvider = new AbstractPropertiesProvider(object) {
				String[] properties = new String[] {
						"anyAttribute", //$NON-NLS-1$
						"isSequential", //$NON-NLS-1$
						"inputDataItem", //$NON-NLS-1$
						"outputDataItem", //$NON-NLS-1$
						"loopDataInputRef", //$NON-NLS-1$
						"loopDataOutputRef", //$NON-NLS-1$
						"completionCondition", //$NON-NLS-1$
						"loopCardinality", //$NON-NLS-1$
						"behavior", //$NON-NLS-1$
						"noneBehaviorEventRef", //$NON-NLS-1$
						"oneBehaviorEventRef", //$NON-NLS-1$
						"complexBehaviorDefinition", //$NON-NLS-1$
				};
				
				@Override
				public String[] getProperties() {
					return properties; 
				}
			};
		}
		return propertiesProvider;
	}

	@Override
	protected void cleanBindings() {
		super.cleanBindings();
		throwBehaviorEditor = null;
		noneBehaviorEventEditor = null;
		oneBehaviorEventEditor = null;
		complexBehaviorList = null;
		instanceWidgets = null;
		loopCardinalityButton = null;
		loopDataInputButton = null;
		loopCardinalityWidgets = null;
		loopDataInputWidgets = null;
		loopDataInputEditor = null;
		loopCardinalityEditor = null;
		loopDataOutputWidgets = null;
		isSequentialEditor = null;
		loopDataOutputEditor = null;
	}

	public void createBindings(EObject be) {
//		super.createBindings(be);
		createWidgets();
	}
	
	private MultiInstanceLoopCharacteristics getBO() {
		return (MultiInstanceLoopCharacteristics)getBusinessObject();
	}
	
	private void createWidgets() {

		Label label;
		
		////////////////////////
		// "sequential" checkbox
		if (isEnabled("isSequential")) { //$NON-NLS-1$
			isSequentialEditor = new BooleanObjectEditor(this, getBO(), PACKAGE.getMultiInstanceLoopCharacteristics_IsSequential()) {
				
				protected boolean setValue(final Object result) {
					Object oldValue = getBusinessObjectDelegate().getValue(object, feature);
					if (oldValue!=result && !oldValue.equals(result)) {
						TransactionalEditingDomain editingDomain = getDiagramEditor().getEditingDomain();
						editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
							@Override
							protected void doExecute() {
								getBusinessObjectDelegate().setValue(object, feature, button.getSelection());
								// This little bit of java jimnastics is needed to update the multi-instance marker on the activity.
								// Because Graphiti's Resource change listener will only invoke an Update Feature on Pictogram Elements
								// that are linked to business model objects; a change to the "is sequential" boolean attribute in the
								// MultiInstanceLoopCharacteristics will not fire an update (the MI object is not directly linked
								// to any PE). We therefore force a change to the containing Activity object, which IS linked to a
								// Pictogram Element, and thereby cause the multi-instance decorator to change from 3 vertical bars to
								// 3 horizontal bars (or vice-versa) whenever the "is sequential" checkbox changes state.
								Activity activity = (Activity)getBO().eContainer();
								int n = activity.getCompletionQuantity();
								activity.setCompletionQuantity(n+1);
								activity.setCompletionQuantity(n);
							}
						});
					}
					return true;
				}
			};
			isSequentialEditor.createControl(getAttributesParent(),Messages.MultiInstanceLoopCharacteristicsDetailComposite_Sequential_Label);
			isSequentialEditor.getControl().addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					showCompletionConditionWidgets(getBO().isIsSequential());
					redrawParent();
				}
			});
		}
		
		if (isEnabled("completionCondition")) { //$NON-NLS-1$
			// completion condition expression
			completionConditionWidgets = toolkit.createComposite(getAttributesParent(), SWT.NONE);
			completionConditionWidgets.setLayout(new GridLayout(1,false));
			completionConditionWidgets.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,true,3,1));
			// initially hidden
			completionConditionWidgets.setVisible(false);
			((GridData)completionConditionWidgets.getLayoutData()).exclude = true;
		}
		
		////////////////////////
		// radio buttons for number of instances controls
		if (isEnabled("loopCardinality") && isEnabled("loopDataInputRef")) { //$NON-NLS-1$ //$NON-NLS-2$
			label = toolkit.createLabel(getAttributesParent(), Messages.MultiInstanceLoopCharacteristicsDetailComposite_Number_Of_Instances);
			label.setLayoutData(new GridData(SWT.RIGHT,SWT.TOP,false,false,1,1));
	
			loopCardinalityButton = toolkit.createButton(getAttributesParent(), Messages.MultiInstanceLoopCharacteristicsDetailComposite_Integer_Expression_Button, SWT.RADIO);
			loopCardinalityButton.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,false,false,1,1));
			loopCardinalityButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (loopCardinalityButton.getSelection()) {
						showLoopDataInputWidgets(false);
						showLoopCardinalityWidgets(true);
						redrawParent();
					}
				}
			});
	
			loopDataInputButton = toolkit.createButton(getAttributesParent(), Messages.MultiInstanceLoopCharacteristicsDetailComposite_Collection_Button, SWT.RADIO);
			loopDataInputButton.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,true,false,1,1));
			loopDataInputButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (loopDataInputButton.getSelection()) {
						showLoopCardinalityWidgets(false);
						showLoopDataInputWidgets(true);
						redrawParent();
					}
				}
			});
		}
		
		////////////////////////
		// instances control composite
		instanceWidgets = toolkit.createComposite(getAttributesParent(), SWT.NONE);
		instanceWidgets.setLayout(new GridLayout(3,false));
		instanceWidgets.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,3,1));
		if (isEnabled("loopCardinality") && isEnabled("loopDataInputRef")) { //$NON-NLS-1$ //$NON-NLS-2$
			// initially hidden
			instanceWidgets.setVisible(false);
			((GridData)instanceWidgets.getLayoutData()).exclude = true;
		}
		
		if (isEnabled("loopCardinality")) { //$NON-NLS-1$
			// expression
			loopCardinalityWidgets = toolkit.createComposite(instanceWidgets, SWT.NONE);
			loopCardinalityWidgets.setLayout(new GridLayout(1,false));
			loopCardinalityWidgets.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,true,3,1));
		}
		
		if (isEnabled("loopDataInputRef")) { //$NON-NLS-1$
			// data collection
			loopDataInputWidgets = toolkit.createComposite(instanceWidgets, SWT.NONE);
			loopDataInputWidgets.setLayout(new GridLayout(3,false));
			loopDataInputWidgets.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,true,3,1));
		}
		
		////////////////////////
		// produces output checkbox
		if (isEnabled("loopDataOutputRef")) { //$NON-NLS-1$
			label = toolkit.createLabel(getAttributesParent(), Messages.MultiInstanceLoopCharacteristicsDetailComposite_Produces_Output_Label);
			label.setLayoutData(new GridData(SWT.RIGHT,SWT.TOP,false,false,1,1));
			producesOutputButton = toolkit.createButton(getAttributesParent(), "", SWT.CHECK); //$NON-NLS-1$
			producesOutputButton.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,true,false,2,1));
			producesOutputButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					showLoopDataOutputWidgets(producesOutputButton.getSelection());
					redrawParent();
				}
			});
	
			////////////////////////
			// output data collection composite
			loopDataOutputWidgets = toolkit.createComposite(getAttributesParent(), SWT.NONE);
			loopDataOutputWidgets.setLayout(new GridLayout(3,false));
			loopDataOutputWidgets.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,3,1));
			// initially hidden
			loopDataOutputWidgets.setVisible(false);
			((GridData)loopDataOutputWidgets.getLayoutData()).exclude = true;
		}
		
		////////////////////////
		// throw behavior combo and related widgets
		if (isEnabled("behavior")) { //$NON-NLS-1$
			this.bindAttribute(getAttributesParent(), getBO(), "behavior", Messages.MultiInstanceLoopCharacteristicsDetailComposite_Throw_Behavior_Label); //$NON-NLS-1$
			this.bindReference(getAttributesParent(), getBO(), PACKAGE.getMultiInstanceLoopCharacteristics_NoneBehaviorEventRef());
			this.bindReference(getAttributesParent(), getBO(), PACKAGE.getMultiInstanceLoopCharacteristics_OneBehaviorEventRef());
			this.bindList(getBO(), PACKAGE.getMultiInstanceLoopCharacteristics_ComplexBehaviorDefinition());
		}
		
		updateWidgets();
	}

	private void updateWidgets() {
		MultiInstanceLoopCharacteristics lc = getBO();
		
		showCompletionConditionWidgets(lc.isIsSequential());
		
		if (lc!=null && !updatingWidgets) {
			updatingWidgets = true;
			switch (getInstanceType()) {
			case None:
				if (loopCardinalityButton!=null)
					loopCardinalityButton.setSelection(false);
				if (loopDataInputButton!=null)
					loopDataInputButton.setSelection(false);
				loopDataInputWidgetsShowing = true;
				loopCardinalityWidgetsShowing = true;
				showLoopDataInputWidgets(false);
				showLoopCardinalityWidgets(false);
				break;
			case LoopCardinality:
				if (loopCardinalityButton!=null)
					loopCardinalityButton.setSelection(true);
				if (loopDataInputButton!=null)
					loopDataInputButton.setSelection(false);
				showLoopDataInputWidgets(false);
				showLoopCardinalityWidgets(true);
				break;
			case DataInput:
				if (loopCardinalityButton!=null)
					loopCardinalityButton.setSelection(false);
				if (loopDataInputButton!=null)
					loopDataInputButton.setSelection(true);
				showLoopCardinalityWidgets(false);
				showLoopDataInputWidgets(true);
				break;
			}
			
			if (producesOutputButton!=null) {
				boolean producesOutput = (lc.getLoopDataOutputRef()!=null || lc.getOutputDataItem()!=null);
				producesOutputButton.setSelection(producesOutput);
				showLoopDataOutputWidgets(producesOutput);
			}
		}
		updatingWidgets = false;
	}
	
	private InstanceType getInstanceType() {
		if (isEnabled("loopCardinality") && isEnabled("loopDataInputRef")) { //$NON-NLS-1$ //$NON-NLS-2$
			MultiInstanceLoopCharacteristics lc = getBO();
			if (lc.getLoopDataInputRef()==null) {
				if (lc.getLoopCardinality()==null)
					return InstanceType.None;
				return InstanceType.LoopCardinality;
			}
			else if (lc.getLoopCardinality()==null) {
				if (lc.getLoopDataInputRef()==null)
					return InstanceType.None;
				return InstanceType.DataInput;
			}
		}
		else if (isEnabled("loopCardinality")) { //$NON-NLS-1$
			return InstanceType.LoopCardinality;
		}
		else if (isEnabled("loopDataInputRef")) { //$NON-NLS-1$
			return InstanceType.DataInput;
		}
		return InstanceType.None;
	}

	private boolean isEnabled(String featureName) {
		final EClass eclass = PACKAGE.getMultiInstanceLoopCharacteristics();
		EStructuralFeature feature = eclass.getEStructuralFeature(featureName);
		return super.isModelObjectEnabled(eclass,feature);
	}
	
	private void showCompletionConditionWidgets(boolean show) {
		if (!isEnabled("completionCondition")) //$NON-NLS-1$
			return;
		
		if (show != completionConditionWidgetsShowing) {
			if (show) {
				completionConditionWidgets.setVisible(true);
				((GridData)completionConditionWidgets.getLayoutData()).exclude = false;

				this.bindReference(completionConditionWidgets, getBO(), "completionCondition"); //$NON-NLS-1$
			}
			else {
				completionConditionWidgets.setVisible(false);
				((GridData)completionConditionWidgets.getLayoutData()).exclude = true;
				
				// remove the Transformation and assignments
				if (!updatingWidgets && getBO().getCompletionCondition()!=null) {
					editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
						@Override
						protected void doExecute() {
							getBO().setCompletionCondition(null);
						}
					});
				}
			}
			completionConditionWidgetsShowing = show;
		}
	}

	private void showLoopDataInputWidgets(boolean show) {
		if (!isEnabled("loopDataInputRef")) //$NON-NLS-1$
			return;

		if (show != loopDataInputWidgetsShowing) {
			if (show) {
				instanceWidgets.setVisible(true);
				((GridData)instanceWidgets.getLayoutData()).exclude = false;

				loopDataInputWidgets.setVisible(true);
				((GridData)loopDataInputWidgets.getLayoutData()).exclude = false;

				this.bindReference(loopDataInputWidgets, getBO(), "loopDataInputRef"); //$NON-NLS-1$
			}
			else {
				instanceWidgets.setVisible(false);
				((GridData)instanceWidgets.getLayoutData()).exclude = true;

				loopDataInputWidgets.setVisible(false);
				((GridData)loopDataInputWidgets.getLayoutData()).exclude = true;

				// remove the loopDataInputRef reference
				if (!updatingWidgets && getBO().getLoopDataInputRef()!=null) {
					editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
						@Override
						protected void doExecute() {
							getBO().setLoopDataInputRef(null);
							getBO().setInputDataItem(null);
						}
					});
				}
			}
			loopDataInputWidgetsShowing = show;
		}
	}

	private void showLoopCardinalityWidgets(boolean show) {
		if (!isEnabled("loopCardinality")) //$NON-NLS-1$
			return;

		if (show != loopCardinalityWidgetsShowing) {
			if (show) {
				instanceWidgets.setVisible(true);
				((GridData)instanceWidgets.getLayoutData()).exclude = false;

				loopCardinalityWidgets.setVisible(true);
				((GridData)loopCardinalityWidgets.getLayoutData()).exclude = false;

				this.bindReference(loopCardinalityWidgets, getBO(), "loopCardinality"); //$NON-NLS-1$
			}
			else {
				instanceWidgets.setVisible(false);
				((GridData)instanceWidgets.getLayoutData()).exclude = true;

				loopCardinalityWidgets.setVisible(false);
				((GridData)loopCardinalityWidgets.getLayoutData()).exclude = true;
				
				// remove the Transformation and assignments
				if (!updatingWidgets && getBO().getLoopCardinality()!=null) {
					editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
						@Override
						protected void doExecute() {
							getBO().setLoopCardinality(null);
						}
					});
				}
			}
			loopCardinalityWidgetsShowing = show;
		}
	}

	private void showLoopDataOutputWidgets(boolean show) {
		if (!isEnabled("loopDataOutputRef")) //$NON-NLS-1$
			return;

		if (show != loopDataOutputWidgetsShowing) {
			if (show) {
				loopDataOutputWidgets.setVisible(true);
				((GridData)loopDataOutputWidgets.getLayoutData()).exclude = false;
				
				this.bindReference(loopDataOutputWidgets, getBO(), "loopDataOutputRef"); //$NON-NLS-1$
			}
			else {
				loopDataOutputWidgets.setVisible(false);
				((GridData)loopDataOutputWidgets.getLayoutData()).exclude = true;

				// remove the Loop Data Output and Output Data Item
				if (!updatingWidgets && (getBO().getLoopDataOutputRef()!=null || getBO().getOutputDataItem()!=null)) {
					editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
						@Override
						protected void doExecute() {
							getBO().setLoopDataOutputRef(null);
							getBO().setOutputDataItem(null);
						}
					});
				}
			}
			loopDataOutputWidgetsShowing = show;
		}
	}

	private void redrawParent() {
		// this DetailComposite should be sitting in a SashForm created
		// by a ListComposite. layout this thing first
		getAttributesParent().layout();
		layout();
		// and then search for the DetailComposite that contains the list 
		Composite parent = getParent();
		while (parent!=null) {
			parent = parent.getParent();
			if (parent instanceof AbstractDetailComposite) {
				parent.layout();
				parent.getParent().layout();
				break;
			}
		}
	}
	
	protected void bindAttribute(Composite parent, EObject object, EAttribute attribute, String label) {
		if (attribute.getName().equals("behavior")) { //$NON-NLS-1$
			throwBehaviorEditor = new ComboObjectEditor(this,object,attribute) {

				@Override
				protected boolean setValue(Object result) {
					MultiInstanceLoopCharacteristics lc = (MultiInstanceLoopCharacteristics)object;
					boolean updated = super.setValue(result);
					if (updated) {
						switch (lc.getBehavior()) {
						case ALL:
							if (noneBehaviorEventEditor!=null) {
								noneBehaviorEventEditor.setVisible(false);
							}
							if (oneBehaviorEventEditor!=null) {
								oneBehaviorEventEditor.setVisible(false);
							}
							if (complexBehaviorList!=null) {
								complexBehaviorList.setVisible(false);
							}
							break;
						case NONE:
							if (oneBehaviorEventEditor!=null) {
								oneBehaviorEventEditor.setVisible(false);
							}
							if (complexBehaviorList!=null) {
								complexBehaviorList.setVisible(false);
							}
							if (noneBehaviorEventEditor!=null) {
								noneBehaviorEventEditor.setVisible(true);
							}
							break;
						case ONE:
							if (noneBehaviorEventEditor!=null) {
								noneBehaviorEventEditor.setVisible(false);
							}
							if (complexBehaviorList!=null) {
								complexBehaviorList.setVisible(false);
							}
							if (oneBehaviorEventEditor!=null) {
								oneBehaviorEventEditor.setVisible(true);
							}
							break;
						case COMPLEX:
							if (noneBehaviorEventEditor!=null) {
								noneBehaviorEventEditor.setVisible(false);
							}
							if (oneBehaviorEventEditor!=null) {
								oneBehaviorEventEditor.setVisible(false);
							}
							if (complexBehaviorList!=null) {
								complexBehaviorList.setVisible(true);
							}
							break;
						}
						redrawParent();

//						Display.getDefault().asyncExec( new Runnable() {
//
//							@Override
//							public void run() {
//								redrawPage();
//							}
//							
//						});
					}
					return updated;
				}
			};
			throwBehaviorEditor.createControl(parent,label);
		}
		else
			super.bindAttribute(parent, object, attribute, label);
	}
	
	protected void bindReference(Composite parent, final EObject object, final EReference reference) {
		final MultiInstanceLoopCharacteristics lc = (MultiInstanceLoopCharacteristics)object;

		if (reference == PACKAGE.getMultiInstanceLoopCharacteristics_CompletionCondition()) {
			if (isModelObjectEnabled(lc.eClass(), reference)) {
				// use a FormalExpression detail composite here
				if (completionConditionEditor==null) {
					completionConditionEditor = PropertiesCompositeFactory.INSTANCE.createDetailComposite(
							Expression.class, completionConditionWidgets, SWT.BORDER);
					((DefaultDetailComposite)completionConditionEditor).setPropertiesProvider( new AbstractPropertiesProvider(getBO()) {
						String[] properties = new String[] {
								"language", //$NON-NLS-1$
								"body", //$NON-NLS-1$
								// this must evaluate to a Boolean data type
						};
						
						@Override
						public String[] getProperties() {
							return properties; 
						}
					});
				}
				
				// create a new Loop Cardinality FormalExpression if necessary
				Expression expression = getBO().getCompletionCondition();
				if (expression==null) {
					expression = createModelObject(FormalExpression.class);
					InsertionAdapter.add(getBO(), PACKAGE.getMultiInstanceLoopCharacteristics_CompletionCondition(), expression);
				}
				completionConditionEditor.setBusinessObject(expression);
				completionConditionEditor.setTitle(Messages.MultiInstanceLoopCharacteristicsDetailComposite_Completion_Condition_Label);
			}
		}
		else if (reference == PACKAGE.getMultiInstanceLoopCharacteristics_LoopDataInputRef()) {
			if (isModelObjectEnabled(lc.eClass(), reference)) {
				if (loopDataInputEditor==null) {
					loopDataInputEditor = new DefaultDetailComposite(parent, SWT.BORDER) {

						@Override
						public void createBindings(EObject be) {
							ObjectEditor editor = new ComboObjectEditor(this,object,reference, PACKAGE.getDataInput());
							editor.createControl(getAttributesParent(), Messages.MultiInstanceLoopCharacteristicsDetailComposite_Input_Data_Label);
							EStructuralFeature f = PACKAGE.getMultiInstanceLoopCharacteristics_InputDataItem();
							editor = new DataInputOutputItemEditor(lc,f);
							editor.createControl(getAttributesParent(), Messages.MultiInstanceLoopCharacteristicsDetailComposite_Input_Parameter_Label);
						}
					};
				}
				loopDataInputEditor.setBusinessObject(object);
				loopDataInputEditor.setTitle(Messages.MultiInstanceLoopCharacteristicsDetailComposite_Input_Data_Title);
			}
		}
		else if (reference == PACKAGE.getMultiInstanceLoopCharacteristics_LoopDataOutputRef()) {
			if (isModelObjectEnabled(lc.eClass(), reference)) {
				if (loopDataOutputEditor==null) {
					loopDataOutputEditor = new DefaultDetailComposite(parent, SWT.BORDER) {

						@Override
						public void createBindings(EObject be) {
							ObjectEditor editor = new ComboObjectEditor(this,object,reference);
							editor.createControl(getAttributesParent(), Messages.MultiInstanceLoopCharacteristicsDetailComposite_Output_Data_Label);
							EStructuralFeature f = PACKAGE.getMultiInstanceLoopCharacteristics_OutputDataItem();
							editor = new DataInputOutputItemEditor(lc,f);
							editor.createControl(getAttributesParent(), Messages.MultiInstanceLoopCharacteristicsDetailComposite_Output_Parameter_Label);
						}
					};
				}
				loopDataOutputEditor.setBusinessObject(object);
				loopDataOutputEditor.setTitle(Messages.MultiInstanceLoopCharacteristicsDetailComposite_Output_Data_Title);
			}
		}
		else if (reference == PACKAGE.getMultiInstanceLoopCharacteristics_LoopCardinality()) {
			if (isModelObjectEnabled(lc.eClass(), reference)) {
				// use a FormalExpression detail composite here
				if (loopCardinalityEditor==null) {
					loopCardinalityEditor = PropertiesCompositeFactory.INSTANCE.createDetailComposite(
							Expression.class, loopCardinalityWidgets, SWT.BORDER);
				}
				
				// create a new Loop Cardinality FormalExpression if necessary
				Expression expression = getBO().getLoopCardinality();
				if (expression==null) {
					expression = createModelObject(FormalExpression.class);
					InsertionAdapter.add(getBO(), PACKAGE.getMultiInstanceLoopCharacteristics_LoopCardinality(), expression);
				}
				loopCardinalityEditor.setBusinessObject(expression);
				loopCardinalityEditor.setTitle(Messages.MultiInstanceLoopCharacteristicsDetailComposite_Expression_Title);
			}
		}
		else if (reference == PACKAGE.getMultiInstanceLoopCharacteristics_NoneBehaviorEventRef()) {
			if (isModelObjectEnabled(lc.eClass(), reference)) {
				String displayName = ExtendedPropertiesProvider.getLabel(object, reference);
				noneBehaviorEventEditor = new ComboObjectEditor(this,object,reference);
				noneBehaviorEventEditor.setStyle(SWT.READ_ONLY);
				noneBehaviorEventEditor.createControl(parent,displayName);
				noneBehaviorEventEditor.setVisible( lc.getBehavior() == MultiInstanceBehavior.NONE );
			}				
		}		
		else if (reference == PACKAGE.getMultiInstanceLoopCharacteristics_OneBehaviorEventRef()) {
			if (isModelObjectEnabled(lc.eClass(), reference)) {
				String displayName = ExtendedPropertiesProvider.getLabel(object, reference);
				oneBehaviorEventEditor = new ComboObjectEditor(this,object,reference);
				oneBehaviorEventEditor.setStyle(SWT.READ_ONLY);
				oneBehaviorEventEditor.createControl(parent,displayName);
				oneBehaviorEventEditor.setVisible( lc.getBehavior() == MultiInstanceBehavior.ONE );
			}
		}		
		else
			super.bindReference(parent, object, reference);
	}
	
	protected AbstractListComposite bindList(EObject object, EStructuralFeature feature, EClass listItemClass) {
		MultiInstanceLoopCharacteristics lc = (MultiInstanceLoopCharacteristics)object;
		if (feature.getName().equals("complexBehaviorDefinition")) { //$NON-NLS-1$
			if (isModelObjectEnabled(lc.eClass(), feature)) {
				complexBehaviorList = super.bindList(getAttributesParent(), object, feature, listItemClass);
				complexBehaviorList.setVisible( lc.getBehavior() == MultiInstanceBehavior.COMPLEX );
				return complexBehaviorList;
			}
			return null;
		}
		else
			return super.bindList(object, feature, listItemClass);
	}
	
	public class DataInputOutputItemDialog extends FeatureEditingDialog {

		public DataInputOutputItemDialog(DiagramEditor editor, EObject object, EStructuralFeature feature) {
			super(editor, object, feature);
		}

		public DataInputOutputItemDialog(DiagramEditor editor, EObject object, EStructuralFeature feature, EObject value) {
			super(editor, object, feature, value);
		}
		
		protected Composite createDialogContent(Composite parent) {
			Composite content = new DefaultDetailComposite(parent, SWT.NONE) {
				
				@Override
				public AbstractPropertiesProvider getPropertiesProvider(EObject object) {
					if (propertiesProvider==null) {
						propertiesProvider = new AbstractPropertiesProvider(object) {
							String[] properties = new String[] {
									"anyAttribute", //$NON-NLS-1$
									"name", //$NON-NLS-1$
									"itemSubjectRef", //$NON-NLS-1$
							};
							
							@Override
							public String[] getProperties() {
								return properties; 
							}
						};
					}
					return propertiesProvider;
				}

			};
			return content;
		}
	}
	
	public class DataInputOutputItemEditor extends TextAndButtonObjectEditor {

		public DataInputOutputItemEditor(MultiInstanceLoopCharacteristics object, EStructuralFeature feature) {
			super(MultiInstanceLoopCharacteristicsDetailComposite.this, object, feature);
		}

		@Override
		protected void buttonClicked(int buttonId) {
			if (buttonId==ID_DEFAULT_BUTTON) {
				FeatureEditingDialog dlg = new DataInputOutputItemDialog(getDiagramEditor(), object, feature);
				dlg.open();
				updateText();
			}
		}
		
		@Override
		protected boolean setValue(final Object result) {
			return true;
		}
		
		protected void updateText() {
			// update the read-only text for this DataInput or DataOutput editor:
			// this will be in the form "Parameter Name (Data Type)"
			ItemAwareElement item = (ItemAwareElement) object.eGet(feature);
			String newText = ""; //$NON-NLS-1$
			if (item!=null) {
				String name = ModelUtil.getName(item);
				String type = ExtendedPropertiesProvider.getTextValue(item.getItemSubjectRef());
				if (name!=null)
					newText = name;
				if (type!=null)
					newText += "  (" + type + ")"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			if (!text.getText().equals(newText)) {
				setText(newText);
			}
		}
	}
}
