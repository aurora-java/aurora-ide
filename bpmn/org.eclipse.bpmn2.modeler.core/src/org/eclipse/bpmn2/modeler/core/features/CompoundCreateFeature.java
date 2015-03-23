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
 * @author Bob Brodt
 ******************************************************************************/

package org.eclipse.bpmn2.modeler.core.features;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.modeler.core.runtime.CustomTaskImageProvider;
import org.eclipse.bpmn2.modeler.core.runtime.CustomTaskImageProvider.IconSize;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.bpmn2.modeler.core.runtime.ToolPaletteDescriptor;
import org.eclipse.bpmn2.modeler.core.runtime.ToolPaletteDescriptor.ToolDescriptor;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.graphiti.IExecutionInfo;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

/**
 * This is a Graphiti CreateFeature class that can be used to create multiple objects.
 * Each of the objects created is defined in a {@link CompoundCreateFeaturePart}.
 *
 * @param <CONTEXT> a subclass of a Graphiti {@link IContext}.
 */
public class CompoundCreateFeature<CONTEXT extends IContext>
		extends AbstractCreateFeature
		implements IBpmn2CreateFeature<BaseElement, CONTEXT>, ICreateConnectionFeature {
	
	/** The {@code CompoundCreateFeaturePart} children. */
	protected List<CompoundCreateFeaturePart<CONTEXT>> children = new ArrayList<CompoundCreateFeaturePart<CONTEXT>>();
	
	/** The ToolDescriptor that defined this {@code CompoundCreateFeature}. */
	protected ToolDescriptor tool;

	/**
	 * Instantiates a new compound create feature.
	 *
	 * @param fp the Feature Provider
	 * @param tool the tool
	 */
	public CompoundCreateFeature(IFeatureProvider fp, ToolDescriptor tool) {
		super(fp, tool.getName(), tool.getDescription());
		this.tool = tool;
	}
	
	/**
	 * Instantiates a new compound create feature.
	 *
	 * @param fp the Feature Provider
	 */
	public CompoundCreateFeature(IFeatureProvider fp) {
		super(fp, null, null);
	}
	
	/**
	 * Adds the child CreateFeature. This constructs a
	 * {@code CompoundCreateFeaturePart} and adds it to our list of children.
	 *
	 * @param feature the Create Feature
	 * @return the compound create feature part
	 */
	public CompoundCreateFeaturePart<CONTEXT> addChild(IFeature feature) {
		CompoundCreateFeaturePart<CONTEXT> node = new CompoundCreateFeaturePart<CONTEXT>(feature);
		children.add(node);
		return node;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractCreateFeature#canExecute(org.eclipse.graphiti.features.context.IContext)
	 */
	@Override
	public boolean canExecute(IContext context) {
		boolean ret = false;
		if (context instanceof ICreateContext)
			ret = canCreate((ICreateContext) context);
		else if (context instanceof ICreateConnectionContext)
			ret = canCreate((ICreateConnectionContext)context);
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractCreateFeature#execute(org.eclipse.graphiti.features.context.IContext)
	 */
	@Override
	public void execute(IContext context) {
		// create a list for PEs that are created during execution
		List<PictogramElement> pes = new ArrayList<PictogramElement>();
		context.putProperty(GraphitiConstants.PICTOGRAM_ELEMENTS, pes);

		if (context instanceof ICreateContext)
			create((ICreateContext) context);
		else if (context instanceof ICreateConnectionContext)
			create((ICreateConnectionContext)context);
		getDiagramEditor().selectPictogramElements(pes.toArray(new PictogramElement[pes.size()]));
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.ICreate#canCreate(org.eclipse.graphiti.features.context.ICreateContext)
	 */
	@Override
	public boolean canCreate(ICreateContext context) {
		for (CompoundCreateFeaturePart<CONTEXT> ft : children) {
			if (ft.canCreate(context)==false)
				return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.ICreateConnection#canCreate(org.eclipse.graphiti.features.context.ICreateConnectionContext)
	 */
	@Override
	public boolean canCreate(ICreateConnectionContext context) {
		for (CompoundCreateFeaturePart<CONTEXT> ft : children) {
			if (ft.canCreate(context)==false)
				return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.ICreate#create(org.eclipse.graphiti.features.context.ICreateContext)
	 */
	@Override
	public Object[] create(ICreateContext context) {
		List<Object> businessObjects = new ArrayList<Object>();
		List<PictogramElement> pictogramElements = new ArrayList<PictogramElement>();
		ContainerShape targetContainer = context.getTargetContainer();
		if (targetContainer==null)
			targetContainer = getDiagram();
		
		PictogramElement[] selection = getDiagramBehavior().getDiagramContainer().getSelectedPictogramElements();
		int index = 0;
		for (CompoundCreateFeaturePart<CONTEXT> fp : children) {
			String optional = fp.getProperty(ToolPaletteDescriptor.TOOLPART_OPTIONAL);
			if ("true".equals(optional)) { //$NON-NLS-1$
				if (index<selection.length) {
					boolean replace = true;
					PictogramElement pe = selection[index++];
					if (pe instanceof Diagram) {
						replace = false;
					}
					else if (fp.feature instanceof ICreateFeature) {
						if (!(pe instanceof ContainerShape))
							replace = false;
					}
					else if (fp.feature instanceof ICreateConnectionFeature) {
						if (!(pe instanceof Connection))
							replace = false;
					}
					
					if (replace) {
						Object bo = BusinessObjectUtil.getFirstBaseElement(pe);
						pictogramElements.add(pe);
						businessObjects.add(bo);
						String id = fp.getProperty(ToolPaletteDescriptor.TOOLPART_ID);
						if (id!=null) {
							Graphiti.getPeService().setPropertyValue(pe, ToolPaletteDescriptor.TOOLPART_ID, id);
						}
						continue;
					}
				}
			}
			fp.create(context, targetContainer, pictogramElements, businessObjects);
		}
		return businessObjects.toArray();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.ICreateConnection#create(org.eclipse.graphiti.features.context.ICreateConnectionContext)
	 */
	@Override
	public Connection create(ICreateConnectionContext context) {
		List<Object> businessObjects = new ArrayList<Object>();
		List<PictogramElement> pictogramElements = new ArrayList<PictogramElement>();
		ContainerShape targetContainer = getDiagram();
		for (CompoundCreateFeaturePart<CONTEXT> ft : children) {
			ft.create(context, targetContainer, pictogramElements, businessObjects);
		}
		if (businessObjects.size()>0) {
			Object o = businessObjects.get(0);
			if (o instanceof Connection)
				return (Connection)o;
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractCreateFeature#getCreateImageId()
	 */
	@Override
	public String getCreateImageId() {
		String icon = tool.getIcon();
		if (icon!=null) {
			TargetRuntime rt = tool.getParent().getParent().getRuntime();
			return CustomTaskImageProvider.getImageId(rt, icon, IconSize.SMALL);
		}
		// use the create image from the first child toolpart
		IFeature feature = getChildren().get(0).getFeature();
		if (feature instanceof ICreateFeature)
			return ((ICreateFeature)feature).getCreateImageId();
		if (feature instanceof ICreateConnectionFeature)
			return ((ICreateConnectionFeature)feature).getCreateImageId();
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractCreateFeature#getCreateLargeImageId()
	 */
	@Override
	public String getCreateLargeImageId() {
		String icon = tool.getIcon();
		if (icon!=null) {
			TargetRuntime rt = tool.getParent().getParent().getRuntime();
			return CustomTaskImageProvider.getImageId(rt, icon, IconSize.LARGE);
		}
		IFeature feature = getChildren().get(0).getFeature();
		if (feature instanceof ICreateFeature)
			return ((ICreateFeature)feature).getCreateLargeImageId();
		if (feature instanceof ICreateConnectionFeature)
			return ((ICreateConnectionFeature)feature).getCreateLargeImageId();
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractFeature#isAvailable(org.eclipse.graphiti.features.context.IContext)
	 */
	@Override
	public boolean isAvailable(IContext context) {
		for (CompoundCreateFeaturePart<CONTEXT> ft : children) {
			if (ft.isAvailable(context)==false)
				return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IBpmn2CreateFeature#createBusinessObject(org.eclipse.graphiti.features.context.IContext)
	 */
	@Override
	public BaseElement createBusinessObject(CONTEXT context) {
		for (CompoundCreateFeaturePart<CONTEXT> ft : children) {
			if (ft.getFeature() instanceof IBpmn2CreateFeature) {
				IBpmn2CreateFeature f = (IBpmn2CreateFeature)ft.getFeature();
				BaseElement be = (BaseElement)f.createBusinessObject(context);
				if (be!=null)
					return be;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IBpmn2CreateFeature#getBusinessObject(org.eclipse.graphiti.features.context.IContext)
	 */
	@Override
	public BaseElement getBusinessObject(CONTEXT context) {
		for (CompoundCreateFeaturePart<CONTEXT> ft : children) {
			if (ft.getFeature() instanceof IBpmn2CreateFeature) {
				IBpmn2CreateFeature f = (IBpmn2CreateFeature)ft.getFeature();
				BaseElement be = (BaseElement)f.getBusinessObject(context);
				if (be!=null)
					return be;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IBpmn2CreateFeature#putBusinessObject(org.eclipse.graphiti.features.context.IContext, org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public void putBusinessObject(CONTEXT context, BaseElement businessObject) {
		for (CompoundCreateFeaturePart<CONTEXT> ft : children) {
			if (ft.getFeature() instanceof IBpmn2CreateFeature) {
				IBpmn2CreateFeature f = (IBpmn2CreateFeature)ft.getFeature();
				f.putBusinessObject(context, businessObject);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IBpmn2CreateFeature#getBusinessObjectClass()
	 */
	@Override
	public EClass getBusinessObjectClass() {
		if (children.size()==1) {
			return children.get(0).getBusinessObjectClass();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IBpmn2CreateFeature#postExecute(org.eclipse.graphiti.IExecutionInfo)
	 */
	@Override
	public void postExecute(IExecutionInfo executionInfo) {
		for (CompoundCreateFeaturePart<CONTEXT> ft : children) {
			if (ft.getFeature() instanceof IBpmn2CreateFeature) {
				IBpmn2CreateFeature f = (IBpmn2CreateFeature)ft.getFeature();
				f.postExecute(executionInfo);
			}
		}
	}

	/**
	 * Gets the list of {@code CompoundCreateFeaturePart} children.
	 *
	 * @return the children
	 */
	public List<CompoundCreateFeaturePart<CONTEXT>> getChildren() {
		return children;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.ICreateConnection#canStartConnection(org.eclipse.graphiti.features.context.ICreateConnectionContext)
	 */
	@Override
	public boolean canStartConnection(ICreateConnectionContext context) {
		for (CompoundCreateFeaturePart<CONTEXT> ft : children) {
			if (ft.getFeature() instanceof ICreateConnectionFeature) {
				ICreateConnectionFeature f = (ICreateConnectionFeature)ft.getFeature();
				if (!f.canStartConnection(context))
					return false;
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.ICreateConnection#startConnecting()
	 */
	public void startConnecting() {
		for (CompoundCreateFeaturePart<CONTEXT> ft : children) {
			if (ft.getFeature() instanceof ICreateConnectionFeature) {
				ICreateConnectionFeature f = (ICreateConnectionFeature)ft.getFeature();
				f.startConnecting();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.ICreateConnection#endConnecting()
	 */
	public void endConnecting() {
		for (CompoundCreateFeaturePart<CONTEXT> ft : children) {
			if (ft.getFeature() instanceof ICreateConnectionFeature) {
				ICreateConnectionFeature f = (ICreateConnectionFeature)ft.getFeature();
				f.endConnecting();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.ICreateConnection#attachedToSource(org.eclipse.graphiti.features.context.ICreateConnectionContext)
	 */
	public void attachedToSource(ICreateConnectionContext context) {
		for (CompoundCreateFeaturePart<CONTEXT> ft : children) {
			if (ft.getFeature() instanceof ICreateConnectionFeature) {
				ICreateConnectionFeature f = (ICreateConnectionFeature)ft.getFeature();
				f.attachedToSource(context);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.ICreateConnection#canceledAttaching(org.eclipse.graphiti.features.context.ICreateConnectionContext)
	 */
	public void canceledAttaching(ICreateConnectionContext context) {
		for (CompoundCreateFeaturePart<CONTEXT> ft : children) {
			if (ft.getFeature() instanceof ICreateConnectionFeature) {
				ICreateConnectionFeature f = (ICreateConnectionFeature)ft.getFeature();
				f.canceledAttaching(context);
			}
		}
	}
	
	public EClass getFeatureClass() {
		return null;
	}
}
