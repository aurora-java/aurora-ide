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

package org.eclipse.bpmn2.modeler.core.utils;

import org.eclipse.bpmn2.modeler.core.Activator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

/**
 * Simple error dialog class that runs in the UI thread
 */
public class ErrorDialog {

	protected String title;
	protected String message;
	
	public ErrorDialog(String title, String message) {
		this.title = title;
		this.message = message;
	}
	
	public ErrorDialog(String title, Exception e) {
		this(title,null,e);;
	}
	
	public ErrorDialog(String title, String message, Exception e) {
		this.title = title;
		this.message = (message==null ? "" : message+"\n"); //$NON-NLS-1$ //$NON-NLS-2$
		Throwable t = e;
		while (t!=null) {
			String msg = t.getMessage();
			if (msg==null)
				msg = t.toString();
			this.message += msg;
			t = t.getCause();
			if (t!=null) {
				this.message += "\nCaused by "; //$NON-NLS-1$
			}
		}
	}
	
	public void show() {
		Thread t = Thread.currentThread();
//		Display.getDefault().syncExec(new Runnable() {
//			@Override
//			public void run() {
				try {
					String me = Activator.getDefault().getDescriptor().getLabel();
					MessageDialog.openError(Display.getDefault().getActiveShell(), me + " - " + title, message); //$NON-NLS-1$
				}
				catch (Exception e) {}
//			}
//		});

	}
}
