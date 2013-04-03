package aurora.plugin.source.gen.screen.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;
import aurora.plugin.source.gen.screen.model.properties.IPropertyDescriptor;


public class TabFolder extends Container {

	public static final String TAB_PANEL = "tabPanel";
//	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
//			PD_PROMPT, PD_WIDTH, PD_HEIGHT };

	public TabFolder() {
		setSize(800, 420);
		this.setComponentType(TAB_PANEL);
		addPropertyChangeListener(new TabItemOperationListener());
	}

	public void disSelectAll() {
		for (AuroraComponent ac : getChildren()) {
			if (ac instanceof TabItem) {
				((TabItem) ac).setCurrent(false);
			} else
				break;
		}
	}

	public ArrayList<TabItem> getTabItems() {
		ArrayList<TabItem> als = new ArrayList<TabItem>(children.size() / 2);
		for (int i = 0; i < children.size() / 2; i++)
			als.add((TabItem) children.get(i));
		return als;
	}

	public TabItem getCurrent() {
		for (AuroraComponent ac : getChildren()) {
			if (ac instanceof TabItem) {
				if (((TabItem) ac).isCurrent())
					return (TabItem) ac;
			}
		}
		return null;
	}

	@Override
	public boolean isResponsibleChild(AuroraComponent component) {
		Class<? extends AuroraComponent> cls = component.getClass();
		return cls.equals(TabItem.class) || cls.equals(TabBody.class);
	}


	private class TabItemOperationListener implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			if (!ComponentInnerProperties.CHILDREN.equals(evt.getPropertyName()))
				return;
			Object obj = evt.getNewValue();
			if (!(obj instanceof TabItem))
				return;
			TabItem ti = (TabItem) obj;
			TabBody body = (TabBody) ti.getBody();
			List<?> list = getChildren();
			int idx = list.indexOf(ti);
			disSelectAll();
			if (idx == -1) {
				// TabItem be deleted, now delete it`s TabBody
				idx = list.indexOf(body);
				removeChild(body);
				if (ti.isCurrent()) {
					// calc which tabitem should be selected
					// if right tabitem(s) exists ,select the right one
					// else if left tabitem(s) exists ,select the left one
					// (else,the last tabitem be deleted,do nothing)
					idx = idx - list.size() / 2;
					if (idx < list.size() / 2)
						((TabItem) list.get(idx)).setCurrent(true);
					else if (idx > 0)
						((TabItem) list.get(idx - 1)).setCurrent(true);
				}
				return;
			}
			if (idx > list.size() / 2) {
				// when move or create,refers to a wrong
				// model,so the TabItem can not be inserted at the right
				// position,now reset it
				// System.out.println("smart correction : " + idx);
				list.remove(ti);
				idx = list.size() / 2;
				addChild(ti, idx);
				return;
			}
			// TabItem be added,now add it`s TabBody
			addChild(body, idx + 1 + list.size() / 2);
			ti.setCurrent(true);
			// System.out.println("------");
			// for (int i = 0; i < list.size() / 2; i++) {
			// TabItem t = (TabItem) list.get(i);
			// System.out.printf("%-12s%s   %s\n", t.getPrompt(),
			// t.getBody().hashCode() == list.get(list.size() / 2 + i)
			// .hashCode(), t.isCurrent() ? "*" : "");
			// }
		}
	}

}
