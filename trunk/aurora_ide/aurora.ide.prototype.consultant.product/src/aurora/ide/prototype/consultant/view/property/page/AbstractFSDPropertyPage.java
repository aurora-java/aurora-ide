package aurora.ide.prototype.consultant.view.property.page;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.dialogs.PropertyPage;

import uncertain.composite.CompositeMap;
import aurora.ide.prototype.consultant.product.fsd.wizard.ApplyControl;
import aurora.ide.prototype.consultant.product.fsd.wizard.AuthorControl;
import aurora.ide.prototype.consultant.product.fsd.wizard.FSDContentControl;
import aurora.ide.prototype.consultant.product.fsd.wizard.TitleControl;
import aurora.ide.prototype.consultant.view.FunctionSelectionDialog;
import aurora.ide.prototype.consultant.view.Node;
import aurora.ide.prototype.consultant.view.util.ResourceUtil;
import aurora.ide.swt.util.GridLayoutUtil;
import aurora.ide.swt.util.PageModel;
import aurora.ide.swt.util.WidgetFactory;

abstract public class AbstractFSDPropertyPage extends PropertyPage {

	private PageModel model;

	public AbstractFSDPropertyPage() {
		this.noDefaultAndApplyButton();
	}

	@Override
	public void setElement(IAdaptable element) {
		super.setElement(element);
		loadPageModel();
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite content = new Composite(parent, SWT.NONE);
		/* Create the example widgets */
		content.setLayout(new GridLayout());
		TabFolder tf = new TabFolder(content, SWT.TOP);
		tf.setLayoutData(new GridData(GridData.FILL_BOTH));
		TabItem item1 = new TabItem(tf, SWT.NONE);
		item1.setText(Messages.AbstractFSDPropertyPage_0);
		item1.setToolTipText(Messages.AbstractFSDPropertyPage_1);
		Composite c = new Composite(tf, SWT.NONE);
		c.setLayout(GridLayoutUtil.COLUMN_LAYOUT_1);
		createFSDDescControl(c);
		item1.setControl(c);

		TabItem item2 = new TabItem(tf, SWT.NONE);
		item2.setText(Messages.AbstractFSDPropertyPage_2);
		item2.setToolTipText(Messages.AbstractFSDPropertyPage_3);
		Composite c2 = new Composite(tf, SWT.NONE);
		c2.setLayout(GridLayoutUtil.COLUMN_LAYOUT_1);
		createContentControl(c2);
		item2.setControl(c2);
		return content;
	}

	protected void createContentControl(Composite c2) {
		new FSDContentControl(this.getModel(),getBasePath()) {
			protected void clickAddButton(Shell shell, final TableViewer tv) {
				FunctionSelectionDialog fsd = new FunctionSelectionDialog();
				String path = fsd.openUIPSelectionDialog(Messages.AbstractFSDPropertyPage_4, shell,
						getProjectNode());
				if (path != null && path.length() > 0) {
					getTableInput().add(path);
				}
				tv.setInput(getTableInput());
			}
		}.createFSDContentControl(c2);
	}

	protected Object getProjectNode() {
		IAdaptable element = this.getElement();
		if (element instanceof Node) {
			return ResourceUtil.getProjectNode((Node) element);
		}
		return null;
	}

	protected void createFSDDescControl(Composite root) {

		Composite parent = WidgetFactory.composite(root);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(GridLayoutUtil.COLUMN_LAYOUT_1);
		createTitleControl(parent);
		new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL)
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createAuthorControl(parent);
		new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL)
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createApplyControl(parent);
	}

	protected void createApplyControl(Composite parent) {
		new ApplyControl(this.getModel()).createApplyControl(parent);
	}

	protected void createAuthorControl(Composite parent) {
		new AuthorControl(this.getModel()).createAuthorControl(parent);
	}

	protected void createTitleControl(Composite parent) {
		new TitleControl(this.getModel()).createTitleControl(parent);
	}

	public PageModel getModel() {
		if (model == null)
			model = new PageModel();
		return model;
	}

	protected void loadPageModel() {
		IAdaptable element = this.getElement();
		if (element instanceof Node) {
			File file = ((Node) element).getFile();
			CompositeMap pp = loadProperties(file);
			if (pp != null) {
				new TitleControl(this.getModel()).loadFromMap(pp);
				new AuthorControl(this.getModel()).loadFromMap(pp);
				new ApplyControl(this.getModel()).loadFromMap(pp);
				new FSDContentControl(this.getModel(),getBasePath()).loadFromMap(pp);
			}
		}
	}

	protected CompositeMap loadProperties(File file) {
		return null;
	}

	protected void saveProperties(CompositeMap map) throws IOException {
	}

	protected void saveTOMap(CompositeMap map) {
		new TitleControl(this.getModel()).saveToMap(map);
		new AuthorControl(this.getModel()).saveToMap(map);
		new ApplyControl(this.getModel()).saveToMap(map);
		new FSDContentControl(this.getModel(),getBasePath()).saveToMap(map);
	}

	abstract protected IPath getBasePath();

	@Override
	public boolean performOk() {
		CompositeMap map = new CompositeMap("properties"); //$NON-NLS-1$
		saveTOMap(map);
		try {
			saveProperties(map);
		} catch (IOException e) {
			this.setErrorMessage(Messages.AbstractFSDPropertyPage_6);
			return false;
		}
		return super.performOk();
	}
}
