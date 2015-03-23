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
 * @author Innar Made
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.wizards;

import java.io.File;

import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.utils.ErrorUtils;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil.Bpmn2DiagramType;
import org.eclipse.bpmn2.modeler.ui.Activator;
import org.eclipse.bpmn2.modeler.ui.Bpmn2DiagramEditorInput;
import org.eclipse.bpmn2.modeler.ui.editor.BPMN2Editor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class BPMN2DiagramCreator {

	public static Bpmn2DiagramEditorInput createDiagram(URI uri, Bpmn2DiagramType diagramType, String targetNamespace) throws CoreException {
		return createDiagram(null, uri, diagramType, targetNamespace, null);
	}

	public static Bpmn2DiagramEditorInput createDiagram(IEditorInput oldInput, URI modelUri, Bpmn2DiagramType diagramType, String targetNamespace, BPMN2Editor diagramEditor) {

		// Should we create a new Graphiti Diamgra file or reuse the one
		// from an already open editor window?
		boolean createNew = true;
		URI diagramUri = null;
		BPMN2Editor otherEditor = BPMN2Editor.findOpenEditor(diagramEditor, oldInput);
		
		String modelName = modelUri.trimFragment().trimFileExtension().lastSegment();
		// We still need to create a Diagram object for this editor
		final Diagram diagram = DIUtils.createDiagram(modelName); //$NON-NLS-1$

		if (otherEditor!=null) {
			// reuse the temp Diagram File from other editor
			diagramUri = otherEditor.getDiagramUri();
			createNew = false;
		}
		else {
			// delete old temp file if necessary
			if (oldInput instanceof Bpmn2DiagramEditorInput) {
				URI oldUri = ((Bpmn2DiagramEditorInput)oldInput).getUri();
				if(oldUri.toFileString()!=null){
					final File oldTempFile = new File(oldUri.toFileString());
					if (oldTempFile!=null && oldTempFile.exists()) {
						// If two or more editor windows are open on the same file
						// when the workbench is first starting up, then deleting
						// the old temp file before all copies of the editor are
						// initialized can cause problems.
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								try {
									oldTempFile.delete();
								} catch (Exception e) {
								}
							}
						});
					}
				}
			}
			String diagramName = FileService.createTempName(modelName);
			diagramUri = URI.createFileURI(diagramName);
			FileService.createEmfFileForDiagram(diagramUri, diagram, diagramEditor);
		}

		String providerId = GraphitiUi.getExtensionManager().getDiagramTypeProviderId(diagram.getDiagramTypeId());
		
		// No need to create a new one if old input is already a Bpmn2DiagramEditorInput,
		// just update it
		Bpmn2DiagramEditorInput newInput;
		if (oldInput instanceof Bpmn2DiagramEditorInput) {
			newInput = (Bpmn2DiagramEditorInput)oldInput;
			newInput.updateUri(diagramUri);
		}
		else if (createNew) {
			newInput = new Bpmn2DiagramEditorInput(modelUri, diagramUri, providerId);
		}
		else {
			newInput = (Bpmn2DiagramEditorInput) otherEditor.getEditorInput();
		}
		
		newInput.setInitialDiagramType(diagramType);
		newInput.setTargetNamespace(targetNamespace);

		if (diagramEditor==null) {
			openEditor(newInput);
		}

		return newInput;
	}

	public static IEditorPart openEditor(final DiagramEditorInput editorInput) {
		final Object result[] = { new Object() };
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				try {
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IEditorPart part = null;
					part = page.findEditor(editorInput);
					if (part!=null) {
						page.activate(part);
					}
					else {
						part = page.openEditor(editorInput, BPMN2Editor.EDITOR_ID);
					}
					result[0] = part;
				} catch (PartInitException e) {
					String error = Messages.BPMN2DiagramCreator_Create_Error;
					IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, error, e);
					ErrorUtils.showErrorWithLogging(status);
				}
			}
		});
		return (IEditorPart)result[0];
	}
}
