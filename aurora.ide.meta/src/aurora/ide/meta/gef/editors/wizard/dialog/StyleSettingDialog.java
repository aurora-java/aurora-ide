package aurora.ide.meta.gef.editors.wizard.dialog;

import java.util.ArrayList;
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
import org.eclipse.swt.graphics.Point;
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

import aurora.ide.meta.gef.editors.models.link.Parameter;

public class StyleSettingDialog extends Dialog {

	private TableViewer paraViewer;

	private Parameter c_parameter;

	private List<Parameter> input = new ArrayList<Parameter>();

	public StyleSettingDialog(Shell parentShell) {
		super(parentShell);
	}

	public StyleSettingDialog(Shell parentShell, List<Parameter> input) {
		super(parentShell);
		this.input = input;
	}

	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE | SWT.MAX | SWT.HELP;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(400, 300);
	}

	public List<Parameter> getResult() {
		return input;
	}

	public List<Parameter> getInput() {
		return input;
	}

	public void setInput(List<Parameter> input) {
		this.input = input;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.getShell().setText("参数设置");

		Composite composite = new Composite(container, SWT.None);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		paraViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE);
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
		column.setText("参数名称");
		column.setWidth(100);
		column = new TableColumn(table, SWT.NONE);
		column.setText("参数值");
		column.setWidth(100);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(layoutData);
		Composite bs = new Composite(composite, SWT.NONE);
		bs.setLayout(new GridLayout());
		Button add = new Button(bs, SWT.NONE);
		add.setText(" New  ");
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
		edit.setText("  Edit  ");
		edit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				AddParamDialog dialog = new AddParamDialog(getShell(), c_parameter);
				if (dialog.open() == Dialog.OK) {
					paraViewer.setInput(input);
				}
			}
		});
		final Button del = new Button(bs, SWT.NONE);
		del.setText("Delete");
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
		return container;
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
			return "";
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
			shell.setText("Parameter");
			shell.setMinimumSize(400, 150);
		}

		protected Control createDialogArea(Composite parent) {
			Composite composite = (Composite) super.createDialogArea(parent);
			composite.setLayout(new GridLayout(2, false));
			Label name = new Label(composite, SWT.NONE);
			name.setText("Name :");
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
			value.setText("Value :");
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
