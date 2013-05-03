package aurora.ide.meta.gef.editors.template.handle;

import java.util.List;

import aurora.ide.meta.extensions.ComponentFactory;
import aurora.ide.meta.extensions.ExtensionComponent;
import aurora.ide.meta.extensions.ExtensionLoader;
import aurora.plugin.source.gen.screen.model.AuroraComponent;

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
	}
}
