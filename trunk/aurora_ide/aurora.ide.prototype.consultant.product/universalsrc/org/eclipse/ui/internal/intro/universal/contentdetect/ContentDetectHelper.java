/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.internal.intro.universal.contentdetect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

import aurora.ide.prototype.consultant.product.Activator;

public class ContentDetectHelper {
	
	public static final int NO_STATE = -1;
	private static final String EXTENSION_COUNT_XML = "extensionCount.xml"; //$NON-NLS-1$
	private static final String EXTENSION_NAMES_XML = "extensionNames.xml"; //$NON-NLS-1$
	private static final String INTROCOUNT = "introcount"; //$NON-NLS-1$
	private static final String CONTRIBUTOR = "contributor"; //$NON-NLS-1$
	private static final String NAME = "name"; //$NON-NLS-1$
	private static final String ROOT = "root"; //$NON-NLS-1$
	private static final String PLUGIN_ID = Activator.PLUGIN_ID; //$NON-NLS-1$
	
	private File configurationDirectory;

	private File getConfigurationLocation() {
		if (configurationDirectory == null) {
		    Location location = Platform.getConfigurationLocation();
		    if (location != null) {
			    URL configURL = location.getURL();
			    if (configURL != null && configURL.getProtocol().startsWith("file")) { //$NON-NLS-1$
				    configurationDirectory = new File(configURL.getFile(), PLUGIN_ID);
					if (configurationDirectory != null && !configurationDirectory.exists()) {
						configurationDirectory.mkdirs();
					}
			    }
		    }
		}
		return configurationDirectory;
	}
	
	public void saveExtensionCount(int count) {	
		XMLMemento writeMemento = XMLMemento.createWriteRoot(ROOT);
		writeMemento.putInteger(INTROCOUNT, count);
		saveMemento(writeMemento, EXTENSION_COUNT_XML);
	}
	
	public int getExtensionCount() {	
		XMLMemento readMemento = getReadMemento(EXTENSION_COUNT_XML);
		if (readMemento == null) {
			return NO_STATE;
		}

		Integer extensionCount = readMemento.getInteger(INTROCOUNT);
		if (extensionCount == null) {
			return NO_STATE;
		}
			
		return extensionCount.intValue();
	}

	public void saveContributors(Set contributors) {
		XMLMemento writeMemento = XMLMemento.createWriteRoot(ROOT);
		for (Iterator iter = contributors.iterator(); iter.hasNext();) {
			IMemento childMemento = writeMemento.createChild(CONTRIBUTOR);
			childMemento.putString(NAME, (String)iter.next());
		}
		saveMemento(writeMemento, EXTENSION_NAMES_XML);
	}
	
	public Set getContributors() {	
		Set contributors = new HashSet();
		XMLMemento readMemento = getReadMemento(EXTENSION_NAMES_XML);
		if (readMemento == null) {
			return contributors;
		}
		IMemento[] children = readMemento.getChildren(CONTRIBUTOR);
		for (int c = 0; c < children.length; c++ ) {
			contributors.add(children[c].getString(NAME));
		}
		return contributors;
	}
	
	private XMLMemento getReadMemento(String filename) {
		XMLMemento memento;
		InputStreamReader reader = null;

		try {
			final File stateFile = getStateFile(filename); 

			FileInputStream input = new FileInputStream(stateFile);
			reader = new InputStreamReader(input, "utf-8"); //$NON-NLS-1$
			memento = XMLMemento.createReadRoot(reader);

			
		} catch (FileNotFoundException e) {
			memento = null;
			// Do nothing, the file will not exist the first time the workbench in used.
		} catch (Exception e) {
            // TODO should we log an error?
			memento = null;
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				// TODO should we log an error?
			}
		}
		return memento;
	}
	
	private void saveMemento(XMLMemento memento, String filename) {
		// Save the IMemento to a file.
		File stateFile = getStateFile(filename); 
		OutputStreamWriter writer = null;
		try {
			FileOutputStream stream = new FileOutputStream(stateFile);
			writer = new OutputStreamWriter(stream, "utf-8"); //$NON-NLS-1$
			memento.save(writer);
		} catch (IOException e) {
			stateFile.delete();
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
			}
		}
	}
	
	private File getStateFile(String filename) {
		if (getConfigurationLocation() == null) {
			return null;
		}
		File stateFile = new File(getConfigurationLocation(), filename);
		return stateFile;
	}
	
	public Set findNewContributors(Set contributors, Set previousContributors) {
		Set result = new HashSet(contributors);
		for (Iterator iter = previousContributors.iterator(); iter.hasNext();) {
			result.remove(iter.next());
		}
		return result;
	}

	public void deleteStateFiles() {
		try {
			File stateFile = new File(getConfigurationLocation(), EXTENSION_COUNT_XML);	
			stateFile.delete();
			stateFile = new File(getConfigurationLocation(), EXTENSION_NAMES_XML);	
			stateFile.delete();
		} catch (RuntimeException e) {
		}
	}

}
