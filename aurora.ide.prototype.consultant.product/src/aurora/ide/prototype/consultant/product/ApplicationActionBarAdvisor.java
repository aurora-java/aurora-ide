package aurora.ide.prototype.consultant.product;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import aurora.ide.libs.AuroraImagesUtils;
import aurora.ide.meta.gef.message.Messages;
import aurora.ide.prototype.consultant.product.action.DemonstrateAction;
import aurora.ide.prototype.consultant.product.action.ExportFSDAction;
import aurora.ide.prototype.consultant.product.action.GetHelpAction;
import aurora.ide.prototype.consultant.product.action.NewFileAction;
import aurora.ide.prototype.consultant.product.action.OpenFileAction;
import aurora.ide.prototype.consultant.product.action.ShowViewerAction;
import aurora.ide.prototype.consultant.product.action.UpdateQuickUIAction;
import aurora.ide.prototype.consultant.view.NavigationView;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	// Actions - important to allocate these only in makeActions, and then use
	// them
	// in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.
	private IWorkbenchAction exitAction;
	private IWorkbenchAction aboutAction;
	private IWorkbenchAction newWindowAction;
	private NewFileAction newFileAction;
	private OpenFileAction openFileAction;
	// private Action messagePopupAction;
	// private IWorkbenchAction printAction;
	private IWorkbenchAction saveAction;
	private IWorkbenchAction saveAsAction;
	private IWorkbenchAction introAction;
	private ExportFSDAction exportFSDAction;
	private IAction showUIPNavViewer;
	private IAction demonstrateAction;
	private UpdateQuickUIAction updateQuickUIAction;
	private GetHelpAction getHelpAction;

	// private TestAction testAction;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(final IWorkbenchWindow window) {

		exitAction = ActionFactory.QUIT.create(window);
		exitAction.setText(Messages.ApplicationActionBarAdvisor_4);
		exitAction.setToolTipText(Messages.ApplicationActionBarAdvisor_5);
		register(exitAction);

		aboutAction = ActionFactory.ABOUT.create(window);
		register(aboutAction);

		newWindowAction = ActionFactory.OPEN_NEW_WINDOW.create(window);
		newWindowAction.setText(Messages.ApplicationActionBarAdvisor_6);
		newWindowAction.setToolTipText(Messages.ApplicationActionBarAdvisor_7);
		register(newWindowAction);

		newFileAction = new NewFileAction(window,
				Messages.ApplicationActionBarAdvisor_0);
		register(newFileAction);

		openFileAction = new OpenFileAction(window,
				Messages.ApplicationActionBarAdvisor_1);
		register(openFileAction);

		exportFSDAction = new ExportFSDAction(window,
				Messages.ApplicationActionBarAdvisor_14);
		register(exportFSDAction);

		// messagePopupAction = new MessagePopupAction("Open Message", window);
		// register(messagePopupAction);

		// this.printAction = ActionFactory.PRINT.create(window);
		// register(this.printAction);

		this.saveAction = ActionFactory.SAVE.create(window);
		saveAction.setText(Messages.ApplicationActionBarAdvisor_8);
		saveAction.setToolTipText(Messages.ApplicationActionBarAdvisor_9);
		register(this.saveAction);

		this.saveAsAction = ActionFactory.SAVE_AS.create(window);
		saveAsAction.setText(Messages.ApplicationActionBarAdvisor_10);
		saveAsAction.setToolTipText(Messages.ApplicationActionBarAdvisor_11);
		register(this.saveAsAction);

		introAction = ActionFactory.INTRO.create(window);
		introAction.setText(Messages.ApplicationActionBarAdvisor_12);
		introAction.setToolTipText(Messages.ApplicationActionBarAdvisor_13);
		register(introAction);

		// testAction = new TestAction(window,
		// Messages.ApplicationActionBarAdvisor_1);

		this.showUIPNavViewer = new ShowViewerAction(
				window,
				Messages.ApplicationActionBarAdvisor_15,
				AuroraImagesUtils.getImageDescriptor("/meta.png"), NavigationView.ID); //$NON-NLS-2$ //$NON-NLS-1$
		showUIPNavViewer.setText(Messages.ApplicationActionBarAdvisor_17);
		showUIPNavViewer
				.setToolTipText(Messages.ApplicationActionBarAdvisor_18);
		register(this.showUIPNavViewer);

		demonstrateAction = new DemonstrateAction(window);

		register(demonstrateAction);

		this.updateQuickUIAction = new UpdateQuickUIAction(window,
				Messages.ApplicationActionBarAdvisor_15,
				AuroraImagesUtils.getImageDescriptor("/meta.png")); //$NON-NLS-2$ //$NON-NLS-1$
		updateQuickUIAction.setText(Messages.ApplicationActionBarAdvisor_21);
		updateQuickUIAction
				.setToolTipText(Messages.ApplicationActionBarAdvisor_22);
		register(this.updateQuickUIAction);

		this.getHelpAction = new GetHelpAction(window,
				Messages.ApplicationActionBarAdvisor_15,
				AuroraImagesUtils.getImageDescriptor("/meta.png")); //$NON-NLS-2$ //$NON-NLS-1$
		getHelpAction.setText(Messages.ApplicationActionBarAdvisor_24);
		getHelpAction.setToolTipText(Messages.ApplicationActionBarAdvisor_25);
		register(this.getHelpAction);

	}

	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager(
				Messages.ApplicationActionBarAdvisor_2,
				IWorkbenchActionConstants.M_FILE);
		MenuManager helpMenu = new MenuManager(
				Messages.ApplicationActionBarAdvisor_3,
				IWorkbenchActionConstants.M_HELP);
		menuBar.add(fileMenu);
		// Add a group marker indicating where action set menus will appear.
		// menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));

		MenuManager viewerMenu = new MenuManager(
				Messages.ApplicationActionBarAdvisor_19,
				IWorkbenchActionConstants.M_VIEW);
		menuBar.add(viewerMenu);
		viewerMenu.add(showUIPNavViewer);

		menuBar.add(helpMenu);

		// File
		// fileMenu.add(newWindowAction);
		// fileMenu.add(new Separator());
		fileMenu.add(newFileAction);
		fileMenu.add(openFileAction);

		fileMenu.add(new Separator());
		fileMenu.add(saveAction);
		fileMenu.add(saveAsAction);
		fileMenu.add(new Separator());
		fileMenu.add(exitAction);
		// Help
		helpMenu.add(introAction);
		helpMenu.add(this.updateQuickUIAction);
		helpMenu.add(this.getHelpAction);
		helpMenu.add(aboutAction);
	}

	protected void fillCoolBar(ICoolBarManager coolBar) {
		IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
		coolBar.add(new ToolBarContributionItem(toolbar, "main")); //$NON-NLS-1$
		toolbar.add(newFileAction);
		toolbar.add(openFileAction);
		toolbar.add(saveAction);
		toolbar.add(saveAsAction);
		toolbar.add(introAction);
		toolbar.add(exportFSDAction);
		toolbar.add(demonstrateAction);

		// toolbar.add(testAction);
	}
}
