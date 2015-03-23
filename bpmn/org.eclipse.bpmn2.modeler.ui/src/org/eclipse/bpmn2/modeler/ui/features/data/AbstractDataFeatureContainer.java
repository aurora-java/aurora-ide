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
 * @author Innar Made
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.features.data;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.DataObjectReference;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.modeler.core.features.BaseElementFeatureContainer;
import org.eclipse.bpmn2.modeler.core.features.data.MoveDataFeature;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.ui.features.AbstractDefaultDeleteFeature;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

public abstract class AbstractDataFeatureContainer extends BaseElementFeatureContainer {

	@Override
	public IMoveShapeFeature getMoveFeature(IFeatureProvider fp) {
		return new MoveDataFeature(fp);
	}

	@Override
	public ILayoutFeature getLayoutFeature(IFeatureProvider fp) {
		return null;
	}
	
	@Override
	public IResizeShapeFeature getResizeFeature(IFeatureProvider fp) {
		return new DefaultResizeShapeFeature(fp) {
			@Override
			public boolean canResizeShape(IResizeShapeContext context) {
				return false;
			}
		};
	}

	@Override
	public IDeleteFeature getDeleteFeature(IFeatureProvider fp) {
		return new AbstractDefaultDeleteFeature(fp) {

			@Override
			public void delete(IDeleteContext context) {
				EObject businessObject = BusinessObjectUtil.getBusinessObjectForPictogramElement(context.getPictogramElement());
				if (businessObject instanceof DataObject) {
					// also remove all Data Object References
					List<PictogramElement> pictogramElements = new ArrayList<PictogramElement>();
					Definitions definitions = ModelUtil.getDefinitions(businessObject);
					TreeIterator<EObject> iter = definitions.eAllContents();
					while (iter.hasNext()) {
						EObject obj = iter.next();
						if (obj instanceof DataObjectReference &&
							((DataObjectReference)obj).getDataObjectRef() == businessObject) {
							for (PictogramElement pe : Graphiti.getLinkService().getPictogramElements(getDiagram(),obj)) {
								if (pe instanceof ContainerShape && !pictogramElements.contains(pe))
									pictogramElements.add(pe);
							}
						}
					}
					for (PictogramElement pe : pictogramElements) {
						IDeleteContext dc = new DeleteContext(pe);
						IDeleteFeature df = getFeatureProvider().getDeleteFeature(dc);
						if (df.canDelete(dc))
							df.delete(dc);
					}
				}
				super.delete(context);
			}
		};
	}
}
