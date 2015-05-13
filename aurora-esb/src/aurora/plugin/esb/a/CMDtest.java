package aurora.plugin.esb.a;

public class CMDtest {
	static String start = "start ";

	public static void main(String[] args) {
		String cmd1 = "start task1";
		String cmd2 = "adsf";
		excCmd(cmd1);
	}

	private static String excCmd(String cmd) {
		boolean valid = cmd.startsWith(start);
		if (valid) {
			String taskName = cmd.replaceFirst(start, "");
		}
		return cmd
				+ " is invalid";

	}
}
