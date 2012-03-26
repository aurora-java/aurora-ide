package aurora.ide.meta.gef.editors.models.io;

import uncertain.composite.CommentCompositeMap;
import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;

public class ReferenceHandler implements IOHandler {
	public static final String COMMENT = "comment";
	public static final String NS_PREFIX = "ref";
	public static final String NS_URI = "meta.reference";
	public static final String REF_ID = "referenceid";

	public CompositeMap toCompositeMap(AuroraComponent ac, ModelIOContext mic) {
		CompositeMap map = new CommentCompositeMap(ac.getClass()
				.getSimpleName());
		map.setNameSpace(NS_PREFIX, NS_URI);
		map.put(REF_ID, ac.markid);
		return map;
	}

	public AuroraComponent fromCompositeMap(CompositeMap map, ModelIOContext mic) {
		return null;
	}

}
