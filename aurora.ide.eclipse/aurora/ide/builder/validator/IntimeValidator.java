package aurora.ide.builder.validator;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;

import uncertain.composite.CompositeMap;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.processor.AbstractProcessor;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.CompositeMapUtil;

public class IntimeValidator extends AbstractValidator {

	public IntimeValidator(IFile file) {
		super(file);
	}

	/**
	 * @param sourceViewer
	 */
	public IntimeValidator(IFile file, IDocument doc) {
		this.file = file;
		this.doc = doc;
		try {
			map = CompositeMapUtil.loaderFromString(doc.get());
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		AuroraBuilder.deleteMarkers(file);
		
 	}

	@Override
	public AbstractProcessor[] getMapProcessor() {
		String ext = getFileExt();
		if (file.getName().equalsIgnoreCase("uncertain.local.xml")) {
			return new UncertainLocalValidator().getMapProcessor();
		} else if ("bm".equals(ext)) {
			return new BmValidator().getMapProcessor();
		} else if ("svc".equals(ext)) {
			return new SvcValidator().getMapProcessor();
		} else if ("screen".equals(ext)) {
			return new ScreenValidator().getMapProcessor();
		}
		return new AbstractProcessor[0];
	}

	private String getFileExt() {
		String ext = file.getFileExtension();
		if (ext == null)
			return null;
		return ext.toLowerCase();
	}
}
