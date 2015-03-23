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
 * @author Ivar Meikas
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.preferences;

import java.util.ArrayList;

import org.eclipse.bpmn2.modeler.core.preferences.AbstractPropertyChangeListenerProvider;
import org.eclipse.emf.ecore.ENamedElement;

public class ModelEnablementTreeEntry extends AbstractPropertyChangeListenerProvider {

	private String name;
	private ENamedElement element;
	private Boolean enabled;
	private ModelEnablementTreeEntry parent;
	private ArrayList<ModelEnablementTreeEntry> children;
	// "friends" are references to this ModelEnablementTreeEntry.
	private ArrayList<ModelEnablementTreeEntry> friends;
	private static ArrayList<ModelEnablementTreeEntry> EMPTY_LIST = new ArrayList<ModelEnablementTreeEntry>();

	public ModelEnablementTreeEntry() {
	}

	public ModelEnablementTreeEntry(ENamedElement element, ModelEnablementTreeEntry parent) {
		setElement(element);
		this.parent = parent;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		if (element!=null)
			return element.getName();
		return name==null ? "" : name; //$NON-NLS-1$
	}

	public String getPreferenceName() {
		if (parent == null || parent.getElement()==null) {
			return getName();
		} else {
			return parent.getPreferenceName() + "." + getName(); //$NON-NLS-1$
		}
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public void setSubtreeEnabled(Boolean enabled) {
		setEnabled(enabled);
		for (ModelEnablementTreeEntry child : getChildren()) {
			child.setSubtreeEnabled(enabled);
		}
	}

	public int getSubtreeSize() {
		return getSubtreeSize(this);
	}
	
	private static int getSubtreeSize(ModelEnablementTreeEntry parent) {
		int size = 0;
		for (ModelEnablementTreeEntry child : parent.getChildren()) {
			++size;
			size += getSubtreeSize(child);
		}
		return size;
	}
	
	public int getSubtreeEnabledCount() {
		return getSubtreeEnabledCount(this);
	}
	
	private static int getSubtreeEnabledCount(ModelEnablementTreeEntry parent) {
		int count = 0;
		for (ModelEnablementTreeEntry child : parent.getChildren()) {
			if (child.getEnabled())
				++count;
			count += getSubtreeEnabledCount(child);
		}
		return count;
	}
	
	public void setElement(ENamedElement element) {
		if (element!=null)
			this.name = element.getName();
		this.element = element;
	}

	public ENamedElement getElement() {
		return element;
	}

	public void setChildren(ArrayList<ModelEnablementTreeEntry> children) {
		this.children = children;
	}

	public ArrayList<ModelEnablementTreeEntry> getChildren() {
		if (children==null)
			return EMPTY_LIST;
		return children;
	}

	public void setParent(ModelEnablementTreeEntry parent) {
		this.parent = parent;
	}

	public ModelEnablementTreeEntry getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return "ModelEnablementTreeEntry [element=" + getName() + ", enabled=" + enabled + ", children=" + children + ", parent=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ (parent == null ? "null" : parent.getName()) + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public boolean hasChildren() {
		return children != null && children.size() > 0;

	}
	
	public boolean hasFriends() {
		return friends!=null && friends.size()>0;
	}
	
	public ArrayList<ModelEnablementTreeEntry> getFriends() {
		if (friends==null)
			return EMPTY_LIST;
		return friends;
	}
	
	public void addFriend(ModelEnablementTreeEntry friend) {
		if (friend!=null) {
			if (friends==null)
				friends = new ArrayList<ModelEnablementTreeEntry>();
			if (!friends.contains(friend))
				friends.add(friend);
		}
	}
}