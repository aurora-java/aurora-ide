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
package org.eclipse.bpmn2.modeler.ui.editor;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.bpmn2.modeler.core.utils.FixDuplicateIdsDialog;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.core.utils.Tuple;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DefaultPersistencyBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

public class BPMN2PersistencyBehavior extends DefaultPersistencyBehavior {

	BPMN2Editor editor;
	
	public BPMN2PersistencyBehavior(DiagramBehavior diagramBehavior) {
		super(diagramBehavior);
		editor = (BPMN2Editor)diagramBehavior.getDiagramContainer();
	}
	
    @Override
    public Diagram loadDiagram(URI diagramUri) {
    	Diagram diagram = super.loadDiagram(diagramUri);
//    	Resource resource = editor.getResource();
//		List<Tuple<EObject,EObject>> dups = ModelUtil.findDuplicateIds(resource);
//		if (dups.size()>0) {
//			FixDuplicateIdsDialog dlg = new FixDuplicateIdsDialog(dups);
//			dlg.open();
//		}
    	return diagram;
    }
    
    @Override
	public void saveDiagram(IProgressMonitor monitor) {
    	Resource resource = editor.getResource();
		List<Tuple<EObject,EObject>> dups = ModelUtil.findDuplicateIds(resource);
		if (dups.size()>0) {
			FixDuplicateIdsDialog dlg = new FixDuplicateIdsDialog(dups);
			dlg.open();
		}

    	super.saveDiagram(monitor);
    }
    
	protected IRunnableWithProgress createOperation(final Set<Resource> savedResources,
			final Map<Resource, Map<?, ?>> saveOptions) {
		// Do the work within an operation because this is a long running
		// activity that modifies the workbench.
		final IRunnableWithProgress operation = new IRunnableWithProgress() {
			// This is the method that gets invoked when the operation runs.
			public void run(final IProgressMonitor monitor) {
				// Save the resources to the file system.
				try {
					savedResources.addAll(save(diagramBehavior.getEditingDomain(), saveOptions, monitor));
				} catch (final WrappedException e) {
					String emsg = e.getMessage().replaceAll("\tat .*", "").replaceFirst(".*Exception: ","").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					final String msg = emsg.replace("\r\n\r\n", "").replace("\n\n", "");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							MessageDialog.openError(Display.getDefault().getActiveShell(), Messages.BPMN2PersistencyBehavior_Cannot_Save_Title, msg);
							monitor.setCanceled(true);
						}
					});
					throw e;
				}
			}
		};
		return operation;
	}

}
