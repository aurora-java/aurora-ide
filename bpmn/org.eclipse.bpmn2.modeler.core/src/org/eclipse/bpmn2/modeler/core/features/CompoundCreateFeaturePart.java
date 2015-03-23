/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013 Red Hat, Inc.
 * All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.features;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.runtime.ModelDescriptor;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.bpmn2.modeler.core.runtime.ToolPaletteDescriptor;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.PictogramLink;
import org.eclipse.graphiti.mm.pictograms.PictogramsFactory;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

/**
 * This is a Graphiti CreateFeature child component of {@link CompoundCreateFeature}.
 *
 * @param <CONTEXT> a subclass of a Graphiti {@link IContext}.
 */
public class CompoundCreateFeaturePart<CONTEXT> {
	
	/** The Graphiti CreateFeature. */
	IFeature feature;
	
	/** The CreateFeature children. */
	List<CompoundCreateFeaturePart<CONTEXT>> children = new ArrayList<CompoundCreateFeaturePart<CONTEXT>>();
	
	/** The list of properties parsed from the Tool Palette tool definition. */
	Hashtable<String, String> properties = null;
	
	/**
	 * Instantiates a new compound create feature part.
	 *
	 * @param feature the feature
	 */
	public CompoundCreateFeaturePart(IFeature feature) {
		this.feature = feature;
	}
	
	/**
	 * Check if all children can be executed.
	 *
	 * @param context the Graphiti Context
	 * @return true, if all children can create their component parts
	 */
	public boolean canCreate(IContext context) {
		if (feature instanceof ICreateFeature && context instanceof ICreateContext) {
			if (!((ICreateFeature)feature).canCreate((ICreateContext)context))
				return false;
			if (children.size()>0) {
				/*
				 * Some types of objects have constraints on the target
				 * container e.g. a StartEvent with a CompensateEventDefinition
				 * MAY NOT be created within a Process, but MAY be created in a
				 * SubProcess. The restriction here is not imposed by the
				 * StartEvent, but by the CompensateEventDefinition which is a
				 * child of the StartEvent CompoundCreateFeaturePart. This bit
				 * of code ensures that this constraint is checked correctly.
				 */
				
				PictogramElement parentContainer = ((ICreateContext)context).getTargetContainer();
				// create a throw-away CreateContext for this child feature part
				CreateContext childContext = new CreateContext();
				// make the target container this feature part (e.g. the StartEvent)
				ContainerShape targetContainer = PictogramsFactory.eINSTANCE.createContainerShape();
				childContext.setTargetContainer(targetContainer);
				// create a throw-away BPMN2 object so we can link it to the container shape
				EClass eClass = ((AbstractBpmn2CreateFeature)feature).getBusinessObjectClass();
				EObject businessObject = Bpmn2Factory.eINSTANCE.create(eClass);
				// do the linking
				PictogramLink link = PictogramsFactory.eINSTANCE.createPictogramLink();
				link.setPictogramElement(targetContainer);
				link.getBusinessObjects().add(businessObject);
				targetContainer.setLink(link);
				
				// Set the parent business object. This is required by {@link
				// org.eclipse.bpmn2.modeler.core.utils.FeatureSupport#getAllowedEventDefinitions()}
				// when doing validation for target Events & Event Definitions.
				childContext.putProperty(GraphitiConstants.PARENT_CONTAINER,
						BusinessObjectUtil.getBusinessObjectForPictogramElement(parentContainer));
				
				// test the children feature parts
				for (CompoundCreateFeaturePart<CONTEXT> child : children) {
					if (!child.canCreate(childContext))
						return false;
				}
			}
		}
		else if (feature instanceof ICreateConnectionFeature && context instanceof ICreateConnectionContext) {
			if (!((ICreateConnectionFeature)feature).canCreate((ICreateConnectionContext)context))
				return false;
		}
		return true;
	}

