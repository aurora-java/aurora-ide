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

package org.eclipse.bpmn2.modeler.ui.property.editors;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Import;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.ItemKind;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.TextAndButtonObjectEditor;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.core.validation.SyntaxCheckerUtils;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;

/**
 * This class implements a Data Structure editor for ItemDefinitions.
 * <p>
 * The ItemDefinition which is the object of this
 * {@link org.eclipse.bpmn2.modeler.core.merrimac.dialogs.ObjectEditor} will be
 * populated with the structureRef.
 */
public class ItemDefinitionStructureEditor extends TextAndButtonObjectEditor {

	Definitions definitions = null;
	ItemDefinition itemDefinition;
	String prefix = null;
	
	/**
	 * @param parent
	 * @param object
	 */
	public ItemDefinitionStructureEditor(AbstractDetailComposite parent, ItemDefinition itemDefinition) {
		super(parent, itemDefinition, Bpmn2Package.eINSTANCE.getItemDefinition_StructureRef());
		this.itemDefinition = itemDefinition;
		definitions = ModelUtil.getDefinitions(itemDefinition);
	}
	
	@Override
	protected void buttonClicked(int buttonId) {
		// Default button was clicked: open a text editor and allow editing of just the
		// data structure name part (the localpart) if the original structureRef contained
		// a namespace prefix.
		String text = getText();
		int index = text.indexOf(":"); //$NON-NLS-1$
		prefix = null;
		if (index>0) {
			prefix = text.substring(0,index);
			if (SyntaxCheckerUtils.isNCName(prefix)) {
				// this is a valid namespace prefix
				text = text.substring(index+1);
			}
			else
				prefix = null;
		}
		final ItemKind thisItemKind = itemDefinition.getItemKind();
		final boolean thisIsCollection = itemDefinition.isIsCollection();
		final Import thisImport = itemDefinition.getImport();
		
		IInputValidator validator = new IInputValidator() {
			@Override
			public String isValid(String newText) {
				if (newText==null || newText.isEmpty())
					return Messages.ItemDefinitionStructureEditor_DataStructureEmpty_Error;
				if (newText.contains(":") && prefix!=null) { //$NON-NLS-1$
					return Messages.ItemDefinitionStructureEditor_DataStructureInvalid_Error;
				}
				String thisText = (prefix!=null) ?
						prefix + ":" + newText : //$NON-NLS-1$
						newText;
				for (ItemDefinition that : ModelUtil.getAllRootElements(definitions, ItemDefinition.class)) {
					String thatText = ModelUtil.getStringWrapperTextValue(that.getStructureRef());
					if (
							thisText.equals(thatText) &&
							that.getItemKind() == thisItemKind &&
							that.isIsCollection() == thisIsCollection &&
							that.getImport() == thisImport 
					) {
						return Messages.ItemDefinitionStructureEditor_DuplicateItemDefinition_Error;
					}
				}
				return null;
			}
		};

		InputDialog dialog = new InputDialog(
				parent.getShell(),
				Messages.ItemDefinitionStructureEditor_EditDataStructure_Title,
				Messages.ItemDefinitionStructureEditor_EditDataStructure_Prompt,
				text,
				validator);
		
		if (dialog.open()==Window.OK){
			text = dialog.getValue();
			if (prefix!=null)
				text = prefix + ":" + text; //$NON-NLS-1$
			if (!text.equals( getText() )) {
				setValue(text);
			}
		}
	}
}
