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
package org.eclipse.bpmn2.modeler.core.validation;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.bpmn2.modeler.core.validation.messages"; //$NON-NLS-1$
	public static String BPMN2ProjectValidator_No_Project_Nature;
	public static String BPMN2ProjectValidator_Dont_Ask_Again;
	public static String BPMN2ProjectValidator_Invalid_File;
	public static String BPMN2ProjectValidator_Title;
	public static String BPMN2ValidationConstraints_0;
	public static String BPMN2ValidationConstraints_2;
	
	public static String BPMN2ValidationConstraints_10;
	public static String BPMN2ValidationConstraints_11;
	public static String BPMN2ValidationConstraints_12;
	public static String BPMN2ValidationConstraints_13;
	public static String BPMN2ValidationConstraints_14;
	public static String BPMN2ValidationConstraints_15;
	public static String BPMN2ValidationConstraints_16;
	
	public static String BPMN2ValidationConstraints_29;
	public static String BPMN2ValidationConstraints_Missing_Connection;
	public static String BPMN2ValidationConstraints_30;
	public static String BPMN2ValidationConstraints_31;
	public static String BPMN2ValidationConstraints_32;
	public static String BPMN2ValidationConstraints_33;
	public static String BPMN2ValidationConstraints_45;
	public static String BPMN2ValidationConstraints_46;
	public static String BPMN2ValidationConstraints_48;
	public static String BPMN2ValidationConstraints_49;
	public static String BPMN2ValidationConstraints_Missing_Feature;
	public static String BPMN2ValidationConstraints_52;
	public static String BPMN2ValidationConstraints_6;
	public static String BPMN2ValidationConstraints_7;
	public static String BPMN2ValidationConstraints_Duplicate_Data_Type;
	public static String BPMN2ValidationConstraints_Duplicate_Input;
	public static String BPMN2ValidationConstraints_Duplicate_Output;
	public static String LiveValidationListener_Title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
