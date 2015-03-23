package aurora.ide.editor.textpage.quickfix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.osgi.util.NLS;

import uncertain.composite.CompositeMap;
import uncertain.schema.Element;
import aurora.ide.builder.CompositeMapInfo;
import aurora.ide.builder.SxsdUtil;

public class TagProposalCreator extends AbstractProposalCreator {

	public TagProposalCreator(IDocument doc, CompositeMap rootMap,
			IMarker marker) {
		super(doc, rootMap, marker);
	}

	@Override
	protected boolean isFixable() {
		return super.isFixable() && isValidWord(getMarkerWord());
	}

	@Override
	protected void create(ArrayList<ICompletionProposal> result) {
		CompositeMap map = getCursorMap();
		IDocument doc = getDocument();
		String tagName = map.getName();
		CompositeMapInfo info = new CompositeMapInfo(map, doc);
		IRegion mapRegion = info.getMapRegion();
		IRegion mapNameRegion = info.getMapNameRegion();
		IRegion mapEndNameRegion = info.getMapEntTagNameRegion();
		boolean isSelfClose = mapNameRegion.equals(mapEndNameRegion);
		StringBuilder mapStr = null;
		int start1 = 0, start2 = 0;
		if (!isSelfClose) {
			start2 = mapEndNameRegion.getOffset() - mapRegion.getOffset();
			start1 = mapNameRegion.getOffset() - mapRegion.getOffset();
			mapStr = new StringBuilder(getString(doc, mapRegion));
		}
		for (SortElement se : getAvailableTag()) {
			String name = se.name;
			if (!isSelfClose) {
				mapStr.replace(start2, start2 + tagName.length(), name);
				mapStr.replace(start1, start1 + tagName.length(), name);
				result.add(new CompletionProposal(mapStr.toString(), mapRegion
						.getOffset(), mapRegion.getLength(), mapNameRegion
						.getOffset() - mapRegion.getOffset() + name.length(),
						img_rename, NLS.bind(Messages.Change_to, name), null,
						NLS.bind(Messages.Suggest_change_to, name)));
				continue;
			}
			result.add(new CompletionProposal(name, mapNameRegion.getOffset(),
					mapNameRegion.getLength(), name.length(), img_rename, NLS
							.bind(Messages.Change_to, name), null, NLS.bind(
							Messages.Suggest_change_to, name)));
		}
		result.add(new CompletionProposal("", mapRegion.getOffset(), mapRegion //$NON-NLS-1$
				.getLength(), 0, img_remove, NLS.bind(Messages.Delete_tag,
				tagName), null, NLS
				.bind(Messages.TagProposalCreator_2, tagName)));
	}

	private ArrayList<SortElement> getAvailableTag() {
		ArrayList<SortElement> comp = new ArrayList<SortElement>();
		CompositeMap map = getCursorMap();
		String tagName = map.getName();
		CompositeMap parent = map.getParent();
		List<Element> aChilds = SxsdUtil.getAvailableChildElements(parent);
		for (Element e : aChilds) {
			String name = e.getQName().getLocalName();
			if (name.equals(tagName))
				continue;
			int ed = QuickAssistUtil.getApproiateEditDistance(tagName, name);
			if (ed != -1) {
				comp.add(new SortElement(name, ed));
			}
		}
		Collections.sort(comp);
		return comp;
	}

	private String getString(IDocument doc, IRegion region) {
		try {
			return doc.get(region.getOffset(), region.getLength());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
