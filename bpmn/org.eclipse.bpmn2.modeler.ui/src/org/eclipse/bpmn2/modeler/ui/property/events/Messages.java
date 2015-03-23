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
package org.eclipse.bpmn2.modeler.ui.property.events;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.bpmn2.modeler.ui.property.events.messages"; //$NON-NLS-1$
	public static String CommonEventDetailComposite_Attached_To_Label;
	public static String CommonEventDetailComposite_Event_Definition_Title;
	public static String CommonEventDetailComposite_Input_Data_Title;
	public static String CommonEventDetailComposite_Output_Data_Title;
	public static String EventDefinitionsListComposite_Event_ID_Header;
	public static String EventDefinitionsListComposite_Event_Type_Header;
	public static String EventDefinitionsListComposite_Map_Incoming;
	public static String EventDefinitionsListComposite_Map_Outgoing;
	public static String EventDefinitionsListComposite_No_Transaction;
	public static String TimerEventDefinitionDetailComposite_Duration;
	public static String TimerEventDefinitionDetailComposite_Interval;
	public static String TimerEventDefinitionDetailComposite_Time_Date;
	public static String TimerEventDefinitionDetailComposite_Type;
	public static String TimerEventDefinitionDetailComposite_Value;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
