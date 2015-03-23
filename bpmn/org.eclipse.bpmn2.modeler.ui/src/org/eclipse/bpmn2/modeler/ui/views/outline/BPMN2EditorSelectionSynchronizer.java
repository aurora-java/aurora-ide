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

import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.internal.parts.ContainerShapeEditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

public class BPMN2EditorSelectionSynchronizer extends SelectionSynchronizer {
	
	protected EditPart convert(EditPartViewer viewer, EditPart part) {
		if (viewer instanceof BPMN2EditorOutlineTreeViewer) {
			BPMN2EditorOutlineTreeViewer ov = (BPMN2EditorOutlineTreeViewer)viewer;
			return ov.convert(part);
		}
		else if (viewer instanceof GraphicalViewer && part instanceof AbstractGraphicsTreeEditPart) {
			return BPMN2EditorOutlineTreeViewer.convert((GraphicalViewer)viewer, (AbstractGraphicsTreeEditPart)part);
		}
		return super.convert(viewer,part);
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		IStructuredSelection sel = (IStructuredSelection) event.getSelection();
		Object elem = sel.getFirstElement();
		if (elem instanceof ContainerShapeEditPart) {
			Object model = ((ContainerShapeEditPart)elem).getModel();
			if (model instanceof ContainerShape && FeatureSupport.isLabelShape((ContainerShape)model)) {
				ScrollingGraphicalViewer viewer = (ScrollingGraphicalViewer)event.getSource();
				PictogramElement labelOwner = FeatureSupport.getLabelOwner((ContainerShape)model);
				elem = viewer.getEditPartRegistry().get(labelOwner);
				if (elem!=null) {
					sel = new StructuredSelection(elem);
					event = new SelectionChangedEvent(viewer, sel);
				}
			}
		}
		super.selectionChanged(event);
	}

}
