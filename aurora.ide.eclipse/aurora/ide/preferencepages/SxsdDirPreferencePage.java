package aurora.ide.preferencepages;


import java.io.File;
import java.util.StringTokenizer;


import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import aurora.ide.AuroraPlugin;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.LoadSchemaManager;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.navigator.action.SxsdValidVisablePropertyTester;


public class SxsdDirPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	public static final String SXSD_DIRECTORY = "SXSD_DIRECTORY";
	private List pathList;
	private Button removeButton;
	private Button editButton;
	private final static String delimiter = ",";

	public SxsdDirPreferencePage() {
		super();

		// Set the preference store for the preference page.
		IPreferenceStore store = AuroraPlugin.getDefault().getPreferenceStore();
		setPreferenceStore(store);
	}

	protected Control createContents(Composite parent) {
		Composite top = new Composite(parent, SWT.LEFT);
		top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Sets the layout for the top composite's
		// children to populate.
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		top.setLayout(gridLayout);

		GridData gridData = new GridData();

		Label listLabel = new Label(top, SWT.NONE);
		listLabel.setText(LocaleMessage.getString("sxsd.builtin-package.list"));
		gridData.horizontalSpan = 2;
		listLabel.setLayoutData(gridData);

		pathList = new List(top, SWT.BORDER);
		pathList.setItems(getSxsdPaths());

		// Create a data that takes up the extra space
		// in the dialog and spans both columns.
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		pathList.setLayoutData(gridData);

		pathList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectionChanged();
			}
		});

		// Create a composite for the add and remove buttons.
		Composite buttonGroup = new Composite(top, SWT.NONE);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		buttonGroup.setLayoutData(gridData);

		RowLayout rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		rowLayout.pack = false;
		buttonGroup.setLayout(rowLayout);

		Button addButton = new Button(buttonGroup, SWT.PUSH);
		addButton.setText(LocaleMessage.getString("add.path"));
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addButton();
			}
		});
		editButton = new Button(buttonGroup, SWT.PUSH);
		editButton.setText(LocaleMessage.getString("edit.path"));
		editButton.setEnabled(false);
		editButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editButton();
			}
		});

		removeButton = new Button(buttonGroup, SWT.PUSH);
		removeButton.setText(LocaleMessage.getString("remove.path"));
		removeButton.setEnabled(false);
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				pathList.remove(pathList.getSelectionIndex());
				selectionChanged();
			}
		});
		return top;
	}

	/**
	 * @see IWorkbenchPreferencePage#init
	 */
	public void init(IWorkbench wb) {
	}

	/*
	 * The user has pressed Ok or Apply. Store/apply this page's values
	 * appropriately.
	 */
	public boolean performOk() {
		if (!refreshSxsdPaths(pathList.getItems())) {
			return false;
		}
		return super.performOk();
	}

	private void addButton() {
		SxsdBuiltinPackagePathDialog dir = new SxsdBuiltinPackagePathDialog(
				new Shell());
		if (dir.open() == Window.OK) {
			String path = dir.getPath();
			if (path != null && path.length() > 0)
				pathList.add(path);
		}
	}

	private void editButton() {
		SxsdBuiltinPackagePathDialog dir = new SxsdBuiltinPackagePathDialog(
				new Shell(), pathList.getSelection()[0]);
		if (dir.open() == Window.OK) {
			pathList.remove(pathList.getSelectionIndex());
			String path = dir.getPath();
			if (path != null && path.length() > 0)
				pathList.add(path);
		}
	}

	/*
	 * Sets the enablement of the remove button depending on the selection in
	 * the list.
	 */
	private void selectionChanged() {
		int index = pathList.getSelectionIndex();
		removeButton.setEnabled(index >= 0);
		editButton.setEnabled(index >= 0);
	}

	public boolean refreshSxsdPaths(String[] elements) {
		if (!LoadSchemaManager.refeshSchemaManager(elements)) {
			return false;
		}
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < elements.length; i++) {
			buffer.append(elements[i]);
			buffer.append(delimiter);
		}
		AuroraPlugin.getDefault().getPreferenceStore().setValue(SXSD_DIRECTORY,
				buffer.toString());
		return true;
	}

	public static String[] getSxsdPaths() {
		String preferenceValue = AuroraPlugin.getDefault().getPreferenceStore()
				.getString(SXSD_DIRECTORY);
		StringTokenizer tokenizer = new StringTokenizer(preferenceValue,
				delimiter);
		int tokenCount = tokenizer.countTokens();
		String[] elements = new String[tokenCount];

		for (int i = 0; i < tokenCount; i++) {
			elements[i] = tokenizer.nextToken();
		}

		return elements;
	}

	class SxsdBuiltinPackagePathDialog extends TitleAreaDialog {
		Text pathText;
		String textPath;

		public SxsdBuiltinPackagePathDialog(Shell shell) {
			super(shell);
		}

		public SxsdBuiltinPackagePathDialog(Shell shell, String defaultPath) {
			super(shell);
			this.textPath = defaultPath;
		}

		protected Control createContents(Composite parent) {
			Control contents = super.createContents(parent);
			setTitle(LocaleMessage.getString("add.sxsd.builtin-package.path"));
			return contents;
		}

		/**
		 * Creates the gray area
		 * 
		 * @param parent
		 *            the parent composite
		 * @return Control
		 */
		protected Control createDialogArea(Composite parent) {
			Composite control = (Composite) super.createDialogArea(parent);

			Composite composite = new Composite(control, SWT.NONE);
			GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, true);
			composite.setLayoutData(gridData);
			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 3;
			composite.setLayout(gridLayout);

			Label propertyLabe = new Label(composite, SWT.NONE);
			propertyLabe.setText(LocaleMessage
					.getString("sxsd.builtin-package.path"));
			pathText = new Text(composite, SWT.BORDER);
			pathText.setEditable(false);
			gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			gridData.heightHint = 17;
			pathText.setLayoutData(gridData);
			if (textPath != null)
				pathText.setText(textPath);

			Button brower = new Button(composite, SWT.PUSH);
			setButtonLayoutData(brower);
			brower.setText(LocaleMessage.getString("openBrowse"));
			brower.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					DirectoryDialog directoryDialog = new DirectoryDialog(
							new Shell());
					if (pathText.getText() != null)
						directoryDialog.setFilterPath(pathText.getText());
					String path = directoryDialog.open();
					pathText.setText(path);
					textPath = path;
					Button OK = getButton(IDialogConstants.OK_ID);
					if (validSxsdDir(path)) {
						OK.setEnabled(true);
					}
				}

				public void widgetDefaultSelected(SelectionEvent e) {

				}
			});
			brower.setFocus();
			return composite;
		}

		public boolean validSxsdDir(String sxsdDir) {
			boolean isValid = SxsdValidVisablePropertyTester
					.isValidDir(new File(sxsdDir));
			if (!isValid) {
				DialogUtil.showErrorMessageBox("this.path.is.not.valid");
				return false;
			}
			return true;
		}

		/**
		 * Creates the buttons for the button bar
		 * 
		 * @param parent
		 *            the parent composite
		 */
		protected void createButtonsForButtonBar(Composite parent) {
			super.createButtonsForButtonBar(parent);
			Button OK = getButton(IDialogConstants.OK_ID);
			OK.setEnabled(false);
		}

		protected boolean isResizable() {
			return true;
		}

		public String getPath() {
			return textPath;
		}
	}
}
