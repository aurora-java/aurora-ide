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


package org.eclipse.bpmn2.modeler.ui.property.tasks;

import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractBpmn2PropertySection;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.TextObjectEditor;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.widgets.Composite;

public class ScriptTaskDetailComposite extends ActivityDetailComposite {

	private TextObjectEditor scriptEditor;
	
	public ScriptTaskDetailComposite(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @param section
	 */
	public ScriptTaskDetailComposite(AbstractBpmn2PropertySection section) {
		super(section);
	}

	@Override
	public void cleanBindings() {
		super.cleanBindings();
		scriptEditor = null;
	}
	
	protected void bindAttribute(Composite parent, EObject object, EAttribute attribute) {
		if ("script".equals(attribute.getName())) { //$NON-NLS-1$
			scriptEditor = new TextObjectEditor(this,object,attribute);
			scriptEditor.createControl(getAttributesParent(),Messages.ScriptTaskDetailComposite_Script_Label);
		}
		else
			super.bindAttribute(parent,object,attribute);
	}

}