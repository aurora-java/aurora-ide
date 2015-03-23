package aurora.ide.editor.textpage.quickfix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.osgi.util.NLS;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.api.composite.map.CommentXMLOutputter;
import aurora.ide.builder.CompositeMapInfo;

public class DsProposalCreator extends AbstractProposalCreator {
	private String LS = CommentXMLOutputter.LINE_SEPARATOR;

	public DsProposalCreator(IDocument doc, CompositeMap rootMap, IMarker marker) {
		super(doc, rootMap, marker);
	}

	@Override
	protected boolean isFixable() {
		return super.isFixable() && isValidWord(getMarkerWord());
	}

	@Override
	protected void create(ArrayList<ICompletionProposal> result) {
		IRegion region = getMarkerRegion();
		for (SortElement se : getAvailableDs()) {
			String dsn = se.name;
			CompletionProposal cp = new CompletionProposal(dsn,
					region.getOffset(), region.getLength(), dsn.length(),
					img_rename, NLS.bind(Messages.Change_to, dsn), null,
					NLS.bind(Messages.Suggest_change_to, dsn));
			result.add(cp);
		}
		ICompletionProposal cp = getCreateNewDsPropospal();
		if (cp != null)
			result.add(cp);
	}

	private ICompletionProposal getCreateNewDsPropospal() {
		CompositeMap[] pathMap = getDataSetsPathMaps();
		if (pathMap[0] == null)
			return null;
		int insertOffset = 0;
		String insertTag = ""; //$NON-NLS-1$
		CompositeMap dataSetMap = new CommentCompositeMap();// new dataset to
															// create
		@SuppressWarnings("unchecked")
		Map<String, String> nsMapping = getRootMap().getNamespaceMapping();
		CompositeMap map = getCursorMap();
		String word = getMarkerWord();
		IDocument doc = getDocument();
		String aPrefix = map
				.getString("http://www.aurora-framework.org/application"); //$NON-NLS-1$
		if (aPrefix == null)
			aPrefix = "a"; //$NON-NLS-1$
		dataSetMap.setNamespaceMapping(nsMapping);
		dataSetMap.setName("dataSet"); //$NON-NLS-1$
		dataSetMap.setPrefix(aPrefix);
		dataSetMap.put("id", word); //$NON-NLS-1$
		int replaceLength = 0;
		if (pathMap[2] == null) {// if dataSets tag not exists
			CompositeMap dataSetsMap = new CommentCompositeMap();
			dataSetsMap.setNamespaceMapping(nsMapping);
			dataSetsMap.setName("dataSets"); //$NON-NLS-1$
			dataSetsMap.setPrefix(aPrefix);
			dataSetsMap.addChild(dataSetMap);
			String prefix = ""; //$NON-NLS-1$
			CompositeMapInfo info;
			if (pathMap[1] == null) {// if script tag not exists,then insert in
										// view as the fist child
				info = new CompositeMapInfo(pathMap[0], doc);
				prefix = info.getLeadPrefix()
						+ CommentXMLOutputter.DEFAULT_INDENT;
				IRegion region = info.getStartTagRegion();
				insertOffset = region.getOffset() + region.getLength();
				insertTag = LS + prefix
						+ dataSetsMap.toXML().trim().replace(LS, LS + prefix);

			} else {// insert after after view tag
				info = new CompositeMapInfo(pathMap[1], doc);
				prefix = info.getLeadPrefix();
				IRegion endRegion = info.getEndTagRegion();
				insertOffset = endRegion.getOffset() + endRegion.getLength();
				insertTag = LS + prefix
						+ dataSetsMap.toXML().trim().replace(LS, LS + prefix);
			}
		} else {
			CompositeMap outerDsMap = getOuterDataSetMap(map);
			/*
			 * if cursor map is in a dataSet,then insert a new ds before the
			 * dataset
			 */
			if (outerDsMap != null) {
				CompositeMapInfo info = new CompositeMapInfo(outerDsMap, doc);
				IRegion region = info.getStartTagRegion();
				String prefix = info.getLeadPrefix();
				insertOffset = region.getOffset();
				insertTag = dataSetMap.toXML().trim() + LS + prefix;
			}
			/*
			 * insert at the tail of datasets tag
			 */
			else {
				CompositeMapInfo info = new CompositeMapInfo(pathMap[2], doc);
				IRegion region = info.getMapRegion();
				String prefix = info.getLeadPrefix();
				clearnsURI(pathMap[2]);
				replaceLength = region.getLength();
				pathMap[2].addChild(dataSetMap);
				insertOffset = region.getOffset();
				insertTag = pathMap[2].toXML().trim().replace(LS, LS + prefix);
			}
		}
		return new CompletionProposal(insertTag, insertOffset, replaceLength,
				insertTag.length(), img_new,
				NLS.bind(Messages.Create_ds, word), null, NLS.bind(
						Messages.Create_ds, word));
	}

	private void clearnsURI(CompositeMap map) {
		map.iterate(new IterationHandle() {

			public int process(CompositeMap map) {
				map.setNameSpaceURI(null);
				return 0;
			}
		}, true);
	}

	private CompositeMap getOuterDataSetMap(CompositeMap map) {
		while (map != null && !map.getName().equalsIgnoreCase("dataSet")) //$NON-NLS-1$
			map = map.getParent();
		return map;
	}

	/**
	 * try to get 3 node : view ,script(just under view),datasets
	 * 
	 * @param rootMap
	 * @return
	 */
	private CompositeMap[] getDataSetsPathMaps() {
		final CompositeMap[] path = new CompositeMap[3];
		getRootMap().iterate(new IterationHandle() {

			public int process(CompositeMap map) {
				if (map.getName().equalsIgnoreCase("view")) { //$NON-NLS-1$
					path[0] = map;
				} else if (map.getName().equalsIgnoreCase("script") //$NON-NLS-1$
						&& map.getParent().getName().equalsIgnoreCase("view")) { //$NON-NLS-1$
					path[1] = map;
				} else if (map.getName().equalsIgnoreCase("dataSets")) { //$NON-NLS-1$
					path[2] = map;
					return IterationHandle.IT_BREAK;
				}
				return 0;
			}
		}, true);
		return path;
	}

	/**
	 * get all dataset defined before {@code cursor map}
	 * 
	 * @return
	 */
	private Set<String> getDefinedDataSets() {
		final Set<String> set = new HashSet<String>();
		getRootMap().iterate(new IterationHandle() {

			public int process(CompositeMap map) {
				if (map == getCursorMap())
					return IterationHandle.IT_BREAK;
				if (!map.getName().equalsIgnoreCase("dataSet")) //$NON-NLS-1$
					return 0;
				String ds = map.getString("id"); //$NON-NLS-1$
				if (ds != null)
					set.add(ds);
				return 0;
			}
		}, true);
		return set;
	}

	private ArrayList<SortElement> getAvailableDs() {
		ArrayList<SortElement> comp = new ArrayList<SortElement>();
		String uds = getMarkerWord();
		for (String ds : getDefinedDataSets()) {
			int ed = QuickAssistUtil.getApproiateEditDistance(uds, ds);
			if (ed > 0)
				comp.add(new SortElement(ds, ed));
		}
		Collections.sort(comp);
		return comp;
	}

}
