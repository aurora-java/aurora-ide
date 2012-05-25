package aurora.ide.prototype.freemarker.model;

import java.util.Arrays;

import aurora.ide.prototype.freemarker.FreeMarkerGenerator;

import uncertain.composite.CompositeMap;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class BoxWrapper extends TemplateModelWrapper {

	private FreeMarkerGenerator fmg;

	public BoxWrapper(String name, CompositeMap cm, FreeMarkerGenerator fmg) {
		super(name, cm,fmg);
		this.fmg = fmg;
	}

	private static final String isBox = "isbox";

	private static final String[] INNER_KEYS = { isBox };

	@Override
	public TemplateModel get(String key) throws TemplateModelException {
		if (isInnerKey(key)) {
			return getInnerValue(key);
		}
		return super.get(key);
	}

	private boolean isInnerKey(String key) {
		return Arrays.asList(INNER_KEYS).contains(key.toLowerCase());
	}

	private TemplateModel getInnerValue(String key)
			throws TemplateModelException {
		if (isBox.equalsIgnoreCase(key)) {
			return dow.wrap(true);
		}

		return dow.wrap("null");
	}
}
