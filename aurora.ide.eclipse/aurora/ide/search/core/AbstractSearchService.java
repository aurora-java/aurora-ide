package aurora.ide.search.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.schema.Attribute;
import uncertain.util.resource.Location;
import aurora.ide.AuroraPlugin;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.javascript.search.Javascript4RhinoSearchService;
import aurora.ide.javascript.search.JavascriptSearchService;
import aurora.ide.search.cache.CacheManager;
import aurora.ide.search.reference.IDataFilter;
import aurora.ide.search.reference.MapFinderResult;
import aurora.ide.search.ui.LineElement;
import aurora.ide.search.ui.MessageFormater;

abstract public class AbstractSearchService implements ISearchService {
	public final static QualifiedName bmReference = new QualifiedName(
			"http://www.aurora-framework.org/schema/bm", "model");
	public final static QualifiedName screenReference = new QualifiedName(
			"http://www.aurora-framework.org/application", "screen");
	public final static QualifiedName localFieldReference = new QualifiedName(
			"http://www.aurora-framework.org/schema/bm", "localField");
	public final static QualifiedName foreignFieldReference = new QualifiedName(
			"http://www.aurora-framework.org/schema/bm", "foreignField");
	public final static QualifiedName datasetReference = new QualifiedName(
			"http://www.aurora-framework.org/application", "dataset");
	public final static QualifiedName urlReference = new QualifiedName(
			"http://www.aurora-framework.org/application", "screenBm");

	private Map<CompositeMap, IResource> compositeMap = new HashMap<CompositeMap, IResource>();
	private Map<IFile, Exception> exceptionMap = new HashMap<IFile, Exception>();

	private JavascriptSearchService jsService;

	public JavascriptSearchService getJsService() {
		return jsService;
	}

	private boolean runInUI = false;
	private Object pattern;

	public boolean isPostException() {
		return isPostException;
	}

	public void setPostException(boolean isPostException) {
		this.isPostException = isPostException;
	}

	private boolean isPostException = true;

	private ISearchQuery query;
	private Object source;
	private IResource[] roots;
	private IFile fCurrentFile;
	private int fNumberOfScannedFiles;
	private int fNumberOfFilesToScan;
	private boolean supportJS;

	public void setSupportJS(boolean supportJS) {
		this.supportJS = supportJS;
	}

	public AbstractSearchService(IResource[] roots, Object source) {
		this.roots = roots;
		this.source = source;
		jsService = new Javascript4RhinoSearchService();
		if (source instanceof IFile)
			jsService.addSource((IFile) source);
	}

	public AbstractSearchService(IResource[] roots, Object source,
			ISearchQuery query) {
		this(roots, source);
		this.query = query;
	}

	abstract protected CompositeMapIteator createIterationHandle(IFile resource);

	protected List<AbstractMatch> buildMatchLines(IFile file,
			List<MapFinderResult> r, Object pattern) throws CoreException {

		List<AbstractMatch> lines = new ArrayList<AbstractMatch>();
		if (isSupportJS()) {
			lines.addAll(this.jsService.buildMatchLines(file, r, pattern));
		}

		for (int i = 0; i < r.size(); i++) {
			MapFinderResult result = (MapFinderResult) r.get(i);
			CompositeMap map = result.getMap();
			Location location = map.getLocation();
			IDocument document = getDocument(file);
			int lineNo = location.getStartLine();

			LineElement l = null;

			try {
				IRegion lineInformation = document
						.getLineInformation(lineNo - 1);
				String lineContent = document.get(lineInformation.getOffset(),
						lineInformation.getLength());
				l = new LineElement(file, lineNo, lineInformation.getOffset(),
						lineContent);
				lines.addAll(createLineMatches(result, l, file, pattern));
			} catch (BadLocationException e1) {
				continue;
			}
		}

		return lines;
	}

