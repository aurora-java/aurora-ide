package aurora.ide.meta.gef.editors.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.ComboBoxLabelProvider;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * Descriptor for a property that has a value which should be edited with a
 * combo box cell editor. This class provides a default
 * <code>ILabelProvider</code> that will render the label of the given
 * descriptor as the <code>String</code> found in the labels array at the
 * currently selected index.
 * <p>
 * The value of the property is a 0-based <code>Integer</code> index into the
 * labels array.
 * </p>
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * <p>
 * Example:
 * 
 * <pre>
 * String[] values = { &quot;Top left&quot;, &quot;Top right&quot;, &quot;Bottom left&quot;, &quot;Bottom right&quot; };
 * IPropertyDescriptor pd = new ComboBoxPropertyDescriptor(&quot;origin&quot;, &quot;Origin&quot;,
 * 		values);
 * </pre>
 * 
 * </p>
 * 
 * @noextend This class is not intended to be subclassed by clients.
 */
public class ComboPropertyDescriptor extends PropertyDescriptor {

	/**
	 * The labels to display in the combo box
	 */
	private String[] labels;

	/**
	 * Creates an property descriptor with the given id, display name, and list
	 * of value labels to display in the combo box cell editor.
	 * 
	 * @param id
	 *            the id of the property
	 * @param displayName
	 *            the name to display for the property
	 * @param labelsArray
	 *            the labels to display in the combo box
	 */
	public ComboPropertyDescriptor(Object id, String displayName,
			String[] labelsArray) {
		super(id, displayName);
		labels = labelsArray;
	}

	/**
	 * The <code>ComboBoxPropertyDescriptor</code> implementation of this
	 * <code>IPropertyDescriptor</code> method creates and returns a new
	 * <code>ComboBoxCellEditor</code>.
	 * <p>
	 * The editor is configured with the current validator if there is one.
	 * </p>
	 */
	public CellEditor createPropertyEditor(Composite parent) {
		CellEditor editor = new ComboCellEditor(parent, labels, SWT.READ_ONLY);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}

	/**
	 * The <code>ComboBoxPropertyDescriptor</code> implementation of this
	 * <code>IPropertyDescriptor</code> method returns the value set by the
	 * <code>setProvider</code> method or, if no value has been set it returns a
	 * <code>ComboBoxLabelProvider</code> created from the valuesArray of this
	 * <code>ComboBoxPropertyDescriptor</code>.
	 * 
	 * @see #setLabelProvider(ILabelProvider)
	 */
	public ILabelProvider getLabelProvider() {
		if (isLabelProviderSet()) {
			return super.getLabelProvider();
		}
		return new ComboBoxLabelProvider(labels);
	}
}
