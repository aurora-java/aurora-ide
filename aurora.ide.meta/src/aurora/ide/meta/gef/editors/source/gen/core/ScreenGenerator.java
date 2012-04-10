package aurora.ide.meta.gef.editors.source.gen.core;

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
import aurora.ide.meta.gef.editors.models.InitModel;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.Renderer;
import aurora.ide.meta.gef.editors.models.TabFolder;
import aurora.ide.meta.gef.editors.models.TabItem;
import aurora.ide.meta.gef.editors.models.Toolbar;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.models.link.Parameter;
import aurora.ide.meta.gef.editors.models.link.TabRef;

public class ScreenGenerator {

	private IDGenerator idGenerator;
	private AuroraComponent2CompositMap a2Map;
	private ScriptGenerator scriptGenerator;

	private IProject project;
	private CompositeMap screenMap;
	private CompositeMap viewMap;
	private CompositeMap scriptMap;
	private CompositeMap datasetsMap;
	private CompositeMap screenBodyMap;
	private ViewDiagram viewDiagram;
	private DatasetGenerator datasetGenerator;

	public ScreenGenerator(IProject project) {
		this.project = project;
	}

	public String genFile(String header, ViewDiagram view)
			throws TemplateNotBindedException {
		String bindTemplate = view.getBindTemplate();
		boolean forCreate = view.isForCreate();
		if (!forCreate) {
			throw new TemplateNotBindedException();
		}

		if (bindTemplate == null || "".equals(bindTemplate))
			throw new TemplateNotBindedException();
		init(view);
		run(view);

		String xml = header + screenMap.toXML();
		return xml;
	}

	protected void run(ViewDiagram viewDiagram) {
		genInitProceduce();
		genDatasets();
		fill(viewDiagram, screenBodyMap);
		fillLinks(viewMap);
		scriptMap.setText(scriptGenerator.getScript());
	}

	private void genInitProceduce() {
		// <a:init-procedure>
		// <model-query model="acp.acp_req_maintain_init_hd_id"
		// rootpath="acp_req_hd_id"/>
		// <model-query model="acp.acp_req_update_init_header"
		// rootpath="acp_requisition_header"/>
		// </a:init-procedure>
		List<InitModel> initModels = this.viewDiagram.getInitModels();
		for (InitModel initModel : initModels) {
			CompositeMap procedureMap = this.getOrCreateChildMap(viewMap,
					"init-procedure");
			CompositeMap mq = procedureMap.getChildByAttrib("model-query",
					"model", initModel.getPath());
			if (mq == null) {
				mq = createCompositeMap("model-query");
				mq.put("model", initModel.getPath());
				IPath path = new Path(initModel.getPath());
				String p = this.idGenerator.genID(path.lastSegment(), 0);
				mq.put("rootpath", p);
				procedureMap.addChild(mq);
			}
		}
	}

	private String getRootPath(InitModel initModel) {
		CompositeMap procedureMap = this.viewMap.getChild("init-procedure");
		if (procedureMap == null)
			return null;
		CompositeMap mq = procedureMap.getChildByAttrib("model-query", "model",
				initModel.getPath());
		if (mq != null) {
			return mq.getString("rootpath");
		}
		return null;
	}

	private CompositeMap getOrCreateChildMap(CompositeMap parentMap, String name) {
		CompositeMap child = parentMap.getChild(name);
		if (child == null) {
			child = createCompositeMap(name);
			parentMap.addChild(child);
		}
		return child;
	}

	private void genDatasets() {
		List<Container> sectionContainers = viewDiagram.getSectionContainers(
				viewDiagram, Container.SECTION_TYPES);
		for (Container container : sectionContainers) {
			datasetGenerator.fillDatasets(container);
		}
	}

	protected void init(ViewDiagram view) {
		viewDiagram = view;
		idGenerator = new IDGenerator(view);
		a2Map = new AuroraComponent2CompositMap(this);
		screenMap = AuroraComponent2CompositMap.createScreenCompositeMap();
		viewMap = a2Map.toCompositMap(view);
		scriptMap = viewMap.createChild("script");
		datasetsMap = createCompositeMap("dataSets");
		screenBodyMap = createCompositeMap("screenBody");
		screenMap.addChild(viewMap);
		viewMap.addChild(datasetsMap);
		viewMap.addChild(screenBodyMap);
		scriptGenerator = new ScriptGenerator(this, scriptMap);
		datasetGenerator = new DatasetGenerator(this);
	}

