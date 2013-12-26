package aurora.ide.prototype.consultant.product.fsd.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import uncertain.composite.CompositeMap;

import aurora.ide.prototype.consultant.product.fsd.FunctionDesc;
import aurora.ide.swt.util.PageModel;

public class TitleControl extends FSDComposite {

	public TitleControl(PageModel model) {
		super(model);
	}

	public void createTitleControl(Composite parent) {
		Composite functonComposite = new Composite(parent, SWT.NONE);
		functonComposite.setLayout(new GridLayout(2, false));
		functonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createInputField(functonComposite, Messages.FunctionDescPage_0,
				FunctionDesc.doc_title);
		createInputField(functonComposite, Messages.FunctionDescPage_1,
				FunctionDesc.fun_code);
		createInputField(functonComposite, Messages.FunctionDescPage_2,
				FunctionDesc.fun_name);
	}

	public void saveToMap(CompositeMap map) {
		map.createChild(FunctionDesc.doc_title).setText(
				this.getModel().getStringPropertyValue(FunctionDesc.doc_title));
		map.createChild(FunctionDesc.fun_code).setText(
				this.getModel().getStringPropertyValue(FunctionDesc.fun_code));
		map.createChild(FunctionDesc.fun_name).setText(
				this.getModel().getStringPropertyValue(FunctionDesc.fun_name));
	}

	public void loadFromMap(CompositeMap map) {
		this.updateModel(FunctionDesc.doc_title,
				getMapCData(FunctionDesc.doc_title, map));
		this.updateModel(FunctionDesc.fun_code,
				getMapCData(FunctionDesc.fun_code, map));
		this.updateModel(FunctionDesc.fun_name,
				getMapCData(FunctionDesc.fun_name, map));
	}

}
