package aurora.ide.meta.gef.editors.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import aurora.ide.AuroraPlugin;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.editors.composite.IPathChangeListener;
import aurora.ide.meta.gef.editors.composite.ScreenUIPBrowseButton;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.models.link.TabRef;
import aurora.ide.meta.gef.editors.property.Messages;
import aurora.ide.meta.gef.editors.wizard.dialog.ParameterComposite;
import aurora.ide.meta.project.AuroraMetaProject;

public class CreateEditTabRefWizardPage extends WizardPage {

	private Text urlField;

	private String url;

	private ParameterComposite pc;

	private TabRef _ref;

	protected CreateEditTabRefWizardPage() {
		super("Create & Edit Ref");
		this.setTitle("Ref");
		this.setDescription("Create & Edit Ref.");
	}

	public void init(TabRef link) {
		if (link != null) {
			url = link.getOpenPath();
			_ref = link;
		}
	}

	public TabRef getLink() {
		TabRef l = new TabRef();
		l.setOpenPath(url);
		l.getParameters().addAll(pc.getParameters());

		return l;
	}

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

		ScreenUIPBrowseButton br = new ScreenUIPBrowseButton(root, SWT.FLAT);

		br.setText("Browse..");
		IProject auroraProject = getAuroraProject();
		br.setAuroraProject(auroraProject);
		br.addListener(new IPathChangeListener() {
			public void pathChanged(String openPath) {
				if (openPath != null) {
					urlField.setText(openPath);
				}
			}
		});

		urlField.setText(url == null ? "" : url);

		createParaTable(root);
		this.setControl(root);
	}

	private void createParaTable(Composite composite_right) {

		AuroraComponent comp = (AuroraComponent) _ref.getTabItem();
		ViewDiagram root = null;
		while (comp != null) {
			if (comp instanceof ViewDiagram) {
				root = (ViewDiagram) comp;
				break;
			}
			comp = comp.getParent();
		}
		if (root == null) {
			setErrorMessage(Messages.ButtonClickEditDialog_9);
			setPageComplete(false);
			return;
		}
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 3;
		pc = new ParameterComposite(root, composite_right, SWT.NONE, comp);
		pc.setLayoutData(data);
		pc.setParameters(_ref.getParameters());
	}

	// public IResource getWebHome() {
	// IFile activeIFile = AuroraPlugin.getActiveIFile();
	// IProject proj = activeIFile.getProject();
	// AuroraMetaProject mProj = new AuroraMetaProject(proj);
	//
	// String webHome = "";
	// try {
	// webHome = ResourceUtil.getWebHome(mProj.getAuroraProject());
	// } catch (ResourceNotFoundException e1) {
	// e1.printStackTrace();
	// }
	// IResource res = ResourcesPlugin.getWorkspace().getRoot()
	// .findMember(webHome);
	// return res;
	// }

	private IProject getAuroraProject() {

		IFile activeIFile = AuroraPlugin.getActiveIFile();
		IProject proj = activeIFile.getProject();
		AuroraMetaProject mProj = new AuroraMetaProject(proj);

		try {
			return mProj.getAuroraProject();
		} catch (ResourceNotFoundException e1) {
			e1.printStackTrace();
		}
		return null;
	}
	//
	// public IContainer getUIPFolder() {
	// IFile activeIFile = AuroraPlugin.getActiveIFile();
	// IProject proj = activeIFile.getProject();
	// AuroraMetaProject mProj = new AuroraMetaProject(proj);
	// try {
	// return mProj.getScreenFolder();
	// } catch (ResourceNotFoundException e) {
	// e.printStackTrace();
	// }
	// return mProj.getProject();
	// }
	//
	// public IFile openResourceSelector(Shell shell, String[] exts) {
	// MutilInputResourceSelector fss = new MutilInputResourceSelector(shell);
	// IResource res = getWebHome();
	// fss.setExtFilter(exts);
	// IContainer uipFolder = getUIPFolder();
	// fss.setInputs(new IContainer[] { (IContainer) res, uipFolder });
	// Object obj = fss.getSelection();
	// if (!(obj instanceof IFile)) {
	//
	// return null;
	// }
	// return (IFile) obj;
	// }
	//
	// public void buttonClick(Shell shell, Text feedback, String[] exts) {
	// IFile file = openResourceSelector(shell, exts);
	// if (file == null)
	// return;
	// IPath path = file.getFullPath();
	// IContainer web = Util.findWebInf(file).getParent();
	// path = path.makeRelativeTo(web.getFullPath());
	// feedback.setText(Util.getPKG(path));
	// }

}
