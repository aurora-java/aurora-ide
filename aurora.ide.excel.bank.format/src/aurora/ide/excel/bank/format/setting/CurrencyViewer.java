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

public class CurrencyViewer extends CTableViewer {

	@Override
	public TableLabelProvider getLabelProvider() {
		return new TableLabelProvider() {

			public String getColumnText(Object element, int i) {

				if (element instanceof CompositeMap) {
					if (i == 0) {
						return getCurType(element);
					}
					if (i == 1) {
						return getUnit(element);
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
		Button edit = WidgetFactory.button(buttonComposite, Messages.CurrencyViewer_1);
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
			if(ss.isEmpty())
				return;
			CompositeMap firstElement = (CompositeMap) ss.getFirstElement();
			CurrencyInputDialog d = new CurrencyInputDialog(shell);
			d.setCurrency_type(this.getCurType(firstElement));
			d.setUnit(this.getUnit(firstElement));
			int open = d.open();
			if (CurrencyInputDialog.OK == open) {
				CompositeMap m = CurrencySetting.createMap(
						d.getCurrency_type(), d.getUnit());
				firstElement.put("currency_type", this.getCurType(m)); //$NON-NLS-1$
				firstElement.put("unit", this.getUnit(m)); //$NON-NLS-1$
				setInput(tv);
			}

		}

	}

	protected void clickAddButton(Shell shell, TableViewer tv) {
		CurrencyInputDialog d = new CurrencyInputDialog(shell);
		int open = d.open();
		if (CurrencyInputDialog.OK == open) {
			CompositeMap m = CurrencySetting.createMap(d.getCurrency_type(),
					d.getUnit());
			getTableInput().add(m);
			setInput(tv);
		}
	}

	public String getCurType(Object element) {
		return ((CompositeMap) element).getString("currency_type", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getUnit(Object element) {
		return ((CompositeMap) element).getString("unit", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
