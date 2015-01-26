package aurora.plugin.oracle.streams;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CopyFiles {
	public static void main(String[] args) {
		readTxtFile(
				"/Users/shiliyan/Desktop/work/aurora/workspace/oracle_streams/streams_server_client/deploy/WEB-INF/aurora.plugin.oracle.streams/a",
				"/Users/shiliyan/Desktop/work/aurora/workspace/oracle_streams/streams_server_client/lib/");
	}

	public static void readTxtFile(String filePath, String newPath) {
		try {
			String encoding = "utf-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					File oldfile = new File(lineTxt);
					Copy(lineTxt, newPath + oldfile.getName());
					System.out.println(oldfile.getName());
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
	}

	static String jarName(String path) {
		// /Volumes/Macintosh
		// HD/.m2/repository/org/springframework/spring-jms/3.2.11.RELEASE/spring-jms-3.2.11.RELEASE.jar
		// new File(path).get;
		return "";
	}

	public static void Copy(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) {
				InputStream inStream = new FileInputStream(oldPath);
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread;
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("error  ");
			e.printStackTrace();
		}
	}
}
