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
package org.eclipse.bpmn2.modeler.core;

import java.util.Hashtable;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EDataType.Internal.ConversionDelegate;
import org.eclipse.emf.ecore.EDataType.Internal.ConversionDelegate.Factory;

public class EDataTypeConversionFactory implements Factory {

	public static EDataTypeConversionFactory INSTANCE = new EDataTypeConversionFactory();
	
	/**
	 * The URI for our EDataType conversion factory. This must be the same as the "uri" specified in
	 * the {@code org.eclipse.emf.ecore.conversion_delegate} extension point in the implementation's plugin.xml.
	 */
	public final static String DATATYPE_CONVERSION_FACTORY_URI = "http://org.eclipse.bpmn2.modeler.EDataTypeConversionFactory"; //$NON-NLS-1$

	/**
	 *  A registry that maps a data type name to a conversion delegate.
	 *  Clients may register their own types and conversion delegates
	 *  with {@code EDataTypeConversionFactory#registerConversionDelegate(String,Class)}.
	 */
	private static Hashtable<String, Class<? extends ConversionDelegate>> registry =
			new Hashtable<String, Class<? extends ConversionDelegate>>();
	
	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.EDataType.Internal.ConversionDelegate.Factory#createConversionDelegate(org.eclipse.emf.ecore.EDataType)
	 * 
	 * Consult our registry for the name of the given data type.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ConversionDelegate createConversionDelegate(EDataType eDataType) {
		Class clazz = registry.get(eDataType.getName());
		if (clazz!=null) {
			try {
				return (DefaultConversionDelegate) clazz.getConstructor().newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Register a Data Type Conversion Delegate class.
	 * 
	 * @param type  the data type name.
	 * @param delegate  the Conversion Delegate class.
	 */
	public static void registerConversionDelegate(String type, Class<? extends ConversionDelegate> delegate) {
		registry.put(type,delegate);
	}

	/**
	 * Remove a Data Type Conversion Delegate from the registry.
	 * 
	 * @param type  the data type name.
	 */
	public static void unregisterConversionDelegate(String type) {
		registry.remove(type);
	}
	
	/**
	 * Check if the given data type has been registered.
	 * 
	 * @param type  the data type name.
	 * @return true if a Conversion Delegate has been registered for the given data type name.
	 */
	public static boolean isFactoryFor(String type) {
		if (type!=null)
			return registry.get(type) != null;
		return false;
	}
}
