package aurora.ide.prototype.consultant.view.wizard;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Composite;

import aurora.ide.prototype.consultant.view.util.ResourceUtil;
import aurora.ide.swt.util.GridLayoutUtil;
import aurora.ide.swt.util.TextField;
import aurora.ide.swt.util.UWizardPage;
import aurora.ide.swt.util.WidgetFactory;

public class CreateModulePage extends UWizardPage {

	public static final String[] properties = new String[] { "pj_name",
			"module_name" };
	private File parent;

	protected CreateModulePage(String pageName, File parent) {
		super(pageName);
		this.setTitle("Aurora Quick UI");
		this.setMessage("新建模块");
		this.parent = parent;
	}

	private TextField createInputField(Composite parent, String label) {
		TextField createTextField = WidgetFactory
				.createTextField(parent, label);
		return createTextField;
	}

	@Override
	protected String[] getModelPropertyKeys() {
		return properties;
	}

	@Override
	protected String verifyModelProperty(String key, Object val) {
		if (properties[1].equals(key)) {
			if (val == null || "".equals(val)) {
				return "模块名无效";
			}

			String n = "" + val;
			IPath p = new Path(n);
			if (p.segmentCount() != 1 || p.hasTrailingSeparator()) {
				return "模块名无效";
			}
			File m = new File(parent, n);
			if (m.exists()) {
				return "模块已存在";
			}
		}
		return null;
	}

	@Override
	protected void createPageControl(Composite parent) {
		parent.setLayout(GridLayoutUtil.COLUMN_LAYOUT_2);
		TextField pjField = createInputField(parent, "项目名");
		pjField.getText().setEnabled(false);
		File project = ResourceUtil.getProject(this.parent);
		if (project != null) {
			pjField.setText(ResourceUtil.getFullProjectRelativePath(project,
					this.parent).toString());
		} else {
			pjField.setText(this.parent.getName());
		}
		TextField mf = createInputField(parent, "模块名");
		mf.addModifyListener(new TextModifyListener(properties[1], mf.getText()));
	}

}
