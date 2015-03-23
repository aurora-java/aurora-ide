package aurora.ide.editor.textpage.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import uncertain.composite.CompositeMap;
import aurora.ide.AuroraPlugin;
import aurora.ide.api.composite.map.CommentXMLOutputter;
import aurora.ide.builder.CompositeMapInfo;
import aurora.ide.editor.BaseCompositeMapEditor;
import aurora.ide.editor.textpage.TextPage;
import aurora.ide.editor.textpage.format.JSBeautifier;
import aurora.ide.editor.textpage.format.SQLFormat;
import aurora.ide.editor.textpage.quickfix.QuickAssistUtil;
import aurora.ide.helpers.AuroraResourceUtil;

public class CFormatAction extends Action implements IEditorActionDelegate {
	private static final int XML = 0;
	private static final int SQL = 1;
	private static final int JS = 2;

	private IEditorPart activeEditor;
	private FormatJS formatJS;
	private FormatSQL formatSQL;
	private CompositeMap selectMap;
	private IDocument doc;
	private TextPage page;

	public CFormatAction() {
		this.setActionDefinitionId("aurora.ide.format");
		formatJS = new FormatJS();
		formatSQL = new FormatSQL();
	}

	@Override
	public void run() {
		activeEditor = (TextPage) ((BaseCompositeMapEditor) AuroraPlugin
				.getActivePage().getActiveEditor()).getActiveEditor();
		page = (TextPage) activeEditor;
		try {
			switch (getSelectionType()) {
			case JS:
				formatJS();
				// formatJS.setActiveEditor(null, activeEditor);
				// formatJS.run(null);
				break;
			case SQL:
				formatSQL();
				// /formatSQL.setActiveEditor(null, activeEditor);
				// formatSQL.run(null);
				break;
			case XML:
				formatXML();
				break;
			}
		} catch (Exception e) {
		}
	}

	private int getSelectionType() throws Exception {
		doc = page.getInputDocument();
		CompositeMap rootMap = page.toCompoisteMap();
		selectMap = QuickAssistUtil.findMap(rootMap, doc,
				page.getSelectedRange().x);
		String mapName = selectMap.getName();
		if (mapName.toLowerCase().matches(".*script"))
			return JS;
		if (mapName.toLowerCase().matches(".+-sql"))
			return SQL;
		return XML;
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		// activeEditor = targetEditor;
		// formatJS.setActiveEditor(action, targetEditor);
	}

	private void formatXML() {
		String content = doc.get();
		if (content == null) {
			return;
		}
		int offset = page.getSelectedRange().x;
		try {
			CompositeMap data = page.toCompoisteMap();
			String formatContent = AuroraResourceUtil.xml_decl
					+ CommentXMLOutputter.defaultInstance().toXML(data, true);
			if (content.equals(formatContent))
				return;
			page.refresh(formatContent);
			if (offset >= formatContent.length())
				offset = formatContent.length();
			page.setHighlightRange(offset, 0, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void formatJS() {
		int offset = page.getSelectedRange().x;
		CompositeMapInfo info = new CompositeMapInfo(selectMap, doc);
		IRegion startRegion = info.getStartTagRegion();
		IRegion endRegion = info.getEndTagRegion();
		int jsOffset = startRegion.getOffset() + startRegion.getLength()
				+ "<![CDATA[".length();
		int jsLength = endRegion.getOffset() - "]]>".length() - jsOffset;
		String jsCode;
		try {
			jsCode = doc.get(jsOffset, jsLength);
			if (jsCode == null || jsCode.trim().length() == 0)
				return;
			String prefix = info.getLeadPrefix();
			JSBeautifier bf = new JSBeautifier();
			String indent = CommentXMLOutputter.DEFAULT_INDENT + prefix;
			String formatedJs = bf.beautify(jsCode.trim(), bf.opts).trim();
			String jsCodeNew = ("\n" + formatedJs).replaceAll("\n",
					CommentXMLOutputter.LINE_SEPARATOR + indent)
					+ CommentXMLOutputter.LINE_SEPARATOR + prefix;
			if (jsCodeNew.equals(jsCode)) {
				return;
			}
			doc.replace(jsOffset, jsLength, jsCodeNew);
			page.setHighlightRange(offset, 0, true);
		} catch (BadLocationException e) {
			e.printStackTrace();
			return;
		}
	}

	private void formatSQL() {
		int offset = page.getSelectedRange().x;
		CompositeMapInfo info = new CompositeMapInfo(selectMap, doc);
		IRegion startRegion = info.getStartTagRegion();
		IRegion endRegion = info.getEndTagRegion();
		int sqlOffset = startRegion.getOffset() + startRegion.getLength()
				+ "<![CDATA[".length();
		int sqlLength = endRegion.getOffset() - "]]>".length() - sqlOffset;
		String sqlCode;
		try {
			sqlCode = doc.get(sqlOffset, sqlLength);
			if (sqlCode == null || sqlCode.trim().length() == 0)
				return;
			String prefix = info.getLeadPrefix();
			String indent = CommentXMLOutputter.DEFAULT_INDENT + prefix;
			SQLFormat sf = new SQLFormat();
			String sqlCodeNew = sf.format(sqlCode);
			StringBuilder sb = new StringBuilder(5000);
			sb.append(CommentXMLOutputter.LINE_SEPARATOR);
			for (String line : sqlCodeNew.split("\n|\r\n")) {
				sb.append(indent + line + CommentXMLOutputter.LINE_SEPARATOR);
			}
			sb.append(prefix);
			sqlCodeNew = sb.toString();
			if (sqlCodeNew.equals(sqlCode)) {
				return;
			}
			doc.replace(sqlOffset, sqlLength, sqlCodeNew);
			page.setHighlightRange(offset, 0, true);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}
}
