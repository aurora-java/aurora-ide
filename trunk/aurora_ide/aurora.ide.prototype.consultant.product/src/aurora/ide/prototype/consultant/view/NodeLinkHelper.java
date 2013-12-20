package aurora.ide.prototype.consultant.view;

import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IWorkbenchPage;

import aurora.ide.editor.editorInput.PathEditorInput;

public class NodeLinkHelper {

	private NavigationView commonNavigator;

	public NodeLinkHelper(NavigationView commonNavigator) {
		this.commonNavigator = commonNavigator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.navigator.ILinkHelper#findSelection(org.eclipse.ui.
	 * IEditorInput)
	 */
	public IStructuredSelection findSelection(IEditorInput anInput) {
		Node node = getNode(anInput);
		if (node != null) {
			return new StructuredSelection(node);
		}
		return StructuredSelection.EMPTY;
	}

	public Node getNode(IEditorInput anInput) {
		if (anInput instanceof IPathEditorInput) {
			IPath path = ((IPathEditorInput) anInput).getPath();
			Node rootNode = this.getRootNode(path);
			return findNode(path, rootNode);
		}
		return null;
	}

	public Node findNode(IPath path, Node rootNode) {
		Node r = rootNode;
		if (rootNode != null) {
			IPath makeRelativeTo = path.makeRelativeTo(rootNode.getPath());
			String[] segments = makeRelativeTo.segments();
			for (String string : segments) {
				if (r != null) {
					r.makeChildren();
					r = r.getChild(string);
				}
			}
		}
		if (r != null && path.equals(r.getPath()))
			return r;
		return null;
	}

	public Node getRootNode(IPath path) {
		Object input = commonNavigator.getViewer().getInput();
		if (input instanceof Root) {
			List<Node> children = ((Root) input).getChildren();
			for (Node node : children) {
				if (node.getPath().isPrefixOf(path)) {
					return node;
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.navigator.ILinkHelper#activateEditor(org.eclipse.ui.
	 * IWorkbenchPage, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void activateEditor(IWorkbenchPage aPage,
			IStructuredSelection aSelection) {
		if (aSelection == null || aSelection.isEmpty())
			return;
		Object firstElement = aSelection.getFirstElement();
		if (firstElement instanceof Node) {
			IEditorInput fileInput = new PathEditorInput(
					((Node) firstElement).getPath());
			IEditorPart editor = null;
			if ((editor = aPage.findEditor(fileInput)) != null)
				aPage.bringToTop(editor);
		}

	}

}
