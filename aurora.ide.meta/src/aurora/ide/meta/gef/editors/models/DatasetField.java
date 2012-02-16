package aurora.ide.meta.gef.editors.models;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import aurora.ide.meta.gef.editors.property.BooleanPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.StringPropertyDescriptor;

public class DatasetField extends AuroraComponent {
	/**
	 * 
	 */

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
