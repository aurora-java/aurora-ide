package aurora.ide.meta.gef.editors.models;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class CommonDatasetField extends DatasetField {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1477465773367078816L;
	private LovDatasetField lovDatasetField = new LovDatasetField();
	private ComboDatasetField comboDatasetField = new ComboDatasetField();
	private CheckboxDatasetField checkDatasetField = new CheckboxDatasetField();
	private IPropertyDescriptor[] lovPDS = lovDatasetField
			.getPropertyDescriptors();
	private IPropertyDescriptor[] comboPDS = comboDatasetField
			.getPropertyDescriptors();
	private IPropertyDescriptor[] checkBoxPDS = checkDatasetField
			.getPropertyDescriptors();

	public IPropertyDescriptor[] getPropertyDescriptors(String inputType) {

		if (Input.LOV.equals(inputType)) {
			return lovPDS;
		}
		if (Input.Combo.equals(inputType)) {
			return comboPDS;
		}
		if (CheckBox.CHECKBOX.equals(inputType)) {
			return checkBoxPDS;
		}
		return super.getPropertyDescriptors();
	}

	public Object getPropertyValue(String inputType, Object propName) {

		if (Input.LOV.equals(inputType)) {
			return lovDatasetField.getPropertyValue(propName);
		}
		if (Input.Combo.equals(inputType)) {
			return comboDatasetField.getPropertyValue(propName);
		}
		if (CheckBox.CHECKBOX.equals(inputType)) {
			return checkDatasetField.getPropertyValue(propName);
		}
		return super.getPropertyValue(propName);
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		IPropertyDescriptor[] mpd = this.mergePropertyDescriptor(
				lovDatasetField.getPropertyDescriptors(),
				super.getPropertyDescriptors());
		IPropertyDescriptor[] mpd2 = this.mergePropertyDescriptor(mpd,
				comboDatasetField.getPropertyDescriptors());
		IPropertyDescriptor[] mergePropertyDescriptor = this
				.mergePropertyDescriptor(mpd2,
						checkDatasetField.getPropertyDescriptors());
		return mergePropertyDescriptor;
	}

	public Object getPropertyValue(Object propName) {
		Object propertyValue = super.getPropertyValue(propName);
		if (propertyValue == null || "".equals(propertyValue))
			propertyValue = this.getPropertyValue(Input.LOV, propName);
		if (propertyValue == null || "".equals(propertyValue))
			propertyValue = this.getPropertyValue(Input.Combo, propName);
		if (propertyValue == null || "".equals(propertyValue))
			propertyValue = this.getPropertyValue(CheckBox.CHECKBOX, propName);
		return propertyValue;
	}

	public void setPropertyValue(Object propName, Object val) {
		this.setPropertyValue("", propName, val);
		this.setPropertyValue(Input.LOV, propName, val);
		this.setPropertyValue(Input.Combo, propName, val);
		this.setPropertyValue(CheckBox.CHECKBOX, propName, val);
	}

	public void setPropertyValue(String inputType, Object propName, Object val) {
		if (Input.LOV.equals(inputType)) {
			lovDatasetField.setPropertyValue(propName, val);
		} else if (Input.Combo.equals(inputType)) {
			comboDatasetField.setPropertyValue(propName, val);
		} else if (CheckBox.CHECKBOX.equals(inputType)) {
			checkDatasetField.setPropertyValue(propName, val);
		} else
			super.setPropertyValue(propName, val);
	}

	public LovDatasetField getLovDatasetField() {
		return lovDatasetField;
	}

	public ComboDatasetField getComboDatasetField() {
		return comboDatasetField;
	}

	public CheckboxDatasetField getCheckDatasetField() {
		return checkDatasetField;
	}

	public IPropertyDescriptor[] getLovPDS() {
		return lovPDS;
	}

	public IPropertyDescriptor[] getComboPDS() {
		return comboPDS;
	}

	public IPropertyDescriptor[] getCheckBoxPDS() {
		return checkBoxPDS;
	}

}
