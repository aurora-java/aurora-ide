package aurora.ide.prototype.consultant.product.fsd;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

public class FunctionDesc {
	private Map<Object, Object> simpleProperties = new HashMap<Object, Object>();

	public final static String doc_title = "doc_title", fun_code = "fun_code",
			fun_name = "fun_name", writer = "writer", c_date = "c_date",
			u_date = "u_date", no = "no.", ver = "ver",
			c_manager = "c_manager", dept = "dept", h_manager = "h_manager";

	public Object getPropertyValue(String propId) {
		return simpleProperties.get(propId);
	}

	public void setPropertyValue(String propId, Object val) {
		simpleProperties.put(propId, val);
	}

	public String getDocTitle() {
		return "" + getPropertyValue(FunctionDesc.doc_title);
	}

	public String getFunCode() {
		return "" + getPropertyValue(FunctionDesc.fun_code);
	}

	public String getFunName() {
		return "" + getPropertyValue(FunctionDesc.fun_name);
	}

	public String getWriter() {
		return "" + getPropertyValue(FunctionDesc.writer);
	}

	public String getCreateDate() {
		return "" + getPropertyValue(FunctionDesc.c_date);
	}

	public String getUpdateDate() {
		return "" + getPropertyValue(FunctionDesc.u_date);
	}

	public String getControlNo() {
		return "" + getPropertyValue(FunctionDesc.no);
	}

	public String getVer() {
		return "" + getPropertyValue(FunctionDesc.ver);
	}

	public String getCustomerManager() {
		return "" + getPropertyValue(FunctionDesc.c_manager);
	}

	public String getDept() {
		return "" + getPropertyValue(FunctionDesc.dept);
	}

	public String getHandManager() {
		return "" + getPropertyValue(FunctionDesc.h_manager);
	}

	public ModifyListener createModifyListener(final String key) {
		return new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				Object source = e.getSource();
				if (source instanceof Text) {
					simpleProperties.put(key, ((Text) source).getText());
				}
			}
		};
	}
}
