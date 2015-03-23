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

package org.eclipse.bpmn2.modeler.core.features;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.ui.platform.ICellEditorProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 *
 */
public abstract class AbstractBpmn2DirectEditingFeature extends AbstractDirectEditingFeature implements ICellEditorProvider {

	/**
	 * Construct a Direct Editing Feature. This implements {@link 
	 * @param fp the Feature Provider
	 */
	public AbstractBpmn2DirectEditingFeature(IFeatureProvider fp) {
		super(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.IDirectEditing#getEditingType()
	 */
	@Override
	public int getEditingType() {
		return TYPE_CUSTOM;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.IDirectEditing#getInitialValue(org.eclipse.graphiti.features.context.IDirectEditingContext)
	 */
	@Override
	public String getInitialValue(IDirectEditingContext context) {
		String value = "";
		BaseElement be = getBusinessObject(context);
		EStructuralFeature feature = be.eClass().getEStructuralFeature("name"); //$NON-NLS-1$
		if (feature!=null)
			value = (String) be.eGet(feature);
		return value==null ? "" : value; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature#setValue(java.lang.String, org.eclipse.graphiti.features.context.IDirectEditingContext)
	 */
	@Override
	public void setValue(String value, IDirectEditingContext context) {
		BaseElement be = getBusinessObject(context);
		EStructuralFeature feature = be.eClass().getEStructuralFeature("name"); //$NON-NLS-1$
		if (feature!=null) {
			be.eSet(feature, value);
			PictogramElement pe = context.getPictogramElement();
			FeatureSupport.updateLabel(getFeatureProvider(), ((Shape) pe).getContainer(), null);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature#canDirectEdit(org.eclipse.graphiti.features.context.IDirectEditingContext)
	 */
	@Override
	public boolean canDirectEdit(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);
		if (bo instanceof BaseElement && ((BaseElement)bo).eClass().getEStructuralFeature("name")!=null) //$NON-NLS-1$
			return true;
		return false;
	}

	private BaseElement getBusinessObject(IDirectEditingContext context) {
		return (BaseElement) getBusinessObjectForPictogramElement(context.getPictogramElement());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.platform.ICellEditorProvider#createCellEditor(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public CellEditor createCellEditor(Composite parent) {
		TextCellEditor ce = new org.eclipse.jface.viewers.TextCellEditor(parent, SWT.MULTI | SWT.WRAP | SWT.LEFT);
		return ce;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.platform.ICellEditorProvider#relocate(org.eclipse.jface.viewers.CellEditor, org.eclipse.draw2d.IFigure)
	 */
	@Override
	public void relocate(CellEditor cellEditor, IFigure figure) {
		Text text = (Text) cellEditor.getControl();

		Rectangle rect = figure.getClientArea().getCopy();
		figure.translateToAbsolute(rect);
		org.eclipse.swt.graphics.Rectangle trim = text.computeTrim(0, 0, 0, 0);
		rect.translate(trim.x, trim.y);
		rect.width += trim.width;
		if (rect.width<100) {
			rect.x -= (100 - rect.width)/2;
			rect.width = 100;
		}
		rect.height += trim.height;
		text.setBounds(rect.x, rect.y, rect.width, rect.height);
	}

}
