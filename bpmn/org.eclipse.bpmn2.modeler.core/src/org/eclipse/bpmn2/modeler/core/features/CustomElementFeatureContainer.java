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
import org.eclipse.bpmn2.modeler.core.preferences.ModelEnablements;
import org.eclipse.bpmn2.modeler.core.runtime.CustomTaskDescriptor;
import org.eclipse.bpmn2.modeler.core.runtime.CustomTaskImageProvider;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.ImportUtil;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.IExecutionInfo;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.ocl.util.Adaptable;

/**
 * The base class for custom shape and connection Feature Containers.
 */
public class CustomElementFeatureContainer implements ICustomElementFeatureContainer {
	
	/** The custom element id. */
	protected String id;
	
	/** The custom task descriptor contributed by the extension plug-in. */
	protected CustomTaskDescriptor customTaskDescriptor;
	
	/** The feature container delegate. */
	protected IFeatureContainer featureContainerDelegate = null;
	
	/** The Feature Provider. */
	protected IBpmn2FeatureProvider fp;
	
	/**
	 * Instantiates a new custom element feature container.
	 */
	public CustomElementFeatureContainer() {
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.ICustomElementFeatureContainer#getDescription()
	 */
	public String getDescription() {
		if (customTaskDescriptor!=null)
			return customTaskDescriptor.getName();
		return Messages.CustomElementFeatureContainer_Description;
	}

	/**
	 * Creates the feature container.
	 *
	 * @param fp the Feature Provider
	 * @return the i feature container
	 */
	protected IFeatureContainer createFeatureContainer(IFeatureProvider fp) {
		EClass eClass = (EClass) ModelDecorator.findEClassifier(
				customTaskDescriptor.getRuntime().getModelDescriptor().getEPackage(), customTaskDescriptor.getType());
		return ((IBpmn2FeatureProvider)fp).getFeatureContainer(eClass.getInstanceClass());
	}
	
	/**
	 * Gets the feature container.
	 *
	 * @param fp the Feature Provider
	 * @return the feature container
	 */
	protected IFeatureContainer getFeatureContainer(IFeatureProvider fp) {
		if (featureContainerDelegate==null) {
			featureContainerDelegate = createFeatureContainer(fp);
		}
		return featureContainerDelegate;
	}
	
	/* (non-Javadoc)
	 * Determine if the context applies to this modelObject and return the Task object. Return null otherwise.
	 * @param context - the Graphiti context.
	 * 
	 * @see org.eclipse.bpmn2.modeler.core.features.BaseElementFeatureContainer#getApplyObject(org.eclipse.graphiti.features.context.IContext)
	 */
	@Override
	public Object getApplyObject(IContext context) {
		Object id = getId(context);
		if (id==null || !this.id.equals(id)) {
			return null;
		}
		
		if (context instanceof IPictogramElementContext) {
			PictogramElement pe = ((IPictogramElementContext)context).getPictogramElement();
			return BusinessObjectUtil.getBusinessObjectForPictogramElement(pe);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IFeatureContainer#canApplyTo(java.lang.Object)
	 */
	@Override
	public boolean canApplyTo(Object o) {
		boolean b1 =  o instanceof BaseElement;
		boolean b2 = o.getClass().isAssignableFrom(BaseElement.class);
		return b1 || b2;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IFeatureContainer#isAvailable(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public boolean isAvailable(IFeatureProvider fp) {
		DiagramEditor editor = (DiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();
		if (editor != null) {
			ModelEnablements me = (ModelEnablements)editor.getAdapter(ModelEnablements.class);
			if (me!=null) {
				return me.isEnabled(customTaskDescriptor.getType());
			}
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.activity.task.ICustomTaskFeatureContainer#setId(java.lang.String)
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Set this modelObject's ID in the given Graphiti context.
	 * 
	 * @param context - if this is a IPictogramElementContext, set the property
	 *            in the contained PictogramElement's list of properties;
	 *            otherwise set the Context's property
	 * @param id - ID of this Custom Task
	 */
	public static void setId(IContext context, String id) {
		
		if (context instanceof IPictogramElementContext) {
			PictogramElement pe = ((IPictogramElementContext)context).getPictogramElement();
			Graphiti.getPeService().setPropertyValue(pe,GraphitiConstants.CUSTOM_ELEMENT_ID,id); 
		}
		else {
			context.putProperty(GraphitiConstants.CUSTOM_ELEMENT_ID, id);
		}
	}
	
	/**
	 * Returns the modelObject ID string from the given Graphiti context.
	 *
	 * @param context the context
	 * @return - ID string for this modelObject.
	 */
	public static String getId(IContext context) {
		Object id = null;

		// IAddContext can also mean that a file is dragged, therefore we have
		// to check if we are really dragging a customTask
		if (context instanceof IAddContext) {
			TargetRuntime rt = TargetRuntime.getCurrentRuntime();
			Object newObject = ((IAddContext)context).getNewObject();
			if (newObject instanceof EObject ) {
				for (CustomTaskDescriptor ctd : rt.getCustomTaskDescriptors()) {
					// FIXME: {@see ICustomElementFeatureContainer#getId(EObject)}
					id = ctd.getFeatureContainer().getId((EObject) newObject);
					if (ctd.getId().equals(id)) {
						context.putProperty(GraphitiConstants.CUSTOM_ELEMENT_ID, id);
						return (String)id;
					}
				}
			}
		}
		
		if (context instanceof IPictogramElementContext) {
			PictogramElement pe = ((IPictogramElementContext)context).getPictogramElement();
			id = Graphiti.getPeService().getPropertyValue(pe,GraphitiConstants.CUSTOM_ELEMENT_ID); 
		}
		else if (context instanceof ICustomContext) {
			for (PictogramElement pe : ((ICustomContext)context).getPictogramElements()) {
				id = Graphiti.getPeService().getPropertyValue(pe,GraphitiConstants.CUSTOM_ELEMENT_ID);
				if (id!=null)
					break;
			}
		}
		else if (context instanceof IReconnectionContext) {
			PictogramElement pe = ((IReconnectionContext) context).getConnection();
			id = Graphiti.getPeService().getPropertyValue(pe,GraphitiConstants.CUSTOM_ELEMENT_ID); 
		}
		else {
			id = context.getProperty(GraphitiConstants.CUSTOM_ELEMENT_ID);
		}
		return (String)id;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.ICustomElementFeatureContainer#getId(org.eclipse.emf.ecore.EObject)
	 */
	public String getId(EObject object) {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.ui.features.activity.task.ICustomTaskFeatureContainer#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.ui.features.activity.task.ICustomTaskFeatureContainer#setCustomTaskDescriptor(org.eclipse.bpmn2.modeler.core.preferences.TargetRuntime.CustomTaskDescriptor)
	 */
	@Override
	public void setCustomTaskDescriptor(CustomTaskDescriptor customTaskDescriptor) {
		this.customTaskDescriptor = customTaskDescriptor;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IFeatureContainer#getAddFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public IAddFeature getAddFeature(IFeatureProvider fp) {
		return new AddCustomElementFeature(fp);
	}

	/**
	 * The Class AddCustomElementFeature.
	 */
	public class AddCustomElementFeature extends AbstractBpmn2AddFeature<BaseElement> {

		/** The add feature delegate. */
		protected AbstractBpmn2AddFeature<BaseElement> addFeatureDelegate;
		
		/**
		 * Instantiates a new {@code AddFeature} for custom elements.
		 *
		 * @param fp the Feature Provider
		 */
		public AddCustomElementFeature(IFeatureProvider fp) {
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
	 * @see org.eclipse.bpmn2.modeler.core.features.IFeatureContainer#getUpdateFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public IUpdateFeature getUpdateFeature(IFeatureProvider fp) {
		return getFeatureContainer(fp).getUpdateFeature(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IFeatureContainer#getDirectEditingFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public IDirectEditingFeature getDirectEditingFeature(IFeatureProvider fp) {
		return getFeatureContainer(fp).getDirectEditingFeature(fp);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IFeatureContainer#getLayoutFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public ILayoutFeature getLayoutFeature(IFeatureProvider fp) {
		return getFeatureContainer(fp).getLayoutFeature(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IFeatureContainer#getRemoveFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public IRemoveFeature getRemoveFeature(IFeatureProvider fp) {
		return getFeatureContainer(fp).getRemoveFeature(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IFeatureContainer#getDeleteFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public IDeleteFeature getDeleteFeature(IFeatureProvider fp) {
		return getFeatureContainer(fp).getDeleteFeature(fp);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.ICustomElementFeatureContainer#getCustomFeatures(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public ICustomFeature[] getCustomFeatures(IFeatureProvider fp) {
		return getFeatureContainer(fp).getCustomFeatures(fp);
	}

}
