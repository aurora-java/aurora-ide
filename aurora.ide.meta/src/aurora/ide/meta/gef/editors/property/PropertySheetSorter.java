package aurora.ide.meta.gef.editors.property;

import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.ui.views.properties.IPropertySheetEntry;

public class PropertySheetSorter {
	private Collator collator;

	public PropertySheetSorter() {
		this(Collator.getInstance());
	}

	public PropertySheetSorter(Collator collator) {
		this.collator = collator;
	}

	public int compare(IPropertySheetEntry entryA, IPropertySheetEntry entryB) {
		return getCollator().compare(entryA.getDisplayName(),
				entryB.getDisplayName());
	}

	protected Collator getCollator() {
		return collator;
	}

	public void sort(IPropertySheetEntry[] entries) {
		Arrays.sort(entries, new Comparator<IPropertySheetEntry>() {
			public int compare(IPropertySheetEntry a, IPropertySheetEntry b) {
				return PropertySheetSorter.this.compare(a, b);
			}
		});
	}

}
