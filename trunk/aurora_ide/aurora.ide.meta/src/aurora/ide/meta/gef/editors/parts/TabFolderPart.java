package aurora.ide.meta.gef.editors.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.IFigure;

import aurora.ide.meta.gef.editors.figures.TabFolderFigure;
import aurora.ide.meta.gef.editors.models.IProperties;
import aurora.ide.meta.gef.editors.models.TabBody;
import aurora.ide.meta.gef.editors.models.TabFolder;
import aurora.ide.meta.gef.editors.models.TabItem;

public class TabFolderPart extends ContainerPart {

	@Override
	protected IFigure createFigure() {
		getModel().addPropertyChangeListener(new TabItemOperationListener());
		TabFolderFigure figure = new TabFolderFigure();
		figure.setModel(getModel());
		return figure;
	}

	public TabFolder getModel() {
		return (TabFolder) super.getModel();
	}

	public TabFolderFigure getFigure() {
		return (TabFolderFigure) super.getFigure();
	}

	protected void refreshVisuals() {
		super.refreshVisuals();
	}

	@Override
	protected void refreshChildren() {
		super.refreshChildren();
	}

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
	}

	private class TabItemOperationListener implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			if (!IProperties.CHILDREN.equals(evt.getPropertyName()))
				return;
			Object obj = evt.getNewValue();
			if (!(obj instanceof TabItem))
				return;
			TabItem ti = (TabItem) obj;
			TabBody body = (TabBody) ti.getBody();
			List<?> list = getModel().getChildren();
			int idx = list.indexOf(ti);
			getModel().disSelectAll();
			if (idx == -1) {
				// TabItem已被删除,现在删除相应的TabBody
				idx = list.indexOf(body);
				getModel().removeChild(body);
				if (ti.isCurrent()) {
					// 重新计算应该被选定的标签页
					// 如果存在右侧标签,则选定右侧标签
					// 否则,如果存在存在左侧标签,则选定它
					// (则否则,就是最后一个标签被删了,不用选了)
					idx = idx - list.size() / 2;
					if (idx < list.size() / 2)
						((TabItem) list.get(idx)).setCurrent(true);
					else if (idx > 0)
						((TabItem) list.get(idx - 1)).setCurrent(true);
				}
				return;
			}
			if (idx > list.size() / 2) {
				// 移动或创建时,reference错误,导致插入的TabItem位置不对,重新调整
				// System.out.println("smart correction : " + idx);
				list.remove(ti);
				idx = list.size() / 2;
				getModel().addChild(ti, idx);
				return;
			}
			// TabItem被添加,现在添加对应的TabBody
			getModel().addChild(body, idx + 1 + list.size() / 2);
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

	@Override
	public int getResizeDirection() {
		return NSEW;
	}
}
