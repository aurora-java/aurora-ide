package aurora.plugin.source.gen.screen.model;

import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;
import aurora.plugin.source.gen.screen.model.properties.IPropertyDescriptor;


public class CheckBox extends Input {

	public static final String TEXT = "text";
//	private String text = "text";
	public static final String CHECKBOX = "checkBox";

//	public static final IPropertyDescriptor PD_TEXT = new StringPropertyDescriptor(
//			TEXT, TEXT);
//	public static final IPropertyDescriptor PD_CHECKED_VALUE = new StringPropertyDescriptor(
//			DatasetField.CHECKED_VALUE, "*" + DatasetField.CHECKED_VALUE);
//	public static final IPropertyDescriptor PD_UNCHECKED_VALUE = new StringPropertyDescriptor(
//			DatasetField.UNCHECKED_VALUE, "*" + DatasetField.UNCHECKED_VALUE);
//
//	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
//			PD_PROMPT, PD_NAME, PD_TEXT, DatasetField.PD_READONLY,
//			DatasetField.PD_REQUIRED, PD_CHECKED_VALUE, PD_UNCHECKED_VALUE };

	public CheckBox() {
		setSize(120, 20);
		this.setComponentType(CHECKBOX);
		this.setText(TEXT);
	}

	public String getText() {
//		return text;
		return this.getStringPropertyValue(ComponentProperties.text);
	}

	public void setText(String text) {
//		if (eq(this.text, text))
//			return;
//		String oldV = this.text;
//		this.text = text;
//		firePropertyChange(TEXT, oldV, text);
		this.setPropertyValue(ComponentProperties.text,text);
	}


//	public Object getPropertyValue(Object propName) {
//		if (TEXT.equals(propName))
//			return getText();
//		else if (DatasetField.UNCHECKED_VALUE.equals(propName)
//				|| DatasetField.CHECKED_VALUE.equals(propName)
//				|| DatasetField.DEFAULT_VALUE.equals(propName)) {
//			return getDatasetField().getPropertyValue(propName);
//		}
//		return super.getPropertyValue(propName);
//	}

//	public void setPropertyValue(Object propName, Object val) {
//		if (TEXT.equals(propName))
//			setText((String) val);
//		else if (DatasetField.UNCHECKED_VALUE.equals(propName)
//				|| DatasetField.CHECKED_VALUE.equals(propName)
//				|| DatasetField.DEFAULT_VALUE.equals(propName)) {
//			getDatasetField().setPropertyValue(propName, val);
//		} else
//			super.setPropertyValue(propName, val);
//	}

	public void setParent(Container part) {
		super.setParent(part);
//		if (getDatasetField() != null)
//			getDatasetField().setDataset(
//					DataSetFieldUtil.findDataset(getParent()));
	}

	public void setDatasetField(DatasetField field) {
		field.setName(getName());
//		field.setDataset(DataSetFieldUtil.findDataset(getParent()));
		field.setCheckedValue("Y");
		field.setUncheckedValue("N");
		field.setDefaultValue("N");
		super.setDatasetField(field);
	}
}
