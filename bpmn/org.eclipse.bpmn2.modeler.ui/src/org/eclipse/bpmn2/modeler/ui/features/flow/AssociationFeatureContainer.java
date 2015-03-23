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
package org.eclipse.bpmn2.modeler.ui.features.flow;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.Artifact;
import org.eclipse.bpmn2.Association;
import org.eclipse.bpmn2.AssociationDirection;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2UpdateFeature;
import org.eclipse.bpmn2.modeler.core.features.BaseElementConnectionFeatureContainer;
import org.eclipse.bpmn2.modeler.core.features.DefaultDeleteBPMNShapeFeature;
import org.eclipse.bpmn2.modeler.core.features.flow.AbstractAddFlowFeature;
import org.eclipse.bpmn2.modeler.core.features.flow.AbstractCreateFlowFeature;
import org.eclipse.bpmn2.modeler.core.features.flow.AbstractReconnectFlowFeature;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.StyleUtil;
import org.eclipse.bpmn2.modeler.ui.ImageProvider;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.IReconnectionFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.ReconnectionContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeService;

public class AssociationFeatureContainer extends BaseElementConnectionFeatureContainer {

	// the property used to store the current Association's direction;
	// the value can be one of the AssociationDirection enumerations (a null
	// or empty string is the same as "None")
	public static final String ASSOCIATION_DIRECTION = "association.direction"; //$NON-NLS-1$
	public static final String ARROWHEAD_DECORATOR = "arrowhead.decorator"; //$NON-NLS-1$
	
	protected CreateConnectionContext createContext;
	
	@Override
	public boolean canApplyTo(Object o) {
		return super.canApplyTo(o) && o instanceof Association;
	}

	@Override
	public IAddFeature getAddFeature(IFeatureProvider fp) {
		return new AddAssociationFeature(fp);
	}

	@Override
	public ICreateConnectionFeature getCreateConnectionFeature(IFeatureProvider fp) {
		return new CreateAssociationFeature(fp);
	}

	@Override
	public IUpdateFeature getUpdateFeature(IFeatureProvider fp) {
		return new UpdateAssociationFeature(fp);
	}
	
	@Override
	public IReconnectionFeature getReconnectionFeature(IFeatureProvider fp) {
		return new ReconnectAssociationFeature(fp);
	}
	
	@Override
	public IDeleteFeature getDeleteFeature(IFeatureProvider fp) {
		return new DefaultDeleteBPMNShapeFeature(fp) {

			@Override
			public void delete(IDeleteContext context) {
				PictogramElement pe = context.getPictogramElement();
				if (pe instanceof Connection) {
					Connection c = (Connection) pe;
					if (c.getStart()!=null)
						AnchorUtil.deleteConnectionPoint(c.getStart().getParent());
					if (c.getEnd()!=null)
						AnchorUtil.deleteConnectionPoint(c.getEnd().getParent());
				}
				super.delete(context);
			}			
		};
	}

