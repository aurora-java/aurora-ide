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
package org.eclipse.bpmn2.modeler.ui.features.event.definitions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.bpmn2.modeler.ui.features.event.definitions.messages"; //$NON-NLS-1$
	public static String CancelEventDefinitionContainer_Description;
	public static String CancelEventDefinitionContainer_Name;
	public static String CompensateEventDefinitionContainer_Description;
	public static String CompensateEventDefinitionContainer_Name;
	public static String ConditionalEventDefinitionContainer_Description;
	public static String ConditionalEventDefinitionContainer_Name;
	public static String ErrorEventDefinitionContainer_Description;
	public static String ErrorEventDefinitionContainer_Name;
	public static String EscalationEventDefinitionContainer_Description;
	public static String EscalationEventDefinitionContainer_Name;
	public static String LinkEventDefinitionContainer_Description;
	public static String LinkEventDefinitionContainer_Name;
	public static String MessageEventDefinitionContainer_Description;
	public static String MessageEventDefinitionContainer_Name;
	public static String SignalEventDefinitionContainer_Description;
	public static String SignalEventDefinitionContainer_Name;
	public static String TerminateEventDefinitionFeatureContainer_Description;
	public static String TerminateEventDefinitionFeatureContainer_Name;
	public static String TimerEventDefinitionContainer_Description;
	public static String TimerEventDefinitionContainer_Name;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
