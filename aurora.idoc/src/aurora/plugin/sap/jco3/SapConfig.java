package aurora.plugin.sap.jco3;

import java.util.Properties;

import uncertain.core.IGlobalInstance;

import aurora.plugin.sap.ISapConfig;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.ext.DestinationDataProvider;

public class SapConfig implements ISapConfig,IGlobalInstance{	
	String defaultSid;
	public SapConfig(){
		String jarPath=JCoDestinationManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		jarPath=jarPath.substring(0, jarPath.lastIndexOf("/"));
		System.setProperty("java.library.path", System.getProperty("java.library.path")+":"+jarPath);		
		System.out.println(System.getProperty("java.library.path"));		
	}
	
	public void addInstances(InstanceConfig[] instances) {	
		JCo3Provider myProvider=new JCo3Provider();
		InstanceConfig instance;
		Properties connectProperties;
		int l=instances.length;
		for(int i=0;i<l;i++){
			instance=instances[i];		
			connectProperties = new Properties();
			connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, instance.getServer_ip());
			connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,  instance.getSystem_number());
			connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, instance.getSap_client());
			connectProperties.setProperty(DestinationDataProvider.JCO_USER, instance.getUserid());
			connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, instance.getPassword());
			connectProperties.setProperty(DestinationDataProvider.JCO_LANG,  instance.getDefault_lang());
			connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT,  Integer.toString(instance.getMax_conn()));
			myProvider.changeProperties(instance.getSid(), connectProperties); 
		}	
		if(l==1)
			defaultSid=instances[0].sid;
	}
	
	public JCoDestination getJCoDestination(String sid) throws Exception{
		if(sid==null){
			if(defaultSid!=null)
				sid=defaultSid;
			else
				throw new IllegalArgumentException("jco-invoke: sid attribute is null");
		}		
		return JCoDestinationManager.getDestination(sid);
	}
}
