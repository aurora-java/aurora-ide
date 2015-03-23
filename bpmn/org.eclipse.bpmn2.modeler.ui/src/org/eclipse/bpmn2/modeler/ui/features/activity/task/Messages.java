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
package org.eclipse.bpmn2.modeler.ui.features.activity.task;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.bpmn2.modeler.ui.features.activity.task.messages"; //$NON-NLS-1$
	public static String BusinessRuleTaskFeatureContainer_Description;
	public static String BusinessRuleTaskFeatureContainer_Name;
	public static String ManualTaskFeatureContainer_Description;
	public static String ManualTaskFeatureContainer_Name;
	public static String ReceiveTaskFeatureContainer_Description;
	public static String ReceiveTaskFeatureContainer_Name;
	public static String ScriptTaskFeatureContainer_Description;
	public static String ScriptTaskFeatureContainer_Name;
	public static String SendTaskFeatureContainer_Description;
	public static String SendTaskFeatureContainer_Name;
	public static String ServiceTaskFeatureContainer_Description;
	public static String ServiceTaskFeatureContainer_Name;
	public static String TaskFeatureContainer_Description;
	public static String TaskFeatureContainer_Name;
	public static String UserTaskFeatureContainer_Description;
	public static String UserTaskFeatureContainer_Name;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
