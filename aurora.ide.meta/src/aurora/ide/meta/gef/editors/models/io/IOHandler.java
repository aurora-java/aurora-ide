package aurora.ide.meta.gef.editors.models.io;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;

public interface IOHandler {
	CompositeMap toCompositeMap(AuroraComponent ac, ModelIOContext mic);

	AuroraComponent fromCompositeMap(CompositeMap map, ModelIOContext mic);
}
