package aurora.ide.sxsd.editor;

import org.eclipse.ui.forms.editor.FormEditor;

import aurora.ide.editor.CompositeMapTreePage;



public class SxsdTreePage extends  CompositeMapTreePage{
	private static final String PageId = "SxsdTreePage";
	private static final String PageTitle = "Simple XML Schema";

	public SxsdTreePage(FormEditor editor) {
		super(editor, PageId, PageTitle);
	}
}
