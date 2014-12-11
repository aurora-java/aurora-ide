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
import aurora.ide.excel.bank.format.setting.dialog.IDXInputDialog;
import aurora.ide.excel.bank.format.view.CTableViewer;
import aurora.ide.excel.bank.format.view.Messages;
import aurora.ide.excel.bank.format.view.TableLabelProvider;
import aurora.ide.excel.bank.format.view.WidgetFactory;

public class IDXViewer extends CTableViewer {

	@Override
	public TableLabelProvider getLabelProvider() {
		return new TableLabelProvider() {
			public String getColumnText(Object element, int i) {

				if (element instanceof CompositeMap) {
					if (i == 0) {
						return ((CompositeMap) element).getText();
					}
					if (i == 1) {
						return ((CompositeMap) element).getString("value", "");
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
		Button edit = WidgetFactory.button(buttonComposite, "编辑");
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
		Button up = WidgetFactory.button(buttonComposite,
				Messages.CTableViewer_2);
		up.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				moveElement(tv, -1);
			}

		});
		Button down = WidgetFactory.button(buttonComposite,
				Messages.CTableViewer_3);
		down.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				moveElement(tv, 1);
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

			IDXInputDialog d = new IDXInputDialog(shell);
			d.setCode(firstElement.getString("name", ""));
			d.setDesc(firstElement.getText());
			d.setValue(firstElement.getString("value", ""));
			int open = d.open();
			if (IDXInputDialog.OK == open) {
				firstElement.setText(d.getDesc());
				firstElement.put("name", d.getCode());
				firstElement.put("value", d.getValue());
				setInput(tv);
			}
		}
	}

	protected void clickAddButton(Shell shell, TableViewer tv) {
		IDXInputDialog d = new IDXInputDialog(shell);
		int open = d.open();
		if (IDXInputDialog.OK == open) {
			CompositeMap m = PreferencesSetting.createMap("custom",
					d.getDesc(), d.getValue());
			getTableInput().add(m);
			setInput(tv);
		}
	}

}
