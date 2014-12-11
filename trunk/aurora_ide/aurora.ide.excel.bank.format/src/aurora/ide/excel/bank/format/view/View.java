package aurora.ide.excel.bank.format.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import aurora.excel.model.format.runner.Runner;
import aurora.excel.model.format.runner.XLSFileSetting;
import aurora.ide.excel.bank.format.setting.OutputFileSettingDialog;
import aurora.ide.excel.bank.format.setting.PreferencesSetting;
import aurora.ide.excel.bank.format.setting.PropertySettingDialog;

public class View extends ViewPart {
	public static final String ID = "aurora.ide.excel.bank.format.view";

	private TableViewer viewer;

	private CTableViewer v;

	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			if (parent instanceof Object[]) {
				return (Object[]) parent;
			}
			return new Object[0];
		}
	}

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		parent.setLayout(layout);
		v = new CTableViewer() {

			protected void clickAddButton(Shell shell, final TableViewer tv) {
				FileDialog dialog = new FileDialog(shell, SWT.OPEN);
				dialog.setText("Open File"); //$NON-NLS-1$
				dialog.setFilterExtensions(new String[] { "*.xls" }); //$NON-NLS-1$
				String path = dialog.open();
				if (path != null && path.length() > 0) {
					getTableInput().add(path);
				}
				setInput(tv);
			}

			protected void createButton(final Composite tableComposite,
					final TableViewer tv, Composite buttonComposite) {
				super.createButton(tableComposite, tv, buttonComposite);
				Button convert = WidgetFactory.button(buttonComposite, "运行");
				convert.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						doConvert(e);
					}

				});
			}

			protected void handleDoubleClick(DoubleClickEvent event) {
				String string = event.getSelection().toString();
				String fName = new Path(string).removeFileExtension()
						.lastSegment();
				new PropertySettingDialog(View.this.getSite().getShell(), fName)
						.open();
			}
		};
		v.addColumn("表单码", 100);
		v.addColumn("表单地址", 300);
		viewer = v.createContentTable(parent);
	}

	private void doConvert(SelectionEvent e) {
		OutputFileSettingDialog d = new OutputFileSettingDialog(this.getSite()
				.getShell());
		int open = d.open();
		if (OutputFileSettingDialog.OK == open) {
			DirectoryDialog dialog = new DirectoryDialog(this.getSite()
					.getShell());
			dialog.setText("保存地址");
			String _path = dialog.open();
			if (_path != null && _path.length() > 0) {
				List<Object> input = v.getInput();
				List<XLSFileSetting> settings = new ArrayList<XLSFileSetting>();
				for (Object object : input) {
					String path = "" + object;
					String fName = new Path(path).removeFileExtension()
							.lastSegment();
					XLSFileSetting set = new XLSFileSetting();
					set.setFilePath(path);
					set.setXls_setting(PreferencesSetting.loadXLSSetting(fName));
					settings.add(set);
				}
				boolean run = new Runner(d.getSetting(), settings, _path).run();
				if (run) {
					MessageDialog.openInformation(this.getSite().getShell(),
							"完成", "转换完成");
				}
			}
		}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}