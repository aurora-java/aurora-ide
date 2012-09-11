package aurora.ide.views;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

public class StringEditorInput implements IStorageEditorInput {

	private final String inputString;

	public StringEditorInput(String inputString) {
		this.inputString = inputString == null ? "" : inputString;
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

	public IStorage getStorage() throws CoreException {

		return new IStorage() {

			public InputStream getContents() throws CoreException {
				return new ByteArrayInputStream(inputString.getBytes());

			}

			public IPath getFullPath() {

				return null;

			}

			public String getName() {

				return StringEditorInput.this.getName();

			}

			public boolean isReadOnly() {

				return false;

			}

			public Object getAdapter(Class adapter) {

				return null;

			}

		};

	}

}
