package aurora.plugin.sap.sync.idoc;

import uncertain.composite.CompositeMap;

public class IDocFile {
	public static final String IDOC_NODE = "IDOC";
	public static final String TABNAM_NODE = "TABNAM";
	public static final String MANDT_NODE = "MANDT";
	public static final String DOCNUM_NODE = "DOCNUM";
	public static final String DOCREL_NODE = "DOCREL";
	public static final String STATUS_NODE = "STATUS";
	public static final String DIRECT_NODE = "DIRECT";
	public static final String OUTMOD_NODE = "OUTMOD";
	public static final String IDOCTYP_NODE = "IDOCTYP";
	public static final String CIMTYP_NODE = "CIMTYP";
	public static final String MESTYP_NODE = "MESTYP";
	public static final String SNDPOR_NODE = "SNDPOR";
	public static final String SNDPRT_NODE = "SNDPRT";
	public static final String SNDPRN_NODE = "SNDPRN";
	public static final String RCVPOR_NODE = "RCVPOR";
	public static final String RCVPRT_NODE = "RCVPRT";
	public static final String RCVPRN_NODE = "RCVPRN";
	public static final String CREDAT_NODE = "CREDAT";
	public static final String CRETIM_NODE = "CRETIM";
	public static final String SERIAL_NODE = "SERIAL";
	
	private String fileFullPath;
	private int idocFileId;
	private int idocServerId;
	private CompositeMap fileContent;
	private IDocType idocType;

	public IDocFile(String fileFullPath, int idocFileId, int idocServerId) {
		super();
		this.fileFullPath = fileFullPath;
		this.idocFileId = idocFileId;
		this.idocServerId = idocServerId;
	}

	public String getFileFullPath() {
		return fileFullPath;
	}

	public void setFileFullPath(String fileFullPath) {
		this.fileFullPath = fileFullPath;
	}

	public int getIdocFileId() {
		return idocFileId;
	}

	public void setIdocFileId(int idocFileId) {
		this.idocFileId = idocFileId;
	}

	public int getIdocServerId() {
		return idocServerId;
	}

	public void setIdocServerId(int idocServerId) {
		this.idocServerId = idocServerId;
	}

	public CompositeMap getFileContent() {
		return fileContent;
	}

	public void setFileContent(CompositeMap fileContent) {
		this.fileContent = fileContent;
	}
	
	public IDocType getIdocType() {
		return idocType;
	}

	public void setIdocType(IDocType idocType) {
		this.idocType = idocType;
	}

	public void clear(){
		if(fileContent != null)
			fileContent.clear();
	}
}
