/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013 Red Hat, Inc.
 * All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.runtime;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Target Runtime Extension Descriptor class for Graphical Tool Palette
 * definitions. A Tool Palette must reference a
 * {@link ModelEnablementDescriptor} class instance by its ID. The Model
 * Enablement set is used to filter the BPMN2 elements that are available on
 * this Tool Palette.
 * 
 * Instances of this class correspond to <toolPalette> extension elements in the
 * extension's plugin.xml See the description of the "toolPalette" element in
 * the org.eclipse.bpmn2.modeler.runtime extension point schema.
 */
public class ToolPaletteDescriptor extends BaseRuntimeExtensionDescriptor {

	public final static String EXTENSION_NAME = "toolPalette"; //$NON-NLS-1$

	public final static String TOOLPART_ID = "ToolPartID"; //$NON-NLS-1$
	public final static String TOOLPART_OPTIONAL = "ToolPartOptional"; //$NON-NLS-1$
	public final static String DEFAULT_PALETTE_ID = "org.bpmn2.modeler.toolpalette.default.categories"; //$NON-NLS-1$
	
	// The Drawers
	public static class CategoryDescriptor {
		private ToolPaletteDescriptor parent;
		private String id;
		// these are used to sort the categories
		private String before;
		private String after;
		private String name;
		private String description;
		private String icon;
		private String fromPalette;
		private List<ToolDescriptor> tools = new ArrayList<ToolDescriptor>();
		
		public CategoryDescriptor(ToolPaletteDescriptor parent, String id, String name, String description, String icon) {
			this.parent = parent;
			this.id = id;
			this.name = name;
			this.description = description;
			this.icon = icon;
		}
		
		public ToolDescriptor addTool(String id, String name, String description, String icon, String object) {
			ToolDescriptor tool = new ToolDescriptor(this, id, name, description, icon, object);
			tools.add(tool);
			return tool;
		}
		
		public ToolDescriptor addTool(String id, String name, String description, String icon) {
			ToolDescriptor tool = new ToolDescriptor(this, id, name, description, icon);
			tools.add(tool);
			return tool;
		}
		
		public List<ToolDescriptor> getTools() {
			return tools;
		}

		public String getId() {
			return id;
		}

		public void setBefore(String before) {
			this.before = before;
		}

		public String getBefore() {
			return before;
		}

		public void setAfter(String after) {
			this.after = after;
		}

		public String getAfter() {
			return after;
		}
		
		public String getName() {
			return name;
		}
		
		public String getDescription() {
			return description;
		}

		public String getIcon() {
			return icon;
		}
		
		public String getFromPalette() {
			return fromPalette;
		}

		public void setFromPalette(String fromPalette) {
			this.fromPalette = fromPalette;
		}

		public ToolPaletteDescriptor getParent() {
			return parent;
		}
	};
	
	// The Tools
	public static class ToolDescriptor {
		private CategoryDescriptor parent;
		private String id;
		private String name;
		private String description;
		private String icon;
		private String fromPalette;
		private List<ToolPart> toolParts = new ArrayList<ToolPart>() {
			@Override
			public boolean add(ToolPart tp) {
				return super.add(tp);
			}
		};
		
		public ToolDescriptor(CategoryDescriptor parent, String id, String name, String description, String icon) {
			this.parent = parent;
			this.id = id;
			this.name = name;
			this.description = description;
			this.icon = icon;
		}
		
		public ToolDescriptor(CategoryDescriptor parent, String id, String name, String description, String icon, String object) {
			this(parent, id, name, description, icon);
			parseToolObjectString(object);
		}
		
