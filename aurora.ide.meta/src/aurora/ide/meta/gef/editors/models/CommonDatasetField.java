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

	public IPropertyDescriptor[] getPropertyDescriptors(String inputType) {

		if (Input.LOV.equals(inputType)) {
			return lovDatasetField.getPropertyDescriptors();
		}
		if (Input.Combo.equals(inputType)) {
			return comboDatasetField.getPropertyDescriptors();
		}
		if (CheckBox.CHECKBOX.equals(inputType)) {
			return checkDatasetField.getPropertyDescriptors();
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

	/**
	 * @deprecated
	 * */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return null;
	}

	/**
	 * @deprecated
	 * */
	public Object getPropertyValue(Object propName) {
		return null;
	}

	/**
	 * @deprecated
	 * */
	public void setPropertyValue(Object propName, Object val) {
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

}
