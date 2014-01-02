package aurora.ide.prototype.consultant.view.wizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import aurora.ide.prototype.consultant.product.fsd.wizard.FSDContentControl;
import aurora.ide.prototype.consultant.product.fsd.wizard.Messages;
import aurora.ide.prototype.consultant.view.FunctionSelectionDialog;
import aurora.ide.prototype.consultant.view.Node;
import aurora.ide.swt.util.GridLayoutUtil;
import aurora.ide.swt.util.UWizardPage;

public class FunctionFSDContentPage extends UWizardPage {

	public static String[] properties = new String[] {
			FSDContentControl.FSD_DOCX_PATH, FSDContentControl.FSD_TABLE_INPUT,
			FSDContentControl.ONLY_SAVE_LOGIC };
	private Node projectNode;

	protected FunctionFSDContentPage(String pageName, String title,
			ImageDescriptor titleImage, Node projectNode, CompositeMap input) {
		super(pageName);
		this.setTitle(title);
		this.setImageDescriptor(titleImage);
		this.setMessage(Messages.ContentDescPage_2);
		this.projectNode = projectNode;
		new FSDContentControl(this.getModel()).loadFromMap(input);
	}

	@Override
	protected String[] getModelPropertyKeys() {
		return properties;
	}

	@Override
	protected String verifyModelProperty(String key, Object val) {
		if (properties[0].equals(key)) {
			if (val == null || "".equals(val)) {
				return "保存路径无效";
			}
		}
		return null;
	}

	@Override
	protected Composite createPageControl(Composite control) {
		Composite c2 = new Composite(control, SWT.NONE);
		c2.setLayoutData(new GridData(GridData.FILL_BOTH));
		c2.setLayout(GridLayoutUtil.COLUMN_LAYOUT_1);
		new FSDContentControl(this.getModel()) {
			protected void clickAddButton(Shell shell, final TableViewer tv) {
				FunctionSelectionDialog fsd = new FunctionSelectionDialog();
				String path = fsd.openUIPSelectionDialog("选择UIP", shell,
						getProjectNode());
				if (path != null && path.length() > 0) {
					getTableInput().add(path);
				}
				tv.setInput(getTableInput());
			}
		}.createFSDContentControl(c2);
		return c2;
	}

	protected Node getProjectNode() {
		return projectNode;
	}
	protected void saveTOMap(CompositeMap map) {
		new FSDContentControl(this.getModel()).saveToMap(map);
	}
}
