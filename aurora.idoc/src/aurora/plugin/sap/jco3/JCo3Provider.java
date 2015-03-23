package aurora.plugin.sap.jco3;

import java.util.HashMap;
import java.util.Properties;

import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;

public class JCo3Provider implements DestinationDataProvider{
	private DestinationDataEventListener eL;
    private HashMap<String, Properties> secureDBStorage = new HashMap<String, Properties>();
    public JCo3Provider(){
    	
    }
	public Properties getDestinationProperties(String destinationName) {		
		Properties p = secureDBStorage.get(destinationName);
        if(p!=null){
            return p;
        }
        return null;
	}

	public void setDestinationDataEventListener(
			DestinationDataEventListener eventListener) {
		this.eL = eventListener;		
	}

	public boolean supportsEvents() {		
		return true;
	}
	
	public void changeProperties(String destName, Properties properties){
		 if (!Environment.isDestinationDataProviderRegistered()) {
			 Environment.registerDestinationDataProvider(this);
		 }
         synchronized(secureDBStorage)
         {
             if(properties==null)
             {
                 if(secureDBStorage.remove(destName)!=null)
                     eL.deleted(destName);
             }
             else 
             {
                 secureDBStorage.put(destName, properties);
                 eL.updated(destName); // create or updated
             }
         }
	}
}
