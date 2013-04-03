package aurora.plugin.source.gen.screen.model;


import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;
import aurora.plugin.source.gen.screen.model.properties.IPropertyDescriptor;

public class DatasetField extends AuroraComponent {


//	/** defaultValue */
//	public static final String DEFAULT_VALUE = "defaultValue";
//
//	/** the checkedValue of CheckBox */
//	public static final String CHECKED_VALUE = "checkedValue";
//	/** the unchekedValue of CheckBox */
//	public static final String UNCHECKED_VALUE = "uncheckedValue";
//
//	/** the displayField of ComboBox */
//	public static final String DISPLAY_FIELD = "displayField";
//	/** the options of ComboBox , point to a dataset id */
//	public static final String OPTIONS = "options";
//	/** the valueField of ComboBox */
//	public static final String VALUE_FIELD = "valueField";
//	/** the returnField of ComboBox */
//	public static final String RETURN_FIELD = "returnField";
//
//	/** LOV grid height */
//	public static final String LOV_GRID_HEIGHT = "lovGridHeight";
//	/** the height of LOV popup window */
//	public static final String LOV_HEIGHT = "lovHeight";
//	/** the model of LOV popup window */
//	public static final String LOV_SERVICE = "lovService";
//	/** user defined LOV screen */
//	public static final String LOV_URL = "lovUrl";
//	/** the width of LOV popup window */
//	public static final String LOV_WIDTH = "lovWidth";
//	/** the title of LOV popup window */
//	public static final String TITLE = "title";
//
//	public static final String[] keys = { READONLY, REQUIRED, DEFAULT_VALUE,
//			CHECKED_VALUE, UNCHECKED_VALUE, DISPLAY_FIELD, OPTIONS,
//			VALUE_FIELD, RETURN_FIELD, LOV_SERVICE, TITLE };
//
//	public static final String[] lov_keys = { READONLY, REQUIRED, LOV_SERVICE,
//			TITLE, LOV_HEIGHT, LOV_GRID_HEIGHT, LOV_WIDTH };

//	public static final IPropertyDescriptor PD_READONLY = new BooleanPropertyDescriptor(
//			READONLY, "*" + READONLY);
//	public static final IPropertyDescriptor PD_REQUIRED = new BooleanPropertyDescriptor(
//			REQUIRED, "*" + REQUIRED);
//	public static final IPropertyDescriptor PD_DEFAULT_VALUE = new StringPropertyDescriptor(
//			DEFAULT_VALUE, "*" + DEFAULT_VALUE);

//	private boolean required = false;
//	private boolean readOnly = false;
//
//	private String defaultValue = "";
//	private String checkedValue = "Y";
//	private String uncheckedValue = "N";
//
//	private String displayField = "";
//	private String options = "";
//	private String valueField = "";
//	private String returnField = "";
//
//	private int lovGridHeight;
//	private int lovHeight;
//	private String lovService = "";
//	private String lovUrl = "";
//	private int lovWidth;
//	private String title = "";

//	private Dataset dataset;
	



//	public Object getPropertyValue(Object propName) {
//		if (NAME.equals(propName)) {
//			return this.getName();
//		} else if (READONLY.equals(propName)) {
//			return this.isReadOnly();
//		} else if (REQUIRED.equals(propName)) {
//			return this.isRequired();
//		} else if (DEFAULT_VALUE.equals(propName)) {
//			return this.getDefaultValue();
//		} else if (CHECKED_VALUE.equals(propName)) {
//			return getCheckedValue();
//		} else if (UNCHECKED_VALUE.equals(propName)) {
//			return getUncheckedValue();
//		} else if (DISPLAY_FIELD.equals(propName)) {
//			return getDisplayField();
//		} else if (OPTIONS.equals(propName)) {
//			return getOptions();
//		} else if (VALUE_FIELD.equals(propName)) {
//			return getValueField();
//		} else if (RETURN_FIELD.equals(propName)) {
//			return getReturnField();
//		} else if (LOV_GRID_HEIGHT.equals(propName)) {
//			return getLovGridHeight();
//		} else if (LOV_WIDTH.equals(propName)) {
//			return getLovWidth();
//		} else if (LOV_HEIGHT.equals(propName)) {
//			return getLovHeight();
//		} else if (LOV_SERVICE.equals(propName)) {
//			return getLovService();
//		} else if (LOV_URL.equals(propName)) {
//			return getLovUrl();
//		} else if (TITLE.equals(propName)) {
//			return getTitle();
//		}
//		return null;
//	}

//	public void setPropertyValue(Object propName, Object val) {
//		if (NAME.equals(propName)) {
//			setName(nns(val));
//		} else if (READONLY.equals(propName)) {
//			setReadOnly((Boolean) val);
//		} else if (REQUIRED.equals(propName)) {
//			setRequired((Boolean) val);
//		} else if (DEFAULT_VALUE.equals(propName)) {
//			setDefaultValue(nns(val));
//		} else if (CHECKED_VALUE.equals(propName)) {
//			setCheckedValue(nns(val));
//		} else if (UNCHECKED_VALUE.equals(propName)) {
//			setUncheckedValue(nns(val));
//		} else if (DISPLAY_FIELD.equals(propName)) {
//			setDisplayField(nns(val));
//		} else if (OPTIONS.equals(propName)) {
//			setOptions(nns(val));
//		} else if (VALUE_FIELD.equals(propName)) {
//			setValueField(nns(val));
//		} else if (RETURN_FIELD.equals(propName)) {
//			setReturnField(nns(val));
//		} else if (LOV_GRID_HEIGHT.equals(propName)) {
//			setLovGridHeight((Integer) val);
//		} else if (LOV_WIDTH.equals(propName)) {
//			this.setLovWidth((Integer) val);
//		} else if (LOV_HEIGHT.equals(propName)) {
//			setLovHeight((Integer) val);
//		} else if (LOV_SERVICE.equals(propName)) {
//			setLovService(nns(val));
//		} else if (LOV_URL.equals(propName)) {
//			setLovUrl(nns(val));
//		} else if (TITLE.equals(propName)) {
//			setTitle(nns(val));
//		}
//	}

//	/**
//	 * not null string
//	 * 
//	 * @param val
//	 * @return if val is null returns ""<br/>
//	 *         else returns val.toString()
//	 */
//	private String nns(Object val) {
//		return val == null ? "" : val.toString();
//	}