	/**
	 * Creates the parent object.
	 *
	 * @param context the context
	 * @return the list
	 */
	public List<Object> create(IContext context) {
		// Create the parent element.
		// For ICreateContext this will result in a BaseElement and a ContainerShape;
		// for ICreateConnectionContext we only get a Graphiti Connection element.
		List<Object> businessObjects = new ArrayList<Object>();
		if (feature instanceof ICreateFeature && context instanceof ICreateContext) {
			if (canCreate(context)) {
				Object created[] = ((ICreateFeature)feature).create((ICreateContext)context);
				for (Object o : created)
					businessObjects.add(o);
			}
		}
		else if (feature instanceof ICreateConnectionFeature && context instanceof ICreateConnectionContext) {
			if (canCreate(context)) {
				businessObjects.add(((ICreateConnectionFeature)feature).create((ICreateConnectionContext)context));
			}
		}

		BaseElement businessObject = null;
		ContainerShape targetContainer = null;
		Connection connection = null;
		for (Object o : businessObjects) {
			if (o instanceof ContainerShape && targetContainer==null) {
				targetContainer = (ContainerShape)o;
			}
			else if (o instanceof Connection && connection==null) {
				connection = (Connection)o;
			}
			else if (o instanceof BaseElement && businessObject==null) {
				businessObject = (BaseElement)o;
			}
		}
		if (connection!=null) {
			// we need the BaseElement that is linked to this connection
			businessObject = BusinessObjectUtil.getFirstBaseElement(connection);
		}
		// initialize any model features specified in the ToolPart definition
		applyBusinessObjectProperties(businessObject);
		
		// Now process the child features
		List<PictogramElement> createdPEs = new ArrayList<PictogramElement>();
		for (int i =0; i<children.size(); ++i) {
			CompoundCreateFeaturePart<CONTEXT> node = children.get(i);
			node.create(context, targetContainer, createdPEs, businessObjects);
		}
		return businessObjects;
	}

