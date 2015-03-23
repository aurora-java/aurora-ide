package aurora.ide.editor.textpage.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import aurora.ide.api.composite.map.CommentXMLOutputter;
import aurora.ide.editor.textpage.TextPage;
import aurora.ide.editor.textpage.format.JSBeautifier;
import aurora.ide.editor.textpage.scanners.XMLPartitionScanner;
import aurora.ide.helpers.DialogUtil;

public class FormatJS implements IEditorActionDelegate {

	IEditorPart activeEditor;

	public FormatJS() {
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		activeEditor = targetEditor;
	}

	public void run(IAction action) {
		if (activeEditor == null || !(activeEditor instanceof TextPage)) {
			return;
		}
		TextPage tp = (TextPage) activeEditor;
		IDocument document = tp.getInputDocument();
		int offset = tp.getSelectedRange().x;
		try {
			if (offset <= 0)
				return;
			ITypedRegion region = document.getPartition(offset);
			ITypedRegion parentRegion = document.getPartition(region
					.getOffset() - 1);
			String parentNode = document.get(parentRegion.getOffset(),
					parentRegion.getLength());
			int startLine = document.getLineOfOffset(parentRegion.getOffset());
			String txt = document.get(document.getLineOffset(startLine),
					document.getLineLength(startLine));
			int index = txt.toLowerCase().indexOf("<script");
			if (index <= 0)
				return;
			String prefix = txt.substring(0, index);
			if (!XMLPartitionScanner.XML_CDATA.equals(region.getType())
					|| !parentNode.toLowerCase().matches("<script( .*){0,1}>")) {
				DialogUtil.showErrorMessageBox("此区域非javascript代码");
				return;
			}

			int begin = region.getOffset() + "<![CDATA[".length();
			int length = region.getLength() - "<![CDATA[".length()
					- "]]>".length();
			String jsCode = document.get(begin, length);
			if (jsCode == null || jsCode.trim().length() == 0)
				return;
			JSBeautifier bf = new JSBeautifier();
			String indent = CommentXMLOutputter.DEFAULT_INDENT + prefix;
			String jsCodeNew = (CommentXMLOutputter.LINE_SEPARATOR + bf.beautify(
					jsCode, bf.opts)).replaceAll("\n",
							CommentXMLOutputter.LINE_SEPARATOR + indent)
					+ CommentXMLOutputter.LINE_SEPARATOR + prefix;
			if (jsCodeNew.equals(jsCode))
				return;
			document.replace(begin, length, jsCodeNew);
			tp.setHighlightRange(offset, 0, true);
		} catch (Throwable e) {
			DialogUtil.showExceptionMessageBox(e);
			return;
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
