package aurora.ide.excel.bank.format.setting;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import aurora.ide.excel.bank.format.setting.dialog.IDXInputDialog;
import aurora.ide.excel.bank.format.view.CTableViewer;
import aurora.ide.excel.bank.format.view.Messages;
import aurora.ide.excel.bank.format.view.TableLabelProvider;
import aurora.ide.excel.bank.format.view.WidgetFactory;

public class DATViewer extends CTableViewer {

	@Override
	public TableLabelProvider getLabelProvider() {
		return new TableLabelProvider() {
			public String getColumnText(Object element, int i) {

				if (element instanceof CompositeMap) {
					if (i == 0) {
						return ((CompositeMap) element).getText();
					}
					if (i == 1) {
						return ((CompositeMap) element).getString("value", ""); //$NON-NLS-1$ //$NON-NLS-2$
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
		Button edit = WidgetFactory.button(buttonComposite, Messages.DATViewer_2);
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
				clickDelButton(tableComposite.getShell(), tv);
			}
		});
	}

	protected void clickDelButton(Shell shell, TableViewer tv) {

		ISelection s = tv.getSelection();
		if (s instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) s;
			if (ss.isEmpty())
				return;

			CompositeMap firstElement = (CompositeMap) ss.getFirstElement();
			if ("data".equals(firstElement.getName())) { //$NON-NLS-1$
				super.clickDelButton(tv);
			} else {
				MessageDialog.openInformation(shell, Messages.DATViewer_4, Messages.DATViewer_5
						+ firstElement.getText());
			}
		}
	}

	protected void clickEditButton(Shell shell, TableViewer tv) {
		ISelection s = tv.getSelection();
		if (s instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) s;
			if (ss.isEmpty())
				return;

			CompositeMap firstElement = (CompositeMap) ss.getFirstElement();

			IDXInputDialog d = new IDXInputDialog(shell);
			d.setCode(firstElement.getString("name", "")); //$NON-NLS-1$ //$NON-NLS-2$
			d.setDesc(firstElement.getText());
			d.setValue(firstElement.getString("value", "")); //$NON-NLS-1$ //$NON-NLS-2$
			int open = d.open();
			if (IDXInputDialog.OK == open) {
				firstElement.setText(d.getDesc());
				firstElement.put("name", d.getCode()); //$NON-NLS-1$
				firstElement.put("value", d.getValue()); //$NON-NLS-1$
				setInput(tv);
			}
		}
	}

	protected void clickAddButton(Shell shell, TableViewer tv) {
		IDXInputDialog d = new IDXInputDialog(shell);
		d.setCode("data"); //$NON-NLS-1$
		d.setDesc(Messages.DATViewer_13);
		d.setValue("3"); //$NON-NLS-1$
		int open = d.open();
		if (IDXInputDialog.OK == open) {
			CompositeMap m = PreferencesSetting.createMap("data", d.getDesc(), //$NON-NLS-1$
					d.getValue());
			getTableInput().add(m);
			setInput(tv);
		}
	}

}
