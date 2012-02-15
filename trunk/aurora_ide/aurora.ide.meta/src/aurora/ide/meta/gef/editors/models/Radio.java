package aurora.ide.meta.gef.editors.models;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import aurora.ide.meta.gef.editors.property.BooleanPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.IntegerPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.StringPropertyDescriptor;

public class Radio extends Input {

	private static final long serialVersionUID = 319077599101372088L;
	public static final String SELECTION_STATE = "selection_state";
	public static final String TEXT = "radio_text";
	private boolean selected = false;
	private String text = "text";
	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
			new StringPropertyDescriptor(PROMPT, "Prompt"),
			new BooleanPropertyDescriptor(SELECTION_STATE, "Selected"),
			new StringPropertyDescriptor(TEXT, "Text"),
			new IntegerPropertyDescriptor(WIDTH, "Width") };

	public Radio() {
		setSize(new Dimension(120, 20));
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		if (this.selected == selected)
			return;
		boolean oldV = this.selected;
		this.selected = selected;
		firePropertyChange(SELECTION_STATE, oldV, selected);
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
		return pds;
	}

	@Override
	public Object getPropertyValue(Object propName) {
		if (SELECTION_STATE.equals(propName))
			return isSelected();
		else if (TEXT.equals(propName))
			return getText();
		return super.getPropertyValue(propName);
	}

	@Override
	public void setPropertyValue(Object propName, Object val) {
		if (SELECTION_STATE.equals(propName))
			setSelected((Boolean) val);
		else if (TEXT.equals(propName))
			setText((String) val);
		super.setPropertyValue(propName, val);
	}

}
