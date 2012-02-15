package aurora.ide.meta.gef.editors.figures;

import aurora.ide.meta.gef.editors.ImagesUtils;
import aurora.ide.meta.gef.editors.models.CheckBox;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.Input;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

public class GridColumnFigure extends Figure {
	private static Image checkImg = ImagesUtils
			.getImage("palette/checkbox_01.png");

	private int labelWidth;

	private int columnHight = 25;

	private GridColumn gridColumn;

	public GridColumnFigure() {
		this.setLayoutManager(new DummyLayout());
		this.setBorder(new GridColumnBorder("prompt", "grid_bg", this));
	}

	public int getLabelWidth() {
		return labelWidth;
	}

	public void setLabelWidth(int labelWidth) {
		this.labelWidth = labelWidth;
	}

	@Override
	public void handleFocusGained(FocusEvent event) {
		super.handleFocusGained(event);

	}

	/**
	 * @see org.eclipse.draw2d.Label#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	protected void paintFigure(Graphics graphics) {
		Rectangle copy = this.getBounds().getCopy();

		if (this.getChildren().size() > 0) {
			return;
		}
		int k = 1;
		for (int i = copy.y + columnHight; i < copy.y + copy.height; i += 25) {
			if (k % 2 == 0) {
				graphics.setBackgroundColor(ColorConstants.GRID_ROW);
				graphics.fillRectangle(copy.x, i, copy.width, 25);
			}
			graphics.setForegroundColor(ColorConstants.GRID_COLUMN_GRAY);
			graphics.drawLine(copy.x, i, copy.x + copy.width, i);
			k++;
		}
		String editor = gridColumn.getEditor();
		if (editor == null || editor.length() == 0)
			return;
		if (CheckBox.CHECKBOX.equals(editor)) {
			Rectangle r1 = new Rectangle(checkImg.getBounds());
			graphics.drawImage(checkImg, copy.x + (copy.width - r1.width) / 2,
					copy.y + 5 + columnHight);
			return;
		}
		graphics.setForegroundColor(ColorConstants.GRID_COLUMN_GRAY);
		graphics.drawRectangle(copy.x + 2, copy.y + columnHight + 2,
				copy.width - 5, gridColumn.getRowHight() - 4);
		Image img = null;
		if (Input.Combo.equals(editor))
			img = ImagesUtils.getImage("palette/itembar_01.png");
		else if (Input.CAL.equals(editor)
				|| Input.DATETIMEPICKER.equals(editor))
			img = ImagesUtils.getImage("palette/itembar_02.png");
		else if (Input.LOV.equals(editor))
			img = ImagesUtils.getImage("palette/itembar_03.png");
		else if (Input.TEXT.equals(editor))
			img = ImagesUtils.getImage("palette/itembar_04.png");
		else if (Input.NUMBER.equals(editor))
			img = ImagesUtils.getImage("palette/itembar_05.png");
		graphics.drawImage(img, copy.x + copy.width - 20, copy.y + 5
				+ columnHight);
		// super.paintFigure(graphics);
	}

	public void setModel(GridColumn component) {
		this.gridColumn = component;

	}

	public String getPrompt() {
		return gridColumn.getPrompt();
	}

	public int getColumnHight() {
		return columnHight;
	}

	public void setColumnHight(int columnHight) {
		this.columnHight = columnHight;
		if (gridColumn != null)
			gridColumn.setHeadHight(columnHight);
		// this.repaint();
	}

}
