package aurora.ide.meta.gef.editors.models;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class GridSelectionCol extends GridColumn {
	private static final long serialVersionUID = 767365033992120193L;
	private String selectionModel = ResultDataSet.SELECT_NONE;

	public GridSelectionCol() {
		super();
		this.setSize(new Dimension(25, 380));
	}

	@Override
	public boolean isResponsibleChild(AuroraComponent child) {
		return false;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return NONE_PROPS;
	}

	public String getSelectionMode() {
		return selectionModel;
	}

	public void setSelectionMode(String selectionModel) {
		if (eq(this.selectionModel, selectionModel))
			return;
		String oldV = this.selectionModel;
		this.selectionModel = selectionModel;
		firePropertyChange("SELECTIONMODEL", oldV, selectionModel);
	}

}
