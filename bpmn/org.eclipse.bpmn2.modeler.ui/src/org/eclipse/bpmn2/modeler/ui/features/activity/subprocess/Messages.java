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
package org.eclipse.bpmn2.modeler.ui.features.activity.subprocess;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.bpmn2.modeler.ui.features.activity.subprocess.messages"; //$NON-NLS-1$
	public static String AdHocSubProcessFeatureContainer_Description;
	public static String AdHocSubProcessFeatureContainer_Name;
	public static String CallActivityFeatureContainer_Description;
	public static String CallActivityFeatureContainer_Name;
	public static String ExpandFlowNodeFeature_Description;
	public static String ExpandFlowNodeFeature_Name;
	public static String PullupFeature_Description;
	public static String PullupFeature_Description_1;
	public static String PullupFeature_Name;
	public static String PushdownFeature_Description;
	public static String PushdownFeature_Description_1;
	public static String PushdownFeature_Name;
	public static String SubProcessFeatureContainer_Description;
	public static String SubProcessFeatureContainer_Name;
	public static String TransactionFeatureContainer_Description;
	public static String TransactionFeatureContainer_Name;
	public static String UpdateExpandableActivityFeature_Expand_Changed;
	public static String UpdateExpandableActivityFeature_No_DI_Element;
	public static String UpdateExpandableActivityFeature_Trigger_Changed;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
