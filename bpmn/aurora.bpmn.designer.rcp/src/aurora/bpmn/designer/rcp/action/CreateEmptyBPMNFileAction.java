package aurora.bpmn.designer.rcp.action;

import org.eclipse.bpmn2.modeler.ui.Bpmn2DiagramEditorInput;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import aurora.bpmn.designer.rcp.Activator;
import aurora.ide.designer.editor.AuroraBpmnEditor;
import aurora.ide.designer.editor.BPMServiceInputStreamEditorInput;

public class CreateEmptyBPMNFileAction extends Action {

	private final IWorkbenchWindow window;
	private int instanceNum = 0;
	private final String viewId;

	public CreateEmptyBPMNFileAction(IWorkbenchWindow window, String label,
			String viewId) {
		this.window = window;
		this.viewId = viewId;
		setText(label);
		// // The id is used to refer to the action in a menu or toolbar
		 setId("aurora.bpmn.designer.rcp.action.CreateEmptyBPMNFileAction");
		// // Associate the action with a pre-defined command, to allow key
		// bindings.
		// setActionDefinitionId(ICommandIds.CMD_OPEN);
		setImageDescriptor(Activator
				.getImageDescriptor("/icons/sample2.gif"));
	}

	public void run() {
		if (window != null) {
			try {
				URI modelUri = URI
						.createURI("platform:/plugin/aurora.bpmn.designer.rcp/test.bpmn#/0");
				URI diagramUri = URI
						.createURI("platform:/plugin/aurora.bpmn.designer.rcp/test.bpmn#/1");
				Bpmn2DiagramEditorInput input = new Bpmn2DiagramEditorInput(
						modelUri, diagramUri,
						"org.eclipse.bpmn2.modeler.ui.diagram.MainBPMNDiagramType");
				// diagramComposite.setInput(new DiagramEditorInput(uri,
				// "org.eclipse.graphiti.examples.tutorial.diagram.TutorialDiagramTypeProvider"));
				window.getActivePage().openEditor(new BPMServiceInputStreamEditorInput(TestBPMN.getStream()), AuroraBpmnEditor.ID,
						true);
			} catch (PartInitException e) {
				MessageDialog.openError(window.getShell(), "Error",
						"Error opening view:" + e.getMessage());
			}
		}
	}
}
