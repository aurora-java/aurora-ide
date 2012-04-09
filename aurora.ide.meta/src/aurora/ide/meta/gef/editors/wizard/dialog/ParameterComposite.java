package aurora.ide.meta.gef.editors.wizard.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.models.link.Parameter;

public class ParameterComposite extends Composite {

	private TableViewer paraViewer;

	private Parameter c_parameter;

	private List<Parameter> input = new ArrayList<Parameter>();

	private ViewDiagram diagram;

	private AuroraComponent context;

	public ParameterComposite(ViewDiagram diagram, Composite parent, int style,AuroraComponent context) {
		super(parent, style);
		createControl();
		this.diagram = diagram;
		this.context = context;
	}

	private Container[] getContainers() {
//		if (diagram != null) {
//			List<Container> sectionContainers = diagram
//					.getSectionContainers(diagram,Container.SECTION_TYPES);
//			return sectionContainers.toArray(new Container[sectionContainers
//					.size()]);
//		}
		return null;
	}

	class ParameterContentProvider implements IStructuredContentProvider {

		public ParameterContentProvider() {
			super();
		}

		public void dispose() {

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof List) {
				return ((List) inputElement)
						.toArray(new Parameter[((List) inputElement).size()]);
			}
			return null;
		}
	}

	class ParameterLabelProvider extends BaseLabelProvider implements
			ITableLabelProvider, ITableColorProvider {
		private Color COLOR_ODD = new Color(null, 245, 255, 255);
		private Color COLOR_EVEN = new Color(null, 255, 255, 255);
		private int columnNumIndx = 1;

		public ParameterLabelProvider() {
			super();
		}

		public Color getForeground(Object element, int columnIndex) {
			if (columnIndex == columnNumIndx)
				return new Color(null, 128, 128, 128);
			return null;
		}

		public Color getBackground(Object element, int columnIndex) {
			int rowNum = input.indexOf((Parameter) element);
			return (rowNum % 2 == 0) ? COLOR_EVEN : COLOR_ODD;
		}

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

	private void createControl() {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		this.setLayout(layout);
		paraViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.SINGLE);

		paraViewer.setContentProvider(new ParameterContentProvider());
		paraViewer.setLabelProvider(new ParameterLabelProvider());
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
		// layoutData.horizontalSpan = 2;
		table.setLayoutData(layoutData);
		Composite bs = new Composite(this, SWT.NONE);
		bs.setLayout(new GridLayout());
		Button add = new Button(bs, SWT.NONE);
		add.setText(" New  ");
		// add.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1,
		// 1));
		add.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				ParameterDialog pd = new ParameterDialog(getShell(),
						getContainers(), null,context);
				int open = pd.open();
				if (open == ParameterDialog.OK) {
					Parameter parameter = pd.getParameter();
					input.add(parameter);
					paraViewer.setInput(input);
				}
			}
		});
		final Button edit = new Button(bs, SWT.NONE);
		edit.setText("  Edit  ");
		// edit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
		// 1,
		// 1));
		edit.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				ParameterDialog pd = new ParameterDialog(getShell(),
						getContainers(), c_parameter,context);
				int open = pd.open();
				if (open == ParameterDialog.OK) {
					Parameter parameter = pd.getParameter();
					c_parameter.setName(parameter.getName());
					c_parameter.setContainer(parameter.getContainer());
					c_parameter.setValue(parameter.getValue());
					paraViewer.refresh(c_parameter);
				}

			}
		});
		final Button del = new Button(bs, SWT.NONE);
		del.setText("Delete");
		// del.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1,
		// 1));
		edit.setEnabled(false);
		del.setEnabled(false);
		paraViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				ISelection s = event.getSelection();
				if (s instanceof IStructuredSelection) {
					IStructuredSelection ss = (IStructuredSelection) s;
					@SuppressWarnings("unchecked")
					List<Parameter> list = ss.toList();
					edit.setEnabled(list.size() == 1);
					del.setEnabled(list.size() > 0);
					c_parameter = list.size() > 0 ? list.get(0) : null;
				}
			}
		});
		del.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				input.remove(c_parameter);
				paraViewer.setInput(input);
			}
		});
	}

	public void setParameters(List<Parameter> paras) {
		if (paras != null) {
			for (Parameter p : paras) {
				this.input.add(p.clone());
			}
		}

		paraViewer.setInput(input);
	}

	public List<Parameter> getParameters() {
		return input;
	}

}
