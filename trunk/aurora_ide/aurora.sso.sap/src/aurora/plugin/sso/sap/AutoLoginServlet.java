package aurora.plugin.sso.sap;

import java.io.IOException;
import java.util.logging.Level;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import uncertain.composite.CompositeMap;
import uncertain.core.IContainer;
import uncertain.core.UncertainEngine;
import uncertain.event.Configuration;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.IProcedureManager;
import uncertain.proc.Procedure;
import aurora.application.action.HttpSessionCopy;
import aurora.application.features.HttpRequestTransfer;
import aurora.service.IServiceFactory;
import aurora.service.ServiceInvoker;
import aurora.service.ServiceThreadLocal;
import aurora.service.http.HttpServiceInstance;
import aurora.service.http.WebContextInit;

public class AutoLoginServlet extends HttpServlet {

	private static final long serialVersionUID = -278029498162151076L;

	public final static String PLUGIN = AutoLoginServlet.class.getCanonicalName();
	public static final String KEY_LOCALE_ID = "locale_id";
	public final static String DEFAULT_LOGIN_PROC = "init.auto_login";

	private String afterLoginRedirectUrl;
	private String autoLoginProc;
	private String loginFailedRedirectUrl;
	private String verifyPseFile;

	IObjectRegistry mRegistry;
	ILogger logger;
	SapUserDistil distil;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ServletContext context = config.getServletContext();
		UncertainEngine uncertainEngine = WebContextInit.getUncertainEngine(context);
		if (uncertainEngine == null)
			throw new ServletException("Uncertain engine not initialized");

		// get global service config
		mRegistry = uncertainEngine.getObjectRegistry();
		if (mRegistry == null)
			throw new ServletException("IObjectRegistry not initialized");
		
		afterLoginRedirectUrl = super.getInitParameter("afterLoginRedirectUrl");
		loginFailedRedirectUrl = super.getInitParameter("loginFailedRedirectUrl");

		autoLoginProc = super.getInitParameter("autoLoginProc");
		if(autoLoginProc == null)
			autoLoginProc = DEFAULT_LOGIN_PROC;
		
		verifyPseFile = super.getInitParameter("verifyPseFile");
		if(verifyPseFile == null)
			throw new IllegalArgumentException("Please set init-param 'verifyPseFile' !");
		
		logger = LoggingContext.getLogger(PLUGIN, mRegistry);
		distil = new SapUserDistil();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
		logger.info("sap sso login");
		HttpServletRequest request = (HttpServletRequest)servletRequest;
		HttpServletResponse response = (HttpServletResponse)servletResponse;
		
		String targetUrl = getTargetUrl(request);
		
		HttpSession session = request.getSession(false);
		if(session != null){
			response.sendRedirect(targetUrl);
			return;
		}

		try {
			String loginName = distil.execute(request,verifyPseFile, logger);
//			if(loginName == null)
//				loginName = "annwang";
			if(loginName == null){
				if(loginFailedRedirectUrl != null)
					response.sendRedirect(loginFailedRedirectUrl);
				return;
			}
			executeLoginProc(request,response,loginName);
			session = request.getSession(false);
			logger.info("session:"+session);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "", e);
			return;
		}
		if(targetUrl != null)
			response.sendRedirect(targetUrl);
	}
	private String getTargetUrl(HttpServletRequest request){
		if(request == null)
			return null;
		String targetUrl = getFullTargetUrl(request);
		logger.info("full targetUrl:"+targetUrl);
		if(targetUrl != null){
			return targetUrl;
		}
		else
			return afterLoginRedirectUrl;
	}
	private String getFullTargetUrl(HttpServletRequest request){
		String targetUrl = request.getParameter("targetUrl");
		logger.info("source targetUrl:"+targetUrl);
		if(targetUrl == null)
			return null;
		if(targetUrl.startsWith("http"))
			return targetUrl;
		//http://oatest.amecnsh.com:18080/amec/sso_login/language=en?targetUrl=http://oatest.amecnsh.com:18080/amec/abc.screen
		String path = request.getContextPath();  
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
		if(!targetUrl.startsWith("/")){
			targetUrl = "/"+targetUrl;
		}
		targetUrl =basePath+targetUrl;
		return targetUrl;
	}

	public void executeLoginProc(HttpServletRequest request,HttpServletResponse response,String loginName) throws Exception {
		IProcedureManager procedureManager = (IProcedureManager) mRegistry.getInstanceOfType(IProcedureManager.class);
		IServiceFactory serviceFactory = (IServiceFactory) mRegistry.getInstanceOfType(IServiceFactory.class);
		Procedure proc = procedureManager.loadProcedure(autoLoginProc);
		CompositeMap auroraContext = new CompositeMap("sso_conext");
		
//		int locale_id=getLocale_id(request,request.getSession(false));
		CompositeMap parameter = auroraContext.createChild("parameter");
		parameter.put("user_name", loginName);
//		parameter.put("locale_id", loginName);
		
		HttpServiceInstance svc = createHttpService(autoLoginProc,request,response,procedureManager,auroraContext);
		
		ServiceThreadLocal.setCurrentThreadContext(auroraContext);
		ServiceInvoker.invokeProcedureWithTransaction(autoLoginProc, proc, serviceFactory,svc,auroraContext);
		
		ServiceThreadLocal.setCurrentThreadContext(auroraContext);
		HttpRequestTransfer.copyRequest(svc);
		
        HttpSessionCopy.copySession(auroraContext, request.getSession(false));
	}
	private int getLocale_id(HttpServletRequest request,HttpSession session) {
		int locale_id =2;
		if(session!=null){
			Object locale_id_object = session.getAttribute(KEY_LOCALE_ID);
			if(locale_id_object!=null){
				locale_id =(Integer)locale_id_object;
			}
		}
		if (request.getParameter("language") != null) {
			String language = request.getParameter("language").toString();
			if (language.toUpperCase().equals("ZH")){
				return locale_id = 1;
			}
			else locale_id=2;
		}
		return locale_id;
	}
	
	public HttpServiceInstance createHttpService(String service_name,HttpServletRequest request, HttpServletResponse response,IProcedureManager procedureManager,CompositeMap context){
    	HttpServiceInstance svc = new HttpServiceInstance(service_name,procedureManager);
    	svc.setRequest(request);
        svc.setResponse(response);
        svc.setContextMap(context);
        svc.setName(service_name);
        HttpRequestTransfer.copyRequest(svc);
        HttpSessionCopy.copySession(svc.getContextMap(), request.getSession(false));
        IContainer container = (IContainer) mRegistry.getInstanceOfType(IContainer.class);
        Configuration config = (Configuration)container.getEventDispatcher();
        if(config!=null)
        	svc.setRootConfig(config);
        return svc;
	}
}
