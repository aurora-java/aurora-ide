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

import java.util.List;

import org.eclipse.bpmn2.modeler.core.preferences.Bpmn2Preferences;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.properties.tabbed.AbstractTabDescriptor;
import org.eclipse.ui.views.properties.tabbed.TabContents;

/**
 * Target Runtime Extension Descriptor class for Property Tabs.
 * Instances of this class correspond to <propertyTab> extension elements in the extension's plugin.xml
 * See the description of the "property" element in the org.eclipse.bpmn2.modeler.runtime extension point schema.
 * 
 * Unfortunately, this class must extend from AbstractTabDescriptor, so it is not a BaseRuntimeExtensionDescriptor
 * like all of the other extension descriptor classes.
 */
public class PropertyTabDescriptor extends AbstractTabDescriptor implements IRuntimeExtensionDescriptor {

	public final static String EXTENSION_NAME = "propertyTab"; //$NON-NLS-1$

	protected String id;
	protected String category;
	protected String label;
	protected String afterTab = null;
	protected String replaceTab = null;
	protected boolean indented = false;
	protected Image image = null;
	protected boolean popup = true;
	protected TargetRuntime targetRuntime;
	protected final IConfigurationElement configurationElement;
	protected IFile configFile;

	public PropertyTabDescriptor(IConfigurationElement e) {
		configurationElement = e;
		id = e.getAttribute("id"); //$NON-NLS-1$
		category = e.getAttribute("category"); //$NON-NLS-1$
		if (category==null || category.isEmpty())
			category = "BPMN2"; //$NON-NLS-1$
		label = e.getAttribute("label"); //$NON-NLS-1$
		afterTab = e.getAttribute("afterTab"); //$NON-NLS-1$
		replaceTab = e.getAttribute("replaceTab"); //$NON-NLS-1$
		String s = e.getAttribute("indented"); //$NON-NLS-1$
		indented = s!=null && s.trim().equalsIgnoreCase("true"); //$NON-NLS-1$
		s = e.getAttribute("popup"); //$NON-NLS-1$
		if (s!=null && s.trim().equalsIgnoreCase("false")) //$NON-NLS-1$
			popup = false;

		Bpmn2SectionDescriptor sd = new Bpmn2SectionDescriptor(this,e);
	}

	private PropertyTabDescriptor(PropertyTabDescriptor other) {
		this.configurationElement = other.configurationElement;
		this.id = other.id;
		if (other.category==null || other.category.isEmpty() )
			other.category = "BPMN2"; //$NON-NLS-1$
		this.category = other.category;
		this.label = other.label;
	}

	public void dispose() {
		List<IRuntimeExtensionDescriptor> list = targetRuntime.getRuntimeExtensionDescriptors(getExtensionName());
		list.remove(this);
		// notify any open editors that property tabs have changed
		PropertyChangeEvent event = new PropertyChangeEvent(this, Bpmn2Preferences.PREF_SHOW_ADVANCED_PROPERTIES, null, new Object());
		for (Bpmn2Preferences p : Bpmn2Preferences.getInstances(targetRuntime)) {
			p.propertyChange(event);
		}
	}
	
	public String getExtensionName() {
		return EXTENSION_NAME;
	}

	public void setRuntime(TargetRuntime targetRuntime) {
		this.targetRuntime = targetRuntime;
		List<IRuntimeExtensionDescriptor> list = targetRuntime.getRuntimeExtensionDescriptors(getExtensionName());
		list.add(this);
	}

	public TargetRuntime getRuntime() {
		return targetRuntime;
	}
	
	public IFile getConfigFile() {
		return configFile;
	}

	public void setConfigFile(IFile configFile) {
		this.configFile = configFile;
	}

	public String getRuntimeId() {
		return targetRuntime==null ? null : targetRuntime.getId();
	}
	
	@Override
	public String getCategory() {
		return category;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return label;
	}
	
	public boolean isPopup() {
		return popup;
	}

	public void setPopup(boolean popup) {
		this.popup = popup;
	}

	@Override
	public String getAfterTab() {
		if (afterTab==null || afterTab.trim().length()==0)
			return super.getAfterTab();
		return afterTab;
	}

	@Override
	public Image getImage() {
		if (image==null)
			return super.getImage();
		return image;
	}

	@Override
	public TabContents createTab() {
		// TODO Auto-generated method stub
		return super.createTab();
	}

	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return super.isSelected();
	}

	@Override
	public void setSectionDescriptors(List sectionDescriptors) {
		// TODO Auto-generated method stub
		super.setSectionDescriptors(sectionDescriptors);
	}

	@Override
	public boolean isIndented() {
		return indented;
	}

	@Override
	public Object clone() {
		PropertyTabDescriptor td = new PropertyTabDescriptor(this);
		td.afterTab = this.afterTab;
		td.replaceTab = this.replaceTab;
		if (image!=null)
			td.image = new Image(Display.getDefault(), this.image, SWT.IMAGE_COPY);
		td.indented = this.indented;
		td.targetRuntime = this.targetRuntime;
		td.configFile = this.configFile;
//		for (Bpmn2SectionDescriptor sd : (List<Bpmn2SectionDescriptor>)getSectionDescriptors()) {
//			clone.getSectionDescriptors().add( new Bpmn2SectionDescriptor(sd) );
//		}
		return td;
	}

	public PropertyTabDescriptor copy() {
		PropertyTabDescriptor td = new PropertyTabDescriptor(this);
		td.id += td.hashCode();
		td.afterTab = this.afterTab;
		td.replaceTab = this.replaceTab;
		if (image!=null)
			td.image = new Image(Display.getDefault(), this.image, SWT.IMAGE_COPY);
		td.indented = this.indented;
		td.targetRuntime = this.targetRuntime;
		td.popup = this.popup;
		td.image = this.image;
		td.configFile = this.configFile;
		for (Bpmn2SectionDescriptor sd : (List<Bpmn2SectionDescriptor>)getSectionDescriptors()) {
			td.getSectionDescriptors().add(new Bpmn2SectionDescriptor(td, sd));
		}

		return td;
	}

	public String getReplaceTab() {
		if (replaceTab==null || replaceTab.trim().length()==0)
			return null;
		return replaceTab;
	}
	
	public boolean isReplacementForTab(String id) {
		String replacements = getReplaceTab();
		if (replacements!=null) {
			String[] rep = replacements.split(" "); //$NON-NLS-1$
			for (String r : rep) {
				if (r.equals(id))
					return true;
			}
		}
		return false;
	}
}