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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;

/**
 * The interface that defines a provider for an EditControl.
 * <p>
 * An EditControl is an SWT Composite that can be embedded in a Viewer or
 * Dialog, just like any other Composite.
 * <p>
 * EditControl must implement setValue() and getValue() methods which are used
 * to initialize the EditControl's widget with data, and fetch data from the
 * widget.
 * <p>
 * Listeners are used to notify the client when the widget's data has changed,
 * therefore the implementation must add "this" as a widget Selection Listener
 * in the createControl() method.
 */
public interface EditControlProvider {

	/**
	 * An wrapper class for SWT widgets.
	 * <p>
	 * Clients must extend this class and provide appropriate editing widgets.
	 */
	public abstract class EditControl extends Composite implements SelectionListener {
		protected List<SelectionListener> listeners;

		public EditControl(Composite parent, int style) {
			super(parent, style);
		}

		/**
		 * The implementation must return the value of the object being edited,
		 * performing data type conversion if necessary.
		 * 
		 * @return the value of the object being edited, in the correct data
		 *         type.
		 */
		public abstract Object getValue();

		/**
		 * The implementation must convert the given object value to a form that
		 * can be represented by the SWT editing widget.
		 * 
		 * @param value
		 *            the object value
		 * @return true if the value is valid, false if not.
		 */
		public abstract boolean setValue(Object value);

		/**
		 * The implementation must add this EditControl as a selection listener.
		 * This will notify the parent when the value changes in the editing
		 * widget.
		 * 
		 * @param listener
		 *            should be this EditControl, or some proxy.
		 */
		public void addSelectionListener(SelectionListener listener) {
			if (listeners == null)
				listeners = new ArrayList<SelectionListener>();
			listeners.add(listener);
		}

		/**
		 * Remove the selection listener previously added.
		 * 
		 * @param listener
		 *            a listener previously added by
		 *            {@code EditControl#addSelectionListener(SelectionListener)}
		 */
		public void removeSelectionListener(SelectionListener listener) {
			if (listeners == null)
				return;
			listeners.remove(listener);
			if (listeners.size() == 0)
				listeners = null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse
		 * .swt.events.SelectionEvent)
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (listeners != null) {
				for (SelectionListener listener : listeners)
					listener.widgetSelected(e);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org
		 * .eclipse.swt.events.SelectionEvent)
		 */
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	}

	/**
	 * The implementation must create the editing widget(s) in the given parent
	 * container and apply the given style bits if appropriate.
	 * 
	 * @param parent
	 *            the parent Composite container widget.
	 * @param style
	 *            style bits for the editing widget.
	 * @return the editing widget control. If more than one widget is required
	 *         for editing the data object, the one returned should be the same
	 *         widget that was registered as the selection listener.
	 */
	public EditControl createControl(Composite parent, int style);
}
