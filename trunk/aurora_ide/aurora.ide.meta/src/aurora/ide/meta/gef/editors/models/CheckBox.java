package aurora.ide.meta.gef.editors.models;

import aurora.ide.meta.gef.editors.property.StringPropertyDescriptor;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class CheckBox extends Input {

	private static final long serialVersionUID = 319077599101372088L;
	public static final String TEXT = "text";
	private String text = "text";
	public static final String CHECKBOX = "checkBox";

	private DatasetField dsField = new CheckboxDatasetField();

	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
			PD_PROMPT, new StringPropertyDescriptor(TEXT, "Text"), PD_NAME };

	public CheckBox() {
		setSize(new Dimension(120, 20));
		this.setType(CHECKBOX);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		if (eq(this.text, text))
			return;
		String oldV = this.text;
		this.text = text;
		firePropertyChange(TEXT, oldV, text);
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		IPropertyDescriptor[] pds_dsf = dsField.getPropertyDescriptors();
		return mergePropertyDescriptor(pds, pds_dsf);
	}

	@Override
	public Object getPropertyValue(Object propName) {
		if (TEXT.equals(propName))
			return getText();
		if (DatasetField.UNCHECKED_VALUE.equals(propName)
				|| DatasetField.CHECKED_VALUE.equals(propName)
				|| DatasetField.DEFAULT_VALUE.equals(propName)) {
			return dsField.getPropertyValue(propName);
		}
		return super.getPropertyValue(propName);
	}

	@Override
	public void setPropertyValue(Object propName, Object val) {
		if (TEXT.equals(propName))
			setText((String) val);
		else if (DatasetField.UNCHECKED_VALUE.equals(propName)
				|| DatasetField.CHECKED_VALUE.equals(propName)
				|| DatasetField.DEFAULT_VALUE.equals(propName)) {
			dsField.setPropertyValue(propName, val);
		} else
			super.setPropertyValue(propName, val);
	}

	@Override
	public DatasetField getDatasetField() {

		return dsField;
	}

	@Override
	public void setDatasetField(DatasetField field) {
		dsField = field;
	}

}
