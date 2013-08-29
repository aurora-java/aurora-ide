package aurora.ide.prototype.consultant.product.fsd.wizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import aurora.ide.prototype.consultant.product.fsd.FunctionDesc;
import aurora.ide.swt.util.GridLayoutUtil;
import aurora.ide.swt.util.TextField;
import aurora.ide.swt.util.WidgetFactory;

public class FunctionDescPage extends WizardPage implements ModifyListener {

	private FunctionDesc model;

	protected FunctionDescPage(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		setModel(new FunctionDesc());
	}

	private TextField createInputField(Composite parent, String label,
			String key) {
		TextField createTextField = WidgetFactory
				.createTextField(parent, label);
		createTextField.addModifyListener(getModel().createModifyListener(key));
		createTextField.addModifyListener(this);
		return createTextField;
	}

	@Override
	public void createControl(Composite root) {

		Composite parent = WidgetFactory.composite(root);
		parent.setLayout(GridLayoutUtil.COLUMN_LAYOUT_1);

		GridLayout ly = new GridLayout(2, false);

		Composite functonComposite = new Composite(parent, SWT.NONE);
		functonComposite.setLayout(ly);
		functonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createInputField(functonComposite, Messages.FunctionDescPage_0, FunctionDesc.doc_title);
		createInputField(functonComposite, Messages.FunctionDescPage_1, FunctionDesc.fun_code);
		createInputField(functonComposite, Messages.FunctionDescPage_2, FunctionDesc.fun_name);

		new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL)
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite infoComposite = new Composite(parent, SWT.NONE);
		infoComposite.setLayout(new GridLayout(2, false));
		infoComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createInputField(infoComposite, Messages.FunctionDescPage_3, FunctionDesc.writer);
		createInputField(infoComposite, Messages.FunctionDescPage_4, FunctionDesc.c_date);
		createInputField(infoComposite, Messages.FunctionDescPage_5, FunctionDesc.u_date);
		createInputField(infoComposite, Messages.FunctionDescPage_6, FunctionDesc.no);
		createInputField(infoComposite, Messages.FunctionDescPage_7, FunctionDesc.ver);

		new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL)
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Group applyComposite = new Group(parent, SWT.NONE);
		applyComposite.setText(Messages.FunctionDescPage_8);
		// Composite applyComposite = new Composite(parent, SWT.NONE);
		applyComposite.setLayout(ly);
		applyComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createInputField(applyComposite, Messages.FunctionDescPage_9, FunctionDesc.c_manager);
		createInputField(applyComposite, Messages.FunctionDescPage_10, FunctionDesc.dept);
		createInputField(applyComposite, Messages.FunctionDescPage_11, FunctionDesc.h_manager);

		this.setControl(parent);
		modifyText(null);
	}

	protected boolean verifyPage(String key, String message) {
		Object propertyValue = getModel().getPropertyValue(key);
		if (propertyValue == null || "".equals(propertyValue)) { //$NON-NLS-1$
			this.setErrorMessage(message);
			this.setPageComplete(false);
			return false;
		}
		this.setErrorMessage(null);
		this.setPageComplete(true);
		return true;
	}

	@Override
	public void modifyText(ModifyEvent e) {
		if (verifyPage(FunctionDesc.doc_title, Messages.FunctionDescPage_13)
				&& verifyPage(FunctionDesc.fun_code, Messages.FunctionDescPage_14)
				&& verifyPage(FunctionDesc.fun_name, Messages.FunctionDescPage_15))
			;
	}

	public FunctionDesc getModel() {
		return model;
	}

	public void setModel(FunctionDesc model) {
		this.model = model;
	}

}
