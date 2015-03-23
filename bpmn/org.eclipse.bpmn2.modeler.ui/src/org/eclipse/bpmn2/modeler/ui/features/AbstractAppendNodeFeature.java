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
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.features.IBpmn2CreateFeature;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.bpmn2.modeler.core.preferences.Bpmn2Preferences;
import org.eclipse.bpmn2.modeler.core.preferences.ModelEnablements;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.core.utils.GraphicsUtil;
import org.eclipse.bpmn2.modeler.core.utils.Tuple;
import org.eclipse.bpmn2.modeler.ui.diagram.Bpmn2ToolBehaviorProvider;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
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
public abstract class AbstractAppendNodeFeature<T extends FlowNode> extends AbstractCustomFeature {
	
	protected boolean changesDone = false;
	protected Bpmn2Preferences preferences;
	
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
	public AbstractAppendNodeFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		CreateContext createContext = prepareCreateContext(context);
		if (createContext==null)
			return false;
		
		List<IToolEntry> tools = getTools();
		if (tools.size()==0)
			return false;
		
		for (IToolEntry tool : tools) {
			ICreateFeature feature = ((ObjectCreationToolEntry)tool).getCreateFeature();
			if (!feature.canCreate(createContext))
				return false;
		}
		return true;
	}

	@Override
	public boolean isAvailable(IContext context) {
		return getTools().size()>0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public void execute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			PictogramElement pe = pes[0];
			Object bo = getBusinessObjectForPictogramElement(pe);
			preferences = Bpmn2Preferences.getInstance((EObject)bo);
			if (pe instanceof ContainerShape && bo instanceof FlowNode) {
				ContainerShape oldShape = (ContainerShape)pe;
				
				// Let user select the new type of object to append. The selection will
				// be from a list of subtypes of <code>T</code> as defined by the various
				// AbstractAppendNodeNodeFeature specializations; for example the class
				// AppendActivityFeature will construct a popup list of all Activity subclasses
				// e.g. Task, ScriptTask, SubProcess, etc. 
				ICreateFeature createFeature = selectNewShape();
				if (createFeature!=null) {
					CreateContext createContext = prepareCreateContext(context);

					if (createFeature.canCreate(createContext)) {
						// if user made a selection, then create the new shape...
						ContainerShape newShape = createNewShape(oldShape, createFeature, createContext);
						// ...and connect this shape to the new one with a SequenceFlow...
						createNewConnection(oldShape, newShape);
						
						// .. then reroute the connection
						FeatureSupport.updateConnections(getFeatureProvider(), newShape);

						getFeatureProvider().
							getDiagramTypeProvider().
							getDiagramBehavior().
							getDiagramContainer().
							setPictogramElementForSelection(newShape);
						
						changesDone = true;
					}
				}
			}
		}
	}
	
	protected ICreateFeature selectNewShape() {
		Bpmn2ToolBehaviorProvider toolProvider = getToolProvider();
		List<IToolEntry> tools = getTools();
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
					
					feature = ((ObjectCreationToolEntry)tool).getCreateFeature();
				}
				else
					feature = null;
			}
		}

		return feature;
	}

	protected List<EClass> getAvailableTypes() {
		DiagramEditor editor = (DiagramEditor)getDiagramEditor();
		ModelEnablements enablements =
				(ModelEnablements)editor.getAdapter(ModelEnablements.class);
		EClass newType = getBusinessObjectClass();

		// build a list of possible subclasses for the popup menu
		List<EClass> subtypes = new ArrayList<EClass>();
		for (EClassifier ec : Bpmn2Package.eINSTANCE.getEClassifiers() ) {
			if (ec instanceof EClass) {
				if ( ((EClass) ec).isAbstract()) {
					continue;
				}
				EList<EClass>superTypes = ((EClass)ec).getEAllSuperTypes(); 
				if (superTypes.contains(newType) &&
						enablements.isEnabled((EClass)ec)) {
					if (ec!=Bpmn2Package.eINSTANCE.getBoundaryEvent() &&
							ec!=Bpmn2Package.eINSTANCE.getStartEvent()) {
						subtypes.add((EClass)ec);
					}
				}
			}
		}
		return subtypes;
	}
	
	protected ContainerShape createNewShape(ContainerShape oldShape, ICreateFeature createFeature, CreateContext createContext) {
		ILayoutService layoutService = Graphiti.getLayoutService();
		boolean horz = preferences.isHorizontalDefault();

		ILocation loc = layoutService.getLocationRelativeToDiagram(oldShape);
		int x = loc.getX();
		int y = loc.getY();
		int xOffset = 0;
		int yOffset = 0;
		GraphicsAlgorithm ga = oldShape.getGraphicsAlgorithm();
		int width = ga.getWidth();
		int height = ga.getHeight();
		
		FlowElement newObject;
		ContainerShape newShape;
		createContext.setX(0);
		createContext.setY(0);
		Object[] created = createFeature.create(createContext);
		if (created[0] instanceof List) {
			// this will happen if the createFeature is a CompoundCreateFeature
			// for example an Event with an EventDefinition child element
			newObject = (FlowElement) ((List)created[0]).get(0);
			newShape = (ContainerShape) ((List)created[0]).get(1);
		}
		else {
			newObject = (FlowElement) created[0];
			newShape = (ContainerShape) created[1];
		}
		
		ContainerShape containerShape = oldShape.getContainer();
		if (containerShape!=getDiagram()) {
			// we are adding a new shape to a container (e.g a SubProcess)
			// so we need to adjust the location to be relative to the
			// container instead of the diagram
			loc = layoutService.getLocationRelativeToDiagram(containerShape);
			xOffset = loc.getX();
			yOffset = loc.getY();
		}
		
		BaseElement oldObject = BusinessObjectUtil.getFirstElementOfType(oldShape, BaseElement.class);
		if (oldObject instanceof Lane) {
			((Lane)oldObject).getFlowNodeRefs().add((FlowNode)newObject);
		}
		
		// move the new shape so that it does not collide with an existing shape
		MoveShapeContext moveContext = new MoveShapeContext(newShape);//new AreaContext(), newObject);
		DefaultMoveShapeFeature moveFeature = (DefaultMoveShapeFeature)getFeatureProvider().getMoveShapeFeature(moveContext);
		IDimension size = GraphicsUtil.calculateSize(newShape);
		int wOffset = 50;
		int hOffset = 50;
		int w = size.getWidth();
		int h = size.getHeight();
		if (horz) {
			x += width + wOffset + w/2;
			y += height/2 - h/2;
			boolean done = false;
			while (!done) {
				done = true;
				List<Shape> shapes = getFlowElementChildren(containerShape);
				for (Shape s : shapes) {
					if (GraphicsUtil.intersects(s, x-w/2, y-h/2, w, h)) {
						y += 100;
						done = false;
						break;
					}
				}
			}
		}
		else {
			x += width/2 - w/2;
			y += height + hOffset + h/2;
			boolean done = false;
			while (!done) {
				done = true;
				List<Shape> shapes = getFlowElementChildren(containerShape);
				for (Shape s : shapes) {
					if (GraphicsUtil.intersects(s, x-w/2, y-h/2, w, h)) {
						x += 100;
						done = false;
						break;
					}
				}
			}
		}
		moveContext.setX(x - xOffset);
		moveContext.setY(y - yOffset);
		moveContext.setSourceContainer( oldShape.getContainer() );
		moveContext.setTargetContainer( oldShape.getContainer() );
		
		if (moveFeature.canMoveShape(moveContext))
			moveFeature.moveShape(moveContext);
		
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
	
	protected Connection createNewConnection(ContainerShape oldShape, ContainerShape newShape) {
		Tuple<FixPointAnchor, FixPointAnchor> anchors = AnchorUtil.getSourceAndTargetBoundaryAnchors(oldShape, newShape, null);

		CreateConnectionContext ccc = new CreateConnectionContext();
		ccc.setSourcePictogramElement(oldShape);
		ccc.setTargetPictogramElement(newShape);
		ccc.setSourceAnchor(anchors.getFirst());
		ccc.setTargetAnchor(anchors.getSecond());

		FlowNode oldObject = BusinessObjectUtil.getFirstElementOfType(oldShape, FlowNode.class);
		FlowNode newObject = BusinessObjectUtil.getFirstElementOfType(newShape, FlowNode.class);

		// create a new SequenceFlow to connect the old and new FlowNodes
		SequenceFlow sequenceFlow = Bpmn2ModelerFactory.create(oldObject.eResource(), SequenceFlow.class);
		FlowElementsContainer container = (FlowElementsContainer) oldObject.eContainer();
		container.getFlowElements().add(sequenceFlow);
		sequenceFlow.setSourceRef(oldObject);
		sequenceFlow.setTargetRef(newObject);
		sequenceFlow.setName(null);

		AddConnectionContext acc = new AddConnectionContext(ccc.getSourceAnchor(), ccc.getTargetAnchor());
		acc.setNewObject(sequenceFlow);
		Connection connection = (Connection)getFeatureProvider().addIfPossible(acc);
		return connection;
	}

	protected Bpmn2ToolBehaviorProvider getToolProvider() {
		IToolBehaviorProvider[] toolProviders = getFeatureProvider().getDiagramTypeProvider().getAvailableToolBehaviorProviders();
		for (IToolBehaviorProvider tp : toolProviders) {
			if (tp instanceof Bpmn2ToolBehaviorProvider) {
				return (Bpmn2ToolBehaviorProvider)tp;
			}
		}
		return null;
	}
	
	protected List<IToolEntry> getTools() {
		List<IToolEntry> tools = new ArrayList<IToolEntry>();
		Bpmn2ToolBehaviorProvider toolProvider = getToolProvider();

		if (toolProvider!=null) {
			List<EClass> availableTypes = getAvailableTypes();
		
			for (IToolEntry te : toolProvider.getTools()) {
				if (te instanceof ObjectCreationToolEntry) {
					ObjectCreationToolEntry cte = (ObjectCreationToolEntry)te;
					ICreateFeature f = cte.getCreateFeature();
					if (f instanceof IBpmn2CreateFeature) {
						EClass type = ((IBpmn2CreateFeature)f).getBusinessObjectClass();
						if (availableTypes.contains(type))
							tools.add(te);
					}
				}
			}
		}
		return tools;
	}

	/**
	 * @return
	 */
	public abstract EClass getBusinessObjectClass();

	@Override
	public boolean hasDoneChanges() {
		return changesDone;
	}

	private CreateContext prepareCreateContext(ICustomContext context) {
		CreateContext cc = new CreateContext();
		PictogramElement[] pes = context.getPictogramElements();
		if (pes==null || pes.length!=1)
			return null;
		EObject container = pes[0].eContainer();
		if (!(container instanceof ContainerShape))
			return null;
		
		cc.setTargetContainer((ContainerShape)container);
		
		// set the IMPORT flag so that the new shape's location is not adjusted during creation
		cc.putProperty(GraphitiConstants.IMPORT_PROPERTY, Boolean.TRUE);
		return cc;
	}
}
