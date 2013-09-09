package aurora.ide.prototype.consultant.product;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import aurora.ide.prototype.consultant.view.NavigationView;


public class Perspective implements IPerspectiveFactory {

	/**
	 * The ID of the perspective as specified in the extension.
	 */
	public static final String ID = "aurora.ide.prototype.consultant.product.perspective";

	public void createInitialLayout(IPageLayout layout) {
//		String editorArea = layout.getEditorArea();
//		layout.setEditorAreaVisible(false);
		
//		layout.addStandaloneView(NavigationView.ID,  false, IPageLayout.LEFT, 0.25f, editorArea);
//		IFolderLayout folder = layout.createFolder("messages", IPageLayout.TOP, 0.5f, editorArea);
//		folder.addPlaceholder(View.ID + ":*");
//		folder.addView(View.ID);
//		
//		layout.addStandaloneView("org.eclipse.ui.navigator.ProjectExplorer",  true, IPageLayout.LEFT, 0.25f, editorArea);
//		IFolderLayout folder = layout.createFolder("messages", IPageLayout.TOP, 0.5f, editorArea);
//		folder.addPlaceholder(View.ID + ":*");
//		folder.addView(View.ID);
//		org.eclipse.ui.navigator.ProjectExplorer
		
//		layout.getViewLayout(NavigationView.ID).setCloseable(false);
		
		

		String editorArea = layout.getEditorArea();
//		layout.setEditorAreaVisible(false);
		
		layout.addStandaloneView(NavigationView.ID,  true, IPageLayout.LEFT, 0.25f, editorArea);
//		layout.addStandaloneView(ProjectExplorer.VIEW_ID,  true, IPageLayout.LEFT, 0.25f, editorArea);
//		ProjectExplorer
		
//		layout.getViewLayout(NavigationView.ID).setCloseable(true);
	
		
	}
}
