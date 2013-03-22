package aurora.ide.meta.gef.editors.models.io;

import java.util.Arrays;
import java.util.List;

import uncertain.composite.CompositeMap;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.InitProcedure;
import aurora.ide.meta.gef.editors.models.ModelQuery;
import aurora.ide.meta.gef.editors.models.ViewDiagram;

public class ModelIOManager extends DefaultIOHandler {

	public static final String NS_CHILDLIST = "cl";
	public static final String NS_CHILDLIST_URI = "http://meta.editor/childlist";
	public static final String BIND_TEMPLATE = "bindTemplate";
	public static final String INIT_PROCEDURE = "init-procedure";
	public static final String TEMPLATE_TYPE = "templatetype";
	public static final String UNBINDMODELS = "unBindModels";

	ModelIOContext mic = new ModelIOContext();

	private ModelIOManager() {
	}

	public static ModelIOManager getNewInstance() {
		return new ModelIOManager();
	}

	public CompositeMap toCompositeMap(ViewDiagram diagram) {
		CompositeMap root = toCompositeMap(diagram, mic);
		return root;
	}

	public ViewDiagram fromCompositeMap(CompositeMap root) {
		if (!ViewDiagram.class.getSimpleName().equalsIgnoreCase(root.getName()))
			return null;
		ViewDiagram dia = (ViewDiagram) fromCompositeMap(root, mic);
		recoverReference(dia);
		return dia;
	}

	private void restoreInitProcedure(ViewDiagram dia, CompositeMap imMap) {
		@SuppressWarnings("unchecked")
		List<CompositeMap> list = imMap.getChildsNotNull();
		ModelQueryHandler imh = new ModelQueryHandler();
		for (CompositeMap m : list) {
			ModelQuery im = (ModelQuery) imh.fromCompositeMap(m, mic);
			dia.addModelQuery(im);
		}
	}

	private void recoverReference(ViewDiagram dia) {
		for (ReferenceDecl rd : mic.refDeclList) {
			rd.arg = mic.markMap.get(rd.markid);
			rd.run();
		}
	}

	@Override
	protected void storeSimpleAttribute(CompositeMap map, AuroraComponent ac) {
		super.storeSimpleAttribute(map, ac);
		ViewDiagram diagram = (ViewDiagram) ac;
		map.put(BIND_TEMPLATE, diagram.getBindTemplate());
		map.put(TEMPLATE_TYPE, diagram.getTemplateType());
		// about UnBindModels
		List<String> list = diagram.getUnBindModels();
		String str = StringUtil.join(list, ";");
		map.put(UNBINDMODELS, str);
	}

	@Override
	protected void storeComplexAttribute(CompositeMap map, AuroraComponent ac) {
		super.storeComplexAttribute(map, ac);
		ViewDiagram diagram = (ViewDiagram) ac;
		InitProcedure ip = diagram.getInitProcedure();
		if (ip != null) {
			List<ModelQuery> imList = ip.getModelQuerys();
			if (imList.size() > 0) {
				CompositeMap imMap = new CommentCompositeMap(INIT_PROCEDURE);
				ModelQueryHandler imhandler = new ModelQueryHandler();
				for (ModelQuery im : imList) {
					CompositeMap m = imhandler.toCompositeMap(im, mic);
					imMap.addChild(m);
				}
				map.addChild(imMap);
			}
		}
	}

	@Override
	protected void restoreSimpleAttribute(AuroraComponent ac, CompositeMap map) {
		super.restoreSimpleAttribute(ac, map);
		ViewDiagram dia = (ViewDiagram) ac;

		String tpl = map.getString(BIND_TEMPLATE);
		dia.setBindTemplate(tpl);
		dia.setTemplateType(map.getString(TEMPLATE_TYPE));
		// about UnBindModels
		String str = map.getString(UNBINDMODELS);
		dia.getUnBindModels().addAll(Arrays.asList(StringUtil.split(str, ";")));
	}

	@Override
	protected void restoreComplexAttribute(AuroraComponent ac, CompositeMap map) {
		super.restoreComplexAttribute(ac, map);
		CompositeMap initProcedureMap = map.getChild(INIT_PROCEDURE);
		if (initProcedureMap != null) {
			restoreInitProcedure((ViewDiagram) ac, initProcedureMap);
		}
	}

	@Override
	protected AuroraComponent getNewObject(CompositeMap map) {
		return new ViewDiagram();
	}
}
