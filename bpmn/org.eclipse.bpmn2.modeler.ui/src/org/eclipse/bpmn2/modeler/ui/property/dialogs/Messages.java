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
package org.eclipse.bpmn2.modeler.ui.property.dialogs;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.bpmn2.modeler.ui.property.dialogs.messages"; //$NON-NLS-1$
	public static String NamespacesEditingDialog_Invalid_Duplicate_Prefix;
	public static String NamespacesEditingDialog_Invalid_Empty_Namespace;
	public static String NamespacesEditingDialog_Invalid_Empty_Prefix;
	public static String NamespacesEditingDialog_Invalid_Namespace_URI;
	public static String NamespacesEditingDialog_Namespace;
	public static String NamespacesEditingDialog_Prefix;
	public static String SchemaSelectionDialog_Add_Import;
	public static String SchemaSelectionDialog_Cannot_Load;
	public static String SchemaSelectionDialog_Imports;
	public static String SchemaSelectionDialog_Loaded;
	public static String SchemaSelectionDialog_Structure;
	public static String SchemaSelectionDialog_TItle;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
