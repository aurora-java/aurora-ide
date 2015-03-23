package aurora.ide.search.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
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

public class AddAttributeAction extends AbstractSearchResultPageAction {
	private String s_name;

	private String s_value;

	private ArrayList<RefactoringReplaceInfo> infos;

	private IFile c_file;

	public AddAttributeAction(Shell shell) {
		super(shell);
	}

	public void setControl(final Text name, final Text value) {
		s_name = name.getText().trim();
		s_value = value.getText().trim();
		name.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				s_name = name.getText().trim();
				notifyActionChanged();
			}
		});
		value.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				s_value = value.getText().trim();
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

	@Override
	public boolean isRefactorSelectionEnabled() {
		boolean s = super.isRefactorSelectionEnabled();
		return s && checkReplaceWith();
	}

	private boolean checkReplaceWith() {
		String s = "<a replace />";
		s = s.replace("replace", this.getReplaceWith());
		return Util.checkXMLForm(s);
	}

	@Override
	public boolean isRefactorAllEnabled() {
		return super.isRefactorAllEnabled() && checkReplaceWith();
	}

	private RefactoringReplaceInfo toRefactoringReplaceInfo(LineElement line) {
		AuroraSearchResult result = this.getSearchResult();
		AbstractMatch[] matches = line.getMatches(result);
		if (matches.length > 0) {
			AuroraMatch am = (AuroraMatch) matches[0];
			CompositeMap map = am.getMatchs().getMap();
			IFile file = (IFile) line.getParent();
			try {
				//
				IDocument document = this.getDocument(file);
				ITypedRegion partition = document.getPartition(am.getOffset());
				IRegion tagRegion = Util.getDocumentRegion(
						partition.getOffset(), partition.getLength(),
						map.getRawName(), document, IColorConstants.TAG_NAME);
				RefactoringReplaceInfo info = new RefactoringReplaceInfo(
						new Region(tagRegion.getOffset()
								+ tagRegion.getLength(), 0), getReplaceWith());
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
		StringBuilder builder = new StringBuilder(" ");
		builder.append(s_name.trim());
		builder.append("=");
		builder.append("\"");
		builder.append(s_value.trim());
		builder.append("\"");
		return builder.toString();
	}

}
