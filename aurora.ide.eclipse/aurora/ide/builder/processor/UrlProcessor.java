package aurora.ide.builder.processor;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import uncertain.schema.IType;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.BuildContext;
import aurora.ide.builder.BuildMessages;
import aurora.ide.builder.ResourceUtil;
import aurora.ide.builder.SxsdUtil;

public class UrlProcessor extends AbstractProcessor {
	static final String CONTEXT_PATH = "${/request/@context_path}/";
	static final String AUTOCRUD = "autocrud/";
	static final String MODULES = "modules/";
	static final Pattern dyPattern = Pattern.compile(".*\\$\\{[^}]+\\}.*");
	static final Pattern dynamicPattern = Pattern.compile("\\$\\{[^}]+\\}");

	@Override
	public void processMap(BuildContext bc) {
		processAttribute(bc);
	}

	@Override
	protected void visitAttribute(Attribute a, BuildContext bc) {
		IType type = a.getAttributeType();
		if (SxsdUtil.isURLReference(type)) {
			String name = a.getName();
			String value = bc.info.getAttrRealValue(name);
			IRegion vregion = bc.info.getAttrValueRegion2(name);
			int line = bc.info.getLineOfRegion(vregion) + 1;
			// uk stands for unknown , because we don`t konw what it is,maybe
			// bm,svc,screen or nothing
			String uk = bc.map.getString(name);
			if (dynamicPattern.matcher(uk).matches())
				return;
			int idx = uk.indexOf('?');
			if (idx != -1) {
				// remove parameters
				uk = uk.substring(0, idx);
				value = value.substring(0, idx);
			}
			if (uk.startsWith(CONTEXT_PATH)) {
				// remove ${/request/@context_path}/ prefix
				uk = uk.substring(CONTEXT_PATH.length());
			}
			if (dyPattern.matcher(uk).matches()) {
				// if uk still contains a dynamic parameter,ignore checking
				// System.out.println(name + ":" + uk + "         ignore");
				return;
			}
			if (uk.endsWith(".screen") || uk.endsWith(".svc")) {
				doForScreenOrSvc(uk, name, value, line, vregion, bc);
				return;
			}
			idx = uk.indexOf(AUTOCRUD);
			if (idx != -1)
				uk = uk.substring(idx + AUTOCRUD.length());
			else {
				idx = uk.indexOf(MODULES);
				if (idx != -1) {
					// System.out.println("i don`t know what it is : " + name +
					// " = " + value);
					doForUnknown(uk, name, value, line, vregion, bc);
					return;
				}
			}
			// assume uk is a bm ref
			uk = uk.split("/")[0];// remove bm operation,like /query...
			doForBm(uk, name, value, line, vregion, bc);
		}
	}

	private void doForScreenOrSvc(String uk, String name, String value,
			int line, IRegion vregion, BuildContext bc) {
		if (BuildContext.LEVEL_UNDEFINED_SCREEN == 0)
			return;
		if (uk.startsWith("/"))
			uk = uk.substring(1);
		// System.out.println(name + "  svc or screen:" + uk);
		IFile findScreenFile = ResourceUtil.getFileUnderWebHome(
				bc.file.getProject(), uk);
		if (findScreenFile != null)
			return;
		IResource res = bc.file.getParent().findMember(value);
		if (res instanceof IFile)
			return;
		String msg = String.format(BuildMessages.get("build.notexists"), name,
				uk);
		int idx = value.indexOf(uk);
		if (idx != -1) {
			vregion = new Region(vregion.getOffset() + idx, uk.length());
		}
		AuroraBuilder.addMarker(bc.file, msg, line, vregion,
				BuildContext.LEVEL_UNDEFINED_SCREEN,
				AuroraBuilder.UNDEFINED_SCREEN);
	}

	private void doForBm(String uk, String name, String value, int line,
			IRegion vregion, BuildContext bc) {
		if (BuildContext.LEVEL_UNDEFINED_BM == 0)
			return;
		IFile bmf = ResourceUtil.getBMFile(bc.file.getProject(), uk);
		if (bmf == null) {
			String msg = null;
			if (uk.length() == 0)
				msg = String
						.format(BuildMessages.get("build.notbeempty"), name);
			else
				msg = String.format(BuildMessages.get("build.notexists"), name,
						uk);
			int idx = value.indexOf(uk);
			if (idx != -1) {
				vregion = new Region(vregion.getOffset() + idx, uk.length());
			}
			AuroraBuilder
					.addMarker(bc.file, msg, line, vregion,
							BuildContext.LEVEL_UNDEFINED_BM,
							AuroraBuilder.UNDEFINED_BM);
		}
	}

	private void doForUnknown(String uk, String name, String value, int line,
			IRegion vregion, BuildContext bc) {
		String msg = "we currently don`t know what it is , maybe a screen or svc with a wrong file name extension.";
		int idx = value.indexOf(uk);
		if (idx != -1) {
			vregion = new Region(vregion.getOffset() + idx, uk.length());
		}
		AuroraBuilder.addMarker(bc.file, msg, line, vregion,
				BuildContext.LEVEL_UNDEFINED_SCREEN,
				AuroraBuilder.UNDEFINED_SCREEN);
	}

	@Override
	public void processComplete(IFile file, CompositeMap map, IDocument doc) {

	}

}
