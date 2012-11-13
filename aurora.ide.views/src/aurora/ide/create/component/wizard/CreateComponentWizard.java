package aurora.ide.create.component.wizard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;
import aurora.ide.composite.map.CompositeMapLoadException;
import aurora.ide.composite.map.DocumentCompositeMap;
import aurora.ide.create.component.ViewDiagramCreator;
import aurora.ide.editor.textpage.TextPage;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.DocumentUtil;
import aurora.ide.meta.exception.TemplateNotBindedException;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.source.gen.core.ScreenGenerator;
import aurora.ide.views.bm.view.FakePrototypeProject;

public class CreateComponentWizard extends Wizard implements
		IPageChangedListener {

	private IProject project;

	private IDocument document;

	private CompositeMap bodyMap;
	private CompositeMap viewMap;
	private CompositeMap screenMap;
	private CompositeMap datasetsMap;

	private List<CompositeMap> insertAfters = new ArrayList<CompositeMap>();

	private PrototpyeViewWizardPage prototpyePage = new PrototpyeViewWizardPage(
			"PrototpyeViewWizardPage");
	private ComponentListWizardPage componentListPage = new ComponentListWizardPage(
			"ComponentListWizardPage");

	private TextPage textPage;

	private ViewDiagramCreator viewDiagramCreator;

	public CreateComponentWizard(List<CompositeMap> input, IProject project,
			TextPage textPage) {
		this.project = project;
		this.document = (IDocument) textPage.getAdapter(IDocument.class);
		this.textPage = textPage;
		viewDiagramCreator = new ViewDiagramCreator(input);
		try {
			createAfterList();
		} catch (CompositeMapLoadException e) {
			componentListPage.setErrorMessage(e.getMessage());
			componentListPage.setPageComplete(false);
		}
		if (screenMap == null) {
			componentListPage.setErrorMessage("找不到screen节点");
			componentListPage.setPageComplete(false);
		}
		if (viewMap == null) {
			componentListPage.setErrorMessage("找不到view节点");
			componentListPage.setPageComplete(false);
		}
		this.prototpyePage.setInserOfterList(insertAfters);
	}

	@Override
	public boolean canFinish() {
		return super.canFinish();
	}

	@Override
	public void addPages() {
		this.addPage(componentListPage);
		componentListPage.setTitle("Component List");
		componentListPage.setDescription("组件列表");
		this.addPage(prototpyePage);
		prototpyePage.setTitle("GUI");
		prototpyePage.setDescription("组件定制");
		super.addPages();
	}

	@Override
	public boolean performFinish() {
		try {
			ViewDiagram viewDiagram = prototpyePage.getViewDiagram();
			CompositeMap generate = generate(viewDiagram);
			String text = createInsertText(generate);
			String insertDatasetText = "";
			IRewriteTarget target = (IRewriteTarget) textPage
					.getAdapter(IRewriteTarget.class);
			if (target != null)
				target.beginCompoundChange();
			int dsOffset = 0;
			int dslength = 0;
			if (viewMap != null && !this.isInSameLine(viewMap)) {
				insertDatasetText = createInsertDatasetText(generate);
				if (this.datasetsMap == null) {
					dsOffset = DocumentUtil.getMapLineOffset(document, viewMap,
							0, true);
				}
				if (this.datasetsMap != null) {
					boolean inSameLine = this.isInSameLine(datasetsMap);
					if (inSameLine) {
						int endLine = datasetsMap.getLocation().getEndLine();
						dsOffset = DocumentUtil.getMapLineOffset(document,
								datasetsMap, -1, false);
						dslength = document.getLineLength(endLine-1);
					} else {
						dsOffset = DocumentUtil.getMapLineOffset(document,
								datasetsMap, -1, false);
					}
				}

			}

			CompositeMap afterMap = prototpyePage.getAfterMap();
			if (afterMap == null) {
				afterMap = insertAfters.get(0);
			}
			int viewOffset = 0;
			int viewLength = 0;

			boolean inSameLine = this.isInSameLine(viewMap);
			if (inSameLine) {
				int endLine = viewMap.getLocation().getEndLine();
				viewOffset = document.getLineOffset(endLine - 1);
				viewLength = document.getLineLength(endLine - 1);
			} else if ("begin".equals(afterMap.getName())) {
				CompositeMap aMap = this.bodyMap == null ? viewMap : bodyMap;
				viewOffset = DocumentUtil.getMapLineOffset(document, aMap, 0,
						true);

			} else if ("end".equals(afterMap.getName())) {
				CompositeMap aMap = this.bodyMap == null ? viewMap : bodyMap;
				viewOffset = DocumentUtil.getMapLineOffset(document, aMap, -1,
						false);
			} else {
				viewOffset = DocumentUtil.getMapLineOffset(document, afterMap,
						0, false);
			}

			if (viewOffset - dsOffset >= 0) {
				document.replace(viewOffset, viewLength, text);
				if (dsOffset != 0) {
					document.replace(dsOffset, dslength, insertDatasetText);
				}
				viewOffset += insertDatasetText.length() - dslength;
			} else {
				if (dsOffset != 0) {
					document.replace(dsOffset, dslength, insertDatasetText);
				}
				document.replace(viewOffset, viewLength, text);
			}

			StyledText st = (StyledText) textPage.getAdapter(StyledText.class);
			st.setSelectionRange(viewOffset, text.length());
		} catch (BadLocationException e) {
			componentListPage.setErrorMessage("操作无法完成请查看log获得更多信息");
			DialogUtil.logErrorException(e);
			return false;
		}
		return true;
	}

	private String createInsertText(CompositeMap generate) {
		if (screenMap == null) {
			return generate.toXML();
		}
		if (viewMap == null || isInSameLine(viewMap)) {
			return generate.getChild("view").toXML();
		}
		if (viewMap != null) {
			CompositeMap child = generate.getChild("view").getChild(
					"screenBody");
			if (child == null)
				return "";
			return getChildText(child);
		}
		return "";
	}

	private String createInsertDatasetText(CompositeMap generate) {

		if (viewMap == null) {
			return "";
		}

		if (datasetsMap == null || isInSameLine(datasetsMap)) {
			return generate.getChild("view").getChild("dataSets").toXML();
		}
		if (datasetsMap != null) {
			CompositeMap child = generate.getChild("view").getChild("dataSets");
			if (child == null)
				return "";
			return getChildText(child);
		}
		return "";
	}

	@SuppressWarnings("rawtypes")
	private String getChildText(CompositeMap child) {
		StringBuilder sb = new StringBuilder();
		List childsNotNull = child.getChildsNotNull();
		for (Iterator iterator = childsNotNull.iterator(); iterator.hasNext();) {
			CompositeMap m = (CompositeMap) iterator.next();
			String string = m.getString("model", "");
			if ("".equals(string)) {
				m.remove("model");
			}
			sb.append(m.toXML());
		}
		return sb.toString();
	}

	private boolean isInSameLine(CompositeMap map) {
		if (map != null) {
			return map.getLocation().getStartLine() == map.getLocation()
					.getEndLine();
		}
		return false;
	}

	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
	}

	@Override
	public void pageChanged(PageChangedEvent event) {
		Object selectedPage = event.getSelectedPage();
		if (prototpyePage.equals(selectedPage)) {
			CompositeMap currentSelectionObject = componentListPage
					.getCurrentSelectionObject();
			prototpyePage.setInput(viewDiagramCreator
					.createPrototypeDiagram(currentSelectionObject));
			prototpyePage.setPageComplete(true);
		} else {
			prototpyePage.setPageComplete(false);
		}
		this.getContainer().updateButtons();
	}

	private CompositeMap generate(ViewDiagram diagram) {
		ScreenGenerator sg = new ScreenGenerator(new FakePrototypeProject(
				project), null);
		try {
			CompositeMap genCompositeMap = sg.genCompositeMap(diagram);
			return genCompositeMap;
		} catch (TemplateNotBindedException e) {
		}
		return null;
	}

	public void createAfterList() throws CompositeMapLoadException {
		insertAfters.add(new CompositeMap("begin"));
		insertAfters.add(new CompositeMap("end"));
		DocumentCompositeMap dcm = new DocumentCompositeMap(document);
		CompositeMap load = dcm.load();
		load.iterate(new IterationHandle() {
			@Override
			public int process(CompositeMap map) {
				if ("screen".equalsIgnoreCase(map.getName())) {
					screenMap = map;
				}
				if ("view".equalsIgnoreCase(map.getName())) {
					viewMap = map;
				}
				if ("datasets".equalsIgnoreCase(map.getName())) {
					datasetsMap = map;
				}
				if ("screenBody".equalsIgnoreCase(map.getName())) {
					bodyMap = map;
				}
				return IterationHandle.IT_CONTINUE;
			}
		}, true);
		load.iterate(new IterationHandle() {
			@Override
			public int process(CompositeMap map) {
				if (viewMap != null && bodyMap == null
						&& viewMap.equals(map.getParent())) {
					insertAfters.add(map);
				}
				if (viewMap != null && bodyMap != null
						&& bodyMap.equals(map.getParent())) {
					insertAfters.add(map);
				}
				return IterationHandle.IT_CONTINUE;
			}
		}, false);
	}

}
