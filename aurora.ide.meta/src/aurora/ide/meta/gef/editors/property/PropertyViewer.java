package aurora.ide.meta.gef.editors.property;

import java.util.ArrayList;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.properties.IPropertySheetEntry;

import aurora.ide.meta.gef.editors.figures.ColorConstants;

public class PropertyViewer extends Canvas implements PaintListener,
		MouseListener, MouseMoveListener, MouseTrackListener {

	static final int LABELSTART = 5;//
	static final int EDITORSTART = 3;// relative to ditorStart
	static final int ROWHEIGHT = 20;
	static final int MINWIDTHLEFT = 30;// 属性名列的最小宽度
	static final int MINWIDTHRIGHT = 40;// 属性值列的最小宽度
	static final String EMPTYTEXT = "<No Properties>";
	private ArrayList<PropertyItem> als = new ArrayList<PropertyItem>();
	private int splitLineX = 80;
	private boolean canResize = true;
	private boolean resizing = false;

	public PropertyViewer(Composite parent, int style) {
		super(parent, style);
		if (parent instanceof ScrolledComposite) {
			((ScrolledComposite) parent).getVerticalBar().setIncrement(
					ROWHEIGHT);
		}
		addPaintListener(this);
		addMouseListener(this);
		addMouseMoveListener(this);
		addMouseTrackListener(this);
		setDragDetect(false);
	}

	public PropertyItem[] getItems() {
		PropertyItem[] pis = new PropertyItem[als.size()];
		for (int i = 0; i < pis.length; i++)
			pis[i] = als.get(i);
		return pis;
	}

	public int getItemCount() {
		return als.size();
	}

	public void removeItem(PropertyItem pi) {
		// pi.getControl(this).dispose();
		als.remove(pi);
		redraw();
	}

	public PropertyItem createItem(IPropertySheetEntry pse, int index) {
		PropertyItem pi = new PropertyItem(pse);
		als.add(index, pi);
		Composite par = getParent();
		if (par instanceof ScrolledComposite) {
			((ScrolledComposite) par).setMinSize(160, getItemCount()
					* ROWHEIGHT);
		}
		redraw();
		return pi;
	}

	public void removeAll() {
		// for (PropertyItem pi : als)
		// pi.getControl(this).dispose();
		als.clear();
		redraw();
	}

	public void paintControl(PaintEvent e) {
		if (als.size() == 0) {
			drawEmpty(e.gc);
			return;
		}
		e.gc.setForeground(ColorConstants.BLACK);
		Point size = getSize();
		for (int i = 0; i < als.size(); i++) {
			PropertyItem pi = als.get(i);
			int y = i * ROWHEIGHT;
			drawString(e.gc, pi.getLabel(), LABELSTART, y, ROWHEIGHT,
					splitLineX - LABELSTART);
			Control ctrl = pi.getControl(this);
			ctrl.setBounds(splitLineX + EDITORSTART, y + 2, size.x - splitLineX
					- EDITORSTART - 1, ROWHEIGHT - 3);
			if (!ctrl.isVisible())
				ctrl.setVisible(true);
		}
		e.gc.setForeground(ColorConstants.FIELDSET_BORDER);
		e.gc.drawRectangle(0, 0, size.x - 1, size.y - 1);
		for (int i = 1; i < als.size() + 1; i++)
			e.gc.drawLine(0, i * ROWHEIGHT, size.x, i * ROWHEIGHT);
		e.gc.drawLine(splitLineX, 0, splitLineX, size.y);
		// setSize(size.x, ROWHEIGHT * als.size());
		// getParent().layout();
	}

	private void drawEmpty(GC gc) {
		gc.setForeground(ColorConstants.TITLETEXT);
		Dimension dim = FigureUtilities.getTextExtents(EMPTYTEXT, getFont());
		Point size = getSize();
		gc.drawString(EMPTYTEXT, (size.x - dim.width) / 2,
				(size.y - dim.height) / 2);
		gc.setForeground(ColorConstants.FIELDSET_BORDER);
		gc.drawRectangle(0, 0, size.x - 1, size.y - 1);
	}

	private void drawString(GC gc, String text, int x, int y, int height,
			int width) {
		Color fgc = ColorConstants.BLACK;
		if (text.charAt(0) == '*') {
			text = text.substring(1);
			fgc = new Color(null, 4, 168, 118);
		}
		Dimension dim = FigureUtilities.getTextExtents(text, getFont());
		StringBuilder sb = new StringBuilder(text.length() + 3);
		sb.append(text);
		sb.append("...");
		while (dim.width > width && sb.length() >= 4) {
			sb.deleteCharAt(sb.length() - 4);
			dim = FigureUtilities.getTextExtents(sb.toString(), getFont());
			text = sb.toString();
		}
		gc.setForeground(fgc);
		gc.drawString(text, x, y + (height - dim.height) / 2);
	}

	public void mouseDoubleClick(MouseEvent e) {
	}

	public void mouseDown(MouseEvent e) {
		if (canResize) {
			resizing = true;
		}
	}

	public void mouseUp(MouseEvent e) {
		resizing = false;
	}

	public void mouseMove(MouseEvent e) {
		int mx = e.x;
		if (mx >= splitLineX - 1 && mx <= splitLineX + 1) {
			setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_SIZEWE));
			canResize = true;
		} else {
			setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_ARROW));
			canResize = false;
		}
		if (resizing) {
			if (mx < MINWIDTHLEFT || mx > getSize().x - MINWIDTHRIGHT)
				return;
			splitLineX = mx;
			setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_SIZEWE));
			redraw();
		}
	}

	public void mouseEnter(MouseEvent e) {

	}

	public void mouseExit(MouseEvent e) {
		resizing = false;
		setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_ARROW));
	}

	public void mouseHover(MouseEvent e) {
		if (als.size() == 0) {
			setToolTipText("No Properties");
			return;
		}
		int idx = e.y / ROWHEIGHT;
		if (idx >= als.size()) {
			setToolTipText(null);
			return;
		}
		PropertyItem pi = als.get(idx);
		setToolTipText(pi.getLabel());
	}
}
