package aurora.ide.meta.gef.editors.models.io;

import java.util.List;

import aurora.ide.api.composite.map.CommentCompositeMap;
import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;

public abstract class DefaultIOHandler implements IOHandler {
	public static final String CHILD_LIST = "CHILD_LIST";
	public static final String MARKID = "markid";
	public static final String SECTION_TYPE = "sectiontype";
	protected ModelIOContext mic;

	public CompositeMap toCompositeMap(AuroraComponent ac, ModelIOContext mic) {
		this.mic = mic;
		CompositeMap map = new CommentCompositeMap();
		map.setName(ac.getClass().getSimpleName());
		map.put(MARKID, ac.markid);
		map.put(AuroraComponent.NAME, ac.getName());
		storeSimpleAttribute(map, ac);
		storeComplexAttribute(map, ac);
		if (ac instanceof Container) {
			Container cont = (Container) ac;
			String sectionType = cont.getSectionType();
			if (sectionType != null) {
				map.put(SECTION_TYPE, sectionType);
			}
			storeChildren(map, cont);
		}
		return map;
	}

	/**
	 * 
	 * @param map
	 * @param ac
	 */
	protected void storeSimpleAttribute(CompositeMap map, AuroraComponent ac) {

	}

	/**
	 * 
	 * @param map
	 * @param ac
	 */
	protected void storeComplexAttribute(CompositeMap map, AuroraComponent ac) {

	}

	/**
	 * 
	 * @param map
	 * @param container
	 */
	protected void storeChildren(CompositeMap map, Container container) {
		List<AuroraComponent> list = container.getChildren();
		if (list.size() == 0)
			return;
		CompositeMap childList = new CommentCompositeMap(CHILD_LIST);
		for (AuroraComponent a : list) {
			if (!isStoreable(a))
				continue;
			IOHandler ioh = ExtIOHandlerUtil.getHandler(a);
			childList.addChild(ioh.toCompositeMap(a, mic));
		}
		map.addChild(childList);
	}

	protected boolean isStoreable(AuroraComponent ac) {
		return true;
	}

	protected abstract AuroraComponent getNewObject(CompositeMap map);

	public AuroraComponent fromCompositeMap(CompositeMap map, ModelIOContext mic) {
		this.mic = mic;
		AuroraComponent ac = getNewObject(map);
		ac.markid = map.getString(MARKID);
		mic.markMap.put(ac.markid, ac);
		ac.setName(notNull(map.getString(AuroraComponent.NAME)));
		restoreSimpleAttribute(ac, map);
		restoreComplexAttribute(ac, map);
		if (ac instanceof Container) {
			String sectionType = map.getString(SECTION_TYPE);
			Container cont = (Container) ac;
			if (sectionType != null && sectionType.length() > 0)
				cont.setSectionType(sectionType);
			restoreChildren(cont, map);
		}
		return ac;
	}

	/**
	 * 
	 * @param ac
	 * @param map
	 */
	protected void restoreSimpleAttribute(AuroraComponent ac, CompositeMap map) {

	}

	/**
	 * 
	 * @param ac
	 * @param map
	 */
	protected void restoreComplexAttribute(AuroraComponent ac, CompositeMap map) {

	}

	/**
	 * 
	 * @param container
	 * @param map
	 */
	protected void restoreChildren(Container container, CompositeMap map) {
		CompositeMap childList = map.getChild(CHILD_LIST);
		if (childList == null)
			return;
		@SuppressWarnings("unchecked")
		List<CompositeMap> list = childList.getChildsNotNull();
		for (CompositeMap m : list) {
			IOHandler ioh1 = ExtIOHandlerUtil.getHandler(m);
			container.addChild(ioh1.fromCompositeMap(m, mic));
		}
	}

	public final CompositeMap getMap(CompositeMap parMap, String prefix,
			String attrName, String value) {
		CompositeMap map = parMap.getChildByAttrib(attrName, value);
		if (map == null)
			return null;
		if (prefix == null) {
			if (map.getPrefix() == null)
				return map;
		} else if (prefix.equals(map.getPrefix()))
			return map;
		return null;
	}

	private String notNull(String s) {
		return s == null ? "" : s;
	}
}
