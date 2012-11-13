package aurora.ide.meta.gef.editors.wizard.dialog;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TypedListener;

import aurora.ide.meta.ImageFromPlugin;
import aurora.ide.meta.MetaPlugin;

public class TLabel extends Composite {

	private String text = null;
	private java.util.List<String> structures = null;
	private Image image;

	private Label canvas;
	private Label label;

	public TLabel(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout());

		canvas = new Label(this, SWT.NONE);
		canvas.setLayoutData(new GridData(GridData.FILL_BOTH));
		canvas.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		canvas.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				notifyListeners(SWT.MouseDown, new Event());
			}
		});
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				if (structures != null) {
					int hegiht = 0;
					GC gc = e.gc;
					for (String s : structures) {
//						ImageDescriptor image = MetaPlugin.imageDescriptorFromPlugin(MetaPlugin.PLUGIN_ID, "template/thumbnails/" + s + ".png");
						Image image =ImageFromPlugin.getImage("template/thumbnails/" + s + ".png");
						if (image != null) {
							gc.drawImage(image, 3, hegiht + 3);
							hegiht = image.getImageData().height + 3;

						}
					}
				} else if (image != null) {
					GC gc = e.gc;
					gc.drawImage(image, (canvas.getBounds().width - image.getImageData().width) / 2, (canvas.getBounds().height - image.getImageData().height) / 2);
				}
			}
		});		
		
		label = new Label(this, SWT.CENTER | SWT.NONE);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				notifyListeners(SWT.MouseDown, new Event());
			}
		});

		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				onPaint(e);
			}
		});

	}

	public void addSelectionListener(SelectionListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		TypedListener typedListener = new TypedListener(listener);
		addListener(SWT.Selection, typedListener);
		addListener(SWT.DefaultSelection, typedListener);
	}

	protected void onPaint(PaintEvent e) {
		canvas.redraw();
		if (text != null) {
			label.setText(text);
			label.setBackground(this.getBackground());
		} else {
			label.setText("");
		}
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public java.util.List<String> getStructures() {
		return structures;
	}

	public void setStructures(java.util.List<String> structures) {
		this.structures = structures;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

}
