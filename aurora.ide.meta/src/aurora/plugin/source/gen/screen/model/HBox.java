package aurora.plugin.source.gen.screen.model;

import aurora.plugin.source.gen.screen.model.properties.IPropertyDescriptor;


public class HBox extends BOX {
	public static final String H_BOX = "hBox";

	/**
	 * 
	 */

//	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
//			PD_PROMPT, PD_LABELWIDTH };

	public HBox() {
		setSize(200, 40);
		this.setComponentType(H_BOX);
	}

	public int getHeadHight() {

		return 5;
	}

	@Override
	final public int getRow() {
		// always 1
		return 1;
	}

	@Override
	final public void setRow(int row) {
		// always 1
	}

	@Override
	final public int getCol() {
		return 1000;
	}

	@Override
	final public void setCol(int col) {
		// always Integer.MAX_VALUE
	}


}
