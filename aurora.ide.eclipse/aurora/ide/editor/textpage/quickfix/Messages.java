package aurora.ide.editor.textpage.quickfix;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "aurora.ide.editor.textpage.quickfix.messages"; //$NON-NLS-1$
	public static String AttrProposalCreator_0;
	public static String AttrProposalCreator_2;
	public static String AttrProposalCreator_4;
	public static String AttrProposalCreator_5;
	public static String BmProposalCreator_5;
	public static String Create_bm;
	public static String Change_to;
	public static String Create_ds;
	public static String Suggest_change_to;
	public static String Delete_tag;
	public static String TagProposalCreator_2;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
