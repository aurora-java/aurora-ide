package aurora.ide.views;

import java.util.ArrayList;
import java.util.List;

public class ListenerHandler {

	private List<IListener> listeners = new ArrayList<IListener>();

	public void addListener(IListener l) {
		listeners.add(l);
	}

	public void handleIt(Object o) {
		for (IListener l : listeners) {
			l.handleEvent(o);
		}
	}

}
