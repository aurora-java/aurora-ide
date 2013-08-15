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
		// 文档标题
		createInputField(functonComposite, "文档标题", FunctionDesc.doc_title);
		// 功能号
		createInputField(functonComposite, "功能号", FunctionDesc.fun_code);
		// 功能描述
		createInputField(functonComposite, "功能名", FunctionDesc.fun_name);

		new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL)
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite infoComposite = new Composite(parent, SWT.NONE);
		infoComposite.setLayout(new GridLayout(2, false));
		infoComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// 作者
		createInputField(infoComposite, "作者", FunctionDesc.writer);
		// 建档日期
		createInputField(infoComposite, "建档日期", FunctionDesc.c_date);
		// 上次更新
		createInputField(infoComposite, "上次更新", FunctionDesc.u_date);
		// 控制号
		createInputField(infoComposite, "控制号", FunctionDesc.no);
		// 版本
		createInputField(infoComposite, "版本", FunctionDesc.ver);

		new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL)
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Group applyComposite = new Group(parent, SWT.NONE);
		applyComposite.setText("审批");
		// Composite applyComposite = new Composite(parent, SWT.NONE);
		applyComposite.setLayout(ly);
		applyComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// 客户项目经理
		createInputField(applyComposite, "客户经理", FunctionDesc.c_manager);
		// 相关业务部门
		createInputField(applyComposite, "相关部门", FunctionDesc.dept);
		// 汉得项目经理
		createInputField(applyComposite, "汉得经理", FunctionDesc.h_manager);

		this.setControl(parent);
		modifyText(null);
	}

	protected boolean verifyPage(String key, String message) {
		Object propertyValue = getModel().getPropertyValue(key);
		if (propertyValue == null || "".equals(propertyValue)) {
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
		if (verifyPage(FunctionDesc.doc_title, "文档标题不能为空")
				&& verifyPage(FunctionDesc.fun_code, "功能号不能为空")
				&& verifyPage(FunctionDesc.fun_name, "功能名不能为空"))
			;
	}

	public FunctionDesc getModel() {
		return model;
	}

	public void setModel(FunctionDesc model) {
		this.model = model;
	}

}
