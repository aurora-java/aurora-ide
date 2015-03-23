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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.modeler.ui.Activator;
import org.eclipse.bpmn2.modeler.ui.IConstants;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.graphics.Image;

/**
 * @author Bob Brodt
 *
 */
public class JavaTypeTreeNode extends TreeNode {

	/**
	 * @param modelObject
	 * @param isCondensed
	 */
	public JavaTypeTreeNode(Object modelObject, boolean isCondensed) {
		super(modelObject, isCondensed);
	}

	@Override
	public String getLabel() {
		IType c = (IType)modelObject;
		return c.getElementName() + " - " + c.getFullyQualifiedName('.'); //$NON-NLS-1$
	}

	@Override
	public Image getImage() {
	    IType c = (IType)modelObject;
		try {
            if (c.isInterface())
            	return Activator.getDefault().getImage(IConstants.ICON_JAVA_INTERFACE_16);
        } catch (JavaModelException e) {
        }
		return Activator.getDefault().getImage(IConstants.ICON_JAVA_CLASS_16);
	}

	@Override
	public boolean hasChildren() {
		return false; //getChildren().length>0;
	}

	@Override
	public Object[] getChildren() {
		List<TreeNode> kids = new ArrayList<TreeNode>();
		try {
			IType c = (IType)modelObject;
			for (IField f : c.getFields()) {
				if (Flags.isPublic(f.getFlags()))
					kids.add(new JavaMemberTreeNode(f,isCondensed));
			}
			for (IMethod m : c.getMethods()) {
				if ((Flags.isPublic(m.getFlags()) && !m.isConstructor()) || c.isInterface())
					kids.add(new JavaMemberTreeNode(m,isCondensed));
			}
			for (IType ic : c.getTypes()) {
				if (Flags.isPublic(ic.getFlags()))
					kids.add(new JavaTypeTreeNode(ic,isCondensed));
			}
		}
		catch (Exception e) {
		}
		return kids.toArray(new Object[kids.size()]);
	}
}
