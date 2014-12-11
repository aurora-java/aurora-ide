package aurora.ide.excel.bank.format.setting;

import java.io.IOException;

import org.eclipse.jface.preference.IPreferenceStore;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import aurora.ide.excel.bank.format.Activator;

public class CurrencySetting {
	static public void saveCurrency(CompositeMap map) {
		IPreferenceStore preferenceStore = Activator.getDefault()
				.getPreferenceStore();
		preferenceStore.setValue("CURRENCY_INFO", map.toXML());
	}

	static public CompositeMap loadCurrency() {
		IPreferenceStore preferenceStore = Activator.getDefault()
				.getPreferenceStore();
		String xml = preferenceStore.getString("CURRENCY_INFO");
		if (xml == null || "".equals(xml))
			return defaultCurrency();
		try {
			CompositeMap map = CompositeLoader.createInstanceForOCM()
					.loadFromString(xml);
			return map;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return defaultCurrency();
	}

	static public CompositeMap defaultCurrency() {
		CompositeMap compositeMap = new CompositeMap("CURRENCY_INFO");
		CompositeMap m = createMap("CNY0001", "5");
		compositeMap.addChild(m);
		return compositeMap;
	}

	public static CompositeMap createMap(String c_type, String unit) {
		CompositeMap m = new CompositeMap("c");
		m.put("currency_type", c_type);
		m.put("unit", unit);
		return m;
	}
}
