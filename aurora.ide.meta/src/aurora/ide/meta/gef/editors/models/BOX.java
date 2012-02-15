package aurora.ide.meta.gef.editors.models;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import aurora.ide.meta.gef.editors.property.IntegerPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.StringPropertyDescriptor;

public class BOX extends RowCol {

	private String title;
	private int labelWidth = 80;

	protected static final IPropertyDescriptor PD_ROW = new IntegerPropertyDescriptor(
			ROW, "Row", 1, 100, 1, 2);
	protected static final IPropertyDescriptor PD_COL = new IntegerPropertyDescriptor(
			COL, "Column", 1, 100, 1, 2);
	protected static final IPropertyDescriptor PD_TITLE = new StringPropertyDescriptor(
			TITLE, "Title");

	protected static final IPropertyDescriptor PD_LABELWIDTH = new IntegerPropertyDescriptor(
			LABELWIDTH, "LabelWidth");
	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
			PD_PROMPT, PD_TITLE, PD_WIDTH, PD_HEIGHT, PD_ROW, PD_COL,
			PD_LABELWIDTH };
	/**
	 * 
	 */
	private static final long serialVersionUID = -8776030333465182289L;

	public BOX() {
		this.setDataset(new QueryDataSet());
	}

	public String getTitle() {
		return title;
	}

	public int getLabelWidth() {
		return labelWidth;
	}

	public void setLabelWidth(int labelWidth) {
		if (this.labelWidth == labelWidth) {
			return;
		}
		int old = this.labelWidth;
		this.labelWidth = labelWidth;
		firePropertyChange(LABELWIDTH, old, labelWidth);
		this.labelWidth = labelWidth;
	}

	public void setTitle(String title) {
		if (eq(this.title, title)) {
			return;
		}
		String old = this.title;
		this.title = title;
		firePropertyChange(TITLE, old, title);

	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		// return pds;
		return mergePropertyDescriptor(pds, getDataset()
				.getPropertyDescriptors());
	}

	@Override
	public Object getPropertyValue(Object propName) {
		if (ROW.equals(propName))
			return getRow();
		else if (COL.equals(propName))
			return getCol();
		else if (TITLE.equals(propName))
			return getTitle();
		else if (LABELWIDTH.equals(propName))
			return getLabelWidth();
		else {
			Object obj = getDataset().getPropertyValue(propName);
			if (obj != null)
				return obj;
		}
		return super.getPropertyValue(propName);
	}

	@Override
	public void setPropertyValue(Object propName, Object val) {
		if (ROW.equals(propName))
			setRow((Integer) val);
		else if (COL.equals(propName))
			setCol((Integer) val);
		else if (TITLE.equals(propName))
			setTitle((String) val);
		else if (LABELWIDTH.equals(propName))
			setLabelWidth((Integer) val);
		else {
			Object obj = getDataset().getPropertyValue(propName);
			if (obj != null)
				getDataset().setPropertyValue(propName, val);
		}
		super.setPropertyValue(propName, val);
	}

	public boolean isResponsibleChild(AuroraComponent component) {
		if (component instanceof Grid)
			return true;
		if (component instanceof Toolbar || component instanceof Navbar
				|| component instanceof GridColumn)
			return false;
		return super.isResponsibleChild(component);
	}

}