	public String getCheckedValue() {
//		return checkedValue;
		return this.getStringPropertyValue(ComponentProperties.checkedValue);
	}

	public void setCheckedValue(String checkedValue) {
//		this.checkedValue = checkedValue;
		this.setPropertyValue(ComponentProperties.checkedValue, checkedValue);
	}

	public String getUncheckedValue() {
//		return uncheckedValue;
		return this.getStringPropertyValue(ComponentProperties.uncheckedValue);
	}

	public void setUncheckedValue(String uncheckedValue) {
//		this.uncheckedValue = uncheckedValue;
		this.setPropertyValue(ComponentProperties.uncheckedValue, uncheckedValue);
	}

	public String getDisplayField() {
//		DataSetFieldUtil dsfu = getNewDsfUtil();
//		if (dsfu == null)
//			return "";
//		CompositeMap opMap = dsfu.getOptionsMap();
//		if (opMap != null) {
//			return opMap.getString("defaultDisplayField");
//		}
//		return "";
		return this.getStringPropertyValue(ComponentProperties.displayField);
		// return displayField;
	}

	public void setDisplayField(String displayField) {
//		this.displayField = displayField;
		this.setPropertyValue(ComponentProperties.displayField, displayField);
	}

	public String getOptions() {
//		DataSetFieldUtil dsfu = getNewDsfUtil();
//		if (dsfu != null)
//			return nns(getNewDsfUtil().getOptions());
//		return "";
		return this.getStringPropertyValue(ComponentProperties.options);
		
		// return options;
	}

	public void setOptions(String options) {
//		this.options = options;
		this.setPropertyValue(ComponentProperties.options, options);
	}

	public String getValueField() {
		// TODO pk
//		DataSetFieldUtil dsfu = getNewDsfUtil();
//		if (dsfu == null)
//			return "";
//		CompositeMap opMap = dsfu.getOptionsMap();
//		if (opMap != null)
//			return nns(dsfu.getPK(opMap));
//		return "";
		// return valueField;
		return this.getStringPropertyValue(ComponentProperties.valueField);
	}

