package aurora.ide.meta.gef.editors.models;

import aurora.ide.meta.gef.editors.property.StringPropertyDescriptor;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class CheckboxDatasetField extends DatasetField {
	/**
	 * 
	 */
	// check box
	// checkedValue="Y" defaultValue="Y"
	// lov
	// mapping = lov service:=
	private static final long serialVersionUID = -4619018857153616914L;

	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
			new StringPropertyDescriptor(UNCHECKED_VALUE, "uncheckedValue"),
			new StringPropertyDescriptor(CHECKED_VALUE, "checkedValue"),
			new StringPropertyDescriptor(DEFAULT_VALUE, "defaultValue") };

	private String checkedValue = "Y";
	private String uncheckedValue = "N";

	public CheckboxDatasetField() {
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
		if (UNCHECKED_VALUE.equals(propName)) {
			return this.getUncheckedValue();
		} else if (CHECKED_VALUE.equals(propName)) {
			return this.getCheckedValue();
		}
		return super.getPropertyValue(propName);
	}

	@Override
	public void setPropertyValue(Object propName, Object val) {
		if (UNCHECKED_VALUE.equals(propName)) {
			setUncheckedValue((String) val);
		} else if (CHECKED_VALUE.equals(propName)) {
			setCheckedValue((String) val);
		}
		super.setPropertyValue(propName, val);
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

}
