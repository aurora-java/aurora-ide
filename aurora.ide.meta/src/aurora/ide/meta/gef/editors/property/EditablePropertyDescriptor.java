package aurora.ide.meta.gef.editors.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import aurora.plugin.source.gen.screen.model.properties.DefaultPropertyDescriptor;

public class EditablePropertyDescriptor extends DefaultPropertyDescriptor
		implements org.eclipse.ui.views.properties.IPropertyDescriptor {
	/**
	 * The object that provides the property value's text and image, or
	 * <code>null</code> if the default label provider is used (the default).
	 */
	private ILabelProvider labelProvider = null;
	/**
	 * Category name, or <code>null</code> if none (the default).
	 */
	private String category = null;
	/**
	 * The flags used to filter the property.
	 */
	private String[] filterFlags;
	/**
	 * The help context ids, or <code>null</code> if none (the default).
	 */
	private Object helpIds;

	public EditablePropertyDescriptor(String id, int style) {
		super(id, style);
	}

	@Override
	public CellEditor createPropertyEditor(Composite parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCategory() {
		return category;
	}

	@Override
	public String[] getFilterFlags() {
		return filterFlags;
	}

	@Override
	public Object getHelpContextIds() {
		// TODO Auto-generated method stub
		return helpIds;
	}

	public void setHelpContextIds(Object contextIds) {
		helpIds = contextIds;
	}

	public void setLabelProvider(ILabelProvider provider) {
		labelProvider = provider;
	}

	public ILabelProvider getLabelProvider() {
		if (labelProvider != null) {
			return labelProvider;
		}
		return new LabelProvider();
	}

	@Override
	public boolean isCompatibleWith(IPropertyDescriptor anotherProperty) {
		return false;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setFilterFlags(String[] filterFlags) {
		this.filterFlags = filterFlags;
	}

}
