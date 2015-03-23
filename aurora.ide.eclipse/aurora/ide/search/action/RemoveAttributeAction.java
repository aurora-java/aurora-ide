package aurora.ide.search.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import uncertain.composite.CompositeMap;
import aurora.ide.editor.textpage.IColorConstants;
import aurora.ide.refactoring.RefactoringReplaceInfo;
import aurora.ide.refactoring.ReplaceRefactoring;
import aurora.ide.search.core.AbstractMatch;
import aurora.ide.search.core.AuroraMatch;
import aurora.ide.search.core.AuroraSearchResult;
import aurora.ide.search.core.Util;
import aurora.ide.search.ui.LineElement;

public class RemoveAttributeAction extends AbstractSearchResultPageAction {
	private String s_name;

	private ArrayList<RefactoringReplaceInfo> infos;

	private IFile c_file;

	public RemoveAttributeAction(Shell shell) {
		super(shell);
	}

	public void setControl(final Text name) {
		s_name = name.getText().trim();

		name.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				s_name = name.getText().trim();
				notifyActionChanged();
			}
		});

	}

	@Override
	protected int getInfoSize() {
		return this.infos == null ? 0 : infos.size();
	}

	@Override
	protected String getSubTaskName() {
		return this.c_file == null ? "" : c_file.getName();
	}

	@Override
	protected Refactoring createRefactoring(List lines, IProgressMonitor monitor) {
		createInfo(lines, monitor);
		return new ReplaceRefactoring(
				infos.toArray(new RefactoringReplaceInfo[infos.size()]));
	}

	@Override
	public boolean isRefactorSelectionEnabled() {

		return !"".equals(s_name) && super.isRefactorSelectionEnabled();
	}

	@Override
	public boolean isRefactorAllEnabled() {

		return !"".equals(s_name) && super.isRefactorAllEnabled();
	}

	private void createInfo(List lines, IProgressMonitor monitor) {
		infos = new ArrayList<RefactoringReplaceInfo>();
		for (Iterator it = lines.iterator(); it.hasNext();) {
			if (monitor.isCanceled()) {
				return;
			}
			Object next = it.next();
			if (next instanceof LineElement) {
				LineElement line = (LineElement) next;
				c_file = (IFile) line.getParent();
				RefactoringReplaceInfo info = toRefactoringReplaceInfo(line);
				if (info != null) {
					infos.add(info);
				}
			}
		}
	}

	private RefactoringReplaceInfo toRefactoringReplaceInfo(LineElement line) {
		AuroraSearchResult result = this.getSearchResult();
		AbstractMatch[] matches = line.getMatches(result);
		if (matches.length > 0) {
			AuroraMatch am = (AuroraMatch) matches[0];
			CompositeMap map = am.getMatchs().getMap();
			Object value = map.get(this.s_name);
			if (null == value)
				return null;
			IFile file = (IFile) line.getParent();
			try {
				//
				IDocument document = this.getDocument(file);
				ITypedRegion partition = document.getPartition(am.getOffset());
				IRegion attRegion = Util.getAttributeRegion(
						partition.getOffset(), partition.getLength(), s_name,
						document);
				IRegion valueRegion = Util.getValueRegion(
						attRegion.getOffset(),
						attRegion.getOffset() - partition.getOffset()
								+ partition.getLength(), value.toString(),
						document, IColorConstants.STRING);

				RefactoringReplaceInfo info = new RefactoringReplaceInfo(
						new Region(attRegion.getOffset(),
								valueRegion.getOffset() - attRegion.getOffset()
										+ valueRegion.getLength()),
						getReplaceWith());
				info.setFile(file);
				return info;
			} catch (CoreException e) {

			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private String getReplaceWith() {
		return "";
	}

}
