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

package org.eclipse.bpmn2.modeler.core.adapters;

import java.util.Hashtable;
import java.util.List;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * A convenience wrapper class for <b>most</b> of the ExtendedPropertiesAdapter methods.
 * <p>
 * Most methods provide reasonable defaults for EObjects that can <b>not</b> be adapted
 * for {@code ExtendedPropertiesAdapter}
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ExtendedPropertiesProvider {

	/**
	 * See {@link ObjectDescriptor#getLabel()}
	 */
	public static String getLabel(EObject object) {
		String label = ""; //$NON-NLS-1$
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object);
		if (adapter != null)
			label = adapter.getObjectDescriptor().getLabel();
		else if (object!=null)
			label = ModelUtil.toCanonicalString(object.eClass().getName());
		return label;
	}

	/**
	 * See {@link ObjectDescriptor#setLabel(String)}
	 */
	public static void setLabel(EObject object, String label) {
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object);
		if (adapter != null)
			adapter.getObjectDescriptor().setLabel(label);
	}

	/**
	 * See {@link FeatureDescriptor#getLabel()}
	 */
	public static String getLabel(EObject object, EStructuralFeature feature) {
		String label = ""; //$NON-NLS-1$
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object, feature);
		if (adapter != null)
			label = adapter.getFeatureDescriptor(feature).getLabel();
		else
			label = ModelUtil.toCanonicalString(feature.getName());
		label = label.replaceAll(" Ref$", ""); //$NON-NLS-1$ //$NON-NLS-2$
		return label;
	}

	/**
	 * See {@link FeatureDescriptor#setLabel()}
	 */
	public static void setLabel(EObject object, EStructuralFeature feature, String label) {
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object, feature);
		if (adapter != null)
			adapter.getFeatureDescriptor(feature).setLabel(label);
	}

	/**
	 * See {@link ObjectDescriptor#getTextValue()}
	 */
	public static String getTextValue(EObject object) {
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object);
		if (adapter != null) {
			String text = adapter.getObjectDescriptor().getTextValue();
			if (text != null && !text.isEmpty()) {
				return text;
			}
		}
		return ModelUtil.toCanonicalString(object);
	}

	/**
	 * See {@link ObjectDescriptor#setTextValue(String)}
	 */
	public static boolean setTextValue(EObject object, String value) {
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object);
		if (adapter!=null) {
			adapter.getObjectDescriptor().setTextValue(value);
			return true;
		}
		return false;
	}

	/**
	 * See {@link FeatureDescriptor#getTextValue()}
	 */
	public static String getTextValue(EObject object, EStructuralFeature feature) {
		if (feature == null)
			return getTextValue(object);

		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object, feature);
		if (adapter != null)
			return adapter.getFeatureDescriptor(feature).getTextValue();
		try {
			Object value = object.eGet(feature);
			return value.toString();
		} catch (Exception e) {
		}
		return ""; //$NON-NLS-1$
	}

	/**
	 * See {@link FeatureDescriptor#getValue()}
	 */
	public static Object getValue(final EObject object, final EStructuralFeature feature) {
		return getValue(object, feature, -1);
	}

	/**
	 * See {@link FeatureDescriptor#getValueList()}
	 */
	public static List<Object> getValueList(EObject object, EStructuralFeature feature) {
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object, feature);
		return adapter.getFeatureDescriptor(feature).getValueList();
	}

	/**
	 * See {@link FeatureDescriptor#getValue(int)}
	 */
	public static Object getValue(final EObject object, final EStructuralFeature feature, int index) {
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object, feature);
		Object value = adapter == null ? object.eGet(feature) : adapter.getFeatureDescriptor(feature).getValue(index);
		return value;
	}

	/**
	 * See {@link FeatureDescriptor#setValue(Object)}
	 */
	public static boolean setValue(EObject object, EStructuralFeature feature, Object value) {
		return setValue(object, feature, value, -1);
	}

	/**
	 * See {@link FeatureDescriptor#setValue(Object,int)}
	 */
	public static boolean setValue(EObject object, EStructuralFeature feature, Object value, int index) {
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object, feature);
		return adapter.getFeatureDescriptor(feature).setValue(value, index);
	}

	/**
	 * See {@link FeatureDescriptor#isMultiLine()}
	 */
	public static boolean isMultiLineText(EObject object, EStructuralFeature feature) {
		if (feature == null)
			return false;

		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object, feature);
		if (adapter != null)
			return adapter.getFeatureDescriptor(feature).isMultiLine();
		return false;
	}

	/**
	 * See {@link FeatureDescriptor#setMultiLine(boolean)}
	 */
	public static boolean setMultiLineText(EObject object, EStructuralFeature feature, boolean multiLine) {
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object, feature);
		if (adapter != null) {
			adapter.getFeatureDescriptor(feature).setMultiLine(multiLine);
			return true;
		}
		return false;
	}

	/**
	 * See {@link FeatureDescriptor#getChoiceOfValues()}
	 */
	public static Hashtable<String, Object> getChoiceOfValues(EObject object, EStructuralFeature feature) {
		if (feature == null)
			return null;

		if (feature.getEType() instanceof EEnum) {
			EEnum en = (EEnum) feature.getEType();
			Hashtable<String, Object> choices = new Hashtable<String, Object>();
			for (EEnumLiteral el : en.getELiterals()) {
				choices.put(el.getLiteral(), el.getInstance());
			}
			return choices;
		}

		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object, feature);
		if (adapter != null)
			return adapter.getFeatureDescriptor(feature).getChoiceOfValues();
		return null;
	}

	/**
	 * See {@link ExtendedPropertiesAdapter.UI_IS_MULTI_CHOICE}
	 */
	public static boolean isMultiChoice(EObject object, EStructuralFeature feature) {
		if (feature == null) {
			return false;
		}
		
		if (feature.getEType() instanceof EEnum) {
			return true;
		}

		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object, feature);
		if (adapter != null) {
			Object result = adapter.getProperty(feature, ExtendedPropertiesAdapter.UI_IS_MULTI_CHOICE);
			if (result instanceof Boolean)
				return ((Boolean) result);
		}
		
		if (feature instanceof EReference && feature.isMany()) {
			return !((EReference)feature).isContainment();
		}

		return getChoiceOfValues(object, feature) != null;
	}

	/**
	 * See {@link ExtendedPropertiesAdapter.UI_CAN_EDIT}
	 */
	public static boolean canEdit(EObject object, EStructuralFeature feature) {
		if (feature != null && feature.getEType() instanceof EClass) {
			ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object, feature);
			if (adapter != null) {
				Object result = adapter.getProperty(feature, ExtendedPropertiesAdapter.UI_CAN_EDIT);
				if (result instanceof Boolean)
					return ((Boolean) result);
			}
			if (feature instanceof EReference) {
				if (((EReference) feature).isContainment())
					return true;
				if (Bpmn2Package.eINSTANCE.getRootElement().isSuperTypeOf((EClass) feature.getEType()))
					return true;
				if (feature.isMany())
					return true;
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * See {@link ExtendedPropertiesAdapter.UI_CAN_CREATE_NEW}
	 */
	public static boolean canCreateNew(EObject object, EStructuralFeature feature) {
		if (feature != null && feature.getEType() instanceof EClass) {
			ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object, feature);
			if (adapter != null) {
				Object result = adapter.getProperty(feature, ExtendedPropertiesAdapter.UI_CAN_CREATE_NEW);
				if (result instanceof Boolean)
					return ((Boolean) result);
			}
			if (feature instanceof EReference) {
				if (((EReference) feature).isContainment())
					return true;
				if (Bpmn2Package.eINSTANCE.getRootElement().isSuperTypeOf((EClass) feature.getEType()))
					return true;
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * See {@link ExtendedPropertiesAdapter.UI_CAN_EDIT_INLINE}
	 */
	public static boolean canEditInline(EObject object, EStructuralFeature feature) {
		if (feature != null && feature.getEType() instanceof EClass) {
			ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object, feature);
			if (adapter != null) {
				Object result = adapter.getProperty(feature, ExtendedPropertiesAdapter.UI_CAN_EDIT_INLINE);
				if (result instanceof Boolean)
					return ((Boolean) result);
			}
		}
		return false;
	}

	/**
	 * See {@link ExtendedPropertiesAdapter.UI_CAN_SET_NULL}
	 */
	public static boolean canSetNull(EObject object, EStructuralFeature feature) {
		if (feature != null && feature.getEType() instanceof EClass) {
			ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object, feature);
			if (adapter != null) {
				Object result = adapter.getProperty(feature, ExtendedPropertiesAdapter.UI_CAN_SET_NULL);
				if (result instanceof Boolean)
					return ((Boolean) result);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Check if the given object feature is a containment list.
	 * 
	 * @param object the EObject
	 * @param feature the object's feature
	 * @return true if the feature is a reference, and is a containment list.
	 */
	public static boolean isList(EObject object, EStructuralFeature feature) {
		if (feature instanceof EReference) {
			return feature.isMany() && ((EReference)feature).isContainment();
		}
		return false;
	}

	/**
	 * Check if the given object feature is a containment feature.
	 * 
	 * @param object the EObject
	 * @param feature the object's feature
	 * @return true if the feature is a reference, and is a containment feature.
	 */
	public static boolean isContainmentFeature(EObject object, EStructuralFeature feature) {
		if (feature instanceof EReference) {
			if (((EReference)feature).isContainment()) {
				if (feature.getEType() instanceof EClass)
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Check if the given object feature is an attribute.
	 * 
	 * @param object the EObject
	 * @param feature the object's feature
	 * @return true if the feature is an attribute.
	 */
	public static boolean isAttribute(EObject object, EStructuralFeature feature) {
		if (feature instanceof EAttribute)
			return true;
		return false;
	}

	/**
	 * Check if the given object feature is a reference.
	 * 
	 * @param object the EObject
	 * @param feature the object's feature
	 * @return true if the feature is a reference.
	 */
	public static boolean isReference(EObject object, EStructuralFeature feature) {
		if (feature instanceof EReference)
			return true;
		return false;
	}
	
	/**
	 * Get the given object feature's type.
	 * 
	 * @param object the EObject
	 * @param feature the object's feature
	 * @return the feature's type.
	 */
	public static EClassifier getEType(EObject object, EStructuralFeature feature) {
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object);
		if (adapter!=null) {
			adapter.getFeatureDescriptor(feature).getEType();
		}
		return feature.getEType();
	}

	/**
	 * See {@link ExtendedPropertiesAdapter#compare(EObject, EObject, boolean)}
	 */
	public static boolean compare(EObject object1, EObject object2, boolean similar) {
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object1);
		if (adapter!=null) {
			adapter.getObjectDescriptor().compare(object2, similar);
		}
		return ExtendedPropertiesAdapter.compare(object1, object2, similar);
	}

}
