package aurora.ide.meta.gef.editors.actions;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.actions.ActionFactory;

import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.meta.gef.editors.parts.ViewDiagramPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;

/**
 * Provides a context menu for the flow editor.
 * 
 * @author
 */
public class ViewContextMenuProvider extends ContextMenuProvider {

	private ActionRegistry actionRegistry;
	private ISelection selection;
	private CommandStack commandStack;

	/**
	 * Creates a new FlowContextMenuProvider assoicated with the given viewer
	 * and action registry.
	 * 
	 * @param viewer
	 *            the viewer
	 * @param registry
	 *            the action registry
	 */
	public ViewContextMenuProvider(EditPartViewer viewer,
			ActionRegistry registry) {
		super(viewer);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				selection = event.getSelection();

			}
		});
		commandStack = viewer.getEditDomain().getCommandStack();
		setActionRegistry(registry);
	}

	/**
	 * @see ContextMenuProvider#buildContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public void buildContextMenu(IMenuManager menu) {
		GEFActionConstants.addStandardActionGroups(menu);

		IAction action;
		
		action = getActionRegistry().getAction(ActionFactory.COPY.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_COPY, action);
		action = getActionRegistry().getAction(ActionFactory.PASTE.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_COPY, action);
		
//		action = getActionRegistry().getAction(SaveAsImageAction.ID);
//		menu.appendToGroup(GEFActionConstants.GROUP_COPY, action);
//		
//		action = getActionRegistry().getAction(CopyAsImageAction.ID);
//		menu.appendToGroup(GEFActionConstants.GROUP_COPY, action);
		
		
		
		action = getActionRegistry().getAction(ActionFactory.UNDO.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_UNDO, action);

		action = getActionRegistry().getAction(ActionFactory.REDO.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_UNDO, action);

		action = getActionRegistry().getAction(ActionFactory.DELETE.getId());
		if (action.isEnabled())
			menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			Object ele = ss.getFirstElement();
			if (ele instanceof ComponentPart) {
				if (ele instanceof ViewDiagramPart)
					return;
				MenuManager typeManager = new MenuManager("TypeChange");
				menu.appendToGroup(GEFActionConstants.GROUP_EDIT, typeManager);
				AuroraComponent model = (AuroraComponent) ((ComponentPart) ele)
						.getModel();
				TypeChangeUtil tc = new TypeChangeUtil(commandStack);
				for (Action a : tc.getActionFor(model))
					typeManager.add(a);

			}
		}

	}

	public ActionRegistry getActionRegistry() {
		return actionRegistry;
	}

	/**
	 * Sets the action registry
	 * 
	 * @param registry
	 *            the action registry
	 */
	public void setActionRegistry(ActionRegistry registry) {
		actionRegistry = registry;
	}

}
