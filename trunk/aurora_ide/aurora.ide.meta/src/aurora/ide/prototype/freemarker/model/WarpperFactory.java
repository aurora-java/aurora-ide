package aurora.ide.prototype.freemarker.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import aurora.ide.prototype.freemarker.FreeMarkerGenerator;

import uncertain.composite.CompositeMap;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class WarpperFactory {

	private static final String[] ArrayTypes = { "datasets", "fields",
			"mapping", "editors", "buttons", "tabs" };

	private static final DefaultObjectWrapper dow = new DefaultObjectWrapper();

	private FreeMarkerGenerator fmg;

	public WarpperFactory(FreeMarkerGenerator fmg) {
		this.fmg = fmg;
	}

	public TemplateModel createWrapper(CompositeMap cm)
			throws TemplateModelException {
		if (cm == null)
			return null;
		if ("init-procedure".equalsIgnoreCase(cm.getName())) {
			return new InitProcedureWrapper("initProcedure", cm, fmg);
		}
		if ("view".equalsIgnoreCase(cm.getName())) {
			return new ViewWrapper("view", cm, fmg);
		}
		if ("form".equalsIgnoreCase(cm.getName())) {
			return new BoxWrapper("form", cm, fmg);
		}
		if ("hBox".equalsIgnoreCase(cm.getName())) {
			return new BoxWrapper("hbox", cm, fmg);
		}
		if ("fieldSet".equalsIgnoreCase(cm.getName())) {
			return new BoxWrapper("fieldSet", cm, fmg);
		}
		if ("vBox".equalsIgnoreCase(cm.getName())) {
			return new BoxWrapper("vbox", cm, fmg);
		}
		if ("grid".equalsIgnoreCase(cm.getName())) {
			return new GridWrapper("grid", cm, fmg);
		}
		if ("tabpanel".equalsIgnoreCase(cm.getName())) {
			return new TabPanelWrapper("tabPanel", cm, fmg);
		}

		// arrays

		if (Arrays.asList(ArrayTypes).contains(cm.getName().toLowerCase())) {

			@SuppressWarnings("rawtypes")
			List childsNotNull = cm.getChildsNotNull();
			List<TemplateModelWrapper> models = new ArrayList<TemplateModelWrapper>();
			for (Object object : childsNotNull) {
				models.add(new TemplateModelWrapper(((CompositeMap) object)
						.getName(), (CompositeMap) object, fmg));
			}
			return dow.wrap(models);

		}

		return new TemplateModelWrapper("none", cm, fmg);

	}
}
