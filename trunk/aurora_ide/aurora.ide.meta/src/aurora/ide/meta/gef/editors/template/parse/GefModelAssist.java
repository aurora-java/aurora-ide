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
		if (field == null) {
			return Input.TEXT;
		}
		if ("java.lang.Long".equals(field.getString("datatype"))) {
			return Input.NUMBER;
		}
		if ("java.lang.String".equals(field.getString("datatype"))) {
			return Input.TEXT;
		}
		if ("java.util.Date".equals(field.getString("datatype"))) {
			return Input.CAL;
		}
		return Input.TEXT;
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
}
