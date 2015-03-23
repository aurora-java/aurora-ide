package aurora.ide.search.condition;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import uncertain.composite.CompositeMap;
import aurora.ide.editor.textpage.IColorConstants;
import aurora.ide.search.core.Util;
import aurora.ide.search.ui.LineElement;

public class NameSpaceCondition extends SearchCondition {
	private String nameSpace;

	public String getNameSpace() {
		return nameSpace;
	}

	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}

	public MatchInfo match(CompositeMap map, IDocument document, LineElement l) {
		if ("".equals(nameSpace)) {
			return MatchInfo.Not_Match;
		}
		boolean stringMatch = Util
				.stringMatch(nameSpace, map.getNamespaceURI(),this.isCaseSensitive(),this.isRegularExpression());
		if (stringMatch) {
			MatchInfo info = new MatchInfo();
			String rawName = map.getRawName();
			info.setMap(map);
			try {
				IRegion documentRegion = Util.getDocumentRegion(l.getOffset(),
						l.getLength(), rawName, document,
						IColorConstants.TAG_NAME);
				info.addRegion(documentRegion);
				return info;
			} catch (BadLocationException e) {
			}
		}
		return MatchInfo.Not_Match;
	}

	@Override
	public SearchCondition read(IDialogSettings s) {
		this.setCaseSensitive(s.getBoolean("isCaseSensitive"));
		this.setNameSpace(s.get("namespace"));
		return this;
	}

	@Override
	public void store(IDialogSettings s) {
		s.put("isCaseSensitive", this.isCaseSensitive());
		if (nameSpace != null)
			s.put("namespace", nameSpace);
	}

	public boolean isEquals(SearchCondition condition) {
		if (condition instanceof NameSpaceCondition) {
			String _name = nameSpace == null ? "" : nameSpace;
			return _name.equals(((NameSpaceCondition) condition).nameSpace);
		}
		return super.isEquals(condition);
	}

}
