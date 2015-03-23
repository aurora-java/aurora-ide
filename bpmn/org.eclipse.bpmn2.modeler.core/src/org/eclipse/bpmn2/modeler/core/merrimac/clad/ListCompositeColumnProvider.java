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
package org.eclipse.bpmn2.modeler.core.merrimac.clad;

import org.eclipse.bpmn2.modeler.core.merrimac.providers.ColumnTableProvider;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class ListCompositeColumnProvider extends ColumnTableProvider {
	protected final AbstractListComposite listComposite;
	protected boolean canModify = true;

	public ListCompositeColumnProvider(AbstractListComposite list) {
		this(list,true);
	}
	
	public ListCompositeColumnProvider(AbstractListComposite list, boolean canModify) {
		super();
		this.canModify = canModify;
		this.listComposite = list;
	}
	
	/**
	 * Implement this to select which columns are editable
	 * @param object - the list object
	 * @param feature - the feature of the item contained in the list
	 * @param item - the selected item in the list
	 * @return true to allow editing
	 */
	public boolean canModify(EObject object, EStructuralFeature feature, EObject item) {
		return canModify;
	}
	
	public void setCanModify(boolean f) {
		canModify = f;
	}
	
	public TableColumn add(EObject object, EStructuralFeature feature) {
		return add(object, (EClass)feature.eContainer(), feature);
	}
	
	public TableColumn add(EObject object, EClass eclass, EStructuralFeature feature) {
		TableColumn tc = null;
		listComposite.getModelEnablements();
		if (listComposite.isModelObjectEnabled(eclass,feature)) {
			tc = new TableColumn(object, feature);
			tc.setOwner(listComposite);
			super.add(tc);
		}
		return tc;
	}
	
	public TableColumn add(TableColumn tc) {
		EStructuralFeature feature = tc.feature;
		EObject object = tc.object;
		if (object!=null) {
			if (listComposite.isModelObjectEnabled(object.eClass(),feature)) {
				tc.setOwner(listComposite);
				super.add(tc);
				return tc;
			}
		}
		if (feature!=null) {
			EClass eclass = (EClass)feature.eContainer();
			if (listComposite.isModelObjectEnabled(eclass,feature)) {
				tc.setOwner(listComposite);
				super.add(tc);
				return tc;
			}
		}
		return tc;
	}
	
	public TableColumn addRaw(EObject object, EStructuralFeature feature) {
		return addRaw(new TableColumn(object, feature));
	}
	
	public TableColumn addRaw(TableColumn tc) {
		tc.setOwner(listComposite);
		super.add(tc);
		return tc;
	}
}