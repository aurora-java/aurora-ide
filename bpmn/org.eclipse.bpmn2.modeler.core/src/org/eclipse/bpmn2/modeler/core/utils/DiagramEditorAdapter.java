/*******************************************************************************
 * Copyright (c) 2011, 2012 Red Hat, Inc. 
 * All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 *
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *******************************************************************************/
package org.eclipse.bpmn2.modeler.core.utils;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.graphiti.ui.editor.DiagramEditor;

public class DiagramEditorAdapter implements Adapter {
	/**
	 * 
	 */
	private final DiagramEditor diagramEditor;
	
	/**
	 * @param diagramEditor
	 */
	public DiagramEditorAdapter(DiagramEditor bpmn2Editor) {
		this.diagramEditor = bpmn2Editor;
	}
	public Notifier getTarget() {
		return null;
	}
	public void setTarget(Notifier newTarget) {
	}
	public boolean isAdapterForType(Object type) {
		return type == DiagramEditorAdapter.class ||
				type == DiagramEditor.class;
	}
	public void notifyChanged(Notification notification) {
	}
	public DiagramEditor getDiagramEditor() {
		return this.diagramEditor;
	}
}