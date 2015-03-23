/*******************************************************************************
 * Copyright (c) 2011, 2012 Red Hat, Inc. 
 * All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 *
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.views.outline;

import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.ObjectEditingDialog;
import org.eclipse.bpmn2.modeler.help.IHelpContexts;
import org.eclipse.bpmn2.modeler.ui.Activator;
import org.eclipse.bpmn2.modeler.ui.IConstants;
import org.eclipse.bpmn2.modeler.ui.editor.BPMN2Editor;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Viewport;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.dnd.EditingDomainViewerDropAdapter;
import org.eclipse.emf.edit.ui.dnd.LocalTransfer;
import org.eclipse.emf.edit.ui.dnd.ViewerDragAdapter;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.internal.fixed.FixedScrollableThumbnail;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.about.AboutAction;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.PageBook;

/**
 * An outline page for the graphical modeling editor. It displays the contents
 * of the editor either as a hierarchical Outline or as a graphical Thumbnail.
 * There are buttons to switch between those displays. Subclasses should
 * overwrite this outline page (and dependent classes), to change the
 * default-behaviour.
 */
@SuppressWarnings("restriction")
public class BPMN2EditorOutlinePage extends ContentOutlinePage implements IPropertyListener, IAdaptable {

	// The IDs to identify the outline and the thunbnail
	public static final int ID_BUSINESS_MODEL_OUTLINE = 0;
	public static final int ID_INTERCHANGE_MODEL_OUTLINE = 1;
	public static final int ID_THUMBNAIL = 2;

	// Common instances of different Editors/Views, to synchronize their
	// behaviour
	private GraphicalViewer graphicalViewer;

	private ActionRegistry actionRegistry;

	private EditDomain editDomain;

	private KeyHandler keyHandler;


	private SelectionSynchronizer selectionSynchronizer;

	private DiagramEditor diagramEditor;
	
	// The thumbnail to display
	private FixedScrollableThumbnail thumbnail;

	// Actions (buttons) to switch between outline and overview
	private IAction showBusinessModelOutlineAction;
	private IAction showInterchangeModelOutlineAction;
	
	private IAction showOverviewAction;

	// The pagebook, which displays either the outline or the overview
	private PageBook pageBook;

	// The outline-controls and the thumbnail-control of the pagebook
	private Tree businessModelOutline;
	private Tree interchangeModelOutline;

	// and their corresponding editpart factories
	private EditPartFactory businessModelEditPartFactory;
	private EditPartFactory interchangeModelEditPartFactory;

	private Canvas overview;

	/**
	 * Creates a new BPMN2EditorOutlinePage. It is important, that this
	 * outline page uses the same handlers (ActionRegistry, KeyHandler,
	 * ZoomManagerAdapter, ...) as the main editor, so that the behaviour is
	 * synchronized between them.
	 * 
	 * @param diagramEditor
	 *            the attached diagram editor
	 * @since 0.9
	 */
	public BPMN2EditorOutlinePage(DiagramEditor diagramEditor) {
		super(new BPMN2EditorOutlineTreeViewer(diagramEditor));
		graphicalViewer = diagramEditor.getGraphicalViewer();
		actionRegistry = (ActionRegistry) diagramEditor.getAdapter(ActionRegistry.class);
		editDomain = diagramEditor.getEditDomain();
		keyHandler = (KeyHandler) diagramEditor.getAdapter(KeyHandler.class);
		selectionSynchronizer = (SelectionSynchronizer) diagramEditor.getAdapter(SelectionSynchronizer.class);
		this.diagramEditor = diagramEditor;
	}

	// ========================= standard behavior ===========================

	/**
	 * Is used to register several global action handlers (COMMAND_UNDO, COMMAND_REDO, COPY,
	 * PASTE, ...) on initialization of this outline page. This activates for
	 * example the undo-action in the central Eclipse-Menu.
	 * 
	 * @param pageSite
	 *            the page site
	 * 
	 * @see org.eclipse.ui.part.Page#init(IPageSite)
	 */
	@Override
	public void init(IPageSite pageSite) {
		super.init(pageSite);
		// TODO: implement editing actions in Outline
//		IActionBars actionBars = pageSite.getActionBars();
//		registerGlobalActionHandler(actionBars, ActionFactory.UNDO.getId());
//		registerGlobalActionHandler(actionBars, ActionFactory.REDO.getId());
//		registerGlobalActionHandler(actionBars, ActionFactory.COPY.getId());
//		registerGlobalActionHandler(actionBars, ActionFactory.PASTE.getId());
//		registerGlobalActionHandler(actionBars, ActionFactory.PRINT.getId());
//		registerGlobalActionHandler(actionBars, ActionFactory.SAVE_AS.getId());
//		actionBars.updateActionBars();
	}
	
