package aurora.ide.prototype.freemarker.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import aurora.ide.prototype.freemarker.FreeMarkerGenerator;

import uncertain.composite.CompositeMap;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class InitProcedureWrapper extends TemplateModelWrapper {


	private FreeMarkerGenerator fmg;
	public InitProcedureWrapper(String name, CompositeMap cm, FreeMarkerGenerator fmg) {
		super(name, cm,fmg);
		this.fmg = fmg;
	}

	@Override
	public TemplateModel get(String key) throws TemplateModelException {
		if(isInnerKey(key)){
			return getInnerValue(key);
		}
		return super.get(key);
	}

	private static final String MODEL_QUERYS = "modelquerys";
	
	private static final String[] INNER_KEYS = { MODEL_QUERYS };
	private boolean isInnerKey(String key) {	return Arrays.asList(INNER_KEYS).contains(key.toLowerCase());}
	private TemplateModel getInnerValue(String key)
			throws TemplateModelException {
		if (MODEL_QUERYS.equalsIgnoreCase(key)) {
			@SuppressWarnings("rawtypes")
			List childsNotNull = this.getCompositeMap().getChildsNotNull();
			List<TemplateModel> models = new ArrayList<TemplateModel>();
			WarpperFactory wf = new WarpperFactory(fmg);
			for (Object object : childsNotNull) {
				CompositeMap map = (CompositeMap) object;
				if("model-query".equalsIgnoreCase(map.getName())){
					models.add(wf.createWrapper(map));
				}
			}
			return dow.wrap(models);
		}

		return dow.wrap("null");
	}
}
