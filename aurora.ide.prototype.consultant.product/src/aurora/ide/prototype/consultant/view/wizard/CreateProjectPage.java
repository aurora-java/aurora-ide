package aurora.ide.prototype.consultant.view.wizard;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import aurora.ide.prototype.consultant.product.fsd.FunctionDesc;
import aurora.ide.prototype.consultant.product.fsd.wizard.Messages;
import aurora.ide.swt.util.GridLayoutUtil;
import aurora.ide.swt.util.TextField;
import aurora.ide.swt.util.UWizardPage;
import aurora.ide.swt.util.WidgetFactory;
import aurora.ide.swt.util.PageModel;

public class CreateProjectPage extends UWizardPage {

	public static final String[] properties = new String[] { "pj_name", //$NON-NLS-1$
			"pj_path", FunctionDesc.c_manager, FunctionDesc.dept, //$NON-NLS-1$
			FunctionDesc.h_manager };
	private PageModel initModel;

	protected CreateProjectPage(String pageName, PageModel initModel) {
		super(pageName);
		this.initModel = initModel;
		this.setTitle("Aurora Quick UI"); //$NON-NLS-1$
		this.setMessage(Messages.CreateProjectPage_3);
	}

	private TextField createInputField(Composite parent, String label,
			String key) {
		TextField createTextField = WidgetFactory
				.createTextField(parent, label);
		createTextField.addModifyListener(new TextModifyListener(key,
				createTextField.getText()));
		createTextField.setText(initModel.getStringPropertyValue(key));
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
				return Messages.CreateProjectPage_5;
			}
		}
		if (properties[1].equals(key)) {
			String p = "" + val; //$NON-NLS-1$
			File path = new File(p);
			if (path.exists() == false || path.isDirectory() == false
					|| path.canWrite() == false) {
				return Messages.CreateProjectPage_7;
			}
			File pj = new File(path, this.getModel().getStringPropertyValue(
					properties[0]));
			if (pj.exists()) {
				return Messages.CreateProjectPage_8;
			}
		}
		return null;
	}

	@Override
	protected Composite createPageControl(Composite control) {
		Composite parent = new Composite(control, SWT.NONE);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		parent.setLayout(GridLayoutUtil.COLUMN_LAYOUT_1);
		GridLayout ly = new GridLayout(3, false);

		Composite functonComposite = new Composite(parent, SWT.NONE);
		functonComposite.setLayout(ly);
		functonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 2;
		final TextField pjField = WidgetFactory.createTextField(
				functonComposite, Messages.CreateProjectPage_9, layoutData);
		pjField.addModifyListener(new TextModifyListener(properties[0], pjField
				.getText()));
		pjField.setText(initModel.getStringPropertyValue(properties[0]));
		final TextField pathField = WidgetFactory.createTextButtonField(
				functonComposite, Messages.CreateProjectPage_10, Messages.ContentDescPage_1);
		pathField.addModifyListener(new TextModifyListener(properties[1],
				pathField.getText()));
		pathField.setText(initModel.getStringPropertyValue(properties[1]));
		pathField.addButtonClickListener(new SelectionAdapter() { 
			
			public void widgetSelected(SelectionEvent e) {
				String queryFile = queryFile();
				if(queryFile == null)
					return;
				else{
					pathField.setText(queryFile);
				}
			}
			private String queryFile() {

				DirectoryDialog directoryDialog = new DirectoryDialog(
						new Shell());
				String value = directoryDialog.open();
				return value;
			}
		});

		new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL)
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Group applyComposite = new Group(parent, SWT.NONE);
		applyComposite.setText(Messages.FunctionDescPage_8);
		applyComposite.setLayout(new GridLayout(2, false));
		applyComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createInputField(applyComposite, Messages.FunctionDescPage_9,
				properties[2]);
		createInputField(applyComposite, Messages.FunctionDescPage_10,
				properties[3]);
		createInputField(applyComposite, Messages.FunctionDescPage_11,
				properties[4]);
		
		return parent;
	}

}
