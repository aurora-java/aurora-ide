package aurora.ide.refactoring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.text.edits.MultiTextEdit;

public class TextFileChangeManager {
	private Map<IFile, TextFileChange> changeMap = new HashMap<IFile, TextFileChange>();
	private Map<IFile, List<IRegion>> regionMap = new HashMap<IFile, List<IRegion>>();

	public TextFileChange getTextFileChange(IFile file) {
		TextFileChange textFileChange = changeMap.get(file);
		if (textFileChange == null) {
			textFileChange = new TextFileChange("File Changed ", file);
			textFileChange.setSaveMode(TextFileChange.FORCE_SAVE);
			textFileChange.setEdit(new MultiTextEdit());
			changeMap.put(file, textFileChange);
		}
		return textFileChange;
	}

	public TextFileChange getTextFileChangeInProcessor(
			RefactoringProcessor processor, IFile file) {
		TextChange textChange = processor.getRefactoring().getTextChange(file);
		if (textChange instanceof TextFileChange) {
			return (TextFileChange) textChange;
		}
		return null;
	}

	public Map<IFile, TextFileChange> getChangeMap() {
		return changeMap;
	}

	public TextFileChange[] getAllChanges() {
		return changeMap.values().toArray(new TextFileChange[changeMap.size()]);
	}

	public TextFileChange[] getAllChangesHasEdit() {
		List<TextFileChange> resultChanges = new ArrayList<TextFileChange>();
		Collection<TextFileChange> values = changeMap.values();
		for (TextFileChange change : values) {
			boolean hasChildren = change.getEdit().hasChildren();
			if (hasChildren) {
				resultChanges.add(change);
			}
		}
		return resultChanges.toArray(new TextFileChange[resultChanges.size()]);
	}

	public boolean isOverlapping(IFile file, int offset, int length) {
		List<IRegion> list = regionMap.get(file);
		if (list == null) {
			list = new ArrayList<IRegion>();
			regionMap.put(file, list);
		}
		if (!list.contains(new Region(offset, length))) {
			list.add(new Region(offset, length));
			return false;
		}
		return true;
	}
}
