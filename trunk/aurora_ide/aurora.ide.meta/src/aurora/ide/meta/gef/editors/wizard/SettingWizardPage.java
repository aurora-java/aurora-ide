package aurora.ide.meta.gef.editors.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import aurora.ide.meta.gef.editors.wizard.template.Temlpate;

public class SettingWizardPage extends WizardPage {

	private Temlpate template;

	private Composite container;
	private Composite compositeQuery;
	private Composite compositeOperation;
	private Composite compositeResult;

	public SettingWizardPage() {
		super("aurora.wizard.setting.Page");
	}

	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		setControl(container);
	}

	public void createQueryArea() {
		compositeQuery = new Composite(container, SWT.NONE);
		compositeQuery.setLayout(new GridLayout());

		Label lblId = new Label(compositeQuery, SWT.None);
		lblId.setText("ID:");
		Text txtId = new Text(compositeQuery, SWT.BORDER);

		Label lblModel = new Label(compositeQuery, SWT.None);
		lblModel.setText("Model:");
		Combo cboModel = new Combo(compositeQuery, SWT.BORDER | SWT.READ_ONLY);
		cboModel.add("Form");
		cboModel.add("TextField");
		cboModel.setText(template.getName());

		Label lblRow = new Label(compositeQuery, SWT.None);
		lblRow.setText("Row:");
		Text txtRow = new Text(compositeQuery, SWT.BORDER);

		Label lblColumn = new Label(compositeQuery, SWT.None);
		lblColumn.setText("Column:");
		Text txtColumn = new Text(compositeQuery, SWT.BORDER);

		Button btnAddBM = new Button(compositeQuery, SWT.None);
		btnAddBM.setText("绑定BM");

		container.layout(true);
	}

	public void createOperationArea() {
		compositeOperation = new Composite(container, SWT.NONE);
		compositeOperation.setLayout(new GridLayout());

		container.layout(true);
	}

	public void createResultArea() {
		compositeResult = new Composite(container, SWT.NONE);
		compositeResult.setLayout(new GridLayout());

		container.layout(true);
	}

	public void deleteArea() {
		if (compositeQuery != null && (!compositeQuery.isDisposed())) {
			compositeQuery.dispose();
		}
		if (compositeOperation != null && (!compositeOperation.isDisposed())) {
			compositeOperation.dispose();
		}
		if (compositeResult != null && (!compositeResult.isDisposed())) {
			compositeResult.dispose();
		}
	}

	public Temlpate getTemplate() {
		return template;
	}

	public void setTemplate(Temlpate template) {
		this.template = template;
	}

}
