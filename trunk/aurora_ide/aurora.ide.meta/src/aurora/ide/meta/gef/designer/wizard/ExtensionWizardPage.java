package aurora.ide.meta.gef.designer.wizard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import aurora.ide.meta.gef.designer.DesignerUtil;
import aurora.ide.meta.gef.designer.IDesignerConst;
import aurora.ide.meta.gef.designer.editor.SimpleBMModelViewer;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.Record;

public class ExtensionWizardPage extends WizardPage implements
		MouseTrackListener, MouseMoveListener, SelectionListener {

	private Tree tree;
	private String[] infos = { Messages.ExtensionWizardPage_1,
			Messages.ExtensionWizardPage_0, Messages.ExtensionWizardPage_2 };
	private String[] defaultSelection = {};
	private Shell tip;
	private Label label = null;
	private BMModel model;
	private Composite stackComposite;
	private StackLayout layout;
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
		tree = new Tree(container, SWT.BORDER | SWT.CHECK | SWT.SINGLE);
		for (int i = 0; i < IDesignerConst.AE_TYPES.length; i++) {
			TreeItem ti = new TreeItem(tree, SWT.NONE);
			ti.setText("generate _for_" + IDesignerConst.AE_TYPES[i] + ".bm"); //$NON-NLS-1$ //$NON-NLS-2$
			ti.setData(IDesignerConst.AE_TYPES[i]);
			ti.setChecked(list.indexOf(IDesignerConst.AE_TYPES[i]) != -1);
		}
		tree.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 2));
		tree.setBackground(container.getBackground());
		tree.addMouseMoveListener(this);
		tree.addMouseTrackListener(this);
		tree.addSelectionListener(this);
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
				for (TreeItem ti : tree.getItems())
					ti.setChecked(true);
			}
		});
		btnSelectAll.setText("Select All"); //$NON-NLS-1$

		Button btnDesselectAll = new Button(container, SWT.NONE);
		btnDesselectAll.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnDesselectAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (TreeItem ti : tree.getItems())
					ti.setChecked(false);
			}
		});
		btnDesselectAll.setText("Deselect All"); //$NON-NLS-1$
	}

	private void createBottom(Composite container) {
		stackComposite = new Composite(container, SWT.BORDER);
		stackComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));
		layout = new StackLayout();
		stackComposite.setLayout(layout);
		for (int i = 0; i < viewers.length; i++) {
			viewers[i] = new SimpleBMModelViewer(stackComposite, 0);
			viewers[i].setDisplayMode((String) tree.getItem(i).getData());
		}
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
		model.getPkRecord().setName(shortName + "_pk");
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
		for (int i = 0; i < tree.getItemCount(); i++) {
			TreeItem ti = tree.getItem(i);
			if (ti.getChecked())
				als.add((String) ti.getData());
		}
		String[] ss = new String[als.size()];
		als.toArray(ss);
		return ss;
	}

	public void mouseEnter(MouseEvent e) {
	}

	public void mouseExit(MouseEvent e) {
	}

	public void mouseHover(MouseEvent e) {
		TreeItem item = tree.getItem(new Point(e.x, e.y));
		if (item != null) {
			int idx = tree.indexOf(item);
			if (tip != null && !tip.isDisposed())
				tip.dispose();
			Display display = Display.getDefault();
			Shell shell = new Shell(display);
			tip = new Shell(shell, SWT.ON_TOP | SWT.NO_FOCUS | SWT.TOOL);
			tip.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
			FillLayout layout = new FillLayout();
			layout.marginWidth = 2;
			tip.setLayout(layout);
			label = new Label(tip, SWT.NONE);
			label.setForeground(display
					.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
			label.setBackground(display
					.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
			label.setText(infos[idx]);
			Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			Rectangle rect = item.getBounds(0);
			Point pt = tree
					.toDisplay(rect.x + rect.width, rect.y + rect.height);
			tip.setBounds(pt.x + 10, pt.y, size.x, size.y);
			tip.setVisible(true);
		}
	}

	public void mouseMove(MouseEvent e) {
		if (tip != null) {
			tip.dispose();
			tip = null;
			label = null;
		}
	}

	public void widgetSelected(SelectionEvent e) {
		TreeItem[] sels = tree.getSelection();
		// tree is single selection
		if (sels.length > 0) {
			int idx = tree.indexOf(sels[0]);
			SimpleBMModelViewer v = viewers[idx];
			layout.topControl = v.getTable();
			stackComposite.layout();
			if (v.getInput() == null)
				v.setInput(model);
			v.refresh();
		}
	}

	public void widgetDefaultSelected(SelectionEvent e) {

	}

	public void setModel(BMModel model) {
		this.model = model;
	}
}
