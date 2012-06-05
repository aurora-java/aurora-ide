package aurora.ide.meta.gef.designer.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.designer.editor.LookupCodeViewer;

public class CreateSyscodeWizardPage extends WizardPage {

	private CompositeMap codeMap;
	private LookupCodeViewer editor;
	private boolean init = false;

	/**
	 * Create the wizard.
	 */
	public CreateSyscodeWizardPage() {
		super("wizardPage");
		setTitle("Auto register SYS_CODE");
		setDescription("Select which sys_code(s) you want to create.\nNote that, if the syscode already exists in database, then it will be delete before create without prompt.");
	}

	public void setCodeMap(CompositeMap map) {
		this.codeMap = map;
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(2, false));
		editor = new LookupCodeViewer(container, SWT.CHECK | SWT.BORDER);
		Tree tree = editor.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 4));
		editor.setInput(codeMap);
		editor.refresh();
		setControl(container);

		Button btnNewButton = new Button(container, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectAll();
			}
		});
		btnNewButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnNewButton.setText("Select All");

		Button btnNewButton_2 = new Button(container, SWT.NONE);
		btnNewButton_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectNone();
			}
		});
		btnNewButton_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnNewButton_2.setText("Select None");

		Button btnNewButton_3 = new Button(container, SWT.NONE);
		btnNewButton_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectByDatabase();
			}
		});
		btnNewButton_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnNewButton_3.setText("Default");
		new Label(container, SWT.NONE);
	}

	private void selectAll() {
		for (TreeItem ti : editor.getTree().getItems()) {
			ti.setChecked(true);
		}
	}

	private void selectNone() {
		for (TreeItem ti : editor.getTree().getItems()) {
			ti.setChecked(false);
		}
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (!init) {
			init = true;
			selectByDatabase();
		}
	}

	private void selectByDatabase() {
		for (TreeItem ti : editor.getTree().getItems()) {
			CompositeMap m = (CompositeMap) ti.getData();
			ti.setChecked(!m.getBoolean("exists"));
		}
	}

	public List<CompositeMap> getCodeToCreate() {
		List<CompositeMap> list = new ArrayList<CompositeMap>();
		for (TreeItem ti : editor.getTree().getItems()) {
			if (ti.getChecked()) {
				list.add((CompositeMap) ti.getData());
			}
		}
		return list;
	}
}
