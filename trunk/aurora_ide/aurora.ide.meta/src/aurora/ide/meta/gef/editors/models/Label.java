package aurora.ide.meta.gef.editors.models;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class Label extends AuroraComponent {

	private static int idx = 0;
	private static final long serialVersionUID = -6906286787889789167L;
	private static final IPropertyDescriptor[] pds = { PD_PROMPT, PD_NAME, PD_WIDTH };
	private DatasetField datasetField = new DatasetField();

	public Label() {
		setPrompt("label" + idx++);
		this.setSize(new Dimension(80, 20));
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return pds;
	}

	public DatasetField getDatasetField() {
		return datasetField;
	}

	public void setDatasetField(DatasetField datasetField) {
		this.datasetField = datasetField;
	}

}