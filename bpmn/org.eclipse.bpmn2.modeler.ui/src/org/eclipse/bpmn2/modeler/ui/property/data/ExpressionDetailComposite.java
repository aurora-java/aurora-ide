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
package org.eclipse.bpmn2.modeler.ui.property.data;

import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractBpmn2PropertySection;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.DefaultDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.ComboObjectEditor;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.ObjectEditor;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.widgets.Composite;

public class ExpressionDetailComposite extends DefaultDetailComposite {

	public ExpressionDetailComposite(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @param section
	 */
	public ExpressionDetailComposite(AbstractBpmn2PropertySection section) {
		super(section);
	}

	@Override
	public AbstractPropertiesProvider getPropertiesProvider(EObject object) {
		if (propertiesProvider==null) {
			propertiesProvider = new AbstractPropertiesProvider(object) {
				String[] properties = new String[] {
						"language", //$NON-NLS-1$
						"body", //$NON-NLS-1$
						"evaluatesToTypeRef" //$NON-NLS-1$
				};
				
				@Override
				public String[] getProperties() {
					return properties; 
				}
			};
		}
		return propertiesProvider;
	}
	
	@Override
	protected void bindAttribute(Composite parent, EObject object, EAttribute attribute, String label) {

		if (isModelObjectEnabled(object.eClass(), attribute) && "language".equals(attribute.getName())) { //$NON-NLS-1$

			if (parent==null)
				parent = getAttributesParent();
			
			if (label==null)
				label = getBusinessObjectDelegate().getLabel(object, attribute);
			
			ObjectEditor editor = new ComboObjectEditor(this,object,attribute) {
				
				@Override
				protected boolean canSetNull() {
					return true;
				}
			};
			editor.createControl(parent,label);
		}
		else
			super.bindAttribute(parent, object, attribute, label);
	}
}
