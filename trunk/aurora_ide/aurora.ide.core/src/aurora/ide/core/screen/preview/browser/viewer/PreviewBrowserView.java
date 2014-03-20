package aurora.ide.core.screen.preview.browser.viewer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class PreviewBrowserView extends ViewPart {
	PreviewBrowser instance = null;
	
	/**
	 * Create the example
	 * 
	 * @see ViewPart#createPartControl
	 */
	public void createPartControl(Composite frame) {
		instance = new PreviewBrowser(frame, true);
	}

	/**
	 * Called when we must grab focus.
	 * 
	 * @see org.eclipse.ui.part.ViewPart#setFocus
	 */
	public void setFocus() {
		instance.focus();
	}

	/**
	 * Called when the View is to be disposed
	 */	
	public void dispose() {
		instance.dispose();
		instance = null;
		super.dispose();
	}
}
