package aurora.ide.prototype.freemarker.model;

import java.util.List;
import java.util.Set;

import uncertain.composite.CompositeMap;

import aurora.ide.prototype.freemarker.FreeMarkerGenerator;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

public class ColumnMethod implements TemplateMethodModel {

	private FreeMarkerGenerator fmg;

	public ColumnMethod(FreeMarkerGenerator freeMarkerGenerator) {
		this.fmg = freeMarkerGenerator;
	}

	public Object exec(@SuppressWarnings("rawtypes") List args)
			throws TemplateModelException {

		if (args.isEmpty()) {
			return "";
		}

		CompositeMap map = fmg.getUid().getMap(args.get(0).toString());
		if (map != null) {
			return columns(map);
		}
		return "";
	}

	private Object columns(CompositeMap map) {
		String r = "";

		@SuppressWarnings("rawtypes")
		List childsNotNull = map.getChildsNotNull();
		for (Object object : childsNotNull) {
			if (object instanceof CompositeMap) {
				r += ((CompositeMap) object).toXML();
				r += "\n";
			}
		}
		return r;
	}

}
