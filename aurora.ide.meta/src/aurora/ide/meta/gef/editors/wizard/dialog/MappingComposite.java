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

import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Mapping;

public class MappingComposite extends Composite {

	private TableViewer paraViewer;

	private Mapping c_mapping;

	private List<Mapping> input = new ArrayList<Mapping>();

	private AuroraComponent context;

	public MappingComposite(Composite parent, int style,AuroraComponent context) {
		super(parent, style);
		createControl();
		this.context = context;
	}


	class MappingContentProvider implements IStructuredContentProvider {

		public MappingContentProvider() {
			super();
		}

		public void dispose() {

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof List) {
				return ((List) inputElement)
						.toArray(new Mapping[((List) inputElement).size()]);
			}
			return null;
		}
	}

	class MappingLabelProvider extends BaseLabelProvider implements
			ITableLabelProvider, ITableColorProvider {
		private Color COLOR_ODD = new Color(null, 245, 255, 255);
		private Color COLOR_EVEN = new Color(null, 255, 255, 255);
		private int columnNumIndx = 1;

		public MappingLabelProvider() {
			super();
		}

		public Color getForeground(Object element, int columnIndex) {
			if (columnIndex == columnNumIndx)
				return new Color(null, 128, 128, 128);
			return null;
		}

		public Color getBackground(Object element, int columnIndex) {
			int rowNum = input.indexOf((Mapping) element);
			return (rowNum % 2 == 0) ? COLOR_EVEN : COLOR_ODD;
		}

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof Mapping) {
				if (0 == columnIndex) {
					return ((Mapping) element).getFrom();
				}
				if (1 == columnIndex) {
					return ((Mapping) element).getTo();
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

		paraViewer.setContentProvider(new MappingContentProvider());
		paraViewer.setLabelProvider(new MappingLabelProvider());
		final Table table = paraViewer.getTable();
		TableColumn column = new TableColumn(table, SWT.NONE);
		column.setText("From");
		column.setWidth(100);
		column = new TableColumn(table, SWT.NONE);
		column.setText("To");
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
				MappingDialog pd = new MappingDialog(getShell(), null,context);
				int open = pd.open();
				if (open == MappingDialog.OK) {
					Mapping mapping = pd.getMapping();
					input.add(mapping);
					paraViewer.setInput(input);
				}
			}
		});
		final Button edit = new Button(bs, SWT.NONE);
		edit.setText("  Edit  ");
		edit.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				MappingDialog pd = new MappingDialog(getShell(), c_mapping,context);
				int open = pd.open();
				if (open == MappingDialog.OK) {
					Mapping mapping = pd.getMapping();
					c_mapping.setFrom(mapping.getFrom());
					c_mapping.setTo(mapping.getTo());
					paraViewer.refresh(c_mapping);
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
					List<Mapping> list = ss.toList();
					edit.setEnabled(list.size() == 1);
					del.setEnabled(list.size() > 0);
					c_mapping = list.size() > 0 ? list.get(0) : null;
				}
			}
		});
		del.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				input.remove(c_mapping);
				paraViewer.setInput(input);
			}
		});
	}

	public void setMappings(List<Mapping> paras) {
		if (paras != null) {
			for (Mapping p : paras) {
				this.input.add(p.clone());
			}
		}

		paraViewer.setInput(input);
	}

	public List<Mapping> getMappings() {
		return input;
	}

}
