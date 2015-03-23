package aurora.ide.preferencepages;


import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import aurora.ide.AuroraPlugin;
import aurora.ide.helpers.LocaleMessage;



/**
 * A preference page for a simple HTML editor.
 */
public class BrowserPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public static final String BROWSER_REMOTE = "remoteUrl";
	public static final String PreferencePageId="aurora.ide.preferencePage.BrowserPreferencePage";
	public BrowserPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		// Set the preference store for the preference page.
		IPreferenceStore store = AuroraPlugin.getDefault().getPreferenceStore();
		setPreferenceStore(store);
	}

	/**
	 * @see org.eclipse.jface.preference.
	 *      FieldEditorPreferencePage#createFieldEditors()
	 */
	protected void createFieldEditors() {
		StringFieldEditor  remoteUrl = new StringFieldEditor (
				BROWSER_REMOTE, LocaleMessage.getString("remote.server.page"), getFieldEditorParent());
		addField(remoteUrl);
	}

	/**
	 * @see IWorkbenchPreferencePage#init
	 */
	public void init(IWorkbench workbench) {
	}

	public static String getRemoteMainUrl() {
		return AuroraPlugin.getDefault().getPreferenceStore().getString(BrowserPreferencePage.BROWSER_REMOTE);
	}
}
