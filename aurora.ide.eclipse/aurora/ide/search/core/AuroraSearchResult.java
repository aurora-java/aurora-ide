package aurora.ide.search.core;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.IEditorMatchAdapter;
import org.eclipse.search.ui.text.IFileMatchAdapter;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

public class AuroraSearchResult extends AbstractSearchResult implements
		IEditorMatchAdapter, IFileMatchAdapter {
	private ISearchQuery query;
	private final Match[] EMPTY_ARR = new Match[0];

	public AuroraSearchResult(ISearchQuery query) {
		super();
		this.query = query;
	}

	public String getLabel() {
		int matchCount = getMatchCount();
		if (query instanceof AbstractSearchQuery) {
			Object pattern = ((AbstractSearchQuery) query).getPattern();

			if (pattern != null) {
				String in = ((AbstractSearchQuery) query).getSearchInLabel();
				String[] args = { pattern.toString(),
						String.valueOf(matchCount), in };
				return MessageFormat.format(Message.result_label, args);
			}
		}
		return "" + matchCount + " matches";
	}

	public String getTooltip() {

		return this.getLabel();
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public ISearchQuery getQuery() {

		return query;
	}

	public IEditorMatchAdapter getEditorMatchAdapter() {
		return this;
	}

	public IFileMatchAdapter getFileMatchAdapter() {
		return this;
	}

	public Match[] computeContainedMatches(AbstractTextSearchResult result,
			IFile file) {
		return getMatches(file);
	}

	public IFile getFile(Object element) {
		if (element instanceof IFile)
			return (IFile) element;
		return null;
	}

	public boolean isShownInEditor(Match match, IEditorPart editor) {
		IEditorInput ei = editor.getEditorInput();
		if (ei instanceof IFileEditorInput) {
			IFileEditorInput fi = (IFileEditorInput) ei;
			return match.getElement().equals(fi.getFile());
		}
		return false;
	}

	public Match[] computeContainedMatches(AbstractTextSearchResult result,
			IEditorPart editor) {
		IEditorInput ei = editor.getEditorInput();
		if (ei instanceof IFileEditorInput) {
			IFileEditorInput fi = (IFileEditorInput) ei;
			return getMatches(fi.getFile());
		}
		return EMPTY_ARR;
	}

}
