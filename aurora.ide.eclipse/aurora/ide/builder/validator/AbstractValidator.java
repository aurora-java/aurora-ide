package aurora.ide.builder.validator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.IDocument;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.BuildContext;
import aurora.ide.builder.CompositeMapInfo;
import aurora.ide.builder.SxsdUtil;
import aurora.ide.builder.processor.AbstractProcessor;
import aurora.ide.search.cache.CacheManager;

public abstract class AbstractValidator implements IterationHandle {
	protected IFile file;
	protected CompositeMap map;
	protected IDocument doc;
	private AbstractProcessor[] aps = null;

	public AbstractValidator(IFile file) {
		super();
		this.file = file;
		try {
			map = CacheManager.getCompositeMap(file);
			doc = CacheManager.getDocument(file);
		} catch (Exception e) {
			AuroraBuilder.addMarker(file, e.getMessage(), 1,
					IMarker.SEVERITY_ERROR, AuroraBuilder.FATAL_ERROR);
			// e.printStackTrace();
		}
	}

	public AbstractValidator() {
	}

	public final void validate() {
		if (map == null)
			return;
		aps = getMapProcessor();
		map.iterate(this, true);
		for (AbstractProcessor np : aps) {
			np.processComplete(file, map, doc);
		}
	}

	public int process(CompositeMap map) {
		CompositeMapInfo info = new CompositeMapInfo(map, doc);
		BuildContext bc = new BuildContext();
		try {
			bc.list = SxsdUtil.getAttributesNotNull(map);
		} catch (Exception e) {
			bc.nullListMsg = e.getMessage();
			// e.printStackTrace();
		}
		bc.doc = doc;
		bc.file = file;
		bc.info = info;
		bc.map = map;
		for (AbstractProcessor np : aps) {
			np.processMap(bc);
		}
		return 0;
	}

	/**
	 * 为了能使AbstractProcessor保存自己的数据,应确保此方法没次调用返回相同的值
	 * 
	 * @return
	 */
	public abstract AbstractProcessor[] getMapProcessor();

}
