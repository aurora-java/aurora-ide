package aurora.ide.meta.gef.editors.wizard;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import aurora.ide.AuroraPlugin;
import aurora.ide.builder.ResourceUtil;
import aurora.ide.meta.exception.ResourceNotFoundException;
//import aurora.ide.meta.gef.editors.models.link.Link;
import aurora.ide.meta.gef.editors.property.ResourceSelector;
import aurora.ide.meta.project.AuroraMetaProject;
import aurora.ide.search.core.Util;

public class CreateEditLinkWizardPage extends WizardPage {

	private Text urlField;
	private Text modelField;
	private Text modelActionField;

	private String url;
	private String model;
	private String modelAction;

	protected CreateEditLinkWizardPage() {
		super("Create & Edit Link");
		this.setTitle("Link");
		this.setDescription("Create & Edit Link.");
	}

//	public void init(Link link) {
//		if (link != null) {
//			url = link.getUrl();
//			model = link.getModel();
//			modelAction = link.getModelaction();
//		}
//	}

//	public Link getLink() {
//		Link l = new Link();
//		l.setUrl(url);
//		l.setModel(model);
//		l.setModelaction(modelAction);
//		return l;
//	}

	public void createControl(final Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout();
		gl.numColumns = 3;
		root.setLayout(gl);

		Label fileName = new Label(root, SWT.NONE);
		fileName.setText("URL : ");

		urlField = new Text(root, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		urlField.setLayoutData(data);
		urlField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				url = urlField.getText();
			}
		});

		Button br = new Button(root, SWT.NONE);
		br.setText("Browse..");
		br.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				Shell shell = parent.getShell();
				buttonClick(shell, urlField, new String[] { "screen" });
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Label modelName = new Label(root, SWT.NONE);
		modelName.setText("Model : ");

		modelField = new Text(root, SWT.BORDER);
		modelField.setLayoutData(data);
		modelField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				model = modelField.getText();
			}
		});

		br = new Button(root, SWT.NONE);
		br.setText("Browse..");
		br.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {

				Shell shell = parent.getShell();
				buttonClick(shell, modelField, new String[] { "bm" });
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Label modelActionLabel = new Label(root, SWT.NONE);
		modelActionLabel.setText("Model Action : ");

		modelActionField = new Text(root, SWT.BORDER);
		modelActionField.setLayoutData(data);
		modelActionField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				modelAction = modelActionField.getText();
			}
		});

		urlField.setText(url == null ? "" : url);
		modelField.setText(model == null ? "" : model);
		modelActionField.setText(modelAction == null ? "" : modelAction);

		this.setControl(root);
	}

	public IResource getWebHome() {
		IFile activeIFile = AuroraPlugin.getActiveIFile();
		IProject proj = activeIFile.getProject();
		AuroraMetaProject mProj = new AuroraMetaProject(proj);

		String webHome = "";
		try {
			webHome = ResourceUtil.getWebHome(mProj.getAuroraProject());
		} catch (ResourceNotFoundException e1) {
			e1.printStackTrace();
		}
		IResource res = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(webHome);
		return res;
	}

	public IFile openResourceSelector(Shell shell, String[] exts) {
		ResourceSelector fss = new ResourceSelector(shell);
		IResource res = getWebHome();
		fss.setExtFilter(exts);
		fss.setInput((IContainer) res);
		Object obj = fss.getSelection();
		if (!(obj instanceof IFile)) {

			return null;
		}
		return (IFile) obj;
	}

	public void buttonClick(Shell shell, Text feedback, String[] exts) {
		IFile file = openResourceSelector(shell, exts);
		if (file == null)
			return;
		IPath path = file.getFullPath();
		IContainer web = Util.findWebInf(file).getParent();
		path = path.makeRelativeTo(web.getFullPath());
		feedback.setText(Util.getPKG(path));
	}

}
