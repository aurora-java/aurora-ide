package aurora.sql.java.editor.content.assist;

public class StringTokenizerDemo {
	public static void main(String args[]) {
		String str = "select   a   from   b  ";
		String abc = "A|B|C|D";
		StringTokenizer strToke = new StringTokenizer(str, " ");// 默认不打印分隔符
		// StringTokenizer strToke=new StringTokenizer(str,":",true);//打印分隔符
		// StringTokenizer strToke=new StringTokenizer(str,":",false);//不打印分隔符
		int size = strToke.countTokens();// 3 & 5
		System.out.println("strToke count = " + size);
		while (strToke.hasMoreElements()) {
			System.out.println(strToke.nextToken());
			System.out.println("posision : " + strToke.getCurrentPosition());
			// System.out.println(strToke.nextElement()); //效果同上
		}
		// String[] str_abc=str.split("\\|");//结果与StringTokenizer一样
		String[] str_abc = str.split("|");// 得到不同的结果
		for (int i = 0; i < str_abc.length; i++) {
			System.out.println(str_abc[i]);
		}
	}
}