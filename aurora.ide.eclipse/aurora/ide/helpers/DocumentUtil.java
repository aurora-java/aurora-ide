package aurora.ide.helpers;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import uncertain.composite.CompositeMap;
import uncertain.util.resource.Location;

public class DocumentUtil {

	public static int getMapLineOffset(IDocument document, CompositeMap map, int i,
			boolean isStartLine) throws BadLocationException {
		int offset;
		Location location = map.getLocationNotNull();
		int line = isStartLine ? location.getStartLine() : location
				.getEndLine();
		offset = document.getLineOffset(line + i);
		return offset;
	}

}
