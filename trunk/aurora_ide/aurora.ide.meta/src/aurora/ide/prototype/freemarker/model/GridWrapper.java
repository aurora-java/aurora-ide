package aurora.ide.prototype.freemarker.model;

import java.util.Arrays;

import aurora.ide.prototype.freemarker.FreeMarkerGenerator;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import uncertain.composite.CompositeMap;

public class GridWrapper extends TemplateModelWrapper {

	private FreeMarkerGenerator fmg;

	public GridWrapper(String name, CompositeMap cm, FreeMarkerGenerator fmg) {
		super(name, cm,fmg);
		this.fmg = fmg;
	}

	private static final String isGrid = "isgrid";

	private static final String[] INNER_KEYS = { isGrid };

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
		if (isGrid.equalsIgnoreCase(key)) {
			return dow.wrap(true);
		}

		return dow.wrap("null");
	}
}
