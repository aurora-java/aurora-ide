package aurora.ide.editor.textpage.quickfix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.osgi.util.NLS;

import uncertain.composite.CompositeMap;
import aurora.ide.AuroraPlugin;
import aurora.ide.builder.ResourceUtil;
import aurora.ide.builder.processor.LocalFieldCollector;
import aurora.ide.search.core.Util;

public class ForeignFieldProposalCreator extends AbstractProposalCreator {

	public ForeignFieldProposalCreator(IDocument doc, CompositeMap rootMap,
			IMarker marker) {
		super(doc, rootMap, marker);
	}

	protected boolean isFixable() {
		return super.isFixable() && isValidWord(getMarkerWord());
	}

	@Override
	protected void create(ArrayList<ICompletionProposal> result) {
		CompositeMap map = getCursorMap();
		CompositeMap refModelDeclearMap = map.getParent();
		String refModel = (String) Util
				.getReferenceModelPKG(refModelDeclearMap);
		if (refModel == null) {
			refModelDeclearMap = refModelDeclearMap.getParent();
			refModel = (String) Util.getReferenceModelPKG(refModelDeclearMap);
		}
		if (refModel == null) {
			return;
		}
		for (SortElement se : getAvailableLocalField(refModel, getMarkerWord())) {
			String name = se.name;
			result.add(new CompletionProposal(name, getMarkerOffset(),
					getMarkerLength(), name.length(), img_rename, NLS.bind(
							Messages.Change_to, name),
					null, NLS
							.bind(Messages.Suggest_change_to, name)));
		}
	}

	private ArrayList<SortElement> getAvailableLocalField(String refModel,
			String field) {
		ArrayList<SortElement> list = new ArrayList<SortElement>();
		IFile currentFile = AuroraPlugin.getActiveIFile();
		if (currentFile == null)
			return list;
		IFile bmFile = ResourceUtil.getBMFile(currentFile.getProject(),
				refModel);
		if (bmFile == null || !bmFile.exists())
			return list;
		Set<String> localFields = new LocalFieldCollector(bmFile).collect();
		for (String s : localFields) {
			int ed = QuickAssistUtil.getApproiateEditDistance(field, s);
			if (ed > 0)
				list.add(new SortElement(s, ed));
		}
		Collections.sort(list);
		return list;
	}

}
