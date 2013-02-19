package aurora.ide.meta.gef.editors.template.handle;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.ViewDiagram;

public class UpdateTemplateHandle extends TemplateHandle {

	public UpdateTemplateHandle(TemplateConfig config) {
		super(config);
	}

	@Override
	public void fill(ViewDiagram viewDiagram) {
		super.fill(viewDiagram);
	}

	@Override
	protected GridColumn createGridColumn(CompositeMap map) {
		GridColumn gc = super.createGridColumn(map);
		gc.setEditor(aurora.ide.meta.gef.Util.getType(map));
		return gc;
	}
}
