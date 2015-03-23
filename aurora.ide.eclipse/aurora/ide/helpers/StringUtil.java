package aurora.ide.helpers;

public class StringUtil {
	public static boolean isBlank(Object s) {
		return null == s || "".equals(s);
	}


    public static StringBuffer concat(String string1, String string2,
            String string3) {
        StringBuffer buffer = new StringBuffer(string1);
        buffer.append(string2);
        buffer.append(string3);
        return buffer;
    }

    public static StringBuffer concat(String string1, String string2,
            String string3, String string4) {
        StringBuffer buffer = concat(string1, string2, string3);
        buffer.append(string4);
        return buffer;
    }

    public static StringBuffer concat(String string1, String string2,
            String string3, String string4, String string5) {
        StringBuffer buffer = concat(string1, string2, string3, string4);
        buffer.append(string5);
        return buffer;
    }

    public static StringBuffer concat(String string1, String string2,
            String string3, String string4, String string5, String string6) {
        StringBuffer buffer = concat(string1, string2, string3, string4,
            string5);
        buffer.append(string6);
        return buffer;
    }




}
