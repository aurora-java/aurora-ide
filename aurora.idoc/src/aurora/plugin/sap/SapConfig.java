package aurora.plugin.sap;

import java.util.HashMap;
import java.util.Map;

import uncertain.core.IGlobalInstance;

import com.sap.conn.jco.JCoDestinationManager;

public class SapConfig implements ISapConfig,IGlobalInstance{
	public SapConfig(){
//		String jarPath=JCoDestinationManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
//		jarPath=jarPath.substring(0, jarPath.lastIndexOf("/"));
//		System.setProperty("java.library.path", System.getProperty("java.library.path")+":"+jarPath);		
//		System.out.println(System.getProperty("java.library.path"));		
	}
	
	Map<String,InstanceConfig> sapInstanceMap=new HashMap<String,InstanceConfig>();
	InstanceConfig defaultSapInstance;

	public InstanceConfig getSapInstance(String sid){
		return sapInstanceMap.get(sid);
	}
	
	public InstanceConfig getSapInstance(){
		return defaultSapInstance;
	}
	
	public void addInstances(InstanceConfig[] instances) {		
		InstanceConfig instance;
		int l=instances.length;
		for(int i=0;i<l;i++){
			instance=instances[i];		
			sapInstanceMap.put(instance.sid, instance);
		}
		if(l==1)
			defaultSapInstance=instances[0];
	}
}
