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
 * @author Bob Brodt
 ******************************************************************************/

package org.eclipse.bpmn2.modeler.ui.property.dialogs;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.bpmn2.modeler.ui.Activator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionStatusDialog;

/**
 * This class is a delegate for a Schema Import Dialog which may be supplied by the currently
 * active Target Runtime. The Target Runtime plugin is searched for an "importDialog" extension
 * and, if found, the class defined by that extension is used as the import dialog.
 * 
 * The class must be a subclass of org.eclipse.ui.dialogs.SelectionStatusDialog and may
 * either build on the default implementation DefaultSchemaImportDialog, or may completely
 * replace it.
 * 
 * See the extension point org.eclipse.bpmn2.modeler.ui for a complete description.
 */

public class SchemaImportDialog {
	// resource type flags for configuring this dialog:
	public final static int ALLOW_XSD   = (1 << 0);
	public final static int ALLOW_WSDL  = (1 << 1);
	public final static int ALLOW_BPMN2 = (1 << 2);
	public final static int ALLOW_JAVA  = (1 << 3);

	SelectionStatusDialog delegate = null;
	
	/**
	 * Create a brand new shiny Schema Import Dialog.
	 * 
	 * @param parent
	 */
	public SchemaImportDialog(Shell parent, int allowedResourceTypes) {
		
		// Get the SchemaImportDialog class for this Target Runtime from contributing plugins;
		// if none found, use a default
		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(
				Activator.UI_EXTENSION_ID);
		try {
			for (IConfigurationElement e : config) {
				if (e.getName().equals("importDialog")) { //$NON-NLS-1$
					String id = e.getAttribute("id"); //$NON-NLS-1$
					String runtimeId = e.getAttribute("runtimeId"); //$NON-NLS-1$
					TargetRuntime rt = TargetRuntime.getCurrentRuntime();
					if (rt!=null && rt.getId().equals(runtimeId)) {
						delegate = (SelectionStatusDialog)e.createExecutableExtension("class"); //$NON-NLS-1$
						break;
					}
				}
			}
		}
		catch (Exception ex) {
			Activator.logError(ex);
		}
		if (delegate==null)
			delegate = new DefaultSchemaImportDialog(parent,allowedResourceTypes);
	}
	
	public SchemaImportDialog(Shell parent) {
		this(parent, -1);
	}

	public int open() {
		return delegate.open();
	}

	public Object[] getResult() {
		return delegate.getResult();
	}
}
