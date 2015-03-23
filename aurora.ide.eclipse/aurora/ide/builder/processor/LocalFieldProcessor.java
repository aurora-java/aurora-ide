package aurora.ide.builder.processor;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.BuildContext;
import aurora.ide.builder.BuildMessages;
import aurora.ide.builder.CompositeMapInfo;
import aurora.ide.builder.SxsdUtil;
import aurora.ide.preferencepages.BuildLevelPage;

/**
 * check local field reference in bm
 * 
 * @author jessen
 * 
 */
public class LocalFieldProcessor extends AbstractProcessor {
	private int level;

	private Set<Object[]> fieldTask = new HashSet<Object[]>();

	@Override
	public void processComplete(IFile file, CompositeMap map1, IDocument doc) {
		if (level == 0)
			return;
		Set<String> fieldSet = new LocalFieldCollector(file).collect();
		if (fieldSet.size() == 0)
			return;
		for (Object[] objs : fieldTask) {
			String name = (String) objs[0];
			String value = (String) objs[1];
			if (fieldSet.contains(value.toLowerCase()))
				continue;
			CompositeMap map = (CompositeMap) objs[2];
			CompositeMapInfo info = new CompositeMapInfo(map, doc);
			IRegion region = info.getAttrValueRegion2(name);
			int line = info.getLineOfRegion(region);
			String msg = String.format(
					BuildMessages.get("build.localfield.undefined"), name,
					value);
			AuroraBuilder.addMarker(file, msg, line + 1, region, level,
					AuroraBuilder.UNDEFINED_LOCALFIELD);
		}
	}

	@Override
	public void processMap(BuildContext bc) {
		String ext = "_" + bc.file.getFileExtension().toUpperCase();
		level = BuildLevelPage.getBuildLevel(AuroraBuilder.UNDEFINED_LOCALFIELD
				+ ext);
		if (level == 0)
			return;
		processAttribute(bc);
	}

	@Override
	public void visitAttribute(Attribute a, BuildContext bc) {
		if (SxsdUtil.isLocalFieldReference(a.getAttributeType())) {
			String name = a.getName();
			String value = bc.map.getString(name);
			if (LocalFieldCollector.localFieldDefine.contains(bc.map.getName())) {
				if (name.equalsIgnoreCase("name")) {
					// fieldSet.add(value.toLowerCase());
					return;
				}
			}
			fieldTask.add(new Object[] { name, value, bc.map });
		}
	}
}
