package aurora.ide.search.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogSettings;

import aurora.ide.AuroraPlugin;
import aurora.ide.search.condition.AttributeCondition;
import aurora.ide.search.condition.ElementCondition;
import aurora.ide.search.condition.NameSpaceCondition;
import aurora.ide.search.condition.SearchCondition;

public class SearchPageManager {
	private List<SearchCondition> previousSearchNameSpace = new ArrayList<SearchCondition>();
	private List<SearchCondition> previousSearchElement = new ArrayList<SearchCondition>();
	private List<SearchCondition> previousSearchAttribute = new ArrayList<SearchCondition>();

	public static final int HISTORY_SIZE = 12;
	public static final String STORE_NAMESPACE_HISTORY_SIZE = "NAMESPACE_HISTORY_SIZE";
	public static final String STORE_ELEMENT_HISTORY_SIZE = "ELEMENT_HISTORY_SIZE";
	public static final String STORE_ATTRIBUTE_HISTORY_SIZE = "ATTRIBUTE_HISTORY_SIZE";

	public void readConfiguration(String sectionKey,
			List<SearchCondition> previousList) {
		IDialogSettings s = getDialogSettings();
		try {
			int historySize = s.getInt(sectionKey);
			for (int i = 0; i < historySize; i++) {
				IDialogSettings histSettings = s.getSection(sectionKey + i);
				if (histSettings != null) {
					SearchCondition condition = createSearchCondition(sectionKey);
					if (condition != null) {
						SearchCondition read = condition.read(histSettings);
						if (read != null) {
							previousList.add(read);
						}
					}
				}
			}
		} catch (NumberFormatException e) {
			// ignore
		}
	}

	private SearchCondition createSearchCondition(String sectionKey) {
		if (STORE_NAMESPACE_HISTORY_SIZE.equals(sectionKey)) {
			return new NameSpaceCondition();
		}
		if (STORE_ELEMENT_HISTORY_SIZE.equals(sectionKey)) {
			return new ElementCondition();
		}
		if (STORE_ATTRIBUTE_HISTORY_SIZE.equals(sectionKey)) {
			return new AttributeCondition();
		}
		return null;
	}

	private IDialogSettings getDialogSettings() {
		return AuroraPlugin.getDefault().getDialogSettingsSection(
				"AuroraSearchPage");
	}

	/**
	 * Stores it current configuration in the dialog store.
	 */
	public void writeConfiguration(String sectionKey,
			List<SearchCondition> previousList) {
		IDialogSettings s = getDialogSettings();
		int historySize = Math.min(previousList.size(), HISTORY_SIZE);
		s.put(sectionKey, historySize);
		for (int i = 0; i < historySize; i++) {
			IDialogSettings histSettings = s.addNewSection(sectionKey + i);
			SearchCondition data = ((SearchCondition) previousList.get(i));
			data.store(histSettings);
		}
	}

	public List<SearchCondition> getPreviousSearchNameSpace() {
		return previousSearchNameSpace;
	}

	public List<SearchCondition> getPreviousSearchElement() {
		return previousSearchElement;
	}

	public List<SearchCondition> getPreviousSearchAttribute() {
		return previousSearchAttribute;
	}

	public String[] getPreviousNameSpace() {
		int size = previousSearchNameSpace.size();
		String[] patterns = new String[size];
		for (int i = 0; i < size; i++) {
			String nameSpace = ((NameSpaceCondition) previousSearchNameSpace
					.get(i)).getNameSpace();
			patterns[i] = nameSpace;
		}

		return patterns;
	}

	public String[] getPreviousElement() {
		int size = previousSearchElement.size();
		String[] patterns = new String[size];
		for (int i = 0; i < size; i++)
			patterns[i] = ((ElementCondition) previousSearchElement.get(i))
					.getElementName();
		return patterns;
	}

	public String[] getPreviousAttributeNames() {
		int size = previousSearchAttribute.size();
		List<String> patterns = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			String name = ((AttributeCondition) previousSearchAttribute.get(i))
					.getName();
			if (name != null  && !patterns.contains(name)) {
				patterns.add(name);
			}

		}
		return patterns.toArray(new String[patterns.size()]);
	}

	public String[] getPreviousAttributeValues() {

		int size = previousSearchAttribute.size();
		List<String> patterns = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			String value = ((AttributeCondition) previousSearchAttribute.get(i))
					.getValue();
			if (value != null && !patterns.contains(value)) {
				patterns.add(value);
			}
		}
		return patterns.toArray(new String[patterns.size()]);
	}

	public void addPreviousSearchCondition(List<SearchCondition> previousList,
			SearchCondition condition) {
		for (Iterator iter = previousList.iterator(); iter.hasNext();) {
			SearchCondition sc = (SearchCondition) iter.next();
			if (sc.isEquals(condition)) {
				iter.remove();
			}
		}

		if (condition != null) {
			previousList.add(0, condition);
		}
	}

}
