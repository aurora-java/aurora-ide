package aurora.ide.preferencepages;


import java.io.IOException;


import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;

import aurora.ide.AuroraPlugin;
import aurora.ide.helpers.SystemException;


public class AuroraTemplateManager {
	private static final String CUSTOM_TEMPLATES_KEY = AuroraPlugin.PLUGIN_ID
			+ ".customtemplates";
	private static AuroraTemplateManager instance;
	private TemplateStore store;
	private ContributionContextTypeRegistry registry;

	private AuroraTemplateManager() {
	}

	public static AuroraTemplateManager getInstance() {
		if (instance == null) {
			instance = new AuroraTemplateManager();
		}
		return instance;
	}

	public TemplateStore getTemplateStore() throws SystemException {
		if (store == null) {
			store = new ContributionTemplateStore(getContextTypeRegistry(),
					AuroraPlugin.getDefault().getPreferenceStore(),
					CUSTOM_TEMPLATES_KEY);
			try {
				store.load();
			} catch (IOException e) {
				throw new SystemException(e);
			}
		}
		return store;
	}

	public ContextTypeRegistry getContextTypeRegistry() {
		if (registry == null) {
			registry = new ContributionContextTypeRegistry();
		}
		registry.addContextType(AuroraTemplateContextType.new_screen);
		registry.addContextType(AuroraTemplateContextType.SIGN);
		return registry;
	}

	public IPreferenceStore getPreferenceStore() {
		return AuroraPlugin.getDefault().getPreferenceStore();
	}

}
