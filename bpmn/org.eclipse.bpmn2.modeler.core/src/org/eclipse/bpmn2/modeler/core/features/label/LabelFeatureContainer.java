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
package org.eclipse.bpmn2.modeler.core.features.label;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.modeler.core.features.DirectEditBaseElementFeature;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.features.IConnectionFeatureContainer;
import org.eclipse.bpmn2.modeler.core.features.IShapeFeatureContainer;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveConnectionDecoratorFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IReconnectionFeature;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IMoveConnectionDecoratorContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

public class LabelFeatureContainer implements IShapeFeatureContainer, IConnectionFeatureContainer {

	public static final String LINE_BREAK = "\n"; //$NON-NLS-1$
	public static final int LABEL_MARGIN = 6;
	public static final int TEXT_PADDING = 5;
	
	@Override
	public Object getApplyObject(IContext context) {
		if (context instanceof IAddContext) {
			if (context.getProperty(GraphitiConstants.PICTOGRAM_ELEMENTS) != null)
			{
				IAddContext addContext = (IAddContext) context;
				return addContext.getNewObject();
			}
		}
		else if (context instanceof IPictogramElementContext) {
			IPictogramElementContext peContext = (IPictogramElementContext) context;
			PictogramElement pe = peContext.getPictogramElement();
			if (pe instanceof Shape) {
				if (FeatureSupport.isLabelShape(pe))
					return BusinessObjectUtil.getBusinessObjectForPictogramElement(pe);
				pe = ((Shape) pe).getContainer();
				if (FeatureSupport.isLabelShape(pe))
					return BusinessObjectUtil.getBusinessObjectForPictogramElement(pe);
			}
		}
		else if (context instanceof IMoveConnectionDecoratorContext) {
			IMoveConnectionDecoratorContext mcdContext = (IMoveConnectionDecoratorContext) context;
			PictogramElement pe = mcdContext.getConnectionDecorator();
			if (FeatureSupport.isLabelShape(pe))
				return BusinessObjectUtil.getBusinessObjectForPictogramElement(pe);
		}
		else if (context instanceof ICustomContext) {
			PictogramElement[] pes = ((ICustomContext) context).getPictogramElements();
			if (pes.length==1 && FeatureSupport.isLabelShape(pes[0]))
				return BusinessObjectUtil.getFirstElementOfType(pes[0], BaseElement.class);
		}
		return null;
	}

	@Override
	public boolean canApplyTo(Object o) {
		return true;
	}
	
	@Override
	public boolean isAvailable(IFeatureProvider fp) {
		return true;
	}

	@Override
	public ICreateFeature getCreateFeature(IFeatureProvider fp) {
		return null;
	}
	
	@Override
	public ICreateConnectionFeature getCreateConnectionFeature(IFeatureProvider fp) {
		return null;
	}

	@Override
	public IAddFeature getAddFeature(IFeatureProvider fp) {
		return new AddShapeLabelFeature(fp);
	}

	@Override
	public IUpdateFeature getUpdateFeature(IFeatureProvider fp) {
		return new UpdateLabelFeature(fp);
	}

	@Override
	public IDirectEditingFeature getDirectEditingFeature(IFeatureProvider fp) {
		return new DirectEditBaseElementFeature(fp);
	}

	@Override
	public ILayoutFeature getLayoutFeature(IFeatureProvider fp) {
		return new LayoutLabelFeature(fp);
	}

	@Override
	public IRemoveFeature getRemoveFeature(IFeatureProvider fp) {
		return new RemoveLabelFeature(fp);
	}

	@Override
	public IMoveShapeFeature getMoveFeature(IFeatureProvider fp) {
		return new MoveShapeLabelFeature(fp) {

			@Override
			public boolean canMoveShape(IMoveShapeContext context) {
				return false;
			}
			
		};
	}

	@Override
	public IMoveConnectionDecoratorFeature getMoveConnectionDecoratorFeature(IFeatureProvider fp) {
		return new MoveConnectionLabelFeature(fp);
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
		return null;
	}
	
	@Override
	public ICustomFeature[] getCustomFeatures(IFeatureProvider fp) {
		return null;
	}

	@Override
	public IReconnectionFeature getReconnectionFeature(IFeatureProvider fp) {
		return null;
	}
}
