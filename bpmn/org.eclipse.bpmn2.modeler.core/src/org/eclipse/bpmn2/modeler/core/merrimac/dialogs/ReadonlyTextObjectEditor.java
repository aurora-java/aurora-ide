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
package org.eclipse.bpmn2.modeler.core.merrimac.dialogs;

import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractDetailComposite;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;

public class ReadonlyTextObjectEditor extends TextAndButtonObjectEditor {

	public ReadonlyTextObjectEditor(AbstractDetailComposite parent,
			EObject object, EStructuralFeature feature) {
		super(parent, object, feature);
	}

	@Override
	protected void buttonClicked(int buttonId) {
		Object value = object.eGet(feature);
		if (value == null || value instanceof EObject) {
			FeatureEditingDialog dialog = createFeatureEditingDialog((EObject)value);
			if (dialog.open()==Window.OK){
				setValue(dialog.getNewObject());
			}
		}
		else {
			String msg = NLS.bind(
				Messages.ReadonlyTextObjectEditor_Invalid_Feature,
				feature.getName(),
				object.eClass().getName()); //$NON-NLS-2$
			MessageDialog.openError(getDiagramEditor().getSite().getShell(), Messages.ReadonlyTextObjectEditor_Title, msg);
		}
	}

}
