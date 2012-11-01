package aurora.ide.meta.gef.designer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import aurora.ide.helpers.StatusUtil;
import aurora.ide.meta.builder.AutoGenBuilder;
import aurora.ide.meta.gef.designer.editor.BMModelContentProvider;
import aurora.ide.meta.gef.designer.editor.BMModelLabelProvider;
import aurora.ide.meta.gef.designer.editor.BMModelViewer;
import aurora.ide.meta.gef.designer.editor.RelationEditDialog;
import aurora.ide.meta.gef.designer.editor.RelationViewer;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.Record;
import aurora.ide.meta.gef.designer.model.RecordPropertyChangeEvent;
import aurora.ide.meta.gef.designer.model.Relation;

public class BMDesignPage extends FormPage implements PropertyChangeListener,
		IResourceChangeListener, IResourceDeltaVisitor {
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
	private ComboViewer defaultDisplayViewer;
	private IMessageManager messageManager;
	private IFile inputFile;

	private static String error_msg_key = "builderror";

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

	protected void createFormContent(IManagedForm managedForm) {
		FormToolkit toolkit = managedForm.getToolkit();
		ScrolledForm form = managedForm.getForm();
		messageManager = form.getForm().getMessageManager();
		form.setLayout(new FillLayout(SWT.HORIZONTAL));
		messageManager.setAutoUpdate(true);
		if (model != null)
			form.getForm().setText(model.getTitle());
		toolkit.decorateFormHeading(form.getForm());
		createActions(form.getToolBarManager());

		Composite body = form.getBody();
		body.setLayout(new GridLayout(2, false));
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
		new Label(com2, SWT.NONE);
		sh.setWeights(new int[] { 2, 1 });
		handleMarker();
	}

	private void createActions(IToolBarManager toolBarManager) {
		BMDesigner designer = (BMDesigner) getEditor();
		toolBarManager.add(new OpenBMAction(designer.getInputFile(), designer
				.getSite().getPage()));
		toolBarManager.add(new SettingAction(model, designer));

		// end
		toolBarManager.update(true);
	}

	private void createTitleControl(Composite body) {
		Composite com = new Composite(body, SWT.NONE);
		com.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		com.setBackground(body.getBackground());
		RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		rowLayout.wrap = false;
		rowLayout.center = true;
		com.setLayout(rowLayout);
		new Label(body, SWT.NONE);// placeholder
		Label label = new Label(com, SWT.NONE);
		label.setBackground(com.getBackground());
		label.setText(DesignerMessages.BMDesignPage_0);

		titleText = new Text(com, SWT.BORDER);
		titleText.setLayoutData(new RowData(200, SWT.DEFAULT));
		if (model != null)
			titleText.setText(model.getTitle());
		titleText.addFocusListener(new FocusListener() {

			public void focusLost(FocusEvent e) {
				model.setTitle(titleText.getText());
			}

			public void focusGained(FocusEvent e) {

			}
		});
		titleText.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 13)
					model.setTitle(titleText.getText());
			}
		});
		titleText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				model.setTitle(titleText.getText());
			}
		});

		Label disLabel = new Label(com, SWT.RIGHT);
		disLabel.setBackground(com.getBackground());
		disLabel.setText("defaultDisplayField : ");
		disLabel.setLayoutData(new RowData(200, SWT.DEFAULT));
		defaultDisplayViewer = new ComboViewer(com, SWT.READ_ONLY);
		defaultDisplayViewer.setContentProvider(new BMModelContentProvider(
				BMModel.RECORD));
		defaultDisplayViewer.setLabelProvider(new BMModelLabelProvider(
				BMModel.RECORD));
		defaultDisplayViewer.getCombo().setLayoutData(
				new RowData(120, SWT.DEFAULT));
		if (model != null) {
			defaultDisplayViewer.setInput(model);
			Record r = model.getDefaultDisplayRecord();
			if (r != null)
				defaultDisplayViewer.setSelection(new StructuredSelection(r));
			defaultDisplayViewer.refresh();
		}
		defaultDisplayViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {

					public void selectionChanged(SelectionChangedEvent event) {
						if (event.getSelection() instanceof IStructuredSelection) {
							Record r = (Record) ((IStructuredSelection) event
									.getSelection()).getFirstElement();
							if (r != null) {
								model.setDefaultDisplay(r.getPrompt());
							}
						}
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
		quickAddText.setMessage(DesignerMessages.BMDesignPage_1);
		quickAddText.setFont(new Font(null, quickAddText.getFont()
				.getFontData()[0].getName(), 12, SWT.NORMAL));
		toolkit.adapt(quickAddText, true, true);

		btnNew = new Button(body, SWT.NONE);
		btnNew.setFont(quickAddText.getFont());
		toolkit.adapt(btnNew, true, true);
		btnNew.setText(DesignerMessages.BMDesignPage_2);
		btnNew.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		btnNew.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				createNewLine();
			}
		});
		quickAddText.addKeyListener(new KeyAdapter() {

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

		new Label(body, SWT.NONE);
		btnUp = new Button(body, SWT.NONE);
		btnUp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1,
				1));
		btnUp.setText(DesignerMessages.BMDesignPage_3);
		SelectionListener listener = new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				int n = e.getSource() == btnUp ? -1 : 1;
				ISelection s = viewer.getSelection();
				if (s instanceof IStructuredSelection) {
					IStructuredSelection ss = (IStructuredSelection) s;
					Record r = (Record) ss.getFirstElement();
					int idx = model.indexOf(r);
					viewer.disposeEditor(idx);
					viewer.disposeEditor(idx + n);
					model.swap(idx, idx + n);
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
		btnDown.setText(DesignerMessages.BMDesignPage_4);
		btnDel = new Button(body, SWT.NONE);
		btnDel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		btnDown.addSelectionListener(listener);
		btnDel.setText(DesignerMessages.BMDesignPage_5);
		btnDel.addSelectionListener(new SelectionAdapter() {

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
		btnClear.setText(DesignerMessages.BMDesignPage_6);
		btnClear.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		btnClear.addSelectionListener(new SelectionAdapter() {

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
		if (model != null) {
			viewer.setInput(model);
			viewer.refresh();
		}
		new Label(body, SWT.NONE);
	}

	private void createRelationTable(Composite body) {
		relationViewer = new RelationViewer(body, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI);
		final Table table = relationViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 5));
		new Label(body, 0);
		btnNewRelation = new Button(body, SWT.NONE);
		btnNewRelation.setText(DesignerMessages.BMDesignPage_7);
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
						for (Record r : model.getRecordList()) {
							if (r.getPrompt().equals(newRel.getLocalField())) {
								viewer.refresh(r);
								break;
							}
						}
					}
				}
			}
		});
		btnEditRelation = new Button(body, SWT.NONE);
		btnEditRelation.setText(DesignerMessages.BMDesignPage_8);
		btnEditRelation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnEditRelation.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				RelationEditDialog red = new RelationEditDialog(table
						.getShell());
				red.setModel(model);
				red.setRelation((Relation) table.getSelection()[0].getData());
				if (red.open() == IDialogConstants.OK_ID) {
					Relation rel = red.getRelation();
					relationViewer.refresh();
					for (Record r : model.getRecordList()) {
						if (r.getPrompt().equals(rel.getLocalField())) {
							viewer.refresh(r);
							break;
						}
					}
				}
			}
		});
		btnDelRelation = new Button(body, SWT.NONE);
		btnDelRelation.setText(DesignerMessages.BMDesignPage_9);
		btnDelRelation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnDelRelation.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			public void widgetSelected(SelectionEvent e) {
				ISelection s = relationViewer.getSelection();
				if (s instanceof IStructuredSelection) {
					IStructuredSelection ss = (IStructuredSelection) s;
					model.removeRelations(ss.toList());
					relationViewer.refresh();
				}
			}
		});
		btnEditRelation.setEnabled(false);
		btnDelRelation.setEnabled(false);
		relationViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {

					public void selectionChanged(SelectionChangedEvent event) {
						ISelection s = event.getSelection();
						if (s instanceof IStructuredSelection) {
							IStructuredSelection ss = (IStructuredSelection) s;
							Object[] arr = ss.toArray();
							btnEditRelation.setEnabled(arr.length == 1);
							btnDelRelation.setEnabled(arr.length > 0);
						}
					}
				});
		if (model != null) {
			relationViewer.setInput(model);
			relationViewer.refresh();
		}
	}

	private void createNewLine() {
		boolean mod = false;
		for (String s : quickAddText.getText().split(
				DesignerMessages.BMDesignPage_10)) {
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

	public void setModel(final BMModel model) {
		this.model = model;
		model.removePropertyChangeListener(this);
		model.addPropertyChangeListener(this);
	}

	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();
		if (delta != null) {
			try {
				delta.accept(this);
			} catch (CoreException e) {
				StatusUtil.showExceptionDialog(getSite().getShell(), null,
						null, true, e);
			}
		}
	}

	public boolean visit(IResourceDelta delta) throws CoreException {
		if (inputFile.equals(delta.getResource())) {
			addRemoveMarker();
			return false;
		}
		return true;
	}

	private void handleMarker() {
		inputFile = ((BMDesigner) getEditor()).getInputFile();
		addRemoveMarker();
		inputFile.getWorkspace().addResourceChangeListener(this,
				IResourceChangeEvent.POST_BUILD);
	}

	void addRemoveMarker() {
		getSite().getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IMarker[] markers;
				try {
					markers = inputFile.findMarkers(
							AutoGenBuilder.MARKER_BUILD_ERROR, false, 0);
					if (markers.length > 0) {
						IMarker m = markers[0];
						String error = (String) m.getAttribute(IMarker.MESSAGE);
						messageManager.addMessage(error_msg_key, error, null,
								IMessageProvider.ERROR);
					} else {
						messageManager.removeMessage(error_msg_key);
					}
				} catch (CoreException e) {
					StatusUtil.showExceptionDialog(getSite().getShell(), null,
							null, false, e);
				}
			}
		});
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(BMModel.STRUCTURE_RECORD)) {
			autoUpdateDisplayField();
			setDirty(true);
			return;
		}
		if (evt.getPropertyName().equals(BMModel.TITLE)) {
			getManagedForm().getForm().getForm()
					.setText((String) evt.getNewValue());
		}
		if (evt instanceof RecordPropertyChangeEvent) {
			Record r = (Record) evt.getSource();
			Object old = evt.getOldValue();
			if (old != null && !old.equals("")) {
				viewer.refresh(r);
				if (IDesignerConst.COLUMN_PROMPT.equals(evt.getPropertyName())) {
					defaultDisplayViewer.refresh();
				}
			}
		}
		setDirty(true);
	}

	private void autoUpdateDisplayField() {
		List<Record> list = model.getRecordList();
		boolean needUpdate = list.size() > 0;
		for (Record r : list) {
			if (r.getPrompt().equals(model.getDefaultDisplay())) {
				needUpdate = false;
				break;
			}
		}
		defaultDisplayViewer.refresh();
		if (needUpdate) {
			defaultDisplayViewer.setSelection(new StructuredSelection(list
					.get(0)));
		}
	}

	public BMModel getModel() {
		return model;
	}

	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean d) {
		isDirty = d;
		getEditor().editorDirtyStateChanged();
	}

}