	/**
	 * Creates the.
	 *
	 * @param context the context
	 * @param targetContainer the target container
	 * @param pictogramElements the pictogram elements
	 * @param businessObjects the business objects
	 */
	public void create(IContext context, ContainerShape targetContainer, List<PictogramElement> pictogramElements, List<Object> businessObjects) {
		IContext childContext = null;
		String value;
		int x = 0;
		int y = 0;
		int width = 0;
		int height = 0;
		if (context instanceof ICreateContext) {
			ICreateContext cc = (ICreateContext)context;
			x = cc.getX();
			y = cc.getY();
			width = cc.getWidth();
			height = cc.getHeight();
		}
		PictogramElement source = null;
		PictogramElement target = null;
		int index = pictogramElements.size();
		
		// Construct a create context for either a shape or connection,
		// depending on the type of element to be created, and apply
		// any creation properties from the ToolPart definition.
		if (feature instanceof ICreateFeature) {
			CreateContext cc = new CreateContext();
			if (targetContainer==null)
				targetContainer = ((ICreateContext)context).getTargetContainer();
			cc.setTargetContainer(targetContainer);
			cc.setTargetConnection(((ICreateContext)context).getTargetConnection());
			value = this.getProperty("x"); //$NON-NLS-1$
			if (value!=null) {
				if (targetContainer instanceof Diagram)
					x += Integer.parseInt(value);
				else
					x = Integer.parseInt(value);
			}
			cc.setX(x);
			value = this.getProperty("y"); //$NON-NLS-1$
			if (value!=null) {
				if (targetContainer instanceof Diagram)
					y += Integer.parseInt(value);
				else
					y = Integer.parseInt(value);
			}
			cc.setY(y);
			value = this.getProperty("width"); //$NON-NLS-1$
			if (value!=null) {
				width = Integer.parseInt(value);
			}
			cc.setWidth(width);
			value = this.getProperty("height"); //$NON-NLS-1$
			if (value!=null) {
				height = Integer.parseInt(value);
			}
			cc.setHeight(height);
			
			childContext = cc;
		}
		else if (feature instanceof ICreateConnectionFeature) {
			CreateConnectionContext cc = new CreateConnectionContext();
			value = this.getProperty("source"); //$NON-NLS-1$
			if (value!=null) {
				for (PictogramElement pe : pictogramElements) {
					String id = Graphiti.getPeService().getPropertyValue(pe, ToolPaletteDescriptor.TOOLPART_ID);
					if (value.equals(id)) {
						source = pe;
						break;
					}
				}
			}
			else if (index-2>=0 && index-2<pictogramElements.size())
				source = pictogramElements.get(index-2);
			
			if (source==null)
				source = ((ICreateConnectionContext)context).getSourcePictogramElement();
			
			value = this.getProperty("target"); //$NON-NLS-1$
			if (value!=null) {
				for (PictogramElement pe : pictogramElements) {
					String id = Graphiti.getPeService().getPropertyValue(pe, ToolPaletteDescriptor.TOOLPART_ID);
					if (value.equals(id)) {
						target = pe;
						break;
					}
				}
			}
			else if (index-1>=0 && index-1<pictogramElements.size())
				target = pictogramElements.get(index-1);

			if (target==null)
				target = ((ICreateConnectionContext)context).getTargetPictogramElement();
			
			Point sp = AnchorUtil.getCenterPoint((Shape)source);
			Point tp = AnchorUtil.getCenterPoint((Shape)target);
			FixPointAnchor sourceAnchor = AnchorUtil.findNearestAnchor((Shape)source, tp);
			FixPointAnchor targetAnchor = AnchorUtil.findNearestAnchor((Shape)target, sp);
			cc.setSourcePictogramElement(source);
			cc.setTargetPictogramElement(target);
			cc.setSourceAnchor(sourceAnchor);
			cc.setTargetAnchor(targetAnchor);
			
			childContext = cc;
		}
		
		List<Object> result = null;
		
		result = create(childContext);
		PictogramElement pe = null;
		Connection cn = null;
		BaseElement be = null;
		for (Object o : result) {
			if (o instanceof ContainerShape) {
				pe = (ContainerShape)o;
			}
			else if (o instanceof Connection) {
				cn = (Connection)o;
			}
			else if (o instanceof BaseElement) {
				be = (BaseElement)o;
			}
		}
		
		PictogramElement updatePE = null;
		if (pe!=null) {
			pictogramElements.add(pe);
			value = this.getProperty(ToolPaletteDescriptor.TOOLPART_ID);
			if (value!=null) {
				Graphiti.getPeService().setPropertyValue(pe, ToolPaletteDescriptor.TOOLPART_ID, value);
			}
			updatePE = pe;
		}
		else if (cn!=null) {
			be = BusinessObjectUtil.getFirstBaseElement(cn);
			value = this.getProperty(ToolPaletteDescriptor.TOOLPART_ID);
			if (value!=null) {
				Graphiti.getPeService().setPropertyValue(cn, ToolPaletteDescriptor.TOOLPART_ID, value);
			}
			updatePE = cn;
		}

		// initialize any model features specified in the ToolPart definition
		applyBusinessObjectProperties(be);
		
		// Update the newly created pictogram element if needed.
		// This should be done within the same transaction so that a single
		// "Undo" can be used to delete all pictogram elements without having
		// to cycle through each transaction created by an Update.
		if (updatePE!=null) {
			addPictogramElementToContext(context, updatePE);
			UpdateContext updateContext = new UpdateContext(updatePE);
			IUpdateFeature updateFeature = feature.getFeatureProvider().getUpdateFeature(updateContext);
			if ( updateFeature.updateNeeded(updateContext).toBoolean() )
				updateFeature.update(updateContext);
		}
		
		businessObjects.add(result);
	}
	
	private void addPictogramElementToContext(IContext context, PictogramElement pe) {
		List<PictogramElement> pes = (List<PictogramElement>) context.getProperty(GraphitiConstants.PICTOGRAM_ELEMENTS);
		if (pes!=null) {
			pes.add(pe);
		}
	}
	
