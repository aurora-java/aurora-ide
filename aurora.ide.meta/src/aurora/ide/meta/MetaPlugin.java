package aurora.ide.meta;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import aurora.ide.helpers.FileCopyer;
import aurora.ide.helpers.FileDeleter;
import aurora.ide.meta.gef.editors.source.gen.core.GeneratorManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class MetaPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "aurora.ide.meta"; //$NON-NLS-1$

	// The shared instance
	private static MetaPlugin plugin;
	
	
	public static boolean isDemonstrate;

	/**
	 * The constructor
	 */
	public MetaPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		uipTemplateConfirm();
		sourceGenTemplateConfirm();
	}

	private void uipTemplateConfirm() {
		IPath template = this.getStateLocation().append("template");
		File tplt = template.toFile();
		if (tplt.isDirectory() && tplt.exists()) {
			return;
		}
		copyTemplateFile();
	}

	private void sourceGenTemplateConfirm() {
		IPath template = GeneratorManager.getDefaultSourceGenTemplatePath();
		File tplt = template.toFile();
		if (tplt.isDirectory() && tplt.exists()) {
			File ver = template.append("config").append("1.0.xml").toFile();
			if (ver.exists()) {
				return;
			}
		}
		FileDeleter.deleteDirectory(tplt);
		copySourceGenTemplateFiles();
	}

	public void copySourceGenTemplateFiles() {

		URL ts = FileLocator.find(Platform.getBundle(PLUGIN_ID), new Path(
				"source.gen"), null);
		try {
			ts = FileLocator.toFileURL(ts);
			IPath template = GeneratorManager.getDefaultSourceGenTemplatePath();
			File tplt = template.toFile();
			FileCopyer.copyDirectory(new File(ts.getFile()), tplt);
		} catch (IOException e) {
			this.getLog().log(
					new Status(Status.ERROR, PLUGIN_ID,
							"template failed init. ", e));
		}
	}

	public void copyTemplateFile() {

		URL ts = FileLocator.find(Platform.getBundle(PLUGIN_ID), new Path(
				"template"), null);
		try {
			ts = FileLocator.toFileURL(ts);
			IPath template = this.getStateLocation().append("template");
			File tplt = template.toFile();
			FileCopyer.copyDirectory(new File(ts.getFile()), tplt);
		} catch (IOException e) {
			this.getLog().log(
					new Status(Status.ERROR, PLUGIN_ID,
							"template failed init. ", e));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static MetaPlugin getDefault() {
		return plugin;
	}

	public static InputStream openFileStream(String path) throws IOException {
		return FileLocator.openStream(Platform.getBundle(PLUGIN_ID), new Path(
				path), false);
	}

	public IDialogSettings getDialogSettingsSection(String name) {
		IDialogSettings dialogSettings = getDialogSettings();
		IDialogSettings section = dialogSettings.getSection(name);
		if (section == null) {
			section = dialogSettings.addNewSection(name);
		}
		return section;
	}

}
