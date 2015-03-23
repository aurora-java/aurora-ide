package aurora.ide.swt.util.viewer;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "aurora.ide.swt.util.viewer.messages"; //$NON-NLS-1$
	public static String CTableViewer_0;
	public static String CTableViewer_1;
	public static String CTableViewer_2;
	public static String CTableViewer_3;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
