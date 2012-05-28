package aurora.ide.prototype.freemarker.model;

import java.util.List;
import java.util.Set;

import uncertain.composite.CompositeMap;

import aurora.ide.prototype.freemarker.FreeMarkerGenerator;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

public class PropertiesMethod implements TemplateMethodModel {

	private FreeMarkerGenerator fmg;

	public PropertiesMethod(FreeMarkerGenerator freeMarkerGenerator) {
		this.fmg = freeMarkerGenerator;
	}

	public Object exec(@SuppressWarnings("rawtypes") List args)
			throws TemplateModelException {

		if (args.isEmpty()) {
			return "";
		}

		CompositeMap map = fmg.getUid().getMap(args.get(0).toString());

		if (map != null) {
			return properties(map);
		}
		return "";
	}

	private Object properties(CompositeMap map) {
		String r = " ";
		@SuppressWarnings("rawtypes")
		Set keySet = map.keySet();
		for (Object object : keySet) {
			String string = map.getString(object, "");
			if ("".equals(string)) {
				continue;
			}
			r += object.toString();
			r += "=";
			r += "\"";
			r += string;
			r += "\"";
			r += " ";
		}

		return r;
	}

}
