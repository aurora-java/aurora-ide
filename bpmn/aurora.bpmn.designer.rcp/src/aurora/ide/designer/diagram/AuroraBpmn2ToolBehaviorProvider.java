package aurora.ide.designer.diagram;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.IntermediateCatchEvent;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.eclipse.bpmn2.modeler.ui.diagram.BPMN2FeatureProvider;
import org.eclipse.bpmn2.modeler.ui.diagram.Bpmn2FeatureMap;
import org.eclipse.bpmn2.modeler.ui.diagram.Bpmn2ToolBehaviorProvider;
import org.eclipse.bpmn2.modeler.ui.diagram.Messages;
import org.eclipse.bpmn2.modeler.ui.editor.BPMN2Editor;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.impl.ConnectionCreationToolEntry;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;

import aurora.ide.designer.diagram.feature.CreateIntermediateCatchTimerEventFeature;

public class AuroraBpmn2ToolBehaviorProvider extends Bpmn2ToolBehaviorProvider {
	// protected List<IPaletteCompartmentEntry> palette;

	public AuroraBpmn2ToolBehaviorProvider(
			IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);
	}

	@Override
	public IPaletteCompartmentEntry[] getPalette() {
		editor = (BPMN2Editor) getDiagramTypeProvider().getDiagramEditor();
		targetRuntime = editor.getTargetRuntime();
		modelEnablements = editor.getModelEnablements();
		featureProvider = (BPMN2FeatureProvider) getFeatureProvider();
		palette = new ArrayList<IPaletteCompartmentEntry>();
		createDefaultpalette();
		// createTimeEvent();
		return palette.toArray(new IPaletteCompartmentEntry[palette.size()]);
	}

	void createTimeEvent() {
		String name = "TimeEvent";

		PaletteCompartmentEntry compartmentEntry = new PaletteCompartmentEntry(
				name, null);
		compartmentEntry.setInitiallyOpen(false);
		CreateIntermediateCatchTimerEventFeature feature = new CreateIntermediateCatchTimerEventFeature(
				featureProvider);
		feature.getDescription();
		// IFeature feature1 = featureProvider
		// .getCreateFeatureForBusinessObject(IntermediateCatchEvent.class);
		// IFeature feature2 = featureProvider
		// .getCreateFeatureForBusinessObject(TimerEventDefinition.class);
		if (feature instanceof ICreateFeature) {
			ICreateFeature cf = (ICreateFeature) feature;
			ObjectCreationToolEntry objectCreationToolEntry = new ObjectCreationToolEntry(
					cf.getCreateName(), cf.getDescription(),
					cf.getCreateImageId(), cf.getCreateLargeImageId(), cf);
			compartmentEntry.addToolEntry(objectCreationToolEntry);
		}

		if (compartmentEntry.getToolEntries().size() > 0)
			palette.add(compartmentEntry);

	}

	private void createDefaultpalette() {
		createDrawer(Messages.BPMNToolBehaviorProvider_Connectors_Drawer_Label,
				AuroraBpmn2FeatureMap.CONNECTIONS, palette);
		createDrawer(Messages.BPMNToolBehaviorProvider_SwimLanes_Drawer_Label,
				AuroraBpmn2FeatureMap.SWIMLANES, palette);
		createDrawer(Messages.BPMNToolBehaviorProvider_Tasks_Drawer_Label,
				AuroraBpmn2FeatureMap.TASKS, palette);
		createDrawer(Messages.BPMNToolBehaviorProvider_Gateways_Drawer_Label,
				AuroraBpmn2FeatureMap.GATEWAYS, palette);
		createDrawer(Messages.BPMNToolBehaviorProvider_Events_Drawer_Label,
				AuroraBpmn2FeatureMap.EVENTS, palette);
		// createDrawer(
		// Messages.BPMNToolBehaviorProvider_Event_Definitions_Drawer_Label,
		// AuroraBpmn2FeatureMap.EVENT_DEFINITIONS, palette);
		// createDrawer(Messages.BPMNToolBehaviorProvider_Data_Items_Drawer_Label,
		// Bpmn2FeatureMap.DATA, palette);
		createDrawer(Messages.BPMNToolBehaviorProvider_SubProcess_Drawer_Label,
				AuroraBpmn2FeatureMap.SUBPROCESS, palette);
		createDrawer(
				Messages.BPMNToolBehaviorProvider_GlobalTasks_Drawer_Label,
				AuroraBpmn2FeatureMap.GLOBAL_TASKS, palette);
		// createDrawer(
		// Messages.BPMNToolBehaviorProvider_Choreography_Drawer_Label,
		// Bpmn2FeatureMap.CHOREOGRAPHY, palette);
		// createDrawer(
		// Messages.BPMNToolBehaviorProvider_Conversation_Drawer_Label,
		// Bpmn2FeatureMap.CONVERSATION, palette);
		// createDrawer(Messages.BPMNToolBehaviorProvider_Artifact_Drawer_Label,
		// Bpmn2FeatureMap.ARTIFACTS, palette);
		// createCustomTasks(palette);
	}

	private void createDrawer(String name, List<Class> items,
			List<IPaletteCompartmentEntry> palette) {
		PaletteCompartmentEntry compartmentEntry = new PaletteCompartmentEntry(
				name, null);
		compartmentEntry.setInitiallyOpen(false);

		createEntries(items, compartmentEntry);

		if (compartmentEntry.getToolEntries().size() > 0)
			palette.add(compartmentEntry);
	}

	private void createEntries(List<Class> neededEntries,
			PaletteCompartmentEntry compartmentEntry) {
		for (Object o : neededEntries) {
			if (o instanceof Class) {
				createEntry((Class) o, compartmentEntry);
			}
		}
	}

	private void createEntry(Class c, PaletteCompartmentEntry compartmentEntry) {
		if (isEnabled(c.getSimpleName())) {
			IFeature feature = featureProvider
					.getCreateFeatureForBusinessObject(c);
			if (feature instanceof ICreateFeature) {
				ICreateFeature cf = (ICreateFeature) feature;
				ObjectCreationToolEntry objectCreationToolEntry = new ObjectCreationToolEntry(
						cf.getCreateName(), cf.getDescription(),
						cf.getCreateImageId(), cf.getCreateLargeImageId(), cf);
				compartmentEntry.addToolEntry(objectCreationToolEntry);
			} else if (feature instanceof ICreateConnectionFeature) {
				ICreateConnectionFeature cf = (ICreateConnectionFeature) feature;
				ConnectionCreationToolEntry connectionCreationToolEntry = new ConnectionCreationToolEntry(
						cf.getCreateName(), cf.getDescription(),
						cf.getCreateImageId(), cf.getCreateLargeImageId());
				connectionCreationToolEntry.addCreateConnectionFeature(cf);
				compartmentEntry.addToolEntry(connectionCreationToolEntry);
			}
		}
	}

	private boolean isEnabled(String className) {
		// return modelEnablements.isEnabled(className);
		return true;
	}

	public void createPaletteProfilesGroup(BPMN2Editor editor,
			PaletteRoot paletteRoot) {
		// super.createPaletteProfilesGroup(editor, paletteRoot);
	}
}
