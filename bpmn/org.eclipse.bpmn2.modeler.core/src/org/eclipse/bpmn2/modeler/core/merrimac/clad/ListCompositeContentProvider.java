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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ListCompositeContentProvider implements IStructuredContentProvider {
	/**
	 * 
	 */
	protected final AbstractListComposite listComposite;
	protected EObject object;
	protected EStructuralFeature feature;
	protected EList<EObject> list;
	
	public ListCompositeContentProvider(AbstractListComposite listComposite, EObject object, EStructuralFeature feature, EList<EObject> list) {
		this.listComposite = listComposite;
		this.object = object;
		this.feature = feature;
		this.list = list;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof EList) {
			list = (EList<EObject>)newInput;
			object = listComposite.getBusinessObject();
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		// display all items in the list that are subclasses of listItemClass
		EClass listItemClass = listComposite.getListItemClass(object,feature);
		if (listItemClass==null) {
			return list.toArray();
		}
		else {
			List<EObject> elements = new ArrayList<EObject>();
			for (EObject o : list) {
				EClass ec = o.eClass();
				boolean isSubType = ec.getESuperTypes().contains(listItemClass);
				if (ec == listItemClass || isSubType)
					elements.add(o);
			}
			return elements.toArray(new EObject[elements.size()]);
		}
	}
}