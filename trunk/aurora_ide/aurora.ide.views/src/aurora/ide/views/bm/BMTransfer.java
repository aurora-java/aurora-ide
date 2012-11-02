package aurora.ide.views.bm;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

public class BMTransfer extends ByteArrayTransfer {

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

	private Object object;
	private long startTime;

	/**
	 * Returns the Object.
	 * 
	 * @return The Object
	 */
	public Object getObject() {
		return object;
	}

	/**
	 * The data object is not converted to bytes. It is held onto in a field.
	 * Instead, a checksum is written out to prevent unwanted drags across
	 * mulitple running copies of Eclipse.
	 * 
	 * @see org.eclipse.swt.dnd.Transfer#javaToNative(Object, TransferData)
	 */
	public void javaToNative(Object object, TransferData transferData) {
		setObject(object);
		startTime = System.currentTimeMillis();
		if (transferData != null)
			super.javaToNative(String.valueOf(startTime).getBytes(),
					transferData);
	}

	/**
	 * The data object is not converted to bytes. It is held onto in a field.
	 * Instead, a checksum is written out to prevent unwanted drags across
	 * mulitple running. copies of Eclipse.
	 * 
	 * @see org.eclipse.swt.dnd.Transfer#nativeToJava(TransferData)
	 */
	public Object nativeToJava(TransferData transferData) {
		byte bytes[] = (byte[]) super.nativeToJava(transferData);
		if (bytes == null) {
			return null;
		}
		long startTime = Long.parseLong(new String(bytes));
		return (this.startTime == startTime) ? getObject() : null;
	}

	/**
	 * Sets the Object.
	 * 
	 * @param obj
	 *            The Object
	 */
	public void setObject(Object obj) {
		object = obj;
	}

}