	public class AddAssociationFeature extends AbstractAddFlowFeature<Association> {
		public AddAssociationFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		public PictogramElement add(IAddContext context) {
			AddConnectionContext addConContext = (AddConnectionContext)context;
			Anchor sourceAnchor = addConContext.getSourceAnchor();
			Anchor targetAnchor = addConContext.getTargetAnchor();
			PictogramElement source = sourceAnchor==null ? null : sourceAnchor.getParent();
			PictogramElement target = targetAnchor==null ? null : targetAnchor.getParent();
			boolean anchorChanged = false;
			
			if (createContext!=null) {
				if (source==null) {
					source = createContext.getSourcePictogramElement();
					sourceAnchor = createContext.getSourceAnchor();
				}
				if (target==null) {
					target = createContext.getTargetPictogramElement();
					targetAnchor = createContext.getTargetAnchor();
				}
			}
			
			if (sourceAnchor==null && source instanceof FreeFormConnection) {
				Shape connectionPointShape = AnchorUtil.createConnectionPoint(getFeatureProvider(),
						(FreeFormConnection)source,
						Graphiti.getPeLayoutService().getConnectionMidpoint((FreeFormConnection)source, 0.5));
				sourceAnchor = AnchorUtil.getConnectionPointAnchor(connectionPointShape);
				anchorChanged = true;
			}
			if (targetAnchor==null && target instanceof FreeFormConnection) {
				Shape connectionPointShape = AnchorUtil.createConnectionPoint(getFeatureProvider(),
						(FreeFormConnection)target,
						Graphiti.getPeLayoutService().getConnectionMidpoint((FreeFormConnection)target, 0.5));
				targetAnchor = AnchorUtil.getConnectionPointAnchor(connectionPointShape);
				anchorChanged = true;
			}
			
			// this is silly! why are there no setters for sourceAnchor and targetAnchor in AddConnectionContext???
			if (anchorChanged) {
				AddConnectionContext newContext = new AddConnectionContext(sourceAnchor, targetAnchor);
				newContext.setSize(addConContext.getHeight(), addConContext.getWidth());
				newContext.setLocation(addConContext.getX(), addConContext.getY());
				newContext.setNewObject(getBusinessObject(addConContext));
				newContext.setTargetConnection(addConContext.getTargetConnection());
				newContext.setTargetConnectionDecorator(addConContext.getTargetConnectionDecorator());
				newContext.setTargetContainer(addConContext.getTargetContainer());
				
				context = newContext;
			}
			// we're done with this
			createContext = null;
			
			return super.add(context);
		}

		@Override
		protected Polyline createConnectionLine(Connection connection) {
			Polyline connectionLine = super.createConnectionLine(connection);
			connectionLine.setLineWidth(2);
			connectionLine.setLineStyle(LineStyle.DOT);
			return connectionLine;
		}

		@Override
		protected void decorateConnection(IAddConnectionContext context, Connection connection, Association businessObject) {
			setAssociationDirection(connection, businessObject);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2AddFeature#getBusinessObjectType()
		 */
		@Override
		public Class getBusinessObjectType() {
			return Association.class;
		}
	}


	public class CreateAssociationFeature extends AbstractCreateFlowFeature<Association, BaseElement, BaseElement> {

		public CreateAssociationFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		public boolean isAvailable(IContext context) {
			if (!isModelObjectEnabled(Bpmn2Package.eINSTANCE.getAssociation()))
				return false;
			return super.isAvailable(context);
		}

		@Override
		public boolean canCreate(ICreateConnectionContext context) {
			if (!super.canCreate(context))
				return false;
			
			if ( context.getTargetPictogramElement() instanceof FreeFormConnection ) {
				// TODO: fix this so it works with Manhattan router
				return true;
			}
			
			BaseElement source = getSourceBo(context);
			BaseElement target = getTargetBo(context);
			if (source!=null && target!=null) {
				if (source instanceof BoundaryEvent && target instanceof Activity)
					return true;
				
				if (source instanceof Artifact || target instanceof Artifact)
					return true;
			}			
			return false;
		}

		@Override
		public Connection create(ICreateConnectionContext context) {
			// save the CreateContext because we'll need it in AddFeature
			createContext = (CreateConnectionContext)context;
			Anchor sourceAnchor = createContext.getSourceAnchor();
			Anchor targetAnchor = createContext.getTargetAnchor();
			PictogramElement source = createContext.getSourcePictogramElement();
			PictogramElement target = createContext.getTargetPictogramElement();
			
			if (sourceAnchor==null && source instanceof FreeFormConnection) {
				Shape connectionPointShape = AnchorUtil.createConnectionPoint(getFeatureProvider(),
						(FreeFormConnection)source,
						Graphiti.getPeLayoutService().getConnectionMidpoint((FreeFormConnection)source, 0.5));
				sourceAnchor = AnchorUtil.getConnectionPointAnchor(connectionPointShape);
				createContext.setSourceAnchor(sourceAnchor);
			}
			if (targetAnchor==null && target instanceof FreeFormConnection) {
				Shape connectionPointShape = AnchorUtil.createConnectionPoint(getFeatureProvider(),
						(FreeFormConnection)target,
						Graphiti.getPeLayoutService().getConnectionMidpoint((FreeFormConnection)target, 0.5));
				targetAnchor = AnchorUtil.getConnectionPointAnchor(connectionPointShape);
				createContext.setTargetAnchor(targetAnchor);
			}

			Connection connection = super.create(context);
			Association association = getBusinessObject(context);
			if (association.getSourceRef() instanceof BoundaryEvent && association.getTargetRef() instanceof Activity) {
				association.setAssociationDirection(AssociationDirection.ONE);
			}
			return connection;
		}

