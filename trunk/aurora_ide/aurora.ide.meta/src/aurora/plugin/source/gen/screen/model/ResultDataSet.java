package aurora.plugin.source.gen.screen.model;

import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;
import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;

public class ResultDataSet extends Dataset {

	public static final String RESULTDATASET = "resultdataset";
	/**
	 * 
	 */
	public static final String SELECT_NONE = "";
	public static final String SELECT_MULTI = "multiple";
	public static final String SELECT_SINGLE = "single";
	public static final String SELECTION_MODE = "selectionModel";
	private static final String[] selectionModes = { SELECT_NONE, SELECT_MULTI,
			SELECT_SINGLE };
	// private static final IPropertyDescriptor PD_SELECTION_MODE = new
	// ComboPropertyDescriptor(
	// SELECTION_MODE, "*SelectionModel", selectionModes);

	// private ContainerHolder queryContainer = new ContainerHolder();
	public static final String QUERY_CONTAINER = "queryContainer";

	public static final int DEFAULT_PAGE_SIZE = 10;
//	private int pageSize;
//	private boolean selectable = false;

	// private String selectionModel="multiple"/"single" ;
//	private String selectionMode = SELECT_NONE;

	public static final String PAGE_SIZE = "pageSize";
	public static final String SELECTABLE = "selectable";
	public static final String QUERY_DATASET = "queryDataSet";

	// private AuroraComponent owner = null;

	// private static final IPropertyDescriptor[] pds = new
	// IPropertyDescriptor[] {
	// PD_SELECTION_MODE,
	// new IntegerPropertyDescriptor(PAGE_SIZE, "*pageSize"),
	// // new BooleanPropertyDescriptor(SELECTABLE, "selectable"),
	// // new DialogPropertyDescriptor(QUERY_CONTAINER, "*queryDataSet",
	// // QueryContainerEditDialog.class)
	// };

	public ResultDataSet() {
		// this.setUse4Query(false);
		super();
		this.setPageSize(DEFAULT_PAGE_SIZE);
		this.setComponentType(RESULTDATASET);
		this.setSelectable(false);
		this.setSelectionMode(SELECT_NONE);
		this.setQueryContainer(new Form());
	}


	// public Object getPropertyValue(Object propName) {
	// if (SELECTION_MODE.equals(propName))
	// return Arrays.asList(selectionModes).indexOf(getSelectionMode());
	// else if (PAGE_SIZE.equals(propName)) {
	// return this.getPageSize();
	// } else if (SELECTABLE.equals(propName)) {
	// return this.isSelectable();
	// } else if (QUERY_DATASET.equals(propName)) {
	// return this.getQueryDataset();
	// } else if (QUERY_CONTAINER.equals(propName))
	// return getQueryContainer();
	// return super.getPropertyValue(propName);
	// }

	// public void setPropertyValue(Object propName, Object val) {
	// if (SELECTION_MODE.equals(propName))
	// setSelectionMode(selectionModes[(Integer) val]);
	// else if (PAGE_SIZE.equals(propName)) {
	// setPageSize((Integer) val);
	// } else if (SELECTABLE.equals(propName)) {
	// setSelectable((Boolean) val);
	// } else if (QUERY_DATASET.equals(propName)) {
	// } else if (QUERY_CONTAINER.equals(propName))
	// setQueryContainer((ContainerHolder) val);
	// else
	// super.setPropertyValue(propName, val);
	// }

	// private String getQueryDataset() {
	// if (queryContainer != null)
	// return queryContainer.getQueryDateset();
	// return "";
	// }
	//
	// public ContainerHolder getQueryContainer() {
	// return queryContainer;
	// }
	//
	// public void setQueryContainer(ContainerHolder queryContainer) {
	// this.queryContainer = queryContainer;
	// }
	//
	// public void setQueryContainer(Container container) {
	// queryContainer.setTarget(container);
	// }

	public int getPageSize() {
		return this.getIntegerPropertyValue(ComponentProperties.pageSize);
		// return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.setPropertyValue(ComponentProperties.pageSize, pageSize);
		// this.pageSize = pageSize;
	}

	public boolean isSelectable() {
//		return selectable;
		return this.getBooleanPropertyValue(ComponentProperties.selectable);
	}

	public void setSelectable(boolean selectable) {
//		this.selectable = selectable;
		this.setPropertyValue(ComponentProperties.selectable, selectable);
	}

	public String getSelectionMode() {
		return this.getStringPropertyValue(ComponentProperties.selectionModel);
		// return selectionMode;
	}

	public void setSelectionMode(String selectionMode) {
		this.setPropertyValue(ComponentProperties.selectionModel, selectionMode);
		// this.selectionMode = selectionMode;
		setSelectable(!selectionMode.equals(SELECT_NONE));
	}

	public Container getQueryContainer() {
		AuroraComponent ac = this
				.getAuroraComponentPropertyValue(ComponentInnerProperties.DATASET_QUERY_CONTAINER);
		return ac instanceof Container ? (Container) ac : null;
	}

	public void setQueryContainer(Container container) {
		this.setPropertyValue(ComponentInnerProperties.DATASET_QUERY_CONTAINER,
				container);
		// queryContainer.setTarget(container);
	}
	public void setPropertyValue(String propName, Object val) {
		if (ComponentProperties.selectionModel.equals(propName)){
			setSelectable(!val.equals(ResultDataSet.SELECT_NONE));
		}
		super.setPropertyValue(propName, val);
	}
	
//	getDataset().
	// public AuroraComponent getOwner() {
	// return owner;
	// }

	// public void setOwner(AuroraComponent owner) {
	// this.owner = owner;
	// queryContainer.setOwner(owner);
	// }

}