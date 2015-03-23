package aurora.plugin.sap;

import uncertain.core.IGlobalInstance;
import uncertain.ocm.IObjectRegistry;

public class SapInstance extends InstanceConfig implements IGlobalInstance {

	public SapInstance(IObjectRegistry registry) {
		super(registry);		
	}
	
}
