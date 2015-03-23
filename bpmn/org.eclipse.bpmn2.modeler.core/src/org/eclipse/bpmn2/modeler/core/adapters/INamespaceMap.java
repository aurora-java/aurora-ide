/*******************************************************************************
 * Copyright (c) 2011, 2012 Red Hat, Inc. 
 * All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 *
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *******************************************************************************/
package org.eclipse.bpmn2.modeler.core.adapters;

import java.util.List;
import java.util.Map;

/**
 * Namespace map of K,V which also holds the reverse map of V,K
 *  
 * @param <K>
 * @param <V>
 */

public interface INamespaceMap <K,V> extends Map<K,V> {
    
    /**
     * Get the entry under key V (which is the value).
     * 
     * @param key the value key
     * @return the list of prefix names 
     */
    public List<K> getReverse ( V key );
    
    
}