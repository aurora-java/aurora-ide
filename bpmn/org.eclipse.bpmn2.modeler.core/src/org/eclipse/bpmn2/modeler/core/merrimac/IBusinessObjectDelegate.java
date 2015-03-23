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

package org.eclipse.bpmn2.modeler.core.merrimac;

import java.util.Hashtable;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * An interface used by the merrimac package to access business objects.
 * A default implementation exists that provides reasonable functionality
 * for all UI widgets, but plugin developers may provide their own implementation.
 */
public interface IBusinessObjectDelegate {
	
	// Object methods
	public EObject createObject(EClass eClass);
	public <T extends EObject> T createObject(Class clazz);
	public EObject createFeature(EObject object, EStructuralFeature feature);

	public String getTextValue(EObject object);
	public boolean setTextValue(EObject object, String value);
	public String getLabel(EObject object);

	// Feature methods
	public EStructuralFeature getFeature(EObject object, String name);
	public String getLabel(EObject object, EStructuralFeature feature);
	
	public Object getValue(EObject object, EStructuralFeature feature);
	public List<Object> getValueList(EObject object, EStructuralFeature feature);
	public boolean setValue(EObject object, EStructuralFeature feature, Object value);
	public Object getValue(EObject object, EStructuralFeature feature, int index);
	public boolean setValue(EObject object, EStructuralFeature feature, Object value, int index);

	public String getTextValue(EObject object, EStructuralFeature feature);
	public boolean setTextValue(EObject object, EStructuralFeature feature, String value);
	
	// Feature flags and attributes
	public boolean isList(EObject object, EStructuralFeature feature);
	public boolean isAttribute(EObject object, EStructuralFeature feature);
	public boolean isReference(EObject object, EStructuralFeature feature);
	boolean isContainmentFeature(EObject object, EStructuralFeature feature);
	public boolean isMultiLineText(EObject object, EStructuralFeature feature);
	public boolean canEdit(EObject object, EStructuralFeature feature);
	public boolean canCreateNew(EObject object, EStructuralFeature feature);
	public boolean canEditInline(EObject object, EStructuralFeature feature);
	public boolean canSetNull(EObject object, EStructuralFeature feature);
	public boolean isMultiChoice(EObject object, EStructuralFeature feature);
	public Hashtable<String, Object> getChoiceOfValues(EObject object, EStructuralFeature feature);
	// TODO: do we need this?
	public EClassifier getEType(EObject object, EStructuralFeature feature);
}
