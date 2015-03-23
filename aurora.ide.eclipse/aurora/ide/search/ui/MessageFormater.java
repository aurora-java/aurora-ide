package aurora.ide.search.ui;

import java.text.MessageFormat;

public class MessageFormater {

	public static String format(String message, Object object) {
		return MessageFormat.format(message, new Object[] { object });
	}

	public static String format(String message, Object[] objects) {
		return MessageFormat.format(message, objects);
	}

	private MessageFormater() {
		// Not for instantiation
	}
}