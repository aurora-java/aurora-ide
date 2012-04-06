package aurora.ide.meta.gef.editors.source.gen.core;

import java.util.ArrayList;
import java.util.List;

import aurora.ide.meta.gef.editors.models.Dataset;
import aurora.ide.meta.gef.editors.models.ViewDiagram;

public class IDGenerator {
	private ViewDiagram diagram;
	private List<String> ids;

	private static final String QS = "_query_ds";

	private static final String RS = "_result_ds";

	public IDGenerator(ViewDiagram diagram) {
		this.diagram = diagram;
		ids = new ArrayList<String>();
	}

	public String genEditorID(String editorType) {
		return genID(editorType, 0);
	}

	public String genLinkID(String fileName) {
		if (fileName == null)
			return null;
		return genID(fileName + "_link", 0);
	}

	public String genDatasetID(Dataset dataset) {
		String[] split = dataset.getModel().split("\\.");
		String name = split[split.length - 1];
		name = dataset.isUse4Query() ? name + QS : name + RS;
		return genID(name, 0);
	}

	private String genID(String id, int i) {
		String oldID = id;
		if (i > 0) {
			id = id + "_" + i;
		}
		if (ids.contains(id)) {
			i++;
			return genID(oldID, i);
		} else {
			ids.add(id);
			return id;
		}
	}

	public String genWindowID(String linkId) {
		if (linkId == null)
			linkId = "";
		return genID(linkId + "_window", 0);
	}
}
