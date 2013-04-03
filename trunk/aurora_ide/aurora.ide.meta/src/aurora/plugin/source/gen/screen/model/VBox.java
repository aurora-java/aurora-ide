package aurora.plugin.source.gen.screen.model;

import aurora.plugin.source.gen.screen.model.properties.IPropertyDescriptor;


public class VBox extends BOX {
	public static final String V_BOX = "vBox";
	/**
	 * 
	 */
//	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
//			PD_PROMPT, PD_LABELWIDTH };

	public VBox() {
		setSize(300, 80);
		this.setComponentType(V_BOX);
	}

	public int getHeadHight() {
		return 5;
	}

	@Override
	final public int getRow() {
		return 1000;
	}

	@Override
	final public void setRow(int row) {
		// always Integer.MAX_VALUE
	}

	@Override
	final public int getCol() {
		// always 1
		return 1;
	}

	@Override
	final public void setCol(int col) {
		// always 1
	}
}
