package aurora.ide.meta.gef.editors.models;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import aurora.ide.meta.gef.editors.property.StringPropertyDescriptor;

public class ComboDatasetField extends DatasetField {
	/**
	 * 
	 */

	private static final long serialVersionUID = -4619018857153616914L;

	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
			new StringPropertyDescriptor(OPTIONS, "options"),
			new StringPropertyDescriptor(DISPLAY_FIELD, "displayField"),
			new StringPropertyDescriptor(VALUE_FIELD, "valueField"),
			new StringPropertyDescriptor(RETURN_FIELD, "returnField") };

	private String displayField = "";
	private String options = "";
	private String valueField = "";
	private String returnField = "";

	public ComboDatasetField() {
		this.setType("field");
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		// IPropertyDescriptor[] propertyDescriptors = super
		// .getPropertyDescriptors();
		// return this.mergePropertyDescriptor(propertyDescriptors, pds);
		return pds;
	}

	@Override
	public Object getPropertyValue(Object propName) {
		if (DISPLAY_FIELD.equals(propName)) {
			return this.getDisplayField();
		}
		if (OPTIONS.equals(propName)) {
			return this.getOptions();
		}
		if (VALUE_FIELD.equals(propName)) {
			return this.getValueField();
		}
		if (RETURN_FIELD.equals(propName)) {
			return this.getReturnField();
		}
		return super.getPropertyValue(propName);
	}

	@Override
	public void setPropertyValue(Object propName, Object val) {
		if (DISPLAY_FIELD.equals(propName)) {
			setDisplayField((String) val);
		} else if (OPTIONS.equals(propName)) {
			setOptions((String) val);
		} else if (VALUE_FIELD.equals(propName)) {
			setValueField((String) val);
		} else if (RETURN_FIELD.equals(propName)) {
			setReturnField((String) val);
		} else
			super.setPropertyValue(propName, val);
	}

	public String getDisplayField() {
		return displayField;
	}

	public void setDisplayField(String displayField) {
		this.displayField = displayField;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public String getValueField() {
		return valueField;
	}

	public void setValueField(String valueField) {
		this.valueField = valueField;
	}

	public String getReturnField() {
		return returnField;
	}

	public void setReturnField(String returnField) {
		this.returnField = returnField;
	}

}
