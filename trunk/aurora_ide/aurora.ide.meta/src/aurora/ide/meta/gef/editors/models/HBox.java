package aurora.ide.meta.gef.editors.models;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class HBox extends BOX {
	public static final String H_BOX = "hBox";

	/**
	 * 
	 */
	private static final long serialVersionUID = 6675954637550725095L;

	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
			PD_PROMPT, PD_LABELWIDTH };

	public HBox() {
		setSize(new Dimension(200, 40));
		this.setType(H_BOX);
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

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		String sectionType = getSectionType();
		if (sectionType == null || sectionType.equals(SECTION_TYPE_BUTTON))
			return pds;
		return mergePropertyDescriptor(pds, getDataset()
				.getPropertyDescriptors());
	}

}
