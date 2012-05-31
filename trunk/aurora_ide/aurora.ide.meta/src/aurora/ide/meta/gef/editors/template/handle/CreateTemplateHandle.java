package aurora.ide.meta.gef.editors.template.handle;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.Util;
import aurora.ide.meta.gef.designer.BMCompositeMap;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.template.BMReference;

public class CreateTemplateHandle extends TemplateHandle {

	public void fill(ViewDiagram viewDiagram) {
		setColNum(viewDiagram, 1);
		this.viewDiagram = viewDiagram;
		for (BMReference bm : config.getModelRelated().keySet()) {
			for (Container ac : config.getModelRelated().get(bm)) {
				BMCompositeMap bmc = new BMCompositeMap(bm.getModel());
				fillContainer(ac, bm, bmc);
			}
		}
	}

	@Override
	protected GridColumn createGridColumn(CompositeMap map) {
		GridColumn gc = super.createGridColumn(map);
		gc.setEditor(Util.getType(map));
		return gc;
	}
}
