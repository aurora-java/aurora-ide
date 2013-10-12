package aurora.ide.prototype.consultant.product.demonstrate;

import aurora.plugin.source.gen.screen.model.AuroraComponent;

public class DemonstrateData extends AuroraComponent {
	public static final String DEMONSTRATE_DATA = "demonstrate_data";
	final public static String OPEN_TYPE = "open_type";
	final public static String OPEN_TYPE_UIP = "open_type_uip";
	final public static String OPEN_TYPE_MESSAGE = "open_type_message";
	final public static String OPEN_UIP_PATH = "open_uip_path";
	final public static String OPEN_MESSAGE = "open_message";
	public static final String DEMONSTRATE_DS_NAME = "demonstrate_ds_name";

	public DemonstrateData() {
		this.setComponentType(DEMONSTRATE_DATA);
	}

	public void setOpenType(String type) {
		this.setPropertyValue(OPEN_TYPE, type);
	}

	public String getOpenType() {
		return this.getStringPropertyValue(OPEN_TYPE);
	}

	public void setOpenUIPPath(String path) {
		this.setPropertyValue(OPEN_UIP_PATH, path);
	}

	public String getOpenUIPPath() {
		return this.getStringPropertyValue(OPEN_UIP_PATH);
	}

	public void setOpenMessage(String message) {
		this.setPropertyValue(OPEN_MESSAGE, message);
	}

	public String getOpenMessage() {
		return this.getStringPropertyValue(OPEN_MESSAGE);
	}

	public void setDemonstrateDSName(String dsName) {
		this.setPropertyValue(DEMONSTRATE_DS_NAME, dsName);
	}

	public String getDemonstrateDSName() {
		return this.getStringPropertyValue(DEMONSTRATE_DS_NAME);
	}

	public void setDemonstrateData(String data) {
		this.setPropertyValue(DEMONSTRATE_DATA, data);
	}

	public String getDemonstrateData() {
		return this.getStringPropertyValue(DEMONSTRATE_DATA);
	}

}