	/**
	 * Creates the Control of this outline page. By default this is a PageBook,
	 * which can toggle between a hierarchical Outline and a graphical
	 * Thumbnail.
	 * 
	 * @param parent
	 *            the parent
	 * 
	 * @see org.eclipse.gef.ui.parts.ContentOutlinePage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		pageBook = new PageBook(parent, SWT.NONE);
		businessModelOutline = (Tree)getViewer().createControl(pageBook);
		interchangeModelOutline = (Tree)getViewer().createControl(pageBook);
		overview = new Canvas(pageBook, SWT.NONE);
		createOutlineViewer();

		// register listeners
		selectionSynchronizer.addViewer(getViewer());
		diagramEditor.addPropertyListener(this);
		
		addContextMenu(getViewer());
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IHelpContexts.Outline_View);
	}

	private void addContextMenu(final EditPartViewer viewer) {
		// add a double-click listener to show the Property Dialog for the selected item
		viewer.getControl().addMouseListener(new MouseListener() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				for (Object p : viewer.getSelectedEditParts()) {
					if (p instanceof AbstractGraphicsTreeEditPart) {
						Object model = ((AbstractGraphicsTreeEditPart)p).getModel();
						if (model instanceof EObject) {
							EObject businessObject = (EObject) model;
							ObjectEditingDialog dialog = new ObjectEditingDialog(diagramEditor, businessObject);
							dialog.open();
						}
					}
				}
			}

			@Override
			public void mouseDown(MouseEvent e) {
			}

			@Override
			public void mouseUp(MouseEvent e) {
			}
		});

		// Add a context menu for editing actions
		final MenuManager contextMenu = new MenuManager("#PopUp");
		contextMenu.add(new Separator("additions"));
		contextMenu.setRemoveAllWhenShown(true);
		contextMenu.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				System.out.println(viewer.getSelection());
				contextMenu.add(new AboutAction(diagramEditor.getSite().getWorkbenchWindow()));
			}
		});
		
		Menu menu = contextMenu.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(Activator.PLUGIN_ID+".outline", contextMenu, viewer);
	}
	
	private void removeKeyListeners(Tree tree) {
		for (Listener l : tree.getListeners(SWT.KeyUp)) {
			tree.removeListener(SWT.KeyUp, l);
		}
		for (Listener l : tree.getListeners(SWT.KeyDown)) {
			tree.removeListener(SWT.KeyDown, l);
		}
	}
	
	/**
	 * Deregisters all 'listeners' of the main-editor.
	 */
	@Override
	public void dispose() {
		// deregister listeners
		selectionSynchronizer.removeViewer(getViewer());
		diagramEditor.removePropertyListener(this);

		if (thumbnail != null)
			thumbnail.deactivate();

		super.dispose();
	}

	/**
	 * Returns the Control of this outline page, which was created in
	 * createControl().
	 * 
	 * @return the control
	 * 
	 * @see org.eclipse.gef.ui.parts.ContentOutlinePage#getControl()
	 */
	@Override
	public Control getControl() {
		return pageBook;
	}

	/**
	 * Refreshes the outline on any change of the diagram editor. Most
	 * importantly, there is a property change event editor-dirty.
	 */
	public void propertyChanged(Object source, int propId) {
		refresh();
	}
	
	public Object getAdapter(Class key) {
		if (key==BPMN2Editor.class)
			return diagramEditor;
		else if (diagramEditor!=null)
			return diagramEditor.getAdapter(key);
		return null;
	}
	
	/**
	 * Toggles the page to display between hierarchical Outline and graphical
	 * Thumbnail.
	 * 
	 * @param id
	 *            The ID of the page to display. It must be either ID_BUSINESS_MODEL_OUTLINE or
	 *            ID_THUMBNAIL.
	 */
	protected void showPage(int id) {
		if (id == ID_BUSINESS_MODEL_OUTLINE) {
			if (businessModelEditPartFactory==null)
				businessModelEditPartFactory = new BPMNDiagramTreeEditPartFactory(ID_BUSINESS_MODEL_OUTLINE);
			getViewer().setEditPartFactory(businessModelEditPartFactory);
			getViewer().setControl(businessModelOutline);
			Diagram diagram = diagramEditor.getDiagramTypeProvider().getDiagram();
			getViewer().setContents(diagram);
			
			showBusinessModelOutlineAction.setChecked(true);
			showInterchangeModelOutlineAction.setChecked(false);
			showOverviewAction.setChecked(false);
			pageBook.showPage(businessModelOutline);
			// TODO: remove this later when edit support is added to Outline view
			removeKeyListeners(businessModelOutline);

		} else if (id == ID_INTERCHANGE_MODEL_OUTLINE) {
			if (interchangeModelEditPartFactory==null)
				interchangeModelEditPartFactory = new BPMNDiagramTreeEditPartFactory(ID_INTERCHANGE_MODEL_OUTLINE);
			getViewer().setEditPartFactory(interchangeModelEditPartFactory);
			getViewer().setControl(interchangeModelOutline);
			Diagram diagram = diagramEditor.getDiagramTypeProvider().getDiagram();
			getViewer().setContents(diagram);
			
			showBusinessModelOutlineAction.setChecked(false);
			showInterchangeModelOutlineAction.setChecked(true);
			showOverviewAction.setChecked(false);
			pageBook.showPage(interchangeModelOutline);
			// TODO: remove this later when edit support is added to Outline view
			removeKeyListeners(interchangeModelOutline);

		} else if (id == ID_THUMBNAIL) {
			if (thumbnail == null)
				createThumbnailViewer();
			showBusinessModelOutlineAction.setChecked(false);
			showInterchangeModelOutlineAction.setChecked(false);
			showOverviewAction.setChecked(true);
			pageBook.showPage(overview);
		}
	}

