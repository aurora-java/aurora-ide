package aurora.ide.meta.gef.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.plugin.source.gen.screen.model.Button;
import aurora.plugin.source.gen.screen.model.CheckBox;
import aurora.plugin.source.gen.screen.model.FieldSet;
import aurora.plugin.source.gen.screen.model.Form;
import aurora.plugin.source.gen.screen.model.Grid;
import aurora.plugin.source.gen.screen.model.GridColumn;
import aurora.plugin.source.gen.screen.model.HBox;
import aurora.plugin.source.gen.screen.model.Input;
import aurora.plugin.source.gen.screen.model.Label;
import aurora.plugin.source.gen.screen.model.TabFolder;
import aurora.plugin.source.gen.screen.model.TabItem;
import aurora.plugin.source.gen.screen.model.Toolbar;
import aurora.plugin.source.gen.screen.model.VBox;


public class VScreenEditorPaletteFactory {

	private static List<PaletteContainer> createCategories(PaletteRoot root) {
		List<PaletteContainer> categories = new ArrayList<PaletteContainer>();
		categories.add(createControlGroup(root));
		categories.add(createInputDrawer());
		categories.add(createButtonDrawer());
		categories.add(createLayoutDrawer());
		categories.add(createGridDrawer());

		return categories;
	}

	private static List<PaletteContainer> createCategories(PaletteRoot root,
			EditorMode editorMode) {
		List<PaletteContainer> categories = new ArrayList<PaletteContainer>();
		categories.add(createControlGroup(root));
		if (!EditorMode.Template.equals(editorMode.getMode())) {
			categories.add(createInputDrawer());
		}
		categories.add(createButtonDrawer());
		categories.add(createLayoutDrawer());
		if (!EditorMode.Template.equals(editorMode.getMode())) {
			categories.add(createGridDrawer());
			categories.add(createTabDrawer());
		}
		return categories;
	}

	private static PaletteContainer createTabDrawer() {

		PaletteDrawer drawer = new PaletteDrawer("Tab", null);

		List<CombinedTemplateCreationEntry> entries = new ArrayList<CombinedTemplateCreationEntry>();

		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"Tab Folder", "Create a  TabFolder", TabFolder.class,
				new SimpleFactory(TabFolder.class),
				PrototypeImagesUtils.getImageDescriptor("palette/tabfolder.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/tabfolder.png"));
		entries.add(combined);
		// tab item
		combined = new CombinedTemplateCreationEntry("Tab Item",
				"Create a  TabItem", TabItem.class, new SimpleFactory(
						TabItem.class),
				PrototypeImagesUtils.getImageDescriptor("palette/tabitem.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/tabitem.png"));
		entries.add(combined);

