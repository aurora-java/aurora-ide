package aurora.ide.prototype.consultant.product;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(800, 600));
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(false);
//		Display display = configurer.getWorkbenchConfigurer().getWorkbench()
//				.getDisplay();
		// SWT.OpenDoc
//		display.addListener(SWT.OpenDocument, new Listener() {
//
//			@Override
//			public void handleEvent(Event event) {
//				System.out.println(event.text);
//			}
//		});
	}

	public void postWindowCreate() {
		super.postWindowCreate();
		// 设置打开时最大化窗口
		getWindowConfigurer().getWindow().getShell().setMaximized(true);
	}
}
