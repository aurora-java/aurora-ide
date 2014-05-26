/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package ext.org.eclipse.jdt.internal.ui.refactoring;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import org.eclipse.jdt.core.dom.Modifier;

public class VisibilityControlUtil {
	private VisibilityControlUtil(){}

	public static Composite createVisibilityControl(Composite parent, final IVisibilityChangeListener visibilityChangeListener, int[] availableVisibilities, int correctVisibility) {
		List<Integer> allowedVisibilities= convertToIntegerList(availableVisibilities);
		if (allowedVisibilities.size() == 1)
			return null;

		Group group= new Group(parent, SWT.NONE);
		group.setText(RefactoringMessages.VisibilityControlUtil_Access_modifier);
		GridData gd= new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);
		GridLayout layout= new GridLayout();
		layout.makeColumnsEqualWidth= true;
		layout.numColumns= 4;
		group.setLayout(layout);

		String[] labels= new String[] {
			"&public", //$NON-NLS-1$
			"pro&tected", //$NON-NLS-1$
			RefactoringMessages.VisibilityControlUtil_defa_ult_4,
			"pri&vate" //$NON-NLS-1$
		};
		Integer[] data= new Integer[] {
					new Integer(Modifier.PUBLIC),
					new Integer(Modifier.PROTECTED),
					new Integer(Modifier.NONE),
					new Integer(Modifier.PRIVATE)};
		Integer initialVisibility= new Integer(correctVisibility);
		for (int i= 0; i < labels.length; i++) {
			Button radio= new Button(group, SWT.RADIO);
			Integer visibilityCode= data[i];
			radio.setText(labels[i]);
			radio.setData(visibilityCode);
			radio.setSelection(visibilityCode.equals(initialVisibility));
			radio.setEnabled(allowedVisibilities.contains(visibilityCode));
			radio.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					visibilityChangeListener.visibilityChanged(((Integer)event.widget.getData()).intValue());
				}
			});
		}
		group.setLayoutData((new GridData(GridData.FILL_HORIZONTAL)));
		return group;
	}

	private static List<Integer> convertToIntegerList(int[] array) {
		List<Integer> result= new ArrayList<Integer>(array.length);
		for (int i= 0; i < array.length; i++) {
			result.add(new Integer(array[i]));
		}
		return result;
	}
}
