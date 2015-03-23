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
package org.eclipse.bpmn2.modeler.core.runtime;

import org.eclipse.bpmn2.modeler.core.features.ICustomElementFeatureContainer;
import org.eclipse.bpmn2.modeler.core.runtime.CustomTaskImageProvider.IconSize;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

public class CustomTaskDescriptor extends ModelExtensionDescriptor {

	public final static String EXTENSION_NAME = "customTask"; //$NON-NLS-1$

	protected ICustomElementFeatureContainer featureContainer;
	protected String category;
	protected String icon;
	protected String propertyTabs[];
	protected boolean permanent;
	
	public CustomTaskDescriptor(IConfigurationElement e) {
		super(e);
		category = e.getAttribute("category"); //$NON-NLS-1$
		icon = e.getAttribute("icon"); //$NON-NLS-1$
		String tabs = e.getAttribute("propertyTabs"); //$NON-NLS-1$
		if (tabs!=null) {
			propertyTabs = tabs.split(" "); //$NON-NLS-1$
		}
		try {
			featureContainer = (ICustomElementFeatureContainer) e.createExecutableExtension("featureContainer"); //$NON-NLS-1$
			featureContainer.setCustomTaskDescriptor(this);
			featureContainer.setId(id);
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} //$NON-NLS-1$
		setPermanent(true);
	}
	
	@Deprecated
	public CustomTaskDescriptor(String id, String name) {
		super(id,name);
	}
	
	public String getExtensionName() {
		return EXTENSION_NAME;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public ICustomElementFeatureContainer getFeatureContainer() {
		return featureContainer;
	}

	public void setFeatureContainer(ICustomElementFeatureContainer featureContainer) {
		this.featureContainer = featureContainer;
	}
	
	public boolean isPermanent() {
		return permanent;
	}

	public void setPermanent(boolean permanent) {
		this.permanent = permanent;
	}
	
	public String[] getPropertyTabs() {
		if (propertyTabs==null)
			propertyTabs = new String[0];
		return propertyTabs;
	}

	public String getImageId(String icon, IconSize size) {
		if (icon != null && icon.trim().length() > 0) {
			String prefix = featureContainer.getClass().getPackage().getName();
			return prefix + "." + icon.trim() + "." + size.value; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return null;
	}
	
	public String getImagePath(String icon, IconSize size) {
		if (icon != null && icon.trim().length() > 0) {
			String prefix = featureContainer.getClass().getPackage().getName();
			return CustomTaskImageProvider.ICONS_FOLDER + size.value + "/" + icon.trim(); //$NON-NLS-1$
		}
		return null;
	}
}