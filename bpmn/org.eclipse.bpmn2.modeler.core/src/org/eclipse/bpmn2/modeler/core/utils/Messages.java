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
package org.eclipse.bpmn2.modeler.core.utils;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.bpmn2.modeler.core.utils.messages"; //$NON-NLS-1$
	public static String ErrorUtils_Title;
	public static String FixDuplicateIdsDialog_Duplicate_ID;
	public static String FixDuplicateIdsDialog_Message;
	public static String ModelUtil_Choreography_Diagram;
	public static String ModelUtil_Choreograpy_Diagram;
	public static String ModelUtil_Collaboration_Diagram;
	public static String ModelUtil_Illegal_EPackage_For_Attribute;
	public static String ModelUtil_Illegal_EPackage_For_Reference;
	public static String ModelUtil_Illegal_Value;
	public static String ModelUtil_Internal_Error;
	public static String ModelUtil_Process_Diagram;
	public static String ModelUtil_Unknown_Attribute_Data_Type;
	public static String ModelUtil_Unknown_Diagram_Type;
	public static String ModelUtil_Unknown_Object;
	public static String ModelUtil_Unknown_Reference_Object_Type;
	public static String ModelUtil_Unnamed_Object;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
