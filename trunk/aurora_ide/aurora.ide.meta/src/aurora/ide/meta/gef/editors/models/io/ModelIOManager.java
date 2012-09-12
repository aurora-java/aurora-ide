package aurora.ide.meta.gef.editors.models.io;

import java.util.Arrays;
import java.util.List;

import uncertain.composite.CompositeMap;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.InitProcedure;
import aurora.ide.meta.gef.editors.models.ModelQuery;
import aurora.ide.meta.gef.editors.models.ViewDiagram;

public class ModelIOManager {

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
		CompositeMap root = new CommentCompositeMap();
		root.setName(diagram.getClass().getSimpleName());
		root.put(BIND_TEMPLATE, diagram.getBindTemplate());
		root.put(TEMPLATE_TYPE, diagram.getTemplateType());
		// about UnBindModels
		List<String> list = diagram.getUnBindModels();
		String str = StringUtil.join(list, ";");
		root.put(UNBINDMODELS, str);
		// end UnBindModels
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
				root.addChild(imMap);
			}
		}
		for (AuroraComponent ac : diagram.getChildren()) {
			IOHandler ioh = IOHandlerUtil.getHandler(ac);
			root.addChild(ioh.toCompositeMap(ac, mic));
		}
		return root;
	}

	public ViewDiagram fromCompositeMap(CompositeMap root) {
		if (!ViewDiagram.class.getSimpleName().equalsIgnoreCase(root.getName()))
			return null;
		ViewDiagram dia = new ViewDiagram();

		String tpl = root.getString(BIND_TEMPLATE);
		dia.setBindTemplate(tpl);
		dia.setTemplateType(root.getString(TEMPLATE_TYPE));
		// about UnBindModels
		String str = root.getString(UNBINDMODELS);
		dia.getUnBindModels().addAll(Arrays.asList(StringUtil.split(str, ";")));
		// end UnBindModels
		@SuppressWarnings("unchecked")
		List<CompositeMap> list = root.getChildsNotNull();
		for (CompositeMap map : list) {
			if (map.getName().equals(INIT_PROCEDURE)) {
				restoreInitProcedure(dia, map);
				continue;
			}
			IOHandler ioh = IOHandlerUtil.getHandler(map);
			dia.addChild(ioh.fromCompositeMap(map, mic));
		}
		recoverReference(dia);
		return dia;
	}

	private void restoreInitProcedure(ViewDiagram dia, CompositeMap imMap) {
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
}
