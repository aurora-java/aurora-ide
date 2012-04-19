package aurora.ide.meta.gef.editors.template.handle;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.meta.gef.Util;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.search.cache.CacheManager;

public class GefModelAssist {

	public static String getType(CommentCompositeMap field) {
		String object = field.getString("defaultEditor");
		if (supportEditor(object) != null) {
			return object;
		} else {
			return null;
		}
	}

	public static String getTypeNotNull(CommentCompositeMap field) {
		//String object = field.getString("defaultEditor");
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

	public static CommentCompositeMap getModel(IFile file) {
		try {
			CommentCompositeMap model = (CommentCompositeMap) CacheManager.getWholeBMCompositeMap(file);
			return model;
		} catch (CoreException e1) {
			e1.printStackTrace();
		} catch (ApplicationException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public static List<CommentCompositeMap> getQueryFields(CommentCompositeMap model) {
		if (model == null) {
			return new ArrayList<CommentCompositeMap>();
		}
		CommentCompositeMap qfs = (CommentCompositeMap) model.getChild("query-fields");
		if (qfs != null) {
			List<CommentCompositeMap> fields = new ArrayList<CommentCompositeMap>();
			for (Object field : qfs.getChildsNotNull()) {
				fields.add((CommentCompositeMap) field);
			}
			return fields;
		}
		return new ArrayList<CommentCompositeMap>();
	}

	public static List<CommentCompositeMap> getFields(CommentCompositeMap model) {
		if (model == null) {
			return new ArrayList<CommentCompositeMap>();
		}
		CommentCompositeMap fs = (CommentCompositeMap) model.getChild("fields");
		if (fs != null) {
			List<CommentCompositeMap> fields = new ArrayList<CommentCompositeMap>();
			for (Object field : fs.getChildsNotNull()) {
				fields.add((CommentCompositeMap) field);
			}
			return fields;
		}
		return new ArrayList<CommentCompositeMap>();
	}

	public static CommentCompositeMap getCompositeMap(CommentCompositeMap parent, String name, String value) {
		if (parent == null) {
			return null;
		}
		for (Object obj : parent.getChildsNotNull()) {
			CommentCompositeMap map = (CommentCompositeMap) obj;
			if (map.getString(name) != null && (map).getString(name).equals(value)) {
				return map;
			}
		}
		return null;
	}
}
