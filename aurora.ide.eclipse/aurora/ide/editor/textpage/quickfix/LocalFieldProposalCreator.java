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
import uncertain.ocm.OCManager;
import aurora.ide.AuroraPlugin;
import aurora.ide.bm.ExtendModelFactory;
import aurora.ide.builder.processor.LocalFieldCollector;

public class LocalFieldProposalCreator extends AbstractProposalCreator {

	public LocalFieldProposalCreator(IDocument doc, CompositeMap rootMap,
			IMarker marker) {
		super(doc, rootMap, marker);
	}

	protected boolean isFixable() {
		return super.isFixable() && isValidWord(getMarkerWord());
	}

	@Override
	protected void create(ArrayList<ICompletionProposal> result) {
		for (SortElement se : getAvailableLocalFields()) {
			String name = se.name;
			result.add(new CompletionProposal(name, getMarkerOffset(),
					getMarkerLength(), name.length(), img_rename, NLS.bind(
							Messages.Change_to, name), null, NLS.bind(
							Messages.Suggest_change_to, name)));
		}
	}

	private ArrayList<SortElement> getAvailableLocalFields() {
		ArrayList<SortElement> list = new ArrayList<SortElement>();
		IFile currentFile = AuroraPlugin.getActiveIFile();
		if (currentFile == null)
			return list;
		CompositeMap wholeMap = getRootMap();
		if (wholeMap.getString("extend") != null) {
			try {
				ExtendModelFactory factory = new ExtendModelFactory(
						OCManager.getInstance(), currentFile);
				wholeMap = factory.getModel(getRootMap()).getObjectContext();
			} catch (Exception e) {
				return list;
			}
		}
		Set<String> localFields = new LocalFieldCollector(wholeMap).collect();
		String locf = getMarkerWord();
		for (String s : localFields) {
			int ed = QuickAssistUtil.getApproiateEditDistance(locf, s);
			if (ed > 0)
				list.add(new SortElement(s, ed));
		}
		Collections.sort(list);
		return list;
	}
}
