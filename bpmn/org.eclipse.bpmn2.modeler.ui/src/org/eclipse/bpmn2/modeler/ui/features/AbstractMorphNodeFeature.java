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

package org.eclipse.bpmn2.modeler.ui.features;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.features.IBpmn2CreateFeature;
import org.eclipse.bpmn2.modeler.core.features.CustomShapeFeatureContainer.CreateCustomShapeFeature;
import org.eclipse.bpmn2.modeler.core.preferences.ModelEnablements;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil.AnchorLocation;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.GraphicsUtil;
import org.eclipse.bpmn2.modeler.ui.diagram.Bpmn2ToolBehaviorProvider;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReconnectionFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.context.impl.ReconnectionContext;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.IToolEntry;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.ILayoutService;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.internal.util.ui.PopupMenu;
import org.eclipse.graphiti.ui.internal.util.ui.PopupMenu.CascadingMenu;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * @author Bob Brodt
 *
 */
public abstract class AbstractMorphNodeFeature<T extends FlowNode> extends AbstractCustomFeature {

	protected boolean changesDone = false;;
	
	// label provider for the popup menu that displays allowable Activity subclasses
	private static ILabelProvider labelProvider = new ILabelProvider() {

		public void removeListener(ILabelProviderListener listener) {
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void dispose() {

		}

		public void addListener(ILabelProviderListener listener) {

		}

		public String getText(Object element) {
			if (element instanceof ObjectCreationToolEntry) {
				ObjectCreationToolEntry te = (ObjectCreationToolEntry)element;
				return te.getLabel();
			}
			else if (element instanceof IPaletteCompartmentEntry) {
				IPaletteCompartmentEntry ce = (IPaletteCompartmentEntry)element;
				return ce.getLabel();
			}
			return "?"; //$NON-NLS-1$
		}

		public Image getImage(Object element) {
			return null;
		}

	};

	/**
	 * @param fp
	 */
	public AbstractMorphNodeFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		return getTools(context).size()>0;
	}

