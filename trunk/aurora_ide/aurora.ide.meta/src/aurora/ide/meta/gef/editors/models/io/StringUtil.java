package aurora.ide.meta.gef.editors.models.io;

import java.util.Arrays;
import java.util.List;

public class StringUtil {
	public static String join(List<?> list, String sep) {
		if (list == null)
			return null;
		int len = list.size();
		if (len == 0)
			return "";
		StringBuilder sb = new StringBuilder();
		sb.append(list.get(0));
		for (int i = 1; i < len; i++) {
			sb.append(sep);
			sb.append(list.get(i));
		}
		return sb.toString();
	}

	public static <T> String join(T[] arr, String sep) {
		if (arr == null)
			return null;
		return join(Arrays.asList(arr), sep);
	}

	public static String[] split(String string, String regex) {
		if (string == null)
			return new String[0];
		return string.split(regex);
	}
}
