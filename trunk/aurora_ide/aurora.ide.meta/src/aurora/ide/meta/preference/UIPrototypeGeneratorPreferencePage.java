package aurora.ide.meta.preference;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import aurora.ide.helpers.FileExplorer;
import aurora.ide.meta.MetaPlugin;
import aurora.ide.meta.gef.editors.source.gen.core.GeneratorManager;

public class UIPrototypeGeneratorPreferencePage extends PreferencePage
		implements IWorkbenchPreferencePage {

	private static final String KEY = "aurora.ide.meta.preference.UIPrototypeGeneratorPreferencePage.source.gen.config.path";
	private String value;
	private Text t1;

	public UIPrototypeGeneratorPreferencePage() {
	}

	public UIPrototypeGeneratorPreferencePage(String title) {
		super(title);
	}

	public UIPrototypeGeneratorPreferencePage(String title,
			ImageDescriptor image) {
		super(title, image);
	}

	public void init(IWorkbench workbench) {

	}

	@Override
	protected Control createContents(Composite parent) {
		final Composite root = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout();
		gl.numColumns = 4;
		root.setLayout(gl);
		Label l = new Label(root, SWT.NONE);
		l.setText("包路径: ");
		t1 = new Text(root, SWT.READ_ONLY);
		t1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		t1.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				value = t1.getText();
			}
		});
		t1.setText(getPath());
		Button b1 = new Button(root, SWT.NONE);
		b1.setText("更改");
		b1.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {

				DirectoryDialog directoryDialog = new DirectoryDialog(
						new Shell());
				String value = directoryDialog.open();
				if (value != null)
					t1.setText(value);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		Button b2 = new Button(root, SWT.NONE);
		b2.setText("打开");
		b2.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				FileExplorer.open(value);
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}

		});
		return root;
	}

	@Override
	protected void performDefaults() {
		t1.setText(getDefaultPath());
		super.performDefaults();
	}

	@Override
	public boolean performOk() {
		MetaPlugin.getDefault().getPreferenceStore().setValue(KEY, value);
		return super.performOk();
	}

	public static String getPath() {
		String string = MetaPlugin.getDefault().getPreferenceStore()
				.getString(KEY);
		if (string == null || "".equals(string)) {
			return getDefaultPath();
		} else {
			return string;
		}
	}

	private static String getDefaultPath() {
		return GeneratorManager.getDefaultSourceGenTemplatePath().toOSString();
	}

}
