package aurora.ide.meta.gef.editors.parts;

import java.util.HashMap;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import aurora.ide.meta.gef.editors.EditorMode;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Button;
import aurora.plugin.source.gen.screen.model.CheckBox;
import aurora.plugin.source.gen.screen.model.FieldSet;
import aurora.plugin.source.gen.screen.model.Form;
import aurora.plugin.source.gen.screen.model.Grid;
import aurora.plugin.source.gen.screen.model.GridColumn;
import aurora.plugin.source.gen.screen.model.GridSelectionCol;
import aurora.plugin.source.gen.screen.model.HBox;
import aurora.plugin.source.gen.screen.model.Input;
import aurora.plugin.source.gen.screen.model.Label;
import aurora.plugin.source.gen.screen.model.Navbar;
import aurora.plugin.source.gen.screen.model.ScreenBody;
import aurora.plugin.source.gen.screen.model.TabBody;
import aurora.plugin.source.gen.screen.model.TabFolder;
import aurora.plugin.source.gen.screen.model.TabItem;
import aurora.plugin.source.gen.screen.model.Toolbar;
import aurora.plugin.source.gen.screen.model.VBox;

/**
 */
public class AuroraPartFactory implements EditPartFactory {

	private static HashMap<Class<? extends AuroraComponent>, Class<? extends ComponentPart>> map = 
			new HashMap<Class<? extends AuroraComponent>, Class<? extends ComponentPart>>(20);
	static {
		map.put(ScreenBody.class, ViewDiagramPart.class);
		map.put(Form.class, BoxPart.class);
		map.put(FieldSet.class, BoxPart.class);
		map.put(HBox.class, BoxPart.class);
		map.put(VBox.class, BoxPart.class);
		map.put(CheckBox.class, CheckBoxPart.class);
//		map.put(Radio.class, RadioPart.class);
		map.put(Input.class, InputPart.class);
		map.put(Grid.class, GridPart.class);
		map.put(Button.class, ButtonPart.class);
		map.put(GridColumn.class, GridColumnPart.class);
		map.put(Toolbar.class, ToolbarPart.class);
		map.put(Navbar.class, NavbarPart.class);
		map.put(TabItem.class, TabItemPart.class);
		map.put(TabFolder.class, TabFolderPart.class);
		map.put(TabBody.class, TabBodyPart.class);
		map.put(GridSelectionCol.class, GridSelectionColPart.class);
		map.put(Label.class, LabelPart.class);
	}
	private EditorMode editorMode;

	public AuroraPartFactory(EditorMode editorMode) {
		this.editorMode = editorMode;
	}

	public EditPart createEditPart(EditPart context, Object model) {
		ComponentPart part = null;
		// if (model instanceof ViewDiagram)
		// part = new ViewDiagramPart();
		// // else if (model instanceof Label)
		// // part = new LabelPart();
		// else if (model instanceof Form) {
		// part = new BoxPart();
		// } else if (model instanceof FieldSet) {
		// part = new BoxPart();
		// } else if (model instanceof HBox) {
		// part = new BoxPart();
		// } else if (model instanceof VBox) {
		// part = new BoxPart();
		// } else if (model instanceof CheckBox) {
		// part = new CheckBoxPart();
		// } else if (model instanceof Radio) {
		// part = new RadioPart();
		// } else if (model instanceof Input) {
		// part = new InputPart();
		// } else if (model instanceof Grid) {
		// part = new GridPart();
		// } else if (model instanceof Button) {
		// part = new ButtonPart();
		// } else if (model instanceof GridColumn) {
		// part = new GridColumnPart();
		// } else if (model instanceof Toolbar) {
		// part = new ToolbarPart();
		// } else if (model instanceof Navbar) {
		// part = new NavbarPart();
		// } else if (model instanceof TabItem) {
		// part = new TabItemPart();
		// } else if (model instanceof TabFolder) {
		// part = new TabFolderPart();
		// }
		// if (part == null) {
		// System.out.println();
		// }
		Class<? extends ComponentPart> cls = map.get(model.getClass());
		if (cls != null) {
			try {
				part = cls.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
		}
		if (part == null)
			return part;
		part.setParent(context);
		part.setModel(model);
		part.setEditorMode(editorMode);
		return part;
	}
}
