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
package org.eclipse.bpmn2.modeler.ui.adapters.properties;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.bpmn2.modeler.ui.adapters.properties.messages"; //$NON-NLS-1$
	public static String ActivityPropertiesAdapter_CompletionCondition_Label;
	public static String CallActivityPropertiesAdapter_Called_Activity;
	public static String DataAssociationPropertiesAdapter_Source;
	public static String DataAssociationPropertiesAdapter_Target;
	public static String ErrorPropertiesAdapter_Error_Code;
	public static String ErrorPropertiesAdapter_ID;
	public static String EscalationPropertiesAdapter_Escalation_Code;
	public static String EscalationPropertiesAdapter_ID;
	public static String FormalExpressionPropertiesAdapter_Constraint;
	public static String FormalExpressionPropertiesAdapter_Script;
	public static String FormalExpressionPropertiesAdapter_Script_Language;
	public static String GroupPropertiesAdapter_CreateCategory_Prompt;
	public static String GroupPropertiesAdapter_CreateCategory_Title;
	public static String ImportPropertiesAdapter_Import;
	public static String ItemAwareElementPropertiesAdapter_ID;
	public static String ItemDefinitionPropertiesAdapter_ItemDefinition_Label;
	public static String ItemDefinitionPropertiesAdapter_Structure;
	public static String OperationPropertiesAdapter_Title;
	public static String ParticipantPropertiesAdapter_Multiplicity;
	public static String PropertyPropertiesAdapter_EventVar_Prefix;
	public static String PropertyPropertiesAdapter_LocalVar_Prefix;
	public static String PropertyPropertiesAdapter_ProcessVar_Prefix;
	public static String PropertyPropertiesAdapter_TaskVar_Prefix;
	public static String PropertyPropertiesAdapter_Property_Label;
	public static String SignalPropertiesAdapter_ID;
	public static String Interface_Operations_Label;
	public static String Interface_Implementation_Label;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
