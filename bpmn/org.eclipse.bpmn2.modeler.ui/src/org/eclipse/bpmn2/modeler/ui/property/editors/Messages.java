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
package org.eclipse.bpmn2.modeler.ui.property.editors;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.bpmn2.modeler.ui.property.editors.messages"; //$NON-NLS-1$
	public static String ItemDefinitionStructureEditor_DataStructureEmpty_Error;
	public static String ItemDefinitionStructureEditor_DataStructureInvalid_Error;
	public static String ItemDefinitionStructureEditor_DuplicateItemDefinition_Error;
	public static String ItemDefinitionStructureEditor_EditDataStructure_Prompt;
	public static String ItemDefinitionStructureEditor_EditDataStructure_Title;
	public static String SchemaObjectEditor_0;
	public static String SchemaObjectEditor_Browse_Button;
	public static String SchemaObjectEditor_Invalid_Selection_Message;
	public static String SchemaObjectEditor_Invalid_Selection_Title;
	public static String SchemaObjectEditor_WSDL_Fault;
	public static String SchemaObjectEditor_WSDL_Input;
	public static String SchemaObjectEditor_WSDL_Message;
	public static String SchemaObjectEditor_WSDL_Message_Part;
	public static String SchemaObjectEditor_WSDL_Operation;
	public static String SchemaObjectEditor_WSDL_Output;
	public static String SchemaObjectEditor_WSDL_Port;
	public static String SchemaObjectEditor_XML_Attribute;
	public static String ServiceImplementationObjectEditor_Create_New_Title;
	public static String ServiceImplementationObjectEditor_Dialog_Cancelled;
	public static String ServiceImplementationObjectEditor_Edit_Title;
	public static String ServiceImplementationObjectEditor_Implementation_URI_Label;
	public static String ServiceImplementationObjectEditor_Implementation_Name_Label;
	public static String ServiceImplementationObjectEditor_Invalid_Duplicate;
	public static String ServiceImplementationObjectEditor_Invalid_Empty;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
