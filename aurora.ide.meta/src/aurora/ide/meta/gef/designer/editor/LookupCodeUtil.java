package aurora.ide.meta.gef.designer.editor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

import uncertain.composite.CompositeMap;
import aurora.ide.api.composite.map.CommentCompositeLoader;
import aurora.ide.api.composite.map.CommentCompositeMapParser;
import aurora.ide.builder.ResourceUtil;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.designer.DesignerMessages;
import aurora.ide.meta.project.AuroraMetaProject;
import freemarker.template.Template;

public class LookupCodeUtil {
	public static final String SYS_CODE_FILE_NAME = DesignerMessages.sys_code_file_name;
	public static final String SYS_CODE_TEMPLATE = "create_syscode.ftl";
	private static Template tpl = null;

	public static boolean isCode(Object o) {
		if (!(o instanceof CompositeMap))
			return false;
		CompositeMap m = (CompositeMap) o;
		return "code".equalsIgnoreCase(m.getName());
	}

	public static boolean isValue(Object o) {
		if (!(o instanceof CompositeMap))
			return false;
		CompositeMap m = (CompositeMap) o;
		return "value".equalsIgnoreCase(m.getName());
	}

	/**
	 * get the code id of a code
	 * 
	 * @param code
	 * @return
	 */
	public static String getCode(Object code) {
		if (!isCode(code))
			return "<invalid code>";
		CompositeMap m = (CompositeMap) code;
		String codeName = m.getString("code");
		if (codeName == null)
			return "<missing code name>";
		return codeName.toUpperCase();
	}

	public static String getValueAsString(Object o) {
		if (!isValue(o))
			return "<invalid value>";
		return getValue(o) + " : [" + getValueNameZHS(o) + ","
				+ getValueNameUS(o) + "]";
	}

	public static String getValue(Object v) {
		if (!isValue(v))
			return "<invalid value>";
		CompositeMap m = (CompositeMap) v;
		return m.getString("value");
	}

	public static String getValueNameZHS(Object v) {
		if (!isValue(v))
			return "<invalid value>";
		CompositeMap m = (CompositeMap) v;
		return m.getString("zhs");
	}

	public static String getValueNameUS(Object v) {
		if (!isValue(v))
			return "<invalid value>";
		CompositeMap m = (CompositeMap) v;
		return m.getString("us");
	}

	public static CompositeMap getCodeRoot(Object o) {
		if (isCode(o))
			return (CompositeMap) o;
		if (isValue(o))
			return ((CompositeMap) o).getParent();
		return null;
	}

	/**
	 * @param metaProject
	 * @return CaseInsensitiveMap
	 * @throws ResourceNotFoundException
	 */
	public static CompositeMap load(IProject metaProject)
			throws ResourceNotFoundException {
		AuroraMetaProject amp = new AuroraMetaProject(metaProject);
		IFolder mf = amp.getModelFolder();
		IFile file = mf.getFile(SYS_CODE_FILE_NAME);
		if (file == null || !file.exists())
			throw new RuntimeException(DesignerMessages.LookupCodeDialog_3
					+ "\nfile not exists.");
		CommentCompositeLoader loader = new CommentCompositeLoader();
		CommentCompositeMapParser parser = new CommentCompositeMapParser(loader);
		CompositeMap codemap;
		try {
			codemap = parser.parseStream(file.getContents(true));
		} catch (Exception e) {
			String errorMessage = DesignerMessages.LookupCodeDialog_3;
			errorMessage += "\n" + e.getMessage(); //$NON-NLS-1$
			throw new RuntimeException(errorMessage);
		}
		return codemap;
	}

	public static boolean isSyscodeFile(IFile f) {
		if (f == null || !f.exists())
			return false;
		if (!f.getName().equals(SYS_CODE_FILE_NAME))
			return false;
		if (!ResourceUtil.isAuroraMetaProject(f.getProject()))
			return false;
		AuroraMetaProject amp = new AuroraMetaProject(f.getProject());
		try {
			IFolder folder = amp.getModelFolder();
			return (folder.getFullPath().isPrefixOf(f.getFullPath()));
		} catch (ResourceNotFoundException e) {
			return false;
		}
	}

	public static Template getSourceTemplate() {
		if (tpl == null) {
			InputStream is = LookupCodeUtil.class
					.getResourceAsStream(SYS_CODE_TEMPLATE);
			try {
				InputStreamReader reader = new InputStreamReader(is, "UTF-8");
				tpl = new Template("code_template", reader);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return tpl;
	}
}
