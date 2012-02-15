package aurora.ide.meta.gef.editors;

import org.eclipse.gef.ui.palette.FlyoutPaletteComposite;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public abstract class FlayoutBMGEFEditor extends
		GraphicalEditorWithFlyoutPalette {
	private FlyoutPaletteComposite splitter;
	private DatasetView datasetView;

	public void createPartControl(Composite parent) {

		// super.createPartControl(parent);

		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
		// sashForm.SASH_WIDTH = 1;

		SashForm c = new SashForm(sashForm, SWT.VERTICAL | SWT.BORDER);
		// c.setBackground(ColorConstants.WHITE);
		// c.setLayout(new GridLayout());
		createBMViewer(c);
		createPropertyViewer(c);

		Composite cpt = new Composite(sashForm, SWT.NONE);
		cpt.setLayout(new GridLayout());
		// Composite top = new Composite(cpt, SWT.BORDER);
		// GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		// layoutData.heightHint = 25;
		// top.setLayoutData(layoutData);
		// top.setLayout(new FillLayout());
		Composite bottom = new Composite(cpt, SWT.NONE);
		bottom.setLayoutData(new GridData(GridData.FILL_BOTH));
		bottom.setLayout(new FillLayout());

		// datasetView = new DatasetView();
		// datasetView.createControl(top);
		//
		// initDatasetView();

		super.createPartControl(bottom);
		sashForm.setWeights(new int[] { 1, 4 });
	}

	protected void initDatasetView() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createGraphicalViewer(Composite parent) {
		super.createGraphicalViewer(parent);
	}

	public DatasetView getDatasetView() {
		return datasetView;
	}

	protected abstract void createPropertyViewer(Composite c);

	protected abstract void createBMViewer(Composite c);
}
