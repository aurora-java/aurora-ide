package aurora.ide.meta.js.object;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import uncertain.composite.CompositeMap;

public class CompositeMapBuilder {
	public static final String aurora_uri = "http://www.aurora-framework.org/application";

	public static CompositeMapObject newMapping(Context cx, Scriptable thisObj,
			Object[] args, Function funObj) {
		CompositeMap mapping = new CompositeMap("a", aurora_uri, "mapping");
		for (int i = 0; i < args.length / 2; i++) {
			CompositeMap m = new CompositeMap("a", aurora_uri, "map");
			m.put("from", args[i * 2]);
			m.put("to", args[i * 2 + 1]);
			mapping.addChild(m);
		}
		return (CompositeMapObject) cx.newObject(thisObj,
				CompositeMapObject.CLASS_NAME, new Object[] { mapping });
	}
}
