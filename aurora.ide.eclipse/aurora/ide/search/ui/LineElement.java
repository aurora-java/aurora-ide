package aurora.ide.search.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.Match;

import aurora.ide.search.core.AbstractMatch;

public class LineElement {

	private final IResource fParent;

	private final int fLineNumber;
	private final int fLineStartOffset;
	private final String fLineContents;

	public LineElement(IResource parent, int lineNumber, int lineStartOffset,
			String lineContents) {
		fParent = parent;
		fLineNumber = lineNumber;
		fLineStartOffset = lineStartOffset;
		fLineContents = lineContents;
	}


	public IResource getParent() {
		return fParent;
	}

	public int getLine() {
		return fLineNumber;
	}

	public String getContents() {
		return fLineContents;
	}

	public int getOffset() {
		return fLineStartOffset;
	}

	public boolean contains(int offset) {
		return fLineStartOffset <= offset
				&& offset < fLineStartOffset + fLineContents.length();
	}

	public int getLength() {
		return fLineContents.length();
	}

	public AbstractMatch[] getMatches(AbstractTextSearchResult result) {
		List<AbstractMatch> res = new ArrayList<AbstractMatch> ();
		Match[] matches = result.getMatches(fParent);
		for (int i = 0; i < matches.length; i++) {
			AbstractMatch curr = (AbstractMatch) matches[i];
			if (curr.getLineElement() == this) {
				res.add(curr);
			}
		}
		return res.toArray(new AbstractMatch[res.size()]);
	}

	public int getNumberOfMatches(AbstractTextSearchResult result) {
		int count = 0;
		Match[] matches = result.getMatches(fParent);
		for (int i = 0; i < matches.length; i++) {
			AbstractMatch curr = (AbstractMatch) matches[i];
			if (curr.getLineElement() == this) {
				count++;
			}
		}
		return count;
	}

}
