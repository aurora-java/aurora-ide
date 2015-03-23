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
package org.eclipse.bpmn2.modeler.ui.features.artifact;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Group;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2AddFeature;
import org.eclipse.bpmn2.modeler.core.features.AbstractUpdateBaseElementFeature;
import org.eclipse.bpmn2.modeler.core.features.BaseElementFeatureContainer;
import org.eclipse.bpmn2.modeler.core.features.DefaultMoveBPMNShapeFeature;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.features.MultiUpdateFeature;
import org.eclipse.bpmn2.modeler.core.features.artifact.AbstractCreateArtifactFeature;
import org.eclipse.bpmn2.modeler.core.features.label.AddShapeLabelFeature;
import org.eclipse.bpmn2.modeler.core.features.label.UpdateLabelFeature;
import org.eclipse.bpmn2.modeler.core.preferences.ShapeStyle.LabelPosition;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.core.utils.StyleUtil;
import org.eclipse.bpmn2.modeler.ui.ImageProvider;
import org.eclipse.bpmn2.modeler.ui.features.AbstractDefaultDeleteFeature;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;
import org.eclipse.graphiti.mm.algorithms.AbstractText;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeService;

public class GroupFeatureContainer extends BaseElementFeatureContainer {
	protected final IGaService gaService = Graphiti.getGaService();
	protected final IPeService peService = Graphiti.getPeService();

	@Override
	public boolean canApplyTo(Object o) {
		return super.canApplyTo(o) && o instanceof Group;
	}

	@Override
	public ICreateFeature getCreateFeature(IFeatureProvider fp) {
		return new CreateGroupFeature(fp);
	}

	@Override
	public IAddFeature getAddFeature(IFeatureProvider fp) {
		return new AddGroupFeature(fp);
	}

	@Override
	public IDeleteFeature getDeleteFeature(IFeatureProvider fp) {
		return new DeleteGroupFeature(fp);
	}

	@Override
	public IUpdateFeature getUpdateFeature(IFeatureProvider fp) {
		MultiUpdateFeature multiUpdate = new MultiUpdateFeature(fp);
		multiUpdate.addFeature(new UpdateGroupFeature(fp));
		UpdateLabelFeature updateLabelFeature = new UpdateLabelFeature(fp) {
			
			@Override
			protected boolean hasLabel(BaseElement element) {
				return element instanceof Group;
			}

			@Override
			protected String getLabelString(BaseElement element) {
				String name = ""; //$NON-NLS-1$
				Group group = (Group) element;
				if (group.getCategoryValueRef()!=null)
					name = ExtendedPropertiesProvider.getTextValue(group.getCategoryValueRef());
				return name;
			}

			@Override
			protected LabelPosition getLabelPosition(AbstractText text) {
				return LabelPosition.TOP;
			}
		};
		multiUpdate.addFeature(updateLabelFeature);
		return multiUpdate;
	}

	@Override
	public IDirectEditingFeature getDirectEditingFeature(IFeatureProvider fp) {
		return null;
	}

	@Override
	public ILayoutFeature getLayoutFeature(IFeatureProvider fp) {
		return null;
	}

	@Override
	public ICustomFeature[] getCustomFeatures(IFeatureProvider fp) {
		return null;
	}

	@Override
	public IMoveShapeFeature getMoveFeature(IFeatureProvider fp) {
		return new MoveGroupFeature(fp);
	}

	@Override
	public IResizeShapeFeature getResizeFeature(IFeatureProvider fp) {
		return new ResizeGroupFeature(fp);
	}

	public class AddGroupFeature extends AbstractBpmn2AddFeature<Group> {
		public AddGroupFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		public boolean canAdd(IAddContext context) {
			return true;
		}

		public IAddFeature getAddLabelFeature(IFeatureProvider fp) {
			return new AddShapeLabelFeature(fp);
		}

