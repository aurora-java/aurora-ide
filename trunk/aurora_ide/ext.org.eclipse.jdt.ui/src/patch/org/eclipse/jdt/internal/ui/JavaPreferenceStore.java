package patch.org.eclipse.jdt.internal.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;



public class JavaPreferenceStore {
	public static IPreferenceStore getPreferenceStore() {

		Plugin plugin = Platform.getPlugin(getJavaPluginID());
		if (plugin instanceof AbstractUIPlugin) {
			return ((AbstractUIPlugin) plugin).getPreferenceStore();
		}
		return null;

	}

	public static String getJavaPluginID() {
		return JavaUI.ID_PLUGIN;
	}
}
