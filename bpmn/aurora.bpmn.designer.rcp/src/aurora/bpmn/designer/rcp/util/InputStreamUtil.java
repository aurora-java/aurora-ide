package aurora.bpmn.designer.rcp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

public class InputStreamUtil {

	static public String resource2String(Resource resource) {
		String r = "";
		URI uri = resource.getURI();

		try {
			FileInputStream fis = new FileInputStream(new File(
					uri.toFileString()));
			byte[] b = new byte[fis.available()];
			fis.read(b);
			fis.close();
			r = new String(b, "UTF-8");
			return r;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return r;
	}
	static public String stream2String(InputStream  stream) {
		String r = "";

		try {
			byte[] b = new byte[stream.available()];
			stream.read(b);
			stream.close();
			r = new String(b, "UTF-8");
			return r;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return r;
	}
}
