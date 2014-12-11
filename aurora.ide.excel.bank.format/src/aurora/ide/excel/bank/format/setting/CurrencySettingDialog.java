package aurora.ide.excel.bank.format.setting;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import uncertain.composite.CompositeMap;
import aurora.ide.excel.bank.format.view.WidgetFactory;

public class CurrencySettingDialog extends Dialog {

	private CompositeMap loadCurrency;
	private CurrencyViewer v2;
	private TableViewer tableViewer;
	private CompositeMap firstElement;

	public CurrencySettingDialog(Shell parentShell) {
		super(parentShell);
		loadCurrency = CurrencySetting.loadCurrency();
	}

	@Override
	protected Control createContents(Composite parent) {
		return super.createContents(parent);
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		return super.createButtonBar(parent);
	}

	protected void createButtonsForButtonBar(Composite parent) {
		// create OK and Cancel buttons by default
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite cc = (Composite) super.createDialogArea(parent);

		Composite c2 = new Composite(cc, SWT.NONE);
		c2.setLayout(new GridLayout());
		c2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label l = new Label(c2, SWT.NONE);
		l.setText("币种");
		l.setFont(JFaceResources.getBannerFont());
		WidgetFactory.hSeparator(c2);

		Composite c = new Composite(cc, SWT.NONE);
		c.setLayout(new GridLayout());
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout ly = (GridLayout) c.getLayout();
		ly.numColumns = 2;

		v2 = new CurrencyViewer();
		v2.addColumn("币种", 100);
		v2.addColumn("币种单位", 100);
		v2.setInput(loadCurrency.getChildsNotNull());
		tableViewer = v2.createContentTable(c);
		return c;
	}

	private void createProperty(Composite c, String text, String value) {
		Label n = new Label(c, SWT.NONE);
		n.setText(text);
		Text t = new Text(c, SWT.READ_ONLY | SWT.WRAP);
		t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		t.setBackground(t.getDisplay().getSystemColor(
				SWT.COLOR_WIDGET_BACKGROUND));
		t.setText(value);
	}

	@Override
	protected Point getInitialSize() {
		Point initialSize = super.getInitialSize();
		return new Point(400, 450);
	}

	protected void okPressed() {
		// if(v2.)
		ISelection selection = tableViewer.getSelection();
		if (selection instanceof IStructuredSelection) {
			boolean empty = selection.isEmpty();
			if (empty == false) {
				setFirstElement((CompositeMap) ((IStructuredSelection) selection)
						.getFirstElement());
				super.okPressed();
				CurrencySetting.saveCurrency(loadCurrency);
				return;
			}
		}
		MessageDialog.openInformation(this.getShell(), "INFO", "未选择币种");
	}

	public CompositeMap getFirstElement() {
		return firstElement;
	}

	private void setFirstElement(CompositeMap firstElement) {
		this.firstElement = firstElement;
	}

}
