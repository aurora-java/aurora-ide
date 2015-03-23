package aurora.ide.editor.textpage.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import aurora.ide.AuroraPlugin;
import aurora.ide.editor.BaseCompositeMapEditor;
import aurora.ide.editor.textpage.TextPage;
import aurora.ide.editor.textpage.scanners.XMLPartitionScanner;

public class ToggleBlockCommentAction extends Action implements
		IEditorActionDelegate {
	IEditorPart activeEditor;

	public ToggleBlockCommentAction() {
		setActionDefinitionId("aurora.ide.toggleblockcomment");
	}

	public void run() {
		try {
			comment((TextPage) ((BaseCompositeMapEditor) AuroraPlugin
					.getActivePage().getActiveEditor()).getActiveEditor());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void comment(TextPage page) throws Exception {
		Point sRange = page.getSelectedRange();
		if (sRange.y == 0)
			return;
		IDocument doc = page.getInputDocument();
		ITypedRegion partitionRegion = doc.getPartition(sRange.x);
		String pType = partitionRegion.getType();
		if (XMLPartitionScanner.XML_CDATA.equals(pType)) {
			doBlockComment(page, "/*", "*/");
		} else {
			doBlockComment(page, "<!--", "-->");
		}
	}

	private void doBlockComment(TextPage page, String prefix, String sufix)
			throws Exception {
		Point sRange = page.getSelectedRange();
		IDocument doc = page.getInputDocument();
		String text = doc.get(sRange.x, sRange.y);
		if (isCommentOf(text, prefix, sufix)) {
			StringBuilder sb = new StringBuilder(text);
			int idx = sb.indexOf(prefix + " ");
			if (idx == -1) {
				idx = sb.indexOf(prefix);
				sb.delete(idx, idx + prefix.length());
			} else {
				sb.delete(idx, idx + prefix.length() + 1);
			}
			idx = sb.lastIndexOf(" " + sufix);
			if (idx == -1) {
				idx = sb.lastIndexOf(sufix);
				sb.delete(idx, idx + sufix.length());
			} else {
				sb.delete(idx, idx + sufix.length() + 1);
			}
			doc.replace(sRange.x, sRange.y, sb.toString());
			page.getSelectionProvider().setSelection(
					new TextSelection(sRange.x, sRange.y
							- (text.length() - sb.length())));
		} else {
			String textNew = prefix + " " + text + " " + sufix;
			doc.replace(sRange.x, sRange.y, textNew);
			page.getSelectionProvider().setSelection(
					new TextSelection(sRange.x, sRange.y
							+ (textNew.length() - text.length())));
		}
	}

	/*
	 * 不对嵌套的注释(非法)进行检测,仅检测两端是否符合要求
	 */
	private boolean isCommentOf(String text, String p, String s) {
		int idx = text.indexOf(p);
		if (idx == -1)
			return false;
		if (text.substring(0, idx).trim().length() > 0)
			return false;
		idx = text.lastIndexOf(s);
		if (idx == -1)
			return false;
		if (text.substring(idx + s.length()).trim().length() > 0)
			return false;
		return true;
	}

	public void run(IAction action) {
		if (activeEditor == null || !(activeEditor instanceof TextPage)) {
			return;
		}
		TextPage tp = (TextPage) activeEditor;
		try {
			comment(tp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		action.setEnabled(false);
		if (!(selection instanceof TextSelection))
			return;
		String text = ((TextSelection) selection).getText();
		if (text == null || text.length() == 0)
			return;
		action.setEnabled(true);
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.activeEditor = targetEditor;
	}
}