		@Override
		public PictogramElement add(IAddContext context) {
			IGaService gaService = Graphiti.getGaService();
			IPeService peService = Graphiti.getPeService();
			Group businessObject = getBusinessObject(context);

			int x = context.getX();
			int y = context.getY();
			int width = this.getWidth(context);
			int height = this.getHeight(context);
			
			if (!(context.getTargetContainer() instanceof Diagram)) {
				ILocation loc = Graphiti.getPeService().getLocationRelativeToDiagram(context.getTargetContainer());
				x += loc.getX();
				y += loc.getY();
				((AddContext)context).setTargetContainer(this.getDiagram());
			}

			ContainerShape containerShape = peService.createContainerShape(context.getTargetContainer(), true);
			link(containerShape, businessObject);
			
			// NOTE: We do not want a Group Shape to be a graphiti shape container for
			// anything that is added or moved into the Group, so instead of using a
			// rectangle for the Group shape, we'll use a polyline instead.
//			RoundedRectangle rect = gaService.createRoundedRectangle(containerShape, 5, 5);
//			rect.setFilled(false);
//			rect.setLineWidth(2);
//			rect.setForeground(manageColor(StyleUtil.CLASS_FOREGROUND));
//			rect.setLineStyle(LineStyle.DASHDOT);
//			gaService.setLocationAndSize(rect, x, y, width, height);
//			peService.createChopboxAnchor(containerShape);
//			AnchorUtil.addFixedPointAnchors(containerShape, rect);
			int xy[] = new int[] {0, 0, width, 0, width, height, 0, height, 0, 0};
			Polyline rect = gaService.createPolyline(containerShape, xy);
			rect.setLineWidth(3);
			rect.setForeground(manageColor(StyleUtil.CLASS_FOREGROUND));
			rect.setLineStyle(LineStyle.DASHDOT);
			gaService.setLocationAndSize(rect, x, y, width, height);
			peService.createChopboxAnchor(containerShape);
			AnchorUtil.addFixedPointAnchors(containerShape, rect);
//
			
			boolean isImport = context.getProperty(GraphitiConstants.IMPORT_PROPERTY) != null;
			createDIShape(containerShape, businessObject, !isImport);
			
			// hook for subclasses to inject extra code
			((AddContext)context).setWidth(width);
			((AddContext)context).setHeight(height);
			decorateShape(context, containerShape, businessObject);

			return containerShape;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2AddFeature#decorateShape(org.eclipse.graphiti.features.context.IAddContext, org.eclipse.graphiti.mm.pictograms.ContainerShape, org.eclipse.bpmn2.BaseElement)
		 */
		protected void decorateShape(IAddContext context, ContainerShape containerShape, Group businessObject) {
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2AddFeature#getBusinessObjectType()
		 */
		@Override
		public Class getBusinessObjectType() {
			return Group.class;
		}
	}

	public static class CreateGroupFeature extends AbstractCreateArtifactFeature<Group> {

		public CreateGroupFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		public String getStencilImageId() {
			return ImageProvider.IMG_16_GROUP;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2CreateFeature#getBusinessObjectClass()
		 */
		@Override
		public EClass getBusinessObjectClass() {
			return Bpmn2Package.eINSTANCE.getGroup();
		}
	}
	
	public static class UpdateGroupFeature extends AbstractUpdateBaseElementFeature {

		public UpdateGroupFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		public boolean update(IUpdateContext context) {
			ContainerShape groupShape = (ContainerShape)context.getPictogramElement();
			List<ContainerShape> containedShapes = FeatureSupport.findGroupedShapes(groupShape);
			FeatureSupport.updateCategoryValues(getFeatureProvider(), containedShapes);
			return true;
		}
	}
	
	public static class MoveGroupFeature extends DefaultMoveBPMNShapeFeature {

		public MoveGroupFeature(IFeatureProvider fp) {
			super(fp);
		}
		List<ContainerShape> containedShapes = new ArrayList<ContainerShape>();

		@Override
		public boolean canMoveShape(IMoveShapeContext context) {
			return true;
		}

		@Override
		protected void preMoveShape(IMoveShapeContext context) {
			super.preMoveShape(context);
			ContainerShape groupShape = (ContainerShape) context.getShape();
			ContainerShape container = context.getTargetContainer();
			if (!(container instanceof Diagram)) {
				ILocation loc = Graphiti.getPeService().getLocationRelativeToDiagram(container);
				int x = context.getX() + loc.getX();
				int y = context.getY() + loc.getY();
				((MoveShapeContext)context).setX(x);
				((MoveShapeContext)context).setY(y);
				((MoveShapeContext)context).setDeltaX(x - preMoveLoc.getX());
				((MoveShapeContext)context).setDeltaY(y - preMoveLoc.getY());
				((MoveShapeContext)context).setTargetContainer(getDiagram());
			}

			// find all shapes that are inside this Group
			// these will be moved along with the Group
			containedShapes = FeatureSupport.findGroupedShapes(groupShape);
		}

		@Override
		protected void postMoveShape(IMoveShapeContext context) {

			super.postMoveShape(context);
			
			ContainerShape groupShape = (ContainerShape) context.getShape();
			for (ContainerShape shape : containedShapes) {
				if (!FeatureSupport.isLabelShape(shape)) {
					ILocation loc = Graphiti.getPeService().getLocationRelativeToDiagram(shape);
					int x = loc.getX() + context.getDeltaX();
					int y = loc.getY() + context.getDeltaY();
					MoveShapeContext mc = new MoveShapeContext(shape);
					mc.setSourceContainer(shape.getContainer());
					mc.setTargetContainer(shape.getContainer());
					mc.setX(x);
					mc.setY(y);
					IMoveShapeFeature mf = getFeatureProvider().getMoveShapeFeature(mc);
					mf.moveShape(mc);
				}
			}
			for (ContainerShape cs : FeatureSupport.findGroupedShapes(groupShape)) {
				if (!containedShapes.contains(cs)) {
					containedShapes.add(cs);
				}
			}
			FeatureSupport.updateCategoryValues(getFeatureProvider(), containedShapes);
		}
	}

	public class ResizeGroupFeature extends DefaultResizeShapeFeature {

		public ResizeGroupFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		public void resizeShape(IResizeShapeContext context) {
			ContainerShape groupShape = (ContainerShape) context.getPictogramElement();
			List<ContainerShape> containedShapesBeforeResize = FeatureSupport.findGroupedShapes(groupShape);

			int x = context.getX();
			int y = context.getY();
			int w = context.getWidth();
			int h = context.getHeight();
			Polyline rect = (Polyline) groupShape.getGraphicsAlgorithm();
			Point p;
			p = rect.getPoints().get(1);
			p.setX(w);
			p = rect.getPoints().get(2);
			p.setX(w);
			p.setY(h);
			p = rect.getPoints().get(3);
			p.setY(h);
			Graphiti.getGaService().setLocationAndSize(rect, x, y, w, h);
			for (Shape shape : groupShape.getChildren()) {
				if (shape.getGraphicsAlgorithm() instanceof AbstractText) {
					AbstractText text = (AbstractText) shape.getGraphicsAlgorithm();
					gaService.setLocationAndSize(text, 0, 0, w, text.getHeight());
				}
			}

			DIUtils.updateDIShape(groupShape);
			FeatureSupport.updateLabel(getFeatureProvider(), groupShape, null);

			List<ContainerShape> containedShapesAfterResize = FeatureSupport.findGroupedShapes(groupShape);
			FeatureSupport.updateCategoryValues(getFeatureProvider(), containedShapesBeforeResize);
			FeatureSupport.updateCategoryValues(getFeatureProvider(), containedShapesAfterResize);
			
			FeatureSupport.updateConnections(getFeatureProvider(), groupShape);
		}
	}
	
	public class DeleteGroupFeature extends AbstractDefaultDeleteFeature {

		public DeleteGroupFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		public void delete(IDeleteContext context) {
			ContainerShape groupShape = (ContainerShape) context.getPictogramElement();
			List<ContainerShape> containedShapes = FeatureSupport.findGroupedShapes(groupShape);
			
			super.delete(context);

			FeatureSupport.updateCategoryValues(getFeatureProvider(), containedShapes);
		}
		
	}
}