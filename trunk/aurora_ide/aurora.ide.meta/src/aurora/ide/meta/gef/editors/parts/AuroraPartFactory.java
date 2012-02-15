package aurora.ide.meta.gef.editors.parts;

import java.util.HashMap;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Button;
import aurora.ide.meta.gef.editors.models.CheckBox;
import aurora.ide.meta.gef.editors.models.FieldSet;
import aurora.ide.meta.gef.editors.models.Form;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.GridSelectionCol;
import aurora.ide.meta.gef.editors.models.HBox;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.Navbar;
import aurora.ide.meta.gef.editors.models.Radio;
import aurora.ide.meta.gef.editors.models.TabBody;
import aurora.ide.meta.gef.editors.models.TabFolder;
import aurora.ide.meta.gef.editors.models.TabItem;
import aurora.ide.meta.gef.editors.models.Toolbar;
import aurora.ide.meta.gef.editors.models.VBox;
import aurora.ide.meta.gef.editors.models.ViewDiagram;

/**
 */
public class AuroraPartFactory implements EditPartFactory {

	private static HashMap<Class<? extends AuroraComponent>, Class<? extends ComponentPart>> map = new HashMap<Class<? extends AuroraComponent>, Class<? extends ComponentPart>>(
			20);
	static {
		map.put(ViewDiagram.class, ViewDiagramPart.class);
		map.put(Form.class, BoxPart.class);
		map.put(FieldSet.class, BoxPart.class);
		map.put(HBox.class, BoxPart.class);
		map.put(VBox.class, BoxPart.class);
		map.put(CheckBox.class, CheckBoxPart.class);
		map.put(Radio.class, RadioPart.class);
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
	}

	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part = null;
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
			System.out.println(model.getClass());
		}
		if (part == null)
			return part;
		part.setParent(context);
		part.setModel(model);
		return part;
	}
}
