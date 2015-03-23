package aurora.ide.search.cache;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;

import uncertain.composite.CompositeMap;

public class CacheFile {
	private IFile file;
	private long modificationStamp;
	private CompositeMap compositeMap;
	private IDocument document;
	private String toXML;
	private String string;

	public IFile getFile() {
		return file;
	}

	public long getModificationStamp() {
		return modificationStamp;
	}

	public CompositeMap getCompositeMap() {
		return compositeMap;
	}

	public CacheFile(IFile file) {
		super();
		this.file = file;
		this.modificationStamp = file.getModificationStamp();
	}

	public boolean checkModification() {
		return file.getModificationStamp() != this.modificationStamp;
	}

	public String getToXML() {
		return toXML;
	}

	public void setToXML(String toXML) {
		this.toXML = toXML;
	}

	public IDocument getDocument() {
		return document;
	}

	public void setDocument(IDocument document) {
		this.document = document;
	}

	public void setCompositeMap(CompositeMap compositeMap) {
		this.compositeMap = compositeMap;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	public void clear() {
		file = null;
		modificationStamp = -1;
		compositeMap = null;
		document = null;
		toXML = null;
		string = null;
	}

}
