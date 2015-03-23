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

package org.eclipse.bpmn2.modeler.core.merrimac.dialogs;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.bpmn2.modeler.core.Activator;
import org.eclipse.bpmn2.modeler.core.IConstants;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractDetailComposite;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.ui.celleditor.FeatureEditorDialog;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * EObject Reference List Editor.
 * This class implements an EObject reference list editor. The feature must be an EList of EObject references.
 * The list is rendered in a single-line text field with an "Edit" button to the right. Clicking the edit button
 * displays an EMF FeatureEditorDialog, which allows adding, removing and reordering of available object references.
 * 
 * @author Bob Brodt
 */
public class FeatureListObjectEditor extends MultivalueObjectEditor {

	Text text;
	List<EObject> references;
	Composite buttons;
	Button editButton;
	protected IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	
	private class ListLabelProvider extends LabelProvider {

		@Override
		public Image getImage(Object element) {
			return super.getImage(element);
		}

		@Override
		public String getText(Object element) {
			return ModelUtil.getTextValue(element);
		}
	}
	
	/**
	 * @param parent
	 * @param object
	 * @param feature
	 */
	public FeatureListObjectEditor(AbstractDetailComposite parent, EObject object, EStructuralFeature feature) {
		super(parent, object, feature, (EClass)feature.getEType());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.ui.property.editors.ObjectEditor#createControl(org.eclipse.swt.widgets.Composite, java.lang.String, int)
	 */
	@Override
	protected Control createControl(Composite composite, String label, int style) {
		createLabel(composite, label);

		text = getToolkit().createText(composite, ""); //$NON-NLS-1$
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		setEditable(false);
		
		references = getValue();
		updateTextField();

		boolean canEdit = canEdit();
		boolean canCreateNew = canCreateNew();

		if (canEdit || canCreateNew) {
			buttons =  getToolkit().createComposite(composite);
			buttons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
			buttons.setLayout(new FillLayout(SWT.HORIZONTAL));

			if (canCreateNew) {
				// TODO: this isn't working yet.
				Button createButton = getToolkit().createButton(buttons, null, SWT.PUSH);
				createButton.setImage( Activator.getDefault().getImage(IConstants.ICON_ADD_20));
				createButton.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						// create a new target object
						FeatureEditingDialog dialog = createFeatureEditingDialog(null);							
						if ( dialog.open() == Window.OK) {
							updateEObject(dialog.getNewObject());
							updateTextField();
						}
					}
				});
			}
			if (canEdit) {
				editButton = getToolkit().createButton(buttons, null, SWT.PUSH);
				editButton.setImage( Activator.getDefault().getImage(IConstants.ICON_EDIT_20));
				
				editButton.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						Hashtable<String,Object> choices = getChoiceOfValues(object,feature);
						List values = new ArrayList();
						values.addAll(choices.values());

						FeatureEditorDialog featureEditorDialog = new FeatureEditorDialog(parent.getShell(),
								new ListLabelProvider(), object, feature, Messages.FeatureListObjectEditor_Title, values) {

							protected Control createContents(Composite parent) {
								Composite control = (Composite)super.createContents(parent);
								setDialogSize(control);
								return control;
							}

							@Override
							protected void configureShell(Shell shell) {
								// TODO Auto-generated method stub
								super.configureShell(shell);
							}

							public void setDialogSize(final Control parent) {
								final String key = featureEType.getName() + ".list"; //$NON-NLS-1$
								Point p = getShell().getSize();
								int width = preferenceStore.getInt("dialog."+key+".width"); //$NON-NLS-1$ //$NON-NLS-2$
								if (width==0)
									width = p.x;
								int height = preferenceStore.getInt("dialog."+key+".height"); //$NON-NLS-1$ //$NON-NLS-2$
								if (height==0)
									height = p.y;
								getShell().setSize(width,height);
								
								p = getShell().getLocation();
								int x = preferenceStore.getInt("dialog."+key+".x"); //$NON-NLS-1$ //$NON-NLS-2$
								if (x==0)
									x = p.x;
								int y = preferenceStore.getInt("dialog."+key+".y"); //$NON-NLS-1$ //$NON-NLS-2$
								if (y==0)
									y = p.y;
								getShell().setLocation(x,y);

								getShell().addControlListener(new ControlListener() {
									public void controlMoved(ControlEvent e)
									{
										Point p = getShell().getLocation();
										preferenceStore.setValue("dialog."+key+".x", p.x); //$NON-NLS-1$ //$NON-NLS-2$
										preferenceStore.setValue("dialog."+key+".y", p.y); //$NON-NLS-1$ //$NON-NLS-2$
									}
									
									public void controlResized(ControlEvent e)
									{
										Point p = getShell().getSize();
										preferenceStore.setValue("dialog."+key+".width", p.x); //$NON-NLS-1$ //$NON-NLS-2$
										preferenceStore.setValue("dialog."+key+".height", p.y); //$NON-NLS-1$ //$NON-NLS-2$
									}
							
								});
								
								if (parent.getLayoutData() instanceof GridData) {
									GridData data = (GridData)parent.getLayoutData();
									// TODO: figure out why this dialog insists on resizing its shell
									// even after we've set the shell bounds.
									data.widthHint = width - 16;
									data.heightHint = height - 38;
								}
//								getShell().pack();
							}
						};

						if (featureEditorDialog.open() == Window.OK) {
							updateEObject((EList<EObject>) featureEditorDialog.getResult());
							updateTextField();
						}
					}
				});
			}
		}

		return text;
	}

	private void updateEObject(final EObject result) {
		TransactionalEditingDomain domain = getDiagramEditor().getEditingDomain();
		domain.getCommandStack().execute(new RecordingCommand(domain) {
			@Override
			protected void doExecute() {
				if (result == null) {
					references.clear();
					return;
				}
				if (!references.contains(result)) {
					references.add(result);
				}
			}
		});
	}
	
	private void updateEObject(final EList<EObject> result) {
		TransactionalEditingDomain domain = getDiagramEditor().getEditingDomain();
		domain.getCommandStack().execute(new RecordingCommand(domain) {
			@Override
			protected void doExecute() {

				if (result == null) {
					references.clear();
					return;
				}
				references.retainAll(result);
				for (EObject di : result) {
					if (!references.contains(di)) {
						references.add(di);
					}
				}
			}
		});
	}

	private void updateTextField() {
		String listText = ""; //$NON-NLS-1$
		if (references != null) {
			for (int i = 0; i < references.size() - 1; i++) {
				listText += ExtendedPropertiesProvider.getTextValue(references.get(i)) + ", "; //$NON-NLS-1$
			}
			if (references.size() > 0) {
				listText += ExtendedPropertiesProvider.getTextValue(references.get(references.size() - 1));
			}
		}

		if (editButton!=null)
			editButton.setEnabled(getChoiceOfValues(object,feature).size()>0);

		text.setText(listText);
	}
	
	@Override
	public void notifyChanged(Notification notification) {
		if ( notification.getEventType() == -1 ||
				(object == notification.getNotifier() && feature == notification.getFeature())) {
			super.notifyChanged(notification);
			updateTextField();
		}
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		text.setVisible(visible);
		GridData data = (GridData)text.getLayoutData();
		data.exclude = !visible;
		if (buttons!=null) {
			buttons.setVisible(visible);
			data = (GridData)buttons.getLayoutData();
			data.exclude = !visible;
		}
	}
	
	public void dispose() {
		super.dispose();
		if (editButton!=null && !editButton.isDisposed()) {
			editButton.dispose();
			editButton = null;
		}
		if (text!=null && !text.isDisposed()) {
			text.dispose();
			text = null;
		}
		if (buttons!=null && !buttons.isDisposed()) {
			buttons.dispose();
			buttons = null;
		}
	}
	
	public Control getControl() {
		return text;
	}

	@Override
	public List getValue() {
		Object v = object.eGet(feature);
		if (v instanceof List) {
			return (List)v;
		}
		return null;
	}
}
