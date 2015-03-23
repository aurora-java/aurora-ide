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
package org.eclipse.bpmn2.modeler.ui.features.data;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.bpmn2.modeler.ui.features.data.messages"; //$NON-NLS-1$
	public static String DataInputFeatureContainer_Name;
	public static String DataInputFeatureContainer_Description;
	public static String DataObjectFeatureContainer_Description;
	public static String DataObjectFeatureContainer_Name;
	public static String DataObjectFeatureContainer_New;
	public static String DataObjectFeatureContainer_Ref;
	public static String DataObjectFeatureContainer_Default_Name;
	public static String DataObjectReferenceFeatureContainer_Name;
	public static String DataObjectReferenceFeatureContainer_Description;
	public static String DataOutputFeatureContainer_Name;
	public static String DataOutputFeatureContainer_Description;
	public static String DataStoreReferenceFeatureContainer_Name;
	public static String DataStoreReferenceFeatureContainer_Description;
	public static String DataStoreReferenceFeatureContainer_New;
	public static String DataStoreReferenceFeatureContainer_Ref;
	public static String DataStoreReferenceFeatureContainer_Default_Name;
	public static String MessageFeatureContainer_Name;
	public static String MessageFeatureContainer_Description;
	public static String MessageFeatureContainer_New;
	public static String MessageFeatureContainer_Default_Name;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
