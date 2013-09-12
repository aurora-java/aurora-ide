package aurora.ide.prototype.consultant.product.fsd.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import aurora.ide.prototype.consultant.product.fsd.FunctionDesc;
import aurora.ide.swt.util.GridLayoutUtil;
import aurora.ide.swt.util.TableContentProvider;
import aurora.ide.swt.util.TableLabelProvider;
import aurora.ide.swt.util.TextField;
import aurora.ide.swt.util.WidgetFactory;

public class ContentDescPage extends WizardPage {

	private String savePath;

	private List<String> uipFiles = new ArrayList<String>();
	
	private boolean onlySaveLogic = false;

	protected ContentDescPage(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		this.setMessage(Messages.ContentDescPage_2);
	}

	@Override
	public void createControl(Composite root) {
		Composite parent = WidgetFactory.composite(root);
		parent.setLayout(GridLayoutUtil.COLUMN_LAYOUT_1);

		Composite pathComposite = WidgetFactory.composite(parent);
		pathComposite.setLayout(GridLayoutUtil.COLUMN_LAYOUT_3);
		pathComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final TextField tf = WidgetFactory.createTextButtonField(pathComposite,
				Messages.ContentDescPage_0, Messages.ContentDescPage_1);
		tf.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				Object source = e.getSource();
				if (source instanceof Text) {
					setSavePath(((Text) source).getText());
				}
				verifyPage();
			}
		});
		tf.addButtonClickListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				FileDialog sd = new FileDialog(getShell(), SWT.SAVE);
				sd.setFileName(getDefaultFileName());
				sd.setFilterExtensions(new String[] { "*.docx" }); //$NON-NLS-1$
				sd.setOverwrite(true);
				String open = sd.open();
				if (open == null || open.length() < 1) {
					return;
				}
				tf.setText(open);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		//
		final Button checked = new Button(parent, SWT.CHECK);
		checked.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		checked.setText(Messages.ContentDescPage_3);
		checked.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				setOnlySaveLogic(checked.getSelection());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		Composite tableComposite = WidgetFactory.composite(parent);

		createTable(tableComposite);
		//
		this.setControl(parent);
		verifyPage();
	}

	protected String getDefaultFileName() {
		IWizard wizard = this.getWizard();
		IWizardPage page = wizard.getPage("FunctionDescPage"); //$NON-NLS-1$
		if (page instanceof FunctionDescPage) {
			FunctionDesc model = ((FunctionDescPage) page).getModel();
			return model.getPropertyValue(FunctionDesc.fun_code) + "_" //$NON-NLS-1$
					+ model.getPropertyValue(FunctionDesc.fun_name);
		}
		return "NO_NAME"; //$NON-NLS-1$
	}

	private void createTable(Composite tableComposite) {
		tableComposite.setLayout(GridLayoutUtil.COLUMN_LAYOUT_2);
		tableComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final TableViewer tv = new TableViewer(tableComposite, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		Table table = tv.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn column1 = new TableColumn(table, SWT.LEFT);
		column1.setWidth(128);
		column1.setText(Messages.ContentDescPage_5);

		TableColumn column2 = new TableColumn(table, SWT.NONE);
		column2.setWidth(193);
		column2.setText(Messages.ContentDescPage_6);

		tv.setContentProvider(new TableContentProvider());
		tv.setLabelProvider(new TableLabelProvider() {
			public String getColumnText(Object element, int i) {

				if (element instanceof String) {
					if (i == 0) {
						Path p = new Path(element.toString());
						return p.lastSegment();
					}
					if (i == 1) {
						return element.toString();
					}
				}
				return ""; //$NON-NLS-1$
			}
		});

		Composite buttonComposite = WidgetFactory.composite(tableComposite);
		buttonComposite.setLayout(GridLayoutUtil.COLUMN_LAYOUT_1);
		buttonComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		Button add = WidgetFactory.button(buttonComposite,
				Messages.ContentDescPage_8);
		add.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
				dialog.setText("Open File"); //$NON-NLS-1$
				dialog.setFilterExtensions(new String[] { "*.uip" }); //$NON-NLS-1$
				String path = dialog.open();
				if (path != null && path.length() > 0) {
					getUipFiles().add(path);
				}
				tv.setInput(getUipFiles());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		Button del = WidgetFactory.button(buttonComposite,
				Messages.ContentDescPage_10);
		del.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ISelection s = tv.getSelection();
				if (s instanceof IStructuredSelection) {
					IStructuredSelection ss = (IStructuredSelection) s;
					List<?> list = ss.toList();
					getUipFiles().removeAll(list);
					tv.setInput(getUipFiles());
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		Button up = WidgetFactory.button(buttonComposite,
				Messages.ContentDescPage_11);
		up.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				moveElement(tv, -1);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		Button down = WidgetFactory.button(buttonComposite,
				Messages.ContentDescPage_12);
		down.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				moveElement(tv, 1);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		tv.setInput(getUipFiles());
	}

	protected boolean verifyPage() {
		if (getSavePath() == null || "".equals(getSavePath())) { //$NON-NLS-1$
			this.setErrorMessage(Messages.ContentDescPage_14);
			this.setPageComplete(false);
			return false;
		}
		// Path p = new Path(savePath);
		// // p.isValidPath(path)
		// if (propertyValue == null || "".equals(propertyValue)) {
		// this.setErrorMessage(message);
		// this.setPageComplete(false);
		// return false;
		// }
		this.setErrorMessage(null);
		this.setPageComplete(true);
		return true;
	}

	public void moveElement(final TableViewer tv, int i) {
		ISelection s = tv.getSelection();
		if (s instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) s;
			String r1 = (String) ss.getFirstElement();
			int idx = getUipFiles().indexOf(r1);
			if (idx == -1)
				return;
			int idx2 = idx + i;
			if (idx2 < 0 || idx2 == getUipFiles().size())
				return;
			String r2 = getUipFiles().get(idx2);
			getUipFiles().set(idx2, r1);
			getUipFiles().set(idx, r2);
			tv.setInput(getUipFiles());
		}
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public List<String> getUipFiles() {
		return uipFiles;
	}

	public void setUipFiles(List<String> uipFiles) {
		this.uipFiles = uipFiles;
	}

	public boolean isOnlySaveLogic() {
		return onlySaveLogic;
	}

	public void setOnlySaveLogic(boolean onlySaveLogic) {
		this.onlySaveLogic = onlySaveLogic;
	}
}
