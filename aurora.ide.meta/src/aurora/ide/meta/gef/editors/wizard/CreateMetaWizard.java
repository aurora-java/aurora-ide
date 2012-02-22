package aurora.ide.meta.gef.editors.wizard;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import uncertain.composite.CompositeMap;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.meta.gef.editors.VScreenEditor;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Button;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.QueryDataSet;
import aurora.ide.meta.gef.editors.models.ResultDataSet;
import aurora.ide.meta.gef.editors.models.Toolbar;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.template.ButtonRegion;
import aurora.ide.meta.gef.editors.template.QueryRegion;
import aurora.ide.meta.gef.editors.template.Region;
import aurora.ide.meta.gef.editors.template.ResultRegion;
import aurora.ide.meta.gef.editors.template.Template;
import aurora.ide.meta.gef.editors.template.parse.AuroraModelFactory;
import aurora.ide.meta.gef.editors.template.parse.GefModelAssist;
import aurora.ide.search.core.Util;
import aurora.ide.search.ui.EditorOpener;

public class CreateMetaWizard extends Wizard implements INewWizard {
	private NewWizardPage newPage = new NewWizardPage();
	private SettingWizardPage settingPage = new SettingWizardPage();

	private IWorkbench workbench;
	private Template template;
	private Map<Button, String> bRelation = new HashMap<Button, String>();
	private Map<Grid, String> gRelation = new HashMap<Grid, String>();
	private Map<String, AuroraComponent> rRelation = new HashMap<String, AuroraComponent>();

	public void addPages() {
		addPage(newPage);
		addPage(settingPage);
	}

	@Override
	public boolean performFinish() {
		EditorOpener editorOpener = new EditorOpener();
		try {
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(newPage.getPath() + "/" + newPage.getFileName()));
			if (!file.exists()) {
				file.create(null, true, null);
			}
			VScreenEditor editor = (VScreenEditor) editorOpener.open(workbench.getActiveWorkbenchWindow().getActivePage(), file, true);
			editor.setDiagram(createView());
			return true;
		} catch (Exception e) {
			DialogUtil.showExceptionMessageBox(e);
			e.printStackTrace();
			return false;
		}
	}

	private ViewDiagram createView() {
		ViewDiagram viewDiagram = new ViewDiagram();
		Container container = null;
		for (Region region : template.getRegions()) {
			if (region instanceof ButtonRegion) {
				if ((container = createButtonRegion((ButtonRegion) region)) != null) {
					container.setSectionType(Container.SECTION_TYPE_BUTTON);
				}
			} else if (region instanceof QueryRegion) {
				if ((container = createQueryRegion((QueryRegion) region)) != null) {
					container.setSectionType(Container.SECTION_TYPE_QUERY);
				}
			} else if (region instanceof ResultRegion) {
				if ((container = createResultRegion((ResultRegion) region)) != null) {
					container.setSectionType(Container.SECTION_TYPE_RESULT);
				}
			}
			if (container != null) {
				viewDiagram.addChild(container);
				rRelation.put(region.getId(), container);
			}
		}
		for (Button btn : bRelation.keySet()) {
			btn.getButtonClicker().setTargetComponent(rRelation.get(bRelation.get(btn)));
		}
		// TODO
		for (Grid grid : gRelation.keySet()) {
			grid.getDataset().setQueryContainer((Container) rRelation.get(gRelation.get(grid)));
		}
		viewDiagram.setBindTemplate(template.getPath());
		return viewDiagram;
	}

	private Container createButtonRegion(ButtonRegion region) {
		try {
			Container container = AuroraModelFactory.createModel(region.getContainer());
			for (int i = 0; i < region.getButtons().size(); i++) {
				Button btn = new Button();
				btn.getButtonClicker().setActionID(region.getButtons().get(i).getType());
				btn.setText(region.getButtons().get(i).getText());
				bRelation.put(btn, region.getButtons().get(i).getTarget());
				container.addChild(btn);
			}
			return container;
		} catch (Exception e) {
			DialogUtil.showExceptionMessageBox(e);
		}
		return null;
	}

	private Container createQueryRegion(QueryRegion region) {
		try {
			Container container = AuroraModelFactory.createModel(region.getContainer());
			QueryDataSet dataset = new QueryDataSet();
			dataset.setModel(Util.toPKG(region.getModel().getModel().getFullPath()));
			container.setDataset(dataset);
			for (CompositeMap map : GefModelAssist.getQueryFields(GefModelAssist.getModel(region.getModel().getModel()))) {
				Input input = new Input();
				input.setType(GefModelAssist.getType(map));
				input.setName(map.getString("name"));
				input.setPrompt(map.getString("prompt"));
				input.setType(map.getString("defaultEditor"));
				container.addChild(input);
			}
			return container;
		} catch (Exception e) {
			DialogUtil.showExceptionMessageBox(e);
		}
		return null;
	}

	private Container createResultRegion(ResultRegion region) {
		try {
			if ("Grid".equalsIgnoreCase(region.getContainer())) {
				return createGrid(region);
			} else {
				Container container = AuroraModelFactory.createModel(region.getContainer());
				QueryDataSet dataset = new QueryDataSet();
				dataset.setModel(Util.toPKG(region.getModel().getModel().getFullPath()));
				container.setDataset(dataset);
				for (CompositeMap map : GefModelAssist.getFields(GefModelAssist.getModel(region.getModel().getModel()))) {
					Input input = new Input();
					input.setType(GefModelAssist.getType(map));
					input.setName(map.getString("name") == null ? "" : map.getString("name"));
					input.setPrompt(map.getString("prompt") == null ? "prompt" : map.getString("prompt"));
					container.addChild(input);
				}
				return container;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private Container createGrid(ResultRegion region) {
		Grid grid = AuroraModelFactory.createModel(region.getContainer());
		gRelation.put(grid, region.getQueryRegion());
		Toolbar tool = new Toolbar();
		String[] buttonType = { Button.ADD, Button.SAVE, Button.DELETE, Button.CLEAR, Button.EXCEL };
		for (String s : buttonType) {
			Button btn = new Button();
			btn.setButtonType(s);
			tool.addChild(btn);
		}
		grid.addChild(tool);
		for (CompositeMap map : GefModelAssist.getFields(GefModelAssist.getModel(region.getModel().getModel()))) {
			GridColumn gc = new GridColumn();
			gc.setName(map.getString("name"));
			gc.setPrompt(map.getString("prompt"));
			gc.setEditor(map.getString("defaultEditor"));
			grid.addCol(gc);
		}
		ResultDataSet dataset = new ResultDataSet();
		dataset.setModel(Util.toPKG(region.getModel().getModel().getFullPath()));
		grid.setDataset(dataset);
		grid.setNavbarType(Grid.NAVBAR_COMPLEX);
		grid.setSelectionMode(ResultDataSet.SELECT_MULTI);
		return grid;
	}

	public boolean canFinish() {
		IWizardPage page = getContainer().getCurrentPage();
		if ((page instanceof SettingWizardPage) && ((SettingWizardPage) page).isPageComplete()) {
			template = ((SettingWizardPage) page).getTemplate();
			return true;
		}
		return false;
	}

	public boolean needsProgressMonitor() {
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
	}
}
