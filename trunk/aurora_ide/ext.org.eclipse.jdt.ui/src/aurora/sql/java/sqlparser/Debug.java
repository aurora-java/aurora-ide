package aurora.sql.java.sqlparser;

public class Debug {
	public static void err(Object o, String msg) {
		System.err.print("Need IMPL ");
		System.err.print(o.getClass());
		System.err.print(" ");
		System.err.print(msg);
		System.err.println();
	}

	public static void err(Object clazz, Object o, String msg) {
		if (o != null) {
			err(clazz, msg);
		}
	}
}