	/**
	 * Creates the hierarchical Outline viewer.
	 */
	protected void createOutlineViewer() {
		// set the standard handlers
		getViewer().setEditDomain(editDomain);
		getViewer().setKeyHandler(keyHandler);

		// add a context-menu
		ContextMenuProvider contextMenuProvider = createContextMenuProvider();
		if (contextMenuProvider != null)
			getViewer().setContextMenu(contextMenuProvider);

		// add buttons outline/overview to toolbar
		IToolBarManager tbm = getSite().getActionBars().getToolBarManager();
		showBusinessModelOutlineAction = new Action() {

			@Override
			public void run() {
				showPage(ID_BUSINESS_MODEL_OUTLINE);
			}
		};
		showBusinessModelOutlineAction.setImageDescriptor(Activator.getDefault().getImageDescriptor(IConstants.ICON_BUSINESS_MODEL));
		showBusinessModelOutlineAction.setToolTipText(Messages.BPMN2EditorOutlinePage_Business_Model_Title);
		tbm.add(showBusinessModelOutlineAction);

		showInterchangeModelOutlineAction = new Action() {

			@Override
			public void run() {
				Diagram diagram = diagramEditor.getDiagramTypeProvider().getDiagram();
				getViewer().setContents(diagram);
				getViewer().setControl(interchangeModelOutline);
				showPage(ID_INTERCHANGE_MODEL_OUTLINE);
			}
		};
		showInterchangeModelOutlineAction.setImageDescriptor(Activator.getDefault().getImageDescriptor(IConstants.ICON_INTERCHANGE_MODEL));
		showInterchangeModelOutlineAction.setToolTipText(Messages.BPMN2EditorOutlinePage_DI_Model_Title);
		tbm.add(showInterchangeModelOutlineAction);
		
		
		showOverviewAction = new Action() {

			@Override
			public void run() {
				showPage(ID_THUMBNAIL);
			}
		};
		showOverviewAction.setImageDescriptor(Activator.getDefault().getImageDescriptor(IConstants.ICON_THUMBNAIL));
		showOverviewAction.setToolTipText(Messages.BPMN2EditorOutlinePage_Thumbnail_Title);
		
		tbm.add(showOverviewAction);

		// by default show the outline-page
		showPage(ID_BUSINESS_MODEL_OUTLINE);
	}

	/**
	 * Returns a new ContextMenuProvider. Can be null, if no context-menu shall
	 * be displayed.
	 * 
	 * @return A new ContextMenuProvider.
	 */
	protected ContextMenuProvider createContextMenuProvider() {
		return null;
	}

	/**
	 * Creates the graphical Thumbnail viewer.
	 */
	protected void createThumbnailViewer() {
		LightweightSystem lws = new LightweightSystem(overview);
		ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) graphicalViewer.getRootEditPart();
		thumbnail = new FixedScrollableThumbnail((Viewport) rootEditPart.getFigure());
		thumbnail.setBorder(new MarginBorder(3));
		thumbnail.setSource(rootEditPart.getLayer(LayerConstants.PRINTABLE_LAYERS));
		lws.setContents(thumbnail);
	}

	// ========================= private helper methods =======================

	private void registerGlobalActionHandler(IActionBars actionBars, String id) {
		IAction action = actionRegistry.getAction(id);
		if (action != null)
			actionBars.setGlobalActionHandler(id, action);
	}

	/**
	 * Refresh.
	 */
	void refresh() {
		try {
			final EditPartViewer viewer = getViewer();
			final EditPart contents = viewer.getContents();
			if (contents != null) {
				contents.refresh();
			}
		}
		catch (Exception e) {
			// ignore SWT exceptions caused by closing the editor
		}
	}
}