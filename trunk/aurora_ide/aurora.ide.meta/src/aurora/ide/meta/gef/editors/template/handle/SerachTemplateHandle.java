package aurora.ide.meta.gef.editors.template.handle;

import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.designer.BMCompositeMap;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.template.BMReference;

public class SerachTemplateHandle extends TemplateHandle {

	private Map<String, String> refRelat;

	@Override
	public void fill(ViewDiagram viewDiagram) {
		super.fill(viewDiagram);
	}

	@Override
	protected void fillContainer(Container ac, BMReference bm) {
		refRelat = getReferenceRelation(new BMCompositeMap(bm.getModel()));
		super.fillContainer(ac, bm);
	}

	@Override
	protected void fillBox(Container ac, BMCompositeMap bmc) {
		ac.getChildren().clear();
		for (CompositeMap map : getFieldsWithoutPK(bmc)) {
			Input input = AuroraModelFactory.createComponent(GefModelAssist.getTypeNotNull(map));
			String name = map.getString("name");
			if (refRelat.containsKey(name)) {
				name = refRelat.get(name);
			}
			input.setName(name);
			String prompt = map.getString("prompt");
			prompt = prompt == null ? map.getString("name") : prompt;
			input.setPrompt(prompt);
			((Container) ac).addChild(input);
		}
	}

	@Override
	protected GridColumn createGridColumn(CompositeMap map) {
		GridColumn gc = new GridColumn();
		String name = map.getString("name");
		if (refRelat.containsKey(name)) {
			name = refRelat.get(name);
		}
		gc.setName(name);
		String prompt = map.getString("prompt");
		prompt = prompt == null ? map.getString("name") : prompt;
		gc.setPrompt(prompt);
		return gc;
	}
}
