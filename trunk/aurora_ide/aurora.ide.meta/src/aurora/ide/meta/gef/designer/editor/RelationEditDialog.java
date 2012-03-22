package aurora.ide.meta.gef.designer.editor;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import uncertain.composite.CompositeMap;
import aurora.ide.AuroraPlugin;
import aurora.ide.builder.ResourceUtil;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.Record;
import aurora.ide.meta.gef.designer.model.Relation;
import aurora.ide.meta.gef.editors.property.ResourceSelector;
import aurora.ide.meta.gef.editors.source.gen.DataSetFieldUtil;
import aurora.ide.meta.project.AuroraMetaProject;
import aurora.ide.search.core.Util;

public class RelationEditDialog extends Dialog {
	private Text text_relname;
	private Text text_refmodel;
	private BMModel model;
	private Relation relation;
	private Combo com_jointype;
	private AuroraMetaProject amproj;
	private IProject metaProject;
	private IProject auroraProject;
	private ComboViewer bmFieldComboViewer;
	private ComboViewer localFieldComboViewer;
	private ArrayList<CompositeMap> bmfieldList = null;
	private ArrayList<CompositeMap> refFieldList = new ArrayList<CompositeMap>();
	private ListViewer bmFieldListViewer;
	private ListViewer refFieldListViewer;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public RelationEditDialog(Shell parentShell) {
		super(parentShell);
	}

	public void setModel(BMModel model) {
		this.model = model;
	}

	public void setRelation(Relation rel) {
		this.relation = rel;
	}

	public Relation getRelation() {
		return relation;
	}

	private void beforeCreate() {
		if (relation == null) {
			getShell().setText("Create New Relation");
			relation = new Relation();
		} else
			getShell().setText("Edit Relation");
		metaProject = AuroraPlugin.getActiveIFile().getProject();
		amproj = new AuroraMetaProject(metaProject);
		try {
			auroraProject = amproj.getAuroraProject();
		} catch (ResourceNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		beforeCreate();
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 3;

		Label label = new Label(container, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		label.setText("关系名:");

		text_relname = new Text(container, SWT.BORDER);
		text_relname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		text_relname.setText(relation.getName());
		new Label(container, SWT.NONE);

		Label label_1 = new Label(container, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		label_1.setText("引用表:");

		text_refmodel = new Text(container, SWT.BORDER | SWT.READ_ONLY) {
			public void setText(String str) {
				if (str.equals(getText()))
					return;
				super.setText(str);
			}

			protected void checkSubclass() {
			}
		};
		text_refmodel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		text_refmodel.addModifyListener(new RefModelModifyListener());
		// text_refmodel.setText(relation.getRefTable());

		Button btnNewButton = new Button(container, SWT.NONE);
		btnNewButton.addSelectionListener(new BmSelectListener());
		btnNewButton.setText("浏览");

		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblNewLabel.setText("本地字段:");

		localFieldComboViewer = new ComboViewer(container, SWT.READ_ONLY);
		Combo combo_1 = localFieldComboViewer.getCombo();
		localFieldComboViewer.setContentProvider(new BMModelContentProvider(
				BMModel.RECORD));
		localFieldComboViewer.setLabelProvider(new BMModelLabelProvider(
				BMModel.RECORD));
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		localFieldComboViewer.setInput(model);
		Record[] rs = model.getRecords();
		for (int i = 0; i < rs.length; i++)
			if (rs[i].getPrompt().equals(relation.getLocalField())) {
				combo_1.select(i);
				break;
			}
		new Label(container, SWT.NONE);

		Label lblNewLabel_1 = new Label(container, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblNewLabel_1.setText("外部字段:");

		bmFieldComboViewer = new ComboViewer(container, SWT.READ_ONLY);
		Combo combo = bmFieldComboViewer.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		bmFieldComboViewer.setContentProvider(new BmFieldContentProvider());
		bmFieldComboViewer.setLabelProvider(new BmFieldLabelProvider());
		text_refmodel.setText(relation.getRefTable());// write it here , this
														// can cause some event.
		new Label(container, SWT.NONE);

		Label lblNewLabel_2 = new Label(container, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblNewLabel_2.setText("关联类型:");

		com_jointype = new Combo(container, SWT.READ_ONLY);
		String[] items = new String[] { "LEFT OUTER", "RIGHT OUTER",
				"FULL OUTER", "INNER", "CROSS" };
		com_jointype.setItems(items);
		com_jointype.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		int idx = Arrays.asList(items).indexOf(relation.getJoinType());
		if (idx == -1)
			idx = 0;
		com_jointype.select(idx);
		new Label(container, SWT.NONE);

		createRefFieldGroup(container);

		return container;
	}

	private void createRefFieldGroup(Composite container) {
		Group composite = new Group(container, SWT.NONE);
		composite.setText("引用字段");
		composite.setLayout(null);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3,
				1));

		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setBounds(8, 20, 48, 15);
		label_2.setText("所有字段");

		Label label_3 = new Label(composite, SWT.NONE);
		label_3.setBounds(224, 20, 48, 15);
		label_3.setText("引用字段");

		bmFieldListViewer = new ListViewer(composite, SWT.BORDER | SWT.V_SCROLL
				| SWT.MULTI);
		bmFieldListViewer.setContentProvider(new BmFieldContentProvider());
		bmFieldListViewer.setLabelProvider(new BmFieldLabelProvider());
		if (bmfieldList != null)
			bmFieldListViewer.setInput(bmfieldList.clone());
		List list = bmFieldListViewer.getList();
		list.setBounds(8, 40, 170, 100);

		createFieldOperateButtons(composite);

		refFieldListViewer = new ListViewer(composite, SWT.BORDER
				| SWT.V_SCROLL | SWT.MULTI);
		refFieldListViewer.setContentProvider(new BmFieldContentProvider());
		refFieldListViewer.setLabelProvider(new BmFieldLabelProvider());
		refFieldListViewer.setInput(refFieldList);
		List list_1 = refFieldListViewer.getList();
		list_1.setBounds(224, 40, 170, 100);
	}

	private void createFieldOperateButtons(Composite composite) {
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setBounds(183, 40, 36, 100);
		composite_1.setLayout(null);

		Button button = new Button(composite_1, SWT.NONE);
		button.addSelectionListener(new FieldOperationListener(1 << 1));
		button.setBounds(0, 0, 36, 25);
		button.setText(">>");

		Button button_1 = new Button(composite_1, SWT.NONE);
		button_1.addSelectionListener(new FieldOperationListener(1 << 2));
		button_1.setBounds(0, 25, 36, 25);
		button_1.setText(">");

		Button button_2 = new Button(composite_1, SWT.NONE);
		button_2.addSelectionListener(new FieldOperationListener(1 << 3));
		button_2.setBounds(0, 50, 36, 25);
		button_2.setText("<");

		Button button_3 = new Button(composite_1, SWT.NONE);
		button_3.addSelectionListener(new FieldOperationListener(1 << 4));
		button_3.setBounds(0, 75, 36, 25);
		button_3.setText("<<");
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		((GridLayout) parent.getLayout()).numColumns++;
		Button button = new Button(parent, SWT.PUSH);
		button.setText(IDialogConstants.OK_LABEL);
		setButtonLayoutData(button);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				relation.setName(text_relname.getText());
				relation.setRefTable(text_refmodel.getText());
				Record r = getSelectedLocalField();
				if (r != null)
					relation.setLocalField(r.getPrompt());
				CompositeMap forMap = getSelectedForienField();
				if (forMap != null)
					relation.setSrcField(forMap.getString("name"));
				int idx = com_jointype.getSelectionIndex();
				if (idx == -1)
					idx = 0;
				relation.setJoinType(com_jointype.getItem(idx));
				setReturnCode(OK);
				close();
			}
		});
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	private Record getSelectedLocalField() {
		ISelection s = localFieldComboViewer.getSelection();
		if (s instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) s;
			if (!ss.isEmpty())
				return (Record) ss.getFirstElement();
		}
		return null;
	}

