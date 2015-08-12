package aurora.plugin.esb.a;

public class CMDtest {
	static String start = "start ";

	public static void main(String[] args) {

		String d = "20150111";

		if (d.startsWith("20") && d.length() == 8) {
			try {
				int parseInt = Integer.parseInt(d);

			} catch (NumberFormatException e) {
			}
		}

		

	}

	public static void t1() {
		String cmd1 = "start task1";
		String cmd2 = "adsf";
		excCmd(cmd1);
	}

	private static String excCmd(String cmd) {
		boolean valid = cmd.startsWith(start);
		if (valid) {
			String taskName = cmd.replaceFirst(start, "");
		}
		return cmd + " is invalid";

	}
}
