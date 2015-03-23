package aurora.ide.builder.processor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import uncertain.composite.CompositeMap;
import uncertain.schema.Element;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.BuildContext;
import aurora.ide.builder.BuildMessages;
import aurora.ide.helpers.LoadSchemaManager;

public class UncertainNsProcessor extends AbstractProcessor {
	private static Collection<?> allTypes = LoadSchemaManager
			.getSchemaManager().getAllTypes();
	private static final Set<String> allTypesNames = new HashSet<String>(200);
	private static Set<String> ignore = new HashSet<String>();

	static {
		ignore.add("table");
		ignore.add("label");
		ignore.add("button");
		ignore.add("form");
		for (Object type : allTypes) {
			if (type instanceof Element) {
				String name = ((Element) type).getLocalName().toLowerCase();
				if (ignore.contains(name))
					allTypesNames.add(name);
			}
		}
	}

	@Override
	public void processMap(BuildContext bc) {
		if (BuildContext.LEVEL_NONOENAMESPACE == 0)
			return;
		if (bc.map.getNamespaceURI() == null) {
			String name = bc.map.getName().toLowerCase();
			if (!allTypesNames.contains(name))
				return;
			IRegion region = bc.info.getMapNameRegion();
			int line = bc.info.getLineOfRegion(region);
			String msg = String.format(
					BuildMessages.get("build.neednamespace"), name);
			AuroraBuilder.addMarker(bc.file, msg, line + 1, region,
					BuildContext.LEVEL_NONOENAMESPACE,
					AuroraBuilder.NONENAMESPACE);
		}
	}

	@Override
	public void processComplete(IFile file, CompositeMap map, IDocument doc) {

	}

}
