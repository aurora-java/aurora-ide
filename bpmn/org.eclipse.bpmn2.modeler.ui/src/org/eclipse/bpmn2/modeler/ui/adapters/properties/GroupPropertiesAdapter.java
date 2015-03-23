/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013, 2014 Red Hat, Inc.
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

import java.util.List;

import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Category;
import org.eclipse.bpmn2.CategoryValue;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Group;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.FeatureDescriptor;
import org.eclipse.bpmn2.modeler.core.adapters.InsertionAdapter;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;

/**
 *
 */
public class GroupPropertiesAdapter extends ExtendedPropertiesAdapter<Group> {

	/**
	 * @param adapterFactory
	 * @param object
	 */
	public GroupPropertiesAdapter(AdapterFactory adapterFactory, Group object) {
		super(adapterFactory, object);
		
    	EStructuralFeature feature = Bpmn2Package.eINSTANCE.getGroup_CategoryValueRef();
    	setProperty(feature, UI_CAN_CREATE_NEW, Boolean.TRUE);
    	
    	this.setFeatureDescriptor(feature, new FeatureDescriptor<Group>(this, object, feature) {

			@Override
			public EObject createFeature(Resource resource, EClass eclass) {
				Category category = null;
				CategoryValue categoryValue = null;
				Definitions definitions = ModelUtil.getDefinitions(resource);
				List<Category> categories = ModelUtil.getAllRootElements(definitions, Category.class);
				if (categories.size()==0) {
					category = (Category) Bpmn2Factory.eINSTANCE.create(Bpmn2Package.eINSTANCE.getCategory());
					ModelUtil.setID(category, resource);
					InsertionAdapter.add(definitions, Bpmn2Package.eINSTANCE.getDefinitions_RootElements(), category);
				}
				else {
					category = categories.get(0);
				}
				String title = Messages.GroupPropertiesAdapter_CreateCategory_Title;
				InputDialog dialog = new InputDialog(null, title, Messages.GroupPropertiesAdapter_CreateCategory_Prompt, "", null); //$NON-NLS-2$
				if (dialog.open() == Window.OK) {
					String name = dialog.getValue();
					if (!name.isEmpty()) {
						categoryValue = (CategoryValue) Bpmn2Factory.eINSTANCE.create(Bpmn2Package.eINSTANCE.getCategoryValue());
						ModelUtil.setID(categoryValue,resource);
						categoryValue.setValue(name);
						category.getCategoryValue().add(categoryValue);
					}
				}
				return categoryValue;
			}
    		
    	});
	}

}
