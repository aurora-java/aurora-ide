/*******************************************************************************
 * Copyright (c) 2011, 2012 Red Hat, Inc.
 *  All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 *
 * @author Bob Brodt
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core;

import org.eclipse.bpmn2.modeler.core.utils.ModelUtil.Bpmn2DiagramType;
import org.eclipse.ui.IEditorInput;

/**
 * Interface that defines certain behavior of a Target Runtime specialization
 * that must be implemented by a contributing plug-in.
 */
public interface IBpmn2RuntimeExtension {

	/**
	 * Check if the given input file is specific to the Target Runtime. The
	 * implementation should check for specific extensions and namespaces that
	 * identify the file for this runtime.
	 * 
	 * @param input
	 *            the editor input object instance. This is created and passed
	 *            to the BPMN2 editor by the Eclipse framework.
	 * @return true if the file is targeted for this runtime, false if the file
	 *         is generic BPMN 2.0 or belongs to some other Target Runtime.
	 */
	public boolean isContentForRuntime(IEditorInput input);

	/**
	 * Return the target namespace defined by this Target Runtime for the given
	 * diagram type.
	 * 
	 * @param diagramType
	 *            one of the pre-defined Diagram Types. The Target Runtime may
	 *            use different namespace URIs for different Diagram Types.
	 * @return a targetNamespace URI
	 */
	public String getTargetNamespace(Bpmn2DiagramType diagramType);

	/**
	 * Used to notify the Target Runtime of BPMN2 editor lifecycle events.
	 *
	 * @param event
	 *            an event object sent by the BPMN2 editor framework.
	 */
	public void notify(LifecycleEvent event);
}
