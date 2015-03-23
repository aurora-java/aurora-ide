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

package org.eclipse.bpmn2.modeler.core.model;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EPackage;

/**
 * This adapter is added to the dynamic EPackage managed by the ModelDecorator.
 * Clients will use this adapter to find the ModelDecorator instance that owns the EPackage.
 * It is also used by the XMLHandler to resolve object features during loading of a BPMN2 Resource.
 */
public class ModelDecoratorAdapter extends AdapterImpl {
	ModelDecorator modelDecorator;
	
	public static void adapt(ModelDecorator md) {
		addAdapter(md, md.getEPackage());
		for (EPackage pkg : md.getRelatedEPackages())
			addAdapter(md, pkg);
	}
	
	private static void addAdapter(ModelDecorator md, EPackage pkg) {
		ModelDecoratorAdapter adapter = null;
		for (Adapter a : pkg.eAdapters()) {
			if (a instanceof ModelDecoratorAdapter) {
				// this EPackage already has an adapter
				adapter = (ModelDecoratorAdapter) a;
				break;
			}
		}
		if (adapter==null) {
			adapter = new ModelDecoratorAdapter(md);
			pkg.eAdapters().add(adapter);
		}
	}
	
	public void dispose() {
		modelDecorator.getEPackage().eAdapters().remove(this);
		for (EPackage pkg : modelDecorator.getRelatedEPackages())
			pkg.eAdapters().remove(this);
	}
	
	public static ModelDecoratorAdapter getAdapter(EPackage pkg) {
		for (Adapter a : pkg.eAdapters()) {
			if (a instanceof ModelDecoratorAdapter) {
				return (ModelDecoratorAdapter) a;
			}
		}
		return null;
	}
	
	public static ModelDecorator getModelDecorator(EPackage pkg) {
		for (Adapter a : pkg.eAdapters()) {
			if (a instanceof ModelDecoratorAdapter) {
				return ((ModelDecoratorAdapter)a).getModelDecorator();
			}
		}
		return null;
	}
	
	private ModelDecoratorAdapter(ModelDecorator modelDecorator) {
		this.modelDecorator = modelDecorator;
	}
	
	public ModelDecorator getModelDecorator() {
		return modelDecorator;
	}
}