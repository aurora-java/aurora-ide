package aurora.ide.editor.textpage.action;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import aurora.ide.api.composite.map.CommentXMLOutputter;
import aurora.ide.editor.textpage.TextPage;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.helpers.DialogUtil;

public class FormatAction implements IEditorActionDelegate {

	IEditorPart activeEditor ;
	public FormatAction() {
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		activeEditor = targetEditor;
	}

	public void run(IAction action) {
		if(activeEditor == null||!(activeEditor instanceof TextPage)){
			DialogUtil.showErrorMessageBox("这个类不是"+TextPage.class.getName());
			return;
		}
		TextPage tp = (TextPage)activeEditor;
		IFile file = tp.getFile();
		IDocument document = tp.getInputDocument();
		String content = document.get();
		if(content == null){
			return;
		}
		int cursorLine = tp.getCursorLine();
		CompositeLoader cl =AuroraResourceUtil.getCompsiteLoader();
		InputStream is =null;
		try {
			is = new ByteArrayInputStream(content.getBytes("UTF-8"));
			CompositeMap data = cl.loadFromStream(is);
			String formatContent = AuroraResourceUtil.xml_decl + CommentXMLOutputter.defaultInstance().toXML(data, true);
			tp.refresh(formatContent);
		} catch (IOException e) {
			DialogUtil.logErrorException("解析"+file.getFullPath().toOSString()+"错误！",e);
		} catch (SAXException e) {
			DialogUtil.logErrorException("解析"+file.getFullPath().toOSString()+"错误！",e);
		}finally{
			try {
				if(is != null)
					is.close();
			} catch (IOException e) {
				DialogUtil.logErrorException("关闭"+is+"错误！",e);
			}
		}
		document = tp.getInputDocument();
		;
		try {
			int offset = document.getLineOffset(cursorLine);
			int length = document.getLineLength(cursorLine);
			if(offset==0||length==0)
				return;
			tp.setHighlightRange(offset, length, true);
		} catch (BadLocationException e) {
			DialogUtil.logErrorException(e);
		}
		
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
