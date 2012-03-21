package aurora.ide.meta.gef.designer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import aurora.ide.meta.gef.designer.editor.BMModelViewer;
import aurora.ide.meta.gef.designer.editor.RelationEditDialog;
import aurora.ide.meta.gef.designer.editor.RelationViewer;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.Record;
import aurora.ide.meta.gef.designer.model.Relation;

public class BMDesignPage extends FormPage {
	private Text quickAddText;
	private BMModel model;
	private BMModelViewer viewer;
	private Button btnNew;
	private Button btnUp;
	private Button btnDown;
	private Button btnDel;
	private Button btnClear;
	private boolean isDirty = false;
	private Text titleText;
	private Button btnNewRelation;
	private Button btnEditRelation;
	private Button btnDelRelation;
	private RelationViewer relationViewer;

	/**
	 * Create the form page.
	 * 
	 * @param id
	 * @param title
	 */
	public BMDesignPage(String id, String title) {
		super(id, title);
	}

	/**
	 * Create the form page.
	 * 
	 * @param editor
	 * @param id
	 * @param title
	 */
	public BMDesignPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	/**
	 * Create contents of the form.
	 * 
	 * @param managedForm
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		FormToolkit toolkit = managedForm.getToolkit();
		ScrolledForm form = managedForm.getForm();
		// form.setText("BM Quick Designer");
		// form.setLayout(null);

		Composite body = form.getBody();
		body.setLayout(new GridLayout(2, false));
		// toolkit.decorateFormHeading(form.getForm());
		// toolkit.paintBordersFor(body);
		createTitleControl(body);
		createQuickInputControl(managedForm);
		SashForm sh = new SashForm(body, SWT.VERTICAL);
		sh.setBackground(body.getBackground());
		sh.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		Composite com1 = new Composite(sh, SWT.NONE);
		com1.setBackground(body.getBackground());
		com1.setLayout(new GridLayout(2, false));
		createMainTable(com1);
		Composite com2 = new Composite(sh, SWT.NONE);
		com2.setBackground(body.getBackground());
		com2.setLayout(new GridLayout(2, false));
		createRelationTable(com2);
		sh.setWeights(new int[] { 2, 1 });
	}

	private void createTitleControl(Composite body) {
		Composite com = new Composite(body, SWT.NONE);
		com.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		com.setBackground(body.getBackground());
		com.setLayout(new GridLayout(2, false));
		new Label(body, SWT.NONE);// placeholder
		Label label = new Label(com, SWT.NONE);
		label.setBackground(body.getBackground());
		label.setText("Title : ");

		titleText = new Text(com, SWT.NONE);
		titleText.setBackground(new Color(null, 240, 244, 244));
		titleText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		// titleText.setSize(200, titleText.getSize().y);
		if (model != null)
			titleText.setText(model.getTitle());
		titleText.addFocusListener(new FocusListener() {

			public void focusLost(FocusEvent e) {
				model.setTitle(titleText.getText());
			}

			public void focusGained(FocusEvent e) {

			}
		});
	}

	private void createQuickInputControl(IManagedForm managedForm) {
		FormToolkit toolkit = managedForm.getToolkit();
		ScrolledForm form = managedForm.getForm();
		Composite body = form.getBody();

		quickAddText = new Text(body, SWT.BORDER);
		quickAddText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		quickAddText.setMessage("输入字段描述,如:单据号,订单日期");
		quickAddText.setFont(new Font(null, quickAddText.getFont()
				.getFontData()[0].getName(), 18, SWT.NORMAL));
		toolkit.adapt(quickAddText, true, true);

		btnNew = new Button(body, SWT.NONE);
		btnNew.setFont(quickAddText.getFont());
		toolkit.adapt(btnNew, true, true);
		btnNew.setText("Create");
		btnNew.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		btnNew.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				createNewLine();
			}
		});
		quickAddText.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 13) {
					createNewLine();
				}
			}
		});
	}

	private void createMainTable(Composite body) {
		viewer = new BMModelViewer(body, SWT.BORDER | SWT.MULTI
				| SWT.FULL_SELECTION);
		viewer.getTable().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true, 1, 6));
		if (model != null)
			viewer.setInput(model);
		new Label(body, SWT.NONE);
		btnUp = new Button(body, SWT.FLAT);
		btnUp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1,
				1));
		btnUp.setText("Up");
		SelectionListener listener = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int n = e.getSource() == btnUp ? -1 : 1;
				ISelection s = viewer.getSelection();
				if (s instanceof IStructuredSelection) {
					IStructuredSelection ss = (IStructuredSelection) s;
					Record r = (Record) ss.getFirstElement();
					int idx = model.indexOf(r);
					Record r1 = model.getAt(idx + n);
					model.setAt(idx, r1);
					model.setAt(idx + n, r);
					btnUp.setEnabled(!model.isFirst(r));
					btnDown.setEnabled(!model.isLast(r));
					viewer.refresh();
				}
			}
		};
		btnUp.addSelectionListener(listener);
		btnDown = new Button(body, SWT.NONE);
		btnDown.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		btnDown.setText("Down");
		btnDel = new Button(body, SWT.NONE);
		btnDel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		btnDown.addSelectionListener(listener);
		btnDel.setText("Delete");
		btnDel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ISelection s = viewer.getSelection();
				if (s instanceof IStructuredSelection) {
					IStructuredSelection ss = (IStructuredSelection) s;
					@SuppressWarnings("unchecked")
					List<Record> list = ss.toList();
					model.remove(list);
					viewer.refresh();
				}
			}
		});

		btnClear = new Button(body, SWT.NONE);
		btnClear.setText("Clear");
		btnClear.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		btnClear.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				model.removeAll();
				viewer.refresh();
			}
		});
		btnDel.setEnabled(false);
		btnUp.setEnabled(false);
		btnDown.setEnabled(false);
		// /add listener to process table selection
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				ISelection s = event.getSelection();
				if (s instanceof IStructuredSelection) {
					IStructuredSelection ss = (IStructuredSelection) s;
					btnDel.setEnabled(!ss.isEmpty());
					Object[] sels = ss.toArray();
					if (sels.length == 1) {
						Record r = (Record) sels[0];
						btnUp.setEnabled(!model.isFirst(r));
						btnDown.setEnabled(!model.isLast(r));
					} else {
						btnUp.setEnabled(false);
						btnDown.setEnabled(false);
					}
				}
			}
		});
		new Label(body, SWT.NONE);
	}

	private void createRelationTable(Composite body) {
		relationViewer = new RelationViewer(body, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI);
		final Table table = relationViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 5));
		new Label(body, 0);
		btnNewRelation = new Button(body, SWT.NONE);
		btnNewRelation.setText("New");
		btnNewRelation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnNewRelation.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				RelationEditDialog red = new RelationEditDialog(table
						.getShell());
				red.setModel(model);
				if (red.open() == IDialogConstants.OK_ID) {
					Relation newRel = red.getRelation();
					if (newRel != null) {
						model.add(newRel);
						relationViewer.refresh();
					}
				}
			}
		});
		btnEditRelation = new Button(body, SWT.NONE);
		btnEditRelation.setText("Edit");
		btnEditRelation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnEditRelation.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				RelationEditDialog red = new RelationEditDialog(table
						.getShell());
				red.setModel(model);
				red.setRelation((Relation) table.getSelection()[0].getData());
				if (red.open() == IDialogConstants.OK_ID) {
					relationViewer.refresh();
				}
			}
		});
		btnDelRelation = new Button(body, SWT.NONE);
		btnDelRelation.setText("Delete");
		btnDelRelation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnEditRelation.setEnabled(false);
		btnDelRelation.setEnabled(false);
		relationViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {

					public void selectionChanged(SelectionChangedEvent event) {
						ISelection s = event.getSelection();
						if (s instanceof IStructuredSelection) {
							IStructuredSelection ss = (IStructuredSelection) s;
							@SuppressWarnings("unchecked")
							List<Relation> list = ss.toList();
							btnEditRelation.setEnabled(list.size() == 1);
							btnDelRelation.setEnabled(list.size() > 0);
						}
					}
				});
		if (model != null)
			relationViewer.setInput(model);
	}

	private void createNewLine() {
		boolean mod = false;
		for (String s : quickAddText.getText().split("\\s*,\\s*|\\s*;\\s*")) {
			if (s.length() == 0)
				continue;
			Record r = DesignerUtil.createRecord(s);
			mod = true;
			model.add(r);
		}
		quickAddText.setText("");
		if (mod)
			viewer.refresh();
	}

	public void setModel(BMModel model) {
		this.model = model;
		model.addPropertyChangeListener(new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println(evt.getPropertyName() + " change from "
						+ evt.getOldValue() + " to " + evt.getNewValue());
				setDirty(true);
			}
		});
		if (viewer != null)
			viewer.setInput(model);
		if (relationViewer != null)
			relationViewer.setInput(model);
		if (titleText != null)
			titleText.setText(model.getTitle());
	}

	public BMModel getModel() {
		return model;
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean d) {
		isDirty = d;
		getEditor().editorDirtyStateChanged();
	}
}
