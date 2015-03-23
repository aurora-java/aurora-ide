package aurora.ide.search.condition;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import uncertain.composite.CompositeMap;
import aurora.ide.editor.textpage.IColorConstants;
import aurora.ide.search.core.Util;
import aurora.ide.search.ui.LineElement;

public class ElementCondition extends SearchCondition {

	private String elementName;

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	@Override
	public SearchCondition read(IDialogSettings s) {
		this.setCaseSensitive(s.getBoolean("isCaseSensitive"));
		this.setElementName(s.get("elementName"));
		return this;
	}

	public MatchInfo match(CompositeMap map, IDocument document, LineElement l) {
		if ("".equals(elementName)) {
			return MatchInfo.Not_Match;
		}
		boolean stringMatch = Util.stringMatch(elementName, map.getName(),
				this.isCaseSensitive(), this.isRegularExpression());
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
	public void store(IDialogSettings s) {
		s.put("isCaseSensitive", this.isCaseSensitive());
		if (elementName != null)
			s.put("elementName", elementName);
	}

	public boolean isEquals(SearchCondition condition) {
		if (condition instanceof ElementCondition) {
			String _name = elementName == null ? "" : elementName;
			return _name.equals(((ElementCondition) condition).elementName);
		}
		return super.isEquals(condition);
	}

}
