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
package org.eclipse.bpmn2.modeler.ui;

/**
 * @author Bob Brodt
 *
 */
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;

/**
 * Similar to IResourceChangeListener but with more specific events.
 */
public interface IFileChangeListener {
	/**
	 * Called after a file is moved or renamed.
	 * 
	 * @param source the previous file
	 * @param destination the new file
	 */
	void moved(IPath oldFilePath, IPath newFilePath);
	
	/**
	 * Called after a file is deleted.
	 */
	void deleted(IPath filePath);
}