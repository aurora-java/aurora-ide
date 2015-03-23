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

package org.eclipse.bpmn2.modeler.ui.adapters.properties;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.ResourceAssignmentExpression;
import org.eclipse.bpmn2.ResourceRole;
import org.eclipse.bpmn2.modeler.core.adapters.AdapterUtil;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.FeatureDescriptor;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Bob Brodt
 *
 */
public class ResourceRolePropertiesAdapter extends ExtendedPropertiesAdapter<ResourceRole> {

	/**
	 * @param adapterFactory
	 * @param object
	 */
	public ResourceRolePropertiesAdapter(AdapterFactory adapterFactory, ResourceRole object) {
		super(adapterFactory, object);
		// ResourceRoles are contained in Process, GlobalTask and Activity
    	setProperty(Bpmn2Package.eINSTANCE.getResourceRole_ResourceAssignmentExpression(), ExtendedPropertiesAdapter.UI_IS_MULTI_CHOICE, Boolean.FALSE);
    	setProperty(Bpmn2Package.eINSTANCE.getResourceRole_ResourceRef(), ExtendedPropertiesAdapter.UI_IS_MULTI_CHOICE, Boolean.TRUE);

    	final EStructuralFeature ref = Bpmn2Package.eINSTANCE.getResourceRole_ResourceAssignmentExpression();
    	setFeatureDescriptor(ref,
			new FeatureDescriptor<ResourceRole>(this,object,ref) {

				@Override
				public String getTextValue() {
					ResourceAssignmentExpression rae = object.getResourceAssignmentExpression();
					if (rae!=null) {
						ExtendedPropertiesAdapter<ResourceAssignmentExpression> adapter =
								ExtendedPropertiesAdapter.adapt(rae);
						return adapter.getObjectDescriptor().getTextValue();
					}
					return ""; //$NON-NLS-1$
				}

				@Override
				protected void internalSet(ResourceRole rr, EStructuralFeature feature, Object value, int index) {
					if (value instanceof FormalExpression) {
						ResourceAssignmentExpression rae = rr.getResourceAssignmentExpression();
						if (rae!=null) {
							ExtendedPropertiesAdapter<ResourceAssignmentExpression> adapter =
									ExtendedPropertiesAdapter.adapt(rae);
					    	EStructuralFeature raeFeature = Bpmn2Package.eINSTANCE.getResourceAssignmentExpression_Expression();
							adapter.getFeatureDescriptor(raeFeature).setValue(value);
						}
					}
					else if (value instanceof ResourceAssignmentExpression) {
						rr.setResourceAssignmentExpression((ResourceAssignmentExpression)value);
					}
				}
    		}
    	);
	}

}
