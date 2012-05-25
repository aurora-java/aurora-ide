package aurora.ide.prototype.freemarker.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import aurora.ide.prototype.freemarker.FreeMarkerGenerator;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import uncertain.composite.CompositeMap;

public class ViewWrapper extends TemplateModelWrapper {

	private FreeMarkerGenerator fmg;

	public ViewWrapper(String name, CompositeMap cm, FreeMarkerGenerator fmg) {
		super(name, cm,fmg);
		this.fmg = fmg;
	}

	@Override
	public TemplateModel get(String key) throws TemplateModelException {
		if (isInnerKey(key)) {
			return getInnerValue(key);
		}
		return super.get(key);
	}

	private static final String Links = "links";

	private static final String[] INNER_KEYS = { Links };

	private boolean isInnerKey(String key) {
		return Arrays.asList(INNER_KEYS).contains(key.toLowerCase());
	}

	private TemplateModel getInnerValue(String key)
			throws TemplateModelException {
		if (Links.equalsIgnoreCase(key)) {
			@SuppressWarnings("rawtypes")
			List childsNotNull = this.getCompositeMap().getChildsNotNull();
			List<TemplateModel> models = new ArrayList<TemplateModel>();
			WarpperFactory wf = new WarpperFactory(fmg);
			for (Object object : childsNotNull) {
				CompositeMap map = (CompositeMap) object;
				if ("link".equalsIgnoreCase(map.getName())) {
					models.add(wf.createWrapper(map));
				}
			}
			return dow.wrap(models);
		}

		return dow.wrap("null");
	}
}
