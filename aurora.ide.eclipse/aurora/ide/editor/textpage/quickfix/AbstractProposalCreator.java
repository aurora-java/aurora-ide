package aurora.ide.editor.textpage.quickfix;

import java.util.ArrayList;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;

import uncertain.composite.CompositeMap;
import aurora.ide.AuroraPlugin;
import aurora.ide.helpers.ImagesUtils;

public abstract class AbstractProposalCreator {
	public static Image img_new = null;
	public static Image img_remove = null;
	public static Image img_rename = null;
	static {
		try {
			img_new =
			// AuroraPlugin.getImageDescriptor("/icons/add.gif")
			// .createImage();
			ImagesUtils.getImage("add.gif");
			img_remove =
			// AuroraPlugin.getImageDescriptor("/icons/delete.gif")
			// .createImage();
			ImagesUtils.getImage("delete.gif");
			img_rename =
			// AuroraPlugin.getImageDescriptor("/icons/rename.gif")
			// .createImage();
			ImagesUtils.getImage("rename.gif");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private CompositeMap rootMap;
	private IDocument doc;
	private IMarker marker;
	private IRegion markerRegion;
	private int line = 0;
	private String word;
	private String markerType;
	private int markerOffset;
	private int markerLength;

	private CompositeMap cursorMap;
	private ArrayList<ICompletionProposal> results = new ArrayList<ICompletionProposal>();

	public AbstractProposalCreator(IDocument doc, CompositeMap rootMap,
			IMarker marker) {
		this.rootMap = rootMap;
		this.doc = doc;
		this.marker = marker;
		markerOffset = marker.getAttribute(IMarker.CHAR_START, 0);
		markerLength = marker.getAttribute(IMarker.CHAR_END, 0) - markerOffset;
		markerRegion = new Region(markerOffset, markerLength);
		try {
			line = doc.getLineOfOffset(markerOffset);
			word = doc.get(markerOffset, markerLength);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		try {
			markerType = marker.getType();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		cursorMap = QuickAssistUtil.findMap(rootMap, doc, markerOffset);
	}

	protected CompositeMap getRootMap() {
		return rootMap;
	}

	protected CompositeMap getCursorMap() {
		return cursorMap;
	}

	protected IDocument getDocument() {
		return doc;
	}

	protected int getMarkerOffset() {
		return markerOffset;
	}

	protected int getMarkerLength() {
		return markerLength;
	}

	protected IRegion getMarkerRegion() {
		return markerRegion;
	}

	protected int getMarkerLine() {
		return line;
	}

	protected String getMarkerWord() {
		return word;
	}

	protected IMarker getMarker() {
		return marker;
	}

	protected String getMarkerType() {
		return markerType;
	}

	protected boolean isFixable() {
		if (markerType == null || word == null || rootMap == null
				|| doc == null)
			return false;
		return true;
	}

	protected boolean isValidWord(String w) {
		char[] ics = { '\'', '"', ' ', '\t', '\r', '\n' };
		for (char c : ics) {
			if (w.indexOf(c) != -1)
				return false;
		}
		return true;
	}

	protected abstract void create(ArrayList<ICompletionProposal> result);

	public ICompletionProposal[] createProposal() {
		if (!isFixable())
			return null;
		create(results);
		ICompletionProposal[] cps = new ICompletionProposal[results.size()];
		results.toArray(cps);
		return cps;
	}
}
