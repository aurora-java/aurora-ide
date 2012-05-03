package aurora.ide.meta.gef.editors.wizard.dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.TypedListener;

import aurora.ide.meta.MetaPlugin;
import aurora.ide.meta.gef.editors.template.Template;

public class TComposite extends SashForm {

	private ImageRegistry images = MetaPlugin.getDefault().getImageRegistry();
	private Map<String, java.util.List<TLabel>> labels = new HashMap<String, java.util.List<TLabel>>();
	private String category;
	private int index = 0;
	private Template template = null;
	private int labelHeight = 120;
	private int labelWidth = 120;

	private Composite composite;
	private Composite leftComposite;
	private ScrolledComposite scrolledComposite;
	private List list;

	public TComposite(Composite parent, int style, Map<String, java.util.List<Template>> templates) {
		super(parent, style);
		createContent(templates);
	}

	public void clear() {
		labels.clear();
		category = null;
		index = 0;
		template = null;
		for (Control c : this.getChildren()) {
			if (c instanceof Sash) {
				continue;
			}
			c.dispose();
		}
	}

	public void createContent(Map<String, java.util.List<Template>> templates) {
		this.setLayout(new GridLayout(2, false));

		leftComposite = new Composite(this, SWT.None);
		leftComposite.setLayout(new GridLayout());
		leftComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		leftComposite.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		list = new List(leftComposite, SWT.NONE);
		list.setLayoutData(new GridData(GridData.FILL_BOTH));
		list.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));

		scrolledComposite = new ScrolledComposite(this, SWT.V_SCROLL);
		scrolledComposite.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		scrolledComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		scrolledComposite.getVerticalBar().setIncrement(10);// 控制垂直方向滚动增量
		scrolledComposite.setMinWidth(labelWidth * 3 + 20);

		composite = new Composite(scrolledComposite, SWT.NONE);
		composite.setLayout(new GridLayout(3, true));
		composite.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		composite.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				switch (e.keyCode) {
				case SWT.ARROW_UP:
					if (index - 3 >= 0) {
						index -= 3;
						selectLabel(labels.get(category).get(index));
					}
					break;
				case SWT.ARROW_DOWN:
					if (index + 3 < labels.get(category).size()) {
						index += 3;
						selectLabel(labels.get(category).get(index));
					}
					break;
				case SWT.ARROW_LEFT:
					if (index - 1 >= 0) {
						index--;
						selectLabel(labels.get(category).get(index));
					}
					break;
				case SWT.ARROW_RIGHT:
					if (index + 1 < labels.get(category).size()) {
						index++;
						selectLabel(labels.get(category).get(index));
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
				// 设置 mainCompoite 大小
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
		scrolledComposite.setContent(composite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		this.setWeights(new int[] { 20, 80 });
		this.setSashWidth(1);

		for (String key : templates.keySet()) {
			list.add(key);
			labels.put(key, new ArrayList<TLabel>());
			Collections.sort(templates.get(key), new Comparator<Template>() {
				public int compare(Template o1, Template o2) {
					return o1.getName().compareToIgnoreCase(o2.getName());
				}
			});
			for (Template value : templates.get(key)) {
				TLabel label = createLabel(value);
				if (label != null) {
					labels.get(key).add(label);
				}
			}
		}

		list.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int n = list.getSelectionIndex();
				if (n < 0 || n >= list.getItemCount()) {
					return;
				}
				category = list.getItem(n);
				index = 0;
				selectLabels();
			}
		});

		if (list.getItems().length > 0) {
			list.select(0);
			category = list.getItem(0);
			selectLabels();
		}
		this.layout();
	}

	private void selectLabels() {
		if (category == null) {
			return;
		}
		for (String key : labels.keySet()) {
			if (key.equals(category)) {
				for (TLabel label : labels.get(key)) {
					label.setVisible(true);
					((GridData) label.getLayoutData()).exclude = false;
				}
				continue;
			}
			for (TLabel label : labels.get(key)) {
				label.setVisible(false);
				((GridData) label.getLayoutData()).exclude = true;
			}
		}
		int x = (int) Math.ceil(labels.get(category).size() / 3.0);
		scrolledComposite.setMinHeight(x * labelHeight + x * 5 + 5);
		composite.layout(true);
		selectLabel(labels.get(category).get(index));
		notifyListeners(SWT.Selection, new Event());
	}

	private TLabel createLabel(Template t) {
		TLabel label = new TLabel(composite, SWT.CENTER);
		GridData gd = new GridData();
		gd.heightHint = labelHeight;
		gd.widthHint = labelWidth;
		label.setLayoutData(gd);
		label.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		label.setCursor(getShell().getDisplay().getSystemCursor(SWT.CURSOR_HAND));
		label.setData(t);
		// label.setStructures(getStructures(t));
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
		for (int i = 0; i < labels.get(category).size(); i++) {
			if (!labels.get(category).get(i).equals(lbl)) {
				setLabelChecked(labels.get(category).get(i), false);
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
			label.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION));
		} else {
			label.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		}
	}

	private Image getImage(String path) {
		if (images.get(path) == null) {
			Image image = MetaPlugin.imageDescriptorFromPlugin(MetaPlugin.PLUGIN_ID, path).createImage();
			images.put(path, image);
		}
		return images.get(path);
	}

	// private java.util.List<String> getStructures(Template t) {
	// java.util.List<String> structures = new ArrayList<String>();
	// for (Component c : t.getChildren()) {
	// structures.add(c.getComponentType());
	// }
	// return structures;
	// }

	public Composite getLeftComposite() {
		return leftComposite;
	}
}
