package aurora.ide.meta.gef.editors.template.handle;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.Util;
import aurora.ide.meta.gef.editors.models.Input;

public class GefModelAssist {

	public static String getType(CompositeMap field) {
		String object = field.getString("defaultEditor");
		if (supportEditor(object) != null) {
			return object;
		} else {
			return null;
		}
	}

	public static String getTypeNotNull(CompositeMap field) {
		String object = Util.getCompositeValue("defaultEditor", field);

		if (supportEditor(object) != null) {
			return object;
		} else {
			if ("java.lang.Long".equals(field.getString("datatype"))) {
				return Input.NUMBER;
			}
			if ("java.lang.String".equals(field.getString("datatype"))) {
				return Input.TEXT;
			}
			if ("java.util.Date".equals(field.getString("datatype"))) {
				return Input.CAL;
			}
		}
		return Input.TEXT;
	}

	private static String supportEditor(String object) {
		for (String t : Input.INPUT_TYPES) {
			if (t.equalsIgnoreCase(object))
				return t;
		}
		return null;
	}
}
