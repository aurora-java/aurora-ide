package aurora.ide.editor;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IEditorPart;

import aurora.ide.refactoring.ui.action.DelBmFieldAciton;
import aurora.ide.refactoring.ui.action.RenameBmFieldAciton;

public class BmEditorContributor extends BaseCompositeMapEditorContributor {

	private static final String BM_REFACTORING = "BM_REFACTORING";

	public BmEditorContributor() {
		System.out.println();
	}

	public void setActivePage(IEditorPart part) {
		super.setActivePage(part);
		this.contributeToMenu(this.getActionBars().getMenuManager());
	}

	@Override
	public void contributeToMenu(IMenuManager menuManager) {
		super.contributeToMenu(menuManager);
//		menuManager.getItems();
//		super.contributeToMenu(menuManager);
//		menuManager.add(new Separator(BM_REFACTORING));
//		menuManager.add(new RenameBmFieldAciton());
//		menuManager.add(new DelBmFieldAciton());
//		menuManager.addMenuListener(new IMenuListener() {
//
//			public void menuAboutToShow(IMenuManager manager) {
//				System.out.println();
//			}
//		});
//		menuManager.appendToGroup(BM_REFACTORING, new RenameBmFieldAciton());
//		menuManager.appendToGroup(BM_REFACTORING, new DelBmFieldAciton());
		
		//
		// IAction action;
		//
		// action = getActionRegistry().getAction(ActionFactory.COPY.getId());
		// menu.appendToGroup(GEFActionConstants.GROUP_COPY, action);
		// action = getActionRegistry().getAction(ActionFactory.PASTE.getId());
		// menu.appendToGroup(GEFActionConstants.GROUP_COPY, action);
		//
		// // action = getActionRegistry().getAction(SaveAsImageAction.ID);
		// // menu.appendToGroup(GEFActionConstants.GROUP_COPY, action);
		// //
		// // action = getActionRegistry().getAction(CopyAsImageAction.ID);
		// // menu.appendToGroup(GEFActionConstants.GROUP_COPY, action);
		//
		//
		//
		// action = getActionRegistry().getAction(ActionFactory.UNDO.getId());
		// menu.appendToGroup(GEFActionConstants.GROUP_UNDO, action);
		//
		// action = getActionRegistry().getAction(ActionFactory.REDO.getId());
		// menu.appendToGroup(GEFActionConstants.GROUP_UNDO, action);
		//
		// action = getActionRegistry().getAction(ActionFactory.DELETE.getId());
		// if (action.isEnabled())
		// menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
		// if (selection instanceof IStructuredSelection) {
		// IStructuredSelection ss = (IStructuredSelection) selection;
		// Object ele = ss.getFirstElement();
		// if (ele instanceof ComponentPart) {
		// if (ele instanceof ViewDiagramPart)
		// return;
		// MenuManager typeManager = new MenuManager("TypeChange");
		// menu.appendToGroup(GEFActionConstants.GROUP_EDIT, typeManager);
		// AuroraComponent model = (AuroraComponent) ((ComponentPart) ele)
		// .getModel();
		// TypeChangeUtil tc = new TypeChangeUtil(commandStack);
		// for (Action a : tc.getActionFor(model))
		// typeManager.add(a);
		//
		// }
		// }
		//
		//

	}
}
