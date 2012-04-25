package aurora.ide.meta.gef.editors.wizard.dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.TypedListener;

import aurora.ide.meta.MetaPlugin;
import aurora.ide.meta.gef.editors.template.Template;

public class TComposite extends SashForm {

	private ImageRegistry images = MetaPlugin.getDefault().getImageRegistry();
	private java.util.List<TLabel> labels = new ArrayList<TLabel>();
	private int index = 0;
	private Template template = null;
	private int labelHeight = 120;
	private int labelWidth = 120;

	private Composite composite;
	private ScrolledComposite scrolledComposite;
	private List list;

	public TComposite(Composite parent, int style, final Map<String, java.util.List<Template>> templates) {
		super(parent, style);
		this.setLayout(new GridLayout(2, false));

		list = new List(this, SWT.NONE);
		list.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		list.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));

		scrolledComposite = new ScrolledComposite(this, SWT.V_SCROLL);
		scrolledComposite.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		scrolledComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		scrolledComposite.getVerticalBar().setIncrement(10);// 控制垂直方向滚动增量

		composite = new Composite(scrolledComposite, SWT.NONE);
		composite.setLayout(new GridLayout(3, true));
		composite.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		composite.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				switch (e.keyCode) {
				case SWT.ARROW_UP:
					if (index - 3 >= 0) {
						index -= 3;
						selectLabel(labels.get(index));
					}
					break;
				case SWT.ARROW_DOWN:
					if (index + 3 < labels.size()) {
						index += 3;
						selectLabel(labels.get(index));
					}
					break;
				case SWT.ARROW_LEFT:
					if (index - 1 >= 0) {
						index--;
						selectLabel(labels.get(index));
					}
					break;
				case SWT.ARROW_RIGHT:
					if (index + 1 < labels.size()) {
						index++;
						selectLabel(labels.get(index));
					}
					break;
				default:
					super.keyPressed(e);
					break;
				}
			}
		});

		composite.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				composite.forceFocus();
			}
		});

		composite.addControlListener(new ControlAdapter() {
			public void controlResized(org.eclipse.swt.events.ControlEvent e) {
				// 计算 mainComposite 大小
				Point size = composite.computeSize(composite.getSize().x, SWT.DEFAULT);
				// 设置 mainCompoite 大小，如果不设置的话，就会出现上述的问题6
				composite.setSize(size);
				// 重新设置滚动条大小
				scrolledComposite.getVerticalBar().setMaximum(size.y);
				// 设置scrolledComposite的 minHeight
				scrolledComposite.setMinHeight(size.y);
				// 获取当前滚动位置，如果mainComposite的大小发生了变化，那么要确保大小变化后的滚动位置是正确的
				// 否则就会在mainComposite下方出现一段空白的位置。
				int scrollHeight = scrolledComposite.getVerticalBar().getSelection() + scrolledComposite.getClientArea().height;
				// 如果mainComposite高度变小了，那么滚动的位置重新设置
				if (scrollHeight >= size.y) {
					scrolledComposite.setOrigin(0, size.y);
				}
			}
		});

		this.setWeights(new int[] { 20, 80 });
		this.setSashWidth(1);

		for (String key : templates.keySet()) {
			list.add(key);
		}

		list.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				createLabels(templates);
				notifyListeners(SWT.Selection, new Event());
				index = 0;
			}

		});

		if (list.getItems().length > 0) {
			list.select(0);
			createLabels(templates);
		}
	}

	private void createLabels(Map<String, java.util.List<Template>> templates) {
		String[] category = list.getSelection();
		if (category == null || category.length <= 0) {
			return;
		}
		java.util.List<Template> tm = templates.get(category[0]);
		for (Control c : composite.getChildren()) {
			if (c != null && !c.isDisposed()) {
				c.dispose();
			}
		}
		labels.clear();
		Collections.sort(tm, new Comparator<Template>() {
			public int compare(Template o1, Template o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		});
		for (Template t : tm) {
			labels.add(createLabel(t));
		}
		template = (Template) labels.get(0).getData();
		setLabelChecked(labels.get(0), true);

		scrolledComposite.setContent(composite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		int x = (int) Math.ceil(tm.size() / 3.0);
		scrolledComposite.setMinWidth(labelWidth * 3 + 20);
		scrolledComposite.setMinHeight(x * labelHeight + x * 5 + 5);

		composite.layout(true);
	}

	private TLabel createLabel(Template t) {
		TLabel label = new TLabel(composite, SWT.CENTER);
		label.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		label.setCursor(getShell().getDisplay().getSystemCursor(SWT.CURSOR_HAND));
		label.setData(t);
		GridData gd = new GridData();
		gd.heightHint = labelHeight;
		gd.widthHint = labelWidth;
		label.setLayoutData(gd);
		label.setText(t.getName());
		label.setImage(getImage(t.getIcon()));
		label.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				TLabel lbl = (TLabel) e.getSource();
				selectLabel(lbl);
				composite.forceFocus();
			}

		});
		return label;
	}

	private void selectLabel(TLabel lbl) {
		setLabelChecked(lbl, true);
		template = (Template) lbl.getData();
		notifyListeners(SWT.Selection, new Event());
		for (int i = 0; i < labels.size(); i++) {
			if (labels.get(i) != lbl) {
				setLabelChecked(labels.get(i), false);
			} else {
				index = i;
			}
		}
		Point p = new Point(lbl.getLocation().x, lbl.getLocation().y + composite.getLocation().y);
		if (p.y < 0) {
			scrolledComposite.setOrigin(0, lbl.getLocation().y - 5);
		} else {
			p.y = p.y + lbl.getSize().y - scrolledComposite.getClientArea().height;
			if (p.y > 0) {
				scrolledComposite.setOrigin(0, scrolledComposite.getOrigin().y + p.y + 5);
			}
		}
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

	private void setLabelChecked(TLabel label, boolean chencked) {
		if (chencked) {
			// label.checked();
			label.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION));
		} else {
			// label.unChecked();
			label.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		}
	}

	private Image getImage(String path) {
		if (images.get(path) == null) {

			// // IPath append = ICONS_PATH.append(getPath(key));
			// ImageDescriptor imageDescriptor = AuroraPlugin
			// .getImageDescriptor(path);
			// // PLUGIN_REGISTRY.put(key, imageDescriptor);
			// // image = PLUGIN_REGISTRY.get(key);

			Image image = new Image(getDisplay(), path);
			images.put(path, image);
		}
		return images.get(path);
	}
}
