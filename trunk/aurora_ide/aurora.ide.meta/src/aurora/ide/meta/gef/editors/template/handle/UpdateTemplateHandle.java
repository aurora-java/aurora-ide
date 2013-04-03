package aurora.ide.meta.gef.editors.template.handle;

import uncertain.composite.CompositeMap;
import aurora.plugin.source.gen.screen.model.GridColumn;
import aurora.plugin.source.gen.screen.model.ScreenBody;

public class UpdateTemplateHandle extends TemplateHandle {

	public UpdateTemplateHandle(TemplateConfig config) {
		super(config);
	}

	@Override
	public void fill(ScreenBody viewDiagram) {
		super.fill(viewDiagram);
	}

	@Override
	protected GridColumn createGridColumn(CompositeMap map) {
		GridColumn gc = super.createGridColumn(map);
		gc.setEditor(aurora.ide.meta.gef.Util.getType(map));
		return gc;
	}
}
