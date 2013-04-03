package aurora.plugin.source.gen.screen.model;

import java.util.Collections;
import java.util.List;

import uncertain.composite.CompositeMap;

import aurora.plugin.source.gen.screen.model.properties.IPropertyDescriptor;


public class Grid extends GridColumn implements DatasetBinder,IDatasetDelegate {

	public static final String GRID = "grid";

	/**
	 * 
	 */

	private GridSelectionCol gsc = new GridSelectionCol();

	public static final String NAVBAR_NONE = "";
	public static final String NAVBAR_SIMPLE = "simple";
	public static final String NAVBAR_COMPLEX = "complex";
	public static final String NAVBAR = "navBar";
	private static final String[] navBarTypes = { NAVBAR_NONE, NAVBAR_SIMPLE,
			NAVBAR_COMPLEX };
	private Navbar navBar = new Navbar();

//	private static final IPropertyDescriptor PD_NAVBAR_TYPE = new ComboPropertyDescriptor(
//			NAVBAR_TYPE, "NavBarType", navBarTypes);
//	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
//			PD_PROMPT, PD_WIDTH, PD_HEIGHT, PD_NAVBAR_TYPE };

	private Toolbar toolbar;

	public Grid() {
		super();
		this.setSize(750, 380);
		ResultDataSet dataset = new ResultDataSet();
//		dataset.setOwner(this);
		// dataset.setUseParentBM(false);
		this.setSectionType(BOX.SECTION_TYPE_RESULT);
		setDataset(dataset);
		this.setComponentType(GRID);
		setPrompt("");
	}

	public String getSelectionMode() {
		return gsc.getSelectionMode();
	}

	public ResultDataSet getDataset() {
		return (ResultDataSet) super.getDataset();
	}

	@Override
	public void setDataset(Dataset dataset) {
		super.setDataset(dataset);
//		((ResultDataSet) dataset).setOwner(this);
		setSelectionMode(getDataset().getSelectionMode());
	}

	public void setSelectionMode(String sm) {
		if (gsc.getSelectionMode().equals(sm))
			return;
		gsc.setSelectionMode(sm);
		getDataset().setSelectionMode(sm);
		getDataset().setSelectable(!sm.equals(ResultDataSet.SELECT_NONE));
		if (gsc.getSelectionMode().equals(ResultDataSet.SELECT_NONE)) {
			removeChild(gsc);
		} else {
			int idx = getChildren().indexOf(gsc);
			if (idx == -1) {
				addChild(gsc, 0);
			}
		}
	}

	public void setNavbarType(String type) {
		if (eq(getNavBarType(), type))
			return;
		navBar.setNavBarType(type);
		if (eq(getNavBarType(), NAVBAR_NONE)) {
			removeChild(navBar);
		} else {
			int idx = getChildren().indexOf(navBar);
			if (idx == -1) {
				addChild(navBar, getChildren().size());
			}
		}
	}

	public void addChild(AuroraComponent ac, int idx) {
		List<AuroraComponent> children = getChildren();
		int index1 = children.indexOf(navBar);
		if (index1 == -1)
			index1 = children.size();
		int index2 = children.indexOf(getToolbar());
		if (index2 == -1)
			index2 = index1;
		int index = Math.min(index1, index2);
		if (idx > index || (idx == 0 && children.indexOf(gsc) != -1))
			idx = index;
		if (ac instanceof Toolbar)
			idx = children.size();
		super.addChild(ac, idx);
		// System.out.println("------------------");
		// print(this, "");
	}

	protected void print(GridColumn gc, String prefix) {
		List<AuroraComponent> cs = gc.getChildren();
		for (AuroraComponent ac : cs) {
			if (ac.getClass().equals(GridColumn.class)) {
				GridColumn gc1 = (GridColumn) ac;
				System.out.println(prefix + ac.getClass().getSimpleName()
						+ "    " + gc1.getPrompt());
				print(gc1, prefix + "    ");
			} else
				System.out.println(prefix + ac.getClass().getSimpleName());
		}
	}

	public boolean hasNavBar() {
		return getChildren().indexOf(navBar) != -1;
	}

	public boolean hasToolbar() {
		return getToolbar() != null;
	}

	public Toolbar getToolbar() {
		if (toolbar != null)
			return toolbar;
		else
			return (Toolbar) getFirstChild(Toolbar.class);
	}

	// public void setToolbar(Toolbar tl) {
	// this.toolbar = tl;
	// this.addChild(tl, 0);
	// }

	public boolean hasSelectionCol() {
		return getChildren().indexOf(gsc) != -1;
	}

	@SuppressWarnings("unchecked")
	public List<Button> getToobarButtons() {
		return this.getToolbar() != null ? getToolbar().getButtons()
				: Collections.EMPTY_LIST;
	}

	@Override
	public boolean isResponsibleChild(AuroraComponent child) {
		if (child instanceof Toolbar || child instanceof Navbar)
			return this.getFirstChild(child.getClass()) == null;
		else if (child instanceof GridSelectionCol)
			return true;
		else if (child instanceof Grid)
			return false;
		return super.isResponsibleChild(child);
	}


//	public Object getPropertyValue(Object propName) {
//		if (NAVBAR_TYPE.equals(propName))
//			return Arrays.asList(navBarTypes).indexOf(getNavBarType());
//		Object val = getDataset().getPropertyValue(propName);
//		if (val != null)
//			return val;
//		return super.getPropertyValue(propName);
//	}

	public String getNavBarType() {
		return navBar.getNavBarType();
	}


//	public void setPropertyValue(Object propName, Object val) {
//		if (NAVBAR_TYPE.equals(propName))
//			setNavbarType(navBarTypes[(Integer) val]);
//		getDataset().setPropertyValue(propName, val);
//		super.setPropertyValue(propName, val);
//		if (ResultDataSet.SELECTION_MODE.equals(propName)) {
//			setSelectionMode(getDataset().getSelectionMode());
//		}
//	}

}
