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
package org.eclipse.bpmn2.modeler.ui.preferences;

import org.eclipse.bpmn2.modeler.core.builder.BPMN2Nature;
import org.eclipse.bpmn2.modeler.core.preferences.Bpmn2Preferences;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.bpmn2.modeler.ui.Activator;
import org.eclipse.bpmn2.modeler.ui.Messages;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;
import org.osgi.service.prefs.BackingStoreException;

public class Bpmn2PropertyPage extends PropertyPage {

	private Bpmn2Preferences preferences;
	
	private Combo cboRuntimes;
	private Button btnCheckProjectNature;
	private Button addRemoveProjectNature;
	private IProject project;
	
	public Bpmn2PropertyPage() {
		super();
		setTitle("BPMN2"); //$NON-NLS-1$
		setDescription(Messages.Bpmn2PropertyPage_HomePage_Description);
	}

	@Override
	protected Control createContents(Composite parent) {
		loadPrefs();
		
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(3, false));

		Label label = new Label(container, SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		label.setText(Bpmn2Preferences.PREF_TARGET_RUNTIME_LABEL);
		
		cboRuntimes = new Combo(container, SWT.READ_ONLY);
		cboRuntimes.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));

		addRemoveProjectNature = new Button(container, SWT.CHECK);
		addRemoveProjectNature.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));
		addRemoveProjectNature.setText("Enable BPMN2 Project Nature");
		try {
			if (project.getNature(BPMN2Nature.NATURE_ID)==null) {
				addRemoveProjectNature.setSelection(false);
			}
			else {
				addRemoveProjectNature.setSelection(true);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		addRemoveProjectNature.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean enabled = addRemoveProjectNature.getSelection();
				btnCheckProjectNature.setEnabled(!enabled);
				BPMN2Nature.setBPMN2Nature(project, enabled);
			}
		});
		boolean enabled = addRemoveProjectNature.getSelection();

		btnCheckProjectNature = new Button(container, SWT.CHECK);
		GridData d = new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1);
		d.horizontalIndent = 20;
		btnCheckProjectNature.setLayoutData(d);
		btnCheckProjectNature.setText(Bpmn2Preferences.PREF_CHECK_PROJECT_NATURE_LABEL);
		btnCheckProjectNature.setEnabled(!enabled);

		initData();

		return container;
	}

	private void restoreDefaults() {
		preferences.setToDefault(Bpmn2Preferences.PREF_TARGET_RUNTIME);
		preferences.getRuntime();
		initData();
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		restoreDefaults();
	}
	
	public void loadPrefs() {
		project = (IProject) getElement().getAdapter(IProject.class);
		preferences = Bpmn2Preferences.getInstance(project);
		preferences.useProjectPreferences();
	}

	private void initData() {
		btnCheckProjectNature.setSelection( preferences.getCheckProjectNature() );
		
		TargetRuntime rt = preferences.getRuntime();
		int i = 0;
		for (TargetRuntime r : TargetRuntime.createTargetRuntimes()) {
			cboRuntimes.add(r.getName());
			cboRuntimes.setData(r.getName(), r);
			if (r == rt)
				cboRuntimes.select(i);
			++i;
		}
	}

	@Override
	public boolean performOk() {
		setErrorMessage(null);
		try {
			updateData();
		} catch (BackingStoreException e) {
			Activator.showErrorWithLogging(e);
		}
		return true;
	}

	@Override
	public void dispose() {
		preferences.dispose();
		super.dispose();
	}

	private void updateData() throws BackingStoreException {
		int i = cboRuntimes.getSelectionIndex();
		TargetRuntime rt = TargetRuntime.createTargetRuntimes().get(i);
		preferences.setRuntime(rt);
		preferences.setCheckProjectNature(btnCheckProjectNature.getSelection());
		
		preferences.flush();
	}
}
