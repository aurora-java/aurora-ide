package aurora.ide.meta.gef.editors.template.handle;

import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.designer.BMCompositeMap;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.Label;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.template.BMReference;

public class DisplayTemplateHandle extends TemplateHandle {

	private Map<String, String> refRelat;

	@Override
	public void fill(ViewDiagram viewDiagram){
		this.viewDiagram = viewDiagram;
		for (BMReference bm : modelRelated.keySet()) {
			for (Container ac : modelRelated.get(bm)) {
				refRelat = getReferenceRelation(new BMCompositeMap(bm.getModel()));
				fillContainer(ac, bm);
			}
		}
	}

	@Override
	protected void fillBox(Container ac, BMCompositeMap bmc) {
		ac.getChildren().clear();
		for (CompositeMap map : getFieldsWithoutPK(bmc)) {
			Label label = new Label();
			String name = map.getString("name");
			if (refRelat.containsKey(name)) {
				name = refRelat.get(name);
			}
			label.setName(name);
			label.setPrompt(map.getString("prompt") == null ? map.getString("name") : map.getString("prompt"));
			((Container) ac).addChild(label);
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
