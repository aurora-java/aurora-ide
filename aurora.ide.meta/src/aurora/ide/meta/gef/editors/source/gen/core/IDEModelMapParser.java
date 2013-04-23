package aurora.ide.meta.gef.editors.source.gen.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import uncertain.composite.CompositeMap;
import aurora.ide.builder.ResourceUtil;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.search.cache.CacheManager;
import aurora.plugin.source.gen.ModelMapParser;

public class IDEModelMapParser extends ModelMapParser {

	private IProject iProject;

	public IDEModelMapParser(CompositeMap uipMap, IProject iProject) {
		super(uipMap);
		this.iProject = iProject;
	}

	public CompositeMap loadModelMap(String optionModel) {
		IFile bmFile = ResourceUtil.getBMFile(iProject, optionModel);
		try {
			return CacheManager.getWholeBMCompositeMap(bmFile);
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		return null;
	}
}
