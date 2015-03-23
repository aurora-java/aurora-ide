package aurora.ide.editor.widgets;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbenchActionConstants;

import uncertain.composite.CompositeMap;
import aurora.ide.AuroraPlugin;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.component.wizard.ActionsFactory;
import aurora.ide.editor.AbstractCMViewer;
import aurora.ide.editor.core.IViewer;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.ImagesUtils;
import aurora.ide.helpers.LoadSchemaManager;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.node.action.ActionInfo;
import aurora.ide.node.action.ActionListener;
import aurora.ide.node.action.CopyElementAction;
import aurora.ide.node.action.ElementDoubleClickListener;
import aurora.ide.node.action.PasteAction;
import aurora.ide.node.action.RefreshAction;
import aurora.ide.node.action.RemoveElementAction;

public class CompositeMapTreeViewer extends AbstractCMViewer {
	protected TreeViewer treeViewer;
	protected IViewer parentViewer;
	private CompositeMap input;
	public final static String VirtualNode = "VirtualNode";

	public CompositeMapTreeViewer(IViewer parentViewer, CompositeMap data) {
		this.parentViewer = parentViewer;
		this.input = data;

	}

	public void create(Composite parent) {
		ViewForm viewForm = new ViewForm(parent, SWT.NONE);
		viewForm.setLayout(new FillLayout());
		Tree tree = new Tree(viewForm, SWT.NONE);
		treeViewer = new TreeViewer(tree);
		treeViewer.setLabelProvider(new CompositeMapTreeLabelProvider());
		CompositeMap parentData = input.getParent();
		if (parentData == null) {
			parentData = createVirtualParentNode(input);
		}
		treeViewer
				.setContentProvider(new CompositeMapTreeContentProvider(input));
		treeViewer.setInput(parentData);

		fillContextMenu();
		// fillDNDListener();
		fillKeyListener();
		treeViewer.addDoubleClickListener(new ElementDoubleClickListener(this));
		viewForm.setContent(treeViewer.getControl());
		fillElementToolBar(viewForm);
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		treeViewer.addSelectionChangedListener(listener);
	}

	public Control getControl() {
		return treeViewer.getControl();
	}

	public Object getViewer() {
		return treeViewer;
	}

	public void setSelection(Object data) {
		selectedData = (CompositeMap) data;

	}

	public void refresh() {
		treeViewer.refresh();
	}

	public void setFocus(Object data) {
		focusData = (CompositeMap) data;
	}

	public void setDirty(boolean dirty) {
		parentViewer.refresh(true);

	}

	public void refresh(boolean dirty) {
		if (dirty) {
			parentViewer.refresh(true);
		} else {
			treeViewer.refresh();
		}

	}

	public void setInput(CompositeMap data) {

		CompositeMap parent = data.getParent();
		if (parent == null) {
			parent = createVirtualParentNode(data);
		}
		treeViewer
				.setContentProvider(new CompositeMapTreeContentProvider(data));
		treeViewer.setInput(parent);
	}

	public CompositeMap getInput() {
		return input;
	}

	public TreeViewer getTreeViewer() {
		return treeViewer;
	}

	public MenuManager addChildElements() throws ApplicationException {
		String text = LocaleMessage.getString("add.element.label");
		ImageDescriptor imageDes = AuroraPlugin
				.getImageDescriptor(LocaleMessage.getString("add.icon"));
		MenuManager childElementMenus = new MenuManager(text, imageDes, null);
		final CompositeMap comp = focusData;

		ActionInfo actionProperties = new ActionInfo(this, comp);
		ActionsFactory.getInstance().addActionsToMenuManager(childElementMenus,
				actionProperties);
		return childElementMenus;
	}

