package aurora.ide.meta.gef.editors.figures;

import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.TabFolder;
import aurora.ide.meta.gef.editors.models.TabItem;

public class TabFolderFigure extends Figure {

	private TabFolder model;

	public TabFolderFigure() {
		setLayoutManager(new DummyLayout());
	}

	@Override
	protected void paintBorder(Graphics g) {
		g.setForegroundColor(ColorConstants.FIELDSET_BORDER);
		List<AuroraComponent> list = model.getChildren();
		if (list.size() == 0)
			g.drawRectangle(getBounds().getResized(-1, -1));
		else {
			Rectangle rect = getBounds().getResized(-1, -TabItem.HEIGHT - 2)
					.translate(0, TabItem.HEIGHT + 1);
			g.drawRectangle(rect);
			g.setForegroundColor(g.getBackgroundColor());
			TabItem currentTab = model.getCurrent();
			if (currentTab == null)
				return;
			Figure curTabFigure = null;
			for (Object o : getChildren()) {
				if (o instanceof TabItemFigure) {
					TabItemFigure tif = (TabItemFigure) o;
					if (tif.getModel() == currentTab) {
						curTabFigure = tif;
						break;
					}
				}
			}
			if (curTabFigure == null)
				return;
			rect = curTabFigure.getBounds().getResized(-1, -1);
			g.drawLine(rect.getBottomLeft(), rect.getBottomRight());
		}
	}

	public void setModel(TabFolder model) {
		this.model = model;
	}

}
