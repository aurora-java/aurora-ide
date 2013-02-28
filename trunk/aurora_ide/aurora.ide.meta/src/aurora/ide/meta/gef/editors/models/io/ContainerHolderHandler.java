package aurora.ide.meta.gef.editors.models.io;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.ContainerHolder;

public class ContainerHolderHandler extends DefaultIOHandler {

	@Override
	protected AuroraComponent getNewObject(CompositeMap map) {
		return new ContainerHolder();
	}

	@Override
	protected void storeSimpleAttribute(CompositeMap map, AuroraComponent ac) {
		super.storeSimpleAttribute(map, ac);
		ContainerHolder ch = (ContainerHolder) ac;
		map.put("filter", ch.getContainerType());
	}

	@Override
	protected void restoreSimpleAttribute(AuroraComponent ac, CompositeMap map) {
		super.restoreSimpleAttribute(ac, map);
		ContainerHolder ch = (ContainerHolder) ac;
		ch.setContainerType(map.getString("filter"));
	}

	@Override
	protected void storeComplexAttribute(CompositeMap map, AuroraComponent ac) {
		super.storeComplexAttribute(map, ac);
		ContainerHolder ch = (ContainerHolder) ac;
		AuroraComponent owner = ch.getOwner();
		ReferenceHandler rh = new ReferenceHandler();
		if (owner != null) {
			CompositeMap ownerMap = rh.toCompositeMap(owner, mic);
			ownerMap.put(ReferenceHandler.COMMENT, "owner");
			map.addChild(ownerMap);
		}
		Container target = ch.getTarget();
		if (target != null) {
			CompositeMap targetMap = rh.toCompositeMap(target, mic);
			targetMap.put(ReferenceHandler.COMMENT, "target");
			map.addChild(targetMap);
		}
	}

	@Override
	protected void restoreComplexAttribute(AuroraComponent ac, CompositeMap map) {
		super.restoreComplexAttribute(ac, map);
		ContainerHolder ch = (ContainerHolder) ac;
		CompositeMap ownerMap = getMap(map, ReferenceHandler.NS_PREFIX,
				ReferenceHandler.COMMENT, "owner");
		if (ownerMap != null) {
			String ownerid = ownerMap.getString(ReferenceHandler.REF_ID);
			if (mic.markMap.get(ownerid) != null) {
				ch.setOwner(mic.markMap.get(ownerid));
			} else {
				ReferenceDecl rd = new ReferenceDecl(ownerid, ac, "setOwner",
						AuroraComponent.class);
				mic.refDeclList.add(rd);
			}
		}
		CompositeMap targetMap = getMap(map, ReferenceHandler.NS_PREFIX,
				ReferenceHandler.COMMENT, "target");
		if (targetMap != null) {
			String targetid = targetMap.getString(ReferenceHandler.REF_ID);
			ReferenceDecl rd = new ReferenceDecl(targetid, ac, "setTarget",
					Container.class);
			mic.refDeclList.add(rd);
		}
	}

}
