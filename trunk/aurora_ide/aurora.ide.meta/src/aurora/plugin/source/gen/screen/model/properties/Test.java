package aurora.plugin.source.gen.screen.model.properties;

public class Test {

	Test() {
		System.out.println("aaaaaa");
	}

	{
		System.out.println("bbbbb");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String ss = "containment,save,inner,list";
		String[] split = ss.split(",");
		for (String string : split) {
			System.out.println(string);
		}

		new Test();
	}

}
