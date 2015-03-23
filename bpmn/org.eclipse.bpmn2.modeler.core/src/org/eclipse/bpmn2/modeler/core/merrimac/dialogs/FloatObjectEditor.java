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

package org.eclipse.bpmn2.modeler.core.merrimac.dialogs;

import java.math.BigInteger;

import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractDetailComposite;
import org.eclipse.bpmn2.modeler.core.utils.ErrorUtils;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Bob Brodt
 *
 */
public class FloatObjectEditor extends TextObjectEditor {

	/**
	 * @param parent
	 * @param object
	 * @param feature
	 */
	public FloatObjectEditor(AbstractDetailComposite parent, EObject object, EStructuralFeature feature) {
		super(parent, object, feature);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.ui.property.editors.ObjectEditor#createControl(org.eclipse.swt.widgets.Composite, java.lang.String)
	 */
	@Override
	protected Control createControl(Composite composite, String label, int style) {
		createLabel(composite,label);

		text = getToolkit().createText(composite, ""); //$NON-NLS-1$
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		text.addVerifyListener(new VerifyListener() {

			/**
			 * taken from
			 * http://dev.eclipse.org/viewcvs/viewvc.cgi/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets
			 * /Snippet19.java?view=co
			 */
			@Override
			public void verifyText(VerifyEvent e) {
				String string = e.text;
				char[] chars = new char[string.length()];
				string.getChars(0, chars.length, chars, 0);
				for (int i = 0; i < chars.length; i++) {
					if (!('0' <= chars[i] && chars[i] <= '9') && chars[i]!='.') {
						e.doit = false;
						return;
					}
				}
			}
		});

		updateText();

		IObservableValue textObserveTextObserveWidget = SWTObservables.observeText(text, SWT.Modify);
		textObserveTextObserveWidget.addValueChangeListener(new IValueChangeListener() {
			@Override
			public void handleValueChange(ValueChangeEvent event) {

				try {
					final Double i = Double.parseDouble(text.getText());
					if (!getValue().equals(i))
						setFeatureValue(i);
				} catch (NumberFormatException e) {
					setFeatureValue(0L);
				}
			}

			@SuppressWarnings("rawtypes")
			private void setFeatureValue(final double i) {
				getBusinessObjectDelegate().setValue(object, feature, Double.toString(i));
			}
		});

		
		text.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
			}

			@Override
			public void focusLost(FocusEvent e) {
				ErrorUtils.showErrorMessage(null);
			}
		});

		return text;
	}
		
		public Double getValue() {
			Object v = getBusinessObjectDelegate().getValue(object, feature);
			if (v instanceof Short)
				return ((Short)v).doubleValue();
			if (v instanceof Integer)
				return ((Integer)v).doubleValue();
			if (v instanceof Long)
				return ((Long)v).doubleValue();
			if (v instanceof Double)
				return (Double)v;
			if (v instanceof BigInteger)
				return ((BigInteger)v).doubleValue();
			if (v instanceof String) {
				try {
					return Double.parseDouble((String)v);
				}
				catch (Exception e){
				}
			}
			return new Double(0);
		}
}
