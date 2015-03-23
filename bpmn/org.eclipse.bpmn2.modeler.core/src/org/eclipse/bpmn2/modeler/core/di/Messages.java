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
package org.eclipse.bpmn2.modeler.core.di;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.bpmn2.modeler.core.di.messages"; //$NON-NLS-1$
	public static String DiagramElementTree_Unknown_Element;
	public static String DIGenerator_No_Diagram;
	public static String DIImport_Dependency_not_found;
	public static String DIImport_No_Add_Feature;
	public static String DIImport_No_Create_Feature;
	public static String DIImport_No_Source;
	public static String DIImport_No_Source_or_Target;
	public static String DIImport_No_Target;
	public static String DIImport_Reference_not_found;
	public static String ImportDiagnostics_Message;
	public static String ImportDiagnostics_Title;
	public static String MissingDIElementsDialog_Message;
	public static String MissingDIElementsDialog_Title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
