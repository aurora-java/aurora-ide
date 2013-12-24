package aurora.ide.prototype.consultant.product.fsd.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import aurora.ide.prototype.consultant.product.fsd.FunctionDesc;
import aurora.ide.swt.util.PageModel;

public class TitleControl extends FSDComposite {

	public TitleControl(PageModel model) {
		super(model);
	}
	
	public void createTitleControl(Composite parent) {
		Composite functonComposite = new Composite(parent, SWT.NONE);
		functonComposite.setLayout(new GridLayout(1, false));
		functonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createInputField(functonComposite, Messages.FunctionDescPage_0,
				FunctionDesc.doc_title);
		createInputField(functonComposite, Messages.FunctionDescPage_1,
				FunctionDesc.fun_code);
		createInputField(functonComposite, Messages.FunctionDescPage_2,
				FunctionDesc.fun_name);
	}

}
