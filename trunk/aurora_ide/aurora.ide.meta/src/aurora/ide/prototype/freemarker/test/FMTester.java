package aurora.ide.prototype.freemarker.test;

import java.io.IOException;

import org.xml.sax.SAXException;

import freemarker.template.TemplateException;
import uncertain.composite.CompositeMap;

public class FMTester {
	
	private CompositeMap uip;

	public FMTester(CompositeMap uip){
		this.uip = uip;
	}
	public String gen(){
		try {
			String gen = new FreeMarkerGenerator2().gen(uip);
			return gen;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "GEN_FAILE";
	}
}
