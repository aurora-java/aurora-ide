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

package org.eclipse.bpmn2.modeler.ui.property.data;

import org.eclipse.bpmn2.DataObjectReference;
import org.eclipse.bpmn2.DataState;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractBpmn2PropertySection;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.DefaultDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.DefaultPropertySection;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Bob Brodt
 *
 */
public class DataObjectReferencePropertySection extends DefaultPropertySection {

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.ui.property.AbstractBpmn2PropertySection#createSectionRoot()
	 */
	@Override
	protected AbstractDetailComposite createSectionRoot() {
		return new DataObjectReferenceDetailComposite(this);
	}

	@Override
	public AbstractDetailComposite createSectionRoot(Composite parent, int style) {
		return new DataObjectReferenceDetailComposite(parent,style);
	}

	@Override
	public EObject getBusinessObjectForSelection(ISelection selection) {
		EObject bo = super.getBusinessObjectForSelection(selection);
		if (bo instanceof DataObjectReference) {
			return bo;
		}		
		return null;
	}
	
	public class DataObjectReferenceDetailComposite extends DefaultDetailComposite {

		private AbstractPropertiesProvider dataObjectReferencePropertiesProvider;
		private AbstractPropertiesProvider dataStatePropertiesProvider;

		/**
		 * @param section
		 */
		public DataObjectReferenceDetailComposite(AbstractBpmn2PropertySection section) {
			super(section);
		}

		public DataObjectReferenceDetailComposite(Composite parent, int style) {
			super(parent, style);
		}

		@Override
		public AbstractPropertiesProvider getPropertiesProvider(EObject object) {
			if (object instanceof DataState) {
				if (dataStatePropertiesProvider == null) {
					dataStatePropertiesProvider = new AbstractPropertiesProvider(object) {
						String[] properties = new String[] { "id", "name" }; //$NON-NLS-1$ //$NON-NLS-2$
						
						@Override
						public String[] getProperties() {
							return properties; 
						}
					};
				}
				return dataStatePropertiesProvider;
			}
			else if (object instanceof DataObjectReference) {
				if (dataObjectReferencePropertiesProvider == null) {
					dataObjectReferencePropertiesProvider = new AbstractPropertiesProvider(object) {
						String[] properties = new String[] { "id", "name", "dataState" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	
						@Override
						public String[] getProperties() {
							return properties; 
						}
					};
			
				}
				return dataObjectReferencePropertiesProvider;
			}
			return null;
		}
		
	}
}
