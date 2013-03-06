package aurora.ide.meta.js;

import java.lang.reflect.InvocationTargetException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.Wrapper;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.js.object.CompositeMapObject;

public class ScriptEngine {

	private static ImporterTopLevel topLevel;
	static {
		RhinoException.useMozillaStackStyle(false);
		initTopLevel(Context.enter());
		Context.exit();
		ContextFactory.initGlobal(new ContextFactory() {
			protected Context makeContext() {
				Context cx = super.makeContext();
				cx.setLanguageVersion(Context.VERSION_1_8);
				cx.setOptimizationLevel(-1);
				cx.setClassShutter(RhinoClassShutter.getInstance());
				cx.setWrapFactory(RhinoWrapFactory.getInstance());
				return cx;
			}
		});
	}

	private int optimizeLevel = -1;

	public ScriptEngine() {
		super();
	}

	public void defineObject(Context cx, Scriptable scope, Object object,
			String className, String varName) {
		Scriptable ctx = cx.newObject(topLevel, className,
				new Object[] { object });
		ScriptableObject.defineProperty(topLevel, varName, ctx,
				ScriptableObject.READONLY);
		// define property for $ctx
	}

	// private void definePropertyForCtx(CompositeMapObject ctx, Context cx,
	// CompositeMap service_context) {
	// String[] names = { "parameter", "session", "cookie", "model" };
	// for (String s : names) {
	// Object p = service_context.getChild(s);
	// if (p == null)
	// p = service_context.createChild(s);
	// ctx.definePrivateProperty(s, cx.newObject(ctx,
	// CompositeMapObject.CLASS_NAME, new Object[] { p }));
	// }
	// }

	private static void initTopLevel(Context cx) {
		topLevel = new ImporterTopLevel(cx);
		try {
			ScriptableObject.defineClass(topLevel, CompositeMapObject.class);
			// var a = new CompositeMapObject();
			topLevel.defineFunctionProperties(
					new String[] { "print", "println" }, ScriptEngine.class,
					ScriptableObject.DONTENUM);
			// --define useful method
			// ScriptableObject cmBuilder = (ScriptableObject) cx
			// .newObject(topLevel);
			// ScriptableObject.defineProperty(topLevel, "CompositeMapBuilder",
			// cmBuilder, ScriptableObject.DONTENUM);
			// Method[] ms = CompositeMapBuilder.class.getDeclaredMethods();
			// ArrayList<String> als = new ArrayList<String>();
			// int mod = Modifier.PUBLIC | Modifier.STATIC;
			// for (Method m : ms) {
			// if ((m.getModifiers() & mod) == mod) {
			// als.add(m.getName());
			// }
			// }
			// String[] names = als.toArray(new String[als.size()]);
			// cmBuilder.defineFunctionProperties(names,
			// CompositeMapBuilder.class, ScriptableObject.DONTENUM);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public Scriptable createScope(CompositeMap xmlMap) {
		Context cx = Context.enter();
		Scriptable scope = cx.newObject(topLevel);
		scope.setParentScope(null);
		scope.setPrototype(topLevel);
		defineObject(cx, scope, xmlMap, CompositeMapObject.CLASS_NAME,
				"context");
		cx.evaluateString(
				scope,
				"var parser = new Packages.aurora.ide.meta.gef.editors.source.gen.core.UIPParser(context.getData());",
				"parser", 1, null);
		Context.exit();
		return scope;
	}

	public Object eval(String source, Scriptable scope) throws Exception {
		Object ret = null;
		Context cx = Context.enter();
		try {
			cx.setOptimizationLevel(optimizeLevel);
			Script scr = cx.compileString(source + " run(context); ", "aa", 1,
					null);
			// Script scr = CompiledScriptCache.getInstance()
			// .getScript(source, cx);
			ret = scr == null ? null : scr.exec(cx, scope);
		} catch (RhinoException re) {
			// if (re.getCause() instanceof InterruptException)
			// throw (InterruptException) re.getCause();
			if (re.getCause() instanceof StopRunningException) {
			} else {
				throw re;
			}
		} finally {
			Context.exit();
		}

		if (ret instanceof Wrapper) {
			ret = ((Wrapper) ret).unwrap();
		} else if (ret instanceof Undefined)
			ret = null;
		return ret;
	}

	public static void print(Context cx, Scriptable thisObj, Object[] args,
			Function funObj) {
		for (int i = 0; i < args.length; i++) {
			if (i > 0)
				System.out.print(" ");
			// Convert the arbitrary JavaScript value into a string form.
			String s = Context.toString(args[i]);
			System.out.print(s);
		}
	}

	public static void println(Context cx, Scriptable thisObj, Object[] args,
			Function funObj) {
		print(cx, thisObj, args, funObj);
		System.out.println();
	}

	public void setOptimizeLevel(int level) {
		optimizeLevel = level;
	}

}
