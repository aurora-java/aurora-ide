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

package org.eclipse.bpmn2.modeler.ui.property;

import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.runtime.CustomTaskDescriptor;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.ui.util.PropertyUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Bob Brodt
 *
 */
public class PropertyLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		EObject object = getBusinessObject(element);
		if (object!=null) {
			CustomTaskDescriptor ctd = getCustomTaskDescriptor(object); 
			if (ctd!=null) {
				return PropertyUtil.getImage("CustomTask"); //$NON-NLS-1$
			}
			return PropertyUtil.getImage(object);
		}
		return super.getImage(element);
	}

	@Override
	public String getText(Object element) {
		EObject object = getBusinessObject(element);
		if (object!=null) {
			String text = ExtendedPropertiesProvider.getTextValue(object);
			// check if this is a CustomTask
			CustomTaskDescriptor ctd = getCustomTaskDescriptor(object); 
			if (ctd!=null) {
				// it is! build the property tab title from the CustomTask name
				// and the object's name (which could be the same)
				String name = ctd.getName();
				if (!text.equals(name))
					return name + ": " + text; //$NON-NLS-1$
			}
			return text;
		}
//		PictogramElement pe = BusinessObjectUtil.getPictogramElementForSelection((ISelection)element);
//		if (pe!=null && pe.getGraphicsAlgorithm()!=null) {
//			return ModelUtil.getLabel( pe.getGraphicsAlgorithm() );
//		}
		return super.getText(element);
	}
	
	private EObject getBusinessObject(Object element) {
		if (element instanceof ISelection) {
			return BusinessObjectUtil.getBusinessObjectForSelection((ISelection)element);
		}
		else if (element instanceof EObject) {
			return (EObject) element;
		}
		return null;
		
	}
	
	private CustomTaskDescriptor getCustomTaskDescriptor(EObject object) {
		TargetRuntime rt = getTargetRuntime(object);
		if (rt!=null) {
			for (CustomTaskDescriptor ctd : rt.getCustomTaskDescriptors()) {
				if (ctd.getFeatureContainer()!=null) {
					String id = ctd.getFeatureContainer().getId(object);
					if (ctd.getId().equals(id)) {
						return ctd;
					}
				}
			}
		}
		return null;
	}
	
	private TargetRuntime getTargetRuntime(EObject object) {
		DiagramEditor editor = ModelUtil.getDiagramEditor(object);
		if (editor!=null) {
			return (TargetRuntime) editor.getAdapter(TargetRuntime.class);
		}
		return null;
	}
}