	private List<AbstractMatch> processFile(IResource resource)
			throws CoreException, ApplicationException {
		List<AbstractMatch> result = new ArrayList<AbstractMatch>();
		CompositeMap bm;
		fNumberOfScannedFiles++;
		CompositeMapIteator finder = createIterationHandle((IFile) resource);
		if (isSupportJS()) {
			finder = new MultiIteatorMapFinder().addFinder(
					jsService.createIterationHandle((IFile) resource))
					.addFinder(finder);
		}
		finder.setFilter(getDataFilter(roots, source));
		bm = getCompositeMap((IFile) resource);
		compositeMap.put(bm, resource);
		bm.iterate(finder, true);
		List<MapFinderResult> r = finder.getResult();
		List<AbstractMatch> lines = buildMatchLines((IFile) resource, r,
				pattern);

		for (int i = 0; i < lines.size(); i++) {
			if (query != null) {
				ISearchResult searchResult = query.getSearchResult();
				if (searchResult instanceof AbstractSearchResult) {
					((AbstractSearchResult) searchResult)
							.addMatch((Match) lines.get(i));
				}
			} else {
				result.add(lines.get(i));
			}
		}

		return result;
	}

	private boolean isSupportJS() {
		return supportJS;
	}

	protected List<AbstractMatch> createLineMatches(MapFinderResult r,
			LineElement l, IFile file, Object pattern) throws CoreException {

		IDocument document = (IDocument) getDocument(file);
		FindReplaceDocumentAdapter dd = new FindReplaceDocumentAdapter(
				(IDocument) getDocument(file));

		List<AbstractMatch> matches = new ArrayList<AbstractMatch>();
		List<Attribute> attributes = r.getAttributes();

		if (attributes == null) {
			return matches;
		}

		for (int i = 0; i < attributes.size(); i++) {
			try {
				int startOffset = l.getOffset();
				Attribute att = (Attribute) attributes.get(i);
				String name = att.getName();
				IRegion nameRegion = getAttributeRegion(startOffset,
						l.getLength(), name, document);
				if (nameRegion == null) {
					continue;
				}
				startOffset = nameRegion.getOffset();
				IRegion valueRegion = dd.find(startOffset, pattern.toString(),
						true, true, true, false);
				if (valueRegion == null) {
					continue;
				}
				startOffset = valueRegion.getOffset();
				AuroraMatch match = new AuroraMatch(file,
						valueRegion.getOffset(), valueRegion.getLength(), l);
				match.setMatchs(r);
				matches.add(match);
			} catch (BadLocationException e) {
				continue;
			}
		}

		return matches;
	}

	protected IRegion getAttributeRegion(int offset, int length, String name,
			IDocument document) throws BadLocationException {
		IRegion attributeRegion = Util.getAttributeRegion(offset, length, name,
				document);
		return attributeRegion;
	}

