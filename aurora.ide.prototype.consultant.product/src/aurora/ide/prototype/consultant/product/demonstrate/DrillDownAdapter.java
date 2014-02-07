package aurora.ide.prototype.consultant.product.demonstrate;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchImages;

import aurora.ide.meta.gef.editors.wizard.dialog.DemonstratingDialog;

public class DrillDownAdapter {

	private DemonstratingDialog demonstratingDialog;

	private ToolItem homeItem;

	private ToolItem backItem;

	private ToolItem forwardItem;

	private UIPInput curentInput;

	private File home;

	/**
	 * Allocates a new DrillDownTreePart.
	 * 
	 * @param tree
	 *            the target tree for refocusing
	 */
	public DrillDownAdapter(DemonstratingDialog demonstratingDialog, File home) {
		this.demonstratingDialog = demonstratingDialog;
		this.home = home;
	}

	/**
	 * Adds actions for "go back", "go home", and "go into" to a menu manager.
	 * 
	 * @param manager
	 *            is the target manager to update
	 */
	public void addNavigationActions(ToolBar textToolBar) {
		createActions(textToolBar);
		updateNavigationButtons();
	}

	/**
	 * Returns whether "go back" is possible for child tree. This is only
	 * possible if the client has performed one or more drilling operations.
	 * 
	 * @return <code>true</code> if "go back" is possible; <code>false</code>
	 *         otherwise
	 */
	public boolean canGoBack() {

		if (curentInput != null) {
			return curentInput.getPrevious() != null;
		}
		return false;
	}

	/**
	 * Returns whether "go home" is possible for child tree. This is only
	 * possible if the client has performed one or more drilling operations.
	 * 
	 * @return <code>true</code> if "go home" is possible; <code>false</code>
	 *         otherwise
	 */
	public boolean canGoHome() {
		return home != null;
	}

	/**
	 * Returns whether "go into" is possible for child tree. This is only
	 * possible if the current selection in the client has one item and it has
	 * children.
	 * 
	 * @return <code>true</code> if "go into" is possible; <code>false</code>
	 *         otherwise
	 */
	public boolean canGoForward() {
		if (curentInput != null) {
			return curentInput.getNext() != null;
		}
		return false;
	}

	/**
	 * Create the actions for navigation.
	 * 
	 */
	private void createActions(ToolBar textToolBar) {
		// Only do this once.

		if (homeItem != null) {
			return;
		}
		ISharedImages images = PlatformUI.getWorkbench().getSharedImages();
		backItem = new ToolItem(textToolBar, SWT.RIGHT);
		backItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				goBack();
			}
		});
		backItem.setToolTipText("Back");
		backItem.setImage(images.getImage(ISharedImages.IMG_TOOL_BACK));

		homeItem = new ToolItem(textToolBar, SWT.RIGHT);
		homeItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				goHome();
			}
		});
		homeItem.setToolTipText("Home");
		homeItem.setImage(WorkbenchImages
				.getImage(ISharedImages.IMG_ETOOL_HOME_NAV));

		forwardItem = new ToolItem(textToolBar, SWT.RIGHT);
		forwardItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				goForward();
			}
		});

		forwardItem.setToolTipText("Forward");
		forwardItem.setImage(images.getImage(ISharedImages.IMG_TOOL_FORWARD));

		updateNavigationButtons();
	}

	/**
	 * Reverts the input for the tree back to the state when <code>goInto</code>
	 * was last called.
	 * <p>
	 * A frame is removed from the drill stack. Then that frame is used to reset
	 * the input and expansion state for the child tree.
	 * </p>
	 */
	public void goBack() {
		if (curentInput != null && curentInput.getPrevious() != null) {
			UIPInput previous = curentInput.getPrevious();
			this.demonstratingDialog.setInput(previous);
		}
		updateNavigationButtons();
	}

	/**
	 * Reverts the input for the tree back to the state when the adapter was
	 * created.
	 * <p>
	 * All of the frames are removed from the drill stack. Then the oldest frame
	 * is used to reset the input and expansion state for the child tree.
	 * </p>
	 */
	public void goHome() {
		if (home != null) {
			this.demonstratingDialog.setInput(home);
		}
		updateNavigationButtons();
	}

	/**
	 * Sets the input for the tree to the current selection.
	 * <p>
	 * The current input and expansion state are saved in a frame and added to
	 * the drill stack. Then the input for the tree is changed to be the current
	 * selection. The expansion state for the tree is maintained during the
	 * operation.
	 * </p>
	 * <p>
	 * On return the client may revert back to the previous state by invoking
	 * <code>goBack</code> or <code>goHome</code>.
	 * </p>
	 */
	public void goForward() {
		if (curentInput != null && curentInput.getNext() != null) {
			UIPInput next = curentInput.getNext();
			this.demonstratingDialog.setInput(next);
		}
		updateNavigationButtons();
	}

	public void inputChanged(UIPInput input) {
		curentInput = input;
		updateNavigationButtons();
	}

	/**
	 * Updates the enabled state for each navigation button.
	 */
	protected void updateNavigationButtons() {
		if (homeItem != null) {
			homeItem.setEnabled(canGoHome());
			backItem.setEnabled(canGoBack());
			forwardItem.setEnabled(canGoForward());
		}
	}

}
