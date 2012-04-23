package aurora.ide.meta.gef.editors.template.handle;

import java.util.ArrayList;
import java.util.List;

import uncertain.composite.CompositeMap;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.BOX;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.template.BMReference;

public class CreateTemplateHandle extends TemplateHandle {

	public void fill(ViewDiagram viewDiagram) {
		this.viewDiagram = viewDiagram;
		setRowColNum(viewDiagram);
		for (BMReference bm : modelRelated.keySet()) {
			for (Container ac : modelRelated.get(bm)) {
				fillContainer(ac, bm);
			}
		}
	}

	@Override
	protected GridColumn createGridColumn(CompositeMap map) {
		GridColumn gc = super.createGridColumn(map);
		gc.setEditor(GefModelAssist.getTypeNotNull(map));
		return gc;
	}

	private void setRowColNum(ViewDiagram viewDiagram) {
		List<BOX> rowCols = new ArrayList<BOX>();
		boolean hasContainer = false;
		for (AuroraComponent ac : viewDiagram.getChildren()) {
			if (ac instanceof BOX) {
				if (((BOX) ac).getChildren().size() == 0) {
					rowCols.add((BOX) ac);
				}
			} else {
				hasContainer = true;
			}
		}
		if (!hasContainer) {
			for (BOX rc : rowCols) {
				rc.setCol(1);
			}
		}
	}
}
