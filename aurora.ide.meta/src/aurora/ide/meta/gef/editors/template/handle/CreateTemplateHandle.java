package aurora.ide.meta.gef.editors.template.handle;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.Util;
import aurora.ide.meta.gef.designer.BMCompositeMap;
import aurora.ide.meta.gef.editors.template.BMReference;
import aurora.plugin.source.gen.screen.model.Container;
import aurora.plugin.source.gen.screen.model.GridColumn;
import aurora.plugin.source.gen.screen.model.ScreenBody;

public class CreateTemplateHandle extends TemplateHandle {

	public CreateTemplateHandle(TemplateConfig config) {
		super(config);
	}

	public void fill(ScreenBody viewDiagram) {
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
