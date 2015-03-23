package aurora.ide.editor.textpage;


import java.util.ResourceBundle;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.eclipse.ui.texteditor.BasicTextEditorActionContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;

import aurora.ide.helpers.LocaleMessage;



/**
 * Manages the installation and deinstallation of actions for the editor.
 */
public class XMLEditorContributor extends BasicTextEditorActionContributor
{

	protected RetargetTextEditorAction contentAssistProposal;
	protected RetargetTextEditorAction contentAssistTip;
	protected RetargetTextEditorAction formatProposal;

	/**
	 * Constructor for SQLEditorContributor. Creates a new contributor in the
	 * form of adding Content Assist, Conent Format and Assist tip menu items
	 */
	public XMLEditorContributor()
	{
		super();
		ResourceBundle bundle = LocaleMessage.getResourceBundle();

		contentAssistProposal = new RetargetTextEditorAction(bundle, "ContentAssistProposal.");
		formatProposal = new RetargetTextEditorAction(bundle, "ContentFormatProposal.");
		contentAssistTip = new RetargetTextEditorAction(bundle, "ContentAssistTip.");

	}

	public void contributeToMenu(IMenuManager mm)
	{
		super.contributeToMenu(mm);
		IMenuManager editMenu = mm.findMenuUsingPath(IWorkbenchActionConstants.M_EDIT);
		if (editMenu != null)
		{
			editMenu.add(new Separator());
			editMenu.add(contentAssistProposal);
			editMenu.add(formatProposal);
			editMenu.add(contentAssistTip);
		}
	}

	/**
	 * Sets the active editor to this contributor. This updates the actions to
	 * reflect the editor.
	 * 
	 * @see EditorActionBarContributor#editorChanged
	 */
	public void setActiveEditor(IEditorPart part)
	{

		super.setActiveEditor(part);

		ITextEditor editor = null;
		if (part instanceof ITextEditor)
			editor = (ITextEditor) part;

		contentAssistProposal.setAction(getAction(editor, "ContentAssistProposal"));
		formatProposal.setAction(getAction(editor, "ContentFormatProposal"));
		contentAssistTip.setAction(getAction(editor, "ContentAssistTip"));

	}

	/**
	 * 
	 * Contributes to the toolbar.
	 * 
	 * @see EditorActionBarContributor#contributeToToolBar
	 */
	public void contributeToToolBar(IToolBarManager tbm)
	{
		super.contributeToToolBar(tbm);
		tbm.add(new Separator());
	}

}