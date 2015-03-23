package aurora.ide.composite.map;

import org.eclipse.jface.text.IDocument;

import uncertain.composite.CompositeMap;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.CompositeMapUtil;

public class DocumentCompositeMap {

	private IDocument doc;

	public DocumentCompositeMap(IDocument document) {
		this.setDoc(document);
	}

	public IDocument getDoc() {
		return doc;
	}

	public void setDoc(IDocument doc) {
		this.doc = doc;
	}

	public CompositeMap load() throws CompositeMapLoadException {
		try {
			CompositeMap loaderFromString = CompositeMapUtil
					.loaderFromString(doc.get());
			return loaderFromString;
		} catch (ApplicationException e) {
			throw new CompositeMapLoadException(e);
		} catch (Exception e) {
			throw new CompositeMapLoadException(e);
		}
	}

}
