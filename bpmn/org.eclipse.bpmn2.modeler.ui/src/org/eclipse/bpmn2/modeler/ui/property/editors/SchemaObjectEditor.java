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

import javax.xml.namespace.QName;

import org.eclipse.bpmn2.Import;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.TextAndButtonObjectEditor;
import org.eclipse.bpmn2.modeler.core.utils.NamespaceUtil;
import org.eclipse.bpmn2.modeler.ui.editor.BPMN2Editor;
import org.eclipse.bpmn2.modeler.ui.property.dialogs.SchemaSelectionDialog;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.wst.wsdl.Fault;
import org.eclipse.wst.wsdl.Input;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.Output;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.wst.wsdl.PortType;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;

/**
 * This class implements a Schema Browser editor which consists of an editable
 * Text field and a "Browse" button. The button allows for selection of a BPMN2
 * Import and schema element defined in the Import. The button uses the
 * {@link SchemaSelectionDialog} which can be used to either select an existing
 * Import or add a new Import to the BPMN2 file.
 * <p>
 * The ItemDefinition which is the object of this {@link ObjectEditor} will be
 * populated with the structureRef (the selected schema element) and the
 * import selected in the {@link SchemaSelectionDialog}
 */
public class SchemaObjectEditor extends TextAndButtonObjectEditor {

