package aurora.ide.search.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import aurora.ide.search.core.PatternConstructor;
import aurora.ide.search.core.Util;
import aurora.ide.search.ui.LineElement;

public class ReplaceAttributeAction extends AbstractSearchResultPageAction {
	public static final String NAME = "name";
	public static final String VALUE = "value";
	private String s_replace;
	private String s_with;

	private ArrayList<RefactoringReplaceInfo> infos;

	private IFile c_file;
	private String type;

	public ReplaceAttributeAction(Shell shell, String type) {
		super(shell);
		this.type = type;
	}

	public void setControl(final Text replace, final Text with) {
		s_replace = replace.getText().trim();
		s_with = with.getText().trim();
		replace.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				s_replace = replace.getText().trim();
				notifyActionChanged();
			}
		});
		with.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				s_with = with.getText().trim();
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
		return !"".equals(s_replace) && super.isRefactorSelectionEnabled();
	}

	@Override
	public boolean isRefactorAllEnabled() {

		return !"".equals(s_replace) && super.isRefactorAllEnabled();
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
				if (NAME.equals(type)) {
					RefactoringReplaceInfo info = createNameReplaceInfo(line);
					if (info != null) {
						infos.add(info);
					}
				}
				if (VALUE.equals(type)) {
					List<RefactoringReplaceInfo> is = createValueReplaceInfo(line);
					infos.addAll(is);
				}
			}
		}
	}

	private List<RefactoringReplaceInfo> createValueReplaceInfo(LineElement line) {
		AuroraSearchResult result = this.getSearchResult();
		AbstractMatch[] matches = line.getMatches(result);
		List<RefactoringReplaceInfo> infos = new ArrayList<RefactoringReplaceInfo>();
		if (matches.length > 0) {
			AuroraMatch am = (AuroraMatch) matches[0];
			IFile file = (IFile) line.getParent();
			try {
				IDocument document = this.getDocument(file);
				ITypedRegion partition = document.getPartition(am.getOffset());
				List<IRegion> valueRegions = Util.getValueRegions(
						partition.getOffset(), partition.getLength(), document);
				for (IRegion r : valueRegions) {
					String text = document.get(r.getOffset() + 1,
							r.getLength() - 2);
					boolean stringMatch = Util.stringMatch(s_replace, text,
							false, false);
					if (stringMatch) {
						RefactoringReplaceInfo info = new RefactoringReplaceInfo(
								new Region(r.getOffset() + 1, r.getLength() - 2),
								getReplaceWith());
						info.setFile(file);
						infos.add(info);
					}
				}

			} catch (CoreException e) {

			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		return infos;
	}

	private RefactoringReplaceInfo createNameReplaceInfo(LineElement line) {
		AuroraSearchResult result = this.getSearchResult();
		AbstractMatch[] matches = line.getMatches(result);
		if (matches.length > 0) {
			AuroraMatch am = (AuroraMatch) matches[0];
			CompositeMap map = am.getMatchs().getMap();
			Object value = map.get(this.s_replace);
			if (null == value)
				return null;
			IFile file = (IFile) line.getParent();
			try {
				//
				IDocument document = this.getDocument(file);
				ITypedRegion partition = document.getPartition(am.getOffset());
				IRegion attRegion = Util.getAttributeRegion(
						partition.getOffset(), partition.getLength(),
						s_replace, document);
				RefactoringReplaceInfo info = new RefactoringReplaceInfo(
						attRegion, getReplaceWith());
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
		return s_with;
	}

}