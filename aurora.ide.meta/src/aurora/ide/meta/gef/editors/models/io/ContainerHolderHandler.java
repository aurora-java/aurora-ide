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
		map.put("owner", owner.markid);
		Container target = ch.getTarget();
		map.put("target", target.markid);
	}

	@Override
	protected void restoreComplexAttribute(AuroraComponent ac, CompositeMap map) {
		super.restoreComplexAttribute(ac, map);
		ContainerHolder ch = (ContainerHolder) ac;
		String ownerid = map.getString("owner");
		if (mic.markMap.get(ownerid) != null) {
			ch.setOwner(mic.markMap.get(ownerid));
		} else {
			ReferenceDecl rd = new ReferenceDecl(ownerid, ac, "setOwner",
					AuroraComponent.class);
			mic.refDeclList.add(rd);
		}
		ReferenceDecl rd = new ReferenceDecl(map.getString("target"), ac,
				"setTarget", Container.class);
		mic.refDeclList.add(rd);
	}

}
