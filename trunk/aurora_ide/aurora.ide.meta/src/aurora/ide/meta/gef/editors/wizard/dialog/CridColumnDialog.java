package aurora.ide.meta.gef.editors.wizard.dialog;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import aurora.ide.builder.ResourceUtil;
import aurora.ide.meta.gef.editors.wizard.SetLinkOrRefWizardPage;
import aurora.ide.meta.gef.i18n.Messages;
import aurora.plugin.source.gen.screen.model.GridColumn;
import aurora.plugin.source.gen.screen.model.Renderer;

public class CridColumnDialog extends Dialog {

	private GridColumn gridColumn;
	private Renderer renderer;
	private SetLinkOrRefWizardPage page;

	public CridColumnDialog(SetLinkOrRefWizardPage page) {
		super(page.getShell());
		this.page = page;
		renderer = new Renderer();
		gridColumn = new GridColumn();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		Composite composite = new Composite(container, SWT.None);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label lbl = new Label(composite, SWT.None);
		lbl.setText(Messages.CridColumnDialog_Prompt);
		Text txtPrompt = new Text(composite, SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		txtPrompt.setLayoutData(gd);
		txtPrompt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				gridColumn.setPrompt(getTextString((Text) e.getSource()));
				setOKButtonEnabled();
			}
		});

		lbl = new Label(composite, SWT.None);
		lbl.setText(Messages.CridColumnDialog_Show);
		Text txtTitle = new Text(composite, SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		txtTitle.setLayoutData(gd);
		txtTitle.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				renderer.setLabelText(getTextString((Text) e.getSource()));
				setOKButtonEnabled();
			}
		});

		lbl = new Label(composite, SWT.None);
		lbl.setText(Messages.CridColumnDialog_Target);
		final Text txtUrl = new Text(composite, SWT.BORDER);
		txtUrl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txtUrl.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String webHome = ResourceUtil.getWebHome(page.getAuroraProject());
				String path = getTextString((Text) e.getSource());
				if (path.toLowerCase().endsWith("uip")) { //$NON-NLS-1$
					path = path.substring(path.indexOf("ui_prototype") + "ui_prototype".length()); //$NON-NLS-1$ //$NON-NLS-2$
				} else if (path.endsWith("screen")) { //$NON-NLS-1$
					path = path.substring(path.indexOf(webHome) + webHome.length());
				}
				renderer.setOpenPath(path);
				setOKButtonEnabled();
			}
		});

		Button btnOpen = new Button(composite, SWT.NONE);
		btnOpen.setText(Messages.CridColumnDialog_Select);
		btnOpen.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String webHome = ResourceUtil.getWebHome(page.getAuroraProject());
				IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(webHome);
				IContainer uipFolder = page.getMetaProject().getFolder("ui_prototype"); //$NON-NLS-1$
				Object obj = page.fileSelect(new IContainer[] { (IContainer) res, uipFolder }, new String[] { "screen", "uip" }); //$NON-NLS-1$ //$NON-NLS-2$
				if (obj instanceof IFile) {
					String path = ((IFile) obj).getFullPath().toString();
					txtUrl.setText(path);
				}
			}
		});

		Group group=new Group(container,SWT.NONE);
		group.setText(Messages.CridColumnDialog_ParSetting);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		PComposite pComposite = new PComposite(group, renderer.getParameters(), SWT.None);
		pComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		return container;
	}

	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		getButton(OK).setEnabled(false);
		return control;
	}

	private String getTextString(Text txt) {
		String s = txt.getText();
		if (s == null || "".equals(s.trim())) { //$NON-NLS-1$
			getButton(OK).setEnabled(false);
			return ""; //$NON-NLS-1$
		}
		return s;
	}

	private void setOKButtonEnabled() {
		if ("".equals(renderer.getLabelText()) || renderer.getLabelText() == null) { //$NON-NLS-1$
			getButton(OK).setEnabled(false);
		} else if ("".equals(renderer.getOpenPath()) || renderer.getOpenPath() == null) { //$NON-NLS-1$
			getButton(OK).setEnabled(false);
		} else if ("".equals(gridColumn.getPrompt()) || gridColumn.getPrompt() == null) { //$NON-NLS-1$
			getButton(OK).setEnabled(false);
		} else {
			getButton(OK).setEnabled(true);
		}
	}

	
	
	@Override
	protected void okPressed() {
		renderer.setRendererType(Renderer.PAGE_REDIRECT);
		gridColumn.setRenderer(renderer);
		super.okPressed();
	}

	@Override
	protected Point getInitialSize() {
		return new Point(400, 350);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Add GridColumn Link"); //$NON-NLS-1$
	}

	public GridColumn getGridColumn() {
		return gridColumn;
	}

	public void setGridColumn(GridColumn gridColumn) {
		this.gridColumn = gridColumn;
	}

	public Renderer getRenderer() {
		return renderer;
	}
}
