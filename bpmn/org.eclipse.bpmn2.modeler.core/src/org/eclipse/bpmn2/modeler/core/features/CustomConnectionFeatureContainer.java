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
package org.eclipse.bpmn2.modeler.core.features;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.modeler.core.runtime.CustomTaskImageProvider.IconSize;
import org.eclipse.bpmn2.modeler.core.features.Messages;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.IExecutionInfo;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveConnectionDecoratorFeature;
import org.eclipse.graphiti.features.IReconnectionFeature;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IAreaContext;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.osgi.util.NLS;

/**
 * The Feature Container for Custom Connections.
 * <p>
 * This class must be extended by contributing plug-ins that want to extend any
 * of the BPMN2 connection elements, e.g. {@link org.eclipse.bpmn2.SequenceFlow}, {@link org.eclipse.bpmn2.Association},
 * {@link org.eclipse.bpmn2.MessageFlow} and
 * {@link org.eclipse.bpmn2.ConversationLink}
 * <p>
 * See the {@code <customTask>} element of the
 * {@code org.eclipse.bpmn2.modeler.runtime} extension point.
 */
public class CustomConnectionFeatureContainer extends CustomElementFeatureContainer implements
		IConnectionFeatureContainer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.bpmn2.modeler.core.features.CustomElementFeatureContainer
	 * #getDescription()
	 */
	public String getDescription() {
		if (customTaskDescriptor != null)
			return customTaskDescriptor.getDescription();
		return Messages.CustomConnectionFeatureContainer_Description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.bpmn2.modeler.core.features.IConnectionFeatureContainer#
	 * getCreateConnectionFeature
	 * (org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public ICreateConnectionFeature getCreateConnectionFeature(IFeatureProvider fp) {
		return new CreateCustomConnectionFeature(fp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.bpmn2.modeler.core.features.CustomElementFeatureContainer
	 * #getAddFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public IAddFeature getAddFeature(IFeatureProvider fp) {
		return new AddCustomConnectionFeature(fp);
	}

	/**
	 * The {@code CreateFeature} base class for creating custom connections.
	 * <p>
	 * Custom Connections contributed to the editor <b>MUST</b> subclass this in
	 * their FeatureContainer implementation.
	 * <p>
	 * The creation process copies the modelObject ID string into the Graphiti
	 * Create Context during the construction phase, then migrates that ID into
	 * the created {@code PictogramElement}. This is necessary because the ID
	 * must be associated with the {@code PictogramElement} to allow our Feature
	 * Provider to correctly identify the Custom Task.
	 * <p>
	 * Custom Elements are not available to the Context Button Pad because the
	 * Graphiti {@code CreateConnectionCommand} constructs a single
	 * {@code ICreateConnectionContext}, and reuses it for all command/context
	 * pairs that are collected for display and user selection in a popup menu.
	 * See
	 * {@link org.eclipse.graphiti.ui.internal.command.CreateConnectionCommand#execute()}
	 * . Each command/context pair is first tested with
	 * {@link org.eclipse.graphiti.internal.command#canExecute()} which
	 * eventually calls our {@link CreateCustomConnectionFeature#canCreate()}
	 * where we insert the Custom Element ID into the context properties. Thus,
	 * all command/context pairs will contain this ID and will attempt to create
	 * a Custom Element when executed.
	 */
	public class CreateCustomConnectionFeature extends
			AbstractBpmn2CreateConnectionFeature<BaseElement, EObject, EObject> {

		/** The create feature delegate. */
		protected AbstractBpmn2CreateConnectionFeature createFeatureDelegate;

		/** Our own copies of create name and description because the ones in super are private **/
		protected String name;
		protected String description;
		
		/**
		 * Instantiates a new {@code CreateFeature} for custom connections. If
		 * the name and/or description are null or empty strings then they are
		 * fetched from the Create Feature delegate when required.
		 *
		 * @param fp the Feature Provider
		 * @param name the name of the element being created
		 * @param description the description of the Create Feature
		 */
		public CreateCustomConnectionFeature(IFeatureProvider fp, String name, String description) {
			super(fp);
			IConnectionFeatureContainer fc = (IConnectionFeatureContainer) getFeatureContainer(fp);
			createFeatureDelegate = (AbstractBpmn2CreateConnectionFeature) fc.getCreateConnectionFeature(fp);
			Assert.isNotNull(createFeatureDelegate);
			this.name = name;
			this.description = description;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2CreateConnectionFeature#getCreateName()
		 */
		@Override
		public String getCreateName() {
			if (name!=null && !name.isEmpty())
				return name;
			return createFeatureDelegate.getCreateName();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.bpmn2.modeler.core.features.
		 * AbstractBpmn2CreateConnectionFeature#getCreateDescription()
		 */
		@Override
		public String getCreateDescription() {
			if (description!=null && !description.isEmpty())
				return description;
			return createFeatureDelegate.getCreateDescription();
		}

		/**
		 * Alternate constructor.
		 *
		 * @param fp the Feature Provider
		 */
		public CreateCustomConnectionFeature(IFeatureProvider fp) {
			this(fp, customTaskDescriptor.getName(), NLS.bind(Messages.CustomElementFeatureContainer_Create,
					customTaskDescriptor.getName()));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.bpmn2.modeler.core.features.
		 * AbstractBpmn2CreateConnectionFeature
		 * #isAvailable(org.eclipse.graphiti.features.context.IContext)
		 */
		@Override
		public boolean isAvailable(IContext context) {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.graphiti.features.impl.AbstractFeature#
		 * addGraphicalRepresentation
		 * (org.eclipse.graphiti.features.context.IAreaContext,
		 * java.lang.Object)
		 */
		@Override
		protected PictogramElement addGraphicalRepresentation(IAreaContext context, Object newObject) {

			// create a new AddContext and copy our ID into it.
			IAddContext addContext = new AddContext(context, newObject);
			setId(addContext, getId());

			// create the PE and copy our ID into its properties.
			PictogramElement pe = getFeatureProvider().addIfPossible(addContext);
			Graphiti.getPeService().setPropertyValue(pe, GraphitiConstants.CUSTOM_ELEMENT_ID, id);

			return pe;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.graphiti.func.ICreateConnection#canCreate(org.eclipse
		 * .graphiti.features.context.ICreateConnectionContext)
		 */
		@Override
		public boolean canCreate(ICreateConnectionContext context) {
			// copy our ID into the CreateContext - this is where it all starts!
			setId(context, id);
			return createFeatureDelegate.canCreate(context);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.bpmn2.modeler.core.features.
		 * AbstractBpmn2CreateConnectionFeature
		 * #canStartConnection(org.eclipse.graphiti
		 * .features.context.ICreateConnectionContext)
		 */
		@Override
		public boolean canStartConnection(ICreateConnectionContext context) {
			setId(context, id);
			return createFeatureDelegate.canStartConnection(context);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.bpmn2.modeler.core.features.
		 * AbstractBpmn2CreateConnectionFeature
		 * #createBusinessObject(org.eclipse.
		 * graphiti.features.context.ICreateConnectionContext)
		 */
		@Override
		public BaseElement createBusinessObject(ICreateConnectionContext context) {
			BaseElement businessObject = createFeatureDelegate.createBusinessObject(context);
			customTaskDescriptor.populateObject(businessObject, true);
			return businessObject;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.bpmn2.modeler.core.features.AbstractCreateFlowElementFeature
		 * #getFlowElementClass()
		 */
		@Override
		public EClass getBusinessObjectClass() {
			return createFeatureDelegate.getBusinessObjectClass();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.graphiti.func.ICreateConnection#create(org.eclipse.graphiti
		 * .features.context.ICreateConnectionContext)
		 */
		@Override
		public Connection create(ICreateConnectionContext context) {
			// Our Custom Task ID should have already been set in canCreate()
			// if not, we have a problem; in other words, canCreate() MUST have
			// been called by the framework before create()
			Assert.isNotNull(getId(context));
			return createFeatureDelegate.create(context);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature
		 * #getCreateImageId()
		 */
		@Override
		public String getCreateImageId() {
			String icon = customTaskDescriptor.getIcon();
			if (icon != null) {
				String id = customTaskDescriptor.getImageId(icon, IconSize.SMALL);
				if (id != null)
					return id;
			}
			return createFeatureDelegate.getCreateImageId();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature
		 * #getCreateLargeImageId()
		 */
		@Override
		public String getCreateLargeImageId() {
			String icon = customTaskDescriptor.getIcon();
			if (icon != null) {
				String id = customTaskDescriptor.getImageId(icon, IconSize.LARGE);
				if (id != null)
					return id;
			}
			return createFeatureDelegate.getCreateLargeImageId();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.bpmn2.modeler.core.features.
		 * AbstractBpmn2CreateConnectionFeature#getSourceClass()
		 */
		@Override
		protected Class<EObject> getSourceClass() {
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.bpmn2.modeler.core.features.
		 * AbstractBpmn2CreateConnectionFeature#getTargetClass()
		 */
		@Override
		protected Class<EObject> getTargetClass() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	/**
	 * The {@code AddFeature} base class for creating custom connections.
	 * <p>
	 * Custom Connections contributed to the editor <b>MUST</b> subclass this in
	 * their FeatureContainer implementation.
	 */
	public class AddCustomConnectionFeature extends AbstractBpmn2AddFeature<BaseElement> {

		/** The add feature delegate. */
		protected AbstractBpmn2AddFeature<BaseElement> addFeatureDelegate;

		/**
		 * Instantiates a new {@code AddConnectionFeature} for custom connections.
		 *
		 * @param fp the Feature Provider
		 */
		public AddCustomConnectionFeature(IFeatureProvider fp) {
			super(fp);
			addFeatureDelegate = (AbstractBpmn2AddFeature) getFeatureContainer(fp).getAddFeature(fp);
			Assert.isNotNull(addFeatureDelegate);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.graphiti.func.IAdd#add(org.eclipse.graphiti.features.
		 * context.IAddContext)
		 */
		@Override
		public PictogramElement add(IAddContext context) {
			PictogramElement pe = addFeatureDelegate.add(context);
			// make sure everyone knows that this PE is a custom task
			if (pe != null)
				peService.setPropertyValue(pe, GraphitiConstants.CUSTOM_ELEMENT_ID, getId());

			return pe;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.bpmn2.modeler.core.features.IBpmn2AddFeature#
		 * getBusinessObject(org.eclipse.graphiti.features.context.IAddContext)
		 */
		@Override
		public BaseElement getBusinessObject(IAddContext context) {
			// TODO Auto-generated method stub
			return addFeatureDelegate.getBusinessObject(context);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.bpmn2.modeler.core.features.IBpmn2AddFeature#
		 * putBusinessObject(org.eclipse.graphiti.features.context.IAddContext,
		 * org.eclipse.emf.ecore.EObject)
		 */
		@Override
		public void putBusinessObject(IAddContext context, BaseElement businessObject) {
			addFeatureDelegate.putBusinessObject(context, businessObject);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.bpmn2.modeler.core.features.IBpmn2AddFeature#postExecute
		 * (org.eclipse.graphiti.IExecutionInfo)
		 */
		@Override
		public void postExecute(IExecutionInfo executionInfo) {
			addFeatureDelegate.postExecute(executionInfo);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.graphiti.func.IAdd#canAdd(org.eclipse.graphiti.features
		 * .context.IAddContext)
		 */
		@Override
		public boolean canAdd(IAddContext context) {
			return addFeatureDelegate.canAdd(context);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2AddFeature#getAddLabelFeature(org.eclipse.graphiti.features.IFeatureProvider)
		 */
		@Override
		public IAddFeature getAddLabelFeature(IFeatureProvider fp) {
			// TODO Auto-generated method stub
			return addFeatureDelegate.getAddLabelFeature(fp);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2AddFeature#getHeight()
		 */
		@Override
		public int getHeight(IAddContext context) {
			return addFeatureDelegate.getHeight(context);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2AddFeature#getWidth()
		 */
		@Override
		public int getWidth(IAddContext context) {
			return addFeatureDelegate.getWidth(context);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2AddFeature#getBusinessObjectType()
		 */
		@Override
		public Class getBusinessObjectType() {
			return addFeatureDelegate.getBusinessObjectType();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.bpmn2.modeler.core.features.CustomElementFeatureContainer
	 * #getUpdateFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public IUpdateFeature getUpdateFeature(IFeatureProvider fp) {
		return getFeatureContainer(fp).getUpdateFeature(fp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.bpmn2.modeler.core.features.CustomElementFeatureContainer
	 * #getDirectEditingFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public IDirectEditingFeature getDirectEditingFeature(IFeatureProvider fp) {
		return getFeatureContainer(fp).getDirectEditingFeature(fp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.bpmn2.modeler.core.features.CustomElementFeatureContainer
	 * #getLayoutFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public ILayoutFeature getLayoutFeature(IFeatureProvider fp) {
		return getFeatureContainer(fp).getLayoutFeature(fp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.bpmn2.modeler.core.features.CustomElementFeatureContainer
	 * #getRemoveFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public IRemoveFeature getRemoveFeature(IFeatureProvider fp) {
		return getFeatureContainer(fp).getRemoveFeature(fp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.bpmn2.modeler.core.features.CustomElementFeatureContainer
	 * #getDeleteFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public IDeleteFeature getDeleteFeature(IFeatureProvider fp) {
		return getFeatureContainer(fp).getDeleteFeature(fp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.bpmn2.modeler.core.features.CustomElementFeatureContainer
	 * #getCustomFeatures(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public ICustomFeature[] getCustomFeatures(IFeatureProvider fp) {
		return getFeatureContainer(fp).getCustomFeatures(fp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.bpmn2.modeler.core.features.IConnectionFeatureContainer#
	 * getReconnectionFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public IReconnectionFeature getReconnectionFeature(IFeatureProvider fp) {
		IConnectionFeatureContainer fc = (IConnectionFeatureContainer) getFeatureContainer(fp);
		return fc.getReconnectionFeature(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IConnectionFeatureContainer#getMoveConnectionDecoratorFeature(org.eclipse.graphiti.features.context.IMoveConnectionDecoratorContext)
	 */
	@Override
	public IMoveConnectionDecoratorFeature getMoveConnectionDecoratorFeature(IFeatureProvider fp) {
		// TODO Auto-generated method stub
		return null;
	}

}
