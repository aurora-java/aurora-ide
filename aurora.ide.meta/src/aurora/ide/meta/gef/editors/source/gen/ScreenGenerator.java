package aurora.ide.meta.gef.editors.source.gen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.exception.TemplateNotBindedException;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Button;
import aurora.ide.meta.gef.editors.models.ButtonClicker;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Dataset;
import aurora.ide.meta.gef.editors.models.DatasetBinder;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.IDatasetFieldDelegate;
import aurora.ide.meta.gef.editors.models.QueryContainer;
import aurora.ide.meta.gef.editors.models.ResultDataSet;
import aurora.ide.meta.gef.editors.models.Toolbar;
import aurora.ide.meta.gef.editors.models.ViewDiagram;

public class ScreenGenerator {

	private IDGenerator idGenerator;
	private AuroraComponent2CompositMap a2Map;
	private ScriptGenerator scriptGenerator;
	private CompositeMap datasets;

	private Map<Dataset, String> datasetMap = new HashMap<Dataset, String>();

	public void genFile(ViewDiagram view) throws TemplateNotBindedException {
		String bindTemplate = view.getBindTemplate();
		// if (bindTemplate == null || "".equals(bindTemplate))
		// throw new TemplateNotBindedException();
		idGenerator = new IDGenerator(view);
		a2Map = new AuroraComponent2CompositMap(this);
		CompositeMap screen = AuroraComponent2CompositMap
				.createScreenCompositeMap();
		CompositeMap viewMap = a2Map.toCompositMap(view);
		CompositeMap script = viewMap.createChild("script");
		datasets = createCompositeMap("datasets");
		CompositeMap screenBody = createCompositeMap("screenBody");
		screen.addChild(viewMap);
		viewMap.addChild(datasets);
		viewMap.addChild(screenBody);

		scriptGenerator = new ScriptGenerator(this, script);
		fill(view, screenBody);
		fillLinks(viewMap);
		script.setText(scriptGenerator.getScript());
		System.out.println(screen.toXML());
	}

	private void fill(Container container, CompositeMap containerMap) {
		List<AuroraComponent> children = container.getChildren();
		for (AuroraComponent ac : children) {
			CompositeMap childMap = a2Map.toCompositMap(ac);
			if (childMap == null) {
				System.out.println(ac.getType());
				continue;
			}
			if (ac instanceof GridColumn && container instanceof Grid) {

				CompositeMap columns = getColumns(containerMap);
				columns.addChild(childMap);
			} else {
				containerMap.addChild(childMap);
			}

			if (ac instanceof GridColumn) {
				genColumnEditor((GridColumn) ac, childMap, containerMap);
			}
			if (ac instanceof Button) {
				fillButton((Button) ac, childMap);
			}
			if (ac instanceof Container) {
				fill((Container) ac, childMap);
				fillDatasets((Container) ac, datasets);
			}
			if (ac instanceof DatasetBinder) {
				bindDataset(container, ac, childMap, datasets);
			}
			if (ac instanceof IDatasetFieldDelegate) {
				fillDataset(findDataset(ac.getParent()), datasets, ac);
			}
		}
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

	public void fillButton(Button ac, CompositeMap buttonMap) {
		if (ac.getParent() instanceof Toolbar) {
			return;
		}
		ButtonClicker bc = ((Button) ac).getButtonClicker();
		String functionName = this.scriptGenerator.genButtonClicker(bc);
		buttonMap.put("click", functionName);
	}

	private void genColumnEditor(GridColumn ac, CompositeMap colmunMap,
			CompositeMap containerMap) {
		CompositeMap gridMap = findGridMap(containerMap);
		if (gridMap == null)
			return;
		String editorType = ac.getEditor();
		if (editorType != null && !("".equals(editorType))) {
			CompositeMap editors = getEditors(gridMap);
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

	private CompositeMap findGridMap(CompositeMap containerMap) {
		if ("grid".equalsIgnoreCase(containerMap.getName())) {
			return containerMap;
		}
		if ("screenBody".equalsIgnoreCase(containerMap.getName())) {
			return null;
		}
		return findGridMap(containerMap.getParent());
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
		if (Container.SECTION_TYPE_QUERY.equals(ac.getSectionType())
				|| Container.SECTION_TYPE_RESULT.equals(ac.getSectionType())) {
			Dataset dataset = ac.getDataset();
			fillDatasets(datasets, dataset);
		}
	}

	public CompositeMap fillDatasets(CompositeMap datasets, Dataset dataset) {
		if (dataset == null || dataset.isUseParentBM())
			return null;
		String dsID = this.datasetMap.get(dataset);
		if (dsID == null) {
			dsID = this.idGenerator.genDatasetID(dataset);
			datasetMap.put(dataset, dsID);
		}
		CompositeMap dsMap = datasets.getChildByAttrib("id", dsID);
		if (dsMap == null) {
			CompositeMap rds = a2Map.toCompositMap(dataset);
			rds.put("id", dsID);

			if (dataset.isUse4Query()) {
				rds.put("autoCreate", true);
			} else {
				rds.put("model", dataset.getModel());
			}
			QueryContainer qs = (QueryContainer) dataset
					.getPropertyValue(ResultDataSet.QUERY_CONTAINER);
			if (qs != null) {
				Dataset ds = qs.getTarget().getDataset();
				Object qds = this.fillDatasets(ds).get("id");
				rds.put(ResultDataSet.QUERY_DATASET, qds.toString());
			}

			datasets.addChild(rds);
			return rds;
		}
		return dsMap;
	}

	public CompositeMap fillDatasets(Dataset dataset) {
		return this.fillDatasets(datasets, dataset);
	}

	private void bindDataset(Container root, AuroraComponent ac,
			CompositeMap child, CompositeMap datasets) {
		if (ac instanceof DatasetBinder) {
			Dataset dataset = null;
			if (ac instanceof Grid) {
				dataset = findDataset((Grid) ac);
			} else {
				dataset = findDataset(root);
			}
			if (dataset != null) {
				CompositeMap ds = this.fillDatasets(datasets, dataset);
				child.put("bindTarget", ds.get("id"));
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
		if (ac instanceof IDatasetFieldDelegate) {
			field.put(AuroraComponent.READONLY, ac.getPropertyValue(AuroraComponent.READONLY));
			field.put(AuroraComponent.REQUIRED, ac.getPropertyValue(AuroraComponent.REQUIRED));
		}

		// lov,combox 特殊处理
		// column 的 required,readonly
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