		drawer.addAll(entries);
		return drawer;
	}

	private static PaletteContainer createGridDrawer() {

		PaletteDrawer drawer = new PaletteDrawer("Grid", null);

		List<CombinedTemplateCreationEntry> entries = new ArrayList<CombinedTemplateCreationEntry>();

		// /button
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"Grid", "Create a  Grid", Grid.class, new SimpleFactory(
						Grid.class),
				PrototypeImagesUtils.getImageDescriptor("palette/grid.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/grid.png"));
		entries.add(combined);

		combined = new CombinedTemplateCreationEntry("Grid Column",
				"Create a  Grid Column", GridColumn.class, new SimpleFactory(
						GridColumn.class),
				PrototypeImagesUtils.getImageDescriptor("palette/column.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/column.png"));
		entries.add(combined);
		drawer.addAll(entries);
		return drawer;
	}

	private static PaletteContainer createInputDrawer() {

		PaletteDrawer drawer = new PaletteDrawer("Input", null);

		List<CombinedTemplateCreationEntry> entries = new ArrayList<CombinedTemplateCreationEntry>();

		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"Label", "Create a new Label", Label.class,
				new SimpleFactory(Label.class) {
					public Object getNewObject() {
						Label newObject = (Label) super.getNewObject();
						return newObject;
					}
				}, PrototypeImagesUtils.getImageDescriptor("palette/label.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/label.png"));
		entries.add(combined);
		
		combined = new CombinedTemplateCreationEntry(
				"TextField", "Create a new TextField", Input.class,
				new SimpleFactory(Input.class) {
					public Object getNewObject() {
						Input newObject = (Input) super.getNewObject();
						newObject.setComponentType(Input.TEXT);
						return newObject;
					}
				}, PrototypeImagesUtils.getImageDescriptor("palette/input_edit.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/input_edit.png"));
		entries.add(combined);
		// number field
		combined = new CombinedTemplateCreationEntry("NumberField",
				"Create a new NumberField", Input.class, new SimpleFactory(
						Input.class) {
					public Object getNewObject() {
						Input newObject = (Input) super.getNewObject();
						newObject.setComponentType(Input.NUMBER);
						return newObject;
					}
				}, PrototypeImagesUtils.getImageDescriptor("palette/input_edit.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/input_edit.png"));
		entries.add(combined);

		combined = new CombinedTemplateCreationEntry("Combox",
				"Create a new Combox", Input.class, new SimpleFactory(
						Input.class) {
					public Object getNewObject() {
						Input newObject = (Input) super.getNewObject();
						newObject.setComponentType(Input.Combo);
						return newObject;
					}
				}, PrototypeImagesUtils.getImageDescriptor("palette/itembar_01.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/itembar_01.png"));
		entries.add(combined);

		combined = new CombinedTemplateCreationEntry("DatePicker",
				"Create a new DatePicker", Input.class, new SimpleFactory(
						Input.class) {
					public Object getNewObject() {
						Input newObject = (Input) super.getNewObject();
						newObject.setComponentType(Input.DATE_PICKER);
						return newObject;
					}
				}, PrototypeImagesUtils.getImageDescriptor("palette/itembar_02.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/itembar_02.png"));
		entries.add(combined);
		combined = new CombinedTemplateCreationEntry("DateTimePicker",
				"Create a new DateTimePicker", Input.class, new SimpleFactory(
						Input.class) {
					public Object getNewObject() {
						Input newObject = (Input) super.getNewObject();
						newObject.setComponentType(Input.DATETIMEPICKER);
						return newObject;
					}
				}, PrototypeImagesUtils.getImageDescriptor("palette/itembar_02.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/itembar_02.png"));
		entries.add(combined);

		combined = new CombinedTemplateCreationEntry("LOV", "Create a new Lov",
				Input.class, new SimpleFactory(Input.class) {
					public Object getNewObject() {
						Input newObject = (Input) super.getNewObject();
						newObject.setComponentType(Input.LOV);
						return newObject;
					}
				}, PrototypeImagesUtils.getImageDescriptor("palette/itembar_03.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/itembar_03.png"));
		entries.add(combined);
		// checkbox
		combined = new CombinedTemplateCreationEntry("CheckBox",
				"Create a CheckBox", CheckBox.class, new SimpleFactory(
						CheckBox.class),
				PrototypeImagesUtils.getImageDescriptor("palette/checkbox_01.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/checkbox_01.png"));
		entries.add(combined);
		// TODO Label

		drawer.addAll(entries);
		return drawer;
	}

	private static PaletteContainer createLayoutDrawer() {

		PaletteDrawer drawer = new PaletteDrawer("Layout", null);

		List<CombinedTemplateCreationEntry> entries = new ArrayList<CombinedTemplateCreationEntry>();

		// /button
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"Form", "Create a  Form", Form.class, new SimpleFactory(
						Form.class),
				PrototypeImagesUtils.getImageDescriptor("palette/form.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/form.png"));
		entries.add(combined);

		combined = new CombinedTemplateCreationEntry("FieldSet",
				"Create a  FieldSet", FieldSet.class, new SimpleFactory(
						FieldSet.class),
				PrototypeImagesUtils.getImageDescriptor("palette/fieldset.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/fieldset.png"));
		entries.add(combined);
		combined = new CombinedTemplateCreationEntry("HBox", "Create a  HBox",
				Grid.class, new SimpleFactory(HBox.class),
				PrototypeImagesUtils.getImageDescriptor("palette/hbox.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/hbox.png"));
		entries.add(combined);
		combined = new CombinedTemplateCreationEntry("VBox", "Create a  VBox",
				Grid.class, new SimpleFactory(VBox.class),
				PrototypeImagesUtils.getImageDescriptor("palette/vbox.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/vbox.png"));
		entries.add(combined);

		combined = new CombinedTemplateCreationEntry("Toolbar",
				"Create a  Toolbar ", Toolbar.class, new SimpleFactory(
						Toolbar.class),
				PrototypeImagesUtils.getImageDescriptor("palette/toolbar.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/toolbar.png"));
		entries.add(combined);

		
		drawer.addAll(entries);
		return drawer;
	}

	private static PaletteContainer createButtonDrawer() {

		PaletteDrawer drawer = new PaletteDrawer("Button", null);

		List<CombinedTemplateCreationEntry> entries = new ArrayList<CombinedTemplateCreationEntry>();

		// /button
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"Button", "Create a Button", Button.class, new SimpleFactory(
						Button.class),
				PrototypeImagesUtils.getImageDescriptor("palette/toolbar_btn_01.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/toolbar_btn_01.png"));
		entries.add(combined);
		drawer.addAll(entries);
		return drawer;
	}

	private static PaletteContainer createControlGroup(PaletteRoot root) {
		PaletteGroup controlGroup = new PaletteGroup("Control Group");

		List<PaletteEntry> entries = new ArrayList<PaletteEntry>();

		ToolEntry tool = new SelectionToolEntry();
		entries.add(tool);
		root.setDefaultEntry(tool);

		// tool = new MarqueeToolEntry();
		// entries.add(tool);

		PaletteSeparator sep = new PaletteSeparator(
				"aurora.ide.meta.gef.editors");
		sep.setUserModificationPermission(PaletteEntry.PERMISSION_NO_MODIFICATION);
		entries.add(sep);

		controlGroup.addAll(entries);
		return controlGroup;
	}

	/**
	 * Creates the PaletteRoot and adds all Palette elements.
	 * 
	 * @return the root
	 */
	public static PaletteRoot createPalette() {
		PaletteRoot flowPalette = new PaletteRoot();
		flowPalette.addAll(createCategories(flowPalette));
		return flowPalette;
	}

	public static PaletteRoot createPalette(EditorMode editorMode) {
		PaletteRoot flowPalette = new PaletteRoot();
		flowPalette.addAll(createCategories(flowPalette, editorMode));
		return flowPalette;
	}

}
