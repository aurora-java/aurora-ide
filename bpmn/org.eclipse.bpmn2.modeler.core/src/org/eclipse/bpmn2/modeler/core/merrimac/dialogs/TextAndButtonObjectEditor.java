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

import org.eclipse.bpmn2.modeler.core.Activator;
import org.eclipse.bpmn2.modeler.core.IConstants;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractDetailComposite;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Bob Brodt
 *
 */
public abstract class TextAndButtonObjectEditor extends TextObjectEditor {

	protected static int ID_DEFAULT_BUTTON = ID_OTHER_BUTTONS+0;
	protected static int ID_ADD_BUTTON = ID_OTHER_BUTTONS+1;
	protected static int ID_REMOVE_BUTTON = ID_OTHER_BUTTONS+2;
	
	protected Button defaultButton;
	protected Button addButton;
	protected Button removeButton;

	/**
	 * @param parent
	 * @param object
	 * @param feature
	 */
	public TextAndButtonObjectEditor(AbstractDetailComposite parent, EObject object, EStructuralFeature feature) {
		super(parent, object, feature);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.ui.property.editors.ObjectEditor#createControl(org.eclipse.swt.widgets.Composite, java.lang.String, int)
	 */
	@Override
	protected Control createControl(Composite composite, String label, int style) {
		super.createControl(composite, label, style);

		// we assume that the "Edit" button will handle editing of this read-only text field
		text.setEditable(false);
		GridData textLayoutData = (GridData)text.getLayoutData();
		textLayoutData.horizontalSpan = 1;
		boolean multiLine = ((style & SWT.MULTI) != 0);

		Composite buttons = new Composite(composite,SWT.NONE);
		buttons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		buttons.setLayout(new FillLayout((style & SWT.MULTI)!=0 ? SWT.VERTICAL : SWT.HORIZONTAL));

		if (canAdd()) {
			addButton = getToolkit().createButton(buttons, null, SWT.PUSH);
			addButton.setImage( Activator.getDefault().getImage(IConstants.ICON_ADD_20));
			if (multiLine)
				textLayoutData.heightHint += 25;
		}
		
		if (canRemove()) {
			removeButton = getToolkit().createButton(buttons, null, SWT.PUSH);
			removeButton.setImage( Activator.getDefault().getImage(IConstants.ICON_REMOVE_20));
			if (multiLine)
				textLayoutData.heightHint += 25;
		}
		
		defaultButton = getToolkit().createButton(buttons, null, SWT.PUSH);
		defaultButton.setImage( Activator.getDefault().getImage(IConstants.ICON_EDIT_20));
		
		updateText();

		SelectionAdapter editListener = new SelectionAdapter() {

			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				int id = ID_DEFAULT_BUTTON;
				if (e.widget == addButton)
					id = ID_ADD_BUTTON;
				else if (e.widget == removeButton)
					id = ID_REMOVE_BUTTON;
				buttonClicked(id);
			}
		};
		defaultButton.addSelectionListener(editListener);
		if (canAdd())
			addButton.addSelectionListener(editListener);
		if (canRemove())
			removeButton.addSelectionListener(editListener);

		return text;
	}
	
	public void dispose() {
		super.dispose();
		if (defaultButton!=null && !defaultButton.isDisposed()) {
			defaultButton.dispose();
			defaultButton = null;
		}
		if (addButton!=null && !addButton.isDisposed()) {
			addButton.dispose();
			addButton = null;
		}
		if (removeButton!=null && !removeButton.isDisposed()) {
			removeButton.dispose();
			removeButton = null;
		}
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (defaultButton!=null && !defaultButton.isDisposed()) {
			defaultButton.setVisible(visible);
		}
		if (addButton!=null && !addButton.isDisposed()) {
			addButton.setVisible(visible);
		}
		if (removeButton!=null && !removeButton.isDisposed()) {
			removeButton.setVisible(visible);
		}
		text.getParent().pack();
		text.getParent().layout();
	}
	
	/**
	 * The implementation must override this to handle the "Edit..." button click.
	 * @param buttonId TODO
	 */
	protected abstract void buttonClicked(int buttonId); 
}
