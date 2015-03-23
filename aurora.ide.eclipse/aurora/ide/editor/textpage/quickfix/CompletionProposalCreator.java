package aurora.ide.editor.textpage.quickfix;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import uncertain.composite.CompositeMap;
import aurora.ide.builder.AuroraBuilder;

public class CompletionProposalCreator {
	private CompositeMap rootMap;
	private IDocument doc;
	private IMarker marker;
	private String markerType = "";

	public CompletionProposalCreator(IDocument doc, CompositeMap rootMap,
			IMarker marker) {
		this.rootMap = rootMap;
		this.doc = doc;
		this.marker = marker;
		try {
			markerType = marker.getType();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public ICompletionProposal[] getCompletionProposal() {
		AbstractProposalCreator creator = null;
		if (markerType.equals(AuroraBuilder.UNDEFINED_ATTRIBUTE))
			creator = new AttrProposalCreator(doc, rootMap, marker);
		else if (markerType.equals(AuroraBuilder.UNDEFINED_DATASET))
			creator = new DsProposalCreator(doc, rootMap, marker);
		else if (markerType.equals(AuroraBuilder.UNDEFINED_BM))
			creator = new BmProposalCreator(doc, rootMap, marker);
		else if (markerType.equals(AuroraBuilder.UNDEFINED_TAG))
			creator = new TagProposalCreator(doc, rootMap, marker);
		else if (markerType.equals(AuroraBuilder.UNDEFINED_FOREIGNFIELD))
			creator = new ForeignFieldProposalCreator(doc, rootMap, marker);
		else if (markerType.equals(AuroraBuilder.UNDEFINED_LOCALFIELD))
			creator = new LocalFieldProposalCreator(doc, rootMap, marker);
		if (creator != null)
			return creator.createProposal();
		return null;
	}
}
