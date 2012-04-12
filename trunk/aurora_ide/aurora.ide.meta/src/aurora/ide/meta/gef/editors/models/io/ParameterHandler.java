package aurora.ide.meta.gef.editors.models.io;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.link.Parameter;

public class ParameterHandler extends DefaultIOHandler {

	private static final String NAME = "name";
	private static final String VALUE = "value";
	private static final String CONTAINER = "container";

	@Override
	protected void storeSimpleAttribute(CompositeMap map, AuroraComponent ac) {
		super.storeSimpleAttribute(map, ac);
		Parameter p = (Parameter) ac;
		map.put(NAME, p.getName());
		map.put(VALUE, p.getValue());

	}

	@Override
	protected void storeComplexAttribute(CompositeMap map, AuroraComponent ac) {
		super.storeComplexAttribute(map, ac);
		Parameter p = (Parameter) ac;
		ReferenceHandler rh = new ReferenceHandler();
		Container container2 = p.getContainer();
		if (container2 != null) {
			CompositeMap cMap = rh.toCompositeMap(container2, mic);
			cMap.put(ReferenceHandler.COMMENT, CONTAINER);
			map.addChild(cMap);
		}
	}

	@Override
	protected void restoreSimpleAttribute(AuroraComponent ac, CompositeMap map) {
		super.restoreSimpleAttribute(ac, map);
		Parameter p = (Parameter) ac;
		p.setName(map.getString(NAME));
		p.setValue(map.getString(VALUE));
	}

	@Override
	protected void restoreComplexAttribute(AuroraComponent ac, CompositeMap map) {
		super.restoreComplexAttribute(ac, map);
		Parameter p = (Parameter) ac;
		CompositeMap m = getMap(map, ReferenceHandler.NS_PREFIX,
				ReferenceHandler.COMMENT, CONTAINER);
		if (m != null) {
			String mid = m.getString(ReferenceHandler.REF_ID);
			Container cont = (Container) mic.markMap.get(mid);
			if (cont != null) {
				p.setContainer(cont);
			} else {
				ReferenceDecl rd = new ReferenceDecl(mid, p, "setContainer",
						Container.class);
				mic.refDeclList.add(rd);
			}
		}
	}

	@Override
	protected AuroraComponent getNewObject(CompositeMap map) {
		return new Parameter();
	}
}
