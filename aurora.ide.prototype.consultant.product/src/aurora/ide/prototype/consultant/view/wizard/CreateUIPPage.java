package aurora.ide.prototype.consultant.view.wizard;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import aurora.ide.prototype.consultant.view.util.ResourceUtil;
import aurora.ide.swt.util.GridLayoutUtil;
import aurora.ide.swt.util.TextField;
import aurora.ide.swt.util.UWizardPage;
import aurora.ide.swt.util.WidgetFactory;

public class CreateUIPPage extends UWizardPage {

	private File parent;
	public static String[] properties = new String[] { "uip_name" }; //$NON-NLS-1$

	protected CreateUIPPage(String pageName, File parent) {
		super(pageName);
		this.parent = parent;
		this.setTitle("Aurora Quick UI"); //$NON-NLS-1$
		this.setMessage(Messages.CreateUIPPage_2);
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
		if (properties[0].equals(key)) {
			if (val == null || "".equals(val)) { //$NON-NLS-1$
				return Messages.CreateUIPPage_4;
			}
			String n = "" + val; //$NON-NLS-1$
			IPath p = new Path(n);
			if (p.segmentCount() != 1 || p.hasTrailingSeparator()) {
				return Messages.CreateUIPPage_6;
			}
			File m = new File(parent, n + ".uip"); //$NON-NLS-1$
			if (m.exists()) {
				return Messages.CreateUIPPage_8;
			}

		}
		return null;
	}

	@Override
	protected Composite createPageControl(Composite control) {
		Composite parent = new Composite(control, SWT.NONE);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		parent.setLayout(GridLayoutUtil.COLUMN_LAYOUT_2);

		TextField pjField = createInputField(parent, Messages.CreateUIPPage_9);
		pjField.getText().setEnabled(false);
		File project = ResourceUtil.getProject(this.parent);
		if (project != null) {
			pjField.setText(ResourceUtil.getFullProjectRelativePath(project,
					this.parent).toString());
		} else {
			pjField.setText(this.parent.getName());
		}

		TextField ff = createInputField(parent, Messages.CreateUIPPage_10);
		ff.addModifyListener(new TextModifyListener(properties[0], ff.getText()));
		return parent;
	}

}
