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
import org.eclipse.bpmn2.modeler.core.model.ModelDecorator;
import org.eclipse.bpmn2.modeler.core.runtime.CustomTaskImageProvider;
import org.eclipse.bpmn2.modeler.core.runtime.CustomTaskImageProvider.IconSize;
import org.eclipse.bpmn2.modeler.core.features.Messages;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.graphiti.IExecutionInfo;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IAreaContext;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.osgi.util.NLS;

/**
 * The Feature Container for Custom Shapes.
 * <p>
 * This class must be extended by contributing plug-ins that want to extend any
 * of the BPMN2 shape elements, e.g. {@link org.eclipse.bpmn2.Task}, {@link org.eclipse.bpmn2.Gateway},
 * {@link org.eclipse.bpmn2.Event}, etc.
 * <p>
 * See the {@code <customTask>} element of the
 * {@code org.eclipse.bpmn2.modeler.runtime} extension point.
 */
public class CustomShapeFeatureContainer extends CustomElementFeatureContainer implements IShapeFeatureContainer {
	
	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.CustomElementFeatureContainer#getDescription()
	 */
	public String getDescription() {
		if (customTaskDescriptor!=null)
			return customTaskDescriptor.getDescription();
		return Messages.CustomShapeFeatureContainer_Description;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.CustomElementFeatureContainer#createFeatureContainer(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	protected IFeatureContainer createFeatureContainer(IFeatureProvider fp) {
		EClass eClass = (EClass) ModelDecorator.findEClassifier(
				customTaskDescriptor.getRuntime().getModelDescriptor().getEPackage(), customTaskDescriptor.getType());
		return ((IBpmn2FeatureProvider)fp).getFeatureContainer(eClass.getInstanceClass());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.CustomElementFeatureContainer#getFeatureContainer(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	protected IFeatureContainer getFeatureContainer(IFeatureProvider fp) {
		if (featureContainerDelegate==null) {
			featureContainerDelegate = createFeatureContainer(fp);
		}
		return featureContainerDelegate;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IShapeFeatureContainer#getCreateFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public ICreateFeature getCreateFeature(IFeatureProvider fp) {
		return new CreateCustomShapeFeature(fp);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.CustomElementFeatureContainer#getAddFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public IAddFeature getAddFeature(IFeatureProvider fp) {
		return new AddCustomShapeFeature(fp);
	}
	
	/**
	 * Base class for Custom Shape Feature construction.
	 * <p>
	 * Custom Connections contributed to the editor <b>MUST</b> subclass this in
	 * their FeatureContainer implementation.
	 * <p>
	 * This class is intended for creation of BPMN2 objects that have custom model
	 * extensions. This is for any object considered to be a "shape", e.g. Activities,
	 * Data Objects, Gateways, Events, etc. 
	 * <p> 
	 * The creation process copies the modelObject ID string into the Graphiti create
	 * context during the construction phase, then migrates that ID into the created
	 * PictogramElement. This is necessary because the ID must be associated with the
	 * PE to allow our BPMNFeatureProvider to correctly identify the Custom Task.
	 */
	public class CreateCustomShapeFeature extends AbstractBpmn2CreateFeature<BaseElement> {

		/** The create feature delegate. */
		protected AbstractBpmn2CreateFeature<BaseElement> createFeatureDelegate;

		/** Our own copies of create name and description because the ones in super are private **/
		protected String name;
		protected String description;