		public ToolPart parseToolObjectString(String object) {
			List<ToolPart> currentParts = toolParts;
			ToolPart toolPart = null;
			String toolPartName = ""; //$NON-NLS-1$
			Stack<List<ToolPart>> stack = new Stack<List<ToolPart>>();
			ToolPart result = null;
			char chars[] = object.toCharArray();
			for (int i=0; i<chars.length; ++i) {
				char c = chars[i];
				if (c=='+') {
					stack.push(currentParts);
					if (!"".equals(toolPartName)) { //$NON-NLS-1$
						toolPart = new ToolPart(this, toolPartName);
						currentParts.add(toolPart);
					}
					currentParts = toolPart.children;
					if (result==null)
						result = toolPart;
					toolPartName = ""; //$NON-NLS-1$
				}
				else if (c=='-') {
					if (!"".equals(toolPartName)) //$NON-NLS-1$
						currentParts.add( new ToolPart(this, toolPartName) );
					currentParts = stack.pop();
					toolPartName = ""; //$NON-NLS-1$
				}
				else if (c==',') {
					if (!"".equals(toolPartName)) { //$NON-NLS-1$
						toolPart = new ToolPart(this, toolPartName);
						currentParts.add(toolPart);
						if (result==null)
							result = toolPart;
						toolPartName = ""; //$NON-NLS-1$
					}
				}
				else if (c=='[') {
					toolPart = new ToolPart(this, toolPartName);
					currentParts.add(toolPart);
					if (result==null)
						result = toolPart;
					toolPartName = ""; //$NON-NLS-1$
					
					// data for preceding object type follows:
					// [name=value] or [name1=value1,name2=value2]
					// are valid
					++i;
					do {
						String prop = ""; //$NON-NLS-1$
						while (i<chars.length) {
							c = chars[i++];
							if (c=='\\')
								c = chars[i++];
							else if (c=='=')
								break;
							prop += c;
						}
						String value = ""; //$NON-NLS-1$
						boolean quote = false;
						while (i<chars.length) {
							c = chars[i++];
							if (c=='\'') {
								quote = !quote;
								continue;
							}
							if (c=='\\')
								c = chars[i++];
							else if (!quote && (c==',' || c==']'))
								break;
							value += c;
						}
						toolPart.putProperty(prop,value);
					} while (i<chars.length && c!=']');
					if (c==']') {
						--i;
					}
				}
				else if ("".equals(toolPartName)) { //$NON-NLS-1$
					if (Character.isJavaIdentifierStart(c))
						toolPartName += c;
				}
				else if (Character.isJavaIdentifierPart(c)) {
					toolPartName += c;
				}
				
				if (i==chars.length-1 && !toolPartName.isEmpty()) {
					toolPart = new ToolPart(this, toolPartName);
					currentParts.add(toolPart);
					if (result==null)
						result = toolPart;
				}
			}
			
			return result;
		}
		
		public ToolDescriptor(CategoryDescriptor parent, String name, String description, String icon) {
			this.parent = parent;
			this.name = name;
			this.description = description;
			this.icon = icon;
		}
		
		public List<ToolPart> getToolParts() {
			return toolParts;
		}
		
		public String getId() {
			return id;
		}
		
		public String getName() {
			return name;
		}
		
		public String getDescription() {
			return description;
		}
		
		public String getIcon() {
			return icon;
		}
		
		public String getFromPalette() {
			return fromPalette;
		}

		public void setFromPalette(String fromPalette) {
			this.fromPalette = fromPalette;
		}

		public CategoryDescriptor getParent() {
			return parent;
		}
	};
	
	public static class ToolPart {
		private ToolDescriptor parent;
		private String name;
		private List<ToolPart> children = new ArrayList<ToolPart>();
		private Hashtable<String, String> properties = null;
		
		public ToolPart(ToolDescriptor parent, String name) {
			this.parent = parent;
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public List<ToolPart> getChildren() {
			return children;
		}
		
		public void putProperty(String name, String value) {
			getProperties().put(name, value);
		}
		
		public Hashtable<String, String> getProperties() {
			if (properties==null)
				properties = new Hashtable<String, String>();
			return properties;
		}
		
		public String getProperty(String name) {
			if (properties==null)
				return null;
			return properties.get(name);
		}
		
		public boolean hasProperties() {
			return properties!=null && properties.size()>0;
		}
		
		public ToolDescriptor getParent() {
			return parent;
		}
	}
	
	/** The model enablement profile for which this toolPalette is to be used **/
	String profileId;
	/** The list of categories in the toolPalette **/
	List<CategoryDescriptor> categories = new ArrayList<CategoryDescriptor>();

	
	public ToolPaletteDescriptor() {
		super();
	}
	
