package aurora.ide.meta.gef.editors.figures;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.TextStyle;

import aurora.ide.meta.gef.util.TextStyleUtil;

public class ResourceDisposer {
	private Map<String, Resource> resourceMap = new HashMap<String, Resource>();

	public void handleResource(String id, Resource r) {
		resourceMap.put(id, r);
	}

	public void disposeResource() {
		Collection<Resource> values = resourceMap.values();
		for (Resource r : values) {
			if (r instanceof TextLayout) {
				TextStyle[] styles = ((TextLayout) r).getStyles();
				for (TextStyle textStyle : styles) {
					TextStyleUtil.dispose(textStyle);
				}
			}
			if (r.isDisposed() == false) {
				r.dispose();
			}
		}
		resourceMap = new HashMap<String, Resource>();
	}

	public void disposeResource(String prop_id) {
		Resource textLayout = resourceMap.get(prop_id);
		if (textLayout != null) {
			if (textLayout instanceof TextLayout) {
				TextStyle[] styles = ((TextLayout) textLayout).getStyles();
				for (TextStyle textStyle : styles) {
					TextStyleUtil.dispose(textStyle);
				}
			}
			if (textLayout.isDisposed() == false) {
				textLayout.dispose();
			}
			resourceMap.remove(prop_id);
		}
	}
}
