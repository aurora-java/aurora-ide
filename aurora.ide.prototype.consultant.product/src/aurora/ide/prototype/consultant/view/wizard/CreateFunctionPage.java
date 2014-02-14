package aurora.ide.prototype.consultant.view.wizard;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import aurora.ide.prototype.consultant.product.fsd.FunctionDesc;
import aurora.ide.prototype.consultant.product.fsd.wizard.Messages;
import aurora.ide.prototype.consultant.view.util.ResourceUtil;
import aurora.ide.swt.util.GridLayoutUtil;
import aurora.ide.swt.util.TextField;
import aurora.ide.swt.util.UWizardPage;
import aurora.ide.swt.util.WidgetFactory;

public class CreateFunctionPage extends UWizardPage {

	public static String[] properties = new String[] { FunctionDesc.fun_code,
			FunctionDesc.fun_name, FunctionDesc.writer, FunctionDesc.c_date,
			FunctionDesc.u_date, FunctionDesc.no, FunctionDesc.ver };
	private File f_parent;

	protected CreateFunctionPage(String pageName, File parent) {
		super(pageName);
		this.setTitle("Aurora Quick UI"); //$NON-NLS-1$
		this.setMessage(Messages.CreateFunctionPage_1);
		this.f_parent = parent;
	}

	private TextField createInputField(Composite parent, String label,
			String key) {
		TextField createTextField = WidgetFactory
				.createTextField(parent, label);
		createTextField.addModifyListener(new TextModifyListener(key,
				createTextField.getText()));
		return createTextField;
	}

	@Override
	protected String[] getModelPropertyKeys() {
		return properties;
	}

	@Override
	protected String verifyModelProperty(String key, Object val) {
		if (properties[0].equals(key)) {
			if (val == null || "".equals(val)) { //$NON-NLS-1$
				return Messages.CreateFunctionPage_3;
			}
			String n = "" + val; //$NON-NLS-1$
			IPath p = new Path(n);
			if (p.segmentCount() != 1 || p.hasTrailingSeparator()) {
				return Messages.CreateFunctionPage_5;
			}
			File m = new File(f_parent, n);
			if (m.exists()) {
				return Messages.CreateFunctionPage_6;
			}

		}
		if (properties[1].equals(key)) {
			if (val == null || "".equals(val)) { //$NON-NLS-1$
				return Messages.CreateFunctionPage_8;
			}
		}
		return null;
	}

	@Override
	protected Composite createPageControl(Composite control) {
		Composite parent = new Composite(control, SWT.NONE);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(GridLayoutUtil.COLUMN_LAYOUT_1);

		GridLayout ly = new GridLayout(2, false);

		Composite functonComposite = new Composite(parent, SWT.NONE);
		functonComposite.setLayout(ly);
		functonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		TextField pjField = createInputField(functonComposite, Messages.CreateFunctionPage_9, "pj_name"); //$NON-NLS-2$
		pjField.getText().setEnabled(false);
		File project = ResourceUtil.getProject(this.f_parent);
		if (project != null) {
			pjField.setText(ResourceUtil.getFullProjectRelativePath(project,
					this.f_parent).toString());
		} else {
			pjField.setText(this.f_parent.getName());
		}

		createInputField(functonComposite, Messages.FunctionDescPage_1,
				FunctionDesc.fun_code);
		createInputField(functonComposite, Messages.FunctionDescPage_2,
				FunctionDesc.fun_name);

		new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL)
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite infoComposite = new Composite(parent, SWT.NONE);
		infoComposite.setLayout(new GridLayout(2, false));
		infoComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createInputField(infoComposite, Messages.FunctionDescPage_3,
				FunctionDesc.writer);
		createInputField(infoComposite, Messages.FunctionDescPage_4,
				FunctionDesc.c_date);
		createInputField(infoComposite, Messages.FunctionDescPage_5,
				FunctionDesc.u_date);
		createInputField(infoComposite, Messages.FunctionDescPage_6,
				FunctionDesc.no);
		createInputField(infoComposite, Messages.FunctionDescPage_7,
				FunctionDesc.ver);
		return parent;

	}

}
