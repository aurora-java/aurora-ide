package aurora.ide.search.condition;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.text.IDocument;

import uncertain.composite.CompositeMap;
import aurora.ide.search.ui.LineElement;

public class SearchCondition {

	private boolean isCaseSensitive;

	private boolean isRegularExpression;

	public boolean isCaseSensitive() {
		return isCaseSensitive;
	}

	public void setCaseSensitive(boolean isCaseSensitive) {
		this.isCaseSensitive = isCaseSensitive;
	}

	public boolean isRegularExpression() {
		return isRegularExpression;
	}

	public void setRegularExpression(boolean isRegularExpression) {
		this.isRegularExpression = isRegularExpression;
	}

	public void store(IDialogSettings s) {

	}

	public SearchCondition read(IDialogSettings s) {
		return this;
	}

	public MatchInfo match(CompositeMap map, IDocument document, LineElement l) {
		return MatchInfo.Not_Match;
	}

	public boolean isEquals(SearchCondition condition) {
		return false;
	}
}
