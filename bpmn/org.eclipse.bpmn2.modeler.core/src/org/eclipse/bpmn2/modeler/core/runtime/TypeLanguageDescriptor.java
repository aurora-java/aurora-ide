/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013, 2014 Red Hat, Inc.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.bpmn2.modeler.core.utils.NamespaceUtil;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.emf.ecore.resource.Resource;

/**
 *
 */
public class TypeLanguageDescriptor extends BaseRuntimeExtensionDescriptor {

	public final static String EXTENSION_NAME = "typeLanguage"; //$NON-NLS-1$

	protected String name;
	protected String uri;
	protected String prefix;
	protected List<Type> types = new ArrayList<Type>();
	
	public class Type {
		TypeLanguageDescriptor typeLanguage;
		String name;
		String qname;
		
		public Type(TypeLanguageDescriptor typeLanguage, String name, String qname) {
			this.typeLanguage = typeLanguage;
			this.name = name;
			this.qname = qname;
		}

		public String getName() {
			return name;
		}

		public String getQName(Resource resource) {
			if (qname==null) {
				// create a QName string by prefixing the primitive type name
				// with the namespace prefix for the typeLanguage specified in
				// the given Definitions object.
				String prefix = NamespaceUtil.getPrefixForNamespace(resource, typeLanguage.getUri());
				if (prefix!=null && !prefix.isEmpty())
					return prefix + ":" + name; //$NON-NLS-1$
				if (typeLanguage.prefix!=null)
					return typeLanguage.prefix + ":" + name; //$NON-NLS-1$
				return name;
			}
			return qname;
		}
		
		public TypeLanguageDescriptor getTypeLanguage() {
			return typeLanguage;
		}
	}
	
	public TypeLanguageDescriptor(IConfigurationElement e) {
		super(e);
		name = e.getAttribute("name"); //$NON-NLS-1$
		uri = e.getAttribute("uri"); //$NON-NLS-1$
		prefix = e.getAttribute("prefix"); //$NON-NLS-1$
		for (IConfigurationElement c : e.getChildren()) {
			if ("type".equals(c.getName())) { //$NON-NLS-1$
				Type pt = new Type(this,
						c.getAttribute("name"), //$NON-NLS-1$
						c.getAttribute("qname") //$NON-NLS-1$
				);
				types.add(pt);
			}
		}
	}
	
	@Override
	public void setRuntime(TargetRuntime targetRuntime) {
		super.setRuntime(targetRuntime);
	}

	@Override
	public void dispose() {
		super.dispose();
		types.clear();
	}

	@Override
	public String getExtensionName() {
		return EXTENSION_NAME;
	}
	
	public String getName() {
		return name;
	}

	public String getUri() {
		return uri;
	}

	public List<Type> getTypes() {
		if (types.isEmpty() && !TargetRuntime.DEFAULT_RUNTIME_ID.equals(getId())) {
			for (TypeLanguageDescriptor d : TargetRuntime.getDefaultRuntime().getTypeLanguageDescriptors()) {
				if (d.name.equals(name)) {
					// Return primitive types from the default runtime if we don't define any
					return d.getTypes();
				}
			}
		}
		return types;
	}

	public String getPrefix() {
		return prefix;
	}
}
