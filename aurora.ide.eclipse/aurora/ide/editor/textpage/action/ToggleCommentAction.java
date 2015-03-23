package aurora.ide.editor.textpage.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import aurora.ide.AuroraPlugin;
import aurora.ide.api.composite.map.CommentXMLOutputter;
import aurora.ide.builder.RegionUtil;
import aurora.ide.editor.BaseCompositeMapEditor;
import aurora.ide.editor.textpage.TextPage;
import aurora.ide.editor.textpage.scanners.XMLPartitionScanner;

public class ToggleCommentAction extends Action implements
		IEditorActionDelegate {

	IEditorPart activeEditor;
	ISelection selection;

	public ToggleCommentAction() {
		setActionDefinitionId("aurora.ide.togglecomment");
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		activeEditor = targetEditor;
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

	private void comment(TextPage page) throws Exception {
		ISelectionProvider selectionProvider = page.getSelectionProvider();
		IDocument doc = page.getInputDocument();
		Point sRange = page.getSelectedRange();
		ITypedRegion partitionRegion = doc.getPartition(sRange.x);
		String pType = partitionRegion.getType();
		if (XMLPartitionScanner.XML_CDATA.equals(pType)) {
			if (!RegionUtil.isSubRegion(partitionRegion, new Region(sRange.x,
					sRange.y)))
				return;
			ITypedRegion pRegion = doc
					.getPartition(partitionRegion.getOffset() - 1);
			String pNode = get(doc, pRegion).toLowerCase();
			if (pNode.matches("<.*script( .*){0,1}>")) {
				doLineComment("//", page);
			} else if (pNode.matches(".+-sql.+")) {
				doLineComment("--", page);
			}
		}
		if (sRange.y > 7) {// try uncomment selected text via a simple way
			StringBuilder sb = new StringBuilder(get(doc, sRange));
			if (sb.subSequence(0, 4).equals("<!--")
					&& sb.subSequence(sb.length() - 3, sb.length()).equals(
							"-->")) {
				int[] delt = uncommentXML(sb, 0, sRange.y);
				set(doc, sb.toString(), sRange);
				select(selectionProvider, sRange.x + delt[1], delt[2]);
				return;
			}
		}

		if (XMLPartitionScanner.XML_START_TAG.equals(pType)
				|| XMLPartitionScanner.XML_END_TAG.equals(pType)) {
			if (sRange.y == 0
					|| RegionUtil.isSubRegion(partitionRegion, new Region(
							sRange.x, sRange.y))) {
				String tagText = get(doc, partitionRegion);
				set(doc, String.format("<!-- %s -->", tagText), partitionRegion);
				select(selectionProvider, sRange.x + 5, sRange.y);
				return;
			}
			commentSelectedXML(doc, selectionProvider, sRange.x, sRange.y);
		} else if (XMLPartitionScanner.XML_COMMENT.equals(pType)) {
			if (sRange.y == 0) {
				int sIC = sRange.x - partitionRegion.getOffset();
				int offset = partitionRegion.getOffset();
				int length = partitionRegion.getLength();
				StringBuilder sb = new StringBuilder(doc.get(offset, length));
				int[] delt = uncommentXML(sb, sIC, sIC);
				doc.replace(offset, length, sb.toString());
				select(selectionProvider, sRange.x + delt[1], 0);
				return;
			}
			if (RegionUtil.isSubRegion(partitionRegion, new Region(sRange.x,
					sRange.y))) {
				StringBuilder sb = new StringBuilder(get(doc, partitionRegion));
				int sIC = sRange.x - partitionRegion.getOffset();
				int eIC = sIC + sRange.y;
				int[] delt = uncommentXML(sb, sIC, eIC);
				set(doc, sb.toString(), partitionRegion);
				select(selectionProvider, sRange.x + delt[1], delt[2]);
				return;
			}
			if (sRange.x == partitionRegion.getOffset()) {
				ITypedRegion[] regions = doc.computePartitioning(sRange.x,
						sRange.y);
				if (regions.length > 2 || !isWhiteRegion(doc, regions[1])) {
					commentSelectedXML(doc, selectionProvider, sRange.x,
							sRange.y);
					return;
				}
				StringBuilder sb = new StringBuilder(get(doc, regions[0]));
				int[] delt = uncommentXML(sb, 0, sRange.y);
				set(doc, sb.toString(), regions[0]);
				select(selectionProvider, sRange.x, delt[2]);
				return;
			}
			commentSelectedXML(doc, selectionProvider, sRange.x, sRange.y);
		} else if (IDocument.DEFAULT_CONTENT_TYPE.equals(pType)) {
			if (sRange.y == 0)
				return;
			IRegion sRegion = new Region(sRange.x, sRange.y);
			if (RegionUtil.isSubRegion(partitionRegion, sRegion)) {
				commentSelectedXML(doc, selectionProvider, sRange.x, sRange.y);
				return;
			} else {
				ITypedRegion[] regions = doc.computePartitioning(
						partitionRegion.getOffset(), sRange.x + sRange.y
								- partitionRegion.getOffset());
				// regions.length can never be 1
				if (regions.length > 3 || !isWhiteRegion(doc, regions[0])) {
					commentSelectedXML(doc, selectionProvider, sRange.x,
							sRange.y);
					return;
				}
				if (regions.length == 3 && !isWhiteRegion(doc, regions[2])) {
					commentSelectedXML(doc, selectionProvider, sRange.x,
							sRange.y);
					return;
				}
				if (!doc.getPartition(regions[1].getOffset())
						.equals(regions[1])) {
					commentSelectedXML(doc, selectionProvider, sRange.x,
							sRange.y);
					return;
				}
				String type = regions[1].getType();
				if (XMLPartitionScanner.XML_COMMENT.equals(type)) {
					StringBuilder sb = new StringBuilder(get(doc, sRange));
					int[] delt = uncommentXML(sb, 0, sRange.y);
					set(doc, sb.toString(), sRange);
					select(selectionProvider, sRange.x + delt[1], delt[2]);
				} else
					commentSelectedXML(doc, selectionProvider, sRange.x,
							sRange.y);
			}
		}
	}

	private void commentSelectedXML(IDocument doc, ISelectionProvider sp,
			int offset, int length) throws Exception {
		String text = doc.get(offset, length);
		String textNew = String.format("<!-- %s -->", text);
		doc.replace(offset, length, textNew);
		select(sp, offset, length + 9);
	}

	private boolean isWhiteRegion(IDocument doc, ITypedRegion tRegion)
			throws Exception {
		if (!IDocument.DEFAULT_CONTENT_TYPE.equals(tRegion.getType()))
			return false;
		for (char c : get(doc, tRegion).toCharArray())
			if (!Character.isWhitespace(c))
				return false;
		return true;
	}

	public void run() {
		try {
			comment((TextPage) ((BaseCompositeMapEditor) AuroraPlugin
					.getActivePage().getActiveEditor()).getActiveEditor());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	/**
	 * 
	 * @param sb
	 * @param sIC
	 *            选区在字符串中的起始位置(相对字符串)
	 * @param eIC
	 *            选区在字符串中的结束位置(相对字符串)
	 * @return 数组delt,3个元素:<br/>
	 *         1. 长度减少值(绝对值)<br/>
	 *         2. 光标位置变化值(相对)<br/>
	 *         3. 选区长度(绝对)
	 */
	private int[] uncommentXML(StringBuilder sb, int sIC, int eIC) {
		int idx1 = sb.indexOf("<!-- ");
		int[] delt = { 0, 0, 0 };
		int sl = 5, el = 4;
		if (idx1 != -1) {
			sb.delete(idx1, idx1 + 5);
		} else {
			idx1 = sb.indexOf("<!--");
			sb.delete(idx1, idx1 + 4);
			sl = 4;
		}
		int idx2 = sb.lastIndexOf(" -->");
		if (idx2 != -1) {
			sb.delete(idx2, idx2 + 4);
		} else {
			idx2 = sb.lastIndexOf("-->");
			sb.delete(idx2, idx2 + 3);
			el = 3;
		}
		idx2 += sl;
		delt[0] = sl + el;
		if (sIC <= idx1 + sl) {
			delt[1] = idx1 - sIC;
			if (eIC > idx1 + sl)
				delt[2] = Math.min(eIC, idx2) - (idx1 + sl);
		} else if (sIC <= idx2) {
			delt[1] = -sl;
			delt[2] = Math.min(eIC, idx2) - sIC;
		} else {
			delt[1] = -sl + idx2 - sIC;
		}
		return delt;
	}

	private String getTextOfLine(IDocument doc, int line) throws Exception {
		IRegion region = doc.getLineInformation(line);
		return doc.get(region.getOffset(), region.getLength());
	}

	private String commentSingleLine(String lineText, String prefix) {
		StringBuilder sb = new StringBuilder(lineText.length() + 10);
		sb.append(lineText);
		for (int i = 0; i < lineText.length(); i++) {
			char c = sb.charAt(i);
			if (!Character.isWhitespace(c)) {
				sb.insert(i, prefix + " ");
				break;
			}
		}
		return sb.toString();
	}

	private String uncommentSingleLine(String lineText, String prefix) {
		StringBuilder sb = new StringBuilder(lineText.length());
		sb.append(lineText);
		int idx = sb.indexOf(prefix);
		if (idx == -1)
			return lineText;
		for (int i = 0; i < prefix.length(); i++)
			sb.deleteCharAt(idx);
		if (sb.charAt(idx) == ' ')
			sb.deleteCharAt(idx);
		return sb.toString();
	}

	private void doLineComment(String prefix, TextPage page) throws Exception {
		IDocument doc = page.getInputDocument();
		Point sRange = page.getSelectedRange();
		int startLine = doc.getLineOfOffset(sRange.x);
		int startOffsetInLine = sRange.x - doc.getLineOffset(startLine);
		ITypedRegion partitionRegion = doc.getPartition(sRange.x);
		if (sRange.y == 0) {
			String text = getTextOfLine(doc, startLine);
			String textNew = null;
			boolean tc = isCommentOf(text, prefix);
			textNew = tc ? uncommentSingleLine(text, prefix)
					: commentSingleLine(text, prefix);
			int delt[] = computeDelt(text, textNew, prefix, startOffsetInLine);
			doc.replace(doc.getLineOffset(startLine), text.length(), textNew);
			page.getSelectionProvider().setSelection(
					new TextSelection(sRange.x + delt[0], 0));
			return;
		}
		if (partitionRegion.getOffset() + partitionRegion.getLength() < sRange.x
				+ sRange.y) {
			return;
		}
		int endLine = doc.getLineOfOffset(sRange.x + sRange.y);
		if (doc.getLineOffset(endLine) == sRange.x + sRange.y)
			endLine--;
		int startOffset = doc.getLineOffset(startLine);
		IRegion endLineRegion = doc.getLineInformation(endLine);
		int length = endLineRegion.getOffset() + endLineRegion.getLength()
				- startOffset;
		int endOffsetInLine = sRange.x + sRange.y - doc.getLineOffset(endLine);
		String text = doc.get(startOffset, length).replace("\r\n", "\n")
				.replace("\r", "\n");
		String[] ss = text.split("\n");
		StringBuilder sb = new StringBuilder(ss.length * 4 + text.length());
		boolean ct = isCommentOf(ss, prefix);
		String textNew = ct ? uncommentSingleLine(ss[0], prefix)
				: commentSingleLine(ss[0], prefix);

		int[] delt = computeDelt(ss[0], textNew, prefix, startOffsetInLine);
		int offsetDelt = delt[0];
		int lengthDelt = delt[1];
		sb.append(textNew);
		if (ss.length > 1) {
			sb.append(CommentXMLOutputter.LINE_SEPARATOR);
			for (int i = 1; i < ss.length - 1; i++) {
				textNew = ct ? uncommentSingleLine(ss[i], prefix)
						: commentSingleLine(ss[i], prefix);
				sb.append(textNew);
				sb.append(CommentXMLOutputter.LINE_SEPARATOR);
				lengthDelt += (textNew.length() - ss[i].length());
			}
			text = ss[ss.length - 1];
			textNew = ct ? uncommentSingleLine(text, prefix)
					: commentSingleLine(text, prefix);
			sb.append(textNew);
			delt = computeDelt(text, textNew, prefix, endOffsetInLine);
			lengthDelt += delt[0];
		}
		doc.replace(startOffset, length, sb.toString());
		page.getSelectionProvider()
				.setSelection(
						new TextSelection(sRange.x + offsetDelt, sRange.y
								+ lengthDelt));
	}

	private boolean isCommentOf(String lineText, String prefix) {
		int idx = lineText.indexOf(prefix);
		if (idx == -1)
			return false;
		for (int i = 0; i < idx; i++) {
			if (!Character.isWhitespace(lineText.charAt(i)))
				return false;
		}
		return true;
	}

	private boolean isCommentOf(String[] ss, String prefix) {
		for (String s : ss) {
			if (s.trim().length() == 0)
				continue;
			if (!isCommentOf(s, prefix))
				return false;
		}
		return true;
	}

	private int getWspLength(String lineText) {
		int i = 0;
		for (; i < lineText.length(); i++) {
			if (!Character.isWhitespace(lineText.charAt(i)))
				return i;
		}
		return i;
	}

	private int getWapLength(String text, String prefix) {
		int idx = text.indexOf(prefix);
		idx += prefix.length();
		if (Character.isWhitespace(text.charAt(idx)))
			return idx + 1;
		return idx;
	}

	/**
	 * 
	 * @param text
	 * @param textNew
	 * @param prefix
	 * @param offsetIL
	 * @return delt[0]:caretDelt<br/>
	 *         delt[1]:lengthDelt
	 */
	private int[] computeDelt(String text, String textNew, String prefix,
			int offsetIL) {
		int[] delt = new int[] { 0, 0 };
		int l1 = text.length();
		int l2 = textNew.length();
		int wsp = getWspLength(text);
		if (l1 < l2)/* add comment */{
			if (wsp <= offsetIL) {
				delt[0] = l2 - l1;
			} else {
				delt[1] = l2 - l1;
			}
		} else/* remove comment */{
			int wap = getWapLength(text, prefix);
			if (wsp >= offsetIL) {
				delt[1] = l2 - l1;
			} else if (wap >= offsetIL) {
				delt[0] = wsp - offsetIL;
				delt[1] = offsetIL - wap;
			} else {
				delt[0] = l2 - l1;
			}
		}
		return delt;
	}

	protected String get(IDocument doc, Object region) throws Exception {
		if (region instanceof IRegion) {
			IRegion r = (IRegion) region;
			return doc.get(r.getOffset(), r.getLength());
		} else if (region instanceof Point) {
			Point p = (Point) region;
			return doc.get(p.x, p.y);
		}
		throw new RuntimeException("Unknow argument type:" + region.getClass());
	}

	protected void set(IDocument doc, String str, Object region)
			throws Exception {
		if (region instanceof IRegion) {
			IRegion r = (IRegion) region;
			doc.replace(r.getOffset(), r.getLength(), str);
		} else if (region instanceof Point) {
			Point p = (Point) region;
			doc.replace(p.x, p.y, str);
		} else
			throw new RuntimeException("Unknow argument type:"
					+ region.getClass());
	}

	protected void select(ISelectionProvider sp, Object... region) {
		if (region.length == 1) {
			if (region[0] instanceof IRegion) {
				IRegion r = (IRegion) region[0];
				sp.setSelection(new TextSelection(r.getOffset(), r.getLength()));
			} else if (region[0] instanceof Point) {
				Point p = (Point) region[0];
				sp.setSelection(new TextSelection(p.x, p.y));
			}
		} else if (region.length == 2) {
			sp.setSelection(new TextSelection((Integer) region[0],
					(Integer) region[1]));
		} else
			throw new RuntimeException("Unknow argument type:"
					+ region.getClass());
	}
}
