package aurora.ide.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;

public class RefactoringReplaceInfo {
	private IRegion region;
	private String replaceWith;
	private IFile file;
	public IFile getFile() {
		return file;
	}
	public void setFile(IFile file) {
		this.file = file;
	}
	public IRegion getRegion() {
		return region;
	}
	public void setRegion(IRegion region) {
		this.region = region;
	}
	public String getReplaceWith() {
		return replaceWith;
	}
	public void setReplaceWith(String replaceWith) {
		this.replaceWith = replaceWith;
	}
	public RefactoringReplaceInfo(IRegion region, String replaceWith) {
		super();
		this.region = region;
		this.replaceWith = replaceWith;
	}
}
