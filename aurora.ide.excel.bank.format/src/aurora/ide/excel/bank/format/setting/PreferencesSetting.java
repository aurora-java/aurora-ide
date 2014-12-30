package aurora.ide.excel.bank.format.setting;

import java.io.IOException;

import org.eclipse.jface.preference.IPreferenceStore;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import aurora.excel.model.format.Formater;
import aurora.ide.excel.bank.format.Activator;

public class PreferencesSetting {

	static public void saveXLSSetting(String xls_code, CompositeMap map) {
		IPreferenceStore preferenceStore = Activator.getDefault()
				.getPreferenceStore();
		preferenceStore.setValue(xls_code, map.toXML());
	}

	static public CompositeMap loadXLSSetting(String xls_code) {
		IPreferenceStore preferenceStore = Activator.getDefault()
				.getPreferenceStore();
		String xml = preferenceStore.getString(xls_code);
		if (xml == null || "".equals(xml)) //$NON-NLS-1$
			return defaultXLSSetting(xls_code);
		try {
			CompositeMap map = CompositeLoader.createInstanceForOCM()
					.loadFromString(xml);
			return map;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return defaultXLSSetting(xls_code);
	}

	static public CompositeMap defaultXLSSetting(String xls_code) {
		CompositeMap compositeMap = new CompositeMap("xls_setting"); //$NON-NLS-1$

		CompositeMap m = defautIDXMap(xls_code);
		compositeMap.addChild(m);

		CompositeMap dat = new CompositeMap("dat"); //$NON-NLS-1$
		dat.addChild(createMap("start_row", Messages.PreferencesSetting_4, "6")); //$NON-NLS-1$ //$NON-NLS-3$
		dat.addChild(createMap("target_code", Messages.PreferencesSetting_7, "2")); //$NON-NLS-1$ //$NON-NLS-3$
		CompositeMap createMap = createMap("data", Messages.PreferencesSetting_10, "3"); //$NON-NLS-1$ //$NON-NLS-3$
//		createMap.put("idx", "0");
		dat.addChild(createMap);
		compositeMap.addChild(dat);
		return compositeMap;
	}

	public static CompositeMap defautIDXMap(String xls_code) {
		CompositeMap m = new CompositeMap("idx"); //$NON-NLS-1$
		// m.addChild(createMap("key_word_code", "关键字代码", ""));
		m.addChild(createMap("xls_code", Messages.PreferencesSetting_14, xls_code)); //$NON-NLS-1$
		m.addChild(createMap("org_code", Messages.PreferencesSetting_16, Formater.org_code)); //$NON-NLS-1$ //$NON-NLS-3$
		m.addChild(createMap("area_code", Messages.PreferencesSetting_19, Formater.area_code)); //$NON-NLS-1$ //$NON-NLS-3$
		m.addChild(createMap("data_type", Messages.PreferencesSetting_22, Formater.data_type)); //$NON-NLS-1$ //$NON-NLS-3$
		m.addChild(createMap("currency_type", Messages.PreferencesSetting_25, Formater.currency_type)); //$NON-NLS-1$ //$NON-NLS-3$
		m.addChild(createMap("unit", Messages.PreferencesSetting_28, Formater.unit)); //$NON-NLS-1$ //$NON-NLS-3$
		m.addChild(createMap("b_data_flag", Messages.PreferencesSetting_31, Formater.b_data_flag)); //$NON-NLS-1$ //$NON-NLS-3$
		m.addChild(createMap("num_type", Messages.PreferencesSetting_34, Formater.num_type)); //$NON-NLS-1$ //$NON-NLS-3$
		m.addChild(createMap("std_org_code", Messages.PreferencesSetting_37, Formater.std_org_code)); //$NON-NLS-1$ //$NON-NLS-3$
		m.put("desc", Messages.PreferencesSetting_40); //$NON-NLS-1$
		return m;
	}

	static public CompositeMap createMap(String name, String text, String value) {
		CompositeMap m = new CompositeMap(name);
		m.put("name", name); //$NON-NLS-1$
		m.put("value", value); //$NON-NLS-1$
		m.setText(text);
		return m;
	}

}
