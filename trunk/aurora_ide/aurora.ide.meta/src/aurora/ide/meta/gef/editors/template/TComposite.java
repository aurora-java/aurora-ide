package aurora.ide.meta.gef.editors.template;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TypedListener;

import aurora.ide.meta.MetaPlugin;

public class TComposite extends Composite {

	private CLabel[] labels = new CLabel[0];
	private Template template;
	private ImageRegistry images = MetaPlugin.getDefault().getImageRegistry();

	private Composite composite;

	public TComposite(Composite parent, int style, java.util.List<Template> templates) {
		super(parent, style);
		this.setLayout(new FillLayout());
		ScrolledComposite scrolledComposite = new ScrolledComposite(this, SWT.V_SCROLL);
		composite = new Composite(scrolledComposite, SWT.None);
		scrolledComposite.setContent(composite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setMinWidth(parent.getSize().y);
		scrolledComposite.setMinHeight(templates.size()*100/3);
		createTemplate(templates);
	}

	private void createTemplate(java.util.List<Template> templates) {
		labels = new CLabel[templates.size()];
		for (int i = 0; i < labels.length; i++) {
			labels[i] = createLabel(templates.get(i));
		}
		if (labels.length > 0) {
			setLabelChecked(labels[0], true);
			template = templates.get(0);
		}
	}

	private CLabel createLabel(Template t) {
		// String path = MetaPlugin.getDefault().getStateLocation().toString();
		composite.setLayout(new GridLayout(3, true));
		composite.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		CLabel label = new CLabel(composite, SWT.CENTER);
		label.setCursor(getShell().getDisplay().getSystemCursor(SWT.CURSOR_HAND));
		label.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		label.setData(t);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 100;
		label.setLayoutData(gd);
		label.setText(t.getName());
		// label.setImage(getImage(t.getIcon()));
		label.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				CLabel lbl = (CLabel) e.getSource();
				setLabelChecked(lbl, true);
				template = (Template) lbl.getData();
				notifyListeners(SWT.Selection, new Event());
				for (int i = 0; i < labels.length; i++) {
					if (labels[i] != lbl) {
						setLabelChecked(labels[i], false);
					}
				}
			}
		});
		return label;
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

	public Template getSelection() {
		return template;
	}

	private void setLabelChecked(CLabel label, boolean chencked) {
		if (chencked) {
			label.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		} else {
			label.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		}
	}

	private Image getImage(String path) {
		if (images.get(path) == null) {
			Image image = new Image(getDisplay(), path);
			images.put(path, image);
		}
		return images.get(path);
	}
}
