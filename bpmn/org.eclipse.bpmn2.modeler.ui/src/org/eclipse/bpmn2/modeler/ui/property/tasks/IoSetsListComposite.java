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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.CallableElement;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.OutputSet;
import org.eclipse.bpmn2.ReceiveTask;
import org.eclipse.bpmn2.SendTask;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.adapters.InsertionAdapter;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.DefaultDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.DefaultListComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.ListCompositeColumnProvider;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.PropertiesCompositeFactory;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.TableColumn;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.RefListEditingDialog;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.TextAndButtonObjectEditor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class IoSetsListComposite extends DefaultListComposite {

	/**
	 * 
	 */
	Activity activity;
	CallableElement element;
	List ioSet;
	InputOutputSpecification ioSpecification;
	EStructuralFeature ioFeature;
	boolean isInput;
	
	public IoSetsListComposite(IoParametersDetailComposite detailComposite, EObject container, InputOutputSpecification ioSpecification, EStructuralFeature ioFeature) {
		super(detailComposite, DEFAULT_STYLE);
		this.ioFeature = ioFeature;
		this.ioSpecification = ioSpecification;
		
		columnProvider = new ListCompositeColumnProvider(this);
		EClass listItemClass = (EClass)ioFeature.getEType();
		setListItemClass(listItemClass);
		
		EStructuralFeature f;
		f = listItemClass.getEStructuralFeature(Messages.IoSetsListComposite_0);
		TableColumn tc;
		tc = new IoSetNameColumn(ioSpecification,f);
		tc.setEditable(true);
		columnProvider.add(tc);

		isInput = (Messages.IoSetsListComposite_1.equals(ioFeature.getName()));
		if (isInput) {
			ioSet = ioSpecification.getInputSets();

			f = listItemClass.getEStructuralFeature(Messages.IoSetsListComposite_2);
			tc = new IoParameterNameColumn(ioSpecification,f);
			tc.setHeaderText(Messages.IoSetsListComposite_Input_Data_Header);
			columnProvider.add(tc);
			
			f = listItemClass.getEStructuralFeature(Messages.IoSetsListComposite_4);
			tc = new IoParameterNameColumn(ioSpecification,f);
			tc.setHeaderText(Messages.IoSetsListComposite_Optional_Inputs_Header);
			columnProvider.add(tc);
			
			f = listItemClass.getEStructuralFeature("whileExecutingInputRefs"); //$NON-NLS-1$
			tc = new IoParameterNameColumn(ioSpecification,f);
			tc.setHeaderText(Messages.IoSetsListComposite_Evaluated_While_Executing_Header);
			columnProvider.add(tc);
			
			if (!(container instanceof SendTask)) {
				f = listItemClass.getEStructuralFeature("outputSetRefs"); //$NON-NLS-1$
				tc = new IoSetNameColumn(ioSpecification,f);
				tc.setHeaderText(Messages.IoSetsListComposite_Output_Sets_Produced_Header);
				columnProvider.add(tc);
			}
		}
		else {
			ioSet = ioSpecification.getOutputSets();

			f = listItemClass.getEStructuralFeature("dataOutputRefs"); //$NON-NLS-1$
			tc = new IoParameterNameColumn(ioSpecification,f);
			tc.setHeaderText(Messages.IoSetsListComposite_Output_Data_Header);
			columnProvider.add(tc);
			
			f = listItemClass.getEStructuralFeature("optionalOutputRefs"); //$NON-NLS-1$
			tc = new IoParameterNameColumn(ioSpecification,f);
			tc.setHeaderText(Messages.IoSetsListComposite_Optional_Outputs_Header);
			columnProvider.add(tc);
			
			f = listItemClass.getEStructuralFeature("whileExecutingOutputRefs"); //$NON-NLS-1$
			tc = new IoParameterNameColumn(ioSpecification,f);
			tc.setHeaderText(Messages.IoSetsListComposite_Produced_While_Executing_Header);
			columnProvider.add(tc);
			
			if (!(container instanceof ReceiveTask)) {
				f = listItemClass.getEStructuralFeature("inputSetRefs"); //$NON-NLS-1$
				tc = new IoSetNameColumn(ioSpecification,f);
				tc.setHeaderText(Messages.IoSetsListComposite_Input_Sets_Reqd_Header);
				columnProvider.add(tc);
			}
		}
		if (container instanceof Activity) {
			this.activity = (Activity)container;
		}
		else if (container instanceof CallableElement) {
			this.element = (CallableElement)container;
		}
	}
	
	public AbstractDetailComposite createDetailComposite(Class eClass, Composite parent, int style) {
		AbstractDetailComposite composite = new IoSetsDetailComposite(parent, SWT.NONE);
		return composite;
	}

	@Override
	protected EObject addListItem(EObject object, EStructuralFeature feature) {
		EObject item = super.addListItem(object, feature);
		return item;
	}

	@Override
	protected EObject editListItem(EObject object, EStructuralFeature feature) {
		return super.editListItem(object, feature);
	}

	@Override
	protected Object removeListItem(EObject object, EStructuralFeature feature, int index) {
		return super.removeListItem(object, feature, index);
	}
	
	public class IoSetsDetailComposite extends DefaultDetailComposite {

		boolean isInput;
		IoSetsFeatureEditor ioRefsEditor;
		IoSetsFeatureEditor optionalIoRefsEditor;
		IoSetsFeatureEditor whileExecIoRefsEditor;
		IoSetsFeatureEditor ioSetRefsEditor;
		
		public IoSetsDetailComposite(Composite parent, int style) {
			super(parent, style);
		}

		@Override
		public void createBindings(EObject be) {
			isInput = (be instanceof InputSet);
			bindAttribute(be, "name"); //$NON-NLS-1$

			// find the IoSetsListComposite if there is one, so we can get the Activity
			// that owns this critter
			Activity activity = null;
			Composite parent = getParent();
			while (parent!=null) {
				if (parent instanceof IoSetsListComposite) {
					activity = ((IoSetsListComposite)parent).activity;
					break;
				}
				parent = parent.getParent();
			}
			ioRefsEditor = new IoSetsFeatureEditor(this,be);
			ioRefsEditor.create(isInput ? "dataInputRefs" : "dataOutputRefs"); //$NON-NLS-1$ //$NON-NLS-2$

			optionalIoRefsEditor = new IoSetsFeatureEditor(this,be);
			optionalIoRefsEditor.create(isInput ? "optionalInputRefs" : "optionalOutputRefs"); //$NON-NLS-1$ //$NON-NLS-2$

			whileExecIoRefsEditor = new IoSetsFeatureEditor(this,be);
			whileExecIoRefsEditor.create(isInput ? "whileExecutingInputRefs" : "whileExecutingOutputRefs"); //$NON-NLS-1$ //$NON-NLS-2$

			if (
					(isInput && !(activity instanceof SendTask)) ||
					(!isInput && !(activity instanceof ReceiveTask)) ) {
				ioSetRefsEditor = new IoSetsFeatureEditor(this,be);
				ioSetRefsEditor.create(isInput ? "outputSetRefs" : "inputSetRefs"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		
		public class IoSetsFeatureEditor extends  TextAndButtonObjectEditor {
			
			public IoSetsFeatureEditor(AbstractDetailComposite parent, EObject object) {
				super(parent,object,null);
			}

			public void create(String featureName) {
				this.feature = object.eClass().getEStructuralFeature(featureName);
				String label = ExtendedPropertiesProvider.getLabel(object,feature);
				label = label.replace(" Ref", ""); //$NON-NLS-1$ //$NON-NLS-2$
				super.createControl(parent, label, style | SWT.MULTI);
			}
			
			@Override
			protected void buttonClicked(int buttonId) {
				RefListEditingDialog dlg = new RefListEditingDialog(getDiagramEditor(), object, feature);
				if (dlg.open()==Window.OK) {
					final List<EObject> result = dlg.getResult();
					
					TransactionalEditingDomain domain = getDiagramEditor().getEditingDomain();
					domain.getCommandStack().execute(new RecordingCommand(domain) {
						@Override
						protected void doExecute() {
							List<EObject> list = (List<EObject>)object.eGet(feature);
							list.clear();
							list.addAll(result);
							updateText();
						}
					});
				}
			}

			@Override
			protected void updateText() {
				String text = null;
				List<EObject> list = (List<EObject>)object.eGet(feature);
				for (EObject item : list) {
					String name = ExtendedPropertiesProvider.getTextValue(item);
					if (text==null)
						text = name;
					else
						text += "\n" + name; //$NON-NLS-1$
				}
				setText(text);
			}
			
			protected boolean setValue(final Object result) {
				return true;
			}
		}
	}
}