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
package org.eclipse.bpmn2.modeler.ui.diagram;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.bpmn2.Group;
import org.eclipse.bpmn2.modeler.core.features.CompoundCreateFeature;
import org.eclipse.bpmn2.modeler.core.features.CompoundCreateFeaturePart;
import org.eclipse.bpmn2.modeler.core.features.CustomConnectionFeatureContainer;
import org.eclipse.bpmn2.modeler.core.features.CustomElementFeatureContainer;
import org.eclipse.bpmn2.modeler.core.features.CustomShapeFeatureContainer;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.features.IBpmn2AddFeature;
import org.eclipse.bpmn2.modeler.core.features.IBpmn2CreateFeature;
import org.eclipse.bpmn2.modeler.core.features.ShowPropertiesFeature;
import org.eclipse.bpmn2.modeler.core.features.activity.ActivitySelectionBehavior;
import org.eclipse.bpmn2.modeler.core.features.choreography.ChoreographyUtil;
import org.eclipse.bpmn2.modeler.core.features.command.CustomKeyCommandFeature;
import org.eclipse.bpmn2.modeler.core.features.event.EventSelectionBehavior;
import org.eclipse.bpmn2.modeler.core.features.gateway.GatewaySelectionBehavior;
import org.eclipse.bpmn2.modeler.core.preferences.ModelEnablements;
import org.eclipse.bpmn2.modeler.core.preferences.ShapeStyle.LabelPosition;
import org.eclipse.bpmn2.modeler.core.runtime.CustomTaskDescriptor;
import org.eclipse.bpmn2.modeler.core.runtime.ModelEnablementDescriptor;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.bpmn2.modeler.core.runtime.ToolPaletteDescriptor;
import org.eclipse.bpmn2.modeler.core.runtime.ToolPaletteDescriptor.CategoryDescriptor;
import org.eclipse.bpmn2.modeler.core.runtime.ToolPaletteDescriptor.ToolDescriptor;
import org.eclipse.bpmn2.modeler.core.runtime.ToolPaletteDescriptor.ToolPart;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.core.utils.GraphicsUtil;
import org.eclipse.bpmn2.modeler.core.utils.ShapeDecoratorUtil;
import org.eclipse.bpmn2.modeler.core.utils.Tuple;
import org.eclipse.bpmn2.modeler.core.validation.ValidationStatusAdapter;
import org.eclipse.bpmn2.modeler.ui.Activator;
import org.eclipse.bpmn2.modeler.ui.IConstants;
import org.eclipse.bpmn2.modeler.ui.ImageProvider;
import org.eclipse.bpmn2.modeler.ui.editor.BPMN2Editor;
import org.eclipse.bpmn2.modeler.ui.features.choreography.ChoreographySelectionBehavior;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.Tool;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.graphiti.IExecutionInfo;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.FeatureCheckerAdapter;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.IFeatureAndContext;
import org.eclipse.graphiti.features.IFeatureChecker;
import org.eclipse.graphiti.features.IFeatureCheckerHolder;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.impl.AddBendpointContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.context.impl.MoveBendpointContext;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.algorithms.AbstractText;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.IToolEntry;
import org.eclipse.graphiti.palette.impl.ConnectionCreationToolEntry;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;
import org.eclipse.graphiti.platform.IPlatformImageConstants;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.tb.ContextButtonEntry;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.graphiti.tb.IContextButtonPadData;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.tb.IImageDecorator;
import org.eclipse.graphiti.tb.ImageDecorator;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.util.ILocationInfo;
import org.eclipse.graphiti.util.LocationInfo;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;

public class Bpmn2ToolBehaviorProvider extends DefaultToolBehaviorProvider implements IFeatureCheckerHolder {

	protected BPMN2Editor editor;
	protected TargetRuntime targetRuntime;
	protected BPMN2FeatureProvider featureProvider;
	protected ModelEnablements modelEnablements;
	protected Hashtable<String, PaletteCompartmentEntry> categories = new Hashtable<String, PaletteCompartmentEntry>();
	protected List<IPaletteCompartmentEntry> palette;
	protected CustomKeyCommandFeature commandFeature = null;
	
	protected class ProfileSelectionToolEntry extends ToolEntry {
		BPMN2Editor editor;
		
		ProfileSelectionToolEntry(BPMN2Editor editor, String profileId) {
			super("", null, null, null, null);
			TargetRuntime rt = editor.getTargetRuntime();
			ModelEnablementDescriptor med = rt.getModelEnablements(profileId);
			setLabel(med.getProfileName());
			setId(profileId);
			setDescription(med.getDescription());
			this.editor = editor;
		}
		
