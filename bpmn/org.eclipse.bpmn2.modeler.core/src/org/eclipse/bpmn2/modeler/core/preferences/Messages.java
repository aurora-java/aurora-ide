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
package org.eclipse.bpmn2.modeler.core.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.bpmn2.modeler.core.preferences.messages"; //$NON-NLS-1$
	
	public static String Bpmn2Preferences_No_Runtime_Plugin_Title;
	public static String Bpmn2Preferences_Activities;
	public static String Bpmn2Preferences_Always_False;
	public static String Bpmn2Preferences_Always_true;
	public static String Bpmn2Preferences_Check_Project_Nature;
	public static String Bpmn2Preferences_Use_Popup_Dialog_For_Lists;
	public static String Bpmn2Preferences_Config_Dialog;
	public static String Bpmn2Preferences_Containers;
	public static String Bpmn2Preferences_Data_Items;
	public static String Bpmn2Preferences_Do_Core_Validation;
	public static String Bpmn2Preferences_Propagate_Group_Categories;
	public static String Bpmn2Preferences_Allow_Mutliple_Connections;
	public static String Bpmn2Preferences_Event_Definitions;
	public static String Bpmn2Preferences_Events;
	public static String Bpmn2Preferences_Expand;
	public static String Bpmn2Preferences_False_if_not_set;
	public static String Bpmn2Preferences_Gateways;
	public static String Bpmn2Preferences_Horizontal;
	public static String Bpmn2Preferences_Marker_Visible;
	public static String Bpmn2Preferences_Message_Visible;
	public static String Bpmn2Preferences_Save_BPMNLabels;
	public static String Bpmn2Preferences_No_Runtime_Plugin_Message;
	public static String Bpmn2Preferences_None;
	public static String Bpmn2Preferences_Show_Advanced_Properties;
	public static String Bpmn2Preferences_Show_Descriptions;
	public static String Bpmn2Preferences_Show_ID_Attribute;
	public static String Bpmn2Preferences_Simplify_Lists;
	public static String Bpmn2Preferences_Target_Runtime;
	public static String Bpmn2Preferences_Timeout;
	public static String Bpmn2Preferences_True_if_not_set;
	public static String Bpmn2Preferences_Resolve_Externals;
	
	public static String ShapeStyle_Category_Connections;
	public static String ShapeStyle_Category_Shapes;
	public static String ShapeStyle_Category_Events;
	public static String ShapeStyle_Category_Gateways;
	public static String ShapeStyle_Category_Tasks;
	public static String ShapeStyle_Category_GlobalTasks;
	public static String ShapeStyle_Category_SubProcess;
	public static String ShapeStyle_Category_Choreography;
	public static String ShapeStyle_Category_Conversation;
	public static String ShapeStyle_Category_SwimLanes;
	public static String ShapeStyle_Category_Data;
	public static String ShapeStyle_Category_Other;
	public static String ShapeStyle_Category_Canvas;
	public static String ShapeStyle_Category_Grid;

	public static String ShapeStyle_RoutingStyle_Direct;
	public static String ShapeStyle_RoutingStyle_Automatic;
	public static String ShapeStyle_RoutingStyle_Manhattan;

	public static String ShapeStyle_LabelPosition_North;
	public static String ShapeStyle_LabelPosition_South;
	public static String ShapeStyle_LabelPosition_West;
	public static String ShapeStyle_LabelPosition_East;
	public static String ShapeStyle_LabelPosition_Top;
	public static String ShapeStyle_LabelPosition_Center;
	public static String ShapeStyle_LabelPosition_Bottom;
	public static String ShapeStyle_LabelPosition_Left;
	public static String ShapeStyle_LabelPosition_Right;
	public static String ShapeStyle_LabelPosition_Movable;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
