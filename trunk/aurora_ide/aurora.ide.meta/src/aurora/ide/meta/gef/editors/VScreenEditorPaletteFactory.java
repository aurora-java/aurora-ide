package aurora.ide.meta.gef.editors;

import aurora.ide.meta.gef.editors.models.Button;
import aurora.ide.meta.gef.editors.models.CheckBox;
import aurora.ide.meta.gef.editors.models.FieldSet;
import aurora.ide.meta.gef.editors.models.Form;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.HBox;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.TabFolder;
import aurora.ide.meta.gef.editors.models.TabItem;
import aurora.ide.meta.gef.editors.models.Toolbar;
import aurora.ide.meta.gef.editors.models.VBox;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.SimpleFactory;

public class VScreenEditorPaletteFactory {

	private static List<PaletteContainer> createCategories(PaletteRoot root) {
		List<PaletteContainer> categories = new ArrayList<PaletteContainer>();
		categories.add(createControlGroup(root));
		categories.add(createComponentsDrawer());
		return categories;
	}

	private static PaletteContainer createComponentsDrawer() {

		PaletteDrawer drawer = new PaletteDrawer("Components", null);

		List<CombinedTemplateCreationEntry> entries = new ArrayList<CombinedTemplateCreationEntry>();

		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"TextField", "Create a new TextField", Input.class,
				new SimpleFactory(Input.class) {
					public Object getNewObject() {
						Input newObject = (Input) super.getNewObject();
						newObject.setType(Input.TEXT);
						return newObject;
					}
				}, ImagesUtils.getImageDescriptor("palette/input_edit.png"),
				ImagesUtils.getImageDescriptor("palette/input_edit.png"));
		entries.add(combined);
		// number field
		combined = new CombinedTemplateCreationEntry("NumberField",
				"Create a new NumberField", Input.class, new SimpleFactory(
						Input.class) {
					public Object getNewObject() {
						Input newObject = (Input) super.getNewObject();
						newObject.setType(Input.NUMBER);
						return newObject;
					}
				}, ImagesUtils.getImageDescriptor("palette/input_edit.png"),
				ImagesUtils.getImageDescriptor("palette/input_edit.png"));
		entries.add(combined);

		combined = new CombinedTemplateCreationEntry("Combox",
				"Create a new Combox", Input.class, new SimpleFactory(
						Input.class) {
					public Object getNewObject() {
						Input newObject = (Input) super.getNewObject();
						newObject.setType(Input.Combo);
						return newObject;
					}
				}, ImagesUtils.getImageDescriptor("palette/itembar_01.png"),
				ImagesUtils.getImageDescriptor("palette/itembar_01.png"));
		entries.add(combined);

		combined = new CombinedTemplateCreationEntry("DatePicker",
				"Create a new DatePicker", Input.class, new SimpleFactory(
						Input.class) {
					public Object getNewObject() {
						Input newObject = (Input) super.getNewObject();
						newObject.setType(Input.CAL);
						return newObject;
					}
				}, ImagesUtils.getImageDescriptor("palette/itembar_02.png"),
				ImagesUtils.getImageDescriptor("palette/itembar_02.png"));
		entries.add(combined);
		combined = new CombinedTemplateCreationEntry("DateTimePicker",
				"Create a new DateTimePicker", Input.class, new SimpleFactory(
						Input.class) {
					public Object getNewObject() {
						Input newObject = (Input) super.getNewObject();
						newObject.setType(Input.DATETIMEPICKER);
						return newObject;
					}
				}, ImagesUtils.getImageDescriptor("palette/itembar_02.png"),
				ImagesUtils.getImageDescriptor("palette/itembar_02.png"));
		entries.add(combined);

