package aurora.ide.core.screen.preview.browser.viewer;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.part.ViewPart;

import aurora.ide.core.actions.EditorViewerLinkAction;
import aurora.ide.core.debug.ITestViewerPart;
import aurora.ide.core.server.launch.AuroraServerManager;

public class PreviewBrowserView extends ViewPart implements ITestViewerPart {
	private PreviewBrowser instance = null;

	private EditorViewerLinkAction linkHelper;

	/**
	 * Create the example
	 * 
	 * @see ViewPart#createPartControl
	 */
	public void createPartControl(Composite frame) {
		instance = new PreviewBrowser(frame, true);
		linkHelper = new EditorViewerLinkAction(this);
		linkHelper.activate();
		editorChanged(this.getSite().getPage().getActiveEditor());

	}

	/**
	 * Called when we must grab focus.
	 * 
	 * @see org.eclipse.ui.part.ViewPart#setFocus
	 */
	public void setFocus() {
		// instance.focus();
	}

	/**
	 * Called when the View is to be disposed
	 */
	public void dispose() {
		linkHelper.deactivated();
		instance.dispose();
		instance = null;
		super.dispose();
	}

	@Override
	public void editorChanged(IEditorPart activeEditor) {
		if(activeEditor==null)
			return;
		IFile file = ResourceUtil.getFile(activeEditor.getEditorInput());
		if (file != null) {
			String fileExtension = file.getFileExtension();
			if ("screen".equals(fileExtension) || "svc".equals(fileExtension)) {
				String url = AuroraServerManager.getInstance().getURL(file);
				if ("".equals(url) == false) {
					instance.getBrowser().setUrl(url);

					// InputStream openStream = null;
					// try {
					// URL webUrl = new URL(url);
					// openStream = webUrl.openStream();
					//
					// BufferedReader bufferedReader = new BufferedReader(
					// new InputStreamReader(openStream));
					// String str = null;
					// StringBuffer result = new StringBuffer();
					// while ((str = bufferedReader.readLine()) != null) {
					// result.append(str);
					// result.append("\n");
					// }
					// System.out.println(result);
					//
					// } catch (MalformedURLException e) {
					// e.printStackTrace();
					// } catch (IOException e) {
					// e.printStackTrace();
					// } finally {
					// if (openStream != null) {
					// try {
					// openStream.close();
					// } catch (IOException e) {
					// }
					// }
					// }

				}
			}
		}
	}
}
