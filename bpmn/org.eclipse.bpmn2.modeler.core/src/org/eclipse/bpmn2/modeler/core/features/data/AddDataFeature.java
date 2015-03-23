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
 * @author Ivar Meikas
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.features.data;

import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2AddFeature;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.features.label.AddShapeLabelFeature;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.core.utils.StyleUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ITargetContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

public abstract class AddDataFeature<T extends ItemAwareElement> extends AbstractBpmn2AddFeature<T> {

	public AddDataFeature(IFeatureProvider fp) {
		super(fp);
	}

	public IAddFeature getAddLabelFeature(IFeatureProvider fp) {
		return new AddShapeLabelFeature(fp);
	}

	@Override
	public boolean canAdd(IAddContext context) {
		return FeatureSupport.isValidDataTarget(context);
	}

	@Override
	public PictogramElement add(IAddContext context) {
		T businessObject = getBusinessObject(context);
 
		int width = getWidth(context);
		int height = getHeight(context);
		int e = 10;
		
		ContainerShape containerShape = peService.createContainerShape(context.getTargetContainer(), true);
		Rectangle invisibleRect = gaService.createInvisibleRectangle(containerShape);
		gaService.setLocationAndSize(invisibleRect, context.getX(), context.getY(), width, height);

		Shape rectShape = peService.createShape(containerShape, false);
		Polygon rect = gaService.createPolygon(rectShape, new int[] { 0, 0, width - e, 0, width, e, width, height, 0,
				height });
		rect.setLineWidth(1);
		StyleUtil.applyStyle(rect,businessObject);

		int p = width - e - 1;
		Polyline edge = gaService.createPolyline(rect, new int[] { p, 0, p, e + 1, width, e + 1 });
		edge.setForeground(manageColor(StyleUtil.CLASS_FOREGROUND));
		edge.setLineWidth(1);

		// Create the "is collection" marker
			int whalf = width / 2;
			createCollectionShape(containerShape, new int[] { whalf - 2, height - 8, whalf - 2, height });
			createCollectionShape(containerShape, new int[] { whalf, height - 8, whalf, height });
			createCollectionShape(containerShape, new int[] { whalf + 2, height - 8, whalf + 2, height });

			String value = "false"; //$NON-NLS-1$
			EStructuralFeature feature = ((EObject)businessObject).eClass().getEStructuralFeature("isCollection"); //$NON-NLS-1$
			if (feature!=null && businessObject.eGet(feature)!=null)
				value = ((Boolean)businessObject.eGet(feature)).toString();

		peService.setPropertyValue(containerShape, GraphitiConstants.COLLECTION_PROPERTY, value);

		boolean isImport = context.getProperty(GraphitiConstants.IMPORT_PROPERTY) != null;
		createDIShape(containerShape, businessObject, !isImport);

		// hook for subclasses to inject extra code
		((AddContext)context).setWidth(width);
		((AddContext)context).setHeight(height);
		decorateShape(context, containerShape, businessObject);

		peService.createChopboxAnchor(containerShape);
		AnchorUtil.addFixedPointAnchors(containerShape, invisibleRect);

		return containerShape;
	}

	private Shape createCollectionShape(ContainerShape container, int[] xy) {
		Shape collectionShape = peService.createShape(container, false);
		Polyline line = gaService.createPolyline(collectionShape, xy);
		line.setForeground(manageColor(StyleUtil.CLASS_FOREGROUND));
		line.setLineWidth(1);
		line.setLineVisible(false);
		peService.setPropertyValue(collectionShape, GraphitiConstants.HIDEABLE_PROPERTY, Boolean.toString(true));
		return collectionShape;
	}

	protected boolean isHorizontal(ITargetContext context) {
		return false;
	}
	
	public abstract String getName(T t);
}