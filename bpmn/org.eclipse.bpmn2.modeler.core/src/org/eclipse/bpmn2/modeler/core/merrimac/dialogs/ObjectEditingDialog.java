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

package org.eclipse.bpmn2.modeler.core.merrimac.dialogs;

import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.PropertiesCompositeFactory;
import org.eclipse.bpmn2.modeler.help.IHelpContexts;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

public class ObjectEditingDialog extends AbstractObjectEditingDialog {

	protected EClass featureEType;

	public ObjectEditingDialog(DiagramEditor editor, EObject object) {
		this(editor,object,object.eClass());
	}
	
	public ObjectEditingDialog(DiagramEditor editor, EObject object, EClass eclass) {
		super(editor, object);
		this.featureEType = eclass;
	}

	public void setFeatureEType(EClass eclass) {
		this.featureEType = eclass;
	}
	
	public EClass getFeatureEType() {
		return featureEType;
	}
	
	protected Composite createDialogContent(Composite parent) {
		Composite content = PropertiesCompositeFactory.INSTANCE.createDialogComposite(
				featureEType, parent, SWT.NONE);
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getShell(), IHelpContexts.Property_Dialog);
		return content;
	}

	@Override
	protected String getPreferenceKey() {
		return featureEType.getName();
	}
	
	@Override
	public int open() {
		title = null;
		if (object!=null)
			title = NLS.bind(Messages.ObjectEditingDialog_Edit, ExtendedPropertiesProvider.getLabel(object));
		create();
		if (cancel)
			return Window.CANCEL;
		if (title==null)
			title = NLS.bind(Messages.ObjectEditingDialog_Create, ExtendedPropertiesProvider.getLabel(object));
		return super.open();
	}
}