		/**
		 * Instantiates a new {@code CreateFeature} for custom shapes. If the
		 * name and/or description are null or empty strings then they are
		 * fetched from the Create Feature delegate when required.
		 *
		 * @param fp the Feature Provider
		 * @param name the name of the element being created
		 * @param description the description of the Create Feature
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public CreateCustomShapeFeature(IFeatureProvider fp, String name, String description) {
			super(fp);
			IShapeFeatureContainer fc = (IShapeFeatureContainer)getFeatureContainer(fp);
			createFeatureDelegate = (AbstractBpmn2CreateFeature)fc.getCreateFeature(fp);
			Assert.isNotNull(createFeatureDelegate);
			this.name = name;
			this.description = description;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2CreateFeature#getCreateName()
		 */
		@Override
		public String getCreateName() {
			if (name!=null && !name.isEmpty())
				return name;
			return createFeatureDelegate.getCreateName();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2CreateFeature#getCreateDescription()
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
		public CreateCustomShapeFeature(IFeatureProvider fp) {
			this(fp, customTaskDescriptor.getName(),
					NLS.bind(Messages.CustomElementFeatureContainer_Create, customTaskDescriptor.getName()));
		}

		/* (non-Javadoc)
		 * @see org.eclipse.graphiti.features.impl.AbstractFeature#addGraphicalRepresentation(org.eclipse.graphiti.features.context.IAreaContext, java.lang.Object)
		 */
		@Override
		protected PictogramElement addGraphicalRepresentation(IAreaContext context, Object newObject) {

			// create a new AddContext and copy our ID into it.
			IAddContext addContext = new AddContext(context, newObject);
			setId(addContext, getId());
			
			// create the PE and copy our ID into its properties.
			PictogramElement pe = getFeatureProvider().addIfPossible(addContext);
			Graphiti.getPeService().setPropertyValue(pe,GraphitiConstants.CUSTOM_ELEMENT_ID,id);
			
			return pe;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.graphiti.func.ICreate#canCreate(org.eclipse.graphiti.features.context.ICreateContext)
		 */
		@Override
		public boolean canCreate(ICreateContext context) {
			// copy our ID into the CreateContext - this is where it all starts!
			setId(context, id);
			return createFeatureDelegate.canCreate(context);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2CreateFeature#createBusinessObject(org.eclipse.graphiti.features.context.ICreateContext)
		 */
		@Override
		public BaseElement createBusinessObject(ICreateContext context) {
			BaseElement businessObject = createFeatureDelegate.createBusinessObject(context);
			customTaskDescriptor.populateObject(businessObject, true);
			return businessObject;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.AbstractCreateFlowElementFeature#getFlowElementClass()
		 */
		@Override
		public EClass getBusinessObjectClass() {
			return createFeatureDelegate.getBusinessObjectClass();
		}

		/**
		 * This delegates to the CreateFeature implementation defined by the
		 * extension plugin. Note that the create() method of this delegate MUST
		 * return an array of two objects: the first object is the BPMN2
		 * element, the second object is the ContainerShape that is the
		 * graphical representation of that element.
		 * 
		 * @see org.eclipse.graphiti.func.ICreate#create(org.eclipse.graphiti.features.context.ICreateContext)
		 */
		@Override
		public Object[] create(ICreateContext context) {
			// Our Custom Task ID should have already been set in canCreate()
			// if not, we have a problem; in other words, canCreate() MUST have
			// been called by the framework before create()
			Assert.isNotNull(getId(context));
			return createFeatureDelegate.create(context);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.graphiti.features.impl.AbstractCreateFeature#getCreateImageId()
		 */
		@Override
		public String getCreateImageId() {
			String icon = customTaskDescriptor.getIcon();
			if (icon!=null) {
				String id = customTaskDescriptor.getImageId(icon, IconSize.SMALL);
				if (id!=null)
					return id;
			}
			return createFeatureDelegate.getCreateImageId();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.graphiti.features.impl.AbstractCreateFeature#getCreateLargeImageId()
		 */
		@Override
		public String getCreateLargeImageId() {
			String icon = customTaskDescriptor.getIcon();
			if (icon!=null) {
				String id = customTaskDescriptor.getImageId(icon, IconSize.LARGE);
				if (id!=null)
					return id;
			}
			return createFeatureDelegate.getCreateLargeImageId();
		}
	}

	/**
	 * The {@code AddFeature} base class for creating custom shapes.
	 * <p>
	 * Custom shapes contributed to the editor <b>MUST</b> subclass this in
	 * their FeatureContainer implementation.
	 */
	public class AddCustomShapeFeature extends AbstractBpmn2AddFeature<BaseElement> {

		/** The add feature delegate. */
		protected AbstractBpmn2AddFeature<BaseElement> addFeatureDelegate;
		
		/**
		 * Instantiates a new {@code AddConnectionFeature} for custom connections.
		 *
		 * @param fp the Feature Provider
		 */
		public AddCustomShapeFeature(IFeatureProvider fp) {
			super(fp);
			addFeatureDelegate = (AbstractBpmn2AddFeature)getFeatureContainer(fp).getAddFeature(fp);
			Assert.isNotNull(addFeatureDelegate);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.graphiti.func.IAdd#add(org.eclipse.graphiti.features.context.IAddContext)
		 */
		@Override
		public PictogramElement add(IAddContext context) {
			PictogramElement pe = addFeatureDelegate.add(context);
			// make sure everyone knows that this PE is a custom task
			if (pe!=null)
				peService.setPropertyValue(pe,GraphitiConstants.CUSTOM_ELEMENT_ID,getId());
			
			// add an icon to the top-left corner if applicable, and if the implementing
			// addFeatureDelegate hasn't already done so.
			String icon = customTaskDescriptor.getIcon();
			if (icon!=null && pe instanceof ContainerShape) {
				boolean addImage = true;
				ContainerShape containerShape = (ContainerShape)pe;
				GraphicsAlgorithm ga = (GraphicsAlgorithm)AbstractBpmn2AddFeature.getGraphicsAlgorithm(containerShape);
				for (PictogramElement child : containerShape.getChildren()) {
					if (child.getGraphicsAlgorithm() instanceof Image) {
						addImage = false;
						break;
					}
				}
				if (ga!=null) {
					for (GraphicsAlgorithm g : ga.getGraphicsAlgorithmChildren()) {
						if (g instanceof Image) {
							addImage = false;
							break;
						}
					}
				}
				else
					addImage = false;
				
				if (addImage) {
					Image img = CustomTaskImageProvider.createImage(customTaskDescriptor, ga, icon, 24, 24);
					Graphiti.getGaService().setLocationAndSize(img, 2, 2, 24, 24);
				}
			}
			return pe;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.IBpmn2AddFeature#getBusinessObject(org.eclipse.graphiti.features.context.IAddContext)
		 */
		@Override
		public BaseElement getBusinessObject(IAddContext context) {
			// TODO Auto-generated method stub
			return addFeatureDelegate.getBusinessObject(context);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.IBpmn2AddFeature#putBusinessObject(org.eclipse.graphiti.features.context.IAddContext, org.eclipse.emf.ecore.EObject)
		 */
		@Override
		public void putBusinessObject(IAddContext context, BaseElement businessObject) {
			addFeatureDelegate.putBusinessObject(context, businessObject);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.IBpmn2AddFeature#postExecute(org.eclipse.graphiti.IExecutionInfo)
		 */
		@Override
		public void postExecute(IExecutionInfo executionInfo) {
			addFeatureDelegate.postExecute(executionInfo);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.graphiti.func.IAdd#canAdd(org.eclipse.graphiti.features.context.IAddContext)
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
	
	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.CustomElementFeatureContainer#getUpdateFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public IUpdateFeature getUpdateFeature(IFeatureProvider fp) {
		return getFeatureContainer(fp).getUpdateFeature(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.CustomElementFeatureContainer#getDirectEditingFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public IDirectEditingFeature getDirectEditingFeature(IFeatureProvider fp) {
		return getFeatureContainer(fp).getDirectEditingFeature(fp);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.CustomElementFeatureContainer#getLayoutFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public ILayoutFeature getLayoutFeature(IFeatureProvider fp) {
		return getFeatureContainer(fp).getLayoutFeature(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.CustomElementFeatureContainer#getRemoveFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public IRemoveFeature getRemoveFeature(IFeatureProvider fp) {
		return getFeatureContainer(fp).getRemoveFeature(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IShapeFeatureContainer#getMoveFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public IMoveShapeFeature getMoveFeature(IFeatureProvider fp) {
		IShapeFeatureContainer fc = (IShapeFeatureContainer)getFeatureContainer(fp);
		return fc.getMoveFeature(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IShapeFeatureContainer#getResizeFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public IResizeShapeFeature getResizeFeature(IFeatureProvider fp) {
		IShapeFeatureContainer fc = (IShapeFeatureContainer)getFeatureContainer(fp);
		return fc.getResizeFeature(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.CustomElementFeatureContainer#getDeleteFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public IDeleteFeature getDeleteFeature(IFeatureProvider fp) {
		return getFeatureContainer(fp).getDeleteFeature(fp);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.CustomElementFeatureContainer#getCustomFeatures(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public ICustomFeature[] getCustomFeatures(IFeatureProvider fp) {
		return getFeatureContainer(fp).getCustomFeatures(fp);
	}

}