		@Override
		protected String getStencilImageId() {
			return ImageProvider.IMG_16_ASSOCIATION;
		}

		@Override
		protected Class<BaseElement> getSourceClass() {
			return BaseElement.class;
		}

		@Override
		protected Class<BaseElement> getTargetClass() {
			return BaseElement.class;
		}

		@Override
		protected BaseElement getSourceBo(ICreateConnectionContext context) {
			Anchor anchor = getSourceAnchor(context);
			if (anchor != null && anchor.getParent() instanceof Shape) {
				Shape shape = (Shape) anchor.getParent();
				Connection connection = AnchorUtil.getConnectionPointOwner(shape);
				if (connection!=null) {
					return BusinessObjectUtil.getFirstElementOfType(connection, getTargetClass());
				}
				return BusinessObjectUtil.getFirstElementOfType(shape, getTargetClass());
			}
			else if (context.getSourcePictogramElement() instanceof Connection) {
				Connection connection = (Connection) context.getSourcePictogramElement();
				return BusinessObjectUtil.getFirstBaseElement(connection);
			}
			return null;
		}

		@Override
		protected BaseElement getTargetBo(ICreateConnectionContext context) {
			Anchor anchor = getTargetAnchor(context);
			if (anchor != null && anchor.getParent() instanceof Shape) {
				Shape shape = (Shape) anchor.getParent();
				Connection connection = AnchorUtil.getConnectionPointOwner(shape);
				if (connection!=null) {
					return BusinessObjectUtil.getFirstElementOfType(connection, getTargetClass());
				}
				return BusinessObjectUtil.getFirstElementOfType(shape, getTargetClass());
			}
			else if (context.getTargetPictogramElement() instanceof Connection) {
				Connection connection = (Connection) context.getTargetPictogramElement();
				return BusinessObjectUtil.getFirstBaseElement(connection);
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2CreateConnectionFeature#getBusinessObjectClass()
		 */
		@Override
		public EClass getBusinessObjectClass() {
			return Bpmn2Package.eINSTANCE.getAssociation();
		}
	}
	
	private static void setAssociationDirection(Connection connection, Association businessObject) {
		IPeService peService = Graphiti.getPeService();
		IGaService gaService = Graphiti.getGaService();
		String newDirection = businessObject.getAssociationDirection().toString();
		if (newDirection==null || newDirection.isEmpty())
			newDirection = AssociationDirection.NONE.toString();
		String oldDirection = peService.getPropertyValue(connection, ASSOCIATION_DIRECTION);
		if (oldDirection==null || oldDirection.isEmpty())
			oldDirection = AssociationDirection.NONE.toString();

		if (!oldDirection.equals(newDirection)) {
			ConnectionDecorator sourceDecorator = null;
			ConnectionDecorator targetDecorator = null;
			for (ConnectionDecorator d : connection.getConnectionDecorators()) {
				String s = peService.getPropertyValue(d, ARROWHEAD_DECORATOR);
				if (s!=null) {
					if (s.equals("source")) //$NON-NLS-1$
						sourceDecorator = d;
					else if (s.equals("target")) //$NON-NLS-1$
						targetDecorator = d;
				}
			}
			
			boolean needSource = false;
			boolean needTarget = false;
			if (newDirection.equals(AssociationDirection.ONE.toString())) {
				needTarget = true;
			}
			else if (newDirection.equals(AssociationDirection.BOTH.toString())) {
				needSource = needTarget = true;
			}
			
			final int w = 7;
			final int l = 13;
			if (needSource) {
				if (sourceDecorator==null) {
					sourceDecorator = peService.createConnectionDecorator(connection, false, 0.0, true);
					Polyline arrowhead = gaService.createPolyline(sourceDecorator, new int[] { -l, w, 0, 0, -l, -w });
					StyleUtil.applyStyle(arrowhead, businessObject);
					peService.setPropertyValue(sourceDecorator, ARROWHEAD_DECORATOR, "source"); //$NON-NLS-1$
				}
			}
			else {
				if (sourceDecorator!=null)
					connection.getConnectionDecorators().remove(sourceDecorator);				
			}
			if (needTarget) {
				if (targetDecorator==null) {
					targetDecorator = peService.createConnectionDecorator(connection, false, 1.0, true);
					Polyline arrowhead = gaService.createPolyline(targetDecorator, new int[] { -l, w, 0, 0, -l, -w });
					StyleUtil.applyStyle(arrowhead, businessObject);
					peService.setPropertyValue(targetDecorator, ARROWHEAD_DECORATOR, "target"); //$NON-NLS-1$
				}
			}
			else {
				if (targetDecorator!=null)
					connection.getConnectionDecorators().remove(targetDecorator);				
			}
		
			// update the property value in the Connection PictogramElement
			peService.setPropertyValue(connection, ASSOCIATION_DIRECTION, newDirection);
		}

	}
	
	public static class UpdateAssociationFeature extends AbstractBpmn2UpdateFeature {

		public UpdateAssociationFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		public boolean canUpdate(IUpdateContext context) {
			if (context.getPictogramElement() instanceof Connection) {
				return BusinessObjectUtil.getFirstElementOfType(
						context.getPictogramElement(), Association.class) != null;
			}
			return false;
		}

		@Override
		public IReason updateNeeded(IUpdateContext context) {
			if (canUpdate(context)) {
				Connection connection = (Connection) context.getPictogramElement();
				Association businessObject = BusinessObjectUtil.getFirstElementOfType(context.getPictogramElement(),
						Association.class);
				String newDirection = businessObject.getAssociationDirection().toString();
				if (newDirection==null || newDirection.isEmpty())
					newDirection = AssociationDirection.NONE.toString();
				String oldDirection = Graphiti.getPeService().getPropertyValue(connection, ASSOCIATION_DIRECTION);
				if (oldDirection==null || oldDirection.isEmpty())
					oldDirection = AssociationDirection.NONE.toString();
	
				if (!oldDirection.equals(newDirection)) {
					return Reason.createTrueReason("Association Direction");
				}
			}
			return Reason.createFalseReason();
		}

		@Override
		public boolean update(IUpdateContext context) {
			Connection connection = (Connection) context.getPictogramElement();
			Association businessObject = BusinessObjectUtil.getFirstElementOfType(context.getPictogramElement(),
					Association.class);
			setAssociationDirection(connection, businessObject);
			return true;
		}
	}

	public static class ReconnectAssociationFeature extends AbstractReconnectFlowFeature {

		public ReconnectAssociationFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		public boolean canReconnect(IReconnectionContext context) {
			BaseElement targetElement = BusinessObjectUtil.getFirstElementOfType(context.getTargetPictogramElement(), BaseElement.class);
			if (targetElement instanceof Association)
				return false;
			PictogramElement targetPictogramElement = context.getTargetPictogramElement();
			if (targetPictogramElement instanceof FreeFormConnection) {
				return true;
			}
			return super.canReconnect(context);
		}

		@Override
		protected Class<? extends EObject> getTargetClass() {
			return BaseElement.class;
		}

		@Override
		protected Class<? extends EObject> getSourceClass() {
			return BaseElement.class;
		}

		@Override
		public void preReconnect(IReconnectionContext context) {
			PictogramElement targetPictogramElement = context.getTargetPictogramElement();
			if (targetPictogramElement instanceof Connection) {
				Shape connectionPointShape = AnchorUtil.createConnectionPoint(
						getFeatureProvider(),
						(Connection)targetPictogramElement,
						context.getTargetLocation());
				
				ReconnectionContext rc = (ReconnectionContext) context;
				rc.setNewAnchor(AnchorUtil.getConnectionPointAnchor(connectionPointShape));
				rc.setTargetPictogramElement(connectionPointShape);
			}
			super.preReconnect(context);
		}

		@Override
		public void postReconnect(IReconnectionContext context) {
			if (AnchorUtil.isConnectionPoint(context.getOldAnchor().getParent())) {
				AnchorUtil.deleteConnectionPoint(context.getOldAnchor().getParent());
			}
			super.postReconnect(context);
		}
	} 
	
}