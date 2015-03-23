package aurora.plugin.sap.sync.idoc;

public class IDocType {
	
	private String idoctyp;
	private String cimtyp;

	public IDocType(String idoctyp, String cimtyp) {
		this.idoctyp = idoctyp;
		this.cimtyp = cimtyp;
	}

	public String getIdoctyp() {
		return idoctyp;
	}

	public void setIdoctyp(String idoctyp) {
		this.idoctyp = idoctyp;
	}

	public String getCimtyp() {
		return cimtyp;
	}

	public void setCimtyp(String cimtyp) {
		this.cimtyp = cimtyp;
	}
}
