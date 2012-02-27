package test;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * RunScript4: Execute scripts in an environment that includes the example
 * Counter class.
 * 
 * @author Norris Boyd
 */
public class RunScript {
	private static Script js;

	
	static	public String getTestJS(String path) throws IOException{
		InputStream resourceAsStream = null;
		
		resourceAsStream = RunScript.class.getClassLoader()
				.getResourceAsStream(path);
		InputStreamReader osw = new InputStreamReader(resourceAsStream);

		BufferedReader r = new BufferedReader(osw);
		StringBuilder b = new StringBuilder();
		String s = r.readLine();
		while (s != null) {
			// System.out.println(s);
			s = r.readLine();
			if (s == null)
				break;
			b.append(s);
			b.append("\n");
		}
		return b.toString();
	}
	
	public final static Script getScript() throws IOException {
		if (js == null) {
			
			Context cx = Context.enter();

			try {
				Scriptable scope = cx.initStandardObjects();
				

				// js = cx.compileReader(osw, "JS lint", 1, null);
				// Object ss = cx.evaluateReader(scope, osw, "queryFunction", 1,
				// null);

				cx.evaluateString(scope, getTestJS("test/a.js") , "aurora", 1, null);
				Object[] propertyIds = ScriptableObject.getPropertyIds(scope);
				Object[] ids = scope.getIds();
				for (Object i : ids) {
					System.out.println(i.toString());
				}
				
				Object object = scope.get("openAssignPage", scope);
				if (object instanceof NativeFunction) {
					((NativeFunction) object).getIds();
					((NativeFunction) object).getAllIds();
					((NativeFunction) object).getClassName();
					Scriptable prototype = ((NativeFunction) object)
							.getPrototype();
//					[arguments, prototype, name, arity, length]
					prototype.getIds();
					((NativeFunction) object).get("arguments", (Scriptable) object);
					((NativeFunction) object).get("prototype", (Scriptable) object);
					((NativeFunction) object).get("name", (Scriptable) object);
					((NativeFunction) object).get("arity", (Scriptable) object);
					((NativeFunction) object).get("length", (Scriptable) object);
					

				}
				if (object instanceof Script) {
//					object.
				}

				NativeFunction sss = (NativeFunction) object;
				Scriptable prototype = sss.getPrototype();
				;
				for (Object i : prototype.getIds()) {
					System.out.println(i.toString());
				}
				String decompileScript = cx.decompileScript((Script) object, 1);

				// ScriptableObject.

				// ScriptRuntime.

				System.out.println(decompileScript);
				// System.out.println(result.toString());
				// // System.out.println( b.toString());
				// Function f =
				// cx.compileFunction(scope,"function xxx(){ x = 1;" +
				// " x2 = 2;" +
				// "return 'a';}", "aaa", 1, null);
				// JavascriptValidator v = new JavascriptValidator(null);
				// v.validate("aa", b.toString());

				// System.out.println( f.get("x", scope));

				// cx.getElements((Scriptable) ss);
				// for(Object i : cx.getElements((Scriptable) ss)){
				// System.out.println(i.toString());
				// }
				// System.out.println(ss.getClass());
				// UniqueTag obj = (UniqueTag) scope.get("aaaaa", scope);
				//
				// System.out.println(obj);
				// NativeObject o = (NativeObject)scope;
				//
				// Object[] ids = o.getAllIds();
				// for(Object i : ids){
				// // System.out.println(i.toString());
				// }
				// Scriptable prototype = o.getPrototype();
				// Object object = o.get("queryFunction", o);
				// Object object2 = o.get(0, o);
				// // o.
				// js.exec(cx, scope);
				// System.out.println(js.getClass());

			} catch (Exception e) {

				e.printStackTrace();
			} finally {
				Context.exit();
				
			}
		}
		return js;
	}

	public static void mm() {
		String s = " function  a(para){para.toString();} function b(){}";
		// s = "{a:1, b:['x','y']};";
		// "obj = {a:1, b:['x','y']}",
		Context cx = Context.enter();
		try {
			// Set version to JavaScript1.2 so that we get object-literal style
			// printing instead of "[object Object]"
			cx.setLanguageVersion(Context.VERSION_1_2);

			// Initialize the standard objects (Object, Function, etc.)
			// This must be done before scripts can be executed.
			Scriptable scope = cx.initStandardObjects();

			// Now we can evaluate a script. Let's create a new object
			// using the object literal notation.
			Object result = cx.evaluateString(scope, s, "MySource", 1, null);
			System.out.println(result.getClass());
			// Scriptable obj = (Scriptable) scope.get("a", scope);
			Object[] propertyIds = ScriptableObject.getPropertyIds(scope);

			for (Object o : propertyIds) {
				System.out.println(o);
			}
			Object object = scope.get("a", scope);
			System.out.println(object.getClass().getInterfaces());
			System.out.println(object instanceof Scriptable);
			System.out.println(object instanceof Script);

			// Should print {a:1, b:["x", "y"]}
			// Function fn = (Function) ScriptableObject.getProperty(obj,
			// "toString");
			// System.out.println(fn.call(cx, scope, obj, new Object[0]));
		} finally {
			Context.exit();
		}

	}

	public static void main(String/* aaaaaa */args[]) {

		try {
			getScript();

		} catch (IOException e) {

			e.printStackTrace();
		}
		// mm();
	}
}
