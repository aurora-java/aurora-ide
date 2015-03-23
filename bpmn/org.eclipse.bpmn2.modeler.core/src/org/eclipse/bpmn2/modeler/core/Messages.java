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
package org.eclipse.bpmn2.modeler.core;

import org.eclipse.osgi.util.NLS;

/**
 * Contains externalized String constants.
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.bpmn2.modeler.core.messages"; //$NON-NLS-1$
	public static String LifecycleEvent_Event_Prefix;
	public static String ModelHandler_20;
	public static String ModelHandler_21;
	public static String ModelHandler_Choreography;
	public static String ModelHandler_Choreography_Task;
	public static String ModelHandler_Collaboration;
	public static String ModelHandler_Collaboration_Diagram;
	public static String ModelHandler_Default;
	public static String ModelHandler_Initiating_Participant;
	public static String ModelHandler_Initiating_Pool;
	public static String ModelHandler_Initiating_Process;
	public static String ModelHandler_Lane_Set;
	public static String ModelHandler_Non_Initiating_Participant;
	public static String ModelHandler_Non_Initiating_Pool;
	public static String ModelHandler_Non_Initiating_Process;
	public static String ModelHandler_Process;
	public static String ModelHandler_Process_Diagram;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
