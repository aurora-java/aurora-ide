package aurora.ide.meta.gef.editors.wizard;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import aurora.ide.AuroraProjectNature;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.InitModel;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.ResultDataSet;
import aurora.ide.meta.gef.editors.models.TabItem;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.models.link.TabRef;
import aurora.ide.meta.gef.editors.template.BMReference;
import aurora.ide.meta.gef.editors.template.Template;
import aurora.ide.meta.gef.editors.template.parse.GefModelAssist;
import aurora.ide.meta.gef.editors.template.parse.TemplateHelper;
import aurora.ide.meta.gef.editors.wizard.dialog.SelectModelDialog;
import aurora.ide.meta.gef.i18n.Messages;
import aurora.ide.meta.project.AuroraMetaProject;
import aurora.ide.project.propertypage.ProjectPropertyPage;
import aurora.ide.search.core.Util;

public class SelectModelWizardPage extends WizardPage {

	// private Template template;
	private ViewDiagram viewDiagram;
	private Map<BMReference, AuroraComponent> modeRelated;
	private Map<BMReference, AuroraComponent> initModeRelated;

	private Composite composite;

	public SelectModelWizardPage() {
		super("aurora.wizard.select.Page"); //$NON-NLS-1$
		setTitle(Messages.SettingWizardPage_Title);
		setDescription(Messages.SettingWizardPage_Model_Bind);
	}

	public void createControl(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		setControl(composite);

	}

	private IPath getBMPath() {
		AuroraMetaProject metaPro = new AuroraMetaProject(getNewWizardPage().getMetaProject());
		try {
			if (metaPro == null || metaPro.getAuroraProject() == null) {
				return null;
			}
			IProject auroraPro = metaPro.getAuroraProject();
			if (auroraPro.hasNature(AuroraProjectNature.ID)) {
				return new Path(auroraPro.getPersistentProperty(ProjectPropertyPage.BMQN));
			}
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void createDynamicTextComponents(Template t) {
		this.viewDiagram = TemplateHelper.getInstance().createView(t);
		List<BMReference> bms = TemplateHelper.getInstance().getBms();
		List<BMReference> initBms = TemplateHelper.getInstance().getInitBms();
		modeRelated = TemplateHelper.getInstance().getModeRelated();
		initModeRelated = TemplateHelper.getInstance().getInitModeRelated();
		IPath bmPath = getBMPath();
		setPageComplete(false);
		for (Control c : composite.getChildren()) {
			if (!c.isDisposed()) {
				c.dispose();
			}
		}

		if (bms != null && bms.size() > 0) {
			Group compoModel = new Group(composite, SWT.NONE);
			compoModel.setLayout(new GridLayout(3, false));
			compoModel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			compoModel.setText("Model");
			for (BMReference bm : bms) {
				createTextField(compoModel, bm, bmPath);
			}
			compoModel.layout();
		}

		if (initBms != null && initBms.size() > 0) {
			Group compoInitModel = new Group(composite, SWT.NONE);
			compoInitModel.setLayout(new GridLayout(3, false));
			compoInitModel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			compoInitModel.setText("InitModel");
			for (BMReference bm : initBms) {
				createTextField(compoInitModel, bm, bmPath);
			}
			compoInitModel.layout();
		}
		composite.layout();
	}

	private void createTextField(Composite composite, final BMReference bm, final IPath bmPath) {
		Label lbl = new Label(composite, SWT.None);
		lbl.setText(bm.getName());
		final Text txt = new Text(composite, SWT.BORDER);
		txt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button btn = new Button(composite, SWT.None);
		btn.setText(Messages.SettingWizardPage_Select_model);
		if (bmPath == null) {
			btn.setEnabled(false);
		}

		txt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (null == txt.getText() || "".equals(txt.getText())) {
					setErrorMessage(null);
					setPageComplete(false);
				} else {
					fillBM(bm, txt);
				}
			}
		});

