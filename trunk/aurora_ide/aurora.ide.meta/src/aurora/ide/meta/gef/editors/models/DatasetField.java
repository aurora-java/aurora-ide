package aurora.ide.meta.gef.editors.models;

import aurora.ide.meta.gef.editors.property.BooleanPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.StringPropertyDescriptor;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class DatasetField extends AuroraComponent {
	/**
	 * 
	 */

	private static final long serialVersionUID = -4619018857153616914L;

	// 默认值
	public static final String DEFAULT_VALUE = "defaultValue";

	// checkbox选中的值
	public static final String CHECKED_VALUE = "checkedValue";
	// checkbox未选中的值
	public static final String UNCHECKED_VALUE = "uncheckedValue";

	// ComboBox的displayField
	public static final String DISPLAY_FIELD = "displayField";
	// ComboBox的options,对应DataSet的id
	public static final String OPTIONS = "options";
	// ComboBox的valueField
	public static final String VALUE_FIELD = "valueField";
	// ComboBox选中值的返回name
	public static final String RETURN_FIELD = "returnField";

	// Lov窗口中grid的高度
	public static final String LOV_GRID_HEIGHT = "lovGridHeight";
	// lov弹出窗口的高度
	public static final String LOV_HEIGHT = "lovHeight";
	// Lov对应的model
	public static final String LOV_SERVICE = "lovService";
	// 自定义URL
	public static final String LOV_URL = "lovUrl";
	// lov弹出窗口的宽度
	public static final String LOV_WIDTH = "lovWidth";
	// Lov弹出窗口的title
	public static final String TITLE = "title";

	// validator : String
	// 自定义校验函数
	// 函数参数为 function(record,name,value)
	// 返回值:
	// (1)校验成功返回true
	// (2)校验失败返回错误的描述信息(文本格式)

	private String defaultValue = "";

	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
			new StringPropertyDescriptor(DEFAULT_VALUE, "defaultValue"),
			new StringPropertyDescriptor(NAME, "name"),
			new StringPropertyDescriptor(PROMPT, "prompt"),
			new BooleanPropertyDescriptor(REQUIRED, "required"),
			new BooleanPropertyDescriptor(READONLY, "readonly") };

	private boolean required = false;
	private boolean readOnly = false;

	public DatasetField() {
		this.setType("field");
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {

		return pds;
	}

	@Override
	public Object getPropertyValue(Object propName) {
		if (NAME.equals(propName)) {
			return this.getName();
		}
		if (READONLY.equals(propName)) {
			return this.isReadOnly();
		}
		if (REQUIRED.equals(propName)) {
			return this.isRequired();
		}
		if (PROMPT.equals(propName)) {
			return this.getPrompt();
		}
		if (DEFAULT_VALUE.equals(propName)) {
			return this.getDefaultValue();
		}
		return null;
	}

	@Override
	public void setPropertyValue(Object propName, Object val) {
		if (NAME.equals(propName)) {
			this.setName((String) val);
		}
		if (READONLY.equals(propName)) {
			this.setReadOnly((Boolean) val);
		}
		if (REQUIRED.equals(propName)) {
			this.setRequired((Boolean) val);
		}
		if (PROMPT.equals(propName)) {
			this.setPrompt((String) val);
		}
		if (DEFAULT_VALUE.equals(propName)) {
			this.setDefaultValue((String) val);
		}
		// super.setPropertyValue(propName, val);
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

}
