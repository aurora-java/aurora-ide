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
package org.eclipse.bpmn2.modeler.ui.property.tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.OutputSet;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.TableColumn;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

public class IoSetNameColumn extends TableColumn {

	public IoSetNameColumn(EObject o, EStructuralFeature f) {
		super(o, f);
		setEditable(false);
	}

	@Override
	public String getText(Object object) {
		String text = null;
		EObject o = (EObject)object;
		if (feature instanceof EAttribute) {
			text = (String)o.eGet(feature);
		}
		else if (feature instanceof EReference) {
			Object v = o.eGet(feature);
			if (v instanceof List){
				// Well this is interesting...I posted a message here about this problem:
				// http://www.eclipse.org/forums/index.php/m/918158/#msg_918158
				// Because both of the reference lists InputSet.outputSetRefs and OutputSet.inputSetRefs
				// are EObjectWithInverseEList, the following XML will create TWO entries in each of these
				// reference lists:
				//
				// <bpmn2:inputSet id="InputSet_2" name="Input Set 2">
				//   <bpmn2:outputSetRefs>OutputSet_1</bpmn2:outputSetRefs>
				// </bpmn2:inputSet>
				// <bpmn2:outputSet id="OutputSet_1" name="Output Set 1">
				//   <bpmn2:inputSetRefs>InputSet_2</bpmn2:inputSetRefs>
				// </bpmn2:outputSet>
				//
				// I wonder if one or the other of these lists should just be a simple EObjectEList?
				// Anyway, to avoid duplicating these list names in the table cell text, we'll just
				// use a Hashset to collect them all and then serialize the strings.
				//
				// There's also a workaround in Bpmn2ModelerXmlHelper to avoid creating duplicates.
				//
				HashSet<String> names = new HashSet<String>();
				for ( Object e : (List)v) 
				{
					String n = null;
					if (e instanceof InputSet) {
						n = ((InputSet)e).getName();
					}
					else if (e instanceof OutputSet) {
						n = ((OutputSet)e).getName();
					}
					if (n!=null)
						names.add(n);
				}
				for (String n : names) {
					if (text==null)
						text = n;
					else
						text += ", " + n; //$NON-NLS-1$

				}
			}
		}
		if (text==null)
			return ""; //$NON-NLS-1$
		return text;
	}
}