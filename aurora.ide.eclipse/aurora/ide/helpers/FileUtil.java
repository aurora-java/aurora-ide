package aurora.ide.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

	static public StringBuilder readStringFile(InputStream resourceAsStream) {
		return readStringFile(resourceAsStream, "UTF-8");
	}

	static public StringBuilder readStringFile(InputStream resourceAsStream, String charset) {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(resourceAsStream, charset));
			String inLine = null;
			while ((inLine = br.readLine()) != null) {
				sb = sb.append(inLine).append("\n");
			}
		} catch (IOException e) {
			DialogUtil.logErrorException(e);
			e.printStackTrace();
		} finally {
			try {
				resourceAsStream.close();
			} catch (IOException e) {
			}
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb;
	}

	static public List<String> readStringFileToList(InputStream resourceAsStream) {
		return readStringFileToList(resourceAsStream, "UTF-8");
	}

	static public List<String> readStringFileToList(InputStream resourceAsStream, String charset) {
		List<String> sb = new ArrayList<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(resourceAsStream, charset));
			String inLine = null;
			while ((inLine = br.readLine()) != null) {
				sb.add(inLine);
			}
		} catch (IOException e) {
			DialogUtil.logErrorException(e);
			e.printStackTrace();
		} finally {
			try {
				resourceAsStream.close();
			} catch (IOException e) {
			}
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb;
	}

	public static void saveToFile(File file, String content, String encoding) throws IOException {

		FileOutputStream os = null;

		try {
			os = new FileOutputStream(file);
			os.write(content.getBytes(encoding));
			os.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (os != null)
				os.close();
		}

	}

}
