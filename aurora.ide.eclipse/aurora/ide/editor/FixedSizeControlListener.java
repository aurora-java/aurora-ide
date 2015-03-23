package aurora.ide.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;

public class FixedSizeControlListener extends ControlAdapter {

	int size;

	public FixedSizeControlListener(int size) {
		this.size = size;
	}

	@Override
	public void controlResized(ControlEvent e) {
		SashForm sf = (SashForm) e.widget;
		int totalSize = sf.getOrientation() == SWT.HORIZONTAL ? sf.getSize().x
				: sf.getSize().y;
		sf.setWeights(new int[] { size, totalSize - size });
		sf.removeControlListener(this);
	}

}