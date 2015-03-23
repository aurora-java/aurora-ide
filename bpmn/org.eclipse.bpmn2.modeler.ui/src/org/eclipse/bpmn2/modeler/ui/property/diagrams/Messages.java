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
package org.eclipse.bpmn2.modeler.ui.property.diagrams;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.bpmn2.modeler.ui.property.diagrams.messages"; //$NON-NLS-1$
	public static String DefinitionsPropertyComposite_Change_Namespace_Title;
	public static String DefinitionsPropertyComposite_Create_Namespace_Title;
	public static String DefinitionsPropertyComposite_Invalid_Duplicate;
	public static String DefinitionsPropertyComposite_Namespace_Details_Title;
	public static String DefinitionsPropertyComposite_Namespace_Label;
	public static String DefinitionsPropertyComposite_Prefix_Label;
	public static String DefinitionsPropertyComposite_Prefix_Message;
	public static String ItemDefinitionDetailComposite_DefinedIn_Title;
	public static String ItemDefinitionDetailComposite_Import_Label;
	public static String ItemDefinitionDetailComposite_Namespace_Label;
	public static String ItemDefinitionDetailComposite_Type_Label;
	public static String ItemDefinitionDetailComposite_TypeLanguage_Label;
	public static String ParticipantDetailComposite_Maximum_Label;
	public static String ParticipantDetailComposite_Mimimum_Label;
	public static String ParticipantDetailComposite_MinMax_Error;
	public static String ParticipantDetailComposite_Multiplicity_Label;
	public static String ResourceRoleListComposite_Roles_Label;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
