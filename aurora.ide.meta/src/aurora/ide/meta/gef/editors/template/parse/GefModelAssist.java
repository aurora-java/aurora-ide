package aurora.ide.meta.gef.editors.template.parse;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import uncertain.composite.CompositeMap;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.search.cache.CacheManager;

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
		String object = field.getString("defaultEditor");
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

	public static CompositeMap getModel(IFile file) {
		try {
			CompositeMap model = CacheManager.getCompositeMap(file);
			return model;
		} catch (CoreException e1) {
			e1.printStackTrace();
		} catch (ApplicationException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public static List<CompositeMap> getQueryFields(CompositeMap model) {
		if (model == null) {
			return new ArrayList<CompositeMap>();
		}
		CompositeMap qfs = model.getChild("query-fields");
		if (qfs != null) {
			List<CompositeMap> fields = new ArrayList<CompositeMap>();
			for (Object field : qfs.getChildsNotNull()) {
				fields.add((CompositeMap) field);
			}
			return fields;
		}
		return new ArrayList<CompositeMap>();
	}

	public static List<CompositeMap> getFields(CompositeMap model) {
		if (model == null) {
			return new ArrayList<CompositeMap>();
		}
		CompositeMap fs = model.getChild("fields");
		if (fs != null) {
			List<CompositeMap> fields = new ArrayList<CompositeMap>();
			for (Object field : fs.getChildsNotNull()) {
				fields.add((CompositeMap) field);
			}
			return fields;
		}
		return new ArrayList<CompositeMap>();
	}

	public static CompositeMap getCompositeMap(CompositeMap parent, String name, String value) {
		for (Object obj : parent.getChildsNotNull()) {
			CompositeMap map = (CompositeMap) obj;
			if (map.getString(name) != null && (map).getString(name).equals(value)) {
				return map;
			}
		}
		return null;
	}
}
