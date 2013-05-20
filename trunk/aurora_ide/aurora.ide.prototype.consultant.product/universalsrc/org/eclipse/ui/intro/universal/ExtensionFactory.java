/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.intro.universal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.internal.intro.universal.WelcomeCustomizationPreferencePage;

import aurora.ide.prototype.consultant.product.Activator;

/**
 * Factory for the intro's public extensions.
 * <p>
 * This allows the extensions to be made available for use by RCP applications without exposing
 * their concrete implementation classes.
 * </p>
 * <p>
 * Currently supported plug-in extensions:
 * <ul>
 * <li>welcomeCustomization - a preference page that allows user customization of the shared
 * Welcome.</li>
 * </ul>
 * <p>This class should be referenced in extensions but not subclassed
 * or instantiated programmatically.
 * 
 * @since 3.2
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */

public class ExtensionFactory implements IExecutableExtensionFactory, IExecutableExtension {

	private String id;
	private IConfigurationElement config;
	private String propertyName;
	private static final String WELCOME_CUSTOMIZATION_PREFERENCE_PAGE = "welcomeCustomization"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IExecutableExtensionFactory#create()
	 */
	public Object create() throws CoreException {
		if (WELCOME_CUSTOMIZATION_PREFERENCE_PAGE.equals(id))
			return configure(new WelcomeCustomizationPreferencePage());

		throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0,
				"Unknown id in data argument for " + getClass(), null)); //$NON-NLS-1$        		
	}

	private Object configure(Object obj) throws CoreException {
		if (obj instanceof IExecutableExtension) {
			((IExecutableExtension) obj).setInitializationData(config, propertyName, null);
		}
		return obj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement,
	 *      java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {
		if (data instanceof String)
			id = (String) data;
		else
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0,
					"Data argument must be a String for " + getClass(), null)); //$NON-NLS-1$
		this.config = config;
		this.propertyName = propertyName;
	}
}