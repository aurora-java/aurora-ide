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
package org.eclipse.bpmn2.modeler.ui.commands;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.bpmn2.modeler.ui.commands.messages"; //$NON-NLS-1$
	public static String CreateDiagramCommand_Title;
	public static String CreateDiagramCommand_Message;
	public static String CreateDiagramCommand_Choreography;
	public static String CreateDiagramCommand_Collaboration;
	public static String CreateDiagramCommand_Invalid_Duplicate;
	public static String CreateDiagramCommand_Invalid_Empty;
	public static String CreateDiagramCommand_Process;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