	public List<AbstractMatch> service(final IProgressMonitor monitor) {

		List<IResource> files = findFilesInScopes(roots);
		fNumberOfFilesToScan = files.size();
		Job monitorUpdateJob = new Job("Aurora Search progress") {
			private int fLastNumberOfScannedFiles = 0;

			public IStatus run(final IProgressMonitor inner) {
				while (!inner.isCanceled()) {
					final IFile file = fCurrentFile;
					if (file != null) {
						if (isRunInUI()) {
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									updateMonitor(monitor, file);
								}
							});
						} else {
							updateMonitor(monitor, file);
						}
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						return Status.OK_STATUS;
					}
				}
				return Status.OK_STATUS;

			}

			private void updateMonitor(final IProgressMonitor monitor,
					final IFile file) {
				String fileName = file.getName();
				final Object[] args = { fileName,
						new Integer(fNumberOfScannedFiles),
						new Integer(fNumberOfFilesToScan) };
				monitor.subTask(MessageFormater.format(Message._scanning, args));
				int steps = fNumberOfScannedFiles - fLastNumberOfScannedFiles;
				monitor.worked(steps);
				fLastNumberOfScannedFiles += steps;
			}

		};

		// searchPattern
		pattern = getSearchPattern(roots, source);

		monitor.beginTask("Searching for " + pattern.toString(), files.size());
		monitorUpdateJob.setSystem(true);
		monitorUpdateJob.schedule();
		List<AbstractMatch> result = new ArrayList<AbstractMatch>();
		try {
			if (files != null) {
				for (int i = 0; i < files.size(); i++) {
					if (monitor.isCanceled())
						return result;
					fCurrentFile = (IFile) files.get(i);
//					if ("ebs_gl_account_rule_doc_types.screen".equals(fCurrentFile.getName())) {
//						System.out.println();
////						ebs_gl_account_rule_doc_types.screen  bgt_journal_query_headers.bm
//					}
					try {
						result.addAll(processFile(fCurrentFile));
					} catch (CoreException e) {
					} catch (ApplicationException e) {
					} catch (Exception e) {
						if (!(e instanceof IllegalArgumentException)) {
							// e.printStackTrace();
						}
						e.printStackTrace();
						handleException(fCurrentFile, e);
					}
				}
			}
		} finally {
			monitorUpdateJob.cancel();
			monitor.done();
			if (isPostException)
				postException();
		}

		return result;
	}

	private static Shell getShell() {
		// index : 0 must the active window.
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow activeWindow = workbench.getActiveWorkbenchWindow();
		IWorkbenchWindow windowToParentOn = activeWindow == null ? (workbench
				.getWorkbenchWindowCount() > 0 ? workbench
				.getWorkbenchWindows()[0] : null) : activeWindow;
		return windowToParentOn == null ? null : activeWindow.getShell();

	}

	private static Display getDisplay() {
		Display display = Display.getCurrent();
		if (display == null)
			display = Display.getDefault();
		return display;
	}

	private void postException() {
		if (exceptionMap.size() != 0) {
			getDisplay().asyncExec(new Runnable() {
				public void run() {
					MultiStatus status = new MultiStatus(
							AuroraPlugin.PLUGIN_ID, IStatus.ERROR,
							getStatusChildren(), "文件解析异常", null);
					ErrorDialog.openError(getShell(), null, null, status);
				}
			});

		}

	}

	public IStatus[] getStatusChildren() {
		IStatus[] children = new IStatus[exceptionMap.size()];
		Set<IFile> keySet = exceptionMap.keySet();
		int i = 0;
		for (Iterator<IFile> iterator = keySet.iterator(); iterator.hasNext();) {
			IFile o = (IFile) iterator.next();
			children[i] = new Status(IStatus.ERROR, AuroraPlugin.PLUGIN_ID, o
					.getFullPath().toString(), (Throwable) exceptionMap.get(o));
			i++;
		}
		return children;
	}

	private void handleException(IFile file, Exception e) {
		exceptionMap.put(file, e);
	}

	private List<IResource> findFilesInScopes(IResource[] roots) {
		List<IResource> result = new ArrayList<IResource>();
		if (roots != null) {
			for (int i = 0; i < roots.length; i++) {
				List<IResource> _result = findFilesInScope(roots[i]);
				merge(result, _result);
			}
		}

		return result;
	}

	private void merge(List<IResource> to, List<IResource> from) {
		if (from == null)
			return;
		for (int i = 0; i < from.size(); i++) {
			if (to.contains(from.get(i))) {
				continue;
			}
			to.add(from.get(i));
		}
	}

	private List<IResource> findFilesInScope(IResource scope) {
		AuroraFileFinder visitor = new AuroraFileFinder();
		try {
			scope.accept(visitor);
			return visitor.getResult();
		} catch (CoreException e) {
		}
		return null;
	}

	protected abstract IDataFilter getDataFilter(IResource[] roots,
			Object source);

	public Object getSearchPattern(IResource[] roots, Object source) {
		return this.pattern == null ? createPattern(roots, source) : pattern;

	}

	public ISearchQuery getQuery() {
		return query;
	}

	public Object getSource() {
		return source;
	}

	public IResource[] getRoots() {
		return roots;
	}

	public CompositeMap getCompositeMap(IFile file) throws CoreException,
			ApplicationException {
		return CacheManager.getCompositeMapCacher().getCompositeMap(file);
	}

	public IFile getFile(CompositeMap map) {
		return (IFile) this.compositeMap.get(map);
	}

	public IDocument getDocument(IFile file) throws CoreException {
		return CacheManager.getDocument(file);
	}

	protected abstract Object createPattern(IResource[] roots, Object source);

	public boolean isRunInUI() {
		return runInUI;
	}

	public void setRunInUI(boolean runInUI) {
		this.runInUI = runInUI;
	}
}
