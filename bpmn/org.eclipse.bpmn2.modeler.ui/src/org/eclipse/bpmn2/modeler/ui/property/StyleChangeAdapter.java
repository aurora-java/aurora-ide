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

package org.eclipse.bpmn2.modeler.ui.property;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.ChoreographyActivity;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.modeler.core.adapters.IExtensionValueAdapter;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.preferences.Bpmn2Preferences;
import org.eclipse.bpmn2.modeler.core.preferences.ShapeStyle;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.core.utils.StyleUtil;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

/**
 *
 */
public class StyleChangeAdapter extends AdapterImpl implements IExtensionValueAdapter {
	
	private BaseElement businessObject;
	private PictogramElement pictogramElement;
	
	public static void adapt(PictogramElement pe) {
		EObject be = BusinessObjectUtil.getBusinessObjectForPictogramElement(pe);
		if (be instanceof BaseElement && ShapeStyle.hasStyle((BaseElement)be)) {
			for (Adapter a : be.eAdapters()) {
				if (a instanceof StyleChangeAdapter)
					return;
			}
			StyleChangeAdapter adapter = new StyleChangeAdapter(pe, (BaseElement)be);
			be.eAdapters().add(adapter);
			adapter.updateStyle(null);
		}
	}
	
	private StyleChangeAdapter(PictogramElement pe, BaseElement be) {
		this.businessObject = be;
		this.pictogramElement = pe;
		for (ExtensionAttributeValue x : be.getExtensionValues()) {
			for (Entry v : x.getValue()) {
				if (v.getEStructuralFeature().getName().equals("style")) {
					EObject style = (EObject) v.getValue();
					style.eAdapters().add(this);
				}
			}
		}
	}
	
	@Override
	public void notifyChanged(Notification msg) {
		Object newValue = msg.getNewValue();
		if (msg.getEventType()==Notification.ADD && newValue instanceof ExtensionAttributeValue) {
			// an <extensionValue> was added, hook this adapter into its notification chain
			((EObject)newValue).eAdapters().add(this);
			EObject style = ShapeStyle.getStyleObject(businessObject);
			if (style!=null) {
				style.eAdapters().add(this);
				updateStyle(newValue);
			}
		}
		else if (msg.getEventType()==Notification.ADD && ShapeStyle.isStyleObject(newValue)) {
			// a "style" element was added, hook this adapter into that notification chain 
			((EObject)newValue).eAdapters().add(this);
		}
		else if (ShapeStyle.isStyleObject(msg.getNotifier())) {
			updateStyle(newValue);
		}
	}

	private void updateStyle(Object newValue) {
		if (businessObject.eResource()==null || pictogramElement.eResource()==null)
			return;
		
		ShapeStyle ss = ShapeStyle.getShapeStyle(businessObject);
		GraphicsAlgorithm ga = null;
		if (businessObject instanceof ChoreographyActivity
				|| businessObject instanceof Lane
				 || businessObject instanceof Participant) {
			ga = pictogramElement.getGraphicsAlgorithm();
		}
		else if (pictogramElement instanceof ContainerShape && ((ContainerShape)pictogramElement).getChildren().size()>0) {
			Shape shape = ((ContainerShape)pictogramElement).getChildren().get(0);
			ga = shape.getGraphicsAlgorithm();
		}
		else
			ga = pictogramElement.getGraphicsAlgorithm();
		
		if (ga!=null) {
			// only certain components, such as color and size apply to the appearance
			// of the figure. Other components (of type enum) apply to the Label Position
			// or line Routing Style which will be handled by the appropriate Update Features.
			if (!(newValue instanceof EEnumLiteral))
				StyleUtil.applyStyle(ga, businessObject, ss);
			
			// also apply the style to Label
			Shape labelShape = FeatureSupport.getLabelShape(pictogramElement);
			if (labelShape!=null) {
				ga = labelShape.getGraphicsAlgorithm();
				Graphiti.getPeService().setPropertyValue(labelShape, GraphitiConstants.LABEL_CHANGED, "true");
				StyleUtil.applyStyle(ga, businessObject, ss);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.adapters.IExtensionValueAdapter#shouldSaveElement()
	 */
	@Override
	public boolean shouldSaveElement(EObject o) {
		if (ShapeStyle.isStyleObject(o)) {
			// this is the "style" object
			return ShapeStyle.isDirty(businessObject);
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.adapters.IExtensionValueAdapter#shouldSaveFeature(org.eclipse.emf.ecore.EStructuralFeature)
	 */
	@Override
	public boolean shouldSaveFeature(EObject o, EStructuralFeature f) {
		if (ShapeStyle.isStyleObject(o)) {
			String feature = f.getName();
			Bpmn2Preferences preferences = Bpmn2Preferences.getInstance(businessObject);
			ShapeStyle ss = preferences.getShapeStyle(businessObject);
			Object v = ShapeStyle.getStyleValue(o, feature);
			if (v!=null) {
				return !v.equals(ss.getStyleValue(businessObject, feature));
			}
			return false;
		}
		return true;
	}
}
