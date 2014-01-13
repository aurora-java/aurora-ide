package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.editors.consultant.property.ConsultantPropertyFactory;
import aurora.ide.meta.gef.util.TextStyleUtil;
import aurora.plugin.source.gen.screen.model.CheckBox;
import aurora.plugin.source.gen.screen.model.GridColumn;
import aurora.plugin.source.gen.screen.model.Input;
import aurora.plugin.source.gen.screen.model.Renderer;
import aurora.plugin.source.gen.screen.model.StyledStringText;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;
import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;

public class GridColumnFigure extends Figure implements IResourceDispose {
	public static final int ROW_HEIGHT = 25;

	private static Image checkImg = PrototypeImagesUtils
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
		// GC.
		// graphics.drawText(s, p, style)
		Rectangle copy = this.getBounds().getCopy();
		Rectangle firstCellRect = copy.getTranslated(0, columnHight).setHeight(
				gridColumn.getRowHight());
		if (this.getChildren().size() > 0) {
			return;
		}
		int k = 1;
		for (int i = copy.y + columnHight; i < copy.y + copy.height; i += ROW_HEIGHT) {
			if (k % 2 == 0) {
				graphics.setBackgroundColor(ColorConstants.GRID_ROW);
				graphics.fillRectangle(copy.x, i, copy.width, ROW_HEIGHT);
			}
			graphics.setForegroundColor(ColorConstants.GRID_COLUMN_GRAY);
			graphics.drawLine(copy.x, i, copy.x + copy.width, i);
			k++;
		}
		String editor = gridColumn.getEditor();
		if (editor == null || editor.length() == 0) {
			paintEffectForRenderer(graphics, gridColumn.getRenderer(),
					firstCellRect);
		} else if (CheckBox.CHECKBOX.equals(editor)) {
			FigureUtil.paintImageAtCenter(graphics, firstCellRect, checkImg);
		} else {
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
		}
		paintSimpleDatas(graphics);
		// super.paintFigure(graphics);
	}

	private void paintSimpleDatas(Graphics graphics) {
		Rectangle copy = this.getBounds().getCopy();
		int k = 1;
		for (int i = copy.y + columnHight; i < copy.y + copy.height
				- this.columnHight; i += ROW_HEIGHT) {
			String propId = ComponentInnerProperties.GRID_COLUMN_SIMPLE_DATA
					+ k;
			String sd = gridColumn.getStringPropertyValue(propId);

			Object obj = gridColumn.getPropertyValue(propId
					+ ComponentInnerProperties.TEXT_STYLE);
			if (obj instanceof StyledStringText
					&& ((StyledStringText) obj).isUseless() == false) {
				paintStyledText(graphics, sd, propId, new Rectangle(copy.x + 2,
						i, copy.width-6, ROW_HEIGHT));
			} else {
				this.paintSimpleData(graphics, sd, new Rectangle(copy.x + 2, i,
						copy.width-6, ROW_HEIGHT));
			}
			k++;
		}
	}

	protected void paintStyledText(Graphics g, String text, String property_id,
			Rectangle r) {
		g.pushState();
		this.disposeResource(property_id);
		Rectangle copy = r.getCopy();
		g.setForegroundColor(ColorConstants.BLACK);
		Dimension dim = FigureUtilities.getTextExtents(text, getFont());
		// FigureUtilities.
//		if (ComponentProperties.prompt.equals(property_id) == false)
//			g.setClip(r.getResized(-16, 0));
		TextLayout tl = new TextLayout(null);
		tl.setText(text);
		tl.setFont(getFont());
		Object obj = gridColumn.getPropertyValue(property_id
				+ ComponentInnerProperties.TEXT_STYLE);
		TextStyle ts = null;
		if (obj instanceof StyledStringText) {
			ts = TextStyleUtil.createTextStyle((StyledStringText) obj,
					Display.getDefault(), getFont());
		} else {
			ts = new TextStyle();
		}
		tl.setStyle(ts, 0, text.length() - 1);

		Point point = TextStyleUtil.getTextAlignment(r, text, getFont(),
				getAlignmentStyle(property_id));

//		Point p = new Point(r.x + 2, r.y + (r.height - dim.height) / 2);
		if (ComponentProperties.prompt.equals(property_id)) {
			g.drawTextLayout(tl, copy.x, copy.y);
		} else {
			g.drawTextLayout(tl, point.x, point.y);
		}
		handleResource(property_id, tl);
		g.popState();
	}

	private int getAlignmentStyle(String property_id) {
		String align = gridColumn
				.getStringPropertyValue(ComponentInnerProperties.GRID_COLUMN_ALIGNMENT);
		if (ConsultantPropertyFactory.aligns[0].equals(align)) {
			return SWT.LEFT;
		}
		if (ConsultantPropertyFactory.aligns[1].equals(align)) {
			return SWT.CENTER;
		}
		if (ConsultantPropertyFactory.aligns[2].equals(align)) {
			return SWT.RIGHT;
		}
		return SWT.LEFT;
	}

	protected void paintSimpleData(Graphics g, String text, Rectangle r) {
		if (text == null || "".equals(text))
			return;
		g.pushState();
		g.setForegroundColor(ColorConstants.BLACK);
		Dimension dim = FigureUtilities.getTextExtents(text, getFont());
//		g.setClip(r.getResized(-16, 0));
		Point point = TextStyleUtil.getTextAlignment(r, text, getFont(),
				getAlignmentStyle(""));
		// Point p = new Point(r.x + 2, r.y + (r.height - dim.height) / 2);
		g.drawText(text, point);
		g.popState();
	}

	private Image getImageOfEditor(String editor) {
		if (Input.Combo.equals(editor))
			return PrototypeImagesUtils.getImage("palette/itembar_01.png");
		else if (Input.DATE_PICKER.equals(editor)
				|| Input.DATETIMEPICKER.equals(editor))
			return PrototypeImagesUtils.getImage("palette/itembar_02.png");
		else if (Input.LOV.equals(editor))
			return PrototypeImagesUtils.getImage("palette/itembar_03.png");
		else if (Input.TEXT.equals(editor))
			return PrototypeImagesUtils.getImage("palette/itembar_04.png");
		else if (Input.NUMBER.equals(editor))
			return PrototypeImagesUtils.getImage("palette/itembar_05.png");
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
			Image fxImg = PrototypeImagesUtils.getImage("palette/fx.png");
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

	public GridColumn getModel() {
		return gridColumn;
	}

	private ResourceDisposer disposer = new ResourceDisposer();

	private void handleResource(String id, Resource r) {
		disposer.handleResource(id, r);
	}

	private void disposeResource(String prop_id) {
		disposer.disposeResource(prop_id);
	}

	public void disposeResource() {
		Border border = this.getBorder();
		if (border instanceof IResourceDispose) {
			((IResourceDispose) border).disposeResource();
		}
		disposer.disposeResource();
		disposer = null;
	}

}
