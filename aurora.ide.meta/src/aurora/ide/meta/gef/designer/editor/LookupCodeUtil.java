package aurora.ide.meta.gef.designer.editor;

import uncertain.composite.CompositeMap;

public class LookupCodeUtil {
	public static boolean isCode(Object o) {
		if (!(o instanceof CompositeMap))
			return false;
		CompositeMap m = (CompositeMap) o;
		return "code".equalsIgnoreCase(m.getName());
	}

	public static boolean isValue(Object o) {
		if (!(o instanceof CompositeMap))
			return false;
		CompositeMap m = (CompositeMap) o;
		return "value".equalsIgnoreCase(m.getName());
	}

	public static String getCodeName(Object code) {
		if (!isCode(code))
			return "<invalid code>";
		CompositeMap m = (CompositeMap) code;
		String codeName = m.getString("code");
		if (codeName == null)
			return "<missing code name>";
		return codeName.toUpperCase();
	}

	public static String getValueAsString(Object o) {
		if (!isValue(o))
			return "<invalid value>";
		CompositeMap m = (CompositeMap) o;
		return m.getString("key") + " : [" + m.getString("zhs") + ","
				+ m.getString("us") + "]";
	}

	public static CompositeMap getCode(Object o) {
		if (isCode(o))
			return (CompositeMap) o;
		if (isValue(o))
			return ((CompositeMap) o).getParent();
		return null;
	}
}
