package aurora.ide.meta.gef.designer.wizard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import aurora.ide.meta.gef.designer.DesignerUtil;
import aurora.ide.meta.gef.designer.IDesignerConst;
import aurora.ide.meta.gef.designer.editor.SimpleBMModelViewer;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.Record;

public class ExtensionWizardPage extends WizardPage implements
		SelectionListener {

	private String[] infos = { Messages.ExtensionWizardPage_1,
			Messages.ExtensionWizardPage_0, Messages.ExtensionWizardPage_2 };
	private String[] defaultSelection = {};
	private BMModel model;
	private TabFolder tabFolder;
	private Button[] checkBoxs;
	private SimpleBMModelViewer[] viewers = new SimpleBMModelViewer[3];

	/**
	 * Create the wizard.
	 */
	public ExtensionWizardPage() {
		super("ExtensionWizardPage"); //$NON-NLS-1$
		setTitle(Messages.ExtensionWizardPage_6);
		setDescription("Wizard Page description"); //$NON-NLS-1$
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
		List<String> list = Arrays.asList(defaultSelection);
		Composite com = new Composite(container, 0);
		com.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 2));
		com.setLayout(new GridLayout(1, true));
		checkBoxs = new Button[IDesignerConst.AE_TYPES.length];
		for (int i = 0; i < checkBoxs.length; i++) {
			checkBoxs[i] = new Button(com, SWT.CHECK);
			checkBoxs[i].setText("generate _for_" + IDesignerConst.AE_TYPES[i]
					+ ".bm");
			checkBoxs[i].setData(IDesignerConst.AE_TYPES[i]);
			checkBoxs[i]
					.setSelection(list.indexOf(IDesignerConst.AE_TYPES[i]) != -1);
			checkBoxs[i].setToolTipText(infos[i]);
		}
		createTopRight(container);
		createBottom(container);

	}

	private void createTopRight(Composite container) {
		Button btnSelectAll = new Button(container, SWT.NONE);
		btnSelectAll.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false,
				false, 1, 1));
		btnSelectAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (Button b : checkBoxs)
					b.setSelection(true);
			}
		});
		btnSelectAll.setText("Select All"); //$NON-NLS-1$

		Button btnDesselectAll = new Button(container, SWT.NONE);
		btnDesselectAll.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnDesselectAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (Button b : checkBoxs)
					b.setSelection(false);
			}
		});
		btnDesselectAll.setText("Deselect All"); //$NON-NLS-1$
	}

	private void createBottom(Composite container) {
		tabFolder = new TabFolder(container, 0);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		for (int i = 0; i < viewers.length; i++) {
			TabItem ti = new TabItem(tabFolder, 0);
			viewers[i] = new SimpleBMModelViewer(tabFolder, 0);
			viewers[i].setDisplayMode((String) checkBoxs[i].getData());
			ti.setControl(viewers[i].getControl());
			ti.setText(IDesignerConst.AE_TYPES[i]);
		}
		tabFolder.addSelectionListener(this);
		widgetSelected(null);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		synModel();
	}

	private void synModel() {
		BaseInfoWizardPage prePage = (BaseInfoWizardPage) getPreviousPage();
		if (prePage == null)
			return;
		String shortName = prePage.getShortName();
		// reset
		model.setTitle(shortName);
		String pre = shortName;
		if (pre.length() > 3)
			pre = pre.substring(0, 3);
		model.setNamePrefix(pre + "_c");
		model.getPkRecord().setName(shortName + "_id");
		String[] preInput = prePage.getPreInput();

		// remove old
		List<Record> list = model.getRecordList();
		L1: for (int i = 0; i < list.size(); i++) {
			String p = list.get(i).getPrompt();
			for (String s : preInput) {
				if (p.equals(s))
					continue L1;
			}
			// System.out.println("remove old:" + p);
			list.remove(i--);
		}
		// create new
		L: for (String s : preInput) {
			for (Record r : model.getRecordList()) {
				if (r.getPrompt().equals(s))
					continue L;
			}
			// System.out.println("create new:" + s);
			model.add(DesignerUtil.createRecord(s));
		}
		// reset default display
		Record r = model.getDefaultDisplayRecord();
		if (r == null && model.getRecordList().size() > 0) {
			model.setDefaultDisplay(model.getRecordList().get(0).getPrompt());
		}
	}

	public void setDefaultSeletion(String[] strs) {
		this.defaultSelection = strs;
	}

	public String[] getUserSelection() {
		ArrayList<String> als = new ArrayList<String>();
		for (int i = 0; i < checkBoxs.length; i++) {
			if (checkBoxs[i].getSelection()) {
				als.add((String) checkBoxs[i].getData());
			}
		}
		String[] ss = new String[als.size()];
		als.toArray(ss);
		return ss;
	}

	public void widgetSelected(SelectionEvent e) {
		SimpleBMModelViewer v = viewers[tabFolder.getSelectionIndex()];
		if (v.getInput() == null)
			v.setInput(model);
		v.refresh();
	}

	public void widgetDefaultSelected(SelectionEvent e) {

	}

	public void setModel(BMModel model) {
		this.model = model;
	}
}