		public Tool createTool() {
			TargetRuntime rt = editor.getTargetRuntime();
			editor.getPreferences().setDefaultToolProfile(rt, getId());
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					editor.updatePalette();
				}
				
			});
			return null;
		}

		@Override
		public ImageDescriptor getLargeIcon() {
			return super.getSmallIcon();
		}

		@Override
		public ImageDescriptor getSmallIcon() {
			TargetRuntime rt = editor.getTargetRuntime();
			String profileId = editor.getPreferences().getDefaultToolProfile(rt);
			if (getId().equals(profileId))
				return Activator.getDefault().getImageDescriptor(IConstants.ICON_CHECKBOX_CHECKED_16);
			return Activator.getDefault().getImageDescriptor(IConstants.ICON_CHECKBOX_UNCHECKED_16);
		}
	}
	
	public Bpmn2ToolBehaviorProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);
	}

	public void createPaletteProfilesGroup(BPMN2Editor editor, PaletteRoot paletteRoot) {
		TargetRuntime rt = editor.getTargetRuntime();

		PaletteDrawer drawer = new PaletteDrawer(Messages.BPMNToolBehaviorProvider_Profiles_Drawer_Label, null);
		int size = 0;

		for (String profileId : editor.getPreferences().getAllToolProfiles(rt)) {
			drawer.add(new ProfileSelectionToolEntry(editor, profileId));
			++size;
		}
		if (size>1) {
			drawer.setInitialState(PaletteDrawer.INITIAL_STATE_CLOSED);
			paletteRoot.add(1,drawer);
		}
	}
	
	@Override
	public IPaletteCompartmentEntry[] getPalette() {

		editor = (BPMN2Editor)getDiagramTypeProvider().getDiagramEditor();
		targetRuntime = editor.getTargetRuntime();
		modelEnablements = editor.getModelEnablements();
		featureProvider = (BPMN2FeatureProvider)getFeatureProvider();

		palette = new ArrayList<IPaletteCompartmentEntry>();
		String profile = editor.getPreferences().getDefaultToolProfile(targetRuntime);
		
		PaletteCompartmentEntry compartmentEntry = null;
		categories.clear();
		ToolPaletteDescriptor toolPaletteDescriptor = targetRuntime.getToolPalette(profile);
		if (toolPaletteDescriptor!=null) {
			boolean needCustomTaskDrawer = true;
			for (CategoryDescriptor category : toolPaletteDescriptor.getCategories()) {
				if (ToolPaletteDescriptor.DEFAULT_PALETTE_ID.equals(category.getId())) {
					createDefaultpalette();
					needCustomTaskDrawer = false;
					continue;
				}
				
				category = getRealCategory(targetRuntime, category);
				compartmentEntry = categories.get(category.getName());
				for (ToolDescriptor tool : category.getTools()) {
					tool = getRealTool(targetRuntime, tool);
					IFeature feature = getCreateFeature(tool);
					if (feature!=null) {
						if (compartmentEntry==null) {
							compartmentEntry = new PaletteCompartmentEntry(category.getName(), category.getIcon());
							compartmentEntry.setInitiallyOpen(false);
							categories.put(category.getName(), compartmentEntry);
						}
						createEntry(tool, feature, compartmentEntry);
					}
				}
				// if there are no tools defined for this category, check if it will be
				// used for only Custom Tasks. If so, create the category anyway.
				if (compartmentEntry==null) {
					for (CustomTaskDescriptor tc : targetRuntime.getCustomTaskDescriptors()) {
						if (category.getName().equals(tc.getCategory())) {
							compartmentEntry = new PaletteCompartmentEntry(category.getName(), category.getIcon());
							compartmentEntry.setInitiallyOpen(false);
							categories.put(category.getName(), compartmentEntry);
							palette.add(compartmentEntry);
							break;
						}
					}
				}
				else if (compartmentEntry.getToolEntries().size()>0)
					palette.add(compartmentEntry);
			}
			if (needCustomTaskDrawer)
				createCustomTasks(palette);
		}
		else
		{
			// create a default toolpalette
			createDefaultpalette();
		}
		
		return palette.toArray(new IPaletteCompartmentEntry[palette.size()]);
	}
	
	private CategoryDescriptor getRealCategory(TargetRuntime rt, CategoryDescriptor category) {
		String fromPalette = category.getFromPalette();
		String id = category.getId();
		if (fromPalette!=null && id!=null) {
			for (TargetRuntime otherRt : TargetRuntime.createTargetRuntimes()) {
				if (otherRt!=rt) {
					for (ToolPaletteDescriptor tp : otherRt.getToolPaletteDescriptors()) {
						if ( fromPalette.equals(tp.getId())) {
							for (CategoryDescriptor c : tp.getCategories()) {
								if (id.equals(c.getId())) {
									return c;
								}
							}
						}
					}
				}
			}
		}
		return category;
	}
	
	private ToolDescriptor getRealTool(TargetRuntime rt, ToolDescriptor tool) {
		String fromPalette = tool.getFromPalette();
		String id = tool.getId();
		if (fromPalette!=null && id!=null) {
			for (TargetRuntime otherRt : TargetRuntime.createTargetRuntimes()) {
				if (otherRt!=rt) {
					for (ToolPaletteDescriptor tp : otherRt.getToolPaletteDescriptors()) {
						if ( fromPalette.equals(tp.getId())) {
							for (CategoryDescriptor c : tp.getCategories()) {
								for (ToolDescriptor t : c.getTools()) {
									if (id.equals(t.getId())) {
										return t;
									}
								}
							}
						}
					}
				}
			}
		}
		return tool;
	}
	
	private void createDefaultpalette() {
		createDrawer(Messages.BPMNToolBehaviorProvider_Connectors_Drawer_Label,Bpmn2FeatureMap.CONNECTIONS,palette);
		createDrawer(Messages.BPMNToolBehaviorProvider_SwimLanes_Drawer_Label,Bpmn2FeatureMap.SWIMLANES,palette);
		createDrawer(Messages.BPMNToolBehaviorProvider_Tasks_Drawer_Label,Bpmn2FeatureMap.TASKS,palette);
		createDrawer(Messages.BPMNToolBehaviorProvider_Gateways_Drawer_Label,Bpmn2FeatureMap.GATEWAYS,palette);
		createDrawer(Messages.BPMNToolBehaviorProvider_Events_Drawer_Label,Bpmn2FeatureMap.EVENTS,palette);
		createDrawer(Messages.BPMNToolBehaviorProvider_Event_Definitions_Drawer_Label,Bpmn2FeatureMap.EVENT_DEFINITIONS,palette);
		createDrawer(Messages.BPMNToolBehaviorProvider_Data_Items_Drawer_Label,Bpmn2FeatureMap.DATA,palette);
		createDrawer(Messages.BPMNToolBehaviorProvider_SubProcess_Drawer_Label,Bpmn2FeatureMap.SUBPROCESS,palette);
		createDrawer(Messages.BPMNToolBehaviorProvider_GlobalTasks_Drawer_Label,Bpmn2FeatureMap.GLOBAL_TASKS,palette);
		createDrawer(Messages.BPMNToolBehaviorProvider_Choreography_Drawer_Label,Bpmn2FeatureMap.CHOREOGRAPHY,palette);
		createDrawer(Messages.BPMNToolBehaviorProvider_Conversation_Drawer_Label,Bpmn2FeatureMap.CONVERSATION,palette);
		createDrawer(Messages.BPMNToolBehaviorProvider_Artifact_Drawer_Label,Bpmn2FeatureMap.ARTIFACTS,palette);
		createCustomTasks(palette);
	}
	
	@SuppressWarnings("rawtypes")
	public static List<Tuple<String, List<Class>>> getDefaultPaletteDrawers() {
		List<Tuple<String, List<Class>>> drawers = new ArrayList<Tuple<String, List<Class>>>();
		drawers.add(new Tuple<String,List<Class>>(Messages.BPMNToolBehaviorProvider_Connectors_Drawer_Label,Bpmn2FeatureMap.CONNECTIONS));
		drawers.add(new Tuple<String,List<Class>>(Messages.BPMNToolBehaviorProvider_SwimLanes_Drawer_Label,Bpmn2FeatureMap.SWIMLANES));
		drawers.add(new Tuple<String,List<Class>>(Messages.BPMNToolBehaviorProvider_Tasks_Drawer_Label,Bpmn2FeatureMap.TASKS));
		drawers.add(new Tuple<String,List<Class>>(Messages.BPMNToolBehaviorProvider_Gateways_Drawer_Label,Bpmn2FeatureMap.GATEWAYS));
		drawers.add(new Tuple<String,List<Class>>(Messages.BPMNToolBehaviorProvider_Events_Drawer_Label,Bpmn2FeatureMap.EVENTS));
		drawers.add(new Tuple<String,List<Class>>(Messages.BPMNToolBehaviorProvider_Event_Definitions_Drawer_Label,Bpmn2FeatureMap.EVENT_DEFINITIONS));
		drawers.add(new Tuple<String,List<Class>>(Messages.BPMNToolBehaviorProvider_Data_Items_Drawer_Label,Bpmn2FeatureMap.DATA));
		drawers.add(new Tuple<String,List<Class>>(Messages.BPMNToolBehaviorProvider_SubProcess_Drawer_Label,Bpmn2FeatureMap.SUBPROCESS));
		drawers.add(new Tuple<String,List<Class>>(Messages.BPMNToolBehaviorProvider_GlobalTasks_Drawer_Label,Bpmn2FeatureMap.GLOBAL_TASKS));
		drawers.add(new Tuple<String,List<Class>>(Messages.BPMNToolBehaviorProvider_Choreography_Drawer_Label,Bpmn2FeatureMap.CHOREOGRAPHY));
		drawers.add(new Tuple<String,List<Class>>(Messages.BPMNToolBehaviorProvider_Conversation_Drawer_Label,Bpmn2FeatureMap.CONVERSATION));
		drawers.add(new Tuple<String,List<Class>>(Messages.BPMNToolBehaviorProvider_Artifact_Drawer_Label,Bpmn2FeatureMap.ARTIFACTS));
		return drawers;
	}
	
	public List<IToolEntry> getTools() {
		List<IToolEntry> tools = new ArrayList<IToolEntry>();
		if (palette==null)
			getPalette();
		
		for (IPaletteCompartmentEntry ce : palette) {
			for (IToolEntry te : ce.getToolEntries()) {
				tools.add(te);
			}
		}
		return tools;
	}

	public IPaletteCompartmentEntry getCategory(IToolEntry tool) {
		if (palette==null)
			getPalette();
		
		for (IPaletteCompartmentEntry ce : palette) {
			for (IToolEntry te : ce.getToolEntries()) {
				if (te == tool)
					return ce;
			}
		}
		return null;
	}
	
	private IFeature getCreateFeature(ToolDescriptor tool) {
		if (tool.getToolParts().size()==1)
			return getCreateFeature(tool, null, null, tool.getToolParts().get(0));
		else {
			CompoundCreateFeature compoundFeature = null;
			for (ToolPart tp : tool.getToolParts()) {
				if (compoundFeature==null)
					compoundFeature = new CompoundCreateFeature(featureProvider,tool);
				getCreateFeature(tool, compoundFeature, null, tp);
			}
			return compoundFeature;
		}
	}
	
	private IFeature getCreateFeature(ToolDescriptor tool, CompoundCreateFeature root, CompoundCreateFeaturePart node, ToolPart toolPart) {
		IFeature parentFeature = null;
		String name = toolPart.getName();
		EClassifier eClass = targetRuntime.getModelDescriptor().getClassifier(name);
		if (eClass!=null) {
			parentFeature = featureProvider.getCreateFeatureForBusinessObject(eClass.getInstanceClass());
		}
		else {
			Activator.logError(new IllegalArgumentException(
					"The object type '"+name+"' referenced by the tool '"+root.getName()+"'is undefined"
					));
		}
		
		if (root!=null) {
			if (node!=null) {
				CompoundCreateFeaturePart n = node.addChild(parentFeature);
				if (toolPart.hasProperties()) {
					n.setProperties(toolPart.getProperties());
				}
			}
			else {
				node = root.addChild(parentFeature);
				if (toolPart.hasProperties()) {
					node.setProperties(toolPart.getProperties());
				}
			}
		}
		else if (toolPart.hasProperties()) {
			root = new CompoundCreateFeature(featureProvider, tool);
			node = root.addChild(parentFeature);
			node.setProperties(toolPart.getProperties());
			parentFeature = root;
		}
		
		for (ToolPart childToolPart : toolPart.getChildren()) {
			if (root==null) {
				root = new CompoundCreateFeature(featureProvider, tool);
				node = root.addChild(parentFeature);
				parentFeature = root;
			}
			getCreateFeature(tool, root, node, childToolPart);
		}

		return parentFeature;
	}

	private void createDrawer(String name, List<Class> items, List<IPaletteCompartmentEntry> palette) {
		PaletteCompartmentEntry compartmentEntry = new PaletteCompartmentEntry(name, null);
		compartmentEntry.setInitiallyOpen(false);

		createEntries(items, compartmentEntry);

		if (compartmentEntry.getToolEntries().size()>0)
			palette.add(compartmentEntry);
	}

	private void createEntries(List<Class> neededEntries, PaletteCompartmentEntry compartmentEntry) {
		for (Object o : neededEntries) {
			if (o instanceof Class) {
				createEntry((Class)o, compartmentEntry);
			}
		}
	}
	
	private boolean isEnabled(String className) {
		return modelEnablements.isEnabled(className);
	}
	
	private void createEntry(Class c, PaletteCompartmentEntry compartmentEntry) {
		if (isEnabled(c.getSimpleName())) {
			IFeature feature = featureProvider.getCreateFeatureForBusinessObject(c);
			if (feature instanceof ICreateFeature) {
				ICreateFeature cf = (ICreateFeature)feature;
				ObjectCreationToolEntry objectCreationToolEntry = new ObjectCreationToolEntry(cf.getCreateName(),
					cf.getDescription(), cf.getCreateImageId(), cf.getCreateLargeImageId(), cf);
				compartmentEntry.addToolEntry(objectCreationToolEntry);
			}
			else if (feature instanceof ICreateConnectionFeature) {
				ICreateConnectionFeature cf = (ICreateConnectionFeature)feature;
				ConnectionCreationToolEntry connectionCreationToolEntry = new ConnectionCreationToolEntry(
						cf.getCreateName(), cf.getDescription(), cf.getCreateImageId(),
						cf.getCreateLargeImageId());
				connectionCreationToolEntry.addCreateConnectionFeature(cf);
				compartmentEntry.addToolEntry(connectionCreationToolEntry);
			}
		}
	}
	
	private void createEntry(ToolDescriptor tool, IFeature feature, PaletteCompartmentEntry compartmentEntry) {
		if (modelEnablements.isEnabled(feature) || feature instanceof CompoundCreateFeature) {
			IFeature targetFeature = feature;
			if (feature instanceof CompoundCreateFeature) {
				CompoundCreateFeature cf = (CompoundCreateFeature)feature;
				targetFeature = ((CompoundCreateFeaturePart)cf.getChildren().get(0)).getFeature();
			}
			if (targetFeature instanceof ICreateFeature) {
				ICreateFeature cf = (ICreateFeature)feature;
				String name = tool.getName();
				if (name==null)
					name = cf.getName();
				String description = tool.getDescription();
				if (description==null)
					description = cf.getCreateDescription();
				ObjectCreationToolEntry objectCreationToolEntry = new ObjectCreationToolEntry(
						name, description, cf.getCreateImageId(), cf.getCreateLargeImageId(), cf);
				compartmentEntry.addToolEntry(objectCreationToolEntry);
			}
			else if (targetFeature instanceof ICreateConnectionFeature) {
				ICreateConnectionFeature cf = (ICreateConnectionFeature)feature;
				String name = tool.getName();
				if (name==null)
					name = cf.getName();
				String description = tool.getDescription();
				if (description==null)
					description = cf.getCreateDescription();
				ConnectionCreationToolEntry connectionCreationToolEntry = new ConnectionCreationToolEntry(
						name, description, cf.getCreateImageId(), cf.getCreateLargeImageId());
				connectionCreationToolEntry.addCreateConnectionFeature(cf);
				compartmentEntry.addToolEntry(connectionCreationToolEntry);
			}
		}
	}

	private void createCustomTasks(List<IPaletteCompartmentEntry> ret) {

		PaletteCompartmentEntry compartmentEntry = null;
		BPMN2Editor editor = (BPMN2Editor) getDiagramTypeProvider().getDiagramEditor();
		TargetRuntime rt = editor.getTargetRuntime();
		
		try {
			for (IPaletteCompartmentEntry e : ret) {
				categories.put(e.getLabel(), (PaletteCompartmentEntry) e);
			}
			
			for (CustomTaskDescriptor ctd : rt.getCustomTaskDescriptors()) {
				CustomElementFeatureContainer container = (CustomElementFeatureContainer)ctd.getFeatureContainer();
				if (!container.isAvailable(featureProvider))
					continue;

				IToolEntry toolEntry = null;
				String id = ctd.getId();
				container.setId(id);
				featureProvider.addFeatureContainer(id, container);
				if (container instanceof CustomShapeFeatureContainer) {
					ICreateFeature cf = ((CustomShapeFeatureContainer)container).getCreateFeature(featureProvider);
					ObjectCreationToolEntry objectCreationToolEntry = new ObjectCreationToolEntry(ctd.getName(),
							ctd.getDescription(), cf.getCreateImageId(), cf.getCreateLargeImageId(), cf);
					toolEntry = objectCreationToolEntry;
				}
				else if (container instanceof CustomConnectionFeatureContainer) {
					ICreateConnectionFeature cf = ((CustomConnectionFeatureContainer)container).getCreateConnectionFeature(featureProvider);
					ConnectionCreationToolEntry connectionCreationToolEntry = new ConnectionCreationToolEntry(
							cf.getCreateName(), ctd.getDescription(), cf.getCreateImageId(),
							cf.getCreateLargeImageId());
					connectionCreationToolEntry.addCreateConnectionFeature(cf);
					toolEntry = connectionCreationToolEntry;
				}
				
				String category = ctd.getCategory();
				if (category==null || category.isEmpty())
					category = Messages.BPMNToolBehaviorProvider_Custom_Tasks_Drawer_Label;
				
				compartmentEntry = categories.get(category);
				if (compartmentEntry==null) {
					compartmentEntry = new PaletteCompartmentEntry(category, null);
					compartmentEntry.setInitiallyOpen(false);
					ret.add(compartmentEntry);
					categories.put(category, compartmentEntry);
				}
				
				compartmentEntry.addToolEntry(toolEntry);
			}
		} catch (Exception ex) {
			Activator.logError(ex);
		}
	}

	@Override
	public IFeatureChecker getFeatureChecker() {
		return new FeatureCheckerAdapter(false) {
			@Override
			public boolean allowAdd(IContext context) {
				return super.allowAdd(context);
			}

			@Override
			public boolean allowCreate() {
				return super.allowCreate();
			}
		};
	}

	private boolean isLabelShape(PictogramElement pe) {
		return FeatureSupport.isLabelShape(pe)  && FeatureSupport.getLabelOwner(pe)!=null;
	}
	
	@Override
	public GraphicsAlgorithm[] getClickArea(PictogramElement pe) {
		if (ActivitySelectionBehavior.canApplyTo(pe)) {
			return ActivitySelectionBehavior.getClickArea(pe);
		} else if (EventSelectionBehavior.canApplyTo(pe)) {
			return EventSelectionBehavior.getClickArea(pe);
		} else if (ChoreographySelectionBehavior.canApplyTo(pe)) {
			return ChoreographySelectionBehavior.getClickArea(pe);
		} else if (GatewaySelectionBehavior.canApplyTo(pe)) {
			return GatewaySelectionBehavior.getClickArea(pe);
		} else if (isLabelShape(pe)) {
			return getClickArea(FeatureSupport.getLabelOwner(pe));
		}
		else {
		}
		return super.getClickArea(pe);
	}

	@Override
	public int getLineSelectionWidth(Polyline polyline) {
		PictogramElement pe = polyline.getPictogramElement();
		if (pe!=null && BusinessObjectUtil.getFirstBaseElement(pe) instanceof Group)
			return 20;
		return super.getLineSelectionWidth(polyline);
	}

	@Override
	public GraphicsAlgorithm getSelectionBorder(PictogramElement pe) {
		if (ActivitySelectionBehavior.canApplyTo(pe)) {
			return ActivitySelectionBehavior.getSelectionBorder(pe);
		} else if (EventSelectionBehavior.canApplyTo(pe)) {
			return EventSelectionBehavior.getSelectionBorder(pe);
		} else if (ChoreographySelectionBehavior.canApplyTo(pe)) {
			return ChoreographySelectionBehavior.getSelectionBorder(pe);
		} else if (GatewaySelectionBehavior.canApplyTo(pe)) {
			return GatewaySelectionBehavior.getSelectionBorder(pe);
		} else if (isLabelShape(pe)) {
			return getSelectionBorder(FeatureSupport.getLabelOwner(pe));
		}
		else if (pe instanceof ContainerShape) {
			if (((ContainerShape)pe).getChildren().size()>0) {
				GraphicsAlgorithm ga = ((ContainerShape)pe).getChildren().get(0).getGraphicsAlgorithm();
				if (!(ga instanceof AbstractText) && !(ga instanceof Polyline))
					return ga;
				ga = ((ContainerShape)pe).getGraphicsAlgorithm();
				if (ga.getGraphicsAlgorithmChildren().size()>0)
					return ga.getGraphicsAlgorithmChildren().get(0);
				return ga;
			}
		}
		return super.getSelectionBorder(pe);
	}

	@Override
	public PictogramElement getSelection(PictogramElement originalPe, PictogramElement[] oldSelection) {
		if (isLabelShape(originalPe)) {
			if (FeatureSupport.getLabelPosition(originalPe)!=LabelPosition.MOVABLE) {
				return FeatureSupport.getLabelOwner(originalPe);
			}
		}

		return null;
	}

	public static Point getMouseLocation(IFeatureProvider fp) {
		DiagramBehavior db = (DiagramBehavior) fp.getDiagramTypeProvider().getDiagramBehavior();
		org.eclipse.draw2d.geometry.Point p = db.getMouseLocation();
		p = db.calculateRealMouseLocation(p);
		Point point = GraphicsUtil.createPoint(p.x, p.y);
		return point;
	}
	
	@Override
	public IContextButtonPadData getContextButtonPad(final IPictogramElementContext context) {
		IContextButtonPadData data = super.getContextButtonPad(context);
		PictogramElement pe = context.getPictogramElement();
		if (isLabelShape(pe)) {
			pe = FeatureSupport.getLabelOwner(pe);
		}
		final IFeatureProvider fp = getFeatureProvider();

		if( pe.getGraphicsAlgorithm()!= null && pe.getGraphicsAlgorithm().getWidth() < 40 ){
		    ILocation origin = getAbsoluteLocation(pe.getGraphicsAlgorithm());
		    data.getPadLocation().setRectangle(origin.getX(), origin.getY(), 40, 40);
		}
		
		// 1. set the generic context buttons
		// Participant bands can only be removed from the choreograpy task
		int genericButtons = CONTEXT_BUTTON_DELETE;
		if (ChoreographyUtil.isChoreographyParticipantBand(pe)) {
			genericButtons |= CONTEXT_BUTTON_REMOVE;
		}

		setGenericContextButtons(data, pe, genericButtons);

		// 2. set the expand & collapse buttons
		CustomContext cc = new CustomContext(new PictogramElement[] { pe });
		for (ICustomFeature cf : fp.getCustomFeatures(cc)) {
			if (cf.canExecute(cc)) {
				ContextButtonEntry button = new ContextButtonEntry(cf, cc);
				button.setText(cf.getName()); //$NON-NLS-1$
				button.setIconId(cf.getImageId());
				button.setDescription(cf.getDescription());
				
				data.getDomainSpecificContextButtons().add(button);
			}
		}

		// 3. add one domain specific context-button, which offers all
		// available connection-features as drag&drop features...

		// 3.a. create new CreateConnectionContext
		CreateConnectionContext ccc = new CreateConnectionContext();
		ccc.setSourcePictogramElement(pe);
		Anchor anchor = null;
		if (pe instanceof Anchor) {
			anchor = (Anchor) pe;
		} else if (pe instanceof AnchorContainer) {
			// assume, that our shapes always have chopbox anchors
			anchor = Graphiti.getPeService().getChopboxAnchor((AnchorContainer) pe);
		}
		ccc.setSourceAnchor(anchor);

		// 3.b. create context button and add "Create Connections" feature
		ContextButtonEntry button = new ContextButtonEntry(null, context);
		button.setText("Create Connection"); //$NON-NLS-1$
		String description = null;
		ArrayList<String> names = new ArrayList<String>();
		button.setIconId(ImageProvider.IMG_16_SEQUENCE_FLOW);
		for (IToolEntry te : getTools()) {
			if (te instanceof ConnectionCreationToolEntry) {
				ConnectionCreationToolEntry cte = (ConnectionCreationToolEntry)te;
				for (IFeature f : cte.getCreateConnectionFeatures()) {
					ICreateConnectionFeature ccf = (ICreateConnectionFeature)f;
					if (ccf.isAvailable(ccc) && ccf.canStartConnection(ccc)) {
						button.addDragAndDropFeature(ccf);
						names.add(ccf.getCreateName());
					}
				}
			}
		}
		
		// 3.c. build a reasonable description for the context button action 
		for (int i=0; i<names.size(); ++i) {
			if (description==null)
				description = Messages.BPMNToolBehaviorProvider_Click_Drag_Prompt;
			description += names.get(i);
			if (i+2 == names.size())
				description += Messages.BPMNToolBehaviorProvider_Click_Drag_Prompt_Last_Separator;
			else if (i+1 < names.size())
				description += Messages.BPMNToolBehaviorProvider_Click_Drag_Prompt_Separator;
		}
		button.setDescription(description);

		// 3.d. add context button, button only if it contains at least one feature
		if (button.getDragAndDropFeatures().size() > 0) {
			data.getDomainSpecificContextButtons().add(button);
		}

		return data;
	}
	
	protected ILocation getAbsoluteLocation(GraphicsAlgorithm ga) {
		if (ga.eContainer() instanceof ConnectionDecorator) {
			return Graphiti.getPeService().getLocationRelativeToDiagram((ConnectionDecorator)ga.eContainer());
		}
		return super.getAbsoluteLocation(ga);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void postExecute(IExecutionInfo executionInfo) {
		for (IFeatureAndContext fc : executionInfo.getExecutionList()) {
			IContext context = fc.getContext();
			IFeature feature = fc.getFeature();
			if (context instanceof AddContext) {
				if (feature instanceof IBpmn2AddFeature) {
					((IBpmn2AddFeature)feature).postExecute(executionInfo);
				}
			}
			else if (context instanceof CreateContext) {
				if (feature instanceof IBpmn2CreateFeature) {
					((IBpmn2CreateFeature)feature).postExecute(executionInfo);
				}
			}
		}
	}

	@Override
	public ICustomFeature getDoubleClickFeature(IDoubleClickContext context) {
		ICustomFeature[] cf = getFeatureProvider().getCustomFeatures(context);
		for (int i = 0; i < cf.length; i++) {
			ICustomFeature iCustomFeature = cf[i];
			if (iCustomFeature instanceof ShowPropertiesFeature &&
					iCustomFeature.canExecute(context)) {
				return iCustomFeature;
			}
		}
		// temp debugging stuff to dump connection routing info
		for (PictogramElement pe : context.getPictogramElements()) {
			String id = Graphiti.getPeService().getPropertyValue(pe, "ROUTING_NET_CONNECTION"); //$NON-NLS-1$
			if (pe instanceof FreeFormConnection) {
				System.out.println("id="+id); //$NON-NLS-1$
				FreeFormConnection c = (FreeFormConnection)pe;
				int i=0;
				ILocation loc = Graphiti.getPeService().getLocationRelativeToDiagram(c.getStart());
				System.out.println("0: "+loc.getX()+","+loc.getY()); //$NON-NLS-1$ //$NON-NLS-2$
				for (Point p : c.getBendpoints()) {
					System.out.println(++i+": "+p.getX()+","+p.getY()); //$NON-NLS-1$ //$NON-NLS-2$
				}
				loc = Graphiti.getPeService().getLocationRelativeToDiagram(c.getEnd());
				System.out.println(++i+": "+loc.getX()+","+loc.getY()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return null;
	}

	@Override
	public GraphicsAlgorithm getChopboxAnchorArea(PictogramElement pe) {
		return super.getChopboxAnchorArea(pe);
	}

    @Override
    public IDecorator[] getDecorators(PictogramElement pe) {
        List<IDecorator> decorators = new ArrayList<IDecorator>();

		if (ShapeDecoratorUtil.isValidationDecorator(pe)) {
	        IFeatureProvider featureProvider = getFeatureProvider();
	        Object bo = featureProvider.getBusinessObjectForPictogramElement((PictogramElement) pe.eContainer());
	        if (bo!=null) {
		        ValidationStatusAdapter statusAdapter = (ValidationStatusAdapter) EcoreUtil.getRegisteredAdapter((EObject) bo,
		                ValidationStatusAdapter.class);
		        if (statusAdapter != null) {
		            IImageDecorator decorator;
		            IStatus status = statusAdapter.getValidationStatus();
		            switch (status.getSeverity()) {
		            case IStatus.INFO:
		                decorator = new ImageDecorator(IPlatformImageConstants.IMG_ECLIPSE_INFORMATION_TSK);
		                break;
		            case IStatus.WARNING:
		                decorator = new ImageDecorator(IPlatformImageConstants.IMG_ECLIPSE_WARNING_TSK);
		                break;
		            case IStatus.ERROR:
		                decorator = new ImageDecorator(IPlatformImageConstants.IMG_ECLIPSE_ERROR_TSK);
		                break;
		            default:
		                decorator = null;
		                break;
		            }
		            if (decorator != null) {
		                decorator.setMessage(status.getMessage());
		                decorators.add(decorator);
		            }
		        }
	        }
		}
		
        return decorators.toArray(new IDecorator[decorators.size()]);
    }

	@Override
	public ICustomFeature getCommandFeature(CustomContext context, String hint) {
		if (commandFeature==null)
			commandFeature = new CustomKeyCommandFeature(getFeatureProvider());
		
		if (commandFeature.isAvailable(hint)) {
			context.putProperty(GraphitiConstants.COMMAND_HINT, hint);
			return commandFeature;
		}
		return super.getCommandFeature(context, hint);
	}

	public ILocationInfo getLocationInfo(PictogramElement pe, ILocationInfo locationInfo) {
		if (locationInfo==null) {
			if (pe instanceof ContainerShape) {
				ContainerShape shape = (ContainerShape) pe;
				locationInfo = new LocationInfo(shape, shape.getGraphicsAlgorithm());
			}
		}
		return locationInfo;
	}

	@Override
	public Object getToolTip(GraphicsAlgorithm ga) {
		return FeatureSupport.getToolTip(ga);
	}
}