		combined = new CombinedTemplateCreationEntry("LOV", "Create a new Lov",
				Input.class, new SimpleFactory(Input.class) {
					public Object getNewObject() {
						Input newObject = (Input) super.getNewObject();
						newObject.setType(Input.LOV);
						return newObject;
					}
				}, ImagesUtils.getImageDescriptor("palette/itembar_03.png"),
				ImagesUtils.getImageDescriptor("palette/itembar_03.png"));
		entries.add(combined);
		// checkbox
		combined = new CombinedTemplateCreationEntry("CheckBox",
				"Create a CheckBox", CheckBox.class, new SimpleFactory(
						CheckBox.class),
				ImagesUtils.getImageDescriptor("palette/checkbox_01.png"),
				ImagesUtils.getImageDescriptor("palette/checkbox_01.png"));
		entries.add(combined);
		// radio
		// combined = new CombinedTemplateCreationEntry("Radio",
		// "Create a Radio",
		// Radio.class, new SimpleFactory(Radio.class),
		// ImagesUtils.getImageDescriptor("palette/radio_01.png"),
		// ImagesUtils.getImageDescriptor("palette/radio_01.png"));
		// entries.add(combined);
		// /button
		combined = new CombinedTemplateCreationEntry("Button",
				"Create a Button", Button.class,
				new SimpleFactory(Button.class),
				ImagesUtils.getImageDescriptor("palette/toolbar_btn_01.png"),
				ImagesUtils.getImageDescriptor("palette/toolbar_btn_01.png"));
		entries.add(combined);

		combined = new CombinedTemplateCreationEntry("Form", "Create a  Form",
				Form.class, new SimpleFactory(Form.class),
				ImagesUtils.getImageDescriptor("palette/form.png"),
				ImagesUtils.getImageDescriptor("palette/form.png"));
		entries.add(combined);
		combined = new CombinedTemplateCreationEntry("FieldSet",
				"Create a  FieldSet", FieldSet.class, new SimpleFactory(
						FieldSet.class),
				ImagesUtils.getImageDescriptor("palette/fieldset.png"),
				ImagesUtils.getImageDescriptor("palette/fieldset.png"));
		entries.add(combined);
		combined = new CombinedTemplateCreationEntry("HBox", "Create a  HBox",
				Grid.class, new SimpleFactory(HBox.class),
				ImagesUtils.getImageDescriptor("palette/hbox.png"),
				ImagesUtils.getImageDescriptor("palette/hbox.png"));
		entries.add(combined);
		combined = new CombinedTemplateCreationEntry("VBox", "Create a  VBox",
				Grid.class, new SimpleFactory(VBox.class),
				ImagesUtils.getImageDescriptor("palette/vbox.png"),
				ImagesUtils.getImageDescriptor("palette/vbox.png"));
		entries.add(combined);

		combined = new CombinedTemplateCreationEntry("Grid", "Create a  Grid",
				Grid.class, new SimpleFactory(Grid.class),
				ImagesUtils.getImageDescriptor("palette/grid.png"),
				ImagesUtils.getImageDescriptor("palette/grid.png"));
		entries.add(combined);

		combined = new CombinedTemplateCreationEntry("Grid Column",
				"Create a  Grid Column", GridColumn.class, new SimpleFactory(
						GridColumn.class),
				ImagesUtils.getImageDescriptor("palette/column.png"),
				ImagesUtils.getImageDescriptor("palette/column.png"));
		entries.add(combined);
		combined = new CombinedTemplateCreationEntry("Toolbar",
				"Create a  Toolbar ", Toolbar.class, new SimpleFactory(
						Toolbar.class),
				ImagesUtils.getImageDescriptor("palette/toolbar.png"),
				ImagesUtils.getImageDescriptor("palette/toolbar.png"));
		entries.add(combined);

		// combined = new CombinedTemplateCreationEntry("Navbar",
		// "Create a  Navbar", Navbar.class, new SimpleFactory(
		// Navbar.class),
		// ImagesUtils.getImageDescriptor("palette/navigation_04.png"),
		// ImagesUtils.getImageDescriptor("palette/navigation_04.png"));
		// entries.add(combined);

		// tab folder
		combined = new CombinedTemplateCreationEntry("Tab Folder",
				"Create a  TabFolder", TabFolder.class, new SimpleFactory(
						TabFolder.class),
				ImagesUtils.getImageDescriptor("palette/tabfolder.png"),
				ImagesUtils.getImageDescriptor("palette/tabfolder.png"));
		entries.add(combined);
		// tab item
		combined = new CombinedTemplateCreationEntry("Tab Item",
				"Create a  TabItem", TabItem.class, new SimpleFactory(
						TabItem.class),
				ImagesUtils.getImageDescriptor("palette/tabitem.png"),
				ImagesUtils.getImageDescriptor("palette/tabitem.png"));
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

		tool = new MarqueeToolEntry();
		entries.add(tool);

		PaletteSeparator sep = new PaletteSeparator(
				"org.eclipse.gef.examples.flow.flowplugin.sep2");
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

}
