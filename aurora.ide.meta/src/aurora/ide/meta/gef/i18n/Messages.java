package aurora.ide.meta.gef.i18n;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "aurora.ide.meta.gef.i18n.messages"; //$NON-NLS-1$
	public static String BMViewer_No_aurora_project;
	public static String BMViewer_No_model;
	public static String BMViewer_No_template;
	public static String ButtonClicker_Close;
	public static String ButtonClicker_Custom;
	public static String ButtonClicker_Open;
	public static String ButtonClicker_Query;
	public static String ButtonClicker_Reset;
	public static String ButtonClicker_Save;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
