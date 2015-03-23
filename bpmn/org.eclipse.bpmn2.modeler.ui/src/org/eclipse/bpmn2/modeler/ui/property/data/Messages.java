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
package org.eclipse.bpmn2.modeler.ui.property.data;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.bpmn2.modeler.ui.property.data.messages"; //$NON-NLS-1$
	public static String DataAssignmentDetailComposite_From_Title;
	public static String DataAssignmentDetailComposite_To_Title;
	public static String InterfacePropertySection_Interfaces_Message;
	public static String InterfacePropertySection_Interfaces_Title;
	public static String InterfacePropertySection_Implementation_Header;
	public static String InterfacePropertySection_No_Interfaces_Error_Message;
	public static String InterfacePropertySection_No_Interfaces_Error_Title;
	public static String InterfacePropertySection_Participant_Title;
	public static String InterfacePropertySection_Process_Title;
	public static String ResourceAssignmentExpressionDetailComposite_Title;
	public static String ResourceParameterBindingDetailComposite_Expression_Label;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
