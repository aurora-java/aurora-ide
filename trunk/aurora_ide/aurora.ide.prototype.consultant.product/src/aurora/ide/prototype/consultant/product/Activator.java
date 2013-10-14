package aurora.ide.prototype.consultant.product;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import aurora.ide.prototype.consultant.demonstrate.DemonstrateDSManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "aurora.ide.prototype.consultant.product"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private DemonstrateDSManager dsManager;

	/**
	 * The constructor
	 */
	public Activator() {

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
		dsManager = DemonstrateDSManager.makeInstance();
		dsManager.loadDemonstrateDS();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		dsManager.saveDemonstrateDS();
		plugin = null;
		dsManager = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	// used for performance logging. Time when the constructor of
	// CustomizableIntroPart is called.
	private long uiCreationStartTime;

	// image registry that can be disposed while the
	// plug-in is still active. This is important for
	// switching themes after the plug-in has been loaded.
	private ImageRegistry volatileImageRegistry;

	/**
	 * Returns the Intro Part.
	 */
	public static IIntroPart getIntro() {
		IIntroPart introPart = PlatformUI.getWorkbench().getIntroManager()
				.getIntro();
		return introPart;
	}

	/**
	 * Returns the Intro Part after forcing an open on it.
	 */
	public static IIntroPart showIntro(boolean standby) {
		IIntroPart introPart = PlatformUI
				.getWorkbench()
				.getIntroManager()
				.showIntro(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
						standby);
		return introPart;
	}

	/**
	 * Returns the standby state of the Intro Part. If the intro is closed,
	 * retruns false.
	 */
	public static boolean isIntroStandby() {
		return PlatformUI.getWorkbench().getIntroManager()
				.isIntroStandby(getIntro());
	}

	/**
	 * Sets the standby state of the Intro Part. If the intro is closed, retruns
	 * false.
	 */
	public static void setIntroStandby(boolean standby) {
		PlatformUI.getWorkbench().getIntroManager()
				.setIntroStandby(getIntro(), standby);
	}

	/**
	 * Returns the standby state of the Intro Part. If the intro is closed,
	 * retruns false.
	 */
	public static boolean closeIntro() {
		// Relies on Workbench.
		return PlatformUI.getWorkbench().getIntroManager()
				.closeIntro(getIntro());
	}

	public ImageRegistry getVolatileImageRegistry() {
		if (volatileImageRegistry == null) {
			volatileImageRegistry = createImageRegistry();
			initializeImageRegistry(volatileImageRegistry);
		}
		return volatileImageRegistry;
	}

	public void resetVolatileImageRegistry() {
		if (volatileImageRegistry != null) {
			volatileImageRegistry.dispose();
			volatileImageRegistry = null;
		}
	}

	public long gettUICreationStartTime() {
		return uiCreationStartTime;
	}

	public void setUICreationStartTime(long uiCreationStartTime) {
		this.uiCreationStartTime = uiCreationStartTime;
	}

	public DemonstrateDSManager getDemonstrateDSManager() {
		return this.dsManager;
	}
}
