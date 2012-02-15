package aurora.ide.meta.gef.editors.source.gen;

import java.util.ArrayList;
import java.util.List;

import aurora.ide.meta.gef.editors.models.ViewDiagram;

public class IDGenerator {
	private ViewDiagram diagram;
	private List<String> ids;

	public IDGenerator(ViewDiagram diagram) {
		this.diagram = diagram;
		ids = new ArrayList<String>();
	}

	public String genEditorID(String editorType) {
		return genID(editorType, 0);
	}

	public String genLinkID(String fileName) {
		return genID(fileName + "_link", 0);
	}

	private String genID(String id, int i) {
		if (i > 0) {
			id = id + "_" + i;
		}
		if (ids.contains(id)) {
			i++;
			return genID(id, i);
		} else {
			ids.add(id);
			return id;
		}
	}
}
