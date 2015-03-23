package aurora.ide.search.cache;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.part.FileEditorInput;

import uncertain.composite.CompositeMap;
import uncertain.ocm.OCManager;
import aurora.bm.BusinessModel;
import aurora.ide.AuroraPlugin;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.bm.ExtendModelFactory;
import aurora.ide.editor.textpage.XMLDocumentProvider;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.PathUtil;

public class CompositeMapCacher implements IResourceChangeListener,
		IResourceDeltaVisitor {

	private final static CompositeMap EMPTY_MAP = new CompositeMap();

	private class ProjectCatcher {
		private Map<IFile, CacheFile> catchMap = new HashMap<IFile, CacheFile>();

		private Map<IFile, CompositeMap> wholeBMMap = new HashMap<IFile, CompositeMap>();

		private IProject project;

		private ProjectCatcher(IProject project) {
			this.project = project;
		}

		private CompositeMap getCompositeMap(IFile file) throws CoreException,
				ApplicationException {
			if (!CacheManager.isSupport(file)) {
				return EMPTY_MAP;
			}
			CacheFile cacheFile = getCacheFile(file);
			if (cacheFile.getCompositeMap() == null) {
				cacheFile.setCompositeMap(loadCompositeMap(file));
			}
			return cacheFile.getCompositeMap();
		}

		private CacheFile getCacheFile(IFile file) {
			CacheFile cacheFile = catchMap.get(file);
			if (cacheFile == null) {
				cacheFile = new CacheFile(file);
				catchMap.put(file, cacheFile);
			}
			return cacheFile;
		}

		private CompositeMap getWholeCompositeMap(IFile file)
				throws CoreException, ApplicationException {
			if (!PathUtil.isBMFile(file)) {
				return EMPTY_MAP;
			}
			CompositeMap map = wholeBMMap.get(file);
			if (map == null) {
				map = loadWholeBM(file);
				if (map != null) {
					// wholeBMMap.put(file, map);
				}
			}
			return map;
		}

		public IProject getProject() {
			return project;
		}

		private CompositeMap loadWholeBM(IFile file) throws CoreException,
				ApplicationException {
			// CompositeMap bm = ((CacheCompositeMap) getCompositeMap(file))
			// .getRealMap();
			CompositeMap bm = AuroraResourceUtil.loadFromResource(file);
			BusinessModel r = createResult(bm, file);
			// return new CacheCompositeMap(
			// (CommentCompositeMap) r.getObjectContext());
			return r.getObjectContext();
		}

		private BusinessModel createResult(CompositeMap config, IFile file) {
			ExtendModelFactory factory = new ExtendModelFactory(
					OCManager.getInstance(), file);
			// ModelFactory factory = new ModelFactory(OCManager.getInstance());
			return factory.getModel(config);
		}

		private CompositeMap loadCompositeMap(IFile file) throws CoreException,
				ApplicationException {

			IDocument document = CacheManager.getDocument(file);
			if (document == null)
				return null;
			CompositeMap loaderFromString = CompositeMapUtil
					.loaderFromString(document.get());
			return new CacheCompositeMap((CommentCompositeMap) loaderFromString);
		}

		private void remove(IFile file) {
			CacheFile cacheFile = catchMap.get(file);
			if (cacheFile != null && cacheFile.checkModification()) {
				catchMap.remove(file).clear();
			}
		}

		public IDocument getDocument(IFile file) {
			if (!CacheManager.isSupport(file)) {
				return null;
			}
			CacheFile cacheFile = getCacheFile(file);
			if (cacheFile.getDocument() == null) {
				cacheFile.setDocument(loadDocument(file));
			}
			return cacheFile.getDocument();
		}

		protected IDocument loadDocument(IFile file) {
			FileEditorInput element = new FileEditorInput(file);
			XMLDocumentProvider provider = new XMLDocumentProvider();
			try {
				provider.connect(element);
			} catch (CoreException e) {
				DialogUtil.logErrorException(e);
			}
			IDocument document = provider.getDocument(element);
			return document;
		}

		public String getTOXML(IFile file) throws CoreException,
				ApplicationException {
			if (!CacheManager.isSupport(file)) {
				return null;
			}
			CacheFile cacheFile = getCacheFile(file);
			if (cacheFile.getToXML() == null) {
				cacheFile.setToXML(getCompositeMap(file).toXML());
			}
			return cacheFile.getToXML();
		}

		public String getString(IFile file) {
			if (!CacheManager.isSupport(file)) {
				return null;
			}
			CacheFile cacheFile = getCacheFile(file);
			if (cacheFile.getString() == null) {
				cacheFile.setString(this.getDocument(file).get());
			}
			return cacheFile.getString();
		}
	}

	private Map<IProject, ProjectCatcher> catcher = new HashMap<IProject, ProjectCatcher>();

	public CompositeMapCacher() {
		AuroraPlugin.getWorkspace().addResourceChangeListener(this);
	}

	public CompositeMap getCompositeMap(IFile file) throws CoreException,
			ApplicationException {
		ProjectCatcher projectCatcher = getProjectCatcher(file);
		return (CompositeMap) projectCatcher.getCompositeMap(file);
	}

	public CompositeMap getWholeCompositeMap(IFile file) throws CoreException,
			ApplicationException {
		ProjectCatcher projectCatcher = getProjectCatcher(file);
		return (CompositeMap) projectCatcher.getWholeCompositeMap(file);
	}

	private void remove(IFile file) {
		getProjectCatcher(file).remove(file);
	}

	private ProjectCatcher getProjectCatcher(IFile file) {
		IProject project = file.getProject();
		ProjectCatcher projectCatcher = catcher.get(project);
		if (projectCatcher == null) {
			projectCatcher = new ProjectCatcher(project);
			catcher.put(project, projectCatcher);
		}
		return projectCatcher;
	}

	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();
		if (delta == null)
			return;
		try {
			delta.accept(this);
		} catch (CoreException e) {
		}
	}

	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource resource = delta.getResource();
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			this.remove(file);
			return false;
		}
		return true;
	}

	public IDocument getDocument(IFile file) throws CoreException {
		ProjectCatcher projectCatcher = getProjectCatcher(file);
		return projectCatcher.getDocument(file);
	}

	public String getTOXML(IFile file) throws CoreException,
			ApplicationException {
		ProjectCatcher projectCatcher = getProjectCatcher(file);
		return projectCatcher.getTOXML(file);
	}

	public String getString(IFile file) throws CoreException,
			ApplicationException {
		ProjectCatcher projectCatcher = getProjectCatcher(file);
		return projectCatcher.getString(file);
	}
}
