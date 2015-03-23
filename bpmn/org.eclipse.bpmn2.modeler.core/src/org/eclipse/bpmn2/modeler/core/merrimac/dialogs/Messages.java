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
package org.eclipse.bpmn2.modeler.core.merrimac.dialogs;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.bpmn2.modeler.core.merrimac.dialogs.messages"; //$NON-NLS-1$
	public static String AbstractObjectEditingDialog_Commit_Error;
	public static String AbstractObjectEditingDialog_Commit_Error_Title;
	public static String FeatureEditingDialog_Create;
	public static String FeatureEditingDialog_Edit;
	public static String FeatureListObjectEditor_Title;
	public static String ModelSubclassSelectionDialog_Title;
	public static String ObjectEditingDialog_Create;
	public static String ObjectEditingDialog_Edit;
	public static String ObjectEditor_No_Description;
	public static String ObjectEditor_Set_Error_Message;
	public static String ReadonlyTextObjectEditor_Invalid_Feature;
	public static String ReadonlyTextObjectEditor_Title;
	public static String RefListEditingDialog_Add;
	public static String RefListEditingDialog_Add_All;
	public static String RefListEditingDialog_Move_Down;
	public static String RefListEditingDialog_Move_Up;
	public static String RefListEditingDialog_Remove;
	public static String RefListEditingDialog_Remove_All;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
