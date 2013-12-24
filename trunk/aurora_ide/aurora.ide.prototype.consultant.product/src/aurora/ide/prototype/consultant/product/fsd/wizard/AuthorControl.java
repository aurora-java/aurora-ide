package aurora.ide.prototype.consultant.product.fsd.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import aurora.ide.prototype.consultant.product.fsd.FunctionDesc;
import aurora.ide.swt.util.PageModel;

public class AuthorControl extends FSDComposite {

	public AuthorControl(PageModel model) {
		super(model);
	}
	public void createAuthorControl(Composite parent) {
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
	}
}
