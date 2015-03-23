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
package org.eclipse.bpmn2.modeler.core.merrimac.clad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.merrimac.providers.ColumnTableProvider;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.ui.provider.PropertyDescriptor.EDataTypeCellEditor;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.core.runtime.Assert;

public class TableColumn extends ColumnTableProvider.Column implements ILabelProvider, ICellModifier {

	protected AbstractListComposite listComposite;
	// the underlying EObject of the table row
	protected EObject object;
	// the EStructuralFeature being managed for this table column
	protected EStructuralFeature feature;
	// The column cell editor
	protected boolean editable = true;
	protected String headerText = null;

	public TableColumn(EObject o, EStructuralFeature f) {
		this(null,o,f);
	}

	public TableColumn(AbstractListComposite abstractListComposite, EObject o, EStructuralFeature f) {
		this.listComposite = abstractListComposite;
		object = o;
		feature = f;
	}

	public TableColumn(EObject o, String f) {
		this(null,o,f);
	}

	public TableColumn(AbstractListComposite abstractListComposite, EObject o, String f) {
		this.listComposite = abstractListComposite;
		object = o;
		feature = o.eClass().getEStructuralFeature(f);
	}
	
	public void setOwner(AbstractListComposite abstractListComposite) {
		this.listComposite = abstractListComposite;
	}
	
	public void setHeaderText(String text) {
		headerText = text;
	}
	
	@Override
	public String getHeaderText() {
		if (headerText!=null)
			return headerText;
		
		String text = ""; //$NON-NLS-1$
		if (feature!=null) {
			if (feature.eContainer() instanceof EClass) {
				EClass eclass = this.listComposite.getListItemClass();
				text = ExtendedPropertiesProvider.getLabel(eclass, feature);
			}
			else
				text = ModelUtil.toCanonicalString(feature.getName());
		}
		return text;
	}

	@Override
	public String getProperty() {
		if (feature!=null)
			return feature.getName(); //$NON-NLS-1$
		return ""; //$NON-NLS-1$
	}

	@Override
	public int getInitialWeight() {
		return 10;
	}

	public String getText(Object element) {
		if (element instanceof EObject) {
			return ExtendedPropertiesProvider.getTextValue((EObject)element,feature);
		}
		return element.toString();
	}
	
	protected int getColumnIndex() {
		return listComposite.getColumnProvider().getColumns().indexOf(this);
	}

	protected Composite getParent() {
		return tableViewer.getTable();
	}
	
	protected void setCellEditor(CellEditor ce) {
		CellEditor[] cellEditors = tableViewer.getCellEditors();
		int index = getColumnIndex();
		if (index>=0 && index<cellEditors.length) {
			CellEditor oldCe = cellEditors[index];
			if (oldCe!=null && oldCe!=ce)
				oldCe.dispose();
			cellEditors[index] = ce;
		}
	}
	
	protected CellEditor getCellEditor() {
		CellEditor[] cellEditors = tableViewer.getCellEditors();
		int index = getColumnIndex();
		if (index>=0 && index<cellEditors.length) {
			return cellEditors[index];
		}
		return null;
	}

	public CellEditor createCellEditor (Composite parent) {
		CellEditor ce = null;
		if (editable && feature!=null) {
			EClassifier ec = feature.getEType();
			Class ic = ec.getInstanceClass();
			if (boolean.class.equals(ic)) {
				ce = new CustomCheckboxCellEditor(parent);
			}
			else if (ec instanceof EEnum) {
				ce = new CustomComboBoxCellEditor(parent, feature);
			}
			else if (ExtendedPropertiesProvider.isMultiChoice(feature.eContainer(), feature)) {
				ce = new CustomComboBoxCellEditor(parent, feature);
			}
			else if (ec instanceof EDataType) {
				ce = new EDataTypeCellEditor((EDataType)ec, parent);
				ce.setValidator(new CustomDataTypeValidator());
			}
			else if (ic==EObject.class) {
				ce = new StringWrapperCellEditor(parent);
			}
		}
		return ce;
	}
	
	public class CustomDataTypeValidator implements ICellEditorValidator {
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ICellEditorValidator#isValid(java.lang.Object)
		 */
		@Override
		public String isValid(Object value) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean canModify(Object element, String property) {
		// This is the only point in the cell editor lifecycle where we have access to the
		// cell object being edited. 
		if (editable && listComposite.getColumnProvider().canModify(object, feature, (EObject)element)) {
			CellEditor ce = getCellEditor();
			return ce!=null;
		}
		return false;
	}

	public void modify(Object element, String property, Object value) {
		modify((EObject)element, feature, value);
	}

	protected void modify(final EObject object, EStructuralFeature feature, Object value) {
		CellEditor ce = getCellEditor();
		if (ce instanceof CustomComboBoxCellEditor) {
			value = ((CustomComboBoxCellEditor)ce).getChoice(value);
		}
		
		boolean result = ExtendedPropertiesProvider.setValue(object, feature, value);
//		if (result==false || getDiagramEditor().getDiagnostics()!=null) {
//			// revert the change and display error errorList message.
//			ErrorUtils.showErrorMessage(getDiagramEditor().getDiagnostics().getMessage());
//		}
//		else {
//			ErrorUtils.showErrorMessage(null);
//			tableViewer.refresh();
//		}
		if (result) {
			tableViewer.refresh(object);
		}
	}
	
