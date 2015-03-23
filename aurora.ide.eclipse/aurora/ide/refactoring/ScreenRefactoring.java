package aurora.ide.refactoring;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import aurora.ide.editor.textpage.IColorConstants;
import aurora.ide.search.cache.CacheManager;
import aurora.ide.search.core.AbstractMatch;
import aurora.ide.search.core.Util;

public class ScreenRefactoring {
	public static final IPath requestPath = new Path(
			"${/request/@context_path}");

	public static IDocument getDocument(IFile file) throws CoreException {
		return CacheManager.getDocument(file);
	}

	public static TextEdit createMoveTOScreenTextEdit(AbstractMatch match,
			IPath newPath) throws CoreException, BadLocationException {
		int offset = match.getOriginalOffset();
		IFile mf = (IFile) match.getElement();
		IDocument document = getDocument(mf);
		ITypedRegion partition = document.getPartition(offset);
		IRegion valueRegion = Util
				.getValueRegion(partition.getOffset(), partition.getLength(),
						offset, document, IColorConstants.STRING);
		String value = document.get(valueRegion.getOffset() + 1,
				valueRegion.getLength() - 2);
		String newPkg = createMoveTOScreenPkg(value, mf, newPath);
		ReplaceEdit edit = new ReplaceEdit(valueRegion.getOffset() + 1,
				valueRegion.getLength() - 2, newPkg);
		return edit;
	}

	public static String findAttributeValue(AbstractMatch match)
			throws CoreException, BadLocationException {
		IFile mf = (IFile) match.getElement();
		IRegion valueRegion = findAttributeValueRegion(match);
		IDocument document = getDocument(mf);
		String value = document.get(valueRegion.getOffset() + 1,
				valueRegion.getLength() - 2);
		return value;
	}

	public static IRegion findAttributeValueRegion(AbstractMatch match)
			throws CoreException, BadLocationException {
		int offset = match.getOriginalOffset();
		IFile mf = (IFile) match.getElement();
		IDocument document = getDocument(mf);
		ITypedRegion partition = document.getPartition(offset);
		IRegion valueRegion = Util
				.getValueRegion(partition.getOffset(), partition.getLength(),
						offset, document, IColorConstants.STRING);

		return valueRegion;
	}

	public static String createMoveTOScreenPkg(String changeValue,
			IFile targetFile, IPath newPath) {
		IContainer webInf = Util.findWebInf(targetFile);
		if (webInf == null)
			return "";
		IResource webRoot = webInf.getParent();
		IPath path = new Path((String) changeValue);
		String qPath = "";
		String[] split = changeValue.split("\\?");
		if (changeValue.length() > 1) {
			for (int i = 1; i < split.length; i++) {
				qPath = "?" + split[i];
			}
		}
		path = new Path(split[0]);

		boolean prefixOfRequest = requestPath.isPrefixOf(path);
		if (prefixOfRequest) {
			path = path.makeRelativeTo(requestPath);
			// if (fileExtension != null) {
			newPath = newPath.makeRelativeTo(webRoot.getProjectRelativePath());
			newPath = requestPath.append(newPath);
			// return newPath.toString();
			// }
		} else {
			newPath = newPath.makeRelativeTo(targetFile.getParent()
					.getProjectRelativePath());
		}
		if (qPath.length() > 0) {
			// newPath =newPath.append(qPath);
			return newPath.toString() + qPath;
		}

		// L/hr_aurora/web/modules/hr/org/hr_org_unit_update.screen
		// web/modules/hr/fdasf/hr_org_unit_choice_single.screen
		// web/modules/hr/fdasf/hr_org_unit_choice_single.screen
		// web/modules/hr/org/hr_org_unit_update.screen
		// ../../fdasf/hr_org_unit_choice_single.screen
		// targetFile.getProjectRelativePath().append("../../fdasf/hr_org_unit_choice_single.screen");

		// .addFileExtension(fileExtension);
		return newPath.toString();
	}
}
