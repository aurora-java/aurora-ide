package aurora.ide.excel.bank.format.setting;

import java.io.IOException;

import org.eclipse.jface.preference.IPreferenceStore;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import aurora.excel.model.format.Formater;
import aurora.ide.excel.bank.format.Activator;

public class CurrencySetting {
	static public void saveCurrency(CompositeMap map) {
		IPreferenceStore preferenceStore = Activator.getDefault()
				.getPreferenceStore();
		preferenceStore.setValue("CURRENCY_INFO", map.toXML()); //$NON-NLS-1$
	}

	static public CompositeMap loadCurrency() {
		IPreferenceStore preferenceStore = Activator.getDefault()
				.getPreferenceStore();
		String xml = preferenceStore.getString("CURRENCY_INFO"); //$NON-NLS-1$
		if (xml == null || "".equals(xml)) //$NON-NLS-1$
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
		CompositeMap compositeMap = new CompositeMap("CURRENCY_INFO"); //$NON-NLS-1$
		CompositeMap m = createMap(Formater.currency_type, Formater.unit); //$NON-NLS-1$ //$NON-NLS-2$
		compositeMap.addChild(m);
		return compositeMap;
	}

	public static CompositeMap createMap(String c_type, String unit) {
		CompositeMap m = new CompositeMap("c"); //$NON-NLS-1$
		m.put("currency_type", c_type); //$NON-NLS-1$
		m.put("unit", unit); //$NON-NLS-1$
		return m;
	}
}
