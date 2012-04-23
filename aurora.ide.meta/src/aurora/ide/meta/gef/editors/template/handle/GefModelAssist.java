package aurora.ide.meta.gef.editors.template.handle;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import uncertain.composite.CompositeMap;

import aurora.ide.helpers.ApplicationException;
import aurora.ide.meta.gef.Util;
import aurora.ide.meta.gef.designer.BMCompositeMap;
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

	public static CompositeMap getModel(IFile file) {
		try {
			CompositeMap model = CacheManager.getWholeBMCompositeMap(file);
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
		return new BMCompositeMap(model).getQueryFields();
	}

	public static List<CompositeMap> getFields(CompositeMap model) {
		if (model == null) {
			return new ArrayList<CompositeMap>();
		}
		return new BMCompositeMap(model).getFields();
	}

	public static CompositeMap getCompositeMap(CompositeMap parent, String name, String value) {
		if (parent == null) {
			return null;
		}
		for (Object obj : parent.getChildsNotNull()) {
			CompositeMap map = (CompositeMap) obj;
			if (map.getString(name) != null && (map).getString(name).equals(value)) {
				return map;
			}
		}
		return null;
	}
}