	public ToolPaletteDescriptor(IConfigurationElement e) {
		super(e);
		profileId = e.getAttribute("profile"); //$NON-NLS-1$
		for (IConfigurationElement c : e.getChildren()) {
			if (c.getName().equals("category")) { //$NON-NLS-1$
				String cid = c.getAttribute("id"); //$NON-NLS-1$
				String name = c.getAttribute("name"); //$NON-NLS-1$
				String description = c.getAttribute("description"); //$NON-NLS-1$
				String icon = c.getAttribute("icon"); //$NON-NLS-1$
				CategoryDescriptor category = addCategory(cid, name, description, icon);
				cid = c.getAttribute("before"); //$NON-NLS-1$
				if (cid!=null)
					category.setBefore(cid);
				cid = c.getAttribute("after"); //$NON-NLS-1$
				if (cid!=null)
					category.setAfter(cid);
				cid = c.getAttribute("fromPalette"); //$NON-NLS-1$
				if (cid!=null)
					category.setFromPalette(cid);
				for (IConfigurationElement t : c.getChildren()) {
					if (t.getName().equals("tool")) { //$NON-NLS-1$
						String tid = t.getAttribute("id"); //$NON-NLS-1$
						name = t.getAttribute("name"); //$NON-NLS-1$
						description = t.getAttribute("description"); //$NON-NLS-1$
						icon = t.getAttribute("icon"); //$NON-NLS-1$
						String object = t.getAttribute("object"); //$NON-NLS-1$
						ToolDescriptor tool = null;
						
						if (object!=null && !object.isEmpty()) {
							tool = category.addTool(tid, name, description, icon, object);
						}
						else {
							tool = category.addTool(tid, name, description, icon);
							for (IConfigurationElement tc : t.getChildren()) {
								if ("object".equals(tc.getName())) { //$NON-NLS-1$
									String id = tc.getAttribute("id"); //$NON-NLS-1$
									String type = tc.getAttribute("type"); //$NON-NLS-1$
									String optional = tc.getAttribute("optional"); //$NON-NLS-1$
									ToolPart tp = tool.parseToolObjectString(type);
									if (id!=null && !id.isEmpty())
										tp.getProperties().put(TOOLPART_ID, id);
									if ("true".equals(optional)) //$NON-NLS-1$
										tp.getProperties().put(TOOLPART_OPTIONAL, optional);
								}
							}
						}
						tid = c.getAttribute("fromPalette"); //$NON-NLS-1$
						if (tid!=null)
							tool.setFromPalette(cid);
					}
				}
			}
		}
	}
	
	public String getExtensionName() {
		return EXTENSION_NAME;
	}

	protected CategoryDescriptor addCategory(String id, String name, String description, String icon) {
		CategoryDescriptor category = null;
		for (CategoryDescriptor cd : categories) {
			if (cd.getId().equals(id)) {
				category = cd;
				break;
			}
		}
		if (category==null) {
			category = new CategoryDescriptor(this, id, name, description, icon);
			categories.add(category);
		}
		return category;
	}

	public void sortCategories() {
		// order the categories depending on "before" abd "after" attributes
		List<CategoryDescriptor> sorted = new ArrayList<CategoryDescriptor>();
		sorted.addAll(categories);
		boolean changed = false;
		for (CategoryDescriptor movedCategory : categories) {
			String before = movedCategory.getBefore();
			if (before!=null) {
				for (CategoryDescriptor cd : sorted) {
					if (cd.getId().equals(before)) {
						sorted.remove(movedCategory);
						int i = sorted.indexOf(cd);
						sorted.add(i,movedCategory);
						changed = true;
						break;
					}
				}				
			}
			String after = movedCategory.getAfter();
			if (after!=null) {
				for (CategoryDescriptor cd : sorted) {
					if (cd.getId().equals(after)) {
						sorted.remove(movedCategory);
						int i = sorted.indexOf(cd);
						if (i+1 < sorted.size())
							sorted.add(i+1,movedCategory);
						else
							sorted.add(movedCategory);
						changed = true;
						break;
					}
				}				
			}
		}
		if (changed) {
			categories.clear();
			categories.addAll(sorted);
		}
	}

	public String getId() {
		return id;
	}
	
	public List<String> getProfileIds() {
		String a[] = profileId.split(" "); //$NON-NLS-1$
		List<String> profiles = new ArrayList<String>();
		for (String p : a) {
			profiles.add(p.trim());
		}
		return profiles;
	}
	
	public List<CategoryDescriptor> getCategories() {
		return categories;
	}
}
