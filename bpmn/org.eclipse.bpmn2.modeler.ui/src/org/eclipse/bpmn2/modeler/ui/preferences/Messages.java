/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013, 2014 Red Hat, Inc.
 * All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.bpmn2.modeler.ui.preferences.messages"; //$NON-NLS-1$
	public static String Bpmn2EditorPreferencePage_Description;
	public static String ToolProfilePreferencePage_Default_Profile_Label;
	public static String ToolProfilePreferencePage_Export;
	public static String ToolProfilePreferencePage_Extension_Elements_Label;
	public static String ToolProfilePreferencePage_Import;
	public static String ToolProfilePreferencePage_Standard_Elements_Label;
	public static String ToolProfilePreferencePage_Unnamed;
	public static String ToolProfilesPreferencePage_AddDrawer_Button;
	public static String ToolProfilesPreferencePage_AddTool_Button;
	public static String ToolProfilesPreferencePage_CopyProfile_Button;
	public static String ToolProfilesPreferencePage_CreateProfile_Message;
	public static String ToolProfilesPreferencePage_CreateProfile_Title;
	public static String ToolProfilesPreferencePage_DeleteDrawer_Button;
	public static String ToolProfilesPreferencePage_DeleteProfile_Button;
	public static String ToolProfilesPreferencePage_DeleteProfile_Message;
	public static String ToolProfilesPreferencePage_DeleteProfile_Title;
	public static String ToolProfilesPreferencePage_DeleteTool_Button;
	public static String ToolProfilesPreferencePage_DiagramType_Label;
	public static String ToolProfilesPreferencePage_EditTool_Button;
	public static String ToolProfilesPreferencePage_EnabledElements_Tab;
	public static String ToolProfilesPreferencePage_NewProfile_Button;
	public static String ToolProfilesPreferencePage_Profile_Duplicate;
	public static String ToolProfilesPreferencePage_Profile_Empty;
	public static String ToolProfilesPreferencePage_SetDefaultProfile_Button;
	public static String ToolProfilesPreferencePage_ShowID_Button;
	public static String ToolProfilesPreferencePage_TargetRuntime_Label;
	public static String ToolProfilesPreferencePage_ToolPalette_Tab;
	public static String ToolProfilesPreferencePage_ToolProfile_Label;

	public static String ToolEnablementPreferences_BPMN_Extensions;
	public static String ToolEnablementPreferences_Target_Extensions;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
