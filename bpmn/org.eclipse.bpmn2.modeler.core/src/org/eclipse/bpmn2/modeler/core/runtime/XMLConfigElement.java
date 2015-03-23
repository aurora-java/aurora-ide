/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013, 2014 Red Hat, Inc.
 * All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.runtime;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.bpmn2.modeler.core.Activator;
import org.eclipse.bpmn2.modeler.core.DefaultConversionDelegate;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EDataType.Internal.ConversionDelegate;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;

public class XMLConfigElement implements IConfigurationElement {

	protected Object parent = null;
	protected String name = null;
	protected String value = null;
	protected boolean valid = true;
	protected Hashtable<String, String> attributes = new Hashtable<String, String>();
	protected List<XMLConfigElement> children = new ArrayList<XMLConfigElement>();
	
	public XMLConfigElement(Object parent) {
		this(parent,""); //$NON-NLS-1$
	}
	
	public XMLConfigElement(Object parent, String name) {
		this.parent = parent;
		if (parent instanceof XMLConfigElement) {
			((XMLConfigElement)parent).children.add(this);
		}
		this.name = name;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked", "resource" })
	@Override
	public Object createExecutableExtension(String propertyName) throws CoreException {
		try {
			String className = attributes.get(propertyName);
			IProject project = getProject();
			IJavaProject javaProject = JavaCore.create(project);
			String[] classPathEntries = JavaRuntime.computeDefaultRuntimeClassPath(javaProject);
			List<URL> urlList = new ArrayList<URL>();
			for (int i = 0; i < classPathEntries.length; i++) {
				String entry = classPathEntries[i];
				IPath path = new Path(entry);
				URL url = path.toFile().toURI().toURL();
				urlList.add(url);
			}
			ClassLoader parentClassLoader = javaProject.getClass().getClassLoader();
			URL[] urls = (URL[]) urlList.toArray(new URL[urlList.size()]);
			URLClassLoader classLoader = new URLClassLoader(urls, parentClassLoader);
			ClassLoader cl = classLoader.getParent();
			Class clazz = classLoader.loadClass(className);
			return clazz.getConstructor().newInstance();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getMessage()));
		}
	}

	private IProject getProject() {
		if (parent instanceof IProject)
			return (IProject) parent;
		if (parent instanceof XMLConfigElement)
			return ((XMLConfigElement)parent).getProject();
		return null;
	}
	
	public void setAttribute(String name, String value) {
		attributes.put(name, value);
	}
	
	@Override
	public String getAttribute(String name) throws InvalidRegistryObjectException {
		return attributes.get(name);
	}

	@Override
	public String getAttribute(String attrName, String locale) throws InvalidRegistryObjectException {
		return getAttribute(attrName);
	}

	@Override
	public String getAttributeAsIs(String name) throws InvalidRegistryObjectException {
		return getAttribute(name);
	}

	@Override
	public String[] getAttributeNames() throws InvalidRegistryObjectException {
		return attributes.keySet().toArray( new String[attributes.keySet().size()] );
	}

	@Override
	public IConfigurationElement[] getChildren() throws InvalidRegistryObjectException {
		return children.toArray( new XMLConfigElement[children.size()] );
	}

	@Override
	public IConfigurationElement[] getChildren(String name) throws InvalidRegistryObjectException {
		List<XMLConfigElement> result = new ArrayList<XMLConfigElement>();
		for ( XMLConfigElement e : children) {
			if (name.equals(e.getName()))
				result.add(e);
		}
		return result.toArray( new XMLConfigElement[result.size()] );
	}

	@Override
	public IExtension getDeclaringExtension() throws InvalidRegistryObjectException {
		XMLConfigElement root = this;
		while (root.getParent() instanceof XMLConfigElement)
			root = (XMLConfigElement) root.getParent();
		return new XMLExtension(root);
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() throws InvalidRegistryObjectException {
		return name;
	}

	@Override
	public Object getParent() throws InvalidRegistryObjectException {
		return parent;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String getValue() throws InvalidRegistryObjectException {
		return value;
	}

	@Override
	public String getValue(String locale) throws InvalidRegistryObjectException {
		return getValue();
	}

	@Override
	public String getValueAsIs() throws InvalidRegistryObjectException {
		return getValue();
	}

	@Override
	public String getNamespace() throws InvalidRegistryObjectException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNamespaceIdentifier() throws InvalidRegistryObjectException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContributor getContributor() throws InvalidRegistryObjectException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValid() {
		return valid;
	}

	public static class XMLExtension implements IExtension {

		XMLConfigElement root;
		
		public XMLExtension(XMLConfigElement root) {
			this.root = root;
		}

		@Override
		public IConfigurationElement[] getConfigurationElements() throws InvalidRegistryObjectException {
			return root.getChildren();
		}

		@Override
		public String getNamespace() throws InvalidRegistryObjectException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getNamespaceIdentifier() throws InvalidRegistryObjectException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IContributor getContributor() throws InvalidRegistryObjectException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getExtensionPointUniqueIdentifier() throws InvalidRegistryObjectException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getLabel() throws InvalidRegistryObjectException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getLabel(String locale) throws InvalidRegistryObjectException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getSimpleIdentifier() throws InvalidRegistryObjectException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getUniqueIdentifier() throws InvalidRegistryObjectException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isValid() {
			// TODO Auto-generated method stub
			return false;
		}

		public IPluginDescriptor getDeclaringPluginDescriptor() throws InvalidRegistryObjectException {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
