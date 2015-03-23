package aurora.ide.editor.outline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.xml.sax.SAXException;

import uncertain.schema.Element;
import uncertain.schema.ISchemaManager;
import aurora.ide.AuroraPlugin;
import aurora.ide.editor.textpage.TextPage;
import aurora.ide.helpers.ImagesUtils;
import aurora.ide.helpers.LoadSchemaManager;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.helpers.LogUtil;

public class TextOutlinePage extends ContentOutlinePage {
	protected TextPage editor;

	private Selected selected = new Selected();
	private static List<String> arrays = new ArrayList<String>();

	public TextOutlinePage(IEditorPart activeEditor) {
		this.editor = (TextPage) activeEditor;
		IDocument inputDocument = editor.getInputDocument();
		inputDocument.addDocumentListener(new DocumentListener());
		if (arrays.size() <= 0) {
			ISchemaManager schemaManager = LoadSchemaManager.getSchemaManager();
			for (Object obj : schemaManager.getAllTypes()) {
				// System.out.println(obj);
				if (obj instanceof Element) {
					initArray((Element) obj);
				}
			}
		}
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		getTreeViewer().addSelectionChangedListener(selected);
		getTreeViewer().setLabelProvider(new OutlineLabelProvider());
		getTreeViewer().setContentProvider(new OutlineContentProvider());
		getTreeViewer().setInput(loadTree());
		IAction collapse = new Action("aurora.outline", SWT.NONE) {
			@Override
			public void run() {
				getTreeViewer().collapseAll();
			}
		};
		collapse.setToolTipText("Collapse All");
		collapse.setImageDescriptor(AuroraPlugin.getImageDescriptor("icons/collapseall.gif"));
		getSite().getActionBars().getToolBarManager().add(collapse);
		super.setActionBars(getSite().getActionBars());
	}

	private void initArray(Element ele) {
		if (ele.isArray()) {
			arrays.add(ele.getLocalName());
		}
		if (ele.getChilds() == null) {
			return;
		}
		for (Object obj : ele.getChilds()) {
			if (obj instanceof Element) {
				initArray((Element) obj);
			}
		}
	}

	private OutlineTree loadTree() {
		IDocument document = editor.getInputDocument();
		OutlineParser p = new OutlineParser(document.get());
		try {
			p.parser();
			return p.getTree();
		} catch (ParserConfigurationException e) {
			LogUtil.getInstance().logError(e.getMessage(), e);
		} catch (SAXException e) {
			// LogUtil.getInstance().logWarning("outline parser", e);
		} catch (IOException e) {
			LogUtil.getInstance().logError(e.getMessage(), e);
		}
		return null;
	}

	public void selectNode(int offset) {
		if (getTreeViewer() == null) {
			return;
		}
		OutlineTree root = (OutlineTree) getTreeViewer().getInput();
		OutlineTree tree = getTree(root == null ? null : root.getChild(0), offset);
		if (tree == null) {
			if (root != null && root.getChild(0) != null) {
				tree = root.getChild(0);
			} else {
				return;
			}
		}
		getTreeViewer().removeSelectionChangedListener(selected);
		getTreeViewer().setSelection(new StructuredSelection(tree));
		getTreeViewer().addSelectionChangedListener(selected);
	}

	private OutlineTree getTree(OutlineTree tree, int offset) {
		if (tree == null) {
			return null;
		}
		if (tree.getRegion() != null) {
			if (tree.getRegion().getOffset() <= offset
					&& tree.getRegion().getOffset() + tree.getRegion().getLength() + 1 > offset) {
				return tree;
			} else {
				return null;
			}
		}
		if (tree.getStartRegion().getOffset() <= offset
				&& tree.getEndRegion().getOffset() + tree.getEndRegion().getLength() + 2 > offset) {
			for (OutlineTree child : tree.getChildren()) {
				OutlineTree t = getTree(child, offset);
				if (t != null) {
					return t;
				}
			}
		} else {
			return null;
		}
		return tree;
	}

	private boolean eq(Object o1, Object o2) {
		if (o1 == null) {
			return o1 == o2;
		}
		return o1.equals(o2);
	}

