package aurora.ide.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * This class is meant to serve as an example for how various contributions are
 * made to a perspective. Note that some of the extension point id's are
 * referred to as API constants while others are hardcoded and may be subject to
 * change.
 */
public class AuroraPerspective implements IPerspectiveFactory {
	public static final String PERSPECTIVE_ID = "aurora.ide.perspectives.AuroraPerspective";

	private IPageLayout factory;

	public AuroraPerspective() {
		super();
	}

	public void createInitialLayout(IPageLayout factory) {
		addPerspectiveShortcuts(factory);
		addFastViews(factory);
		this.factory = factory;
		addViews();
		addActionSets();
		addNewWizardShortcuts();
		addPerspectiveShortcuts();
		addViewShortcuts();
	}

	private void addViews() {
		// Creates the overall folder layout.
		// Note that each new Folder uses a percentage of the remaining
		// EditorArea.

		IFolderLayout bottom = factory.createFolder("bottomRight", // NON-NLS-1
				IPageLayout.BOTTOM, 0.75f, factory.getEditorArea());
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottom.addView("org.eclipse.ui.console.ConsoleView");

		IFolderLayout topLeft = factory.createFolder("topLeft", // NON-NLS-1
				IPageLayout.LEFT, 0.20f, factory.getEditorArea());
		topLeft.addView("org.eclipse.ui.navigator.ProjectExplorer");

		factory.addView("org.eclipse.ui.views.ContentOutline",
				IPageLayout.RIGHT, 0.76f, IPageLayout.ID_EDITOR_AREA);

		factory.addFastView("org.eclipse.team.ccvs.ui.RepositoriesView", 0.50f); // NON-NLS-1
		factory.addFastView("org.eclipse.team.sync.views.SynchronizeView",
				0.50f); // NON-NLS-1
	}

	private void addActionSets() {
		// factory.addActionSet("org.eclipse.debug.ui.launchActionSet"); //
		// NON-NLS-1
		// factory.addActionSet("org.eclipse.debug.ui.debugActionSet"); //
		// NON-NLS-1
		// factory.addActionSet("org.eclipse.debug.ui.profileActionSet"); //
		// NON-NLS-1
		// factory.addActionSet("org.eclipse.jdt.debug.ui.JDTDebugActionSet");
		// // NON-NLS-1
		// factory.addActionSet("org.eclipse.jdt.junit.JUnitActionSet"); //
		// NON-NLS-1
		// factory.addActionSet("org.eclipse.team.ui.actionSet"); // NON-NLS-1
		factory.addActionSet("org.eclipse.team.cvs.ui.CVSActionSet"); // NON-NLS-1
		// factory.addActionSet("org.eclipse.ant.ui.actionSet.presentation"); //
		// NON-NLS-1
		// factory.addActionSet("org.eclipse.jdt.ui.JavaActionSet");
		// factory.addActionSet("org.eclipse.jdt.ui.JavaElementCreationActionSet");
		factory.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET); // NON-NLS-1
	}

	private void addPerspectiveShortcuts() {
		// factory.addPerspectiveShortcut("org.eclipse.jst.j2ee.J2EEPerspective");
		factory.addPerspectiveShortcut("org.eclipse.team.ui.TeamSynchronizingPerspective"); // NON-NLS-1
		factory.addPerspectiveShortcut("org.eclipse.team.cvs.ui.cvsPerspective");
	}

	private void addNewWizardShortcuts() {
		factory.addNewWizardShortcut("org.eclipse.team.cvs.ui.newProjectCheckout");// NON-NLS-1
		factory.addNewWizardShortcut("aurora.ide.wizard.ServiceNewWizard");// NON-NLS-1
		factory.addNewWizardShortcut("aurora.ide.wizard.BMFromSQLWizard");// NON-NLS-1
		factory.addNewWizardShortcut("aurora.ide.wizard.BMFromDBWizard");// NON-NLS-1
		factory.addNewWizardShortcut("aurora.ide.wizard.BMFromProcedure");// NON-NLS-1
		factory.addNewWizardShortcut("aurora.ide.wizard.CreateFormGridWizard");// NON-NLS-1
		factory.addNewWizardShortcut("aurora.ide.wizard.NewBMQ");// NON-NLS-1
		factory.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");// NON-NLS-1
		factory.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");// NON-NLS-1

	}

	private void addViewShortcuts() {
		factory.addShowViewShortcut("org.eclipse.team.ccvs.ui.AnnotateView");
		factory.addShowViewShortcut("org.eclipse.team.ui.GenericHistoryView"); // NON-NLS-1
		factory.addShowViewShortcut("org.eclipse.ui.console.ConsoleView");
		factory.addShowViewShortcut("org.eclipse.jdt.ui.PackageExplorer");
		factory.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		factory.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		factory.addShowViewShortcut("aurora.ide.viewer.statistics.StatisticsView");
	}

	private void addFastViews(IPageLayout layout) {
//		layout.addFastView("org.eclipse.wst.server.ui.ServersView");
	}

	private void addPerspectiveShortcuts(IPageLayout layout) {
		layout.addPerspectiveShortcut("org.eclipse.jdt.ui.JavaPerspective");
	}
}
