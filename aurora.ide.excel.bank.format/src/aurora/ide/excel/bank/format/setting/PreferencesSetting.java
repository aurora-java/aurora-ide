package aurora.ide.excel.bank.format.setting;

import java.io.IOException;

import org.eclipse.jface.preference.IPreferenceStore;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
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
		if (xml == null || "".equals(xml))
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
		CompositeMap compositeMap = new CompositeMap("xls_setting");

		CompositeMap m = defautIDXMap(xls_code);
		compositeMap.addChild(m);

		CompositeMap dat = new CompositeMap("dat");
		dat.addChild(createMap("start_row", "数据开始行号", "6"));
		dat.addChild(createMap("target_code", "指标代码列", "1"));
		CompositeMap createMap = createMap("data", "数据值列", "3");
//		createMap.put("idx", "0");
		dat.addChild(createMap);
		compositeMap.addChild(dat);
		return compositeMap;
	}

	public static CompositeMap defautIDXMap(String xls_code) {
		CompositeMap m = new CompositeMap("idx");
		// m.addChild(createMap("key_word_code", "关键字代码", ""));
		m.addChild(createMap("xls_code", "表单代码", xls_code));
		m.addChild(createMap("org_code", "机构类代码", "c302"));
		m.addChild(createMap("area_code", "地区代码", "1201070"));
		m.addChild(createMap("data_type", "数据属性", "1"));
		m.addChild(createMap("currency_type", "币种", "CNY0001"));
		m.addChild(createMap("unit", "单位", "5"));
		m.addChild(createMap("b_data_flag", "业务数据标志", "1"));
		m.addChild(createMap("num_type", "数值型类型", "1"));
		m.addChild(createMap("std_org_code", "标准化机构编码", "C5003912000016"));
		m.put("desc", "默认");
		return m;
	}

	static public CompositeMap createMap(String name, String text, String value) {
		CompositeMap m = new CompositeMap(name);
		m.put("name", name);
		m.put("value", value);
		m.setText(text);
		return m;
	}

}
