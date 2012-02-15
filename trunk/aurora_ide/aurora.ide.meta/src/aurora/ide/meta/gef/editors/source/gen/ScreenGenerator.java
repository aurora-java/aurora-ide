package aurora.ide.meta.gef.editors.source.gen;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Button;
import aurora.ide.meta.gef.editors.models.ButtonClicker;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Dataset;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.Toolbar;
import aurora.ide.meta.gef.editors.models.ViewDiagram;

public class ScreenGenerator {

	private IDGenerator idGenerator;
	private AuroraComponent2CompositMap a2Map;
	private ScriptGenerator scriptGenerator;

	public void genFile(ViewDiagram root) {
		idGenerator = new IDGenerator(root);
		a2Map = new AuroraComponent2CompositMap(this);
		CompositeMap screen = AuroraComponent2CompositMap
				.createScreenCompositeMap();
		CompositeMap view = a2Map.toCompositMap(root);
		CompositeMap script = view.createChild("script");
		CompositeMap datasets = createCompositeMap("datasets");
		CompositeMap screenBody = createCompositeMap("screenBody");
		screen.addChild(view);
//		view.addChild(script);
		view.addChild(datasets);
		view.addChild(screenBody);

		scriptGenerator = new ScriptGenerator(this, script);
		fill(root, screenBody, datasets);

		fillLinks(view);
		script.setText(	scriptGenerator.getScript());
		System.out.println(screen.toXML());

	}

	private void fillLinks(CompositeMap view) {
		Map<ButtonClicker, String> linkIDs = scriptGenerator.getLinkIDs();
		Set<ButtonClicker> keySet = linkIDs.keySet();
		for (ButtonClicker bc : keySet) {
			String openPath = bc.getOpenPath();
			IPath requestPath = new Path("${/request/@context_path}");
			IPath path = requestPath.append(openPath);
			CompositeMap link = createCompositeMap("link");
			link.put("url", path.toString());
			link.put("id", linkIDs.get(bc));
			view.addChild(0, link);
		}
	}

	public CompositeMap createCompositeMap(String name) {
		return AuroraComponent2CompositMap.createChild(name);
	}

	public String genEditorID(String editorType) {
		return idGenerator.genEditorID(editorType);
	}

	protected void fill(Container root, CompositeMap parent,
			CompositeMap datasets) {

		List<AuroraComponent> children = root.getChildren();
		for (AuroraComponent ac : children) {
			CompositeMap child = a2Map.toCompositMap(ac);
			if (child == null) {
				System.out.println(ac.getType());
				continue;
			}
			if (ac instanceof GridColumn && root instanceof Grid) {
				CompositeMap columns = getColumns(parent);
				genColumnEditor((GridColumn) ac, child, parent);
				columns.addChild(child);
			} else {
				parent.addChild(child);
			}
			if (ac instanceof Button) {
				fillButton((Button) ac, child);
			}
			if (ac instanceof Container) {
				fill((Container) ac, child, datasets);
				fillDatasets((Container) ac, datasets);
			}
			if (ac instanceof Input || ac instanceof Grid) {
				bindDataset(root, ac, child, datasets);
			}
		}
	}

	public void fillButton(Button ac, CompositeMap buttonMap) {
		if(ac.getParent() instanceof Toolbar){
			return;
		}
		ButtonClicker bc = ((Button) ac).getButtonClicker();
		String functionName = this.scriptGenerator.genButtonClicker(bc);
		buttonMap.put("click", functionName);
	}

	private void genColumnEditor(GridColumn ac, CompositeMap colmunMap,
			CompositeMap gridMap) {
		CompositeMap editors = getEditors(gridMap);
		String editorType = ac.getEditor();
		if (editorType != null && !("".equals(editorType))) {
			CompositeMap editorMap = editors.getChild(editorType);
			if (editorMap == null) {
				editorMap = createCompositeMap(editorType);
				String id = genEditorID(editorType);
				editorMap.put("id", id);
				editors.addChild(editorMap);
			}
			colmunMap.put("editor", editorMap.get("id"));
		}
	}

