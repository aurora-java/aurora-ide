package aurora.ide.builder.processor;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.BuildContext;
import aurora.ide.builder.BuildMessages;
import aurora.ide.builder.ResourceUtil;
import aurora.ide.builder.SxsdUtil;

public class ScreenProcessor extends AbstractProcessor {
	private static final Pattern siPattern = Pattern
			.compile("/{0,1}([a-zA-Z_\\d]+/)*[a-zA-Z_\\d]+\\.screen(\\?.*){0,1}");

	@Override
	public void processMap(BuildContext bc) {
		if (BuildContext.LEVEL_UNDEFINED_SCREEN == 0)
			return;
		processAttribute(bc);
	}

	@Override
	public void visitAttribute(Attribute a, BuildContext bc) {
		if (SxsdUtil.isScreenReference(a.getAttributeType())) {
			String name = a.getName();
			String value = bc.map.getString(name);
			IRegion vregion = bc.info.getAttrValueRegion2(name);
			int line = bc.info.getLineOfRegion(vregion) + 1;
			if (value.length() == 0) {
				String msg = String.format(
						BuildMessages.get("build.notbeempty"), name);
				AuroraBuilder.addMarker(bc.file, msg, line, vregion,
						BuildContext.LEVEL_UNDEFINED_SCREEN,
						AuroraBuilder.UNDEFINED_SCREEN);
				return;
			}
			if (!siPattern.matcher(value).matches()) {
				// String msg = value + " 可能不是一个有效的值";
				// AuroraBuilder.addMarker(bc.file, msg, line, vregion,
				// IMarker.SEVERITY_WARNING,
				// AuroraBuilder.UNDEFINED_SCREEN);
				return;
			}
			value = value.split("\\?")[0];
			IFile findScreenFile = ResourceUtil.getFileUnderWebHome(
					bc.file.getProject(), value);
			if (findScreenFile != null)
				return;
			String msg = String.format(BuildMessages.get("build.notexists"),
					name, value);
			AuroraBuilder.addMarker(bc.file, msg, line, vregion,
					BuildContext.LEVEL_UNDEFINED_SCREEN,
					AuroraBuilder.UNDEFINED_SCREEN);
		}
	}

	@Override
	public void processComplete(IFile file, CompositeMap map, IDocument doc) {

	}

}
