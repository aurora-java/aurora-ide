package aurora.ide.sxsd.editor;

import aurora.ide.editor.CompositeMapTreeEditor;
import aurora.ide.editor.CompositeMapTreePage;

public class SxsdEditor extends CompositeMapTreeEditor {

	public CompositeMapTreePage initTreePage() {
		SxsdTreePage treePage = new SxsdTreePage(this);
		return treePage;
	}


}