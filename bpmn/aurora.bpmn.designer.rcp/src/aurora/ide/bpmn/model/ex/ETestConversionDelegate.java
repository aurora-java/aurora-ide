package aurora.ide.bpmn.model.ex;

import org.eclipse.bpmn2.modeler.core.DefaultConversionDelegate;

public class ETestConversionDelegate extends DefaultConversionDelegate {

	@Override
	public String convertToString(Object value) {
		return "hello world";
//		return super.convertToString(value);
	}

	@Override
	public Object createFromString(String literal) {

		return super.createFromString(literal);
	}

}
