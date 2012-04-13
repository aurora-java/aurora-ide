package aurora.ide.meta.gef.designer.wizard;

import java.util.ArrayList;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import aurora.ide.meta.gef.designer.IDesignerConst;

public class ExtensionWizardPage extends WizardPage {

	private Tree tree;
	private Text text;
	private String[] infos = IDesignerConst.AE_TYPES;
	private String emptyInfo = "<select an item for detail..>";

	/**
	 * Create the wizard.
	 */
	public ExtensionWizardPage() {
		super("ExtensionWizardPage");
		setTitle("自动生成扩展bm");
		setDescription("Wizard Page description");
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);

		tree = new Tree(container, SWT.BORDER | SWT.CHECK);
		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem[] sels = tree.getSelection();
				if (sels.length > 0) {
					for (int i = 0; i < tree.getItemCount(); i++) {
						if (tree.getItem(i) == sels[0]) {
							text.setText(infos[i]);
							return;
						}
					}
				}
				text.setText(emptyInfo);
			}
		});
		tree.setBackground(container.getBackground());
		tree.setBounds(29, 20, 401, 93);
		for (int i = 0; i < IDesignerConst.AE_TYPES.length; i++) {
			TreeItem ti = new TreeItem(tree, SWT.NONE);
			ti.setText("generate _for_" + IDesignerConst.AE_TYPES[i] + ".bm");
			ti.setData(IDesignerConst.AE_TYPES[i]);
		}
		Button btnSelectAll = new Button(container, SWT.NONE);
		btnSelectAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (TreeItem ti : tree.getItems())
					ti.setChecked(true);
			}
		});
		btnSelectAll.setBounds(451, 32, 98, 25);
		btnSelectAll.setText("Select All");

		Button btnDesselectAll = new Button(container, SWT.NONE);
		btnDesselectAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (TreeItem ti : tree.getItems())
					ti.setChecked(false);
			}
		});
		btnDesselectAll.setBounds(451, 71, 98, 25);
		btnDesselectAll.setText("Deselect All");

		text = new Text(container, SWT.READ_ONLY | SWT.MULTI);
		text.setBackground(container.getBackground());
		text.setBounds(29, 129, 429, 126);
		text.setText(emptyInfo);
	}

	public String[] getUserSelection() {
		ArrayList<String> als = new ArrayList<String>();
		for (int i = 0; i < tree.getItemCount(); i++) {
			TreeItem ti = tree.getItem(i);
			if (ti.getChecked())
				als.add((String) ti.getData());
		}
		String[] ss = new String[als.size()];
		als.toArray(ss);
		return ss;
	}
}
