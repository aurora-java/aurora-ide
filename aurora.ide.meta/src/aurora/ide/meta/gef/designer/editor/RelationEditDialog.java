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
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import aurora.ide.helpers.DialogUtil;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.designer.BMCompositeMap;
import aurora.ide.meta.gef.designer.DataType;
import aurora.ide.meta.gef.designer.DesignerMessages;
import aurora.ide.meta.gef.designer.IDesignerConst;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.Record;
import aurora.ide.meta.gef.designer.model.Relation;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.property.ResourceSelector;
import aurora.ide.meta.gef.editors.source.gen.DataSetFieldUtil;
import aurora.ide.meta.project.AuroraMetaProject;
import aurora.ide.search.core.Util;

public class RelationEditDialog extends Dialog implements SelectionListener {
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
	private String refTablePkName = "";

	public java.util.List<CompositeMap> bmFieldWithoutPkList = new ArrayList<CompositeMap>();

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
			getShell().setText(DesignerMessages.RelationEditDialog_0);
			relation = new Relation();
		} else
			getShell().setText(DesignerMessages.RelationEditDialog_1);
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
		label.setText(DesignerMessages.RelationEditDialog_2);

		text_relname = new Text(container, SWT.BORDER);
		text_relname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		text_relname.setText(relation.getName());
		new Label(container, SWT.NONE);

		Label label_1 = new Label(container, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		label_1.setText(DesignerMessages.RelationEditDialog_3);

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
		btnNewButton.setText(DesignerMessages.RelationEditDialog_4);

		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblNewLabel.setText(DesignerMessages.RelationEditDialog_5);

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
		localFieldComboViewer
				.addSelectionChangedListener(new LocalFieldChangeListener());
		new Label(container, SWT.NONE);

		Label lblNewLabel_1 = new Label(container, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblNewLabel_1.setText(DesignerMessages.RelationEditDialog_6);

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
		lblNewLabel_2.setText(DesignerMessages.RelationEditDialog_7);

		com_jointype = new Combo(container, SWT.READ_ONLY);
		com_jointype.setItems(IDesignerConst.JOIN_TYPES);
		com_jointype.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		int idx = Arrays.asList(IDesignerConst.JOIN_TYPES).indexOf(
				relation.getJoinType());
		com_jointype.select(idx == -1 ? 0 : idx);
		new Label(container, SWT.NONE);

		createRefFieldGroup(container);

		return container;
	}

	private void createRefFieldGroup(Composite container) {
		Group composite = new Group(container, SWT.NONE);
		composite.setText(DesignerMessages.RelationEditDialog_13);
		GridLayout gl_composite = new GridLayout(3, false);
		gl_composite.verticalSpacing = 0;
		gl_composite.marginHeight = 0;
		gl_composite.horizontalSpacing = 0;
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3,
				1));

		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setText(DesignerMessages.RelationEditDialog_14);
		new Label(composite, SWT.NONE);

		Label label_3 = new Label(composite, SWT.NONE);
		label_3.setText(DesignerMessages.RelationEditDialog_15);

		bmFieldListViewer = new ListViewer(composite, SWT.BORDER | SWT.V_SCROLL
				| SWT.MULTI);
		bmFieldListViewer.setContentProvider(new BmFieldContentProvider());
		bmFieldListViewer.setLabelProvider(new BmFieldLabelProvider());
		bmFieldListViewer.setInput(bmFieldWithoutPkList);
		List list = bmFieldListViewer.getList();
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		createFieldOperateButtons(composite);

		refFieldListViewer = new ListViewer(composite, SWT.BORDER
				| SWT.V_SCROLL | SWT.MULTI);
		refFieldListViewer.setContentProvider(new BmFieldContentProvider());
		refFieldListViewer.setLabelProvider(new BmFieldLabelProvider());
		refFieldListViewer.setInput(refFieldList);
		List list_1 = refFieldListViewer.getList();
		list_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	}

	private void createFieldOperateButtons(Composite composite) {
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				true, 1, 1));
		GridLayout gl_composite_1 = new GridLayout(1, false);
		gl_composite_1.marginHeight = 0;
		gl_composite_1.verticalSpacing = 0;
		gl_composite_1.marginWidth = 0;
		gl_composite_1.horizontalSpacing = 0;
		composite_1.setLayout(gl_composite_1);

		Button button = new Button(composite_1, SWT.NONE);
		button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		button.addSelectionListener(new FieldOperationListener(1 << 1));
		button.setText(DesignerMessages.RelationEditDialog_16);

		Button button_1 = new Button(composite_1, SWT.NONE);
		button_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		button_1.addSelectionListener(new FieldOperationListener(1 << 2));
		button_1.setText(DesignerMessages.RelationEditDialog_17);

		Button button_2 = new Button(composite_1, SWT.NONE);
		button_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		button_2.addSelectionListener(new FieldOperationListener(1 << 3));
		button_2.setText(DesignerMessages.RelationEditDialog_18);

		Button button_3 = new Button(composite_1, SWT.NONE);
		button_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		button_3.addSelectionListener(new FieldOperationListener(1 << 4));
		button_3.setText(DesignerMessages.RelationEditDialog_19);
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// use ignore_id to create a button,and process click event manual.
		// because the ok_id will automatical close the dialog and dispose all
		// controls
		// thus i can`t get the value in control
		Button button = createButton(parent, IDialogConstants.IGNORE_ID,
				IDialogConstants.OK_LABEL, false);
		button.addSelectionListener(this);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);

	}

	public void widgetSelected(SelectionEvent e) {
		relation.setName(text_relname.getText());
		relation.setRefTable(text_refmodel.getText());
		Record r = getSelectedLocalField();
		relation.setLocalField(r == null ? "" : r.getPrompt());
		CompositeMap forMap = getSelectedForeignField();
		if (forMap != null) {
			updateLocalRecord(forMap);
			String prompt = forMap.getString("prompt"); //$NON-NLS-1$
			relation.setSrcField(prompt == null ? "" : prompt); //$NON-NLS-1$
		} else
			relation.setSrcField(""); //$NON-NLS-1$
		int idx = com_jointype.getSelectionIndex();
		relation.setJoinType(com_jointype.getItem(idx == -1 ? 0 : idx));
		String[] refps = new String[refFieldList.size()];
		for (int i = 0; i < refps.length; i++)
			refps[i] = refFieldList.get(i).getString("prompt");
		relation.setRefPromptsArray(refps);
		// close the dialog
		setReturnCode(OK);
		close();
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);
	}

	/*
	 * if the selected src field(from bm) is primary-key,then try to update some
	 * information of local field
	 */
	private void updateLocalRecord(CompositeMap forMap) {
		Record rec = model.getRecordByPrompt(relation.getLocalField());
		if (rec == null)
			return;
		String fpk = forMap.getString("name");
		boolean isForeign = refTablePkName.equals(fpk);
		rec.put(IDesignerConst.COLUMN_ISFOREIGN, isForeign);
		if (isForeign) {
			// if foreign pkname equals local pkname,they are most likely
			// the same bm
			if (!model.getPkRecord().getName().equals(fpk))
				rec.setName(fpk);
			rec.setType(DataType.BIGNIT.getDisplayType());
			rec.setEditor(Input.Combo);
			rec.setOptions(relation.getRefTable());
		}
	}

	private Record getSelectedLocalField() {
		ISelection s = localFieldComboViewer.getSelection();
		if (s instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) s;
			return (Record) ss.getFirstElement();
		}
		return null;
	}

	private CompositeMap getSelectedForeignField() {
		ISelection s = bmFieldComboViewer.getSelection();
		if (s instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) s;
			return (CompositeMap) ss.getFirstElement();
		}
		return null;
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(430, 450);
	}

	/**
	 * when local field change <br/>
	 * if current relation has none name ,then give it a name<br>
	 * if current relation has a name ,but the name is most likely setted by
	 * this method,then change it<br/>
	 * else , the name is setted by user manual ,do not change
	 */
	private final class LocalFieldChangeListener implements
			ISelectionChangedListener {

		public void selectionChanged(SelectionChangedEvent event) {
			Record r = getSelectedLocalField();
			if (r == null)
				return;
			String relname = text_relname.getText();
			boolean change = relname.length() == 0;
			if (!change)
				for (Record rr : model.getRecordList()) {
					if (relname.equals("rel_" + rr.getName())) {
						change = true;
						break;
					}
				}
			if (change)
				text_relname.setText("rel_" + r.getName());
		}
	}

	private final class RefModelModifyListener implements ModifyListener {

		public void modifyText(ModifyEvent e) {
			try {
				String bmPkg = text_refmodel.getText();
				if (bmPkg.length() == 0)
					return;
				DataSetFieldUtil dsfu = new DataSetFieldUtil(metaProject, "", //$NON-NLS-1$
						bmPkg);
				CompositeMap bmMap = dsfu.getBmMap();
				if (bmMap == null) {
					DialogUtil.showErrorMessageBox("bm :" + bmPkg
							+ ", has syntax error.");
					return;
				}
				BMCompositeMap bmc = new BMCompositeMap(bmMap);
				bmfieldList = new ArrayList<CompositeMap>(bmc.getFields());
				bmFieldWithoutPkList = bmc.getFieldsWithoutPk();
				refTablePkName = bmc.getPkFieldName();
				bmFieldComboViewer.setInput(bmfieldList);
				refFieldList.clear();
				String[] refps = relation.getRefPromptsArray();
				if (refps.length == 0) {
					CompositeMap ddfMap = bmc.getDefaultDisplayField();
					if (ddfMap != null) {
						bmFieldWithoutPkList.remove(ddfMap);
						refFieldList.add(ddfMap);
					}
				} else {
					for (String refp : refps) {
						CompositeMap m = bmc.getFieldByPrompt(refp);
						if (m != null) {
							bmFieldWithoutPkList.remove(m);
							refFieldList.add(m);
						}
					}
				}
				if (bmFieldListViewer != null)
					bmFieldListViewer.setInput(bmFieldWithoutPkList);
				if (refFieldListViewer != null) {
					refFieldListViewer.refresh();
				}
				String srcF = relation.getSrcField();
				int pkIndex = -1;
				for (int i = 0; i < bmfieldList.size(); i++) {
					String pmpt = bmfieldList.get(i).getString("prompt"); //$NON-NLS-1$
					String name = bmfieldList.get(i).getString("name"); //$NON-NLS-1$
					if (pmpt != null && pmpt.equals(srcF)) {
						bmFieldComboViewer.getCombo().select(i);
						return;
					}
					if (name != null && name.equals(refTablePkName))
						pkIndex = i;
				}
				bmFieldComboViewer.getCombo().select(pkIndex);
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
			rs.setExtFilter(new String[] { "bm" }); //$NON-NLS-1$
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
