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

package org.eclipse.bpmn2.modeler.ui.property.providers;

import org.eclipse.bpmn2.modeler.ui.Activator;
import org.eclipse.bpmn2.modeler.ui.IConstants;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.swt.graphics.Image;

/**
 * @author Bob Brodt
 *
 */
public class JavaMemberTreeNode extends TreeNode {

	/**
	 * @param modelObject
	 * @param isCondensed
	 */
	public JavaMemberTreeNode(Object modelObject, boolean isCondensed) {
		super(modelObject, isCondensed);
	}

	@Override
	public String getLabel() {
	    IMember member = (IMember)modelObject;
		String label = ""; //$NON-NLS-1$
		String name = member.getElementName();
//		int mod = member.getModifiers();
//		if ((mod & Modifier.PUBLIC)!=0)
//			label += "public ";
//		if ((mod & Modifier.PROTECTED)!=0)
//			label += "protected ";
//		if ((mod & Modifier.PRIVATE)!=0)
//			label += "private ";
//		if ((mod & Modifier.STATIC)!=0)
//			label += "static ";
		
		if (member instanceof IField) {
			IField f = (IField)member;
			try {
                label += Signature.getSignatureSimpleName(f.getTypeSignature()) + " "; //$NON-NLS-1$
            } catch (JavaModelException e) {
            }
		}
		if (member instanceof IMethod) {
		    IMethod m = (IMethod)member;
			try {
                label += Signature.getSignatureSimpleName(m.getReturnType()) + " "; //$NON-NLS-1$
            } catch (JavaModelException e) {
            }
		}
		label += name;
		if (member instanceof IMethod) {
		    IMethod m = (IMethod)member;
			if (m.getParameterTypes().length>0) {
				label += "("; //$NON-NLS-1$
				String[] p = m.getParameterTypes();
				for (int i=0; i<p.length; ++i) {
					label += Signature.getSignatureSimpleName(p[i]);
					if (i+1<p.length)
						label += ", "; //$NON-NLS-1$
				}
				label += ")"; //$NON-NLS-1$
			}
			else
				label += "()"; //$NON-NLS-1$
		}

		return label;
	}

	@Override
	public Image getImage() {
		if (modelObject instanceof IMethod)
			return Activator.getDefault().getImage(IConstants.ICON_JAVA_PUBLIC_METHOD_16);
		if (modelObject instanceof IField)
			return Activator.getDefault().getImage(IConstants.ICON_JAVA_PUBLIC_FIELD_16);
		return null;
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public Object[] getChildren() {
		return null;
	}
}
