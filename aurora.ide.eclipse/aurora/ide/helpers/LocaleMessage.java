package aurora.ide.helpers;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class LocaleMessage {

	final static String resource = "aurora";
	private static ResourceBundle resourceBundle = getResourceBundle();
	/**
	 * Gets a string from the resource bundle. We don't want to crash because of
	 * a missing String. Returns the key if not found.
	 */
	public static String getString(String key) {
		try {
			return LocaleMessage.resourceBundle.getString(key);
		} catch (MissingResourceException e) {
			return key;
		} catch (NullPointerException e) {
			return "!" + key + "!";
		}
	}

	public static ResourceBundle getResourceBundle() {
		Locale locale = Locale.getDefault();
		LocaleMessage.resourceBundle = ResourceBundle.getBundle(LocaleMessage.resource, locale);
		if (LocaleMessage.resourceBundle != null)
			return LocaleMessage.resourceBundle;
		LocaleMessage.resourceBundle = ResourceBundle.getBundle(LocaleMessage.resource);
		return LocaleMessage.resourceBundle;
	}
}
