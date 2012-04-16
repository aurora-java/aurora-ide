package aurora.ide.meta.gef.editors.wizard;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import aurora.ide.builder.ResourceUtil;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.editors.property.MutilInputResourceSelector;
import aurora.ide.meta.gef.editors.template.Component;
import aurora.ide.meta.gef.editors.template.TabRef;
import aurora.ide.meta.gef.editors.template.Template;
import aurora.ide.meta.gef.editors.wizard.dialog.StyleSettingDialog;
import aurora.ide.meta.gef.i18n.Messages;
import aurora.ide.meta.project.AuroraMetaProject;

public class SetLinkOrRefWizardPage extends WizardPage {

	private Composite composite;

	public SetLinkOrRefWizardPage() {
		super("aurora.wizard.setting.Page"); //$NON-NLS-1$
		setTitle(Messages.SettingWizardPage_Title);
		setDescription("Setting");
		setPageComplete(true);
	}

	public void createControl(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		setControl(composite);
	}

	public void createCustom(Template template) {
		for (Control c : composite.getChildren()) {
			if (!c.isDisposed()) {
				c.dispose();
			}
		}

		// if (template.getLink().size() > 0) {
		// Group gl = new Group(composite, SWT.None);
		// gl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// gl.setText("Add Link");
		// gl.setLayout(new GridLayout(4, false));
		// for (Component cp : template.getLink()) {
		// if ("grid".equals(cp.getComponentType())) {
		// // Label lbl = new Label(gl, SWT.None);
		// // lbl.setText("Grid");
		// //
		// //
		// //
		// // Button btn=new Button(gl,SWT.None);
		// // btn.setText("Add");
		// //
		// // btn.addSelectionListener()
		//
		// // Label lbl = new Label(gl, SWT.None);
		// // lbl.setText("显示文本：");
		// // Text txtShow = new Text(gl, SWT.BORDER);
		// // GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		// // gd.horizontalSpan = 3;
		// // txtShow.setLayoutData(gd);
		// //
		// // lbl = new Label(gl, SWT.None);
		// // lbl.setText("目标：");
		// // Text txtTarget = new Text(gl, SWT.BORDER);
		// // txtTarget.setLayoutData(new
		// // GridData(GridData.FILL_HORIZONTAL));
		// // Button btnTarget = new Button(gl, SWT.None);
		// // btnTarget.setText("选择文件");
		// // Button btnParam = new Button(gl, SWT.None);
		// // btnParam.setText("添加参数");
		// }
		// }
		// for (Component cp : template.getLink()) {
		// if ("button".equals(cp.getComponentType())) {
		// Label lbl = new Label(gl, SWT.None);
		// lbl.setText("Button Open:");
		// Text txt = new Text(gl, SWT.BORDER);
		// txt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		// Button btnSelect = new Button(gl, SWT.None);
		// btnSelect.setText("选择文件");
		// // Button btnParam = new Button(gl, SWT.None);
		// // btnParam.setText("添加参数");
		// // btnParam.setEnabled(false);
		//
		// btnSelect.addSelectionListener(new TabRefSelect(txt, null, cp));
		//
		// // btnParam.addSelectionListener(new TabRefParamSelect(cp));
		// }
		// }
		// }

		if (template.getRef().size() > 0) {
			Group gr = new Group(composite, SWT.None);
			gr.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			gr.setText("Set tab Ref");
			gr.setLayout(new GridLayout(4, false));
			for (Component cp : template.getRef()) {
				createRefField((TabRef) cp, gr);
			}
		}
		composite.layout();
	}

	private void createRefField(TabRef cp, Group gr) {
		Label lbl = new Label(gr, SWT.None);
		lbl.setText("Select:");
		Text txt = new Text(gr, SWT.BORDER);
		txt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Button btnSelect = new Button(gr, SWT.None);
		btnSelect.setText("选择文件");

		Button btnParam = new Button(gr, SWT.None);
		btnParam.setText("添加参数");
		btnParam.setEnabled(false);

		btnSelect.addSelectionListener(new TabRefSelect(txt, btnParam, cp));

		btnParam.addSelectionListener(new TabRefParamSelect(cp));
	}

	private IProject getMetaProject() {
		for (IWizardPage page = this; page.getPreviousPage() != null; page = page.getPreviousPage()) {
			if (page instanceof NewWizardPage) {
				return ((NewWizardPage) page).getMetaProject();
			}
		}
		return null;
	}

	private IProject getAuroraProject() {
		try {
			return new AuroraMetaProject(getMetaProject()).getAuroraProject();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Object fileSelect(IContainer[] containers, String[] extFilter) {
		MutilInputResourceSelector fss = new MutilInputResourceSelector(getShell());
		fss.setExtFilter(extFilter);
		fss.setInputs(containers);
		Object obj = fss.getSelection();
		return obj;
	}

	class TabRefSelect extends SelectionAdapter {
		private Text txt;
		private Component cp;
		private Button btn;

		public TabRefSelect(Text txt, Button btn, Component cp) {
			this.txt = txt;
			this.cp = cp;
			this.btn = btn;
		}

		public void widgetSelected(SelectionEvent e) {
			String webHome = ResourceUtil.getWebHome(getAuroraProject());
			IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(webHome);
			IContainer uipFolder = getMetaProject().getFolder("ui_prototype");
			Object obj = fileSelect(new IContainer[] { (IContainer) res, uipFolder }, new String[] { "screen", "uip" });
			if (!(obj instanceof IFile)) {
				txt.setText("");
				cp.setUrl("");
				btn.setEnabled(false);
			} else {
				String path = ((IFile) obj).getFullPath().toString();
				txt.setText(path);
				if (path.endsWith("uip")) {
					path = path.substring(path.indexOf("ui_prototype") + "ui_prototype".length());
				} else if (path.endsWith("screen")) {
					path = path.substring(path.indexOf(webHome) + webHome.length());
				}
				cp.setUrl(path);
				btn.setEnabled(true);
			}
		}
	}

	class TabRefParamSelect extends SelectionAdapter {
		private Component cp;

		public TabRefParamSelect(Component cp) {
			this.cp = cp;
		}

		public void widgetSelected(SelectionEvent e) {
			StyleSettingDialog dialog = new StyleSettingDialog(getShell(), cp.getParas());
			if (dialog.open() == Dialog.OK) {
				cp.setParas(dialog.getResult());
			}
		}
	}
}
