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
package org.eclipse.bpmn2.modeler.ui.features.choreography;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.bpmn2.modeler.ui.features.choreography.messages"; //$NON-NLS-1$
	public static String AddChoreographyMessageFeature_Description;
	public static String AddChoreographyMessageFeature_Name;
	public static String AddChoreographyParticipantFeature_Description;
	public static String AddChoreographyParticipantFeature_Name;
	public static String AddChoreographyParticipantFeature_New_Participant;
	public static String BlackboxFeature_Description;
	public static String BlackboxFeature_Name;
	public static String CallChoreographyFeatureContainer_Description;
	public static String CallChoreographyFeatureContainer_Name;
	public static String ChoreographyTaskFeatureContainer_Description;
	public static String ChoreographyTaskFeatureContainer_Name;
	public static String RemoveChoreographyMessageFeature_Description;
	public static String RemoveChoreographyMessageFeature_Name;
	public static String ShowDiagramPageFeature_Description;
	public static String ShowDiagramPageFeature_Name;
	public static String SubChoreographyFeatureContainer_Description;
	public static String SubChoreographyFeatureContainer_Name;
	public static String WhiteboxFeature_Description;
	public static String WhiteboxFeature_Name;
	public static String WhiteboxFeature_New_Process;
	public static String WhiteboxFeature_Process_For;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
