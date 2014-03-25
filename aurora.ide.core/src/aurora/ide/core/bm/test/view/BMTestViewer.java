package aurora.ide.core.bm.test.view;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.part.ViewPart;

import aurora.ide.core.actions.EditorViewerLinkAction;
import aurora.ide.core.debug.ITestViewerPart;

public class BMTestViewer extends ViewPart implements ITestViewerPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "aurora.ide.core.bm.test.view.BMTestViewer";


	private EditorViewerLinkAction linkHelper;

	private StyledText delete;

	private StyledText update;

	private StyledText insert;

	private StyledText query;


	/**
	 * The constructor.
	 */
	public BMTestViewer() {

		linkHelper = new EditorViewerLinkAction(this);
		linkHelper.activate();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		Group g1 = new Group(parent, SWT.NONE);
		g1.setLayoutData(new GridData(GridData.FILL_BOTH));
		g1.setText("Query");
		g1.setLayout(new GridLayout());
		query = new StyledText(g1, SWT.READ_ONLY);
		query.setLayoutData(new GridData(GridData.FILL_BOTH));

		Group g2 = new Group(parent, SWT.NONE);
		g2.setLayoutData(new GridData(GridData.FILL_BOTH));
		g2.setText("Insert");

		g2.setLayout(new GridLayout());
		insert = new StyledText(g2, SWT.READ_ONLY);
		insert.setLayoutData(new GridData(GridData.FILL_BOTH));

		Group g3 = new Group(parent, SWT.NONE);
		g3.setLayoutData(new GridData(GridData.FILL_BOTH));
		g3.setText("Update");

		g3.setLayout(new GridLayout());
		update = new StyledText(g3, SWT.READ_ONLY);
		update.setLayoutData(new GridData(GridData.FILL_BOTH));

		Group g4 = new Group(parent, SWT.NONE);
		g4.setLayoutData(new GridData(GridData.FILL_BOTH));
		g4.setText("Delete");

		g4.setLayout(new GridLayout());
		delete = new StyledText(g4, SWT.READ_ONLY);
		delete.setLayoutData(new GridData(GridData.FILL_BOTH));

	}


	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		// viewer.getControl().setFocus();
	}

	public void dispose() {
		linkHelper.deactivated();
		super.dispose();
	}

	@Override
	public void editorChanged(IEditorPart activeEditor) {

		IFile file = ResourceUtil.getFile(activeEditor.getEditorInput());
		if (file != null) {
			String fileExtension = file.getFileExtension();
			if ("bm".equals(fileExtension)) {
				BM2SQLHelper b2s = new BM2SQLHelper(file);
				try {
					query.setText(b2s.getSQL("query"));
				} catch (Exception e) {
					String localizedMessage = e.getLocalizedMessage();
					query.setText(localizedMessage==null?"":localizedMessage);
				}
				try {
					insert.setText(b2s.getSQL("insert"));
				} catch (Exception e) {
					String localizedMessage = e.getLocalizedMessage();
					insert.setText(localizedMessage==null?"":localizedMessage);
				}
				try {
					update.setText(b2s.getSQL("update"));
				} catch (Exception e) {
					String localizedMessage = e.getLocalizedMessage();
					update.setText(localizedMessage==null?"":localizedMessage);
				}
				try {
					delete.setText(b2s.getSQL("delete"));
				} catch (Exception e) {
					String localizedMessage = e.getLocalizedMessage();
					delete.setText(localizedMessage==null?"":localizedMessage);
				}
				
			}
		}
	}

}