package aurora.ide.search.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import uncertain.composite.CompositeMap;

public class CompositeMapInDocument {
	private CompositeMap map;
	private IDocument document;
	private IRegion start;
	private IRegion end;
	private IFile file;
	
	public CompositeMapInDocument(CompositeMap map, IDocument document,
			IRegion start, IRegion end) {
		super();
		this.map = map;
		this.document = document;
		this.start = start;
		this.end = end;
	}
	public CompositeMap getMap() {
		return map;
	}
	public void setMap(CompositeMap map) {
		this.map = map;
	}
	public IDocument getDocument() {
		return document;
	}
	public void setDocument(IDocument document) {
		this.document = document;
	}
	public IRegion getStart() {
		return start;
	}
	public void setStart(IRegion start) {
		this.start = start;
	}
	public IRegion getEnd() {
		return end;
	}
	public void setEnd(IRegion end) {
		this.end = end;
	}
	public IFile getFile() {
		return file;
	}
	public void setFile(IFile file) {
		this.file = file;
	}


}
