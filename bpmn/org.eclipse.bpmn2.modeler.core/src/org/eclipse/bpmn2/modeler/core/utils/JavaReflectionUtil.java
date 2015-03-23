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

package org.eclipse.bpmn2.modeler.core.utils;

import org.eclipse.core.internal.registry.osgi.OSGIUtils;
import org.osgi.framework.Bundle;

/**
 * As the name implies, this is a static class of java reflection utilities
 * @author Bob Brodt
 */
public class JavaReflectionUtil {
	
	/**
	 * Find the class with the given simple name in a package hierarchy.
	 * The object is used as the starting point (deepest level)
	 * and the search continues up the package hierarchy.
	 * 
	 * @param object - any object in a package
	 * @param simpleName - simple (non-qualified) class name to search for
	 * @return - the class or null if not found
	 */
	public static Class findClass(Object object, String simpleName) {
		Class clazz = object.getClass();
		do {
			Class result = findClass(clazz, simpleName);
			if (result!=null)
				return result;
			clazz = clazz.getSuperclass();
		}
		while (clazz!=null);
		try {
			// last resort: try the UI plugin, this is where the Messages class is defined
			Bundle b = OSGIUtils.getDefault().getBundle("org.eclipse.bpmn2.modeler.ui"); //$NON-NLS-1$
			clazz = b.loadClass("org.eclipse.bpmn2.modeler.ui.Messages"); //$NON-NLS-1$
			Class result = findClass(clazz, simpleName);
			if (result!=null)
				return result;
		} catch (Exception e) {
		}
		return null;
	}
	
	public static Class findClass(Class clazz, String simpleName) {
		ClassLoader cl = clazz.getClassLoader();
		String packageName = clazz.getPackage().getName();
		int index;
		while ((index = packageName.lastIndexOf(".")) != -1) { //$NON-NLS-1$
			String className = packageName + "." + simpleName;  //$NON-NLS-1$
			try {
				return Class.forName(className, true, cl);
			} catch (ClassNotFoundException e) {
			}
			packageName = packageName.substring(0, index);
		}
		return null;
		
	}
}
