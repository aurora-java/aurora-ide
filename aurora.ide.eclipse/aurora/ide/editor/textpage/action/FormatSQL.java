package aurora.ide.editor.textpage.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import aurora.ide.editor.textpage.TextPage;
import aurora.ide.editor.textpage.format.SQLFormat;
import aurora.ide.helpers.DialogUtil;

public class FormatSQL implements IEditorActionDelegate {
	IEditorPart activeEditor;

	public void run(IAction action) {
		if (activeEditor == null || !(activeEditor instanceof TextPage)) {
			DialogUtil.showErrorMessageBox("这个类不是" + TextPage.class.getName());
			return;
		}
		TextPage tp = (TextPage) activeEditor;
		IDocument document = tp.getInputDocument();
		int cursorLine = tp.getCursorLine();
		try {
			int x = tp.getSelectedRange().x;
			if (x <= 0)
				return;
			ITypedRegion region = document.getPartition(x);
			ITypedRegion parentRegion = document.getPartition(region.getOffset() - 1);
			String parentNode = document.get(parentRegion.getOffset(), parentRegion.getLength());
			int startLine = document.getLineOfOffset(parentRegion.getOffset());
			String txt = document.get(document.getLineOffset(startLine), document.getLineLength(startLine));
			int index = txt.toLowerCase().indexOf("<");
			if (index <= 0)
				return;
			String prefix = txt.substring(0, index);
			if (!parentNode.matches("<[^/].+sql>")) {
				DialogUtil.showErrorMessageBox("此区域非SQL代码");
				return;
			}
			int begin = region.getOffset() + "<![CDATA[".length();
			int length = region.getLength() - "<![CDATA[".length() - "]]>".length();
			String sqlCode = document.get(begin, length);
			SQLFormat sf = new SQLFormat();
			String[] temp = sf.format(sqlCode).split("\n|\r\n");
			StringBuffer result = new StringBuffer("\n");
			for (String s : temp) {
				result.append(prefix + "    " + s + " \n");
			}
			result.append(prefix);
			document.replace(begin, length, result.toString());
		} catch (Exception e) {
			DialogUtil.showExceptionMessageBox(e);
			return;
		}
		try {
			int offset = document.getLineOffset(cursorLine);
			int length = document.getLineLength(cursorLine);
			if (offset == 0 || length == 0)
				return;
			tp.setHighlightRange(offset, length, true);
		} catch (BadLocationException e) {
			DialogUtil.logErrorException(e);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		activeEditor = targetEditor;
	}

}
