/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.preferences;


import org.eclipse.bpmn2.modeler.core.Activator;
import org.eclipse.bpmn2.modeler.core.preferences.Bpmn2Preferences;
import org.eclipse.bpmn2.modeler.core.preferences.TristateCheckboxFieldEditor;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


@SuppressWarnings("nls")
public class Bpmn2EditorBehaviorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	// Page ID must be the same as defined in plugin.xml
	public static String PAGE_ID = "org.eclipse.bpmn2.modeler.Behavior"; //$NON-NLS-1$

	private Bpmn2Preferences preferences;
	private SettableBooleanFieldEditor btnShowIds;
	
	public Bpmn2EditorBehaviorPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		preferences = Bpmn2Preferences.getInstance();
	}

	@Override
	protected void createFieldEditors() {

		BooleanFieldEditor showAdvancedPropsTab = new BooleanFieldEditor(
				Bpmn2Preferences.PREF_SHOW_ADVANCED_PROPERTIES,
				Bpmn2Preferences.PREF_SHOW_ADVANCED_PROPERTIES_LABEL,
				getFieldEditorParent());
		addField(showAdvancedPropsTab);

		BooleanFieldEditor showDescriptions = new BooleanFieldEditor(
				Bpmn2Preferences.PREF_SHOW_DESCRIPTIONS,
				Bpmn2Preferences.PREF_SHOW_DESCRIPTIONS_LABEL,
				getFieldEditorParent());
		addField(showDescriptions);

		btnShowIds = new SettableBooleanFieldEditor(
				Bpmn2Preferences.PREF_SHOW_ID_ATTRIBUTE,
				Bpmn2Preferences.PREF_SHOW_ID_ATTRIBUTE_LABEL,
				getFieldEditorParent());
		addField(btnShowIds);

		// This belongs in Project Property Page
//		BooleanFieldEditor checkProjectNature = new BooleanFieldEditor(
//				Bpmn2Preferences.PREF_CHECK_PROJECT_NATURE,
//				Bpmn2Preferences.PREF_CHECK_PROJECT_NATURE_LABEL,
//				getFieldEditorParent());
//		addField(checkProjectNature);

		// TODO: is this useful? probably not
//		BooleanFieldEditor simplifyLists = new BooleanFieldEditor(
//				Bpmn2Preferences.PREF_SIMPLIFY_LISTS,
//				Bpmn2Preferences.PREF_SIMPLIFY_LISTS_LABEL,
//				getFieldEditorParent());
//		addField(simplifyLists);

		BooleanFieldEditor usePopupDialogForLists = new BooleanFieldEditor(
				Bpmn2Preferences.PREF_USE_POPUP_DIALOG_FOR_LISTS,
				Bpmn2Preferences.PREF_USE_POPUP_DIALOG_FOR_LISTS_LABEL,
				getFieldEditorParent());
		addField(usePopupDialogForLists);

		BooleanFieldEditor propagateGroupCategories = new BooleanFieldEditor(
				Bpmn2Preferences.PREF_PROPAGATE_GROUP_CATEGORIES,
				Bpmn2Preferences.PREF_PROPAGATE_GROUP_CATEGORIES_LABEL,
				getFieldEditorParent());
		addField(propagateGroupCategories);
		
		BooleanFieldEditor doCoreValidation = new BooleanFieldEditor(
				Bpmn2Preferences.PREF_ALLOW_MULTIPLE_CONNECTIONS,
				Bpmn2Preferences.PREF_ALLOW_MULTIPLE_CONNECTIONS_LABEL,
				getFieldEditorParent());
		addField(doCoreValidation);

		//////////////////////////////////////////////////////////////////////////////
		
		TristateCheckboxFieldEditor popupConfigDialog = new TristateCheckboxFieldEditor(
				Bpmn2Preferences.PREF_POPUP_CONFIG_DIALOG,
				Bpmn2Preferences.PREF_POPUP_CONFIG_DIALOG_LABEL,
				getFieldEditorParent());
		addField(popupConfigDialog);

		//////////////////////////////////////////////////////////////////////////////

		Composite comp = new Composite(getFieldEditorParent(), SWT.NONE);
		comp.setLayout(new GridLayout(1,false));
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd.horizontalIndent = 40;
		comp.setLayoutData(gd);

		TristateCheckboxFieldEditor popupConfigDialogForTasks = new TristateCheckboxFieldEditor(
				Bpmn2Preferences.PREF_POPUP_CONFIG_DIALOG_FOR_ACTIVITIES,
				Bpmn2Preferences.PREF_POPUP_CONFIG_DIALOG_FOR_ACTIVITIES_LABEL,
				comp);
		addField(popupConfigDialogForTasks);
		popupConfigDialog.addField(popupConfigDialogForTasks);

		TristateCheckboxFieldEditor popupConfigDialogForGateways = new TristateCheckboxFieldEditor(
				Bpmn2Preferences.PREF_POPUP_CONFIG_DIALOG_FOR_GATEWAYS,
				Bpmn2Preferences.PREF_POPUP_CONFIG_DIALOG_FOR_GATEWAYS_LABEL,
				comp);
		addField(popupConfigDialogForGateways);
		popupConfigDialog.addField(popupConfigDialogForGateways);

		TristateCheckboxFieldEditor popupConfigDialogForEvents = new TristateCheckboxFieldEditor(
				Bpmn2Preferences.PREF_POPUP_CONFIG_DIALOG_FOR_EVENTS,
				Bpmn2Preferences.PREF_POPUP_CONFIG_DIALOG_FOR_EVENTS_LABEL,
				comp);
		addField(popupConfigDialogForEvents);
		popupConfigDialog.addField(popupConfigDialogForEvents);

		TristateCheckboxFieldEditor popupConfigDialogForEventDefs = new TristateCheckboxFieldEditor(
				Bpmn2Preferences.PREF_POPUP_CONFIG_DIALOG_FOR_EVENT_DEFS,
				Bpmn2Preferences.PREF_POPUP_CONFIG_DIALOG_FOR_EVENT_DEFS_LABEL,
				comp);
		addField(popupConfigDialogForEventDefs);
		popupConfigDialog.addField(popupConfigDialogForEventDefs);

		TristateCheckboxFieldEditor popupConfigDialogForDataDefs = new TristateCheckboxFieldEditor(
				Bpmn2Preferences.PREF_POPUP_CONFIG_DIALOG_FOR_DATA_DEFS,
				Bpmn2Preferences.PREF_POPUP_CONFIG_DIALOG_FOR_DATA_DEFS_LABEL,
				comp);
		addField(popupConfigDialogForDataDefs);
		popupConfigDialog.addField(popupConfigDialogForDataDefs);

		TristateCheckboxFieldEditor popupConfigDialogForContainers = new TristateCheckboxFieldEditor(
				Bpmn2Preferences.PREF_POPUP_CONFIG_DIALOG_FOR_CONTAINERS,
				Bpmn2Preferences.PREF_POPUP_CONFIG_DIALOG_FOR_CONTAINERS_LABEL,
				comp);
		addField(popupConfigDialogForContainers);
		popupConfigDialog.addField(popupConfigDialogForContainers);
		
		
		popupConfigDialog.updateCheckState();

		//////////////////////////////////////////////////////////////////////////////
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	@Override
	protected void performDefaults() {
		preferences.setToDefault(Bpmn2Preferences.PREF_SHOW_ADVANCED_PROPERTIES);
		preferences.setToDefault(Bpmn2Preferences.PREF_SHOW_DESCRIPTIONS);
		preferences.setToDefault(Bpmn2Preferences.PREF_SHOW_ID_ATTRIBUTE);
		preferences.setToDefault(Bpmn2Preferences.PREF_CHECK_PROJECT_NATURE);
		preferences.setToDefault(Bpmn2Preferences.PREF_SIMPLIFY_LISTS);

		preferences.setToDefault(Bpmn2Preferences.PREF_POPUP_CONFIG_DIALOG);
		preferences.setToDefault(Bpmn2Preferences.PREF_POPUP_CONFIG_DIALOG_FOR_ACTIVITIES);
		preferences.setToDefault(Bpmn2Preferences.PREF_POPUP_CONFIG_DIALOG_FOR_GATEWAYS);
		preferences.setToDefault(Bpmn2Preferences.PREF_POPUP_CONFIG_DIALOG_FOR_EVENTS);
		preferences.setToDefault(Bpmn2Preferences.PREF_POPUP_CONFIG_DIALOG_FOR_EVENT_DEFS);
		preferences.setToDefault(Bpmn2Preferences.PREF_POPUP_CONFIG_DIALOG_FOR_DATA_DEFS);
		preferences.setToDefault(Bpmn2Preferences.PREF_POPUP_CONFIG_DIALOG_FOR_CONTAINERS);
		preferences.setToDefault(Bpmn2Preferences.PREF_DO_CORE_VALIDATION);
		super.performDefaults();
	}
	
	public class SettableBooleanFieldEditor extends BooleanFieldEditor {
		public SettableBooleanFieldEditor(String prefShowIdAttribute, String prefShowIdAttributeLabel,
				Composite fieldEditorParent) {
			super(prefShowIdAttribute, prefShowIdAttributeLabel, fieldEditorParent);
		}

		public void setSelection(boolean enable) {
			Button btn = getChangeControl(getFieldEditorParent());
			btn.setSelection(enable);
		}
	}
	
	/**
	 * The "Show ID Attribute" preference is shared with the Tool Profiles page.
	 * 
	 * @return
	 */
	public boolean getShowIdAttribute() {
		if (btnShowIds!=null)
			return btnShowIds.getBooleanValue();
		return preferences.getShowIdAttribute();
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible && btnShowIds!=null) {
			// copy the "Show ID Attribute" setting from the Behavior page if it is active
			ToolProfilesPreferencePage page = (ToolProfilesPreferencePage) Bpmn2HomePreferencePage.getPage(getContainer(), ToolProfilesPreferencePage.PAGE_ID);
			if (page!=null)
				btnShowIds.setSelection(page.getShowIdAttribute());
		}
	}
}
