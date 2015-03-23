package aurora.ide.screen.editor;


import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.ResourceTransfer;

import uncertain.composite.CompositeMap;
import uncertain.schema.Element;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.component.wizard.DataSetWizard;
import aurora.ide.editor.CompositeMapTreePage;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.helpers.ProjectUtil;


public class ServiceTreePage extends CompositeMapTreePage {
	protected static final String PageId = "ServicePage";
	protected static final String PageTitle = LocaleMessage.getString("screen.file");
	public ServiceTreePage(FormEditor editor) {
		super(editor, PageId, PageTitle);
	}
	protected void createContent(Composite shell) throws ApplicationException {
		super.createContent(shell);
		fillDNDListener();
	}
	public void fillDNDListener() {
		DragSource ds = new DragSource(getControl(), DND.DROP_COPY | DND.DROP_MOVE);
		ds.setTransfer(new Transfer[]{LocalSelectionTransfer.getTransfer()});
		ds.addDragListener(new DragSourceAdapter() {
			public void dragSetData(DragSourceEvent event) {
			}
		});

		DropTarget dt = new DropTarget(getControl(), DND.DROP_COPY | DND.DROP_MOVE);
		final LocalSelectionTransfer localSelectionTransfer = LocalSelectionTransfer.getTransfer();
		final ResourceTransfer resourceTransfer = ResourceTransfer.getInstance();
		dt.setTransfer(new Transfer[]{localSelectionTransfer, resourceTransfer});
		dt.addDropListener(new DropTargetAdapter() {
			public void dragEnter(DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) {
					if ((event.operations & DND.DROP_COPY) != 0) {
						event.detail = DND.DROP_COPY;
					} else {
						event.detail = DND.DROP_NONE;
					}
				}
				for (int i = 0; i < event.dataTypes.length; i++) {
					if (resourceTransfer.isSupportedType(event.dataTypes[i])) {
						event.currentDataType = event.dataTypes[i];
						// files should only be copied
						if (event.detail != DND.DROP_COPY) {
							event.detail = DND.DROP_NONE;
						}
						break;
					}
				}
			}
			public void drop(DropTargetEvent event) {
				if (resourceTransfer.isSupportedType(event.currentDataType)) {
					Object data = event.data;
					if (data != null) {
//						Element element = LoadSchemaManager.getSchemaManager().getElement(getInput());
						Element element = CompositeMapUtil.getElement(getInput());
						if (element == null || !element.getQName().equals(AuroraConstant.ScreenQN)) {
							DialogUtil.showErrorMessageBox("this.is.not.screen.file");
							return;
						}
						IResource[] resources = (IResource[]) data;
						String bmfile_dir = null;
						String bmFiles = "";
						try {
							bmfile_dir = getFullPath();
						} catch (Exception e) {
							DialogUtil.showExceptionMessageBox(e);
						}
						for (int i = 0; i < resources.length; i++) {
							IResource resource = resources[i];
							String filePath = resource.getLocation().toOSString();
							if (!filePath.toLowerCase().endsWith(AuroraConstant.BMFileExtension)) {
								continue;
							}
							String className = getClassName(new java.io.File(filePath), bmfile_dir);
							bmFiles = bmFiles + className + ",";
						}
						CompositeMap input = getInput();
						CompositeMap view = input.getChild(AuroraConstant.ViewQN.getLocalName());
						if (view == null) {
							String prefix = CompositeMapUtil.getContextPrefix(input, AuroraConstant.ViewQN);
							view = new CommentCompositeMap(prefix, AuroraConstant.ViewQN.getNameSpace(), AuroraConstant.ViewQN
									.getLocalName());
							view.setParent(input);
							input.addChild(view);
						}
						CompositeMap dataSets = view.getChild(AuroraConstant.DataSetSQN.getLocalName());
						if (dataSets == null) {
							String prefix = CompositeMapUtil.getContextPrefix(input, AuroraConstant.DataSetSQN);
							dataSets = new CommentCompositeMap(prefix, AuroraConstant.DataSetSQN.getNameSpace(),
									AuroraConstant.DataSetSQN.getLocalName());
							view.addChild(dataSets);
							dataSets.setParent(view);
						}
						DataSetWizard wizard = new DataSetWizard(dataSets, bmFiles);
						WizardDialog dialog = new WizardDialog(new Shell(), wizard);
						if (dialog.open() == Window.OK)
							refresh(true);
					}

				} else if (localSelectionTransfer.isSupportedType(event.currentDataType)) {
					CompositeMap sourceCm = baseCompositeMapPage.getSelection();
					if (sourceCm == null)
						return;
					CompositeMap objectCm = (CompositeMap) event.item.getData();
					if (objectCm == null)
						return;
					if (objectCm.equals(sourceCm) && objectCm.toXML().equals(sourceCm.toXML())) {
						return;
					}
					if (!CompositeMapUtil.validNextNodeLegalWithAction(objectCm, sourceCm)) {
						return;
					}
					CompositeMap childCm = new CommentCompositeMap(sourceCm);

					if (childCm != null) {
						objectCm.addChild(childCm);
						if (sourceCm.getParent() != null)
							sourceCm.getParent().removeChild(sourceCm);
					}
					refresh(true);
				}
			}
			private CompositeMap getInput() {
				return baseCompositeMapPage.getData();
			}
		});
	}
	private Control getControl() {
		return baseCompositeMapPage.getControl();
	}

	private String getClassName(File file, String fullpath) {
		String path = file.getPath();
		int end = path.lastIndexOf(".");
		path = path.substring(fullpath.length() + 1, end);
		path = path.replace(File.separatorChar, '.');
		return path;
	}

	private String getFullPath() throws ApplicationException {
		IEditorInput input = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()
				.getEditorInput();
		IFile ifile = ((IFileEditorInput) input).getFile();
		IProject project = ifile.getProject();
		String bmFilesDir = ProjectUtil.getBMHomeLocalPath(project);
		java.io.File baseDir = new java.io.File(bmFilesDir);
		String fullPath = baseDir.getAbsolutePath();
		return fullPath;
	}
}
