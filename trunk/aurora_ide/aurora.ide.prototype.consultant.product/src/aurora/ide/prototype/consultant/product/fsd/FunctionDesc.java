package aurora.ide.prototype.consultant.product.fsd;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

import aurora.ide.swt.util.PageModel;

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
		return getStringPropertyValue(FunctionDesc.doc_title);
	}

	public String getStringPropertyValue(String propId) {
		Object x = this.getPropertyValue(propId);
		return x instanceof String ? x.toString() : "";
	}

	public String getFunCode() {
		return getStringPropertyValue(FunctionDesc.fun_code);
	}

	public String getFunName() {
		return getStringPropertyValue(FunctionDesc.fun_name);
	}

	public String getWriter() {
		return getStringPropertyValue(FunctionDesc.writer);
	}

	public String getCreateDate() {
		return getStringPropertyValue(FunctionDesc.c_date);
	}

	public String getUpdateDate() {
		return getStringPropertyValue(FunctionDesc.u_date);
	}

	public String getControlNo() {
		return getStringPropertyValue(FunctionDesc.no);
	}

	public String getVer() {
		return getStringPropertyValue(FunctionDesc.ver);
	}

	public String getCustomerManager() {
		return getStringPropertyValue(FunctionDesc.c_manager);
	}

	public String getDept() {
		return getStringPropertyValue(FunctionDesc.dept);
	}

	public String getHandManager() {
		return getStringPropertyValue(FunctionDesc.h_manager);
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

	static public FunctionDesc create(PageModel pm) {
		FunctionDesc fd = new FunctionDesc();
		fd.setPropertyValue(doc_title, pm.getStringPropertyValue(doc_title));
		fd.setPropertyValue(fun_code, pm.getStringPropertyValue(fun_code));
		fd.setPropertyValue(fun_name, pm.getStringPropertyValue(fun_name));
		fd.setPropertyValue(writer, pm.getStringPropertyValue(writer));
		fd.setPropertyValue(c_date, pm.getStringPropertyValue(c_date));
		fd.setPropertyValue(u_date, pm.getStringPropertyValue(u_date));
		fd.setPropertyValue(no, pm.getStringPropertyValue(no));
		fd.setPropertyValue(ver, pm.getStringPropertyValue(ver));
		fd.setPropertyValue(c_manager, pm.getStringPropertyValue(c_manager));
		fd.setPropertyValue(dept, pm.getStringPropertyValue(dept));
		fd.setPropertyValue(h_manager, pm.getStringPropertyValue(h_manager));
		return fd;
	}
}
