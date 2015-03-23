package aurora.ide.editor.textpage.quickfix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.osgi.util.NLS;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import aurora.ide.builder.CompositeMapInfo;
import aurora.ide.builder.SxsdUtil;

public class AttrProposalCreator extends AbstractProposalCreator {

	public AttrProposalCreator(IDocument doc, CompositeMap rootMap,
			IMarker marker) {
		super(doc, rootMap, marker);
	}

	protected boolean isFixable() {
		return super.isFixable()
				&& getCursorMap().getString(getMarkerWord()) != null;
	}

	@Override
	protected void create(ArrayList<ICompletionProposal> result) {
		CompositeMap map = getCursorMap();
		String word = getMarkerWord();
		CompositeMapInfo info = new CompositeMapInfo(map, getDocument());
		IRegion attrRegion = info.getAttrRegion(word);
		if (attrRegion == null)
			return;
		ArrayList<SortElement> comp = getAvailableAttrNames(map);
		for (SortElement se : comp) {
			String attrName = se.name;
			CompletionProposal cp = new CompletionProposal(attrName,
					attrRegion.getOffset(), word.length(), attrName.length(),
					img_rename, NLS.bind(Messages.AttrProposalCreator_0, word,
							attrName), null, NLS.bind(
							Messages.AttrProposalCreator_2, attrName));
			result.add(cp);
		}
		result.add(new CompletionProposal(
				"", attrRegion.getOffset() - 1, //$NON-NLS-1$
				attrRegion.getLength() + 1, 0, img_remove,
				Messages.AttrProposalCreator_4, null,
				Messages.AttrProposalCreator_5));
	}

	/**
	 * get attribute name already used in this map
	 * 
	 * @param map
	 * @return always in lower case
	 */
	private Set<String> getUsedAttrNames(CompositeMap map) {
		@SuppressWarnings("unchecked")
		Set<Map.Entry<String, String>> enterySet = map.entrySet();
		Set<String> attrSet = new HashSet<String>();
		for (Map.Entry<String, String> e : enterySet) {
			if (e.getKey() != null)
				attrSet.add(e.getKey().toLowerCase());
		}
		return attrSet;
	}

	private ArrayList<SortElement> getAvailableAttrNames(CompositeMap map) {
		ArrayList<SortElement> comp = new ArrayList<SortElement>();
		Set<String> attrSet = getUsedAttrNames(map);
		List<Attribute> definedAttribute;
		try {
			definedAttribute = SxsdUtil.getAttributesNotNull(map);
		} catch (Exception e) {
			return comp;
		}
		for (Attribute attr : definedAttribute) {
			String aname = attr.getName();
			if (attrSet.contains(aname.toLowerCase()))
				continue;
			int ed = QuickAssistUtil.getApproiateEditDistance(getMarkerWord(),
					aname);
			if (ed > 0)
				comp.add(new SortElement(aname, ed));
		}
		Collections.sort(comp);
		return comp;
	}
}
