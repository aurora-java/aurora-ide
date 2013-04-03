package aurora.plugin.source.gen.screen.model;

import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;
import aurora.plugin.source.gen.screen.model.properties.IPropertyDescriptor;


public class GridSelectionCol extends GridColumn {
	public static final String GRIDSELECTIONCOL = "gridselectioncol";
//	private String selectionModel = ResultDataSet.SELECT_NONE;

	public GridSelectionCol() {
		super();
		this.setSize(25, 380);
		this.setComponentType(GRIDSELECTIONCOL);
		this.setSelectionMode(ResultDataSet.SELECT_NONE);
	}

	@Override
	public boolean isResponsibleChild(AuroraComponent child) {
		return false;
	}


	public String getSelectionMode() {
//		return selectionModel;
		return this.getStringPropertyValue(ComponentInnerProperties.GRID_SELECTION_MODE);
	}

	public void setSelectionMode(String selectionModel) {
//		if (eq(this.selectionModel, selectionModel))
//			return;
//		String oldV = this.selectionModel;
//		this.selectionModel = selectionModel;
//		firePropertyChange("SELECTIONMODEL", oldV, selectionModel);
		this.setPropertyValue(ComponentInnerProperties.GRID_SELECTION_MODE, selectionModel);
	}

}
