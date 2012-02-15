package aurora.ide.meta.gef.editors.dnd;

import org.eclipse.core.resources.IFile;
import org.eclipse.gef.dnd.SimpleObjectTransfer;

public class BMTransfer extends SimpleObjectTransfer {

	private static final BMTransfer INSTANCE = new BMTransfer();
	private static final String TYPE_NAME = "BM transfer"//$NON-NLS-1$
			+ System.currentTimeMillis() + ":" + INSTANCE.hashCode();//$NON-NLS-1$
	private static final int TYPEID = registerType(TYPE_NAME);

	private BMTransfer() {
	}

	/**
	 * Returns the singleton instance
	 * 
	 * @return the singleton
	 */
	public static BMTransfer getInstance() {
		return INSTANCE;
	}
	

	public IFile getBM() {
		return (IFile) getObject();
	}

	/**
	 * @see org.eclipse.swt.dnd.Transfer#getTypeIds()
	 */
	protected int[] getTypeIds() {
		return new int[] { TYPEID };
	}

	/**
	 * @see org.eclipse.swt.dnd.Transfer#getTypeNames()
	 */
	protected String[] getTypeNames() {
		return new String[] { TYPE_NAME };
	}

	/**
	 * Sets the <i>template</i> Object.
	 * 
	 * @param template
	 *            the template
	 */
	public void setBM(IFile bm) {
		setObject(bm);
	}

}
