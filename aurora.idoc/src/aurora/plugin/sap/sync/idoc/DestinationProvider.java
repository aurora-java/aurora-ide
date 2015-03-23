package aurora.plugin.sap.sync.idoc;

import java.util.HashSet;
import java.util.Set;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;

import aurora.plugin.sap.jco3.SapConfig;

public class DestinationProvider extends SapConfig {
	
	private Set<String> destinationNames = new HashSet<String>();
	
	private String defaultDestinationName;
	
	public DestinationProvider(){
	}
	
	public void addDestination(String destinationName){
		destinationNames.add(destinationName);
		if(defaultDestinationName == null)
			defaultDestinationName = destinationName;
	}
	
	
	
	public JCoDestination getJCoDestination(String destinationName) throws Exception{
		if(destinationName != null)
			return JCoDestinationManager.getDestination(destinationName);
		return JCoDestinationManager.getDestination(defaultDestinationName);
	}
}
