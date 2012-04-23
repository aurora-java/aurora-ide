package aurora.ide.meta.gef.designer.wizard;

import java.util.StringTokenizer;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import aurora.ide.meta.gef.designer.IDesignerConst;
import aurora.ide.meta.gef.editors.property.ResourceSelector;
import aurora.ide.meta.project.AuroraMetaProject;
import aurora.ide.meta.project.AuroraMetaProjectNature;

public class BaseInfoWizardPage extends WizardPage {

	private static String fileNamePattern = "[\\w\\d_]+(."
			+ IDesignerConst.EXTENSION + ")?";
	private IResource resource;
	private ValidateDirListener dirValidator = new ValidateDirListener();
	private ValidateFileNameListener fnValidator = new ValidateFileNameListener();
	private Text text_dir;
	private Text text_fileName;
	private Text text;

	/**
	 * Create the wizard.
	 */
	public BaseInfoWizardPage() {
		super("BaseInfoWizardPage");
		setTitle("Model Prototype 文件向导");
		setDescription("Wizard Page description");
	}

	void setCurrentSelection(IResource resource) {
		this.resource = resource;
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
		label.setText("目录:");

		text_dir = new Text(container, SWT.BORDER);
		text_dir.addModifyListener(dirValidator);
		text_dir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		Button button = new Button(container, SWT.NONE);
		button.setText("浏览...");
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				// TODO
				selectDir();
			}
		});

		Label label_1 = new Label(container, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		label_1.setText("文件名:");

		text_fileName = new Text(container, SWT.BORDER);
		text_fileName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		text_fileName.addModifyListener(fnValidator);
		text_dir.setText(getDefaultDir());
		text_fileName.setText("");
		if (dirValidator.getMessage() == null)
			text_fileName.forceFocus();
		new Label(container, SWT.NONE);

		Group group = new Group(container, SWT.NONE);
		group.setText("预输入");
		group.setLayout(new GridLayout(4, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 3, 1));
		new Label(group, SWT.NONE);

		text = new Text(group, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.CANCEL | SWT.MULTI);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		Button btnNewButton = new Button(group, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				format();
			}
		});
		btnNewButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		btnNewButton.setText("Format");
	}

	private void selectDir() {
		ResourceSelector rs = new ResourceSelector(getShell());
		IContainer root = ResourcesPlugin.getWorkspace().getRoot();
		rs.setDirOnly(true);
		rs.setInput(root);
		IResource res = rs.getSelection();
		if (res != null) {
			text_dir.setText(res.getFullPath().toString());
		}
	}

	private void format() {
		String str = text.getText();
		StringBuilder sb = new StringBuilder(str.length());
		StringTokenizer st = new StringTokenizer(str, " ,;\t\n\r\f，。；　");
		while (st.hasMoreElements())
			sb.append(st.nextElement() + "\n");
		text.setText(sb.toString());
	}

	private String getDefaultDir() {
		if (resource == null)
			return "";
		IProject proj = resource.getProject();
		try {
			if (!AuroraMetaProjectNature.hasAuroraNature(proj))
				return "";
			AuroraMetaProject mProj = new AuroraMetaProject(proj);
			IFolder folder = mProj.getModelFolder();
			IPath path1 = folder.getFullPath();
			IPath path2 = resource.getFullPath();
			if (resource instanceof IFile)
				path2 = resource.getParent().getFullPath();
			if (path1.isPrefixOf(path2))
				return path2.toString();
			return path1.toString();
		} catch (Exception e) {
		}

		return "";
	}

	private class ValidateDirListener implements ModifyListener {

		public void modifyText(ModifyEvent e) {
			String msg = getMessage();
			if (msg != null) {
				setErrorMessage(msg);
				setPageComplete(false);
			} else {
				setErrorMessage(null);
				setPageComplete(true);
				msg = fnValidator.getMessage();
				if (msg != null) {
					setErrorMessage(msg);
					setPageComplete(false);
				}
			}
		}

		public String getMessage() {
			String t = text_dir.getText();
			if (t == null || t.trim().length() == 0) {
				return "please select a valid directory.";
			}
			IResource res = ResourcesPlugin.getWorkspace().getRoot()
					.findMember(t);
			if (res == null || !(res instanceof IFolder)) {
				return "please select a valid directory.";
			}
			if (!isInAuroraMetaProject(res)) {
				return "the dir must in a aurora meta project.";
			}
			return null;
		}
	}

	private class ValidateFileNameListener implements ModifyListener {

		public void modifyText(ModifyEvent e) {
			String msg = getMessage();
			if (msg != null) {
				setErrorMessage(msg);
				setPageComplete(false);
			} else {
				setErrorMessage(null);
				setPageComplete(true);
				msg = dirValidator.getMessage();
				if (msg != null) {
					setErrorMessage(msg);
					setPageComplete(false);
				}
			}
		}

		public String getMessage() {
			String t = text_fileName.getText();
			if (t == null || !t.toLowerCase().matches(fileNamePattern)) {
				return "please enter a valid file name";
			}
			t = getRealFileName(t);
			IResource res = ResourcesPlugin.getWorkspace().getRoot()
					.findMember(text_dir.getText() + "/" + t);
			if (res != null) {
				String type = res.getClass().getSimpleName().toLowerCase();
				return String.format("%s '%s' already exists.", type,
						res.getName());
			}
			return null;
		}
	}

	private String getRealFileName(String fn) {
		int idx = fn.indexOf('.');
		if (idx == -1)
			fn += "." + IDesignerConst.EXTENSION;
		return fn;
	}

	private boolean isInAuroraMetaProject(IResource res) {
		if (res == null)
			return false;
		IProject proj = res.getProject();
		try {
			return AuroraMetaProjectNature.hasAuroraNature(proj);
		} catch (Exception e) {
		}
		return false;
	}

	public String getFileFullPath() {
		String path = text_dir.getText();
		String fn = text_fileName.getText();
		fn = getRealFileName(fn);
		return path.charAt(path.length() - 1) == '/' ? path + fn : path + "/"
				+ fn;
	}

	public String[] getPreInput() {
		format();
		String input = text.getText();
		StringTokenizer st = new StringTokenizer(input);
		String[] ss = new String[st.countTokens()];
		for (int i = 0; st.hasMoreElements(); i++)
			ss[i] = st.nextToken();
		return ss;
	}
}
