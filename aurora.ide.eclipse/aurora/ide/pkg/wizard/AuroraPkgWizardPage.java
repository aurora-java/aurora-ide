package aurora.ide.pkg.wizard;

import java.util.ArrayList;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class AuroraPkgWizardPage extends WizardPage implements ModifyListener {
	private Text text;
	private String[] baseFileNames = { "class-registry", "package", "instance" };
	private boolean[] selectionStats = { true, true, false };
	private Button[] chkBtns = new Button[baseFileNames.length];
	private IFolder folder;

	/**
	 * Create the wizard.
	 */
	public AuroraPkgWizardPage() {
		super("wizardPage");
		setTitle("Create Aurora Package");
		setDescription("This wizard will help you create a simple aurora package.");
		setPageComplete(false);
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(2, false));

		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblNewLabel.setText("Package:");

		text = new Text(container, SWT.BORDER);
		text.addModifyListener(this);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Group grpAutoCreate = new Group(container, SWT.NONE);
		grpAutoCreate.setText("auto create");
		grpAutoCreate.setLayout(new GridLayout(1, false));
		grpAutoCreate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 2, 1));

		SelectionAdapter sa = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button b = (Button) e.getSource();
				Integer index = (Integer) b.getData("index");
				selectionStats[index] = b.getSelection();
			}
		};

		for (int i = 0; i < chkBtns.length; i++) {
			Button b = chkBtns[i] = new Button(grpAutoCreate, SWT.CHECK);
			b.setSelection(selectionStats[i]);
			b.setText(baseFileNames[i]);
			b.setData("index", i);
			b.addSelectionListener(sa);
		}
	}

	public void modifyText(ModifyEvent e) {
		String pkgName = text.getText();
		validatePkgName(pkgName);
	}

	private void validatePkgName(String name) {
		String error = null;
		if (name.length() == 0) {
			error = "Please Input Package Name.";
		} else if (folder.findMember(name) != null) {
			error = "Package '" + name + "' already exisis.";
		} else if (!isLegal(name)) {
			error = "'" + name + "' is not valid.(a valid name like 'aa.bb')";
		}
		setErrorMessage(error);
		setPageComplete(error == null);
	}

	protected boolean isLegal(String str) {
		if (str.length() == 0)
			return false;
		int dotIdx = -1;
		if (Character.isJavaIdentifierStart(str.charAt(0))) {
			for (int i = 1; i < str.length(); i++)
				if (Character.isJavaIdentifierPart(str.charAt(i))) {
				} else if (str.charAt(i) == '.') {
					dotIdx = i;
				} else
					return false;
		}
		if (dotIdx <= 0 || dotIdx >= str.length() - 1)
			return false;
		return true;
	}

	public void setInitFolder(IFolder folder) {
		this.folder = folder;
	}

	public String getPkgName() {
		return text.getText();
	}

	public ArrayList<String> getSelection() {
		ArrayList<String> als = new ArrayList<String>();
		for (int i = 0; i < baseFileNames.length; i++) {
			if (selectionStats[i])
				als.add(baseFileNames[i]);
		}
		return als;
	}
}
