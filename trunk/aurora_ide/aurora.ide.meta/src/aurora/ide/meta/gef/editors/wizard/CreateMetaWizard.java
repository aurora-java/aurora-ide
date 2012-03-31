package aurora.ide.meta.gef.editors.wizard;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.undo.CreateFileOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;

import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.meta.gef.editors.VScreenEditor;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Button;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.ResultDataSet;
import aurora.ide.meta.gef.editors.models.TabItem;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.models.io.ModelIOManager;
import aurora.ide.meta.gef.editors.template.BMBindComponent;
import aurora.ide.meta.gef.editors.template.BMReference;
import aurora.ide.meta.gef.editors.template.Component;
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

	private Map<String, IFile> modelMap = new HashMap<String, IFile>();
	private Map<String, AuroraComponent> acptMap = new HashMap<String, AuroraComponent>();

	public void addPages() {
		addPage(newPage);
		addPage(settingPage);
	}

	@Override
	public boolean performFinish() {
		EditorOpener editorOpener = new EditorOpener();
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(newPage.getPath() + "/" + newPage.getFileName()));
		CommentCompositeMap rootMap = null;
		try {
			rootMap = (CommentCompositeMap) ModelIOManager.getNewInstance().toCompositeMap(createView());
		} catch (RuntimeException e) {
			e.printStackTrace();
			DialogUtil.showExceptionMessageBox(e);
			return false;
		}
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + rootMap.toXML();
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		final CreateFileOperation cfo = new CreateFileOperation(file, null, is, "create template.");
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) {
				try {
					cfo.execute(monitor, WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		};
		try {
			getContainer().run(true, true, op);
			IEditorPart editor = editorOpener.open(workbench.getActiveWorkbenchWindow().getActivePage(), file, true);
			if (editor instanceof VScreenEditor) {
				((VScreenEditor) editor).markDirty();
			}
			return true;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return false;
	}

	private ViewDiagram createView() {
		ViewDiagram viewDiagram = new ViewDiagram();
		for (Component cpt : template.getChildren()) {
			AuroraComponent ac = createAuroraComponent(cpt);
			viewDiagram.addChild(ac);
		}
		fillQueryField();
		viewDiagram.setForDisplay(template.isForDisplay());
		viewDiagram.setBindTemplate(template.getPath());
		return viewDiagram;
	}

	private void fillQueryField() {
		for (String id : modelMap.keySet()) {
			Object obj = acptMap.get(id);
			if (!(obj instanceof Container)) {
				continue;
			}
			if (((Container) obj).getSectionType() == null || "".equals(((Container) obj).getSectionType())) {
				((Container) obj).setSectionType(Container.SECTION_TYPE_QUERY);
			}
			CommentCompositeMap map = GefModelAssist.getModel(modelMap.get(id));
			for (CommentCompositeMap queryMap : GefModelAssist.getQueryFields(GefModelAssist.getModel(modelMap.get(id)))) {
				Input input = new Input();
				CommentCompositeMap fieldMap = GefModelAssist.getCompositeMap((CommentCompositeMap) map.getChild("fields"), "name", queryMap.getString("field"));
				if (fieldMap == null) {
					fieldMap = queryMap;
				}
				input.setName(fieldMap.getString("name"));
				input.setPrompt(fieldMap.getString("prompt") == null ? fieldMap.getString("name") : fieldMap.getString("prompt"));
				input.setType(GefModelAssist.getTypeNotNull(fieldMap));
				((Container) obj).addChild(input);
			}
		}
	}

	private AuroraComponent createAuroraComponent(Component cpt) {
		AuroraComponent acpt = AuroraModelFactory.createComponent(cpt.getComponentType());
		acptMap.put(cpt.getId(), acpt);
		if ((cpt instanceof BMBindComponent) && (acpt instanceof Container)) {
			fillContainer(cpt, (Container) acpt);
		}
		if (cpt.getChildren() == null) {
			return acpt;
		}
		for (Component cp : cpt.getChildren()) {
			AuroraComponent ac = createAuroraComponent(cp);
			if (ac instanceof Button) {
				aurora.ide.meta.gef.editors.template.Button btn = (aurora.ide.meta.gef.editors.template.Button) cp;
				((Button) ac).setText(btn.getText());
				if (btn.getType() != null) {
					((Button) ac).setButtonType(btn.getType());
				}
			}
			if (acpt instanceof Container) {
				((Container) acpt).addChild(ac);
			} else if (acpt instanceof TabItem) {
				((TabItem) acpt).addChild(ac);
			}
		}
		return acpt;
	}

	private void fillContainer(Component cpt, Container acpt) {
		String bmId = ((BMBindComponent) cpt).getBmReferenceID();
		if (bmId == null || "".equals(bmId)) {
			return;
		}
		BMReference bm = null;
		for (BMReference b : template.getBms()) {
			if (!bmId.equals(b.getId())) {
				continue;
			}
			bm = b;
			break;
		}
		if (bm == null) {
			return;
		}
		ResultDataSet ds = new ResultDataSet();
		String s = Util.toPKG(bm.getModel().getFullPath());
		if (s.endsWith(".bm")) {
			s = s.substring(0, s.lastIndexOf(".bm"));
		}
		ds.setModel(s);
		((Container) acpt).setDataset(ds);
		((Container) acpt).setSectionType(Container.SECTION_TYPE_RESULT);
		if (acpt instanceof Grid) {
			fillGrid((Grid) acpt, bm.getModel());
		} else {
			for (CommentCompositeMap map : GefModelAssist.getFields(GefModelAssist.getModel(bm.getModel()))) {
				Input input = new Input();
				input.setName(map.getString("name"));
				input.setPrompt(map.getString("prompt") == null ? map.getString("name") : map.getString("prompt"));
				if (GefModelAssist.getType(map) != null) {
					input.setType(GefModelAssist.getType(map));
				}
				((Container) acpt).addChild(input);
			}
		}
		String qcId = ((BMBindComponent) cpt).getQueryComponent();
		if (qcId != null && (!"".equals(qcId))) {
			modelMap.put(qcId, bm.getModel());
		}
	}

	private void fillGrid(Grid grid, IFile bm) {
		for (CommentCompositeMap map : GefModelAssist.getFields(GefModelAssist.getModel(bm))) {
			GridColumn gc = new GridColumn();
			gc.setName(map.getString("name"));
			gc.setPrompt(map.getString("prompt") == null ? map.getString("name") : map.getString("prompt"));
			if (GefModelAssist.getType(map) != null) {
				gc.setEditor(GefModelAssist.getType(map));
			}
			grid.addCol(gc);
		}
		grid.setNavbarType(Grid.NAVBAR_COMPLEX);
		grid.setSelectionMode(ResultDataSet.SELECT_MULTI);
	}

	public boolean canFinish() {
		IWizardPage page = getContainer().getCurrentPage();
		if ((page instanceof SettingWizardPage) && page.isPageComplete()) {
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

//
// private Container createButtonRegion(ButtonRegion region) {
// Container container =
// AuroraModelFactory.createModel(region.getContainer());
// for (int i = 0; i < region.getButtons().size(); i++) {
// Button btn = new Button();
// btn.getButtonClicker().setActionID(region.getButtons().get(i).getType());
// btn.setText(region.getButtons().get(i).getText());
// bRelation.put(btn, region.getButtons().get(i).getTarget());
// container.addChild(btn);
// }
// return container;
// }
//
// private Container createQueryRegion(QueryRegion region) {
// Container container =
// AuroraModelFactory.createModel(region.getContainer());
// QueryDataSet dataset = new QueryDataSet();
// String s = Util.toPKG(region.getModel().getModel().getFullPath());
// if (s.endsWith(".bm")) {
// s = s.substring(0, s.lastIndexOf(".bm"));
// }
// dataset.setModel(s);
// container.setDataset(dataset);
// for (CompositeMap map :
// GefModelAssist.getQueryFields(GefModelAssist.getModel(region.getModel().getModel())))
// {
// Input input = createInput(region, map);
// container.addChild(input);
// }
// return container;
// }
//
// private Container createResultRegion(ResultRegion region) {
// if ("Grid".equalsIgnoreCase(region.getContainer())) {
// return createGrid(region);
// } else {
// Container container =
// AuroraModelFactory.createModel(region.getContainer());
// if (container instanceof RowCol) {
// ((RowCol) container).setCol(1);
// ((RowCol) container).setRow(10);
// }
// ResultDataSet dataset = new ResultDataSet();
// String s = Util.toPKG(region.getModel().getModel().getFullPath());
// if (s.endsWith(".bm")) {
// s = s.substring(0, s.lastIndexOf(".bm"));
// }
// dataset.setModel(s);
// container.setDataset(dataset);
// for (CompositeMap map :
// GefModelAssist.getFields(GefModelAssist.getModel(region.getModel().getModel())))
// {
// Input input = createInput(region, map);
// container.addChild(input);
// }
// return container;
// }
// }
//
// private Input createInput(Region region, CompositeMap map) {
// Input input = new Input();
// input.setType(GefModelAssist.getTypeNotNull(map));
// CompositeMap fieldMap =
// GefModelAssist.getCompositeMap(GefModelAssist.getModel(region.getModel().getModel()).getChild("fields"),
// "name", map.getString("field"));
// if (fieldMap == null) {
// fieldMap = map;
// }
// input.setName(fieldMap.getString("name"));
// input.setPrompt(fieldMap.getString("prompt") == null ?
// fieldMap.getString("name") : fieldMap.getString("prompt"));
// input.setType(GefModelAssist.getTypeNotNull(fieldMap));
// return input;
// }
//
// private Container createGrid(ResultRegion region) {
// Grid grid = AuroraModelFactory.createModel(region.getContainer());
// gRelation.put(grid, region.getQueryRegion());
// Toolbar tool = new Toolbar();
// String[] buttonType = { Button.ADD, Button.SAVE, Button.DELETE,
// Button.CLEAR, Button.EXCEL };
// for (String s : buttonType) {
// Button btn = new Button();
// btn.setButtonType(s);
// tool.addChild(btn);
// }
// grid.addChild(tool);
// for (CompositeMap map :
// GefModelAssist.getFields(GefModelAssist.getModel(region.getModel().getModel())))
// {
// GridColumn gc = new GridColumn();
// gc.setName(map.getString("name"));
// gc.setPrompt(map.getString("prompt") == null ? map.getString("name") :
// map.getString("prompt"));
// if (GefModelAssist.getType(map) != null) {
// gc.setEditor(GefModelAssist.getType(map));
// }
// grid.addCol(gc);
// }
// ResultDataSet dataset = new ResultDataSet();
// String s = Util.toPKG(region.getModel().getModel().getFullPath());
// if (s.endsWith(".bm")) {
// s = s.substring(0, s.lastIndexOf(".bm"));
// }
// dataset.setModel(s);
// grid.setDataset(dataset);
// grid.setNavbarType(Grid.NAVBAR_COMPLEX);
// grid.setSelectionMode(ResultDataSet.SELECT_MULTI);
// return grid;
// }
