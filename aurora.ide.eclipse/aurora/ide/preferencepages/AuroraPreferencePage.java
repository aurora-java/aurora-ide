package aurora.ide.preferencepages;


import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import aurora.ide.AuroraPlugin;



public class AuroraPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public AuroraPreferencePage() {
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
	
	}

	/**
	 * @see IWorkbenchPreferencePage#init
	 */
	public void init(IWorkbench workbench) {
	}
}
