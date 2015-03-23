/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013 Red Hat, Inc.
 * All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.ui;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.graphiti.ui.editor.DiagramEditorInputFactory;
import org.eclipse.ui.IMemento;

public class Bpmn2DiagramEditorInputFactory extends DiagramEditorInputFactory {

	public IAdaptable createElement(IMemento memento) {
		// get diagram URI
		final String diagramUriString = memento.getString(DiagramEditorInput.KEY_URI);
		if (diagramUriString == null) {
			return null;
		}
		final String modelUriString = memento.getString(Bpmn2DiagramEditorInput.KEY_MODEL_URI);
		if (modelUriString == null) {
			return null;
		}
		
		// get diagram type provider id
		final String providerID = memento.getString(DiagramEditorInput.KEY_PROVIDER_ID);
		URI modelUri = URI.createURI(modelUriString);
		URI diagramUri = URI.createURI(diagramUriString);
		return new Bpmn2DiagramEditorInput(modelUri, diagramUri, providerID);
	}
}
