package aurora.ide.editor;


import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.jface.viewers.StructuredSelection;

import aurora.ide.helpers.CompositeMapLocatorParser;
import aurora.ide.helpers.DialogUtil;

import uncertain.composite.CompositeMap;


public abstract class CompositeMapTreeEditor extends BaseCompositeMapEditor {

	
	protected CompositeMapTreePage treePage;
	
	public CompositeMapTreeEditor() {
		super();
	}
	
	public CompositeMapPage initMainViewerPage(){
		this.treePage = initTreePage();
		return treePage;
	}
	public abstract CompositeMapTreePage initTreePage();
	
	protected void pageChange(int newPageIndex){
		int currentPage = getCurrentPage();
		super.pageChange(newPageIndex);
		if(!mainViewerPage.isFormContendCreated())
				return;
		if(currentPage==mainViewerIndex&&newPageIndex ==textPageIndex){
			locateTextPage();
		}else if(currentPage==textPageIndex&&newPageIndex ==mainViewerIndex&&getTextPage().checkContentFormat()){
			locateTreePage();
		}
	}
	private void locateTreePage(){
		CompositeMapLocatorParser parser = new CompositeMapLocatorParser();

		try {
			InputStream content = new ByteArrayInputStream(getTextPage().getContent()
					.getBytes("UTF-8"));
			CompositeMap  cm = parser.getCompositeMapFromLine(content, getTextPage().getCursorLine());
			if(cm != null){
				treePage.getTreeViewer().setSelection(
						new StructuredSelection(cm), true);
			}
		} catch (Exception e){
			DialogUtil.showExceptionMessageBox(e);
		}

	}
	private void locateTextPage(){
		CompositeMap selection = treePage.getSelection();
		if(selection == null)
			return;
		CompositeMapLocatorParser parser = new CompositeMapLocatorParser();

		int line = 0;
		try {
			InputStream content = new ByteArrayInputStream(getTextPage().getContent().getBytes("UTF-8"));
			line = parser.LocateCompositeMapLine(content, selection);
		} catch (Exception e) {
			DialogUtil.showExceptionMessageBox(e);
		}
		int offset = getTextPage().getOffsetFromLine(line);
		int length = getTextPage().getLengthOfLine(line);
		if(offset==0||length==0)
			return;
		getTextPage().resetHighlightRange();
		getTextPage().setHighlightRange(offset, length, true);
	}
}