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
package org.eclipse.bpmn2.modeler.core.merrimac.clad;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.bpmn2.modeler.core.merrimac.clad.messages"; //$NON-NLS-1$
	public static String AbstractDetailComposite_Attributes;
	public static String AbstractDetailComposite_Empty_Property_Sheet;
	public static String AbstractDetailComposite_Empty_Property_Sheet_Elements;
	public static String AbstractListComposite_Add;
	public static String AbstractListComposite_Close;
	public static String AbstractListComposite_Delete;
	public static String AbstractListComposite_Details;
	public static String AbstractListComposite_Edit;
	public static String AbstractListComposite_List;
	public static String AbstractListComposite_Move_Down;
	public static String AbstractListComposite_Move_Up;
	public static String AbstractListComposite_Remove;
	public static String DefaultDetailComposite_Documentation;
	public static String DefaultDetailComposite_List_Title;
	public static String DefaultListComposite_Cannot_Delete_Message;
	public static String DefaultListComposite_Cannot_Delete_Title;
	public static String DefaultListComposite_Error_Internal_Error_Message_No_List;
	public static String DefaultListComposite_Internal_Error_Message_No_Editor;
	public static String DefaultListComposite_Internal_Error_Message_No_Factory;
	public static String DefaultListComposite_Internal_Error_Title;
	public static String PropertiesCompositeFactory_Internal_Error_Title;
	public static String PropertiesCompositeFactory_No_Property_Sheet;
	public static String PropertiesCompositeFactory_Unknown_Type;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
