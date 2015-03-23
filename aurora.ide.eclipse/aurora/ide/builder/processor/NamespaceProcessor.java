package aurora.ide.builder.processor;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import uncertain.composite.CompositeMap;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.BuildContext;
import aurora.ide.builder.BuildMessages;

public class NamespaceProcessor extends AbstractProcessor {

	@Override
	public void processMap(BuildContext bc) {
		if (BuildContext.LEVEL_NONOENAMESPACE == 0)
			return;
		if (bc.map.getNamespaceURI() == null) {
			String name = bc.map.getName();
			IRegion region = bc.info.getMapNameRegion();
			int line = bc.info.getLineOfRegion(region);
			String msg = String.format(BuildMessages.get("build.nonamespace"),
					name);
			AuroraBuilder.addMarker(bc.file, msg, line + 1, region,
					BuildContext.LEVEL_NONOENAMESPACE,
					AuroraBuilder.NONENAMESPACE);
		}
	}

	@Override
	public void processComplete(IFile file, CompositeMap map, IDocument doc) {

	}

}
