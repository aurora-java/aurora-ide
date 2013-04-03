package aurora.plugin.source.gen.screen.model;

import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;
import aurora.plugin.source.gen.screen.model.properties.IPropertyDescriptor;


public class BOX extends RowCol {

//	private String title;
//	private int labelWidth = 80;

//	protected static final IPropertyDescriptor PD_ROW = new IntegerPropertyDescriptor(
//			ROW, "Row", 1, 100, 1, 2);
//	protected static final IPropertyDescriptor PD_COL = new IntegerPropertyDescriptor(
//			COL, "Column", 1, 100, 1, 2);
//	protected static final IPropertyDescriptor PD_TITLE = new StringPropertyDescriptor(
//			TITLE, "Title");
//
//	protected static final IPropertyDescriptor PD_LABELWIDTH = new IntegerPropertyDescriptor(
//			LABELWIDTH, "LabelWidth");
//	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
//			PD_PROMPT, PD_TITLE, PD_WIDTH, PD_HEIGHT, PD_COL, PD_LABELWIDTH };
	/**
	 * 
	 */

	public BOX() {
		this.setDataset(new QueryDataSet());
		this.setLabelWidth(80);
	}

	public String getTitle() {
		return ""+this.getPropertyValue(ComponentProperties.title);
	}

	public int getLabelWidth() {
//		return labelWidth;
		Object r = this.getPropertyValue(ComponentProperties.labelWidth);
		if (r instanceof Integer) {
			return (Integer) r;
		}
		return 80;
	
	}

	public void setLabelWidth(int lw) {
//		if (this.labelWidth == labelWidth) {
//			return;
//		}
//		int old = this.labelWidth;
//		this.labelWidth = labelWidth;
//		firePropertyChange(LABELWIDTH, old, labelWidth);
//		this.labelWidth = labelWidth;
		this.setPropertyValue(ComponentProperties.labelWidth, lw);
	}

	public void setTitle(String t) {
//		if (eq(this.title, title)) {
//			return;
//		}
//		String old = this.title;
//		this.title = title;
//		firePropertyChange(TITLE, old, title);
		this.setPropertyValue(ComponentProperties.title, t);

	}


//	public Object getPropertyValue(Object propName) {
//		if (ROW.equals(propName))
//			return getRow();
//		else if (COL.equals(propName))
//			return getCol();
//		else if (TITLE.equals(propName))
//			return getTitle();
//		else if (LABELWIDTH.equals(propName))
//			return getLabelWidth();
//		else {
//			Object obj = getDataset().getPropertyValue(propName);
//			if (obj != null)
//				return obj;
//		}
//		return super.getPropertyValue(propName);
//	}

//	public void setPropertyValue(Object propName, Object val) {
//		if (ROW.equals(propName))
//			setRow((Integer) val);
//		else if (COL.equals(propName))
//			setCol((Integer) val);
//		else if (TITLE.equals(propName))
//			setTitle((String) val);
//		else if (LABELWIDTH.equals(propName))
//			setLabelWidth((Integer) val);
//		else {
//			Object obj = getDataset().getPropertyValue(propName);
//			if (obj != null)
//				getDataset().setPropertyValue(propName, val);
//		}
//		super.setPropertyValue(propName, val);
//	}

	public boolean isResponsibleChild(AuroraComponent component) {
		if (component instanceof Grid)
			return true;
		if (component instanceof Toolbar || component instanceof Navbar
				|| component instanceof GridColumn)
			return false;
		if (component instanceof TabItem)
			return false;
		return super.isResponsibleChild(component);
	}

}
