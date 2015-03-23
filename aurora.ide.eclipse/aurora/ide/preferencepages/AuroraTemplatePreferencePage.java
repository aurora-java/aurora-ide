package aurora.ide.preferencepages;


import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.texteditor.templates.TemplatePreferencePage;

import aurora.ide.AuroraPlugin;


public class AuroraTemplatePreferencePage extends TemplatePreferencePage
		implements IWorkbenchPreferencePage {

	public AuroraTemplatePreferencePage() {
		try {
			setPreferenceStore(AuroraPlugin.getDefault().getPreferenceStore());
			setTemplateStore(AuroraTemplateManager.getInstance()
					.getTemplateStore());
			setContextTypeRegistry(AuroraTemplateManager.getInstance()
					.getContextTypeRegistry());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected boolean isShowFormatterSetting() {
		return false;
	}
}
