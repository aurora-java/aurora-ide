package aurora.ide.views.wizard;

import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import aurora.ide.helpers.AuroraConstant;
import aurora.ide.meta.MetaPlugin;
import aurora.ide.meta.gef.editors.template.Template;
import aurora.ide.meta.gef.editors.template.handle.TemplateHelper;
import aurora.ide.meta.gef.editors.wizard.dialog.TComposite;
import aurora.ide.views.dialog.ResourceSelector;
import aurora.ide.views.wizard.NewScreenWizard.UserInput;

public class SelectTemplatelWizardPage extends WizardPage {
	private Text text_dir;
	private Text text_fileName;
	private Label lblDesc;
	private UserInput us = null;
	private Button chkUseModel;

	/**
	 * Create the wizard.
	 */
	public SelectTemplatelWizardPage() {
		super("wizardPage");
		setTitle("选择模版");
		setDescription("Wizard Page description");
		setPageComplete(false);
	}

	public void setUserInput(UserInput input) {
		us = input;
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(3, false));

		Label label = new Label(container, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		label.setText("目录：");

		text_dir = new Text(container, SWT.BORDER);
		text_dir.setText(us.dir);
		text_dir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		Button button = new Button(container, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ResourceSelector rs = new ResourceSelector(getShell());
				rs.setDirOnly(true);
				rs.setInput(ResourcesPlugin.getWorkspace().getRoot());
				IResource res = rs.getSelection();
				if (res == null)
					return;
				text_dir.setText(res.getFullPath().toString());
				text_fileName.forceFocus();
			}
		});
		button.setText("浏览...");

		Label label_1 = new Label(container, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		label_1.setText("文件名：");

		text_fileName = new Text(container, SWT.BORDER);
		text_fileName.setText(us.fileName);
		text_fileName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		new Label(container, SWT.NONE);
		IPath path = MetaPlugin.getDefault().getStateLocation()
				.append("template");
		createTemplate(container,
				TemplateHelper.getInstance().getTemplates(path));

		createBottomComposite(container);
		initValidator();
		if (text_dir.getText().length() > 0) {
			text_fileName.forceFocus();
		}
	}

	private void createTemplate(Composite composite,
			Map<String, java.util.List<Template>> tempMap) {
		TComposite tComposite = new TComposite(composite, SWT.BORDER, tempMap);
		tComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true,
				3, 1));
		us.template = tComposite.getSelection();
		tComposite.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TComposite t = (TComposite) e.getSource();
				us.template = t.getSelection();
				if (us.template != null)
					setTemplateDescription(us.template.getDescription());
			}
		});
		composite.layout(true);
	}

	private void createBottomComposite(Composite container) {
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.VERTICAL));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 3, 1));

		lblDesc = new Label(composite, SWT.NONE);

		new Label(composite, SWT.NONE);

		chkUseModel = new Button(composite, SWT.CHECK);
		chkUseModel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				us.noUseModel = chkUseModel.getSelection();
			}
		});
		chkUseModel.setSelection(us.noUseModel);
		chkUseModel.setText("不使用模板");
	}

	private void setTemplateDescription(String desc) {
		if (desc == null) {
			desc = "";
		}
		lblDesc.setText(desc);
		lblDesc.redraw();
	}

	private void initValidator() {
		ModifyListener ml = new ModifyListener() {
			private IWorkspace workspace = ResourcesPlugin.getWorkspace();
			private String[] msgs = { null, null };

			@Override
			public void modifyText(ModifyEvent e) {
				Text t = (Text) e.getSource();
				if (t == text_dir)
					validateDir();
				else
					validateFileName();
				if (msgs[0] == null)
					setErrorMessage(msgs[1]);
				else
					setErrorMessage(msgs[0]);
				setPageComplete(getErrorMessage() == null);
			}

			void validateDir() {
				String dir = text_dir.getText();
				us.dir = dir;
				IStatus status = workspace.validatePath(dir, IResource.FOLDER
						| IResource.PROJECT);
				msgs[0] = null;
				if (!status.isOK()) {
					msgs[0] = status.getMessage();
				} else {
					IResource res = workspace.getRoot().findMember(dir);
					if (!(res instanceof IContainer)) {
						msgs[0] = ("Path '" + dir + "' does not exists (or not a folder).");
					}
				}
				if (msgs[0] == null)
					validateFileName();
			}

			void validateFileName() {
				String fn = text_fileName.getText();
				us.fileName = fn;
				if (msgs[0] != null)
					return;
				if (!regExpCheck(fn))
					return;
				int idx = fn.indexOf('.');
				if (idx == -1)
					fn = fn + "." + AuroraConstant.ScreenFileExtension;
				IPath path = new Path(text_dir.getText()).append(fn);
				msgs[1] = null;
				IResource res = workspace.getRoot().findMember(path);
				if (res != null) {
					msgs[1] = (res.getClass().getSimpleName() + " '" + fn + "' already exists.");
				}
			}

			boolean regExpCheck(String fn) {
				if (!new Path("").isValidSegment(fn)) {
					msgs[1] = "'" + fn + "' is not a valid segment";
					return false;
				}
				boolean res = fn.matches("[\\w\\d_]+(."
						+ AuroraConstant.ScreenFileExtension + ")?");
				msgs[1] = "File name '" + fn + "' is not valid.";
				return res;
			}
		};
		text_dir.addModifyListener(ml);
		text_fileName.addModifyListener(ml);
	}
}
