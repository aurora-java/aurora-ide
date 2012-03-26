package aurora.ide.meta.gef.editors.models.io;

import java.util.List;

import uncertain.composite.CommentCompositeMap;
import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.ViewDiagram;

public class ModelIOManager {

	public static final String NS_CHILDLIST = "cl";
	public static final String NS_CHILDLIST_URI = "http://meta.editor/childlist";
	public static final String BIND_TEMPLATE = "bindTemplate";

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
		@SuppressWarnings("unchecked")
		List<CompositeMap> list = root.getChildsNotNull();
		for (CompositeMap map : list) {
			IOHandler ioh = IOHandlerUtil.getHandler(map);
			dia.addChild(ioh.fromCompositeMap(map, mic));
		}
		recoverReference(dia);
		return dia;
	}

	private void recoverReference(ViewDiagram dia) {
		for (ReferenceDecl rd : mic.refDeclList) {
			rd.arg = mic.markMap.get(rd.markid);
			rd.run();
		}
	}
}
