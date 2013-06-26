package aurora.ide.prototype.consultant.product;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * This workbench advisor creates the window advisor, and specifies the
 * perspective id for the initial window.
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

//	private DelayedEventsProcessor processor;
//
//	public ApplicationWorkbenchAdvisor(DelayedEventsProcessor processor) {
//		this.processor = processor;
//	}

	public ApplicationWorkbenchAdvisor() {
	}

	@Override
	public void preStartup() {
		super.preStartup();
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

	public void eventLoopIdle(Display display) {
//		if (processor != null)
//			processor.catchUp(display);
		super.eventLoopIdle(display);
	}

}
