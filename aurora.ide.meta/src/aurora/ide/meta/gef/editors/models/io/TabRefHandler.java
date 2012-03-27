package aurora.ide.meta.gef.editors.models.io;

import java.util.List;

import uncertain.composite.CommentCompositeMap;
import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.link.Parameter;
import aurora.ide.meta.gef.editors.models.link.TabRef;

public class TabRefHandler extends DefaultIOHandler {
	public static final String URL = "url";
	public static final String TABITEM = "tabitem";

	@Override
	protected AuroraComponent getNewObject(CompositeMap map) {
		return new TabRef();
	}

	@Override
	protected void storeSimpleAttribute(CompositeMap map, AuroraComponent ac) {
		super.storeSimpleAttribute(map, ac);
		TabRef ref = (TabRef) ac;
		map.put(URL, ref.getUrl());
	}

	@Override
	protected void storeComplexAttribute(CompositeMap map, AuroraComponent ac) {
		super.storeComplexAttribute(map, ac);
		TabRef ref = (TabRef) ac;
		// ReferenceHandler rh = new ReferenceHandler();
		// CompositeMap tiMap = rh.toCompositeMap(ref.getTabItem(), mic);
		// tiMap.put(ReferenceHandler.COMMENT, TABITEM);
		// map.addChild(tiMap);
		map.addChild(getParameterMap(ref, mic));
	}

	private CompositeMap getParameterMap(TabRef ref, ModelIOContext mic) {
		CompositeMap pMap = new CommentCompositeMap(RendererHandler.PARAMETERS);
		ParameterHandler ph = new ParameterHandler();
		for (Parameter p : ref.getParameters()) {
			pMap.addChild(ph.toCompositeMap(p, mic));
		}
		return pMap;
	}

	@Override
	protected void restoreSimpleAttribute(AuroraComponent ac, CompositeMap map) {
		super.restoreSimpleAttribute(ac, map);
		TabRef ref = (TabRef) ac;
		ref.setUrl(map.getString(URL));
	}

	@Override
	protected void restoreComplexAttribute(AuroraComponent ac, CompositeMap map) {
		super.restoreComplexAttribute(ac, map);
		TabRef ref = (TabRef) ac;
		// CompositeMap m = getMap(map, ReferenceHandler.NS_PREFIX,
		// ReferenceHandler.COMMENT, TABITEM);
		// if (m != null) {
		// String mid = m.getString(ReferenceHandler.REF_ID);
		// TabItem ti = (TabItem) mic.markMap.get(mid);
		// if (ti != null) {
		// ref.setTabItem(ti);
		// } else {
		// ReferenceDecl rd = new ReferenceDecl(mid, ref, "setTabItem",
		// TabItem.class);
		// mic.refDeclList.add(rd);
		// }
		// }
		//
		restoreParameters(ref, map, mic);
	}

	private void restoreParameters(TabRef ref, CompositeMap map,
			ModelIOContext mic) {
		CompositeMap psMap = map.getChild(RendererHandler.PARAMETERS);
		if (psMap == null)
			return;
		ParameterHandler ph = new ParameterHandler();
		@SuppressWarnings("unchecked")
		List<CompositeMap> list = psMap.getChildsNotNull();
		for (CompositeMap m : list) {
			Parameter p = (Parameter) ph.fromCompositeMap(m, mic);
			ref.addParameter(p);
		}
	}
}
