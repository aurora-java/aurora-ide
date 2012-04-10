package aurora.ide.meta.gef.editors.template.parse;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Button;
import aurora.ide.meta.gef.editors.models.CheckBox;
import aurora.ide.meta.gef.editors.models.FieldSet;
import aurora.ide.meta.gef.editors.models.Form;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.HBox;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.TabFolder;
import aurora.ide.meta.gef.editors.models.TabItem;
import aurora.ide.meta.gef.editors.models.Toolbar;
import aurora.ide.meta.gef.editors.models.VBox;
import aurora.ide.meta.gef.editors.models.link.TabRef;

public class AuroraModelFactory {
	private static String[] types = { "toolbar", "form", "fieldset", "vbox", "hbox", "textfield", "numberfield", "lov", "combobox", "datepicker", "datetimepicker", "checkbox", "grid", "tabfolder",
			"tabitem", "button", "tabref" };

	public static boolean isComponent(String type) {
		if (type == null) {
			return false;
		}
		type = type.toLowerCase();
		for (String s : types) {
			if (s.equals(type)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public static <T extends AuroraComponent> T createComponent(String type) {
		if (type == null) {
			return null;
		}
		type = type.toLowerCase();
		if ("toolbar".equals(type)) {
			return (T) new Toolbar();
		} else if ("form".equals(type)) {
			return (T) new Form();
		} else if ("fieldset".equals(type)) {
			return (T) new FieldSet();
		} else if ("vbox".equals(type)) {
			return (T) new VBox();
		} else if ("hbox".equals(type)) {
			return (T) new HBox();
		} else if ("textfield".equals(type)) {
			Input input = new Input();
			input.setType(Input.TEXT);
			return (T) input;
		} else if ("numberfield".equals(type)) {
			Input input = new Input();
			input.setType(Input.NUMBER);
			return (T) input;
		} else if ("lov".equals(type)) {
			Input input = new Input();
			input.setType(Input.LOV);
			return (T) input;
		} else if ("combobox".equals(type)) {
			Input input = new Input();
			input.setType(Input.Combo);
			return (T) input;
		} else if ("datepicker".equals(type)) {
			Input input = new Input();
			input.setType(Input.CAL);
			return (T) input;
		} else if ("datetimepicker".equals(type)) {
			Input input = new Input();
			input.setType(Input.DATETIMEPICKER);
			return (T) input;
		} else if ("checkbox".equals(type)) {
			return (T) new CheckBox();
		} else if ("grid".equals(type)) {
			return (T) new Grid();
		} else if ("tabfolder".equals(type)) {
			return (T) new TabFolder();
		} else if ("tabitem".equals(type)) {
			return (T) new TabItem();
		} else if ("button".equals(type)) {
			return (T) new Button();
		} 
		return null;
	}

}
