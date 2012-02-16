package aurora.ide.meta.gef.editors.models.io;

import java.lang.reflect.Method;

public class ReferenceDecl {
	public String markid;
	public Object methodOwner;
	public String methodName;
	public Class<?> argType;
	Object arg;

	public ReferenceDecl() {
	}

	public ReferenceDecl(String markid, Object methodOwner, String methodName,
			Class<?> argType) {
		super();
		this.markid = markid;
		this.methodOwner = methodOwner;
		this.methodName = methodName;
		this.argType = argType;
	}

	public void run() {
		try {
			Method m = methodOwner.getClass().getMethod(methodName, argType);
			m.invoke(methodOwner, arg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
