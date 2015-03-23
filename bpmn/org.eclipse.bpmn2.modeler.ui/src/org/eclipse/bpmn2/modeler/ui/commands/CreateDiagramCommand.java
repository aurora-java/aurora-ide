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
package org.eclipse.bpmn2.modeler.ui.commands;

import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.Collaboration;
import org.eclipse.bpmn2.Choreography;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BpmnDiFactory;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.ui.editor.BPMN2Editor;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

public class CreateDiagramCommand extends AbstractHandler {

	public final static String ID_CREATE_PROCESS = "org.eclipse.bpmn2.modeler.command.createProcess"; //$NON-NLS-1$
	public final static String ID_CREATE_CHOREOGRAPHY = "org.eclipse.bpmn2.modeler.command.createChoreography"; //$NON-NLS-1$
	public final static String ID_CREATE_COLLABORATION = "org.eclipse.bpmn2.modeler.command.createCollaboration"; //$NON-NLS-1$

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEvaluationContext ctx = (IEvaluationContext)event.getApplicationContext();
		Object var = ctx.getDefaultVariable();
		BPMN2Editor editor = BPMN2Editor.getActiveEditor();
		if (var instanceof List) {
			for (Object e : (List)var) {
				if ( e instanceof EditPart) {
					break;
				}
			}
		}
		
		InputDialog dlg = null;
		final String id = event.getCommand().getId();
		if (ID_CREATE_PROCESS.equals(id)) {
			dlg = new NewDiagramNameDialog(editor, Messages.CreateDiagramCommand_Process);
		}
		else if (ID_CREATE_CHOREOGRAPHY.equals(id)) {
			dlg = new NewDiagramNameDialog(editor, Messages.CreateDiagramCommand_Choreography);
		}
		else if (ID_CREATE_COLLABORATION.equals(id)) {
			dlg = new NewDiagramNameDialog(editor, Messages.CreateDiagramCommand_Collaboration);
		}
		if (dlg!=null) {
			if (dlg.open()==Window.OK) {
				TransactionalEditingDomain domain = editor.getEditingDomain();
				final Definitions definitions = ModelUtil.getDefinitions(editor.getBpmnDiagram());
				final String name = dlg.getValue();
				domain.getCommandStack().execute(new RecordingCommand(domain) {
					@Override
					protected void doExecute() {
						Class clazz = null;
						if (ID_CREATE_PROCESS.equals(id)) {
							clazz = Process.class;
						}
						else if (ID_CREATE_CHOREOGRAPHY.equals(id)) {
							clazz = Choreography.class;
						}
						else if (ID_CREATE_COLLABORATION.equals(id)) {
							clazz = Collaboration.class;
						}
						RootElement bpmnElement = Bpmn2ModelerFactory.create(clazz);
						EStructuralFeature f = bpmnElement.eClass().getEStructuralFeature("name"); //$NON-NLS-1$
						bpmnElement.eSet(f, name);
						definitions.getRootElements().add(bpmnElement);

						BPMNPlane plane = BpmnDiFactory.eINSTANCE.createBPMNPlane();
						plane.setBpmnElement(bpmnElement);
						plane.getPlaneElement();
						BPMNDiagram diagram = BpmnDiFactory.eINSTANCE.createBPMNDiagram();
						diagram.setPlane(plane);
						diagram.setName(name);
						definitions.getDiagrams().add(diagram);
						
						ModelUtil.setID(bpmnElement);
						ModelUtil.setID(plane);
						ModelUtil.setID(diagram);
					}
					
				});
			}
		}
		return null;
	}

	private static class NewDiagramNameDialog extends InputDialog {
		
		public NewDiagramNameDialog(final BPMN2Editor editor, final String type) {
			super(editor.getSite().getShell(),
				NLS.bind(Messages.CreateDiagramCommand_Title,type),
				NLS.bind(Messages.CreateDiagramCommand_Message,type),
				"", //$NON-NLS-1$
				new IInputValidator() {
					@Override
					public String isValid(String newText) {
						if (newText==null || newText.isEmpty())
							return Messages.CreateDiagramCommand_Invalid_Empty;
						for (RootElement re : getDefinitions(editor).getRootElements()) {
							String name = null;
							if (re instanceof Process) {
								name = ((Process)re).getName();
							}
							else if (re instanceof Collaboration) {
								name = ((Collaboration)re).getName();
							}
							else if (re instanceof Choreography) {
								name = ((Choreography)re).getName();
							}
							if (newText.equals(name))
								return NLS.bind(Messages.CreateDiagramCommand_Invalid_Duplicate,type);
						}
						
						return null;
					}
						
				}
			);
		}
		
		public static Definitions getDefinitions(BPMN2Editor editor) {
			return ModelUtil.getDefinitions(editor.getBpmnDiagram());
		}
	}
}
