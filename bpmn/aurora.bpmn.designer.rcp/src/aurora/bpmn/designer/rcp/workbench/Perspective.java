package aurora.bpmn.designer.rcp.workbench;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import aurora.bpmn.designer.rcp.viewer.BPMServiceViewer;

public class Perspective implements IPerspectiveFactory {

	/**
	 * The ID of the perspective as specified in the extension.
	 */
	public static final String ID = "aurora.bpmn.designer.rcp.perspective";

	public void createInitialLayout(IPageLayout layout) {
//		String editorArea = layout.getEditorArea();
//		layout.setEditorAreaVisible(false);
//		
//		layout.addStandaloneView(NavigationView.ID,  false, IPageLayout.LEFT, 0.25f, editorArea);
//		IFolderLayout folder = layout.createFolder("messages", IPageLayout.TOP, 0.5f, editorArea);
//		folder.addPlaceholder(View.ID + ":*");
//		folder.addView(View.ID);
//		
//		layout.getViewLayout(NavigationView.ID).setCloseable(false);
		
		

		String editorArea = layout.getEditorArea();
		layout.addView(BPMServiceViewer.ID, IPageLayout.LEFT, 0.25f, editorArea);
//		layout.addStandaloneView(BPMServiceViewer.ID,  true, IPageLayout.LEFT, 0.25f, editorArea);
//		layout.getFolderForView(NavigationView.ID);
//		layout.getViewLayout(BPMServiceViewer.ID).setCloseable(true);
//		System.out.println("mmmmmmm=========");
	}
}
