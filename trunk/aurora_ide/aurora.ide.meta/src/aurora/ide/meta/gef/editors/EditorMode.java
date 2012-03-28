package aurora.ide.meta.gef.editors;

import aurora.ide.meta.gef.editors.models.ViewDiagram;

public class EditorMode {
	public static final String Template = "Template";
	public static final String Design = "Design";
	public static final String None = "None";

	private VScreenEditor vsEditor;

	public EditorMode(VScreenEditor vsEditor) {
		super();
		this.vsEditor = vsEditor;
	}

	public String getMode() {
		ViewDiagram diagram = vsEditor.diagram;
		if (diagram != null && diagram.isBindTemplate()) {
			return Template;
		}
		return None;
	}
	public boolean isForDisplay(){
		return false;
	}

}
