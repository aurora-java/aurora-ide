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

package org.eclipse.bpmn2.modeler.ui.editor;

import org.eclipse.bpmn2.modeler.core.LifecycleEvent;
import org.eclipse.bpmn2.modeler.core.LifecycleEvent.EventType;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerResourceSetImpl;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.bpmn2.modeler.core.validation.ValidationStatusAdapterFactory;
import org.eclipse.core.commands.operations.DefaultOperationHistory;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.impl.TransactionalEditingDomainImpl;
import org.eclipse.emf.workspace.IWorkspaceCommandStack;
import org.eclipse.emf.workspace.WorkspaceEditingDomainFactory;
import org.eclipse.graphiti.platform.IDiagramContainer;
import org.eclipse.graphiti.ui.editor.DefaultUpdateBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.IDiagramContainerUI;
import org.eclipse.graphiti.ui.editor.IDiagramEditorInput;
import org.eclipse.graphiti.ui.internal.editor.DomainModelWorkspaceSynchronizerDelegate;
import org.eclipse.graphiti.ui.internal.editor.GFWorkspaceCommandStackImpl;
import org.eclipse.ui.IEditorInput;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer;

/**
 * This overrides the DefaultUpdateBehavior provider class from Graphiti. This
 * is necessary because we want to provide our own ResourceSet implementation
 * instead of being forced to deal with the default ResourceSetImpl. See
 * createResourceSetAndEditingDomain() for details.
 * 
 * @author Bob Brodt
 * 
 */
public class BPMN2EditorUpdateBehavior extends DefaultUpdateBehavior {

	private DiagramBehavior diagramBehavior;
	private TransactionalEditingDomain editingDomain;

	/**
	 * @param diagramEditor
	 */
	public BPMN2EditorUpdateBehavior(DiagramBehavior diagramBehavior) {
		super(diagramBehavior);
		this.diagramBehavior = diagramBehavior;
	}

	public TransactionalEditingDomain getEditingDomain() {
		if (editingDomain == null)
			createEditingDomain();
		return editingDomain;
	}

	@Override
	public void createEditingDomain() {
		if (editingDomain == null) {
			// If another editor window is already open for this BPMN2 file
			// then reuse its Editing Domain; otherwise create a new one.
			IDiagramContainerUI dc = diagramBehavior.getDiagramContainer();
			if (dc instanceof BPMN2Editor) {
				BPMN2Editor editor = (BPMN2Editor) dc;
				BPMN2Editor openEditor = BPMN2Editor.findOpenEditor(editor, editor.getEditorInput());
				if (openEditor!=null) {
					BPMN2EditorUpdateBehavior updateBehavior = (BPMN2EditorUpdateBehavior) openEditor.getDiagramBehavior().getUpdateBehavior();
					editingDomain = updateBehavior.editingDomain;
				}
			}
			if (editingDomain==null) {
				editingDomain = createResourceSetAndEditingDomain();
			}
			initializeEditingDomain(editingDomain);
		}
	}

	@Override
	public void dispose() {
		// We can't dispose of the Editing Domain if another editor window
		// is still open - Editing Domain is disposed in super()
		IDiagramContainerUI dc = diagramBehavior.getDiagramContainer();
		if (dc instanceof BPMN2Editor) {
			BPMN2Editor editor = (BPMN2Editor) dc;
			BPMN2Editor openEditor = BPMN2Editor.findOpenEditor(editor, editor.getEditorInput());
			if (openEditor==null)
				super.dispose();
		}
	}

	public void setEditingDomain(TransactionalEditingDomain editingDomain) {
		this.editingDomain = editingDomain;
		super.setEditingDomain(editingDomain);
	}
	
	public TransactionalEditingDomain createResourceSetAndEditingDomain() {
		// Argh!! This is the ONLY line of code that actually differs
		// (significantly) from
		// the Graphiti EMF Service. Here we want to substitute our own
		// Bpmn2ModelerResourceSetImpl instead of using a ResourceSetImpl.
		final ResourceSet resourceSet = new Bpmn2ModelerResourceSetImpl();
		final IWorkspaceCommandStack workspaceCommandStack = new GFWorkspaceCommandStackImpl(
				new DefaultOperationHistory());

		final TransactionalEditingDomainImpl editingDomain = new TransactionalEditingDomainImpl(
				new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE),
				workspaceCommandStack, resourceSet);
		WorkspaceEditingDomainFactory.INSTANCE.mapResourceSet(editingDomain);
		return editingDomain;
	}
	
	protected WorkspaceSynchronizer.Delegate createWorkspaceSynchronizerDelegate() {
		return new BPMN2EditorWorkspaceSynchronizerDelegate(diagramBehavior.getDiagramContainer());
	}
	
	public class BPMN2EditorWorkspaceSynchronizerDelegate implements WorkspaceSynchronizer.Delegate {

		private BPMN2Editor bpmnEditor;

		/**
		 * The DiagramEditorBehavior reacts on a setResourceChanged(true) if he gets
		 * activated.
		 */
		public BPMN2EditorWorkspaceSynchronizerDelegate(IDiagramContainer diagramEditor) {
			this.bpmnEditor = (BPMN2Editor)diagramEditor;
		}

		public void dispose() { 
			bpmnEditor = null;
		}

		public boolean handleResourceChanged(Resource resource) {
			return bpmnEditor.handleResourceChanged(resource);
		}

		public boolean handleResourceDeleted(Resource resource) {
			return bpmnEditor.handleResourceDeleted(resource);
		}

		public boolean handleResourceMoved(Resource resource, URI newURI) {
			return bpmnEditor.handleResourceMoved(resource, newURI);
		}

	}

	@Override
	public void historyNotification(OperationHistoryEvent event) {
		super.historyNotification(event);
		
		switch (event.getEventType()) {
		case OperationHistoryEvent.REDONE:
			TargetRuntime.getCurrentRuntime().notify(new LifecycleEvent(EventType.COMMAND_REDO, event.getOperation()));
			break;
		case OperationHistoryEvent.UNDONE:
			TargetRuntime.getCurrentRuntime().notify(new LifecycleEvent(EventType.COMMAND_UNDO, event.getOperation()));
			break;
		}
	}
}