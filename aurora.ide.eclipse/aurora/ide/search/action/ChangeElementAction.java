package aurora.ide.search.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import aurora.ide.helpers.ApplicationException;
import aurora.ide.refactoring.RefactoringReplaceInfo;
import aurora.ide.refactoring.ReplaceRefactoring;
import aurora.ide.search.cache.CacheManager;
import aurora.ide.search.core.AbstractMatch;
import aurora.ide.search.core.AuroraMatch;
import aurora.ide.search.core.AuroraSearchResult;
import aurora.ide.search.core.CompositeMapInDocument;
import aurora.ide.search.core.CompositeMapInDocumentManager;
import aurora.ide.search.core.Util;
import aurora.ide.search.ui.LineElement;

public class ChangeElementAction extends AbstractSearchResultPageAction {
	private String s_name;

	private String s_namespace;

	private ArrayList<RefactoringReplaceInfo> infos;

	private IFile c_file;

	private static final String DEFAULT_KEY = "ns";

	private Map<IFile, String> keyMaps;

	public ChangeElementAction(Shell shell) {
		super(shell);
	}

	public void setControl(final Text namespace, final Text name) {
		s_name = name.getText().trim();
		s_namespace = namespace.getText().trim();
		name.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				s_name = name.getText().trim();
				notifyActionChanged();
			}
		});
		namespace.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				s_namespace = namespace.getText().trim();
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
		keyMaps = new HashMap<IFile, String>();
		createNamespaceInfo(lines, monitor);

		for (Iterator it = lines.iterator(); it.hasNext();) {
			if (monitor.isCanceled()) {
				return;
			}
			Object next = it.next();
			if (next instanceof LineElement) {
				LineElement line = (LineElement) next;
				c_file = (IFile) line.getParent();
				RefactoringReplaceInfo info = createStartTagReplaceInfo(line);
				if (info != null) {
					infos.add(info);
				}
				RefactoringReplaceInfo endInfo = createEndTagReplaceInfo(line);
				if (info != null) {
					infos.add(endInfo);
				}
			}
		}
	}

	private RefactoringReplaceInfo createEndTagReplaceInfo(LineElement line) {
		AuroraSearchResult result = this.getSearchResult();
		AbstractMatch[] matches = line.getMatches(result);
		if (matches.length > 0) {
			AuroraMatch am = (AuroraMatch) matches[0];
			CompositeMap map = am.getMatchs().getMap();
			IFile file = (IFile) line.getParent();
			String replaceWith = getNewTagname(this.keyMaps.get(file));
			try {
				CompositeMapInDocument mid = CompositeMapInDocumentManager
						.getCompositeMapInDocument(map, this.getDocument(file));
				IRegion tagRegion = getTagRegion(mid.getEnd().getOffset(), map,
						file);
				RefactoringReplaceInfo info = new RefactoringReplaceInfo(
						new Region(tagRegion.getOffset(), tagRegion.getLength()),
						replaceWith);
				info.setFile(file);
				return info;
			} catch (CoreException e) {
			} catch (BadLocationException e) {
			}
		}
		return null;
	}

	private void createNamespaceInfo(List lines, IProgressMonitor monitor) {
		// this.gets
		List<IFile> files = new ArrayList<IFile>();
		for (Iterator it = lines.iterator(); it.hasNext();) {
			Object next = it.next();
			if (next instanceof LineElement) {
				LineElement line = (LineElement) next;
				c_file = (IFile) line.getParent();
				if (files.contains(c_file)) {
					continue;
				} else {
					files.add(c_file);
				}
				try {
					IFile file = (IFile) line.getParent();
					CompositeMap fileMap = CacheManager.getCompositeMap(file);
					Map namespaceMapping = fileMap.getNamespaceMapping();
					if (namespaceMapping != null
							&& namespaceMapping.get(s_namespace) != null) {
						keyMaps.put(file,
								(String) namespaceMapping.get(s_namespace));
						continue;
					}

				} catch (CoreException e) {
					continue;
				} catch (ApplicationException e) {
					continue;
				}
				RefactoringReplaceInfo info = createNamespaceReplaceInfo(line);
				if (info != null) {
					infos.add(info);
				}
			}
		}
	}

	private RefactoringReplaceInfo createNamespaceReplaceInfo(LineElement line) {
		IFile file = (IFile) line.getParent();
		try {
			CompositeMap fileMap = CacheManager.getCompositeMap(file);
			int startLine = fileMap.getLocation().getStartLine();
			IDocument document = this.getDocument(file);
			String key = getKey(fileMap.getNamespaceMapping(), 0);
			keyMaps.put(file, key);
			String ns = getNewNamespaceString(key);
			try {
				IRegion lineInformation = document
						.getLineInformation(startLine - 1);
				return createReplaceInfo(lineInformation.getOffset(), fileMap,
						file, ns);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getNewNamespaceString(String key) {
		// xmlns:ns1=
		StringBuilder b = new StringBuilder(" xmlns:");
		b.append(key);
		b.append("=\"");
		b.append(s_namespace);
		b.append("\"");
		return b.toString();
	}

	private String getKey(Map nsMapping, int i) {
		String key = DEFAULT_KEY + i;
		if (nsMapping != null && nsMapping.containsKey(key)) {
			return getKey(nsMapping, i++);
		}
		return key;
	}

	@Override
	public boolean isRefactorSelectionEnabled() {
		boolean s = !"".equals(s_name) && !"".equals(s_namespace)
				&& super.isRefactorSelectionEnabled();
		return s && checkReplaceWith();
	}

	private boolean checkReplaceWith() {
		StringBuilder b = new StringBuilder("<");
		b.append(this.getNewTagname("c"));
		b.append(" ");
		b.append(this.getNewNamespaceString("c"));
		b.append("/>");
		return Util.checkXMLForm(b.toString());
	}

	@Override
	public boolean isRefactorAllEnabled() {
		return !"".equals(s_name) && !"".equals(s_namespace)
				&& super.isRefactorAllEnabled() && checkReplaceWith();
	}

	private RefactoringReplaceInfo createStartTagReplaceInfo(LineElement line) {
		AuroraSearchResult result = this.getSearchResult();
		AbstractMatch[] matches = line.getMatches(result);
		if (matches.length > 0) {
			AuroraMatch am = (AuroraMatch) matches[0];
			int offset = am.getOffset();
			CompositeMap map = am.getMatchs().getMap();
			IFile file = (IFile) line.getParent();
			String replaceWith = getNewTagname(this.keyMaps.get(file));
			try {
				IRegion tagRegion = getTagRegion(offset, map, file);
				RefactoringReplaceInfo info = new RefactoringReplaceInfo(
						new Region(tagRegion.getOffset(), tagRegion.getLength()),
						replaceWith);
				info.setFile(file);
				return info;
			} catch (CoreException e) {
			} catch (BadLocationException e) {
			}
		}
		return null;
	}

	private RefactoringReplaceInfo createReplaceInfo(int offset,
			CompositeMap map, IFile file, String replaceWith)
			throws CoreException, BadLocationException {
		IRegion tagRegion = getTagRegion(offset, map, file);
		RefactoringReplaceInfo info = new RefactoringReplaceInfo(new Region(
				tagRegion.getOffset() + tagRegion.getLength(), 0), replaceWith);
		info.setFile(file);
		return info;
	}

	private IRegion getTagRegion(int offset, CompositeMap map, IFile file)
			throws CoreException, BadLocationException {
		IDocument document = this.getDocument(file);
		ITypedRegion partition = document.getPartition(offset);
		IRegion tagRegion = Util.getDocumentRegion(partition.getOffset(),
				partition.getLength(), map.getRawName(), document,
				IColorConstants.TAG_NAME);
		return tagRegion;
	}

	private String getNewTagname(String key) {
		StringBuilder builder = new StringBuilder(key);
		builder.append(":");
		builder.append(this.s_name);
		// builder.append(" ");
		return builder.toString();
	}

}
