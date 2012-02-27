package a;

public class d {

	public static int count = 0;

	public static void count() {
		d().println("count  :  " + (count++));
	}

	public static d d() {
		return new d();
	}

	public long start;

	public long end;

	public void println(Object o) {
		System.out.println(o);
	}

	public void print(Object o) {
		System.out.print(o);
	}

	public long startTime() {
		start = System.currentTimeMillis();
		// print("开始时间  ：");
		// println(start);
		return System.currentTimeMillis();
	}

	public long endTime() {
		end = System.currentTimeMillis();
		// print("结束时间 ：");
		// println(end);
		return end;
	}

	public long usedTimes() {
		print("用时： ");
		long u = end - start;
		if (u > 10)
			System.err.println(u);
		else
			println(u);
		return u;
	}

	public void initTime() {
		start = 0;
		end = 0;

	}

	public void b(String name) {
		print("开始 ： ");
		println(name);
		initTime();
		startTime();
	}

	public void e(String name) {
		print("结束 ： ");
		println(name);
		endTime();
		usedTimes();
	}
}
