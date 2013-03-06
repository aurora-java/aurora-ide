package aurora.ide.meta.gef.editors.models;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import uncertain.composite.CompositeMap;
import aurora.ide.AuroraPlugin;
import aurora.ide.meta.gef.editors.property.BooleanPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.StringPropertyDescriptor;
import aurora.ide.meta.gef.editors.source.gen.DataSetFieldUtil;

public class DatasetField extends AuroraComponent {

	private static final long serialVersionUID = -4619018857153616914L;

	/** defaultValue */
	public static final String DEFAULT_VALUE = "defaultValue";

	/** the checkedValue of CheckBox */
	public static final String CHECKED_VALUE = "checkedValue";
	/** the unchekedValue of CheckBox */
	public static final String UNCHECKED_VALUE = "uncheckedValue";

	/** the displayField of ComboBox */
	public static final String DISPLAY_FIELD = "displayField";
	/** the options of ComboBox , point to a dataset id */
	public static final String OPTIONS = "options";
	/** the valueField of ComboBox */
	public static final String VALUE_FIELD = "valueField";
	/** the returnField of ComboBox */
	public static final String RETURN_FIELD = "returnField";

	/** LOV grid height */
	public static final String LOV_GRID_HEIGHT = "lovGridHeight";
	/** the height of LOV popup window */
	public static final String LOV_HEIGHT = "lovHeight";
	/** the model of LOV popup window */
	public static final String LOV_SERVICE = "lovService";
	/** user defined LOV screen */
	public static final String LOV_URL = "lovUrl";
	/** the width of LOV popup window */
	public static final String LOV_WIDTH = "lovWidth";
	/** the title of LOV popup window */
	public static final String TITLE = "title";

	public static final String[] keys = { READONLY, REQUIRED, DEFAULT_VALUE,
			CHECKED_VALUE, UNCHECKED_VALUE, DISPLAY_FIELD, OPTIONS,
			VALUE_FIELD, RETURN_FIELD, LOV_SERVICE, TITLE };

	public static final String[] lov_keys = { READONLY, REQUIRED, LOV_SERVICE,
			TITLE, LOV_HEIGHT, LOV_GRID_HEIGHT, LOV_WIDTH };

	public static final IPropertyDescriptor PD_READONLY = new BooleanPropertyDescriptor(
			READONLY, "*" + READONLY);
	public static final IPropertyDescriptor PD_REQUIRED = new BooleanPropertyDescriptor(
			REQUIRED, "*" + REQUIRED);
	public static final IPropertyDescriptor PD_DEFAULT_VALUE = new StringPropertyDescriptor(
			DEFAULT_VALUE, "*" + DEFAULT_VALUE);

	private boolean required = false;
	private boolean readOnly = false;

	private String defaultValue = "";
	private String checkedValue = "Y";
	private String uncheckedValue = "N";

	private String displayField = "";
	private String options = "";
	private String valueField = "";
	private String returnField = "";

	private int lovGridHeight;
	private int lovHeight;
	private String lovService = "";
	private String lovUrl = "";
	private int lovWidth;
	private String title = "";

	private Dataset dataset;

