package aurora.ide.excel.bank.format.setting;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import aurora.ide.excel.bank.format.setting.dialog.CurrencyInputDialog;
import aurora.ide.excel.bank.format.view.CTableViewer;
import aurora.ide.excel.bank.format.view.Messages;
import aurora.ide.excel.bank.format.view.TableLabelProvider;
import aurora.ide.excel.bank.format.view.WidgetFactory;

public class IDXListViewer extends CTableViewer {

	private String xls_code;

	public IDXListViewer(String xls_code) {
		this.xls_code = xls_code;
	}

	@Override
	public TableLabelProvider getLabelProvider() {
		return new TableLabelProvider() {
			public String getColumnText(Object element, int i) {

				if (element instanceof CompositeMap) {
					if (i == 0) {
						return ((CompositeMap) element).getString("desc", ""); //$NON-NLS-1$ //$NON-NLS-2$
					}
					if (i == 1) {
						return "I00006|A3411|c302|1201070|1|CNY0001|5|1|1|C5003912000016"; //$NON-NLS-1$
					}
				}
				return ""; //$NON-NLS-1$
			}
		};
	}

	@Override
	protected void createButton(final Composite tableComposite,
			final TableViewer tv, Composite buttonComposite) {

		Button add = WidgetFactory.button(buttonComposite,
				Messages.CTableViewer_0);
		add.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				clickAddButton(tableComposite.getShell(), tv);
			}
		});
		Button edit = WidgetFactory.button(buttonComposite, Messages.IDXListViewer_3);
		edit.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				clickEditButton(tableComposite.getShell(), tv);
			}
		});
		Button del = WidgetFactory.button(buttonComposite,
				Messages.CTableViewer_1);
		del.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				clickDelButton(tv);
			}
		});
	}

	protected void clickEditButton(Shell shell, TableViewer tv) {
		ISelection s = tv.getSelection();
		if (s instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) s;
			if (ss.isEmpty())
				return;
			CompositeMap firstElement = (CompositeMap) ss.getFirstElement();

			IDXSettingDialog d = new IDXSettingDialog(shell, firstElement);
			int open = d.open();
			if (CurrencyInputDialog.OK == open) {
				CompositeMap result = d.getResult();
				firstElement.clear();
				firstElement.setName(result.getName());
				firstElement.put("desc", result.getString("desc", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				firstElement.copy(result);
				setInput(tv);
			}
		}
	}

	protected void clickAddButton(Shell shell, TableViewer tv) {
		IDXSettingDialog d = new IDXSettingDialog(shell,
				PreferencesSetting.defautIDXMap(xls_code));
		int open = d.open();
		if (CurrencyInputDialog.OK == open) {
			CompositeMap m = d.getResult();
			getTableInput().add(m);
			setInput(tv);
		}
	}

}
