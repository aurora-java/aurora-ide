package aurora.ide.meta.gef.editors.source.gen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
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
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.QueryContainer;
import aurora.ide.meta.gef.editors.models.Renderer;
import aurora.ide.meta.gef.editors.models.ResultDataSet;
import aurora.ide.meta.gef.editors.models.TabFolder;
import aurora.ide.meta.gef.editors.models.TabItem;
import aurora.ide.meta.gef.editors.models.Toolbar;
import aurora.ide.meta.gef.editors.models.ViewDiagram;

public class ScreenGenerator {

	private IDGenerator idGenerator;
	private AuroraComponent2CompositMap a2Map;
	private ScriptGenerator scriptGenerator;
	private CompositeMap datasets;

	private Map<Dataset, String> datasetMap = new HashMap<Dataset, String>();
	private IProject project;

	public ScreenGenerator(IProject project) {
		this.project = project;
	}

	public String genFile(String header, ViewDiagram view)
			throws TemplateNotBindedException {
		String bindTemplate = view.getBindTemplate();
		if (bindTemplate == null || "".equals(bindTemplate))
			throw new TemplateNotBindedException();
		idGenerator = new IDGenerator(view);
		a2Map = new AuroraComponent2CompositMap(this);
		CompositeMap screen = AuroraComponent2CompositMap
				.createScreenCompositeMap();
		CompositeMap viewMap = a2Map.toCompositMap(view);
		CompositeMap script = viewMap.createChild("script");
		datasets = createCompositeMap("dataSets");
		CompositeMap screenBody = createCompositeMap("screenBody");
		screen.addChild(viewMap);
		viewMap.addChild(datasets);
		viewMap.addChild(screenBody);

		scriptGenerator = new ScriptGenerator(this, script);
		fill(view, screenBody);
		fillLinks(viewMap);

		script.setText(scriptGenerator.getScript());
		String xml = header + screen.toXML();
		return xml;
	}

	private void fill(Container container, CompositeMap containerMap) {
		List<AuroraComponent> children = container.getChildren();
		for (AuroraComponent ac : children) {
			CompositeMap childMap = a2Map.toCompositMap(ac);
			if (childMap == null) {
				continue;
			}

			if (ac instanceof GridColumn && container instanceof Grid) {
				CompositeMap columns = getColumns(containerMap);
				columns.addChild(childMap);
			} else if (ac instanceof TabItem && container instanceof TabFolder) {
				CompositeMap tabs = containerMap.getChild("tabs");
				if (tabs == null) {
					tabs = createCompositeMap("tabs");
					containerMap.addChild(tabs);
				}
				tabs.addChild(childMap);
			} else {
				containerMap.addChild(childMap);
			}
			if (ac instanceof TabItem){
				fill(((TabItem) ac).getBody(), childMap);
			}
			if (ac instanceof GridColumn) {
				genColumnEditor((GridColumn) ac, childMap, containerMap);
				genColumnRenderer((GridColumn) ac, childMap, containerMap);
			}
			if (ac instanceof Button) {
				fillButton((Button) ac, childMap);
			}
			if (ac instanceof Container) {
				fill((Container) ac, childMap);
				fillDatasets((Container) ac);
			}
			if (ac instanceof DatasetBinder) {
				bindDataset(container, ac, childMap, datasets);
			}
			if (ac instanceof IDatasetFieldDelegate) {
				fillDataset(findDataset(ac.getParent()), datasets, ac);
			}

			if (isLov(ac)) {
				a2Map.doLovMap(findDataset(ac.getParent()), ac, childMap);
			}

		}
	}

	private boolean isLov(AuroraComponent ac) {
		if (ac instanceof GridColumn) {
			return Input.LOV.equals(((GridColumn) ac).getEditor());
		}
		if (ac instanceof Input) {
			return Input.LOV.equals(ac.getType());
		}
		return false;
	}

	private void genColumnRenderer(GridColumn ac, CompositeMap childMap,
			CompositeMap containerMap) {
		Renderer renderer = ac.getRenderer();
		String functionName = this.scriptGenerator.genRenderer(renderer);
		if (null == functionName || "".equals(functionName))
			return;
		childMap.put(GridColumn.RENDERER, functionName);
	}

	private void fillLinks(CompositeMap view) {
		Map<Object, String> linkIDs = scriptGenerator.getLinkIDs();
		Set<Object> keySet = linkIDs.keySet();
		for (Object bc : keySet) {
			String openPath = "";
			if (bc instanceof Renderer) {
				openPath = ((Renderer) bc).getOpenPath();
			} else if (bc instanceof ButtonClicker) {
				openPath = ((ButtonClicker) bc).getOpenPath();
			} else {
				continue;
			}
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
		if (null == functionName || "".equals(functionName))
			return;
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
			colmunMap.put(GridColumn.EDITOR, editorMap.get("id"));
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

	public CompositeMap fillDatasets(Container ac) {
		Dataset dataset = findDataset(ac);
		return fillDatasets(datasets, dataset);
	}

	protected CompositeMap fillDatasets(Dataset dataset) {
		return fillDatasets(datasets, dataset);
	}

	private CompositeMap fillDatasets(CompositeMap datasets, Dataset dataset) {
		if (dataset == null)
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
				Container target = qs.getTarget();
				if (target != null) {
					Dataset ds = this.findDataset(target);
					Object qds = this.fillDatasets(ds).get("id");
					rds.put(ResultDataSet.QUERY_DATASET, qds.toString());
				} else {
					rds.put("loadData", true);
				}
			}

			datasets.addChild(rds);
			return rds;
		}
		return dsMap;
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
		if (ac.getName() == null || "".equals(ac.getName()))
			return;
		CompositeMap dsMap = fillDatasets(datasets, dataset);
		if (dsMap == null) {
			return;
		}
		CompositeMap fields = dsMap.getChild("fields");
		if (fields == null) {
			fields = createCompositeMap("fields");
			dsMap.addChild(fields);
		}

		CompositeMap field = fields.getChildByAttrib(AuroraComponent.NAME,
				ac.getPropertyValue(AuroraComponent.NAME));
		if (field == null) {
			field = createCompositeMap("field");
			fields.addChild(field);
			field.put(AuroraComponent.NAME,
					ac.getPropertyValue(AuroraComponent.NAME));
		}
		this.a2Map.bindDatasetField(field, dataset, ac);

	}

	private Dataset findDataset(Container container) {
		if (container == null)
			return null;
		boolean useParentBM = isUseParentBM(container);
		if (useParentBM) {
			return findDataset(container.getParent());
		}
		Dataset dataset = container.getDataset();
		return dataset;
	}

	private boolean isUseParentBM(Container container) {
		if (Container.SECTION_TYPE_QUERY.equals(container.getSectionType())
				|| Container.SECTION_TYPE_RESULT.equals(container
						.getSectionType())) {
			return false;
		}
		return true;
	}

	public IDGenerator getIdGenerator() {
		return idGenerator;
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

}
