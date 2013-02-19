package aurora.ide.meta.gef.editors.models;

public class QueryDataSet extends Dataset {

	public static final String QUERYDATASET = "querydataset";
	/**
	 * 
	 */
	private static final long serialVersionUID = -4436804459187661221L;

	public QueryDataSet() {
		this.setUse4Query(true);
		this.setType(QUERYDATASET);
	}
}