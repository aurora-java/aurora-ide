package aurora.bpmn.designer.rcp.action;

import java.io.InputStream;

public class TestBPMN {

	public static InputStream getStream(){
//		TestBPMN.class.getResourceAsStream("test.bpmn");
		return TestBPMN.class.getResourceAsStream("test.bpmn");
	}
}