	private void applyBusinessObjectProperties(BaseElement be) {
		if (be!=null && properties!=null) {
			ModelDescriptor md = TargetRuntime.getCurrentRuntime().getModelDescriptor();
			for (Entry<String, String> entry : properties.entrySet()) {
				if (entry.getKey().startsWith("$")) { //$NON-NLS-1$
					String featureName = entry.getKey().substring(1);
					EStructuralFeature feature = md.getFeature(be.eClass().getName(), featureName);
					ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(be);
					String value = entry.getValue();
					if (value.startsWith("$")) { //$NON-NLS-1$
						String name = value.substring(1);
						EClassifier eClass = md.getClassifier(name);
						EFactory factory = eClass.getEPackage().getEFactoryInstance();
						EObject object = factory.create((EClass)eClass);
						adapter.getFeatureDescriptor(feature).setValue(object);
					}
					else {
						adapter.getFeatureDescriptor(feature).setValue(value);
					}
				}
			}
		}

	}
	
	/**
	 * Checks if this CreateFeature is available.
	 *
	 * @param context the context
	 * @return true, if is available
	 */
	public boolean isAvailable(IContext context) {
		if (feature!=null && !feature.isAvailable(context))
			return false;
		for (CompoundCreateFeaturePart<CONTEXT> ft : children) {
			if (!ft.isAvailable(context))
				return false;
		}
		return true;
	}
	
	/**
	 * Adds the child.
	 *
	 * @param feature the feature
	 * @return the compound create feature part
	 */
	public CompoundCreateFeaturePart<CONTEXT> addChild(IFeature feature) {
		CompoundCreateFeaturePart<CONTEXT> node = new CompoundCreateFeaturePart<CONTEXT>(feature);
		children.add(node);
		return node;
	}

	/**
	 * Gets the business object class.
	 *
	 * @return the business object class
	 */
	public EClass getBusinessObjectClass() {
		EClass eClass = null;
		if (feature instanceof AbstractBpmn2CreateFeature) {
			eClass = ((AbstractBpmn2CreateFeature)feature).getBusinessObjectClass();
		}
		else if (feature instanceof AbstractBpmn2CreateConnectionFeature) {
			eClass = ((AbstractBpmn2CreateConnectionFeature)feature).getBusinessObjectClass();
		}
		if (eClass==null) {
			for (CompoundCreateFeaturePart<CONTEXT> child : children) {
				EClass ec = child.getBusinessObjectClass();
				if (ec!=null) {
					eClass = ec;
					break;
				}
			}
		}
		return eClass;
	}

	/**
	 * Gets the creates the name.
	 *
	 * @return the creates the name
	 */
	public String getCreateName() {
		String createName = null;
		if (feature!=null)
			createName = feature.getName();
		for (CompoundCreateFeaturePart<CONTEXT> child : children) {
			String cn = child.getCreateName();
			if (cn!=null)
				createName = cn;
		}
		return createName;
	}
	
	/**
	 * Gets the feature.
	 *
	 * @return the feature
	 */
	public IFeature getFeature() {
		return feature;
	}

	/**
	 * Sets the feature.
	 *
	 * @param feature the new feature
	 */
	public void setFeature(IFeature feature) {
		this.feature = feature;
	}

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public List<CompoundCreateFeaturePart<CONTEXT>> getChildren() {
		return children;
	}

	/**
	 * Sets the properties.
	 *
	 * @param properties the properties
	 */
	public void setProperties(Hashtable<String, String> properties) {
		getProperties().putAll(properties);
	}
	
	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	public Hashtable<String, String> getProperties() {
		if (properties==null)
			properties = new Hashtable<String, String>();
		return properties;
	}
	
	/**
	 * Gets the property.
	 *
	 * @param name the name
	 * @return the property
	 */
	public String getProperty(String name) {
		if (properties==null)
			return null;
		return properties.get(name);
	}
}
