package aurora.ide.designer.editor;

import java.io.InputStream;

import org.eclipse.core.resources.IEncodedStorage;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

public class BPMServiceInputStreamEditorInput implements IStorageEditorInput {


	private InputStream input;

	public BPMServiceInputStreamEditorInput(InputStream input) {
		this.input = input;
	}

	public boolean exists() {
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;

	}

	public IPersistableElement getPersistable() {
		return null;

	}

	public Object getAdapter(Class adapter) {
		return null;

	}

	public String getName() {
		return "Aurora";
	}

	public String getToolTipText() {
		return "Aurora";
	}
	
	private class Storage implements IEncodedStorage{

		public InputStream getContents() throws CoreException {
			return input;
		}

		public IPath getFullPath() {
			return null;
		}

		public String getName() {
			return BPMServiceInputStreamEditorInput.this.getName();
		}

		public boolean isReadOnly() {
			return false;
		}

		public Object getAdapter(Class adapter) {
			return null;
		}

		public String getCharset() throws CoreException {
			return "UTF-8";
		}
	} 

	public IStorage getStorage() throws CoreException {
		return new Storage();
	}

}
