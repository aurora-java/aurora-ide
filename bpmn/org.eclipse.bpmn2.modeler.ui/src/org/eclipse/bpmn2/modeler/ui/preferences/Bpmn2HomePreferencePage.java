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
package org.eclipse.bpmn2.modeler.ui.preferences;

import java.util.Iterator;
import java.util.List;

import org.eclipse.bpmn2.modeler.core.Activator;
import org.eclipse.bpmn2.modeler.core.preferences.Bpmn2Preferences;
import org.eclipse.bpmn2.modeler.help.IHelpContexts;
import org.eclipse.bpmn2.modeler.ui.Messages;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferencePageContainer;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class Bpmn2HomePreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	
	Bpmn2Preferences preferences;

	public Bpmn2HomePreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(Messages.Bpmn2PreferencePage_HomePage_Description);
		preferences = Bpmn2Preferences.getInstance();
	}
	
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getShell(), IHelpContexts.User_Preferences);
	}

	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	@Override
	public void createFieldEditors() {

		Group group = new Group(getFieldEditorParent(), SWT.NONE);
		group.setLayout(new GridLayout(3,false));
		group.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));
		group.setText(Messages.Bpmn2HomePreferencePage_Default_DI_Values_Title);

		BPMNDIAttributeDefaultComboFieldEditor isHorizontal = new BPMNDIAttributeDefaultComboFieldEditor(
				Bpmn2Preferences.PREF_IS_HORIZONTAL,
				Bpmn2Preferences.PREF_IS_HORIZONTAL_LABEL,
				group);
		addField(isHorizontal);

		BPMNDIAttributeDefaultComboFieldEditor isExpanded = new BPMNDIAttributeDefaultComboFieldEditor(
				Bpmn2Preferences.PREF_IS_EXPANDED,
				Bpmn2Preferences.PREF_IS_EXPANDED_LABEL,
				group);
		addField(isExpanded);

		BPMNDIAttributeDefaultComboFieldEditor isMessageVisible = new BPMNDIAttributeDefaultComboFieldEditor(
				Bpmn2Preferences.PREF_IS_MESSAGE_VISIBLE,
				Bpmn2Preferences.PREF_IS_MESSAGE_VISIBLE_LABEL,
				group);
		addField(isMessageVisible);

		BPMNDIAttributeDefaultComboFieldEditor isMarkerVisible = new BPMNDIAttributeDefaultComboFieldEditor(
				Bpmn2Preferences.PREF_IS_MARKER_VISIBLE,
				Bpmn2Preferences.PREF_IS_MARKER_VISIBLE_LABEL,
				group);
		addField(isMarkerVisible);
		
		BooleanFieldEditor saveBPMNLabels = new BooleanFieldEditor(
				Bpmn2Preferences.PREF_SAVE_BPMNLABELS,
				Bpmn2Preferences.PREF_SAVE_BPMNLABELS_LABEL,
				getFieldEditorParent());
		addField(saveBPMNLabels);
		
		ComboFieldEditor resolveExternals = new ComboFieldEditor(
				Bpmn2Preferences.PREF_RESOLVE_EXTERNALS,
				Bpmn2Preferences.PREF_RESOLVE_EXTERNALS_LABEL,
				new String[][]  {
						{Messages.Bpmn2PreferencePage_HomePage_Resolve_Externals_Always, "1"},
						{Messages.Bpmn2PreferencePage_HomePage_Resolve_Externals_Never, "0"},
						{Messages.Bpmn2PreferencePage_HomePage_Resolve_Externals_Prompt, "2"} },
				getFieldEditorParent());
		addField(resolveExternals);
		
		IntegerFieldEditor connectionTimeout = new IntegerFieldEditor(
				Bpmn2Preferences.PREF_CONNECTION_TIMEOUT,
				Bpmn2Preferences.PREF_CONNECTION_TIMEOUT_LABEL,
				getFieldEditorParent());
		addField(connectionTimeout);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	@Override
	protected void performDefaults() {
		preferences.setToDefault(Bpmn2Preferences.PREF_IS_HORIZONTAL);
		preferences.setToDefault(Bpmn2Preferences.PREF_IS_EXPANDED);
		preferences.setToDefault(Bpmn2Preferences.PREF_IS_MESSAGE_VISIBLE);
		preferences.setToDefault(Bpmn2Preferences.PREF_IS_MARKER_VISIBLE);
		preferences.setToDefault(Bpmn2Preferences.PREF_CONNECTION_TIMEOUT);
		preferences.setToDefault(Bpmn2Preferences.PREF_SAVE_BPMNLABELS);
		super.performDefaults();
	}
	
	public static IPreferencePage getPage(IPreferencePageContainer container, String nodeId) {
		PreferenceDialog pd = (PreferenceDialog) container;
		PreferenceManager pm = pd.getPreferenceManager();

		List nodes = pm.getElements(PreferenceManager.POST_ORDER);
		for (Iterator i = nodes.iterator(); i.hasNext();) {
			IPreferenceNode node = (IPreferenceNode) i.next();
			if (node.getId().equals(nodeId)) {
				return node.getPage();
			}
		}
		return null;
	}

}