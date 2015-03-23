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
package org.eclipse.bpmn2.modeler.ui.property.providers;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.bpmn2.modeler.ui.property.providers.messages"; //$NON-NLS-1$
	public static String BPMN2ErrorTreeNode_Unnamed;
	public static String BPMN2InterfaceTreeNode_Unnamed;
	public static String BPMN2MessageTreeNode_Unnamed;
	public static String BPMN2OperationTreeNode_Unnamed;
	public static String BPMN2ProcessTreeNode_Unnamed;
	public static String ModelLabelProvider_Unknown;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