	private void fill(Container container, CompositeMap containerMap) {
		List<AuroraComponent> children = container.getChildren();
		for (AuroraComponent ac : children) {
			CompositeMap childMap = a2Map.toCompositMap(ac);
			if (childMap == null) {
				continue;
			}

			if (ac instanceof GridColumn && container instanceof Grid) {
				CompositeMap columns = getOrCreateChildMap(containerMap,
						"columns");
				columns.addChild(childMap);
			} else if (ac instanceof TabItem && container instanceof TabFolder) {
				CompositeMap tabs = this.getOrCreateChildMap(containerMap,
						"tabs");
				tabs.addChild(childMap);
			} else {
				containerMap.addChild(childMap);
			}
			if (ac instanceof TabItem) {
				genTabRef((TabItem) ac, childMap, container, containerMap);
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
				// fillDatasets((Container) ac);
			}
			if (ac instanceof DatasetBinder) {
				datasetGenerator.bindDatasetMap(container, ac, childMap);
			}
			Dataset dataset = datasetGenerator.findDataset(ac.getParent());
			if (ac instanceof IDatasetFieldDelegate) {
				datasetGenerator.fillDatasetMap(dataset, ac);
			}

			if (isLov(ac)) {
				a2Map.doLovMap(dataset, ac, childMap);
			}
		}
	}

	private void genTabRef(TabItem ac, CompositeMap childMap,
			Container container, CompositeMap containerMap) {
		// <a:tab id="con_contract_headers_add_partner_tab"
		// prompt="CON_CONTRACT_PARTNER"
		// ref="${/request/@context_path}/modules/cont/public/con_contract_headers_add_partner_tab.screen?contract_header_id=${/parameter/@contract_header_id}"
		// width="100"/>

		TabRef tabRef = ac.getTabRef();
		if (tabRef != null) {
			String refUrl = getRefUrl(tabRef);
			childMap.put("ref", refUrl);
		}
	}

	private String getRefUrl(TabRef tabRef) {
		String url = tabRef.getUrl();
		List<Parameter> parameters = tabRef.getParameters();
		InitModel initModel = tabRef.getInitModel();
		// ${/model/head_info/record/@acp_req_type_code}
		String rootPath = initModel == null ? "${/parameter/@" : "${/model/"
				+ this.getRootPath(initModel) + "/record/@";
		if (url == null)
			return null;
		IPath path = new Path("${/request/@context_path}");
		path = path.append(url);
		path = path.removeFileExtension().addFileExtension("screen");
		for (int i = 0; i < parameters.size(); i++) {
			if (i == 0) {
				path = path.append("?");
			}
			path = path.append(parameters.get(i).getName());
			path = path.append("=");
			path = path.append(rootPath);
			path = path.append(parameters.get(i).getValue());
			path = path.append("}");

			if (i < parameters.size() - 1) {
				path = path.append("&");
			}
		}
		return path.toString();
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

	// public CompositeMap getColumns(CompositeMap gridMap) {
	// CompositeMap columns = gridMap.getChild("columns");
	// if (columns == null) {
	// columns = createCompositeMap("columns");
	// gridMap.addChild(columns);
	// }
	// return columns;
	// }

	public AuroraComponent2CompositMap getA2Map() {
		return a2Map;
	}

	public ScriptGenerator getScriptGenerator() {
		return scriptGenerator;
	}

	public CompositeMap getScreenMap() {
		return screenMap;
	}

	public CompositeMap getViewMap() {
		return viewMap;
	}

	public CompositeMap getScriptMap() {
		return scriptMap;
	}

	public CompositeMap getDatasetsMap() {
		return datasetsMap;
	}

	public CompositeMap getScreenBodyMap() {
		return screenBodyMap;
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

	public ViewDiagram getViewDiagram() {
		return viewDiagram;
	}

	public CompositeMap fillDatasets(Container container) {
		return this.datasetGenerator.fillDatasets(container);
	}

	public String findDatasetId(Container container) {
		return datasetGenerator.findDatasetId(container);
	}

	public CompositeMap fillDatasetsMap(Dataset ds) {
		return datasetGenerator.fillDatasetsMap(ds);
	}

}
