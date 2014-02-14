package aurora.ide.prototype.consultant.view.wizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import uncertain.composite.CompositeMap;
import aurora.ide.prototype.consultant.product.fsd.FunctionDesc;
import aurora.ide.prototype.consultant.product.fsd.wizard.ApplyControl;
import aurora.ide.prototype.consultant.product.fsd.wizard.AuthorControl;
import aurora.ide.prototype.consultant.product.fsd.wizard.Messages;
import aurora.ide.prototype.consultant.product.fsd.wizard.TitleControl;
import aurora.ide.swt.util.GridLayoutUtil;
import aurora.ide.swt.util.UWizardPage;

public class ProjectFSDDescPage extends UWizardPage {

	public static final String[] properties = new String[] {
			FunctionDesc.doc_title, FunctionDesc.fun_code,
			FunctionDesc.fun_name, FunctionDesc.writer, FunctionDesc.c_date,
			FunctionDesc.u_date, FunctionDesc.no, FunctionDesc.ver,
			FunctionDesc.c_manager, FunctionDesc.dept, FunctionDesc.h_manager };

	protected ProjectFSDDescPage(String pageName, String title,
			ImageDescriptor titleImage, CompositeMap input) {
		super(pageName);
		this.setTitle(title);
		this.setImageDescriptor(titleImage);
		new TitleControl(this.getModel()).loadFromMap(input);
		new AuthorControl(this.getModel()).loadFromMap(input);
		new ApplyControl(this.getModel()).loadFromMap(input);
	}

	protected String[] getModelPropertyKeys() {
		return properties;
	}

	@Override
	protected String verifyModelProperty(String key, Object val) {
		if(properties[0].equals(key)){
			if(val == null || "".equals(val)){ //$NON-NLS-1$
				return Messages.ProjectFSDDescPage_1;
			}
		}
		return null;
	}

	@Override
	protected Composite createPageControl(Composite control) {
//		root.setLayout(GridLayoutUtil.COLUMN_LAYOUT_1);
//
//		Composite parent = WidgetFactory.composite(root);
		Composite parent = new Composite(control, SWT.NONE);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(GridLayoutUtil.COLUMN_LAYOUT_1);
		createTitleControl(parent);
		new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL)
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createAuthorControl(parent);
		new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL)
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createApplyControl(parent);
		
		return parent;
	}

	protected void createApplyControl(Composite parent) {
		new ApplyControl(this.getModel()).createApplyControl(parent);
	}

	protected void createAuthorControl(Composite parent) {
		new AuthorControl(this.getModel()).createAuthorControl(parent);
	}

	protected void createTitleControl(Composite parent) {

		new TitleControl(this.getModel()) {
			public void createTitleControl(Composite parent) {
				Composite functonComposite = new Composite(parent, SWT.NONE);
				functonComposite.setLayout(new GridLayout(2, false));
				functonComposite.setLayoutData(new GridData(
						GridData.FILL_HORIZONTAL));
				createInputField(functonComposite, Messages.FunctionDescPage_0,
						FunctionDesc.doc_title);
			}
		}.createTitleControl(parent);
	}

	protected void saveTOMap(CompositeMap map) {
		new TitleControl(this.getModel()).saveToMap(map);
		new AuthorControl(this.getModel()).saveToMap(map);
		new ApplyControl(this.getModel()).saveToMap(map);
	}
}
