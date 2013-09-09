package aurora.ide.prototype.consultant.view;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.navigator.CommonNavigator;

public class CNFNavigator extends CommonNavigator {
	public static final String ID = "aurora.ide.prototype.consultant.view.navigationView";

	@Override
	protected IAdaptable getInitialInput() {
		Root root = new Root();
		root.addChild(new Node(new Path("/Users/shiliyan/Desktop/Uip_pages")));
		
//		if (parentElement instanceof Root) {
//			File[] files = ((Root) parentElement).getFiles();
//			CNFContentHelper.sortFiles(files);
//			return files;
//		}
//		if (parentElement instanceof File) {
//			return CNFContentHelper.getDirectoryList((File) parentElement);
//		}
		
		
		return root;

	}

	@Override
	public void init(IViewSite aSite, IMemento aMemento)
			throws PartInitException {
		super.init(aSite, aMemento);
	}

	@Override
	protected void initListeners(TreeViewer viewer) {
		super.initListeners(viewer);
	}

	@Override
	protected void handleDoubleClick(DoubleClickEvent anEvent) {
		super.handleDoubleClick(anEvent);
	}
}