	private DataSetFieldUtil dsfUtil = null;

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return NONE_PROPS;
	}

	@Override
	public Object getPropertyValue(Object propName) {
		if (NAME.equals(propName)) {
			return this.getName();
		} else if (READONLY.equals(propName)) {
			return this.isReadOnly();
		} else if (REQUIRED.equals(propName)) {
			return this.isRequired();
		} else if (DEFAULT_VALUE.equals(propName)) {
			return this.getDefaultValue();
		} else if (CHECKED_VALUE.equals(propName)) {
			return getCheckedValue();
		} else if (UNCHECKED_VALUE.equals(propName)) {
			return getUncheckedValue();
		} else if (DISPLAY_FIELD.equals(propName)) {
			return getDisplayField();
		} else if (OPTIONS.equals(propName)) {
			return getOptions();
		} else if (VALUE_FIELD.equals(propName)) {
			return getValueField();
		} else if (RETURN_FIELD.equals(propName)) {
			return getReturnField();
		} else if (LOV_GRID_HEIGHT.equals(propName)) {
			return getLovGridHeight();
		} else if (LOV_WIDTH.equals(propName)) {
			return getLovWidth();
		} else if (LOV_HEIGHT.equals(propName)) {
			return getLovHeight();
		} else if (LOV_SERVICE.equals(propName)) {
			return getLovService();
		} else if (LOV_URL.equals(propName)) {
			return getLovUrl();
		} else if (TITLE.equals(propName)) {
			return getTitle();
		}
		return null;
	}

	@Override
	public void setPropertyValue(Object propName, Object val) {
		if (NAME.equals(propName)) {
			setName(nns(val));
		} else if (READONLY.equals(propName)) {
			setReadOnly((Boolean) val);
		} else if (REQUIRED.equals(propName)) {
			setRequired((Boolean) val);
		} else if (DEFAULT_VALUE.equals(propName)) {
			setDefaultValue(nns(val));
		} else if (CHECKED_VALUE.equals(propName)) {
			setCheckedValue(nns(val));
		} else if (UNCHECKED_VALUE.equals(propName)) {
			setUncheckedValue(nns(val));
		} else if (DISPLAY_FIELD.equals(propName)) {
			setDisplayField(nns(val));
		} else if (OPTIONS.equals(propName)) {
			setOptions(nns(val));
		} else if (VALUE_FIELD.equals(propName)) {
			setValueField(nns(val));
		} else if (RETURN_FIELD.equals(propName)) {
			setReturnField(nns(val));
		} else if (LOV_GRID_HEIGHT.equals(propName)) {
			setLovGridHeight((Integer) val);
		} else if (LOV_WIDTH.equals(propName)) {
			this.setLovWidth((Integer) val);
		} else if (LOV_HEIGHT.equals(propName)) {
			setLovHeight((Integer) val);
		} else if (LOV_SERVICE.equals(propName)) {
			setLovService(nns(val));
		} else if (LOV_URL.equals(propName)) {
			setLovUrl(nns(val));
		} else if (TITLE.equals(propName)) {
			setTitle(nns(val));
		}
	}

	/**
	 * not null string
	 * 
	 * @param val
	 * @return if val is null returns ""<br/>
	 *         else returns val.toString()
	 */
	private String nns(Object val) {
		return val == null ? "" : val.toString();
	}

	public String getCheckedValue() {
		return checkedValue;
	}

	public void setCheckedValue(String checkedValue) {
		this.checkedValue = checkedValue;
	}

	public String getUncheckedValue() {
		return uncheckedValue;
	}

	public void setUncheckedValue(String uncheckedValue) {
		this.uncheckedValue = uncheckedValue;
	}

	public String getDisplayField() {
		DataSetFieldUtil dsfu = getNewDsfUtil();
		if (dsfu == null)
			return "";
		CompositeMap opMap = dsfu.getOptionsMap();
		if (opMap != null) {
			return opMap.getString("defaultDisplayField");
		}
		return "";
		// return displayField;
	}

	public void setDisplayField(String displayField) {
		this.displayField = displayField;
	}

	public String getOptions() {
		DataSetFieldUtil dsfu = getNewDsfUtil();
		if (dsfu != null)
			return nns(getNewDsfUtil().getOptions());
		return "";
		// return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public String getValueField() {
		// TODO pk
		DataSetFieldUtil dsfu = getNewDsfUtil();
		if (dsfu == null)
			return "";
		CompositeMap opMap = dsfu.getOptionsMap();
		if (opMap != null)
			return nns(dsfu.getPK(opMap));
		return "";
		// return valueField;
	}

	public void setValueField(String valueField) {
		this.valueField = valueField;
	}

	public String getReturnField() {
		return getName();
		// return returnField;
	}

	public void setReturnField(String returnField) {
		this.returnField = returnField;
	}

	public int getLovGridHeight() {
		return lovGridHeight;
	}

	public void setLovGridHeight(int lovGridHeight) {
		this.lovGridHeight = lovGridHeight;
	}

	public int getLovHeight() {
		return lovHeight;
	}

	public void setLovHeight(int lovHeight) {
		this.lovHeight = lovHeight;
	}

	public String getLovService() {
		DataSetFieldUtil dsfu = getNewDsfUtil();
		if (dsfu != null)
			return nns(dsfu.getOptions());
		return "";
		// return lovService;
	}

	public void setLovService(String lovService) {
		this.lovService = lovService;
	}

	public String getLovUrl() {
		return lovUrl;
	}

	public void setLovUrl(String lovUrl) {
		this.lovUrl = lovUrl;
	}

	public int getLovWidth() {
		return lovWidth;
	}

	public void setLovWidth(int lovWidth) {
		this.lovWidth = lovWidth;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public DatasetField() {
		this.setType("datasetfield");
		this.setLovGridHeight(350);
		this.setLovHeight(500);
		this.setLovWidth(500);
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setDataset(Dataset dataset) {
		dsfUtil = null;
		this.dataset = dataset;
	}

	public void setName(String name) {
		super.setName(name);
		dsfUtil = null;
	}

	private DataSetFieldUtil getNewDsfUtil() {
		if (dsfUtil == null) {
			IFile file = AuroraPlugin.getActiveIFile();
			if (file == null || dataset == null)
				return null;
			IProject proj = file.getProject();
			dsfUtil = new DataSetFieldUtil(proj, getName(), dataset.getModel());
		}
		return dsfUtil;
	}

}
