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
package org.eclipse.bpmn2.modeler.ui.util;

import java.lang.reflect.Field;

import org.eclipse.bpmn2.modeler.ui.Activator;
import org.eclipse.bpmn2.modeler.ui.IConstants;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class PropertyUtil {

	public static String deCamelCase(String string) {
		return string.replaceAll("([A-Z][a-z])", " $0").substring(1); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static void layoutAllParents(Composite child) {
		Composite parent = child;
		while (parent!=null && parent.getParent() instanceof Composite) {
			parent = parent.getParent(); 
			parent.layout();
		}
	}
	
	// Debugging utilities for widget trees.
	public static void check(Control control) {
		String name = control.getClass().getSimpleName();
		if (control.isDisposed()) {
			System.err.println(name+" disposed!"); //$NON-NLS-1$
			return;
		}
//		if (control instanceof Composite) {
//			((Composite)control).layout(true);
//		}
//		control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
//		Point sz = control.getSize();
//		if (sz.x==0 || sz.y==0)
//			System.err.println(name+" zero size!");
	}

	public static void dump(Composite parent, String comment) {
		System.out.println(comment);
		int i = 1;
		System.out.println("parent="+parent.hashCode()); //$NON-NLS-1$
		check(parent);

		Composite p = parent.getParent();
		while (p!=null) {
			check(p);
			p = p.getParent();
			++i;
		}
		dump(parent,0);
	}
	
	public static void dump(Composite parent, int indent) {
		Control[] kids = parent.getChildren();
		for (Control k : kids) {
			for (int i=0; i<indent; ++i)
				System.out.print("|"); //$NON-NLS-1$
			System.out.print(" "+k+" layoutData="+k.getLayoutData()); //$NON-NLS-1$ //$NON-NLS-2$
			if (k instanceof Composite)
				System.out.print(" layout="+((Composite)k).getLayout()); //$NON-NLS-1$
			check(k);
			
			if (k instanceof Label) {
				System.out.print(((Label)k).getText());
			}
			System.out.println(""); //$NON-NLS-1$
			if (k instanceof Composite) {
				dump((Composite)k, indent+1);
			}
		}
	}
	
	public static Image getImage(EObject element) {
		return getImage(element.eClass().getName());
	}
	
	public static Image getImage(String name) {
		String field = "ICON_" + name.toUpperCase(); //$NON-NLS-1$
		Field f;
		try {
			f = IConstants.class.getField(field);
			if (f!=null)
				return Activator.getDefault().getImage((String)f.get(null));
		} catch (Exception e) {
		}
		return null;
	}
}
