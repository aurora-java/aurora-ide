package aurora.ide.prototype.consultant.demonstrate;

import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.swt.widgets.Shell;

import aurora.ide.meta.gef.editors.figures.GridColumnCellEditorLocator;
import aurora.ide.meta.gef.editors.figures.GridColumnFigure;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.GridColumn;
import aurora.plugin.source.gen.screen.model.Input;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;

public class GridColumnDemonstrating {
	private ComponentPart part;
	private String feature;

	public GridColumnDemonstrating(ComponentPart part) {
		this.part = part;
	}

	public void demonstrating(Shell shell) {

		AuroraComponent ac = part.getComponent();
		if (GridColumn.GRIDCOLUMN.equals(ac.getComponentType())) {
			GridColumn gc = (GridColumn) ac;
			if (Input.Combo.equals(gc.getEditor())) {
				ComboDemonstrating comboDemonstrating = new ComboDemonstrating(
						part) {
					protected CellEditorLocator getCellEditorLocator() {
						return new GridColumnCellEditorLocator(
								(GridColumnFigure) part.getFigure(), getIdx());
					}
				};
				comboDemonstrating.setFeature(feature);
				comboDemonstrating.demonstrating(shell);
			} else if (Input.LOV.equals(gc.getEditor())) {
				LOVDemonstrating lovDemonstrating = new LOVDemonstrating(part);
				lovDemonstrating.setFeature(feature);
				lovDemonstrating.demonstrating(shell);
			} else {
				new ButtonDemonstrating(part).demonstrating(shell);
			}
		}
	}

	private int getIdx() {
		if (feature == null)
			return 1;
		else {
			return Integer.valueOf(feature.replace(
					ComponentInnerProperties.GRID_COLUMN_SIMPLE_DATA, ""));
		}

	}

	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}
}
