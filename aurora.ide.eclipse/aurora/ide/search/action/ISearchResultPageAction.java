package aurora.ide.search.action;

import org.eclipse.jface.viewers.SelectionChangedEvent;

public interface ISearchResultPageAction {
	public void selectionChanged(SelectionChangedEvent event);

	public boolean isRefactorSelectionEnabled();

	public boolean isRefactorAllEnabled();

	public void runAll();

	public void runSelection();
	
	public void addActionChangedListener(IActionChangedListener listener);
}
