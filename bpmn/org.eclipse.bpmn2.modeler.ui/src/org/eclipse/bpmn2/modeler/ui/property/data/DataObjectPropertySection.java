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

import org.eclipse.bpmn2.DataObject;
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
 * From section 10.3.1 of the BPMN 2.0 specification:
 * "The Data Object class is an item-aware parameter. Data Object elements MUST be contained within Process or Sub-
 * Process elements. Data Object elements are visually displayed on a Process diagram. Data Object References are
 * a way to reuse Data Objects in the same diagram. They can specify different states of the same Data Object at
 * different points in a Process. Data Object Reference cannot specify item definitions, and Data Objects cannot
 * specify states. The names of Data Object References are derived by concatenating the name of the referenced Data
 * Data Object the state of the Data Object Reference in square brackets as follows: <Data Object Name> [ <Data
 * Object Reference State> ]."
 */
public class DataObjectPropertySection extends DefaultPropertySection {
	
	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.ui.property.AbstractBpmn2PropertySection#createSectionRoot()
	 */
	@Override
	protected AbstractDetailComposite createSectionRoot() {
		return new DataObjectDetailComposite(this);
	}

	@Override
	public AbstractDetailComposite createSectionRoot(Composite parent, int style) {
		return new DataObjectDetailComposite(parent,style);
	}

	@Override
	public EObject getBusinessObjectForSelection(ISelection selection) {
		EObject bo = super.getBusinessObjectForSelection(selection);
		if (bo instanceof DataObject) {
			return bo;
		}
		if (bo instanceof DataObjectReference) {
			return ((DataObjectReference)bo).getDataObjectRef();
		}		

		return null;
	}
	
	public class DataObjectDetailComposite extends DefaultDetailComposite {

		private AbstractPropertiesProvider dataObjectReferencePropertiesProvider;
		private AbstractPropertiesProvider dataStatePropertiesProvider;

		public DataObjectDetailComposite(Composite parent, int style) {
			super(parent, style);
		}

		/**
		 * @param section
		 */
		public DataObjectDetailComposite(AbstractBpmn2PropertySection section) {
			super(section);
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
			else if (object instanceof DataObject) {
				if (propertiesProvider == null) {
					propertiesProvider = new AbstractPropertiesProvider(object) {
						String[] properties = new String[] { "id", "isCollection", "itemSubjectRef", "dataState" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
						
						@Override
						public String[] getProperties() {
							return properties;
						}
					};
				}
				return propertiesProvider;
			}
			else if (object instanceof DataObjectReference) {
				if (dataObjectReferencePropertiesProvider == null) {
					dataObjectReferencePropertiesProvider = new AbstractPropertiesProvider(object) {
						String[] properties = new String[] { "id", "dataObjectRef" , "dataState" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	
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