	/**
	 * @param parent
	 * @param object
	 * @param feature
	 */
	public SchemaObjectEditor(AbstractDetailComposite parent, EObject object, EStructuralFeature feature) {
		super(parent, object, feature);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.bpmn2.modeler.ui.property.editors.ObjectEditor#createControl
	 * (org.eclipse.swt.widgets.Composite, java.lang.String, int)
	 */
	@Override
	protected Control createControl(Composite composite, String label, int style) {
		super.createControl(composite, label, style);
		// the Text field should be editable
		text.setEditable(true);
		// and change the "Edit" button to a "Browse" to make it clear that
		// an XML type can be selected from the imports 
		defaultButton.setText(Messages.SchemaObjectEditor_Browse_Button);
		return text;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.ui.property.editors.TextAndButtonObjectEditor#buttonClicked()
	 */
	@Override
	protected void buttonClicked(int buttonId) {
		Object[] result = showSchemaSelectionDialog(parent, object);
		if (result.length==2) {
			setValue((String)result[0]);
			if (object instanceof ItemDefinition) {
				((ItemDefinition)object).setImport((Import)result[1]);
			}
		}
	}
	
	public static Object[] showSchemaSelectionDialog(Composite parent, EObject object) {
		// "Browse" button was clicked: open a {@link SchemaSelectionDialog} and
		// get the selected schema element and Import reference.
		SchemaSelectionDialog dialog = new SchemaSelectionDialog(parent.getShell(), object);

		if (dialog.open() == Window.OK) {
			Resource resource = object.eResource();
			Object element = dialog.getResult()[0];
			Import importRef = (Import)dialog.getResult()[1];
			String selectionPath = dialog.getSelectionPath();
			String value = ""; //$NON-NLS-1$
			String selectionType = ""; //$NON-NLS-1$

			if (element instanceof PortType) {
				// the element is a WSDL PortType
				PortType portType = (PortType)element;
				QName qname = portType.getQName();
				String prefix = NamespaceUtil.getPrefixForNamespace(resource, qname.getNamespaceURI());
				if (prefix==null)
					prefix = NamespaceUtil.addNamespace(resource, qname.getNamespaceURI());
				if (prefix!=null)
					value = prefix + ":"; //$NON-NLS-1$
				value += qname.getLocalPart();
				selectionType = Messages.SchemaObjectEditor_WSDL_Port;
			}
			if (element instanceof Operation) {
				// the element is a WSDL Operation
				selectionType = Messages.SchemaObjectEditor_WSDL_Operation;
			}
			if (element instanceof Input) {
				// the element is a WSDL Input
				Input input = (Input)element;
				element = input.getMessage();
				selectionType = Messages.SchemaObjectEditor_WSDL_Input;
			}
			if (element instanceof Output) {
				// the element is a WSDL Output
				Output output = (Output)element;
				element = output.getMessage();
				selectionType = Messages.SchemaObjectEditor_WSDL_Output;
			}
			if (element instanceof Fault) {
				// the element is a WSDL Fault
				Fault fault = (Fault)element;
				element = fault.getMessage();
				selectionType = Messages.SchemaObjectEditor_WSDL_Fault;
			}
			if (element instanceof Part) {
				// the element is a WSDL Message Part
				Part part = (Part)element;
				element = part.getElementDeclaration();
				selectionType = Messages.SchemaObjectEditor_WSDL_Message_Part;
			}
			if (element instanceof Message) {
				// the element is a WSDL Message
				Message message = (Message)element;
				QName qname = message.getQName();
				String prefix = NamespaceUtil.getPrefixForNamespace(resource, qname.getNamespaceURI());
				if (prefix==null)
					prefix = NamespaceUtil.addNamespace(resource, qname.getNamespaceURI());
				if (prefix!=null)
					value = prefix + ":"; //$NON-NLS-1$
				value += qname.getLocalPart();
				selectionType = Messages.SchemaObjectEditor_WSDL_Message;
			}
			if (element instanceof XSDAttributeDeclaration) {
				// the element is a XSD attribute
				selectionType = Messages.SchemaObjectEditor_XML_Attribute;
			}
			
			if (element instanceof XSDElementDeclaration) {
				// the element is a XSD element
				XSDElementDeclaration decl = (XSDElementDeclaration)element;
				XSDSchema schema = getContainingSchema(decl);
				String ns = schema.getTargetNamespace();
				if (ns==null) {
					XSDTypeDefinition type = decl.getTypeDefinition();
					if (type!=null) {
						ns = type.getSchema().getTargetNamespace();
					}
				}
				String prefix = NamespaceUtil.getPrefixForNamespace(resource, ns);
				if (prefix!=null)
					value = prefix + ":"; //$NON-NLS-1$
				value += selectionPath;
			}
			if (element instanceof XSDTypeDefinition) {
				// the element is a XSD type
				XSDTypeDefinition type = (XSDTypeDefinition)element;
				XSDSchema schema = getContainingSchema(type);
				String ns = schema.getTargetNamespace();
				String prefix = NamespaceUtil.getPrefixForNamespace(resource, ns);
				if (prefix!=null)
					value = prefix + ":"; //$NON-NLS-1$
				value += selectionPath;
			}
			if (element instanceof XSDSchema) {
				// the element is a XSD schema
				XSDSchema schema = (XSDSchema)element;
				String prefix = NamespaceUtil.getPrefixForNamespace(resource, schema.getTargetNamespace());
				if (prefix!=null)
					value = prefix + ":"; //$NON-NLS-1$
				value += "schema"; //$NON-NLS-1$
			}
			if (element instanceof Process) {
				// the element is a BPMN2 Process
				Process process = (Process)element;
				process.getSupportedInterfaceRefs();
			}
			if (element instanceof IType) {
				// the element is a Java type
				value = ((IType)element).getFullyQualifiedName('.');
			}
			if (value.isEmpty()) {
				MessageDialog.openWarning(parent.getShell(),
					Messages.SchemaObjectEditor_Invalid_Selection_Title,
					NLS.bind(Messages.SchemaObjectEditor_Invalid_Selection_Message,selectionType)
				);
			}
			else {
				return new Object[] {value, importRef};
			}
		}
		return new Object[] {};
	}
	
	private static XSDSchema getContainingSchema(EObject object) {
		EObject container = object.eContainer();
		if (container instanceof XSDSchema)
			return (XSDSchema) container;
		if (container!=null)
			return getContainingSchema(container);
		return null;
	}
}
