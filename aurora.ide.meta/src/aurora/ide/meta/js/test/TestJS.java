package aurora.ide.meta.js.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.mozilla.javascript.Scriptable;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import aurora.ide.api.composite.map.CommentCompositeLoader;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.meta.js.ScriptEngine;
import aurora.ide.meta.js.object.CompositeMapObject;
import aurora.ide.prototype.freemarker.test.FMTester;

public class TestJS {

	static String loadJS() {
		InputStream resourceAsStream = null;
		try {
			resourceAsStream = TestJS.class.getResourceAsStream("test.js");
			InputStreamReader osw = new InputStreamReader(resourceAsStream);
			BufferedReader r = new BufferedReader(osw);
			StringBuilder b = new StringBuilder();
			String s = r.readLine();
			while (s != null) {
				// System.out.println(s);
				b.append(s);
				s = r.readLine();
				if (s == null)
					break;
				b.append("\n");
			}
			return b.toString();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (resourceAsStream != null)
				try {
					resourceAsStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return "";
	}

	static CompositeMap loadCompositeMap() {
		InputStream is = null;
		try {
			is = TestJS.class.getResourceAsStream("test2.uip");
			CompositeLoader parser = new CommentCompositeLoader();
			CompositeMap rootMap = parser.loadFromStream(is);
			rootMap.put("file_path", "a/b/c/d.uip");
			// rootMap.getChildByAttrib(attrib_key, attrib_value)
			// ModelIOManager mim = ModelIOManager.getNewInstance();
			// diagram = mim.fromCompositeMap(rootMap);
			return rootMap;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return new CompositeMap();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ScriptEngine en = new ScriptEngine();
		Scriptable scope = en.createScope(loadCompositeMap());
		try {
			CompositeMapObject eval = (CompositeMapObject) en.eval(loadJS(),
					scope);
			System.out.println(eval);
			//
			// String head = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n";
			String gen = new FMTester(eval.getData()).gen();
			CompositeMap map = CompositeMapUtil.loaderFromString(gen);
			System.out.println(map.toXML());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
