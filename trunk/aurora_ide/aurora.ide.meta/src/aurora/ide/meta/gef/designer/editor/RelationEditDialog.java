package aurora.ide.meta.gef.designer.editor;

import java.util.Arrays;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.Record;
import aurora.ide.meta.gef.designer.model.Relation;

public class RelationEditDialog extends Dialog {
	private Text text_relname;
	private Text text_refmodel;
	private BMModel model;
	private Relation relation;
	private Combo com_locfield;
	private Combo com_forfield;
	private Combo com_jointype;

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

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		if (relation == null) {
			getShell().setText("Create New Relation");
			relation = new Relation();
		} else
			getShell().setText("Edit Relation");
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

		text_refmodel = new Text(container, SWT.BORDER);
		text_refmodel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		text_refmodel.setText(relation.getRefTable());
		Button btnNewButton = new Button(container, SWT.NONE);
		btnNewButton.setText("浏览");

		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblNewLabel.setText("本地字段:");

		com_locfield = new Combo(container, SWT.READ_ONLY);
		com_locfield.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		com_locfield.setItems(getLocalFields());
		int idx = -1;
		String locf = relation.getLocalField();
		Record[] rels = model.getRecords();
		for (int i = 0; i < rels.length; i++) {
			if (rels[i].getPrompt().equals(locf)) {
				idx = i;
				break;
			}
		}
		com_locfield.select(idx);
		new Label(container, SWT.NONE);

		Label lblNewLabel_1 = new Label(container, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblNewLabel_1.setText("外部字段:");

		com_forfield = new Combo(container, SWT.READ_ONLY);
		com_forfield.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
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
		idx = Arrays.asList(items).indexOf(relation.getJoinType());
		if (idx == -1)
			idx = 0;
		com_jointype.select(idx);
		new Label(container, SWT.NONE);

		return container;
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

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(430, 270);
	}

	private String[] getLocalFields() {
		Record[] rds = model.getRecords();
		String[] res = new String[rds.length];
		for (int i = 0; i < rds.length; i++) {
			res[i] = rds[i].getPrompt() + "[" + rds[i].getName() + "]";
		}
		return res;
	}

}
