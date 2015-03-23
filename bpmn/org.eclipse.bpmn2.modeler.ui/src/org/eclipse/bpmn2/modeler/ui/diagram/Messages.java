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
package org.eclipse.bpmn2.modeler.ui.diagram;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.bpmn2.modeler.ui.diagram.messages"; //$NON-NLS-1$
	public static String BPMNToolBehaviorProvider_Click_Drag_Prompt;
	public static String BPMNToolBehaviorProvider_Click_Drag_Prompt_Last_Separator;
	public static String BPMNToolBehaviorProvider_Click_Drag_Prompt_Separator;
	public static String BPMNToolBehaviorProvider_Connectors_Drawer_Label;
	public static String BPMNToolBehaviorProvider_Custom_Tasks_Drawer_Label;
	public static String BPMNToolBehaviorProvider_Data_Items_Drawer_Label;
	public static String BPMNToolBehaviorProvider_Event_Definitions_Drawer_Label;
	public static String BPMNToolBehaviorProvider_Events_Drawer_Label;
	public static String BPMNToolBehaviorProvider_Gateways_Drawer_Label;
	public static String BPMNToolBehaviorProvider_Artifact_Drawer_Label;
	public static String BPMNToolBehaviorProvider_SubProcess_Drawer_Label;
	public static String BPMNToolBehaviorProvider_GlobalTasks_Drawer_Label;
	public static String BPMNToolBehaviorProvider_Choreography_Drawer_Label;
	public static String BPMNToolBehaviorProvider_Conversation_Drawer_Label;
	public static String BPMNToolBehaviorProvider_SwimLanes_Drawer_Label;
	public static String BPMNToolBehaviorProvider_Profiles_Drawer_Label;
	public static String BPMNToolBehaviorProvider_Tasks_Drawer_Label;
	public static String BPMNToolBehaviorProvider_Unnamed_Profile;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