	private void refresh(OutlineTree tree, OutlineTree input) {
		if (tree == null || input == null) {
			return;
		}
		if (!eq(tree, input)) {
			if (eq(tree.getText(), input.getText()) && eq(tree.getOther(), input.getOther())) {
				input.copy(tree);
			} else {
				input.copy(tree);
				getTreeViewer().refresh(input);
			}
		}
		if (tree.getChildrenCount() != input.getChildrenCount()) {
			input.removeAll();
			for (int i = 0; i < tree.getChildrenCount(); i++) {
				input.add(tree.getChild(i));
			}
			getTreeViewer().refresh(input);
		} else {
			for (int i = 0; i < tree.getChildrenCount(); i++) {
				refresh(tree.getChild(i), input.getChild(i));
			}
		}
	}

	private Image getOutlineTreeImage(OutlineTree tree) {
		if ("array".equals(tree.getImage())) {
			return ImagesUtils.getImage("array.gif");
//			return AuroraPlugin.getImageDescriptor("icons/array.gif").createImage();
		} else if ("script".equals(tree.getImage())) {
			return ImagesUtils.getImage("script.png");
//			return AuroraPlugin.getImageDescriptor("icons/script.png").createImage();
		} else if ("method".equals(tree.getImage())) {
			return ImagesUtils.getImage("method.gif");
//			return AuroraPlugin.getImageDescriptor("icons/method.gif").createImage();
		} else if ("variable".equals(tree.getImage())) {
			return ImagesUtils.getImage("variable.gif");
//			return AuroraPlugin.getImageDescriptor("icons/variable.gif").createImage();
		}
		String defaultPath = LocaleMessage.getString("element.icon");
//		icons/element.gif
		return ImagesUtils.getImage("element.gif");
//		return AuroraPlugin.getImageDescriptor(defaultPath).createImage();
	}

	class OutlineLabelProvider extends BaseLabelProvider implements ILabelProvider {
		public String getText(Object obj) {
			String name = ((OutlineTree) obj).getText();
			int loc = name.indexOf(":");
			if (loc >= 0 && arrays.contains(name.substring(loc + 1))) {
				((OutlineTree) obj).setOther("[" + ((OutlineTree) obj).getChildrenCount() + "]");
			}
			return obj.toString();
		}

		public Image getImage(Object element) {
			String name = ((OutlineTree) element).getOther();
			if (name.matches("\\[\\d+\\]")) {
				return ImagesUtils.getImage("array.gif");
//				return AuroraPlugin.getImageDescriptor(LocaleMessage.getString("array.icon")).createImage();
			}
			return getOutlineTreeImage((OutlineTree) element);
		}
	}

	class Selected implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent event) {
			TreeSelection selection = (TreeSelection) event.getSelection();
			OutlineTree lt = (OutlineTree) selection.getFirstElement();
			if (lt == null) {
				return;
			}
			IRegion region = lt.getStartRegion();
			// IRegion region = lt.getEndRegion();
			TextSelection tt = new TextSelection(region.getOffset(), region.getLength());
			editor.getEditorSite().getSelectionProvider().setSelection(tt);
		}
	}

	class OutlineContentProvider implements ITreeContentProvider {
		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			return ((OutlineTree) inputElement).getChildren().toArray();
		}

		public Object[] getChildren(Object parentElement) {
			return ((OutlineTree) parentElement).getChildren().toArray();
		}

		public Object getParent(Object element) {
			return ((OutlineTree) element).getParent();
		}

		public boolean hasChildren(Object element) {
			return ((OutlineTree) element).getChildren().size() > 0;
		}

	}

	class DocumentListener implements IDocumentListener {
		public void documentAboutToBeChanged(DocumentEvent event) {

		}

		public void documentChanged(DocumentEvent event) {
			if (getTreeViewer() == null||getTreeViewer().getTree().isDisposed()) {
				return;
			}
			OutlineTree tree = loadTree();
			if (tree == null) {
				return;
			}
			if (getTreeViewer().getInput() == null) {
				getTreeViewer().setInput(tree);
			} else {
				refresh(tree, (OutlineTree) getTreeViewer().getInput());
			}
		}
	}
}
