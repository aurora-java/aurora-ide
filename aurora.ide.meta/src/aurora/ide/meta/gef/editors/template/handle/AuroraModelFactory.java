package aurora.ide.meta.gef.editors.template.handle;

import java.util.List;

import aurora.ide.meta.extensions.ComponentFactory;
import aurora.ide.meta.extensions.ExtensionComponent;
import aurora.ide.meta.extensions.ExtensionLoader;
import aurora.ide.meta.gef.editors.models.AuroraComponent;

public class AuroraModelFactory {
	private static String[] types = { "toolbar", "form", "fieldset", "vbox",
			"hbox", "textfield", "numberfield", "lov", "combobox",
			"datepicker", "datetimepicker", "checkbox", "grid", "tabpanel",
			"tab", "button", "tabref", "label" };

	public static boolean isComponent(String type) {
		if (type == null) {
			return false;
		}
		type = type.toLowerCase();
		// for (String s : types) {
		// if (s.equals(type)) {
		// return true;
		// }
		// }
		// return false;
		List<ExtensionComponent> extensionComponents = ExtensionLoader
				.getExtensionComponents();
		for (ExtensionComponent ec : extensionComponents) {
			if (ec.getTypes().contains(type)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public static <T extends AuroraComponent> T createComponent(String type) {

		return (T) ComponentFactory.createComponent(type);

		//
		// if (type == null) {
		// return null;
		// }
		// type = type.toLowerCase();
		// if ("toolbar".equals(type)) {
		// return (T) new Toolbar();
		// } else if ("form".equals(type)) {
		// return (T) new Form();
		// } else if ("fieldset".equals(type)) {
		// return (T) new FieldSet();
		// } else if ("vbox".equals(type)) {
		// return (T) new VBox();
		// } else if ("hbox".equals(type)) {
		// return (T) new HBox();
		// } else if ("textfield".equals(type)) {
		// Input input = new Input();
		// input.setType(Input.TEXT);
		// return (T) input;
		// } else if ("numberfield".equals(type)) {
		// Input input = new Input();
		// input.setType(Input.NUMBER);
		// return (T) input;
		// } else if ("lov".equals(type)) {
		// Input input = new Input();
		// input.setType(Input.LOV);
		// return (T) input;
		// } else if ("combobox".equals(type)) {
		// Input input = new Input();
		// input.setType(Input.Combo);
		// return (T) input;
		// } else if ("datepicker".equals(type)) {
		// Input input = new Input();
		// input.setType(Input.DATE_PICKER);
		// return (T) input;
		// } else if ("datetimepicker".equals(type)) {
		// Input input = new Input();
		// input.setType(Input.DATETIMEPICKER);
		// return (T) input;
		// } else if ("checkbox".equals(type)) {
		// return (T) new CheckBox();
		// } else if ("grid".equals(type)) {
		// return (T) new Grid();
		// } else if ("tabpanel".equals(type)) {
		// return (T) new TabFolder();
		// } else if ("tab".equals(type)) {
		// return (T) new TabItem();
		// } else if ("button".equals(type)) {
		// return (T) new Button();
		// } else if ("label".equals(type)) {
		// return (T) new Label();
		// }
		// return null;
	}

}
