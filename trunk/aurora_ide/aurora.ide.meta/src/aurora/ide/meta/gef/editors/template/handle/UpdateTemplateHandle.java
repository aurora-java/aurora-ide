package aurora.ide.meta.gef.editors.template.handle;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.template.BMReference;

public class UpdateTemplateHandle extends TemplateHandle {

	@Override
	public void fill(ViewDiagram viewDiagram) {
		this.viewDiagram = viewDiagram;
		for (BMReference bm : modelRelated.keySet()) {
			for (Container ac : modelRelated.get(bm)) {
				fillContainer(ac, bm);
			}
		}

		for (BMReference bm : queryModelRelated.keySet()) {
			AuroraComponent ac = auroraComponents.get(queryModelRelated.get(bm));
			if (ac instanceof Container) {
				fillQueryBox((Container) ac, bm);
			}
		}
	}
	
	@Override
	protected GridColumn createGridColumn(CompositeMap map) {
		GridColumn gc = super.createGridColumn(map);
		gc.setEditor(GefModelAssist.getTypeNotNull(map));
		return gc;
	}
}
