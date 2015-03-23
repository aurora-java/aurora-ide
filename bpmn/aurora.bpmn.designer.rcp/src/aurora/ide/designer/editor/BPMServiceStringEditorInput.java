package aurora.ide.designer.editor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IEncodedStorage;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

public class BPMServiceStringEditorInput implements IStorageEditorInput {

	private final String inputString;
	private String encoding;

	public BPMServiceStringEditorInput(String inputString,String encoding) {
		this.inputString = inputString == null ? "" : inputString;
		this.encoding = encoding;
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
	
	private class StringStorage implements IEncodedStorage{

		public InputStream getContents() throws CoreException {
			try {
				return new ByteArrayInputStream(inputString.getBytes(encoding));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return null;
		}

		public IPath getFullPath() {
			return null;
		}

		public String getName() {
			return BPMServiceStringEditorInput.this.getName();
		}

		public boolean isReadOnly() {
			return false;
		}

		public Object getAdapter(Class adapter) {
			return null;
		}

		public String getCharset() throws CoreException {
			return encoding;
		}
	} 

	public IStorage getStorage() throws CoreException {
		return new StringStorage();
	}

}
