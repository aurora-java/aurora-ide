package aurora.ide.prototype.consultant.product;

import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * This workbench advisor creates the window advisor, and specifies the
 * perspective id for the initial window.
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	@Override
	public void preStartup() {
//		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
//		IExtension[] extensions = extensionRegistry
//				.getExtensions("aurora.ide.meta");
//		for (IExtension iExtension : extensions) {
//			String extensionPointUniqueIdentifier = iExtension
//					.getExtensionPointUniqueIdentifier();
//			if ("org.eclipse.ui.actionSets"
//					.equals(extensionPointUniqueIdentifier)) {
////				extensionRegistry
////						.removeExtension(iExtension, "aurora.ide.meta");
//			}
//			System.out.println(extensionPointUniqueIdentifier);
//
//		}
	}

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	public String getInitialWindowPerspectiveId() {

		// this.getWorkbenchConfigurer().getWorkbench().getExtensionTracker().unregisterObject(extension)
		// this.getWorkbenchConfigurer().getWorkbench().getExtensionTracker()
		// getWorkbenchConfigurer().getWorkbench().getExtensionTracker().getObjects(extension);
		// WorkbenchPlugin
		// .getDefault().getActionSetRegistry().getActionSetsFor(
		// part.getSite().getId());
		return Perspective.ID;
	}

}
