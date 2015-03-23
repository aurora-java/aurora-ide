/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013 Red Hat, Inc.
 * All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.property.editors;

import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.ComboObjectEditor;
import org.eclipse.bpmn2.modeler.core.preferences.Bpmn2Preferences;
import org.eclipse.bpmn2.modeler.core.runtime.ServiceImplementationDescriptor;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.ui.adapters.properties.ServiceTaskPropertiesAdapter;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ServiceImplementationObjectEditor extends ComboObjectEditor {

	public static String UNSPECIFIED_VALUE = "##unspecified"; //$NON-NLS-1$
	public static String WEBSERVICE_VALUE = "##WebService"; //$NON-NLS-1$
	
	public ServiceImplementationObjectEditor(AbstractDetailComposite parent, EObject object, EStructuralFeature feature) {
		super(parent, object, feature);
	}

	public ServiceImplementationObjectEditor(AbstractDetailComposite parent, EObject object, EStructuralFeature feature,
			EClass featureEType) {
		super(parent, object, feature, featureEType);
	}
	
	protected boolean canEdit() {
		if (editButton==null)
			return true;
		Object value = object.eGet(feature);
		if (value instanceof String) {
			TargetRuntime rt = TargetRuntime.getCurrentRuntime();
			for (ServiceImplementationDescriptor eld : rt.getServiceImplementationDescriptors()) {
				if (value.equals(eld.getName()))
					return false;
			}
		}
		return true;
	}
	
	protected boolean canCreateNew() {
		return true;
	}

	@Override
	public boolean setValue(Object result) {
		if (ModelUtil.isStringWrapper(result)) {
			result = ModelUtil.getStringWrapperValue(result);
		}
		return super.setValue(result);
	}
	
	public Object getValue() {
		Object value = object.eGet(feature);
		if (value==null)
			return "";
		Hashtable<String,Object> choices = getChoiceOfValues(object, feature);
		for (Entry<String, Object> entry : choices.entrySet()) {
			if (entry.getValue().equals(value))
				return entry.getKey();
		}
		return value.toString();
	}
	
	protected EObject createObject() throws Exception {
		Hashtable<String,Object> choices = getChoiceOfValues(object, feature);
		ImplementationEditingDialog dialog = new ImplementationEditingDialog(
				getDiagramEditor().getEditorSite().getShell(), 
				Messages.ServiceImplementationObjectEditor_Create_New_Title, 
				choices, null, null);
		if ( dialog.open() == Window.OK) {
			Bpmn2Preferences prefs = (Bpmn2Preferences) getDiagramEditor().getAdapter(Bpmn2Preferences.class);
			String newURI = dialog.getURI();
			String newName = dialog.getName();
			prefs.addServiceImplementation(newName, newURI);
			return ModelUtil.createStringWrapper( newURI );
		}
		throw new OperationCanceledException(Messages.ServiceImplementationObjectEditor_Dialog_Cancelled);
	}
	
	protected EObject editObject(EObject value) throws Exception {
		Hashtable<String,Object> choices = getChoiceOfValues(object, feature);
		final String oldURI = ModelUtil.getStringWrapperTextValue(value);
		final String oldName = (String)getValue();
		ImplementationEditingDialog dialog = new ImplementationEditingDialog(
				getDiagramEditor().getEditorSite().getShell(), 
				Messages.ServiceImplementationObjectEditor_Edit_Title, 
				choices, oldName, oldURI);
		if ( dialog.open() == Window.OK) {
			final String newURI = dialog.getURI();
			final String newName = dialog.getName();
			if (!newURI.equals(value)) {
				final Definitions definitions = ModelUtil.getDefinitions(object);
				if (definitions!=null) {
					TransactionalEditingDomain domain = getDiagramEditor().getEditingDomain();
					domain.getCommandStack().execute(new RecordingCommand(domain) {
						@Override
						protected void doExecute() {
							TreeIterator<EObject> iter = definitions.eAllContents();
							while (iter.hasNext()) {
								EObject o = iter.next();
								EStructuralFeature f = o.eClass().getEStructuralFeature("implementation"); //$NON-NLS-1$
								if (f!=null) {
									String implementation = (String)o.eGet(f);
									if (oldURI.equals(implementation)) {
										o.eSet(f, newURI);
									}
								}
							}
						}
					});
				}
	
				Bpmn2Preferences prefs = (Bpmn2Preferences) getDiagramEditor().getAdapter(Bpmn2Preferences.class);
				prefs.removeServiceImplementation(oldName);
				prefs.addServiceImplementation(newName, newURI);
				return ModelUtil.createStringWrapper(newURI);
			}
		}
		throw new OperationCanceledException(Messages.ServiceImplementationObjectEditor_Dialog_Cancelled);
	}
	
	protected Hashtable<String,Object> getChoiceOfValues(EObject object, EStructuralFeature feature) {
		return ServiceTaskPropertiesAdapter.getChoiceOfValues(object);
	}
	
	public class ImplementationEditingDialog extends InputDialog {
		
		private String nameString;
		private Text nameText;
		
		public ImplementationEditingDialog(Shell shell, String title, final Map<String,Object> choices, final String nameString, final String uriString) {
			super(
					shell,
					title,
					Messages.ServiceImplementationObjectEditor_Implementation_URI_Label,
					uriString,
					new IInputValidator() {

						@Override
						public String isValid(String newText) {
							if (newText==null || newText.isEmpty())
								return Messages.ServiceImplementationObjectEditor_Invalid_Empty;
							if (newText.equals(uriString))
								return null;
							if (choices.containsKey(newText) || choices.containsValue(newText))
								return NLS.bind(Messages.ServiceImplementationObjectEditor_Invalid_Duplicate,newText);
							return null;
						}
					}
				);
			this.nameString = nameString;
		}
		
	    @Override
		protected Control createDialogArea(Composite parent) {
	        Composite composite = (Composite) createMyComposite(parent);
	        // create prompt
            Label label = new Label(composite, SWT.WRAP);
            label.setText(Messages.ServiceImplementationObjectEditor_Implementation_Name_Label);
            GridData data = new GridData(GridData.GRAB_HORIZONTAL
                    | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
                    | GridData.VERTICAL_ALIGN_CENTER);
            data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
            label.setLayoutData(data);
            label.setFont(parent.getFont());

            // create the Implementation Name input nameText
	        nameText = new Text(composite, getInputTextStyle());
	        nameText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
	                | GridData.HORIZONTAL_ALIGN_FILL));
	        nameText.addModifyListener(new ModifyListener() {
	            public void modifyText(ModifyEvent e) {
	                validateInput();
	            }
	        });
	        return super.createDialogArea(parent);
	    }
	    
		private Composite createMyComposite(Composite parent) {
			// create a composite with standard margins and spacing
			Composite composite = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
			layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
			layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
			composite.setLayout(layout);
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			applyDialogFont(composite);
			return composite;
		}
		
		@Override
		protected void validateInput() {
            String errorMessage = getValidator().isValid(nameText.getText());
            if (errorMessage==null)
            	super.validateInput();
            else
            	setErrorMessage(errorMessage);
		}

		@Override
		protected void createButtonsForButtonBar(Composite parent) {
			super.createButtonsForButtonBar(parent);
	        nameText.setFocus();
	        if (nameString != null) {
	        	nameText.setText(nameString);
	            nameText.selectAll();
	        }
	        validateInput();
		}

		@Override
		protected void buttonPressed(int buttonId) {
			if (buttonId == IDialogConstants.OK_ID) {
				nameString = nameText.getText();
			} else {
				nameString = null;
			}
			super.buttonPressed(buttonId);
		}
		
		public String getName() {
			return nameString;
		}
		
		public String getURI() {
			return super.getValue();
		}
	}
}