	@Override
	public Object getValue(Object element, String property) {
		CellEditor ce = getCellEditor();
		if (element instanceof EObject) {
			if (ce instanceof CustomCheckboxCellEditor) {
				return ce.getValue();
			}
			else if (ce instanceof CustomComboBoxCellEditor) {
				// for combobox cell editors, the returned value is a list of strings
				return ce.getValue();
			}
			else {
				// all other types of cell editors accept the object/feature value
				EObject object = (EObject)element;
				return object.eGet(feature);
			}
		}
		return getText(element);
	}
	
	
	protected DiagramEditor getDiagramEditor() {
		return listComposite.getDiagramEditor();
	}
	
	protected TransactionalEditingDomain getEditingDomain() {
		return getDiagramEditor().getEditingDomain();
	}
	
	public static class CustomCheckboxCellEditor extends ComboBoxCellEditor {

		private static String[] items = new String[] { "false", "true" }; //$NON-NLS-1$ //$NON-NLS-2$
		
		public CustomCheckboxCellEditor(Composite parent) {
			super(parent, items,SWT.READ_ONLY);
		}
		
		@Override
		public String[] getItems() {
			return items;
		}

		@Override
		public void setItems(String[] items) {
			super.setItems(this.items);
		}

		@Override
		protected Object doGetValue() {
			Integer value = (Integer)super.doGetValue();
			return new Boolean(value.intValue()!=0);
		}

		@Override
		protected void doSetValue(Object value) {
			if (value instanceof Boolean) {
				value = new Integer( ((Boolean)value).booleanValue() ? 1 : 0 );
			}
			else if (value instanceof String) {
				for (int i=0; i<items.length; ++i) {
					if (value.equals(items[i])) {
						value = new Integer(i);
						break;
					}
				}
			}
			super.doSetValue(value);
		}
		
	}

	public class CustomComboBoxCellEditor extends ComboBoxCellEditor {
		
		// list of choices as constructed by ExtendedPropertiesAdapter.FeatureDescriptor#getChoiceOfValues()
		protected Hashtable<String,Object> choices = null;

		public CustomComboBoxCellEditor(Composite parent, EStructuralFeature feature) {
			super(parent, new String[] {""}, SWT.READ_ONLY); //$NON-NLS-1$
		}
		
		public void activate(ColumnViewerEditorActivationEvent activationEvent) {
			Object source = activationEvent.getSource();
			if (source instanceof ViewerCell) {
				Object element = ((ViewerCell)source).getElement();
				if (element instanceof EObject) {
					EObject object = (EObject)element;
					Object current = object.eGet(feature);
					setValue(object, feature, current);
				}
			}
		}

		public void setValue(EObject object, EStructuralFeature feature, Object current) {
			
			// build the list of valid choices for this object/feature and cache it;
			// we'll need it again later in modify()
			// NOTE: This list should be rebuilt every time before we activate this
			// cell editor since the choices may have changed.
			List<String> items = new ArrayList<String>();
			choices = ExtendedPropertiesProvider.getChoiceOfValues(object, feature);
			if (ExtendedPropertiesProvider.canSetNull(object,feature))
				items.add(""); //$NON-NLS-1$
			items.addAll(choices.keySet());
			Collections.sort(items);
			this.setItems(items.toArray(new String[items.size()]));
			
			// find the index of the current value in the choices list
			// need to handle both cases where current value matches the
			// choices key (a String) or an EObject
			int index = -1;
			for (int i=0; i<items.size(); ++i) {
				if (current == choices.get(items.get(i))) {
					index = i;
					break;
				}
				if (current instanceof String) {
					if (current.equals(items.get(i))) {
						index = i;
						break;
					}
				}
			}
			this.setValue(new Integer(index));
		}
		
		public Object getChoice(Object value) {
			// for combobox cell editors, getValue() returns an Integer
			Assert.isTrue(choices!=null && value instanceof Integer);
			int index = ((Integer)value).intValue();
			if (index>=0) {
				// look up the real value from the list of choices created by getValue()
				String[] items = ((ComboBoxCellEditor)getCellEditor()).getItems();
				value = choices.get(items[index]);
			}
			else
				value = null;
			return value;
		}
	}
	public class StringWrapperCellEditor extends TextCellEditor {

		public StringWrapperCellEditor(Composite parent) {
			super(parent);
		}

		@Override
		protected Object doGetValue() {
			String value = (String)super.doGetValue();
			return ModelUtil.createStringWrapper(value);
		}

		@Override
		protected void doSetValue(Object value) {
			if (value==null)
				value = ""; //$NON-NLS-1$
			else
				value = ModelUtil.getStringWrapperTextValue(value);
			super.doSetValue(value);
		}

	}
}