package aurora.ide.meta.gef.editors.wizard.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import aurora.ide.meta.gef.i18n.Messages;
import aurora.plugin.source.gen.screen.model.Parameter;

public class PComposite extends Composite {

	private Parameter c_parameter;

	private List<Parameter> input;

	public PComposite(Composite parent, List<Parameter> input, int style) {
		super(parent, style);
		this.input = input;
		if (this.input != null) {
			createComposite(this);
		}
	}

	private void createComposite(Composite container) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		this.setLayout(layout);
		final TableViewer paraViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE);
		paraViewer.setContentProvider(new IStructuredContentProvider() {
			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof List) {
					List<Parameter> ps = (List<Parameter>) inputElement;
					return ps.toArray();
				}
				return null;
			}
		});
		paraViewer.setLabelProvider(new TableLabelProvider());
		final Table table = paraViewer.getTable();
		TableColumn column = new TableColumn(table, SWT.NONE);
		column.setText(Messages.PComposite_ParName);
		column.setWidth(100);
		column = new TableColumn(table, SWT.NONE);
		column.setText(Messages.PComposite_ParValue);
		column.setWidth(100);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(layoutData);
		Composite bs = new Composite(this, SWT.NONE);
		bs.setLayout(new GridLayout());
		Button add = new Button(bs, SWT.NONE);
		add.setText(Messages.PComposite_New);
		add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				AddParamDialog dialog = new AddParamDialog(getShell());
				if (dialog.open() == Dialog.OK) {
					input.add(dialog.getParameter());
					paraViewer.setInput(input);
				}
			}
		});
		final Button edit = new Button(bs, SWT.NONE);
		edit.setText(Messages.PComposite_Edit);
		edit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				AddParamDialog dialog = new AddParamDialog(getShell(), c_parameter);
				if (dialog.open() == Dialog.OK) {
					paraViewer.setInput(input);
				}
			}
		});
		final Button del = new Button(bs, SWT.NONE);
		del.setText(Messages.PComposite_Delete);
		edit.setEnabled(false);
		del.setEnabled(false);
		del.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				input.remove(c_parameter);
				paraViewer.setInput(input);
			}
		});
		paraViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection s = event.getSelection();
				if (s instanceof IStructuredSelection) {
					IStructuredSelection ss = (IStructuredSelection) s;
					if (ss.getFirstElement() instanceof Parameter) {
						c_parameter = (Parameter) ss.getFirstElement();
						del.setEnabled(true);
						edit.setEnabled(true);
					} else {
						c_parameter = null;
						del.setEnabled(false);
						edit.setEnabled(false);
					}
				}
			}
		});
		if (input != null && input.size() > 0) {
			paraViewer.setInput(input);
		}
	}

	class TableLabelProvider extends BaseLabelProvider implements ITableLabelProvider {
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof Parameter) {
				if (0 == columnIndex) {
					return ((Parameter) element).getName();
				}
				if (1 == columnIndex) {
					return ((Parameter) element).getValue();
				}
			}
			return ""; //$NON-NLS-1$
		}
	}

	class AddParamDialog extends Dialog {
		private Parameter parameter;

		public AddParamDialog(Shell parentShell) {
			super(parentShell);
		}

		public AddParamDialog(Shell parentShell, Parameter parameter) {
			super(parentShell);
			this.parameter = parameter;
		}

		protected void configureShell(Shell shell) {
			super.configureShell(shell);
			shell.setText("Parameter"); //$NON-NLS-1$
			shell.setMinimumSize(400, 150);
		}

		protected Control createDialogArea(Composite parent) {
			Composite composite = (Composite) super.createDialogArea(parent);
			composite.setLayout(new GridLayout(2, false));
			Label name = new Label(composite, SWT.NONE);
			name.setText(Messages.PComposite_Name);
			Text nameField = new Text(composite, SWT.BORDER);
			nameField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			nameField.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (parameter == null) {
						parameter = new Parameter();
					}
					parameter.setName(((Text) e.getSource()).getText());
				}
			});

			Label value = new Label(composite, SWT.NONE);
			value.setText(Messages.PComposite_Value);
			Text valueField = new Text(composite, SWT.BORDER);
			valueField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			valueField.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (parameter == null) {
						parameter = new Parameter();
					}
					parameter.setValue(((Text) e.getSource()).getText());
				}
			});
			if (parameter != null) {
				nameField.setText(parameter.getName());
				valueField.setText(parameter.getValue());
			}
			return composite;
		}

		public Parameter getParameter() {
			return parameter;
		}

		public void setParameter(Parameter parameter) {
			this.parameter = parameter;
		}
	}
}
