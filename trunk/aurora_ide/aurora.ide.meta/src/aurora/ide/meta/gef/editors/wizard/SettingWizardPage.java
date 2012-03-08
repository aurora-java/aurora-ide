package aurora.ide.meta.gef.editors.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;

import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.editors.template.ButtonRegion;
import aurora.ide.meta.gef.editors.template.Model;
import aurora.ide.meta.gef.editors.template.QueryRegion;
import aurora.ide.meta.gef.editors.template.Region;
import aurora.ide.meta.gef.editors.template.ResultRegion;
import aurora.ide.meta.gef.editors.template.Template;
import aurora.ide.meta.gef.editors.wizard.dialog.SelectModelDialog;
import aurora.ide.meta.gef.editors.wizard.dialog.StyleSettingDialog;
import aurora.ide.meta.gef.i18n.Messages;
import aurora.ide.meta.project.AuroraMetaProject;
import aurora.ide.project.propertypage.ProjectPropertyPage;

public class SettingWizardPage extends WizardPage {

	private Template template;
	private Composite container;

	private AuroraMetaProject metaPro;
	private IProject auroraPro;
	private IPath bmPath;

	public SettingWizardPage() {
		super("aurora.wizard.setting.Page"); //$NON-NLS-1$
		setTitle(Messages.SettingWizardPage_Title);
		setDescription(Messages.SettingWizardPage_Model_Bind);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());

		metaPro = new AuroraMetaProject(((NewWizardPage) getPreviousPage()).getMetaProject());
		try {
			auroraPro = metaPro.getAuroraProject();
			bmPath = new Path(auroraPro.getPersistentProperty(ProjectPropertyPage.BMQN));
		} catch (ResourceNotFoundException e) {
			bmPath = null;
			e.printStackTrace();
		} catch (CoreException e) {
			bmPath = null;
			e.printStackTrace();
		} catch (NullPointerException e) {
			bmPath = null;
			e.printStackTrace();
		}

		container = new Composite(composite, SWT.NONE);
		container.setLayout(new GridLayout());
		GridData data = new GridData(GridData.FILL_BOTH);
		container.setLayoutData(data);
		setControl(composite);
		createRegiog();
		Link setting = new Link(composite, SWT.NONE);
		setting.setText(Messages.SettingWizardPage_Detail_Setting);
		setting.setEnabled(false);
		setting.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				StyleSettingDialog dialog = new StyleSettingDialog(getShell(), template);
				dialog.open();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	public void createRegiog() {
		for (Control control : container.getChildren()) {
			control.dispose();
		}
		if (template == null || template.getModels().size() == 0 || template.getRegions().size() == 0) {
			setPageComplete(true);
			return;
		} else {
			setPageComplete(false);
		}
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label lblRegion = new Label(composite, SWT.NONE);
		lblRegion.setText(Messages.SettingWizardPage_Region);
		Label lblTyoe = new Label(composite, SWT.NONE);
		lblTyoe.setText(Messages.SettingWizardPage_Region_type);
		Label lblModel = new Label(composite, SWT.NONE);
		lblModel.setText("Model"); //$NON-NLS-1$
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.CENTER;
		lblModel.setLayoutData(gd);
		new Label(composite, SWT.NONE);

		createDynamicComponents(composite);
		container.layout(true);
	}

	private void createDynamicComponents(Composite composite) {
		Map<Model, List<Region>> relation = getRelation(template);
		for (final Model model : template.getModels()) {
			if (relation.get(model) != null && relation.get(model).size() > 0) {
				Region r = null;
				for (int i = 0; i < relation.get(model).size(); i++) {
					if (!(relation.get(model).get(i) instanceof ButtonRegion)) {
						r = relation.get(model).get(i);
						break;
					}
				}
				if (r == null) {
					continue;
				}
				Label lbl = new Label(composite, SWT.NONE);
				lbl.setText(r.getName());
				lbl = new Label(composite, SWT.NONE);
				lbl.setText(getType(r));
				final Text txtModel = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
				txtModel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				Button btn = new Button(composite, SWT.None);
				btn.setText(Messages.SettingWizardPage_Select_model);
				if (bmPath == null) {
					btn.setEnabled(false);
				}
				btn.addSelectionListener(new SelectionListener() {
					public void widgetSelected(SelectionEvent e) {
						if (bmPath == null) {
							return;
						}
						IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(bmPath);
						SelectModelDialog dialog = new SelectModelDialog(getShell(), folder);
						if (Dialog.OK == dialog.open()) {
							IFile file = (IFile) dialog.getResult();
							txtModel.setText(file.getName());
							model.setModel(file);
						}
					}

					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});
				final List<Text> txts = new ArrayList<Text>();
				for (int i = 1; i < relation.get(model).size(); i++) {
					r = relation.get(model).get(i);
					if (r instanceof ButtonRegion) {
						continue;
					}
					lbl = new Label(composite, SWT.NONE);
					lbl.setText(r.getName());
					lbl = new Label(composite, SWT.NONE);
					lbl.setText(getType(r));
					Text txt = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
					txt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					txts.add(txt);
					new Label(composite, SWT.NONE);
				}
				txtModel.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent e) {
						for (Text t : txts) {
							t.setText(txtModel.getText());
						}
						for (Model model : template.getModels()) {
							if (model == null) {
								setPageComplete(false);
								break;
							} else {
								setPageComplete(true);
							}
						}
					}
				});
			}
		}
	}

	private String getType(Region region) {
		if (region instanceof QueryRegion) {
			return "query"; //$NON-NLS-1$
		} else if (region instanceof ButtonRegion) {
			return "buttons"; //$NON-NLS-1$
		} else if (region instanceof ResultRegion) {
			return "result"; //$NON-NLS-1$
		}
		return "unknown"; //$NON-NLS-1$
	}

	private Map<Model, List<Region>> getRelation(Template template) {
		Map<Model, List<Region>> relation = new HashMap<Model, List<Region>>();
		if (template != null) {
			for (Model model : template.getModels()) {
				for (Region region : template.getRegions()) {
					if (region.getModel().equals(model)) {
						if (relation.get(model) == null) {
							relation.put(model, new ArrayList<Region>());
						}
						relation.get(model).add(region);
					}
				}
			}
		}
		return relation;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

	public Template getTemplate() {
		return template;
	}
}