	public void fillContextMenu() {
		MenuManager mgr = new MenuManager("#PopupMenu");
		MenuManager menuManager = (MenuManager) mgr;
		mgr.setRemoveAllWhenShown(true);

		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(new Separator(
						IWorkbenchActionConstants.MB_ADDITIONS));
				MenuManager childElements;
				try {
					childElements = addChildElements();
					manager.add(childElements);
				} catch (ApplicationException e) {
					DialogUtil.showExceptionMessageBox(e);
				}
				manager.add(new CopyElementAction(CompositeMapTreeViewer.this,
						CopyElementAction.getDefaultImageDescriptor(),
						CopyElementAction.getDefaultText()));
				manager.add(new PasteAction(CompositeMapTreeViewer.this,
						ActionListener.DefaultImage
								| ActionListener.DefaultTitle));
				manager.add(new RemoveElementAction(
						CompositeMapTreeViewer.this,
						ActionListener.DefaultImage
								| ActionListener.DefaultTitle));
				manager.add(new RefreshAction(CompositeMapTreeViewer.this,
						ActionListener.DefaultImage
								| ActionListener.DefaultTitle));
			}
		});

		Menu menu = menuManager.createContextMenu(getControl());
		// Menu menu = menuManager.createMenuBar(mColumnViewer.)
		getControl().setMenu(menu);

	}

	public void fillKeyListener() {
		treeViewer.getTree().addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.stateMask == SWT.CTRL && e.keyCode == 'c') {
					copyElement();
				} else if (e.stateMask == SWT.CTRL && e.keyCode == 'v') {
					pasteElement();
				} else if (e.keyCode == SWT.DEL) {
					removeElement();
				}
			}

			public void keyReleased(KeyEvent e) {
			}
		});

	}

	private void fillElementToolBar(Composite shell) {

		ToolBar toolBar = new ToolBar(shell, SWT.RIGHT | SWT.FLAT);
		Menu menu = new Menu(shell);

		ToolItem addItem = new ToolItem(toolBar, SWT.DROP_DOWN);
		setToolItemShowProperty(addItem,
				LocaleMessage.getString("add.element.label"), "add.gif");
		addItem.addListener(SWT.Selection, new ToolBarAddElementListener(
				toolBar, menu, addItem, this));

		final ToolItem cutItem = new ToolItem(toolBar, SWT.PUSH);
		setToolItemShowProperty(cutItem, LocaleMessage.getString("cut"),
				"cut.gif");
		cutItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				cutElement();
			}
		});

		final ToolItem copyItem = new ToolItem(toolBar, SWT.PUSH);
		setToolItemShowProperty(copyItem, LocaleMessage.getString("copy"),
				"copy.gif");
		copyItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				copyElement();
			}
		});

		final ToolItem pasteItem = new ToolItem(toolBar, SWT.PUSH);
		setToolItemShowProperty(pasteItem, LocaleMessage.getString("paste"),
				"paste.gif");
		pasteItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				pasteElement();
			}
		});
		final ToolItem refreshItem = new ToolItem(toolBar, SWT.PUSH);
		setToolItemShowProperty(refreshItem,
				LocaleMessage.getString("refresh"), "refresh.gif");
		refreshItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				treeViewer.refresh();
				LoadSchemaManager.refeshSchemaManager();
			}
		});
		final ToolItem removeItem = new ToolItem(toolBar, SWT.PUSH);
		setToolItemShowProperty(removeItem, LocaleMessage.getString("delete"),
				"delete.gif");
		removeItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				removeElement();

			}
		});
		toolBar.pack();
		((ViewForm) shell).setTopLeft(toolBar);
	}

	private void setToolItemShowProperty(ToolItem toolItem, String text,
			String iconPath) {
		if (text != null && !text.equals(""))
			toolItem.setToolTipText(text);
		if (iconPath != null && !iconPath.equals("")) {
			// Image icon =
//			 AuroraPlugin.getImageDescriptor(iconPath).createImage();
			Image icon = ImagesUtils.getImage(iconPath);
			toolItem.setImage(icon);
		}

	}

	private CompositeMap createVirtualParentNode(CompositeMap node) {
		if (node == null)
			return null;
		CompositeMap parentNode = node.getParent();
		if (parentNode != null)
			return parentNode;
		CompositeMap virtualNode = new CommentCompositeMap(VirtualNode);
		virtualNode.addChild(node);
		return virtualNode;
	}
}
