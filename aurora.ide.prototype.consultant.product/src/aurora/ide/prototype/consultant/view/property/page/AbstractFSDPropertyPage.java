package aurora.ide.prototype.consultant.view.property.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.dialogs.PropertyPage;

import aurora.ide.prototype.consultant.product.fsd.wizard.ApplyControl;
import aurora.ide.prototype.consultant.product.fsd.wizard.AuthorControl;
import aurora.ide.prototype.consultant.product.fsd.wizard.FSDContentControl;
import aurora.ide.prototype.consultant.product.fsd.wizard.TitleControl;
import aurora.ide.swt.util.GridLayoutUtil;
import aurora.ide.swt.util.PageModel;
import aurora.ide.swt.util.WidgetFactory;

public class AbstractFSDPropertyPage extends PropertyPage {

	private PageModel model;

	@Override
	protected Control createContents(Composite parent) {
		Composite content = new Composite(parent, SWT.NONE);
		/* Create the example widgets */
		content.setLayout(new GridLayout());
		TabFolder tf = new TabFolder(content, SWT.TOP);
		tf.setLayoutData(new GridData(GridData.FILL_BOTH));
		TabItem item1 = new TabItem(tf, SWT.NONE);
		item1.setText("描述");
		item1.setToolTipText("FSD描述");
		Composite c = new Composite(tf, SWT.NONE);
		createFSDDescControl(c);
		item1.setControl(c);

		TabItem item2 = new TabItem(tf, SWT.NONE);
		item2.setText("内容");
		item2.setToolTipText("FSD内容");
		Composite c2 = new Composite(tf, SWT.NONE);
		new FSDContentControl(this.getModel()).createFSDContentControl(c2);
		item2.setControl(c2);
		return content;
	}


	protected void createFSDDescControl(Composite root) {

		Composite parent = WidgetFactory.composite(root);
		parent.setLayout(GridLayoutUtil.COLUMN_LAYOUT_1);
		new TitleControl(this.getModel()).createTitleControl(parent);
		new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL)
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new AuthorControl(this.getModel()).createAuthorControl(parent);
		new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL)
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new ApplyControl(this.getModel()).createApplyControl(parent);
	}


	public PageModel getModel() {
		if (model == null)
			model = new PageModel();
		return model;
	}

}
