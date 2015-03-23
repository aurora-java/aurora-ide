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

import java.lang.reflect.Field;

import org.eclipse.bpmn2.modeler.core.ToolTipProvider;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.merrimac.DefaultBusinessObjectDelegate;
import org.eclipse.bpmn2.modeler.core.merrimac.IBusinessObjectDelegate;
import org.eclipse.bpmn2.modeler.core.merrimac.IConstants;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractDetailComposite;
import org.eclipse.bpmn2.modeler.core.utils.ErrorUtils;
import org.eclipse.bpmn2.modeler.core.utils.JavaReflectionUtil;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.core.validation.ValidationStatusAdapter;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Base class for EObject feature editors. All subclasses must render the given object's feature,
 * which may be either an attribute, a reference to an EObject, or a list of EObject references.
 * Subclasses must also provide means to populate the display widgets from the feature and save
 * modifications to the feature made in the display widget.
 * 
 * @author Bob Brodt
 */
public abstract class ObjectEditor implements INotifyChangedListener {
	
	public static int ID_CREATE_BUTTON = 1;
	public static int ID_EDIT_BUTTON = 2;
	public static int ID_DELETE_BUTTON = 3;
	public static int ID_OTHER_BUTTONS = 4;

	protected EObject object;
	protected EStructuralFeature feature;
	protected AbstractDetailComposite parent;
	private Label label;
	protected ControlDecoration decoration;
	protected int style;
	protected boolean isWidgetUpdating = false;
	private IBusinessObjectDelegate boDelegate;

	public ObjectEditor(AbstractDetailComposite parent, EObject object, EStructuralFeature feature) {
		this.parent = parent;
		this.object = object;
		this.feature = feature;
		this.style = SWT.NONE;
	}
	
	/**
	 * This must be implemented by the ObjectEditor subclasses and should not be made public - clients
	 * should use one of the public createControl() methods instead so that the notifyChange listeners
	 * are hooked in to the change notification chain.
	 *  
	 * @param composite - parent composite for this ObjectEditor control
	 * @param label - a text label displayed to the left of the control
	 * @param style - editor control SWT style flags
	 * @return the control created by the ObjectEditor subclasses (e.g. a Text, or Combo)
	 */
	protected abstract Control createControl(Composite composite, String label, int style);
	
	public void setStyle(int style) {
		this.style = style;
	}
	
	public Control createControl(Composite composite, String label) {
		Control c = createControl(composite,label,style);
		c.setData(IConstants.NOTIFY_CHANGE_LISTENER_KEY, this);
		return c; 
	}
	
	public Control createControl(String label) {
		Control c = createControl(parent,label,style);
		c.setData(IConstants.NOTIFY_CHANGE_LISTENER_KEY, this);
		return c; 
	}

	public IBusinessObjectDelegate getBusinessObjectDelegate() {
		if (boDelegate==null)
			boDelegate = new DefaultBusinessObjectDelegate(getDiagramEditor().getEditingDomain());
		return boDelegate;
	}

