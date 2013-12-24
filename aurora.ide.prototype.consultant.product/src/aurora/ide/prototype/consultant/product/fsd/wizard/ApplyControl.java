package aurora.ide.prototype.consultant.product.fsd.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import aurora.ide.prototype.consultant.product.fsd.FunctionDesc;
import aurora.ide.swt.util.PageModel;

public class ApplyControl extends FSDComposite {

	public ApplyControl(PageModel model) {
		super(model);
	}

	public void createApplyControl(Composite parent) {
		Group applyComposite = new Group(parent, SWT.NONE);
		applyComposite.setText(Messages.FunctionDescPage_8);
		applyComposite.setLayout(new GridLayout(1, false));
		applyComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createInputField(applyComposite, Messages.FunctionDescPage_9,
				FunctionDesc.c_manager);
		createInputField(applyComposite, Messages.FunctionDescPage_10,
				FunctionDesc.dept);
		createInputField(applyComposite, Messages.FunctionDescPage_11,
				FunctionDesc.h_manager);

	}
}