	private CompositeMap getSelectedForienField() {
		ISelection s = bmFieldComboViewer.getSelection();
		if (s instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) s;
			if (!ss.isEmpty())
				return (CompositeMap) ss.getFirstElement();
		}
		return null;
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(430, 414);
	}

	private final class RefModelModifyListener implements ModifyListener {

		public void modifyText(ModifyEvent e) {
			try {
				String bmPkg = text_refmodel.getText();
				if (bmPkg.length() == 0)
					return;
				DataSetFieldUtil dsfu = new DataSetFieldUtil(metaProject, "",
						bmPkg);
				bmfieldList = dsfu.getLocalFields(dsfu.getBmMap());
				bmFieldComboViewer.setInput(bmfieldList);
				if (bmFieldListViewer != null)
					bmFieldListViewer.setInput(bmfieldList.clone());
				refFieldList.clear();
				if (refFieldListViewer != null) {
					refFieldListViewer.refresh();
				}
				for (int i = 0; i < bmfieldList.size(); i++) {
					if (bmfieldList.get(i).getString("name")
							.equals(relation.getSrcField())) {
						bmFieldComboViewer.getCombo().select(i);
						break;
					}
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	private final class BmSelectListener extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			if (auroraProject == null)
				return;
			ResourceSelector rs = new ResourceSelector(getShell());
			rs.setExtFilter(new String[] { "bm" });
			String bmHome = ResourceUtil.getBMHome(auroraProject);
			IFolder folder = ResourcesPlugin.getWorkspace().getRoot()
					.getFolder(new Path(bmHome));
			rs.setInput(folder);
			IResource res = rs.getSelection();
			if (res instanceof IFile) {
				text_refmodel.setText(Util.toBMPKG((IFile) res));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private final class FieldOperationListener extends SelectionAdapter {
		final int op_add_all = 1 << 1;
		final int op_add_selection = 1 << 2;
		@SuppressWarnings("unused")
		final int op_del_selection = 1 << 3;
		final int op_del_all = 1 << 4;
		int op;

		public FieldOperationListener(int op) {
			super();
			this.op = op;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			java.util.List<CompositeMap> als = getOpList();
			getDstList().addAll(als);
			getSrcList().removeAll(als);
			bmFieldListViewer.refresh();
			refFieldListViewer.refresh();
		}

		ArrayList<CompositeMap> getSrcList() {
			if (op == op_add_all || op == op_add_selection)
				return (ArrayList<CompositeMap>) bmFieldListViewer.getInput();
			return refFieldList;
		}

		ArrayList<CompositeMap> getDstList() {
			if (op == op_add_all || op == op_add_selection)
				return refFieldList;
			return (ArrayList<CompositeMap>) bmFieldListViewer.getInput();
		}

		java.util.List<CompositeMap> getOpList() {
			if (op == op_add_all || op == op_del_all)
				return getSrcList();
			ISelection s = (op == op_add_selection ? bmFieldListViewer
					: refFieldListViewer).getSelection();
			return (java.util.List<CompositeMap>) ((IStructuredSelection) s)
					.toList();
		}
	}
}
