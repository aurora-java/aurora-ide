package aurora.ide.meta.gef.editors.models;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class VBox extends BOX {
	/**
	 * 
	 */
	private static final long serialVersionUID = 588460053081220683L;
	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
			PD_PROMPT, PD_LABELWIDTH };

	public VBox() {
		setSize(new Dimension(300, 80));
		this.setType("vBox");
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

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		// return pds;
		return mergePropertyDescriptor(pds, getDataset()
				.getPropertyDescriptors());
	}
}