	protected Object getExtendedProperty(String property) {
		Object result = null;
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object);
		if (adapter!=null && feature!=null) {
			result = adapter.getProperty(feature, property);
		}
		return result;
	}

	protected void setExtendedProperty(String property, Object value) {
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object);
		if (adapter!=null && feature!=null) {
			adapter.setProperty(feature, property, value);
		}
	}
	
	public EStructuralFeature getFeature() {
		return feature;
	}

	public EObject getObject() {
		return object;
	}

	public void setObject(EObject object) {
		this.object = object;
	}
	
	public void setObject(EObject object, EStructuralFeature feature) {
		this.object = object;
		this.feature = feature;
	}
	
	protected FormToolkit getToolkit() {
		return parent.getToolkit();
	}
	
	protected DiagramEditor getDiagramEditor() {
		return parent.getDiagramEditor();
	}

	protected Diagram getDiagram() {
		return getDiagramEditor().getDiagramTypeProvider().getDiagram();
	}
	
	protected Label createLabel(Composite parent, String name) {
		label = getToolkit().createLabel(parent, name);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		updateLabelDecorator();
		return label;
	}
	
	public Label getLabel() {
		return label;
	}

	private boolean statusApplies(IStatus status) {
        if (status instanceof ConstraintStatus) {
        	ConstraintStatus cs = (ConstraintStatus)status;
        	for (EObject f : cs.getResultLocus()) {
        		if (f instanceof EStructuralFeature && f==this.feature) {
        			return true;
        		}
        	}
        }
        return false;
	}
	
	protected FeatureEditingDialog createFeatureEditingDialog(EObject value) {
		return new FeatureEditingDialog(getDiagramEditor(), object, feature, value);
	}

	/**
	 * Returns a descriptive text string for use as a tooltip on the Label for this editor.
	 * The default implementation constructs a key from the object and feature name owned by this
	 * editor, and looks up the description text in the messages resource of the parent plugin.
	 * 
	 * @return
	 */
	protected String getToolTipText() {
		String text = (String) getExtendedProperty(ExtendedPropertiesAdapter.LONG_DESCRIPTION);
		if (text==null || text.isEmpty())
			text = ToolTipProvider.INSTANCE.getToolTip(parent, object, feature);
   		if (text==null || text.isEmpty())
   			text = NLS.bind(Messages.ObjectEditor_No_Description, label.getText());

   		setExtendedProperty(ExtendedPropertiesAdapter.LONG_DESCRIPTION, text);
    	return text;
	}
	
	/**
	 * Updates the error decorators and tooltips of this editor's Label widget.
	 */
	protected void updateLabelDecorator() {
		
		if (label!=null && !label.isDisposed()) {
			String tooltip = label.getToolTipText();
			
			if (tooltip==null && object!=null && feature!=null) {
	   			label.setToolTipText(getToolTipText());
			}
			
			
			boolean applies = false;
	    	String text = null;
	    	String image = null;
	
	    	if (isVisible()) {
		        ValidationStatusAdapter statusAdapter = (ValidationStatusAdapter) EcoreUtil.getRegisteredAdapter(
		        		object, ValidationStatusAdapter.class);
		        if (statusAdapter != null) {
		            final IStatus status = statusAdapter.getValidationStatus();
		            if (status.isMultiStatus()) {
		            	for (IStatus s : status.getChildren()) {
		            		if (statusApplies(s)) {
		            			applies = true;
		            			break;
		            		}
		            	}
		            }
		            else if (statusApplies(status))
		            	applies = true;
		            
		            if (applies) {
			            text = status.getMessage();
			            switch (status.getSeverity()) {
			            case IStatus.INFO:
			                image = ISharedImages.IMG_OBJS_INFO_TSK;
			                break;
			            case IStatus.WARNING:
			                image = ISharedImages.IMG_DEC_FIELD_WARNING;
			                break;
			            case IStatus.ERROR:
			                image = ISharedImages.IMG_DEC_FIELD_ERROR;
			                break;
			            default:
			                break;
			            }
		            }
		        }
			}
			
	        if (applies) {
	        	if (decoration==null) {
	        		decoration = new ControlDecoration(label, SWT.TOP | SWT.LEFT);
	        	}
	        	decoration.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(image));
	        	decoration.show();
	        	decoration.setDescriptionText(text);
	        }
	        else {
	        	if (decoration!=null) {
	        		decoration.hide();
	        		decoration.dispose();
	        		decoration = null;
	        	}
	        }
		}
	}
	
	protected boolean setValue(final Object result) {
		boolean success = getBusinessObjectDelegate().setValue(object, feature, result);
		if (!success) {
			ErrorUtils.showErrorMessage(
				NLS.bind(
					Messages.ObjectEditor_Set_Error_Message,
					new Object[] {
						getBusinessObjectDelegate().getTextValue(object),
						getBusinessObjectDelegate().getLabel(object,feature),
						ModelUtil.getTextValue(result)
					}
				)
			);
			return false;
		}
		return true;
	}

	public abstract Object getValue();
	
	@Override
	public void notifyChanged(Notification notification) {
		updateLabelDecorator();
	}
	
	public void setVisible(boolean visible) {
		label.setVisible(visible);
		GridData data = (GridData)label.getLayoutData();
		data.exclude = !visible;
		updateLabelDecorator();
	}
	
	public boolean isVisible() {
		return label.isVisible();
	}

	public void dispose() {
		if (label!=null && !label.isDisposed()) {
			label.dispose();
			label = null;
		}
		if (decoration!=null) {
    		decoration.hide();
			decoration.dispose();
			decoration = null;
		}
	}
	
	public Control getControl() {
		return label;
	}

	protected boolean isMultiLineText() {
		return getBusinessObjectDelegate().isMultiLineText(object,feature);
	}
	
	protected boolean canEdit() {
		return getBusinessObjectDelegate().canEdit(object,feature);
	}

	protected boolean canCreateNew() {
		return getBusinessObjectDelegate().canCreateNew(object,feature);
	}

	protected boolean canEditInline() {
		return getBusinessObjectDelegate().canEditInline(object,feature);
	}

	protected boolean canSetNull() {
		return getBusinessObjectDelegate().canSetNull(object,feature);
	}

	protected boolean canAdd() {
		return false;
	}

	protected boolean canRemove() {
		return false;
	}
	
	public void setEditable(boolean editable) {
		Control control = getControl();
		if (control instanceof Text) {
			((Text)control).setEditable(editable);
			control.setBackground(control.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
			control.setForeground(control.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
			control.setData(AbstractObjectEditingDialog.DO_NOT_ADAPT, Boolean.TRUE);
		}
	}
}
