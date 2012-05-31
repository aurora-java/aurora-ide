package aurora.ide.prototype.freemarker.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import uncertain.composite.CompositeMap;
import uncertain.schema.Element;
import aurora.ide.helpers.LoadSchemaManager;
import aurora.ide.meta.gef.Util;
import aurora.ide.prototype.freemarker.FreeMarkerGenerator;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.SimpleObjectWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class TemplateModelWrapper implements TemplateHashModel {

	private static final String INIT_PROCEDURE = "initprocedure";

	private static final String IS_BOX = "isbox";

	private static final String IS_LAYOUT = "islayout";

	private static final String HAS_CHILD = "haschild";

	private static final String U_ID = "u_id";

	private static final String NAME = "name";

	private static final String RAWNAME = "rawname";

	private static final String COMPONENTS = "components";

	private static final String CDATA = "cdata";

	protected SimpleObjectWrapper sow = new SimpleObjectWrapper();

	protected DefaultObjectWrapper dow = new DefaultObjectWrapper();

	private CompositeMap cm;
	private String name;

	private FreeMarkerGenerator fmg;

	private static final String[] INNER_KEYS = { U_ID, CDATA, COMPONENTS,
			RAWNAME, NAME, IS_LAYOUT, IS_BOX, HAS_CHILD, INIT_PROCEDURE };

	public TemplateModelWrapper(String name, CompositeMap cm,
			FreeMarkerGenerator freeMarkerGenerator) {
		super();
		this.cm = cm;
		this.name = name;
		this.fmg = freeMarkerGenerator;
	}

	public Set<?> keys() {
		return cm.keySet();
	}

	public Collection<?> values() {
		return cm.values();
	}

	public TemplateModel get(String key) throws TemplateModelException {
		if (isInnerKey(key)) {
			return getInnerValue(key);
		}

		String compositeValue = Util.getCompositeValue(key, cm);
		if (compositeValue != null) {
			return dow.wrap(compositeValue);
		}

		@SuppressWarnings("rawtypes")
		List childsNotNull = cm.getChildsNotNull();
		for (Object object : childsNotNull) {
			if (object instanceof CompositeMap) {
				if (key.equalsIgnoreCase(((CompositeMap) object).getName())) {
					WarpperFactory wf = new WarpperFactory(fmg);
					return wf.createWrapper((CompositeMap) object);
				}
			}
		}

		return null;
	}

	private TemplateModel getInnerValue(String key)
			throws TemplateModelException {
		if (CDATA.equalsIgnoreCase(key)) {
			String text = cm.getText();
			return dow.wrap(text == null ? "" : text);
		}
		if (COMPONENTS.equalsIgnoreCase(key)) {
			@SuppressWarnings("rawtypes")
			List childsNotNull = cm.getChildsNotNull();
			List<TemplateModel> models = new ArrayList<TemplateModel>();
			WarpperFactory wf = new WarpperFactory(fmg);
			for (Object object : childsNotNull) {
				models.add(wf.createWrapper((CompositeMap) object));
			}
			return dow.wrap(models);
		}
		if (RAWNAME.equalsIgnoreCase(key)) {
			String text = cm.getRawName();
			return dow.wrap(text == null ? "" : text);
		}
		if (NAME.equalsIgnoreCase(key)) {
			String text = cm.getName();
			return dow.wrap(text == null ? "" : text);
		}
		if (U_ID.equalsIgnoreCase(key)) {
			return dow.wrap(fmg.getUid().getID(cm));
		}
		if (IS_LAYOUT.equalsIgnoreCase(key)) {
			List childs = cm.getChildsNotNull();
			return dow.wrap(childs.size() > 0);
		}
		if (HAS_CHILD.equalsIgnoreCase(key)) {
			List childs = cm.getChildsNotNull();
			return dow.wrap(childs.size() > 0);
		}
		if (IS_BOX.equalsIgnoreCase(key)) {
			return dow.wrap(false);
		}
		if (INIT_PROCEDURE.equalsIgnoreCase(key)) {
			CompositeMap child = cm.getChild("init-procedure");
			if (child != null) {
				WarpperFactory wf = new WarpperFactory(fmg);
				return wf.createWrapper(child);
			}else{
				return null;
			}
			
		}

		return dow.wrap("null");
	}

	private boolean isInnerKey(String key) {
		return Arrays.asList(INNER_KEYS).contains(key.toLowerCase());
	}

	public int getMax(CompositeMap map) {
		Element element = null;
		try {
			element = LoadSchemaManager.getSchemaManager().getElement(map);
			return Integer.valueOf(element.getMaxOccurs());
		} catch (Exception e) {
		}
		return 1;
	}

	public boolean isEmpty() throws TemplateModelException {
		return false;
	}

	public CompositeMap getCompositeMap() {
		return cm;
	}

	public void setCompositeMap(CompositeMap cm) {
		this.cm = cm;
	}

	public String getName() {
		return name;
	}
}