	public void setValueField(String valueField) {
//		this.valueField = valueField;
		this.setPropertyValue(ComponentProperties.valueField, valueField);
	}

	public String getReturnField() {
//		return getName();
		// return returnField;
		return this.getStringPropertyValue(ComponentProperties.returnField);
	}

	public void setReturnField(String returnField) {
//		this.returnField = returnField;
		this.setPropertyValue(ComponentProperties.returnField, returnField);
	}

	public int getLovGridHeight() {
//		return lovGridHeight;
		return this.getIntegerPropertyValue(ComponentProperties.lovGridHeight);
	}

	public void setLovGridHeight(int lovGridHeight) {
//		this.lovGridHeight = lovGridHeight;
		this.setPropertyValue(ComponentProperties.lovGridHeight, lovGridHeight);
	}

	public int getLovHeight() {
//		return lovHeight;
		return this.getIntegerPropertyValue(ComponentProperties.lovHeight);
	}

	public void setLovHeight(int lovHeight) {
//		this.lovHeight = lovHeight;
		this.setPropertyValue(ComponentProperties.lovHeight, lovHeight);
	}

	public String getLovService() {
//		DataSetFieldUtil dsfu = getNewDsfUtil();
//		if (dsfu != null)
//			return nns(dsfu.getOptions());
//		return "";
		// return lovService;
		return this.getStringPropertyValue(ComponentProperties.lovService);
	}

	public void setLovService(String lovService) {
//		this.lovService = lovService;
		this.setPropertyValue(ComponentProperties.lovService, lovService);
	}

	public String getLovUrl() {
//		return lovUrl;
		return this.getStringPropertyValue(ComponentProperties.lovUrl);
	}

	public void setLovUrl(String lovUrl) {
//		this.lovUrl = lovUrl;
		this.setPropertyValue(ComponentProperties.lovUrl, lovUrl);
	}

	public int getLovWidth() {
//		return lovWidth;
		return this.getIntegerPropertyValue(ComponentProperties.lovWidth);
	}

	public void setLovWidth(int lovWidth) {
//		this.lovWidth = lovWidth;
		this.setPropertyValue(ComponentProperties.lovWidth, lovWidth);
	}

	public String getTitle() {
//		return title;
		return this.getStringPropertyValue(ComponentProperties.title);
	}

	public void setTitle(String title) {
//		this.title = title;
		this.setPropertyValue(ComponentProperties.title, title);
	}

	public DatasetField() {
		this.setComponentType("datasetfield");
		this.setLovGridHeight(350);
		this.setLovHeight(500);
		this.setLovWidth(500);
	}

	public boolean isRequired() {
//		return required;
		return this.getBooleanPropertyValue(ComponentProperties.required);
	}

	public void setRequired(boolean required) {
//		this.required = required;
		this.setPropertyValue(ComponentProperties.required, required);
	}

	public boolean isReadOnly() {
//		return readOnly;
		return this.getBooleanPropertyValue(ComponentProperties.readOnly);
	}

	public void setReadOnly(boolean readOnly) {
//		this.readOnly = readOnly;
		this.setPropertyValue(ComponentProperties.readOnly, readOnly);
	}

	public String getDefaultValue() {
//		return defaultValue;
		return this.getStringPropertyValue(ComponentProperties.defaultValue);
	}

	public void setDefaultValue(String defaultValue) {
//		this.defaultValue = defaultValue;
		this.setPropertyValue(ComponentProperties.defaultValue, defaultValue);
	}

//	public void setDataset(Dataset dataset) {
//		this.dataset = dataset;
//	}

//	public void setName(String name) {
//		super.setName(name);
//	}

//	private DataSetFieldUtil getNewDsfUtil() {
//		if (dsfUtil == null) {
//			IFile file = AuroraPlugin.getActiveIFile();
//			if (file == null || dataset == null)
//				return null;
//			IProject proj = file.getProject();
//			dsfUtil = new DataSetFieldUtil(proj, getName(), dataset.getModel());
//		}
//		return dsfUtil;
//	}

}
