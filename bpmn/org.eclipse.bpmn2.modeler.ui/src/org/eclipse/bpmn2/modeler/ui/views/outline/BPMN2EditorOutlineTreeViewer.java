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
package org.eclipse.bpmn2.modeler.ui.views.outline;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.ui.editor.BPMN2Editor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.util.Adaptable;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;

public class BPMN2EditorOutlineTreeViewer extends TreeViewer implements Adaptable {

	protected DiagramEditor diagramEditor;
	
	public BPMN2EditorOutlineTreeViewer(DiagramEditor diagramEditor) {
		this.diagramEditor = diagramEditor;
	}
	
	public EditPart convert(EditPart part) {
		Object model = part.getModel();
		if (model instanceof PictogramElement) {
			PictogramElement pe = (PictogramElement)model;
			EObject bpmnModel = BusinessObjectUtil.getFirstBaseElement(pe);
			if (bpmnModel==null)
				bpmnModel = BusinessObjectUtil.getBusinessObjectForPictogramElement(pe);
			if (bpmnModel instanceof BPMNDiagram) {
				BPMNDiagram bpmnDiagram = (BPMNDiagram)bpmnModel;
				bpmnModel = bpmnDiagram.getPlane().getBpmnElement();
			}

			return (EditPart)getEditPartRegistry().get(bpmnModel);
		}
		return part;
	}
	
	public static EditPart convert(GraphicalViewer viewer, AbstractGraphicsTreeEditPart part) {
		Object pe = part.getAdapter(PictogramElement.class);
		return (EditPart) viewer.getEditPartRegistry().get(pe);
	}
	
	public void setSelection(ISelection newSelection) {
		// prevent selection of elements contained in collapsed Expandable Activities
		if (newSelection instanceof StructuredSelection) {
			StructuredSelection ss = (StructuredSelection) newSelection;
			if (ss.getFirstElement() instanceof AbstractGraphicsTreeEditPart) {
				AbstractGraphicsTreeEditPart editPart = (AbstractGraphicsTreeEditPart) ss.getFirstElement();
				Object model = editPart.getModel();
				if (model instanceof BaseElement && FeatureSupport.isExpandableElement((BaseElement)model)) {
					Diagram diagram = diagramEditor.getDiagramTypeProvider().getDiagram();
					EditPart editPartParent = editPart.getParent();
					while (editPartParent!=null) {
						model = editPartParent.getModel();
						for (PictogramElement pe : Graphiti.getLinkService().getPictogramElements(diagram, (EObject)model)) {
							if (!FeatureSupport.isElementExpanded(pe)) {
								super.setSelection(new StructuredSelection(editPartParent));
								return;
							}
						}
						editPartParent = editPartParent.getParent();
					}
				}
			}
		}
		super.setSelection(newSelection);
	}

	@Override
	public Object getAdapter(Class adapterType) {
		if (adapterType==BPMN2Editor.class)
			return diagramEditor;
		else if (diagramEditor!=null)
			return diagramEditor.getAdapter(adapterType);
		return null;
	}
}
