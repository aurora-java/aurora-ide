package aurora.ide.search.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import aurora.ide.refactoring.RemoveElementRefactoring;
import aurora.ide.search.core.AbstractMatch;
import aurora.ide.search.core.AuroraMatch;
import aurora.ide.search.core.AuroraSearchResult;
import aurora.ide.search.core.CompositeMapInDocument;
import aurora.ide.search.core.CompositeMapInDocumentManager;
import aurora.ide.search.ui.LineElement;

public class RemoveElementAction extends AbstractSearchResultPageAction {

	private ArrayList<CompositeMapInDocument> infos;

	private IFile c_file;

	public RemoveElementAction(Shell shell) {
		super(shell);
	}

	protected Refactoring createRefactoring(List lines,
			IProgressMonitor monitor) {
		List<CompositeMapInDocument> infos = createInfo(lines, monitor);
		return new RemoveElementRefactoring(
				infos.toArray(new CompositeMapInDocument[infos.size()]));
	}

	private List<CompositeMapInDocument> createInfo(List lines,
			IProgressMonitor monitor) {
		infos = new ArrayList<CompositeMapInDocument>();
		for (Iterator it = lines.iterator(); it.hasNext();) {
			if (monitor.isCanceled()) {
				return infos;
			}
			Object next = it.next();
			if (next instanceof LineElement) {
				LineElement line = (LineElement) next;
				c_file = (IFile) line.getParent();
				CompositeMapInDocument info = toCompsiteMapInfo(line);
				if (info != null) {
					infos.add(info);
				}
			}
		}
		return infos;
	}

	private CompositeMapInDocument toCompsiteMapInfo(LineElement line) {
		AuroraSearchResult result = this.getSearchResult();
		AbstractMatch[] matches = line.getMatches(result);
		if (matches.length > 0) {
			AuroraMatch am = (AuroraMatch) matches[0];
			CompositeMap map = am.getMatchs().getMap();
			IFile file = (IFile) line.getParent();
			try {
				IDocument document = this.getDocument(file);
				CompositeMapInDocument info = CompositeMapInDocumentManager
						.getCompositeMapInDocument(map, document);
				if (info == null) {
					// may be do sth;
					// System.out.println(file.getFullPath());
				} else
					info.setFile(file);
				return info;
			} catch (CoreException e) {
			}
		}
		return null;
	}

	@Override
	protected int getInfoSize() {
		return this.infos == null ? 0 : infos.size();
	}

	@Override
	protected String getSubTaskName() {
		return this.c_file == null ? "" : c_file.getName();
	}

}
