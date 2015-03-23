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

import java.util.HashMap;

import org.eclipse.bpmn2.util.Bpmn2ResourceImpl;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

public class ModelHandlerLocator {

	private static HashMap<URI, ModelHandler> map = new HashMap<URI, ModelHandler>();
	private static HashMap<URI, ModelHandler> diagramMap = new HashMap<URI, ModelHandler>();

	public static ModelHandler getModelHandler(Resource eResource) {
		if (eResource==null)
			return null;
		URI uri = eResource.getURI().trimFragment();

		return getModelHandler(uri);
	}

	public static ModelHandler getModelHandler(URI path) {
		path = path.trimFragment();
		ModelHandler modelHandler = map.get(path);
		if (modelHandler == null) {
			return diagramMap.get(path);
		}
		return modelHandler;
	}

	public static void put(URI diagramPath, ModelHandler mh) {
		diagramMap.put(diagramPath.trimFragment(), mh);
	}

	public static void remove(URI path) {
		path = path.trimFragment();
		if (map.remove(path)==null) {
			diagramMap.remove(path);
		}
	}

	public static ModelHandler createModelHandler(URI path, final Bpmn2ResourceImpl resource) {
		path = path.trimFragment();
		if (map.containsKey(path)) {
			return map.get(path);
		}
		return createNewModelHandler(path, resource);
	}

	private static ModelHandler createNewModelHandler(URI path, final Bpmn2ResourceImpl resource) {
		ModelHandler handler = new ModelHandler();
		path = path.trimFragment();
		map.put(path, handler);
		handler.resource = resource;

		URI uri = resource.getURI();

		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			String platformString = uri.toPlatformString(true);

			// platformString is null if file is outside of workspace
			if ((platformString == null || workspace.getRoot().getFile(new Path(platformString)).exists())
					&& !resource.isLoaded()) {
				handler.loadResource();
			}
		}
		catch (IllegalStateException e) {
			// TODO: what does this mean???
			// Workspace is not initialized so we must be running tests!
			if (!resource.isLoaded()) {
				handler.loadResource();
			}
		}
		catch (Exception spe) {
			// we're here because of an xml parse exception:
			// try to recover if possible by creating an empty <definitions> element.
		}

		handler.createDefinitionsIfMissing();
		return handler;
	}

	/**
	 * Remove the Model Handler instance from our cache.
	 * 
	 * @param modelHandler the Model Handler to be removed
	 */
	public static void dispose(ModelHandler modelHandler) {
		remove(modelHandler.getResource().getURI());
	}
}
