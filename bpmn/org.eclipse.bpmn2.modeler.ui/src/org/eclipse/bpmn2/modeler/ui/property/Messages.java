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
package org.eclipse.bpmn2.modeler.ui.property;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.bpmn2.modeler.ui.property.messages"; //$NON-NLS-1$
	public static String AdvancedDetailComposite_Add_Action;
	public static String AdvancedDetailComposite_Advanced_Button;
	public static String AdvancedDetailComposite_Details_Title;
	public static String AdvancedDetailComposite_Properties_Title;
	public static String AdvancedDetailComposite_Remove_Action;
	public static String DescriptionPropertySection_Appearance_Label;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
