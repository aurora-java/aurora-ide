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
package org.eclipse.bpmn2.modeler.ui.property.diagrams;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Import;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.ItemKind;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractBpmn2PropertySection;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.DefaultDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.ComboObjectEditor;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.ObjectEditor;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.TextObjectEditor;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.core.utils.NamespaceUtil;
import org.eclipse.bpmn2.modeler.ui.property.editors.ItemDefinitionStructureEditor;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class ItemDefinitionDetailComposite extends DefaultDetailComposite {

	public ItemDefinitionDetailComposite(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @param section
	 */
	public ItemDefinitionDetailComposite(AbstractBpmn2PropertySection section) {
		super(section);
	}

	@Override
	public AbstractPropertiesProvider getPropertiesProvider(EObject object) {
		if (propertiesProvider==null) {
			propertiesProvider = new AbstractPropertiesProvider(object) {
				String[] properties = new String[] {
						"itemKind", //$NON-NLS-1$
						"isCollection", //$NON-NLS-1$
						"structureRef", //$NON-NLS-1$
						"documentation", //$NON-NLS-1$
						// this thing is transient so it won't be serialized; no point in allowing user to set it
						// "import"
				};
				
				@Override
				public String[] getProperties() {
					return properties; 
				}
			};
		}
		return propertiesProvider;
	}

	protected void bindAttribute(Composite parent, EObject object, EAttribute attribute, String label) {
		if ("itemKind".equals(attribute.getName())) { //$NON-NLS-1$
			if (isModelObjectEnabled(object.eClass(), attribute)) {

				if (parent==null)
					parent = getAttributesParent();
				
				if (label==null)
					label = ExtendedPropertiesProvider.getLabel(object, attribute);
				
				ObjectEditor editor = new ComboObjectEditor(this,object,attribute) {
					protected boolean setValue(final Object result) {
						super.setValue(result);
						Display.getCurrent().syncExec( new Runnable() {
							@Override
							public void run() {
								setBusinessObject(getBusinessObject());
							}
						});
						return true;
					}
				};
				
				editor.createControl(parent,label);
			}
		}
		else
			super.bindAttribute(parent, object, attribute, label);
	}
	
	@Override
	protected void bindReference(Composite parent, EObject object, EReference reference) {
		if ("structureRef".equals(reference.getName()) && //$NON-NLS-1$
				isModelObjectEnabled(object.eClass(), reference)) {
			
			if (parent==null)
				parent = getAttributesParent();
			
			final ItemDefinition itemDefinition = (ItemDefinition)object;
			String displayName = ExtendedPropertiesProvider.getLabel(object, reference);
			
			if (itemDefinition.getItemKind().equals(ItemKind.INFORMATION)) {
				// This is an Information item: enforce constraints on this thing
				ItemDefinitionStructureEditor editor = new ItemDefinitionStructureEditor(this,itemDefinition);
				editor.createControl(parent,displayName);
			}
			else {
				// This is a Physical item: anything goes
				ObjectEditor editor = new TextObjectEditor(this,object,reference) {
					@Override
					protected boolean setValue(Object result) {
						return super.setValue(ModelUtil.createStringWrapper((String)result));
					}
				};
				editor.createControl(parent,displayName);
			}

			// create a Twistie Section for read-only information about this ItemDefinition 
			Composite container = createSectionComposite(this, Messages.ItemDefinitionDetailComposite_DefinedIn_Title);
			Object structureRef = itemDefinition.getStructureRef();
			Import imp = itemDefinition.getImport();
			if (imp!=null) {
				// the thing is defined in an Import: display Import location, type and namespace
				createText(container, Messages.ItemDefinitionDetailComposite_Import_Label, imp.getLocation());
				createText(container, Messages.ItemDefinitionDetailComposite_Type_Label, imp.getImportType());
				createText(container, Messages.ItemDefinitionDetailComposite_Namespace_Label, imp.getNamespace());
			}
			else if (ModelUtil.isStringWrapper(structureRef)) {
				// the thing is defined within the namespace of the type language,
				// or some other namespace defined within the document: display
				// the namespace information
				String string = ModelUtil.getStringWrapperTextValue(structureRef);
				String prefix = ""; //$NON-NLS-1$
				int index = string.indexOf(":"); //$NON-NLS-1$
				if (index>0)
					prefix = string.substring(0,index);
				Resource resource = ModelUtil.getResource(object);
				String namespace = NamespaceUtil.getNamespaceForPrefix(resource, prefix);
				if (namespace!=null)
					createText(container, Messages.ItemDefinitionDetailComposite_Namespace_Label, namespace);
				else {
					Definitions definitions = ModelUtil.getDefinitions(resource);
					createText(container, Messages.ItemDefinitionDetailComposite_TypeLanguage_Label, definitions.getTypeLanguage());
				}
			}
		}
		else
			super.bindReference(parent, object, reference);
	}
}