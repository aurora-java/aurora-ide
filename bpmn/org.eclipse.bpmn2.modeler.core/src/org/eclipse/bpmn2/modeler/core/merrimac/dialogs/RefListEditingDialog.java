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
package org.eclipse.bpmn2.modeler.core.merrimac.dialogs;

import java.util.Hashtable;
import java.util.Map.Entry;

import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesProvider;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

public class RefListEditingDialog extends AbstractObjectEditingDialog {

	protected EStructuralFeature feature;
	List sourceList;
	Button addButton;
	Button removeButton;
	Button addAllButton;
	Button removeAllButton;
	Button moveUpButton;
	Button moveDownButton;
	List targetList;
	java.util.List<EObject> result = new java.util.ArrayList<EObject>();
	
	public RefListEditingDialog(DiagramEditor editor, EObject object, EStructuralFeature feature) {
		super(editor, object);
		this.feature = feature;
	}

	@Override
	protected Composite createDialogContent(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(3,false));

		sourceList = new List(composite,SWT.BORDER|SWT.MULTI);
		sourceList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		sourceList.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateButtons();
			}
		});

		Composite buttons = new Composite(composite, SWT.NONE);
		buttons.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));

		buttons.setLayout(new FillLayout(SWT.VERTICAL));
		addButton = new Button(buttons,SWT.PUSH);
		addButton.setText(Messages.RefListEditingDialog_Add);
		addButton.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveSelection(sourceList,targetList);
			}
		});
		removeButton = new Button(buttons,SWT.PUSH);
		removeButton.setText(Messages.RefListEditingDialog_Remove);
		removeButton.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveSelection(targetList,sourceList);
			}
		});
		addAllButton = new Button(buttons,SWT.PUSH);
		addAllButton.setText(Messages.RefListEditingDialog_Add_All);
		addAllButton.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sourceList.selectAll();
				moveSelection(sourceList,targetList);
			}
		});
		removeAllButton = new Button(buttons,SWT.PUSH);
		removeAllButton.setText(Messages.RefListEditingDialog_Remove_All);
		removeAllButton.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				targetList.selectAll();
				moveSelection(targetList,sourceList);
			}
		});
		moveUpButton = new Button(buttons,SWT.PUSH);
		moveUpButton.setText(Messages.RefListEditingDialog_Move_Up);
		moveUpButton.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveUp();
			}
		});
		moveDownButton = new Button(buttons,SWT.PUSH);
		moveDownButton.setText(Messages.RefListEditingDialog_Move_Down);
		moveDownButton.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveDown();
			}
		});
		
		targetList = new List(composite,SWT.BORDER|SWT.MULTI);
		targetList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		targetList.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateButtons();
			}
		});
		
		java.util.List<EObject> targets = (java.util.List<EObject>) object.eGet(feature);
		for (EObject value : targets) {
			String key = "" + targetList.getItemCount(); //$NON-NLS-1$
			targetList.add( ExtendedPropertiesProvider.getTextValue(value) );
			targetList.setData(key, value);
		}

		Hashtable<String, Object> sources = ExtendedPropertiesProvider.getChoiceOfValues(object, feature);
		for (Entry<String, Object> entry : sources.entrySet()) {
			if (!targets.contains(entry.getValue())) {
				String key = "" + sourceList.getItemCount(); //$NON-NLS-1$
				sourceList.add(entry.getKey());

				Object value = entry.getValue();
				// this can not be null!
				Assert.isTrue(value instanceof EObject);
				sourceList.setData(key, value);
			}
		}
		
		updateButtons();
		
		return composite;
	}

	public java.util.List<EObject> getResult() {
		return result;
	}
	
	@Override
	protected void okPressed() {
		// compute result list
		int size = targetList.getItemCount();
		for (int i=0; i<size; ++i) {
			String key = "" + i; //$NON-NLS-1$
			result.add((EObject)targetList.getData(key));
		}
		super.okPressed();
	}
	
	private void moveSelection(List sourceList, List targetList) {
		int[] sourceIndex = sourceList.getSelectionIndices();
		int[] targetIndex = new int[sourceIndex.length];
		for (int i=0; i<sourceIndex.length; ++i) {
			targetIndex[i] = targetList.getItemCount();
			String key = "" + sourceIndex[i]; //$NON-NLS-1$
			Object value = sourceList.getData(key);
			String s = sourceList.getItem( sourceIndex[i] );
			targetList.add(s);
			key = "" + targetIndex[i]; //$NON-NLS-1$
			targetList.setData(key,value);
		}
		sourceList.remove(sourceIndex);
		targetList.select(targetIndex);
		updateButtons();
	}
	
	private void moveUp() {
		int index = targetList.getSelectionIndex();
		Assert.isTrue(index>0);
		swap(index,index-1);
	}
	
	private void moveDown() {
		int index = targetList.getSelectionIndex();
		int end = targetList.getItemCount() - 1; 
		Assert.isTrue(index>0 && index<end);
		swap(index,index+1);
	}
	
	private void swap(int index1, int index2) {
		String item1 = targetList.getItem(index1);
		String key1 = "" + index1; //$NON-NLS-1$
		Object value1 = targetList.getData(key1);
		String key2 = "" + index2; //$NON-NLS-1$
		Object value2 = targetList.getData(key2);
		targetList.remove(index1);
		targetList.add(item1, index2);
		targetList.setData(key1,value2);
		targetList.setData(key2,value1);
		targetList.setSelection(index2);
		updateButtons();
	}
	
	private void updateButtons() {
		addButton.setEnabled(sourceList.getSelectionCount()>0);
		removeButton.setEnabled(targetList.getSelectionCount()>0);
		addAllButton.setEnabled(sourceList.getItemCount()>0);
		removeAllButton.setEnabled(targetList.getItemCount()>0);
		if ( targetList.getSelectionCount()==1 ) {
			int end = targetList.getItemCount() - 1;
			int index = targetList.getSelectionIndex();
			moveUpButton.setEnabled(index!=0);
			moveDownButton.setEnabled(index!=end);
		}
		else {
			moveUpButton.setEnabled(false);
			moveDownButton.setEnabled(false);
		}
	}
	
	@Override
	protected String getPreferenceKey() {
		return object.eClass().getName() + "." + feature.getName(); //$NON-NLS-1$
	}

}