	// <a:editors>
	// <a:textField id="editor_tf_2"/>
	// <a:datePicker id="editor_dp_2"/>
	// <a:comboBox id="editor_cb_2"/>
	// <a:lov id="editor_lov_2"/>
	// </a:editors>
	public CompositeMap getEditors(CompositeMap gridMap) {
		CompositeMap editors = gridMap.getChild("editors");
		if (editors == null) {
			editors = createCompositeMap("editors");
			gridMap.addChild(editors);
		}
		return editors;
	}

	public CompositeMap getColumns(CompositeMap gridMap) {
		CompositeMap columns = gridMap.getChild("columns");
		if (columns == null) {
			columns = createCompositeMap("columns");
			gridMap.addChild(columns);
		}
		return columns;
	}

	private void fillDatasets(Container ac, CompositeMap datasets) {
		Dataset dataset = ac.getDataset();
		fillDatasets(datasets, dataset);
	}

	public CompositeMap fillDatasets(CompositeMap datasets, Dataset dataset) {
		if (dataset == null || dataset.isUseParentBM())
			return null;
		CompositeMap dsMap = datasets.getChildByAttrib("id", dataset.getId());
		if (dsMap == null) {
			CompositeMap rds = a2Map.toCompositMap(dataset);
			datasets.addChild(rds);
			return rds;
		}
		return dsMap;

	}

	// columns
	private void bindDataset(Container root, AuroraComponent ac,
			CompositeMap child, CompositeMap datasets) {
		if (ac instanceof Grid || ac instanceof Input) {
			Dataset dataset = null;
			if (ac instanceof Grid) {
				dataset = findDataset((Grid) ac);
			} else {
				dataset = findDataset(root);
			}
			if (dataset != null) {
				child.put("bindTarget", dataset.getId());
			}
			if (ac instanceof Input) {
				// lov,combox 特殊处理
				// required,readonly
				fillDataset(dataset, datasets, ac);
			}
		}
	}

	private void fillDataset(Dataset dataset, CompositeMap datasets,
			AuroraComponent ac) {
		CompositeMap dsMap = fillDatasets(datasets, dataset);
		if (dsMap == null) {
			return;
		}
		CompositeMap fields = dsMap.getChild("fields");
		if (fields == null) {
			fields = dsMap.createChild("fields");
		}
		CompositeMap field = fields.getChildByAttrib(AuroraComponent.NAME,
				ac.getPropertyValue(AuroraComponent.NAME));
		if (field == null) {
			field = fields.createChild("field");
			field.put(AuroraComponent.NAME,
					ac.getPropertyValue(AuroraComponent.NAME));
		}
		if (ac instanceof Input) {
			field.put(AuroraComponent.READONLY, ((Input) ac).isReadOnly());
			field.put(AuroraComponent.REQUIRED, ((Input) ac).isRequired());
		}

		// AuroraComponent.REQUIRED
		// <a:fields>
		// <a:field name="policy_code" required="true"/>
		// <a:field name="policy_name"/>
		// <a:field name="description"/>
		// </a:fields>

	}

	private Dataset findDataset(Container container) {
		Dataset dataset = container.getDataset();
		if (dataset == null)
			return null;
		boolean useParentBM = dataset.isUseParentBM();
		if (useParentBM) {
			return findDataset(container.getParent());
		}
		return dataset;
	}

	public IDGenerator getIdGenerator() {
		return idGenerator;
	}

	// IFile newFileHandle = AuroraPlugin.getWorkspace().getRoot()
	// .getFile(new Path("/hr_aurora/web/a0.screen"));
	// CompositeMap cm = new CompositeMap("xx");
	// cm.put("x", "bb");
	// InputStream is = new ByteArrayInputStream(cm.toXML().getBytes());
	// CreateFileOperation op = new CreateFileOperation(newFileHandle, null,
	// is, "Create New File");
	// try {
	// PlatformUI
	// .getWorkbench()
	// .getOperationSupport()
	// .getOperationHistory()
	// .execute(
	// op,
	// null,
	// WorkspaceUndoUtil.getUIInfoAdapter(this.getSite()
	// .getShell()));
	// } catch (final ExecutionException e) {
	// // handle exceptions
	// e.printStackTrace();
	// }
	// xmlns:a="http://www.aurora-framework.org/application"

}
