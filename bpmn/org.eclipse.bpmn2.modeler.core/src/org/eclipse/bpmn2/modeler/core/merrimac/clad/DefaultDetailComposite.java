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
package org.eclipse.bpmn2.modeler.core.merrimac.clad;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.Documentation;
import org.eclipse.bpmn2.Expression;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.impl.Bpmn2PackageImpl;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.adapters.InsertionAdapter;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.TextObjectEditor;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class DefaultDetailComposite extends AbstractDetailComposite {

	protected final String[] EMPTY_STRING_ARRAY = new String[] {};
	
	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public DefaultDetailComposite(Composite parent, int style) {
		super(parent,style);
	}
	
	public DefaultDetailComposite(AbstractBpmn2PropertySection section) {
		super(section);
	}
	
	protected Composite bindFeature(EObject be, EStructuralFeature feature, EClass eItemClass) {
		Composite composite = null;
		if (feature!=null) {
			if (isAttribute(be,feature)) {
				bindAttribute(getAttributesParent(), be,(EAttribute)feature);
			}
			else if (isList(be,feature)) {
				if (eItemClass==null)
					composite = bindList(be,feature);
				else
					composite = bindList(be,feature, eItemClass);
			}
			else if (isReference(be,feature)) {
				bindReference(getAttributesParent(), be,(EReference)feature);
			}
		}
		return composite;
	}
	
	protected AbstractListComposite bindList(EObject object, EStructuralFeature feature, EClass listItemClass) {
		if (feature.getEType() == PACKAGE.getDocumentation()) {
			if (getPreferences().getSimplifyLists()) {
				List<Documentation> docList = (List<Documentation>)object.eGet(feature);
				Documentation documentation;
				if (docList.size()==0) {
					documentation = createModelObject(Documentation.class);
					InsertionAdapter.add(object, feature, documentation);
				}
				else {
					documentation = docList.get(0);
				}
				EStructuralFeature f = PACKAGE.getDocumentation_Text();
				ExtendedPropertiesProvider.setMultiLineText(documentation, f, true);
				Composite container = createSectionComposite(this, Messages.DefaultDetailComposite_Documentation);
				TextObjectEditor documentationEditor = new TextObjectEditor(this,documentation,f);
				documentationEditor.createControl(container,""); //$NON-NLS-1$
				return null;
			}
		}
		return super.bindList(object, feature, listItemClass);
	}
	
	/**
	 * "rootElements#Process.resources#HumanPerformer"
	 * @param businessObject
	 * @param property
	 * @return
	 */
	protected Composite bindProperty(EObject be, String property) {
		Composite composite = null;
		String[] propArray = property.split("\\."); //$NON-NLS-1$
		String prop0 = propArray[0];
		String[] featureAndClassArray = prop0.split("#"); //$NON-NLS-1$
		String featureName = featureAndClassArray[0];
		EStructuralFeature feature = getFeature(be,featureName);
		EClass eclass = null;
		if (featureAndClassArray.length>1) {
			String className = featureAndClassArray[1];
			eclass = (EClass)Bpmn2PackageImpl.eINSTANCE.getEClassifier(className);
			if (eclass==null)
				eclass = (EClass) getTargetRuntime().getModelDescriptor().getEPackage().getEClassifier(className);
			if (eclass!=null) {
				if (!isModelObjectEnabled(eclass))
					return null;
			}
		}
		
		// reconstruct the remainder of the property string (if any)
		property = ""; //$NON-NLS-1$
		for (int i=1; i<propArray.length; ++i) {
			if (!property.isEmpty())
				property += "."; //$NON-NLS-1$
			property += propArray[i];
		}
		
		if (!property.isEmpty()) {
			// determine new object - may be a list
			if (eclass!=null) {
				Object value = be.eGet(feature);
				if (value instanceof EList) {
					List<Object> values = new ArrayList<Object>();
					for (Object o : (EList)value) {
						if (o instanceof ExtensionAttributeValue) {
							ExtensionAttributeValue eav = (ExtensionAttributeValue)o;
							for (Entry e : eav.getValue()) {
								values.add(e.getValue());
							}
						}
						else {
							values.add(o);
						}
					}
					
					for (Object o : values) {
						if (eclass.isInstance(o)) {
							propArray = property.split("[\\.#]"); //$NON-NLS-1$
							featureName = propArray[0];
							feature = getFeature((EObject)o,featureName);
							composite = bindProperty((EObject)o, property);
							if (composite instanceof AbstractListComposite) {
								EClass listItemClass = ((AbstractListComposite)composite).getDefaultListItemClass((EObject)o, feature);
								((AbstractListComposite)composite).setTitle(
									NLS.bind(
										Messages.DefaultDetailComposite_List_Title,
										getBusinessObjectDelegate().getLabel(listItemClass),
										ModelUtil.toCanonicalString((EObject)o)
									)
								);
							}
						}
					}
				}
			}
			else if (feature!=null) {
				Object value = be.eGet(feature);
				if (value instanceof EList) {
					for (Object o : (EList)value) {
						if (o instanceof EObject) {
							bindProperty((EObject)o, property);
						}
					}
				}
				else if (value instanceof EObject) {
					composite = bindProperty(be, property);
				}
			}
		}
		else {
			composite = bindFeature(be, feature, eclass);
		}
		return composite;
	}
	
	@Override
	public void createBindings(EObject be) {
		AbstractPropertiesProvider provider = getPropertiesProvider(be); 
		if (provider==null) {
			createEmptyLabel(be);
			return;
		}
		
		String[] properties = provider.getProperties();
		if (properties!=null) {
			getAttributesParent();
			for (String property : properties) {
				bindProperty(be,property);
			}
		}
		
		if (getAttributesParent().getChildren().length==0) {
			// yech! ugly hack to hide the Attributes TWISTIE section if it's empty
			if (attributesComposite!=null) {
				attributesComposite.dispose();
				attributesComposite = null;
				attributesSection.dispose();
				attributesSection = null;
			}
		}
		redrawPage();
	}

	protected void bindReference(Composite parent, EObject object, EReference reference) {
		if (isModelObjectEnabled(object.eClass(), reference)) {
			if (parent==null)
				parent = getAttributesParent();
			
			if (reference.getEType() == PACKAGE.getExpression() || reference.getEType() == PACKAGE.getFormalExpression()) {
				String displayName = getBusinessObjectDelegate().getLabel(object, reference);
				Expression expression = (Expression)object.eGet(reference);
				if (expression==null) {
					expression = createModelObject(FormalExpression.class);
					InsertionAdapter.add(object, reference, expression);
				}
				AbstractDetailComposite composite = PropertiesCompositeFactory.INSTANCE.createDetailComposite(Expression.class, getAttributesParent(), SWT.BORDER);
				composite.setBusinessObject(expression);
				composite.setTitle(displayName);
			}
			else
				super.bindReference(parent, object, reference);
		}
	}
}
