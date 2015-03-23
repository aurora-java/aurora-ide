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
package org.eclipse.bpmn2.modeler.core.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.bpmn2.modeler.core.Activator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;

public final class ProxyURIConverterImplExtension extends ExtensibleURIConverterImpl {
	private static final String DIR_NAME = "cache/"; //$NON-NLS-1$
	private URI baseUri;
	
	public ProxyURIConverterImplExtension(URI baseUri) {
		super();
		this.baseUri = baseUri;
	}
	
	/**
	 * We provide local copies for some files from the web. Local copy names are requested url without starting
	 * "http://" and all '/' are replaced with '_'
	 * 
	 * @see org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl#createInputStream(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public InputStream createInputStream(URI uri) throws IOException {
		URI resolvedUri = uri.resolve(baseUri);
		InputStream stream = getInputStreamForUri(resolvedUri);
		if (stream != null) {
			return stream;
		}

		InputStream createInputStream = super.createInputStream(resolvedUri);
		
		return createInputStream;
	}

	/**
	 * We provide local copies for some files from the web. Local copy names are requested url without starting
	 * "http://" and all '/' are replaced with '_'
	 * 
	 * @see org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl#createInputStream(org.eclipse.emf.common.util.URI,
	 *      java.util.Map)
	 */
	@Override
	public InputStream createInputStream(URI uri, java.util.Map<?, ?> options) throws IOException {
		URI resolvedUri = uri.resolve(baseUri);
		InputStream stream = getInputStreamForUri(resolvedUri);
		if (stream != null) {
			return stream;
		}

		InputStream createInputStream = super.createInputStream(resolvedUri, options);
		
		return createInputStream;
	}

	private InputStream getInputStreamForUri(URI uri) throws IOException {
		if (uri.toString().startsWith("http://")) { //$NON-NLS-1$
			return checkForLocalCopy(uri);
		}
		return null;
	}

	private InputStream checkForLocalCopy(URI uri) throws IOException {
		String fileName = uri.toString().substring(7).replace("/", "_"); //$NON-NLS-1$ //$NON-NLS-2$
		URL entry = Activator.getDefault().getBundle().getEntry(DIR_NAME + fileName);

		if (entry != null) {
			return entry.openStream();
		}
		return null;
	}
	
	public URI normalize(URI uri) {
		URI normalizedURI = super.normalize(uri);
		if (normalizedURI.isRelative()) {
			normalizedURI = normalizedURI.resolve(baseUri);
		}
		return normalizedURI;
	}
}