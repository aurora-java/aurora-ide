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
package org.eclipse.bpmn2.modeler.ui.editor;

import org.eclipse.bpmn2.modeler.ui.Bpmn2DiagramEditorInput;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPart;

public class BPMN2EditorMatchingStrategy implements IEditorMatchingStrategy {

	public BPMN2EditorMatchingStrategy() {
	}

	@Override
	public boolean matches(IEditorReference editorRef, IEditorInput input) {
		IWorkbenchPart part = editorRef.getPart(false);
		if (part instanceof BPMN2MultiPageEditor) {
			BPMN2Editor editor = ((BPMN2MultiPageEditor) part).getDesignEditor();
			URI editorUri = editor.getModelUri();
			if (input instanceof Bpmn2DiagramEditorInput) {
				URI inputUri = ((Bpmn2DiagramEditorInput) input).getModelUri();
				if (inputUri!=null && inputUri.equals(editorUri)) {
					return true;
				}
			}
			else if (input instanceof URIEditorInput) {
				final URIEditorInput uriInput = (URIEditorInput) input;
				URI inputUri = uriInput.getURI();
				if (inputUri!=null && inputUri.equals(editorUri)) {
					return true;
				}
			}
			else {
				IFile inputFile = getFile(input);
				if (inputFile != null) {
					// check whether the given input comes with a file which is
					// already opened in the diagram editor.
					IFile editorFile = getFile(editor.getEditorInput());
					if (inputFile.equals(editorFile)) {
						return true;
					}
				}
			}

		}
		return false;
	}

	private IFile getFile(Object input) {
		IFile file = null;
		if (input instanceof IAdaptable) {
			file = (IFile) ((IAdaptable) input).getAdapter(IFile.class);
		}
		return file;
	}
}
