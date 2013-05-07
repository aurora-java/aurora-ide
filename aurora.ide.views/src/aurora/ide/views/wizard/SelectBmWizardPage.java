package aurora.ide.views.wizard;

import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import aurora.ide.builder.ResourceUtil;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.meta.gef.editors.template.BMReference;
import aurora.ide.meta.gef.editors.template.Template;
import aurora.ide.meta.gef.editors.template.handle.TemplateConfig;
import aurora.ide.meta.gef.editors.template.handle.TemplateFactory;
import aurora.ide.meta.gef.editors.template.handle.TemplateHandle;
import aurora.ide.meta.gef.editors.template.handle.TemplateHelper;
import aurora.ide.views.dialog.ResourceSelector;
import aurora.ide.views.wizard.NewScreenWizard.UserInput;
import aurora.plugin.source.gen.screen.model.ScreenBody;

public class SelectBmWizardPage extends WizardPage {
	private UserInput us = null;
	private Composite container;
	private Template tpl = null;
	BmPathValidator validaor = new BmPathValidator();
	HashMap<Object, Boolean> finishFlag = new HashMap<Object, Boolean>();
	ScreenBody view = null;

	private TemplateHelper helper=new TemplateHelper();
	private TemplateConfig config;
	
	/**
	 * Create the wizard.
	 */
	public SelectBmWizardPage() {
		super("wizardPage"); //$NON-NLS-1$
		setTitle(Messages.SelectBmWizardPage_1);
		setDescription(Messages.SelectBmWizardPage_2);
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
		container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(1, true));
		setControl(container);
	}

	public void setVisible(boolean v) {
		if (tpl != us.template) {
			view = helper.createView(us.template);
			config=helper.getConfig();
			finishFlag.clear();
			dynamicCreate();
		}
		super.setVisible(v);
	}

	protected void dynamicCreate() {
		tpl = us.template;
		for (Control c : container.getChildren()) {
			if (!c.isDisposed())
				c.dispose();
		}
		List<BMReference> list = us.template.getBms();
		if (list != null && list.size() > 0) {
			createGroup(container, "Model", list); //$NON-NLS-1$
			setPageComplete(false);
		}

		list = us.template.getLinkBms();
		if (list != null && list.size() > 0) {
			createGroup(container, "Init-Model(optional)", list); //$NON-NLS-1$
		}
		checkFinish();
		container.layout();
	}

	private void createGroup(Composite parent, String title,
			List<BMReference> content) {
		Group group = new Group(container, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setText(Messages.SelectBmWizardPage_5);
		group.setLayout(new GridLayout(3, false));
		for (BMReference bm : content) {
			createInputField(group, bm);
		}
	}

	private void createInputField(Composite parent, BMReference bm) {
		Label l = new Label(parent, SWT.NONE);
		l.setText(bm.getName());
		Text t = new Text(parent, SWT.BORDER);
		t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		t.setData("bm", bm); //$NON-NLS-1$
		t.setText(getModelPath(bm));
		finishFlag.put(t, validatePath(t.getText()) == null);
		t.addModifyListener(validaor);
		Button btn = new Button(parent, SWT.NONE);
		btn.addSelectionListener(new ButtonSelectionListener(t, bm));
		btn.setText(Messages.SelectBmWizardPage_7);
	}

	private String getModelPath(BMReference bm) {
		IFile f = bm.getModel();
		if (f != null)
			return f.getFullPath().toString();
		return ""; //$NON-NLS-1$
	}

	void checkFinish() {
		boolean res = true;
		for (Object o : finishFlag.keySet()) {
			res = finishFlag.get(o);
			if (!res)
				break;
		}
		if (res) {
			TemplateHandle handle = TemplateFactory.getTemplateHandle(view
					.getTemplateType(),config);
			if (handle != null) {
				handle.fill(view);
			}
		}
		setPageComplete(res);
	}

	public ScreenBody getViewDiagram() {
		return view;
	}

	private String validatePath(String path) {
		IResource r = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
		String msg = null;
		if (r == null) {
			msg = Messages.SelectBmWizardPage_9;
		} else if (!(r instanceof IFile)
				|| !AuroraConstant.BMFileExtension.equals(r.getFileExtension())) {
			msg = Messages.SelectBmWizardPage_10;
		}
		return msg;
	}

	private class BmPathValidator implements ModifyListener {

		@Override
		public void modifyText(ModifyEvent e) {
			Text t = (Text) e.getSource();
			String path = t.getText();
			IResource r = ResourcesPlugin.getWorkspace().getRoot()
					.findMember(path);
			String msg = validatePath(path);
			((BMReference) t.getData("bm")).setModel((IFile) r); //$NON-NLS-1$
			t.setToolTipText(msg);
			setErrorMessage(msg);
			t.setBackground(msg == null ? null : NewScreenWizard.WRONG_COLOR);
			finishFlag.put(t, msg == null);
			checkFinish();
		}
	}

	private class ButtonSelectionListener implements SelectionListener {
		private BMReference bm;
		private Text text;

		public ButtonSelectionListener(Text text, BMReference bm) {
			this.text = text;
			this.bm = bm;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			ResourceSelector rs = new ResourceSelector(getShell());
			rs.setExtFilter(new String[] { "bm" }); //$NON-NLS-1$
			IResource res = ResourcesPlugin.getWorkspace().getRoot()
					.findMember(us.dir);
			if (!(res instanceof IContainer)) {
				// this will nearly never happen in fact!
				rs.setInput(ResourcesPlugin.getWorkspace().getRoot());
			} else {
				IProject proj = res.getProject();
				if (ResourceUtil.isAuroraProject(proj)) {
					// this will happen almost every time.
					rs.setInput(ResourceUtil.getBMHomeFolder(proj));
				} else
					rs.setInput(proj);
			}
			IResource sel = rs.getSelection();
			if (sel instanceof IFile) {
				bm.setModel((IFile) sel);
				text.setText(sel.getFullPath().toString());
			}
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}

	}
}
