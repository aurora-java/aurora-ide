package aurora.plugin.pay.haire;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;
import aurora.service.ServiceContext;
import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;

import com.client.pojo.VerifyResult;

public class ResponseWrite extends AbstractEntry {


	private String text;


	public IObjectRegistry getRegistry() {
		return registry;
	}

	public void setRegistry(IObjectRegistry registry) {
		this.registry = registry;
	}

	private IObjectRegistry registry;

	public ResponseWrite(IObjectRegistry registry) {
		this.registry = registry;
		// uncertainEngine = (UncertainEngine)
		// registry.getInstanceOfType(UncertainEngine.class);
	}

	private String getValue(String s, CompositeMap model, String name) {
		String templateName = s;
		if (templateName != null)
			templateName = uncertain.composite.TextParser.parse(templateName,
					model);
		if (templateName == null)
			throw new IllegalArgumentException(name + " can not be null!");
		return templateName;
	}

	@Override
	public void run(ProcedureRunner runner) throws Exception {
		
		CompositeMap context = runner.getContext();
		HttpServiceInstance serviceInstance = (HttpServiceInstance) ServiceInstance
				.getInstance(context);
		HttpServletRequest request = serviceInstance.getRequest();
		request.setCharacterEncoding("UTF-8");

		text = getValue(this.getText(),context,"text");

		HttpServletResponse response = serviceInstance.getResponse();
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = serviceInstance.getResponse().getWriter();
		out.write(text);
		out.flush();	
		out.close();
		
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}



}