	@Override
	public boolean isAvailable(IContext context) {
		if (context instanceof ICustomContext && getTools((ICustomContext)context).size()>0) {
			PictogramElement pe[] = ((ICustomContext)context).getPictogramElements();
			if (pe.length==1) {
				EObject o = BusinessObjectUtil.getBusinessObjectForPictogramElement(pe[0]);
				if (o!=null) {
					return o.eClass() == getBusinessObjectClass();
				}
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public void execute(ICustomContext context) {
		ContainerShape oldShape = getOldShape(context);
		if (oldShape!=null) {
			// Let user select the new type of object to create. The selection will
			// be from a list of subtypes of <code>T</code> as defined by the various
			// AbstractMorphNodeFeature specializations; for example the class
			// AppendActivityFeature will construct a popup list of all Activity subclasses
			// e.g. Task, ScriptTask, SubProcess, etc. 
			ICreateFeature createFeature = selectNewShape(context);
			if (createFeature!=null) {
				// if user made a selection, then create the new shape
				ContainerShape newShape = createNewShape(oldShape, createFeature);
				UpdateContext updateContext = new UpdateContext(newShape);
				IUpdateFeature updateFeature = getFeatureProvider().getUpdateFeature(updateContext);
				if ( updateFeature.updateNeeded(updateContext).toBoolean() )
					updateFeature.update(updateContext);
				
				changesDone = true;
			}
		}
	}
	
	protected ContainerShape getOldShape(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			PictogramElement pe = pes[0];
			Object bo = getBusinessObjectForPictogramElement(pe);
			if (pe instanceof ContainerShape && bo instanceof FlowNode) {
				return (ContainerShape)pe;
			}
		}
		return null;
	}
	
	protected ICreateFeature selectNewShape(ICustomContext context) {
		DiagramEditor editor = (DiagramEditor)getDiagramEditor();
		Bpmn2ToolBehaviorProvider toolProvider = getToolProvider();
		List<IToolEntry> tools = getTools(context);
		ICreateFeature feature = null;
		
		// show popup menu
		boolean doit = tools.size()>0;
		if (doit) {
			// figure out if we need a cascading menu: If there are more than one categories
			// involved for the tools that have been selected, then create a cascading popup menu
			LinkedHashMap<IPaletteCompartmentEntry, List<IToolEntry>> categories = new LinkedHashMap<IPaletteCompartmentEntry, List<IToolEntry>>();
			List<IToolEntry> categorizedTools;
			List<IToolEntry> uncategorizedTools = new ArrayList<IToolEntry>();

			for (IToolEntry te : tools) {
				IPaletteCompartmentEntry ce = toolProvider.getCategory(te);
				if (ce!=null) {
					if (categories.containsKey(ce)) {
						categorizedTools = categories.get(ce);
					}
					else {
						categorizedTools = new ArrayList<IToolEntry>();
						categories.put(ce, categorizedTools);
					}
					categorizedTools.add(te);
				}
				else {
					uncategorizedTools.add(te);
				}
			}
			
			IToolEntry tool = tools.get(0);
			feature = ((ObjectCreationToolEntry)tool).getCreateFeature();
			if (tools.size()>1) {
				PopupMenu popupMenu = null;
				if (categories.size()>1) {
					List<CascadingMenu> cascadingMenus = new ArrayList<CascadingMenu>();
					for (Entry<IPaletteCompartmentEntry, List<IToolEntry>> entry : categories.entrySet()) {
						PopupMenu subMenu = new PopupMenu(entry.getValue(), labelProvider);
						CascadingMenu cascadingMenu = new CascadingMenu(entry.getKey(), subMenu);
						cascadingMenus.add(cascadingMenu);
					}
					popupMenu = new PopupMenu(cascadingMenus, labelProvider);
				}
				else {
					popupMenu = new PopupMenu(tools, labelProvider);
				}
				
				doit = popupMenu.show(Display.getCurrent().getActiveShell());
				if (doit) {
					Object result = popupMenu.getResult();
					if (result instanceof List) {
						for (Object o : (List)result) {
							if (o instanceof IToolEntry) {
								tool = (IToolEntry)o;
								break;
							}
						}
					}
					else if (result instanceof IToolEntry)
						tool = (IToolEntry)result;
					
					if (tool==null)
						feature = null;
					else
						feature = ((ObjectCreationToolEntry)tool).getCreateFeature();
				}
				else
					feature = null;
			}
		}

		return feature;
	}

	protected List<EClass> getAvailableTypes(ICustomContext context) {
		DiagramEditor editor = (DiagramEditor)getDiagramEditor();
		ModelEnablements enablements =
				(ModelEnablements)editor.getAdapter(ModelEnablements.class);
		EClass newType = getBusinessObjectClass();
		List<EClass> subtypes = new ArrayList<EClass>();
		ContainerShape oldShape = getOldShape(context);
		if (oldShape!=null) {
			BaseElement oldObject = BusinessObjectUtil.getFirstElementOfType(oldShape, BaseElement.class);
			EClass oldType = oldObject.eClass();
	
			// build a list of possible subclasses for the popup menu
			for (EClassifier ec : Bpmn2Package.eINSTANCE.getEClassifiers() ) {
				if (ec instanceof EClass) {
					if ( ((EClass) ec).isAbstract()) {
						continue;
					}
					EList<EClass>superTypes = ((EClass)ec).getEAllSuperTypes(); 
					if (superTypes.contains(newType) &&
							enablements.isEnabled((EClass)ec)) {
						if (ec!=Bpmn2Package.eINSTANCE.getBoundaryEvent() &&
								ec!=Bpmn2Package.eINSTANCE.getStartEvent() && ec!=oldType) {
							subtypes.add((EClass)ec);
						}
					}
				}
			}
		}
		return subtypes;
	}
	
	protected ContainerShape createNewShape(ContainerShape oldShape, ICreateFeature createFeature) {
		ILayoutService layoutService = Graphiti.getLayoutService();

		ILocation loc = layoutService.getLocationRelativeToDiagram(oldShape);
		IDimension size = GraphicsUtil.calculateSize(oldShape);
		int x = loc.getX();
		int y = loc.getY();
		ContainerShape oldContainer = oldShape.getContainer();
		if (oldContainer!=null && !(oldContainer instanceof Diagram)) {
			loc = layoutService.getLocationRelativeToDiagram(oldContainer);
			x -= loc.getX();
			y -= loc.getY();
		}
		int w = size.getWidth();
		int h = size.getHeight();
		
		CreateContext createContext = new CreateContext();
		createContext.setTargetContainer(oldShape.getContainer());
		createContext.setLocation(x, y);
		createContext.setSize(w, h);
		createContext.putProperty(GraphitiConstants.IMPORT_PROPERTY, Boolean.TRUE);

		Object[] created = createFeature.create(createContext);
		FlowElement newObject = (FlowElement) created[0];
		ContainerShape newShape = (ContainerShape) created[1];
		
		BaseElement oldObject = BusinessObjectUtil.getFirstElementOfType(oldShape, BaseElement.class);
		if (oldObject instanceof Lane) {
			((Lane)oldObject).getFlowNodeRefs().add((FlowNode)newObject);
		}
		copyBusinessObject((T)oldObject, (T)newObject);
		
		// reconnect the new shape
		List<Anchor> oldAnchors = new ArrayList<Anchor>();
		oldAnchors.addAll(oldShape.getAnchors());
		for (Anchor oldAnchor : oldAnchors) {
			List<Connection> connections = new ArrayList<Connection>();
			connections.addAll(oldAnchor.getIncomingConnections());
			connections.addAll(oldAnchor.getOutgoingConnections());
			for (Connection connection : connections) {
				Anchor newAnchor = newShape.getAnchors().get(0);
				ILocation oldLocation = Graphiti.getPeService().getLocationRelativeToDiagram(oldAnchor);
				if (AnchorUtil.isAdHocAnchor(oldAnchor)) {
					// old anchor is an ad-hoc anchor
					newAnchor = AnchorUtil.createAdHocAnchor(newShape, oldLocation.getX(), oldLocation.getY());
				}
				else if (AnchorUtil.isBoundaryAnchor(oldAnchor)) {
					// must be a boundary anchor
					AnchorLocation oldLoc = AnchorUtil.getBoundaryAnchorLocation(oldAnchor);
					newAnchor = AnchorUtil.getBoundaryAnchors(newShape).get(oldLoc).anchor;

				}

				ReconnectionContext reconnectContext = new ReconnectionContext(connection, oldAnchor, newAnchor, oldLocation);
				reconnectContext.setTargetPictogramElement(newShape);
				if (connection.getStart()==oldAnchor)
					reconnectContext.setReconnectType(ReconnectionContext.RECONNECT_SOURCE);
				else
					reconnectContext.setReconnectType(ReconnectionContext.RECONNECT_TARGET);
				IReconnectionFeature reconnectFeature = getFeatureProvider().getReconnectionFeature(reconnectContext);
				if (reconnectFeature.canReconnect(reconnectContext))
					reconnectFeature.reconnect(reconnectContext);
			}
		}
		
		// delete the old shape
		DeleteContext deleteContext = new DeleteContext(oldShape);
		IDeleteFeature deleteFeature = getFeatureProvider().getDeleteFeature(deleteContext);
		if (deleteFeature.canDelete(deleteContext))
			deleteFeature.delete(deleteContext);
		
		return newShape;
	}

	protected List<Shape> getFlowElementChildren(ContainerShape containerShape) {
		List<Shape> children = new ArrayList<Shape>();
		for (Shape s : containerShape.getChildren()) {
			FlowElement bo = BusinessObjectUtil.getFirstElementOfType(s, FlowElement.class);
			if (s instanceof ContainerShape && bo!=null) {
				children.add(s);
			}
		}
		return children;
	}

	protected Bpmn2ToolBehaviorProvider getToolProvider() {
		IToolBehaviorProvider[] toolProviders = getFeatureProvider().getDiagramTypeProvider().getAvailableToolBehaviorProviders();
		Bpmn2ToolBehaviorProvider toolProvider = null;
		for (IToolBehaviorProvider tp : toolProviders) {
			if (tp instanceof Bpmn2ToolBehaviorProvider) {
				return (Bpmn2ToolBehaviorProvider)tp;
			}
		}
		return null;
	}
	
	protected List<IToolEntry> getTools(ICustomContext context) {
		List<IToolEntry> tools = new ArrayList<IToolEntry>();
		Bpmn2ToolBehaviorProvider toolProvider = getToolProvider();

		if (toolProvider!=null) {
			List<EClass> availableTypes = getAvailableTypes(context);
		
			for (IToolEntry te : toolProvider.getTools()) {
				if (te instanceof ObjectCreationToolEntry) {
					ObjectCreationToolEntry cte = (ObjectCreationToolEntry)te;
					ICreateFeature f = cte.getCreateFeature();
					if (f instanceof IBpmn2CreateFeature && !(f instanceof CreateCustomShapeFeature)) {
						EClass type = ((IBpmn2CreateFeature)f).getBusinessObjectClass();
						if (availableTypes.contains(type))
							tools.add(te);
					}
				}
			}
		}
		return tools;
	}

	public abstract EClass getBusinessObjectClass();
	public abstract void copyBusinessObject(T oldObject, T newObject);

	@Override
	public boolean hasDoneChanges() {
		return changesDone;
	}
}