		btn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(bmPath);
				SelectModelDialog dialog = new SelectModelDialog(getShell(), folder);
				if ((dialog.open() == Dialog.OK) && (dialog.getResult() instanceof IFile)) {
					txt.setText(((IFile) dialog.getResult()).getFullPath().toString());
				}
			}
		});
	}

	private void fillBM(final BMReference bm, final Text txt) {
		IResource r = ResourcesPlugin.getWorkspace().getRoot().findMember(txt.getText());
		if (r == null || !r.exists()) {
			updateStatus("文件不存在");
			return;
		} else if (!(r instanceof IFile) || (!r.getFileExtension().equalsIgnoreCase("bm"))) {
			updateStatus("必须选择bm文件");
			return;
		}
		updateStatus(null);
		bm.setModel((IFile) r);
		if (modeRelated.get(bm) instanceof Container) {
			fillContainer((Container) modeRelated.get(bm), bm);
		} else if (initModeRelated.get(bm) instanceof TabItem) {
			fillInitModel((TabItem) initModeRelated.get(bm), bm);
		}
	}

	private void fillInitModel(TabItem ac, BMReference bm) {
		String s = getBmPath(bm.getModel());
		InitModel m = new InitModel();
		m.setPath(s);
		ac.getTabRef().setInitModel(m);
		viewDiagram.getInitModels().add(m);
		// initModels.add(m);
		// ac.getTabRef().setUrl("11");
		// ref.setUrl(((aurora.ide.meta.gef.editors.template.TabRef)
		// c).getUrl());
		// ref.addAllParameter(((aurora.ide.meta.gef.editors.template.TabRef)
		// c).getParas());
	}

	private void fillContainer(Container ac, BMReference bm) {
		ResultDataSet ds = new ResultDataSet();
		String s = getBmPath(bm.getModel());
		ds.setOwner(ac);
		ds.setModel(s);
		ac.setDataset(ds);
		if (ac instanceof Grid) {
			fillGrid((Grid) ac, bm.getModel());
		}

		else if (viewDiagram.getTemplateType().equals(Template.TYPE_DISPLAY)) {
			for (CommentCompositeMap map : GefModelAssist.getFields(GefModelAssist.getModel(bm.getModel()))) {
				aurora.ide.meta.gef.editors.models.Label label = new aurora.ide.meta.gef.editors.models.Label();
				label.setName(map.getString("name"));
				label.setPrompt(map.getString("prompt") == null ? map.getString("name") : map.getString("prompt"));
				if (GefModelAssist.getType(map) != null) {
					label.setType(GefModelAssist.getType(map));
				}
				((Container) ac).addChild(label);
			}
		} else {
			for (CommentCompositeMap map : GefModelAssist.getFields(GefModelAssist.getModel(bm.getModel()))) {
				Input input = new Input();
				input.setName(map.getString("name"));
				input.setPrompt(map.getString("prompt") == null ? map.getString("name") : map.getString("prompt"));
				if (GefModelAssist.getType(map) != null) {
					input.setType(GefModelAssist.getType(map));
				}
				((Container) ac).addChild(input);

			}
		}
	}

	private String getBmPath(IFile bm) {
		if (bm == null) {
			return "";
		}
		String s = Util.toPKG(bm.getFullPath());
		if (s.endsWith(".bm")) {
			s = s.substring(0, s.lastIndexOf(".bm"));
		}
		return s;
	}

	private void fillGrid(Grid grid, IFile bm) {
		for (int i = 0; i < grid.getChildren().size(); i++) {
			if (grid.getChildren().get(i) instanceof GridColumn) {
				grid.getChildren().remove(i);
				i--;
			}
		}

		for (CommentCompositeMap map : GefModelAssist.getFields(GefModelAssist.getModel(bm))) {
			GridColumn gc = new GridColumn();
			gc.setName(map.getString("name"));
			gc.setPrompt(map.getString("prompt") == null ? map.getString("name") : map.getString("prompt"));
			if (!viewDiagram.getTemplateType().equals(Template.TYPE_DISPLAY)) {
				gc.setEditor(GefModelAssist.getTypeNotNull(map));
			}
			grid.addCol(gc);
		}
		grid.setNavbarType(Grid.NAVBAR_COMPLEX);
		grid.setSelectionMode(ResultDataSet.SELECT_MULTI);
	}

	public void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	// private void setBm(BMReference bm, IPath bmPath, Text txt) {
	// IFolder folder =
	// ResourcesPlugin.getWorkspace().getRoot().getFolder(bmPath);
	// SelectModelDialog dialog = new SelectModelDialog(getShell(), folder);
	// if ((dialog.open() == Dialog.OK) && (dialog.getResult() instanceof
	// IFile)) {
	// txt.setText(((IFile) dialog.getResult()).getFullPath().toString());
	// bm.setModel((IFile) dialog.getResult());
	// if (checkFinish() && template.getLink().size() + template.getRef().size()
	// > 0) {
	// setPageComplete(true);
	// SetLinkOrRefWizardPage page = (SetLinkOrRefWizardPage) getNextPage();
	// page.createCustom(template);
	// } else {
	// setPageComplete(false);
	// }
	// }
	// }
	//
	// public boolean checkFinish() {
	// boolean bool = false;
	// for (BMReference b : template.getBms()) {
	// if (b.getModel() == null) {
	// return false;
	// } else {
	// bool = true;
	// }
	// }
	// return bool;
	// }

	public NewWizardPage getNewWizardPage() {
		return (NewWizardPage) getPreviousPage();
	}

	public ViewDiagram getViewDiagram() {
		return viewDiagram;
	}
}
