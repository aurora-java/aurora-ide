package aurora.ide.meta.gef.editors;

import aurora.plugin.source.gen.screen.model.ScreenBody;

public class EditorMode {
	public static final String Template = "Template";
	public static final String Design = "Design";
	public static final String None = "None";

	private VScreenEditor vsEditor;

	public EditorMode() {

	}

	public EditorMode(VScreenEditor vsEditor) {
		super();
		this.vsEditor = vsEditor;
	}

	public String getMode() {
		ScreenBody diagram = vsEditor.diagram;
		if (diagram != null && diagram.isBindTemplate()) {
			return Template;
		}
		return None;
	}

	public boolean isForDisplay() {
		return vsEditor.diagram.isForDisplay();
	}

	public boolean isForCreate() {
		return vsEditor.diagram.isForCreate();
	}

	public boolean isForUpdate() {
		return vsEditor.diagram.isForUpdate();
	}

	public boolean isForSearch() {
		return vsEditor.diagram.isForSearch();
	}

}
