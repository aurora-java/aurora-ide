package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

import aurora.ide.meta.gef.editors.ImagesUtils;
import aurora.ide.meta.gef.editors.models.CheckBox;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.Renderer;

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
		Rectangle firstCellRect = copy.getTranslated(0, columnHight).setHeight(
				gridColumn.getRowHight());
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
		if (editor == null || editor.length() == 0) {
			paintEffectForRenderer(graphics, gridColumn.getRenderer(),
					firstCellRect);
			return;
		}
		if (CheckBox.CHECKBOX.equals(editor)) {
			FigureUtil.paintImageAtCenter(graphics, firstCellRect, checkImg);
			return;
		}
		graphics.setBackgroundColor(ColorConstants.WHITE);
		if (gridColumn.getDatasetField().isRequired()) {
			graphics.setBackgroundColor(ColorConstants.REQUIRED_BG);
		}
		if (gridColumn.getDatasetField().isReadOnly()) {
			graphics.setBackgroundColor(ColorConstants.READONLY_BG);
		}
		Rectangle rect = firstCellRect.getShrinked(2, 2).translate(-1, 0);
		graphics.fillRectangle(rect);
		graphics.setForegroundColor(ColorConstants.GRID_COLUMN_GRAY);
		graphics.drawRectangle(rect);
		Image img = getImageOfEditor(editor);
		if (img != null) {
			rect.x += rect.width - rect.height;
			rect.width = rect.height;
			FigureUtil.paintImageAtCenter(graphics, rect, img);
		}
		// super.paintFigure(graphics);
	}

	private Image getImageOfEditor(String editor) {
		if (Input.Combo.equals(editor))
			return ImagesUtils.getImage("palette/itembar_01.png");
		else if (Input.DATE_PICKER.equals(editor)
				|| Input.DATETIMEPICKER.equals(editor))
			return ImagesUtils.getImage("palette/itembar_02.png");
		else if (Input.LOV.equals(editor))
			return ImagesUtils.getImage("palette/itembar_03.png");
		else if (Input.TEXT.equals(editor))
			return ImagesUtils.getImage("palette/itembar_04.png");
		else if (Input.NUMBER.equals(editor))
			return ImagesUtils.getImage("palette/itembar_05.png");
		return null;
	}

	private void paintEffectForRenderer(Graphics g, Renderer renderer,
			Rectangle rect) {
		if (renderer == null)
			return;
		String rendererType = renderer.getRendererType();
		if (Renderer.PAGE_REDIRECT.equals(rendererType)) {
			String text = renderer.getDescripition();
			g.setForegroundColor(ColorConstants.LINK_COLOR);
			String lt = text.toLowerCase();
			int idx1 = lt.indexOf("<u>");
			int idx2 = lt.indexOf("</u>");
			boolean u = idx1 + idx2 > 0;
			if (u)
				text = text.substring(idx1 + 3, idx2);
			FigureUtil.paintTextAtCenter(g, rect, text, u);
		} else if (Renderer.USER_FUNCTION.equals(rendererType)) {
			Image fxImg = ImagesUtils.getImage("palette/fx.png");
			FigureUtil.paintImageAtCenter(g, rect, fxImg);
		}
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
