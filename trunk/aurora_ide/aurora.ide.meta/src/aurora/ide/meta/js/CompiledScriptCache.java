package aurora.ide.meta.js;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Script;

import uncertain.core.UncertainEngine;
import uncertain.mbean.MBeanRegister;

/**
 * Every piece of script will be compiled before execute.And the compilation is
 * independent from runtime scope,So we can cache the compiled script,to reduce
 * the full-execute time(each piece of script will only be compiled once).<br/>
 * 
 * @author jessen
 * 
 */
public class CompiledScriptCache  {
	private static CompiledScriptCache instance = null;
	private int maxCacheSize = 1000;

	private HashMap<Key, Value> scriptCache = new HashMap<Key, Value>(256);

	/**
	 * constructor for engine
	 * 
	 * @param engine
	 */
	public CompiledScriptCache(UncertainEngine engine) {
		instance = this;
		String mbean_name = engine.getMBeanName("cache", "name=script");
		MBeanRegister.resiterMBean(mbean_name, instance);
	}

	/**
	 * if instance not exists ,then create it,else do nothing
	 * 
	 * @param engine
	 */
	public static void createInstanceNE(UncertainEngine engine) {
		if (instance != null)
			return;
		synchronized (CompiledScriptCache.class) {
			if (instance == null)
				new CompiledScriptCache(engine);
		}
	}

	public static CompiledScriptCache getInstance() {
		return instance;
	}

	public int getMaxCacheSize() {
		return maxCacheSize;
	}

	public void setMaxCacheSize(int maxCacheSize) {
		this.maxCacheSize = maxCacheSize;
	}

	/**
	 * try to get Script from cache,if not success(not exists,or optimizeLevel
	 * not compatible ) then {@code source} will be compiled,and cached
	 * 
	 * @param source
	 * @param cx
	 * @param sourceName
	 * @return
	 */
	public Script getScript(String source, Context cx, String sourceName) {
		Key k = new Key(source, cx.getOptimizationLevel());
		Value v = scriptCache.get(k);
		if (v == null) {
			v = new Value(cx.compileString(source, sourceName, 1, null));
			autoClear();
			synchronized (scriptCache) {
				scriptCache.put(k, v);
			}
		} else {
			v.lastUseTime = System.currentTimeMillis();
			v.hits.incrementAndGet();
		}
		return v.script;
	}

	/**
	 * 
	 * {@link #getScript(String , Context , String )}<br/>
	 * the sourceName is &lt;Unknown source&gt;
	 * 
	 * @param source
	 * @param cx
	 * @return
	 */
	public Script getScript(String source, Context cx) {
		return getScript(source, cx, "<Unknown source>");
	}

	public boolean isChanged(File file, Context cx) {
		Key k = new Key(file, cx.getOptimizationLevel());
		Value v = scriptCache.get(k);
		return (v == null || k.lastModif != v.lastModif);
	}

	/**
	 * 
	 * @param file
	 * @param cx
	 * @return
	 */
	public Script getScript(File file, Context cx) {
		Key k = new Key(file, cx.getOptimizationLevel());
		Value v = scriptCache.get(k);
		if (v == null || k.lastModif != v.lastModif) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(
						new FileInputStream(file), "UTF-8"));
				v = new Value(cx.compileReader(br, file.getName(), 1, null),
						k.lastModif);
			} catch (RhinoException re) {
				throw re;
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				if (br != null)
					try {
						br.close();
					} catch (IOException e) {
					}
			}
			autoClear();
			synchronized (scriptCache) {
				scriptCache.put(k, v);
			}
		} else {
			v.lastUseTime = System.currentTimeMillis();
			v.hits.incrementAndGet();
		}
		return v.script;
	}

	public int getScriptSize() {
		return scriptCache.size();
	}

	private void autoClear() {
		synchronized (scriptCache) {
			if (scriptCache.size() >= maxCacheSize) {
				Set<Entry<Key, Value>> set = scriptCache.entrySet();
				@SuppressWarnings("unchecked")
				Entry<Key, Value>[] ets = set.toArray(new Entry[set.size()]);
				Arrays.sort(ets, new Comparator<Entry<Key, Value>>() {
					public int compare(Entry<Key, Value> o1,
							Entry<Key, Value> o2) {
						return (int) (o1.getValue().lastUseTime - o2.getValue().lastUseTime);
					}
				});
				for (int i = 0; i < ets.length / 2; i++) {
					set.remove(ets[i]);
				}
				// System.out.println("half clear");
				// -------------
				// scriptCache.clear();
				// System.out.println("full clear");
			}
		}
	}

	public void clearScriptCache() {
		synchronized (scriptCache) {
			scriptCache.clear();
		}
	}

	public String getScriptDetail(int idx) {
		if (idx < 0 || idx >= scriptCache.size())
			return "Index outof bounds";
		Key k = (Key) scriptCache.keySet().toArray()[idx];
		Value v = scriptCache.get(k);
		return String
				.format("compiled script object:%s\ncompiled at:%s\nlast use at:%s\nhits:%d\nsource detail:\n%s",
						v.script, new Date(v.compiledTime), new Date(
								v.lastUseTime), v.hits.intValue(),
						k.source == null ? k.file : k.source);

	}

	static class Key {
		String source;
		File file;
		int optLevel;
		long lastModif;

		Key(Object s, int level) {
			if (s instanceof String)
				source = (String) s;
			else if (s instanceof File) {
				file = (File) s;
				lastModif = file.lastModified();
			}
			optLevel = level;
		}

		@Override
		public int hashCode() {
			if (file != null)
				return file.hashCode() + optLevel;
			if (source != null)
				return source.hashCode() + optLevel;
			return super.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Key))
				return false;
			Key k = (Key) obj;
			return eq(source, k.source) && eq(file, k.file)
					&& optLevel == k.optLevel;
		}

		static boolean eq(Object o1, Object o2) {
			if (o1 == null)
				return o2 == null;
			return o1.equals(o2);
		}

	}

	static class Value {
		Script script;
		long lastModif;// only for file
		long compiledTime;
		long lastUseTime;
		AtomicInteger hits = new AtomicInteger();

		Value(Script scr) {
			this.script = scr;
			this.lastUseTime = this.compiledTime = System.currentTimeMillis();
		}

		Value(Script scr, long lastModif) {
			this(scr);
			this.lastModif = lastModif;
		}
	}

}
